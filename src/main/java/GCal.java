import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import heb.date.CandleLighting;
import heb.date.HebData;
import heb.date.HebrewDate;
import heb.date.RegularHebrewDate;
import heb.date.cal.CalendarUtils;
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
    private static final String HEB_DATE_TOPIC = "openhab/hebDate/";
    private static final String TODAY = "Today";
    private static final String TOMORROW = "Tomorrow";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final String ITEMS = "items";
    private static final String SUMMARY = "summary";

    private static final String PARASHA_ENG = "parashaEng";
    private static final String PARASHA = "parasha";
    private static final String KNISA = "knisa";
    private static final String HAVDALA = "havdala";
    private static final String DATE = "date";
    private static final String HOLIDAY = "holiday";
    private static final String DATE_ENG = "dateEng";
    private static final String OMER = "omer";

    public static void main(String[] args) throws Exception {
        //int daysIncrease = 0;
        //String holiday = getCalEvent(HOLIDAY_CALENDAR, tomorrowStart, tomorrowEnd);
        //holiday = validateHoliday(holiday);
        publishData(schoolCalc(0), schoolCalc(1), isHoliday(0), isHoliday(1), calcHebData());

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

    private static void publishData(String schoolToday, String schoolTomorrow, String holidayToday, String holidayTomorrow, HebData hebData) throws IOException {
        //final SimpleMQTTClient sc = new SimpleMQTTClient("localhost");
        try (MQTTClient sc = new PahoMQTTClient()){
            sc.publish(SCHOOL_TOPIC, schoolTomorrow);
            sc.publish(HOLIDAY_TOPIC, holidayTomorrow);

            sc.publish(SCHOOL_TOPIC + TODAY, parseHoliday(schoolToday));
            sc.publish(SCHOOL_TOPIC + TOMORROW, parseHoliday(schoolTomorrow));
            sc.publish(HOLIDAY_TOPIC + TODAY, parseHoliday(holidayToday));
            sc.publish(HOLIDAY_TOPIC + TOMORROW, parseHoliday(holidayTomorrow));

            sc.publish(HEB_DATE_TOPIC + PARASHA_ENG, hebData.getParasha());
            sc.publish(HEB_DATE_TOPIC + PARASHA, hebData.getParashaHeb());
            sc.publish(HEB_DATE_TOPIC + KNISA, hebData.getKnisatShabat());
            sc.publish(HEB_DATE_TOPIC + HAVDALA, hebData.getHavdala());
            sc.publish(HEB_DATE_TOPIC + DATE, hebData.getHebDate());
            sc.publish(HEB_DATE_TOPIC + DATE_ENG, hebData.getHebDateEng());
            sc.publish(HEB_DATE_TOPIC + HOLIDAY, hebData.getHoliday());
            sc.publish(HEB_DATE_TOPIC + OMER, hebData.getOmer());
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
        RegularHebrewDate regularHebrewDate = getHebDate(daysIncrease);
        System.out.println(regularHebrewDate.isShabatonHoliday() + ":::" + regularHebrewDate.getHoliday());
        return regularHebrewDate.isShabatonHoliday() ? regularHebrewDate.getHoliday() : "NA";
    }

    private static RegularHebrewDate getHebDate(int daysIncrease) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, daysIncrease);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return new RegularHebrewDate(cal);
    }

    private static HebData calcHebData() {
        Calendar cal = Calendar.getInstance();
        RegularHebrewDate currDate = new RegularHebrewDate(cal);

        RegularHebrewDate date = RegularHebrewDate.getNextShabatDate(cal);
        int parshaNum = date.getParshaNum();

        RegularHebrewDate.getParashaEng(parshaNum);

        Calendar c = cal;
        CandleLighting candleLighting = new CandleLighting(new HebrewDate(c));
        while ("".equals(candleLighting.getCandleLighting())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            candleLighting = new CandleLighting(new HebrewDate(c));
        }
        String knisa = candleLighting.getCandleLighting();

        while ("".equals(candleLighting.getHavdala())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            candleLighting = new CandleLighting(new HebrewDate(c));
        }
        String havdala = candleLighting.getHavdala();

        HebData hebData = new HebData(RegularHebrewDate.getParasha(parshaNum),
                RegularHebrewDate.getParashaEng(parshaNum),
                knisa, havdala, date.getHoliday(), date.getOmerAsString(),
                CalendarUtils.getToday(), currDate.getHebrewDateAsString());
        System.out.println("hebData = " + hebData);
        return hebData;
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
