import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import heb.date.RegularHebrewDate;
import mqtt.MQTTClient;
import mqtt.PahoMQTTClient;
import util.KeysCons;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Ahielg
 * @date 29/05/2016
 */

public class GCal {

    private static final Gson gson = new GsonBuilder().create();
    @SuppressWarnings("SpellCheckingInspection")
    private static final List<String> holidays = Arrays.asList("Shavuot", "Pesach I ", "Pesach VII ", "Shmini Atzeret", "Rosh Hashana", "Kippur");
    private static final String sukot = "Sukkot I";

    //private static final String HOLIDAY_CALENDAR = "en.jewish%23holiday%40group.v.calendar.google.com";
    private static final String SCHOOL_TOPIC = "openhab/calSchool";
    private static final String HOLIDAY_TOPIC = "openhab/calHoliday";
    private static final String TODAY = "Today";
    private static final String TOMORROW = "Tomorrow";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String ITEMS = "items";
    private static final String SUMMARY = "summary";

    public static void main(String[] args) throws Exception {
        //int daysIncrease = 0;
        //String holiday = getCalEvent(HOLIDAY_CALENDAR, tomorrowStart, tomorrowEnd);
        //holiday = validateHoliday(holiday);
        publishData(schoolCalc(0), schoolCalc(1), isHoliday(0), isHoliday(1));

    }

    private static String schoolCalc(int daysIncrease) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String todayStart = simpleDateFormat.format(theDate(daysIncrease)) + "-00:00";
        String todayEnd = simpleDateFormat.format(theEndDate(daysIncrease)) + "-00:00";
        System.out.println(todayStart);
        System.out.println(todayEnd);
        return getCalEvent(KeysCons.SCHOOL_CALENDAR, todayStart, todayEnd);
    }

    private static String validateHoliday(String holiday) {
        String result = holiday;
        if (!sukot.equals(holiday)
                && holidays.stream().noneMatch(holiday::contains)) {
            result = "NA";
        }
        return result;
    }

    private static String getCalEvent(String calendar, String start, String end) throws IOException {
        HashMap json1 = readJsonFromUrl("https://www.googleapis.com/calendar/v3/calendars/" + calendar + "/events?key=" + KeysCons.KEY + "&timeMin=" + start + "&timeMax=" + end);
        String result = getEvent(json1);
        System.out.println(result + " ::: " + json1.toString());
        return result;
    }

    private static String getEvent(HashMap map) {
        String result = "NA";
        List items = ((List) map.get(ITEMS));
        if (!items.isEmpty()) {
            Map itemsMap = ((Map) items.get(0));
            result = itemsMap.get(SUMMARY).toString();
        }
        return result;
    }

    private static Date convertToDate(String s) {
        try {
            return DATE_FORMAT.parse(s);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private static void publishData(String schoolToday, String schoolTomorrow, String holidayToday, String holidayTomorrow) throws IOException {
        //final SimpleMQTTClient sc = new SimpleMQTTClient("localhost");
        try (MQTTClient sc = new PahoMQTTClient()){
            sc.publish(SCHOOL_TOPIC, schoolTomorrow);
            sc.publish(HOLIDAY_TOPIC, holidayTomorrow);

            sc.publish(SCHOOL_TOPIC + TODAY, parseHoliday(schoolToday));
            sc.publish(SCHOOL_TOPIC + TOMORROW, parseHoliday(schoolTomorrow));
            sc.publish(HOLIDAY_TOPIC + TODAY, parseHoliday(holidayToday));
            sc.publish(HOLIDAY_TOPIC + TOMORROW, parseHoliday(holidayTomorrow));
        }
    }



    private static String parseHoliday(String holiday) {
        return "NA".equals(holiday) ? "OFF" : "ON";
    }

    private static Date theDate(int daysIncrease) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysIncrease);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    private static String isHoliday(int daysIncrease) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysIncrease);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        RegularHebrewDate regularHebrewDate = new RegularHebrewDate(cal);
        System.out.println(regularHebrewDate.isShabatonHoliday() + ":::" + regularHebrewDate.getHoliday());
        return regularHebrewDate.isShabatonHoliday() ? regularHebrewDate.getHoliday() : "NA";
    }

    private static Date theEndDate(int daysIncrease) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysIncrease);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return new Date(cal.getTimeInMillis());
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static HashMap readJsonFromUrl(String url) throws IOException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return gson.fromJson(jsonText, HashMap.class);
        }
    }

    public static java.util.Date parseRFC3339Date(String dateString) throws java.text.ParseException, IndexOutOfBoundsException {
        Date d;

        //if there is no time zone, we don't need to do any special parsing.
        if (dateString.endsWith("Z")) {
            try {
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//spec for RFC3339
                d = s.parse(dateString);
            } catch (java.text.ParseException pe) {//try again with optional decimals
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");//spec for RFC3339 (with fractional seconds)
                s.setLenient(true);
                d = s.parse(dateString);
            }
            return d;
        }

        //step one, split off the timezone.
        String firstPart = dateString.substring(0, dateString.lastIndexOf('-'));
        String secondPart = dateString.substring(dateString.lastIndexOf('-'));

        //step two, remove the colon from the timezone offset
        secondPart = secondPart.substring(0, secondPart.indexOf(':')) + secondPart.substring(secondPart.indexOf(':') + 1);
        dateString = firstPart + secondPart;
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//spec for RFC3339
        try {
            d = s.parse(dateString);
        } catch (java.text.ParseException pe) {//try again with optional decimals
            s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");//spec for RFC3339 (with fractional seconds)
            s.setLenient(true);
            d = s.parse(dateString);
        }
        return d;
    }
}
