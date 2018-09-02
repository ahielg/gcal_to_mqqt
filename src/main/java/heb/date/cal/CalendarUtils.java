package heb.date.cal;


import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ahielg
 * @date 02/09/2018
 */
public class CalendarUtils {
    public static String getToday() {
        return parseHebDate(currJewishDate());
    }

    public static String parseHebDate(CalendarDate c) {
        return gimatria(c.getDay()) + ' ' + getJewishMonthName(c.getMonth(), c.getYear()) + ' ' + gimatria(c.getYear());
    }

    public static CalendarDate currJewishDate() {
        CalendarDate hebDate = gregorian2Jewish(new CalendarDate(Calendar.getInstance()));
//        System.out.println(hebDate.getYear() + "::" + hebDate.getDay());
//        System.out.println(getJewishMonthName(hebDate.getMonth(), hebDate.getYear()));
        Set<String> holidayForDate = getHolidayForDate(new CalendarDate(Calendar.getInstance()), false);
        /*     for (String s:holidayForDate) {
            System.out.println(s);
        }*/
        return hebDate;
    }

    public static CalendarDate gregorian2Jewish(Calendar date) {
        return CalendarUtils.gregorian2Jewish(new CalendarDate(date));
    }

    public static CalendarDate gregorian2Jewish(CalendarDate date2Convert) {
        CalendarImpl calendar = CalendarImpl.getInstance();
        int absolute = calendar.absoluteFromGregorianDate(date2Convert);
        return calendar.jewishDateFromAbsolute(absolute);
    }

    public static CalendarDate jewish2Gregorian(CalendarDate date2Convert) {
        CalendarImpl ca = CalendarImpl.getInstance();
        int absolute = ca.absoluteFromJewishDate(date2Convert);
        return ca.gregorianDateFromAbsolute(absolute);
    }

    public static boolean hebrewLeapYear(int year) {
        return (((year * 7) + 1) % 19) < 7;
    }

    public static String getJewishMonthName(int monthNumber, int year) {
        String[] jewishMonthNamesLeap = {"ניסן", "אייר", "סיוון", "תמוז",
                "אב", "אלול", "תשרי", "חשוון",
                "כסליו", "טבת", "שבט", "אדר א'", "אדר ב'"};
        String[] jewishMonthNamesNonLeap = {"ניסן", "אייר", "סיוון", "תמוז",
                "אב", "אלול", "תשרי", "חשוון",
                "כסליו", "טבת", "שבט", "אדר"};

        if (hebrewLeapYear(year)) {
            return jewishMonthNamesLeap[monthNumber - 1];
        } else {
            return jewishMonthNamesNonLeap[monthNumber - 1];
        }
    }


    private static int getWeekdayOfHebrewDate(int hebDay, int hebMonth, int hebYear) {
        int absDate = CalendarImpl.getInstance().absoluteFromJewishDate(new CalendarDate(hebDay, hebMonth, hebYear));
        return absDate % 7;
    }

    public static Set<String> getHolidayForDate(CalendarDate gdate, boolean diaspora) {
        CalendarImpl calendar = CalendarImpl.getInstance();
        int absDate = calendar.absoluteFromGregorianDate(gdate);
        CalendarDate jewishDate = calendar.jewishDateFromAbsolute(absDate);
        int hebDay = jewishDate.getDay();
        int hebMonth = jewishDate.getMonth();
        int hebYear = jewishDate.getYear();

        Set<String> listHolidays = new HashSet<String>(5);

        // Holidays in Nisan

        int hagadolDay = 14;
        while (getWeekdayOfHebrewDate(hagadolDay, 1, hebYear) != 6)
            hagadolDay -= 1;
        if (hebDay == hagadolDay && hebMonth == 1)
            listHolidays.add("שבת הגדול");

        if (hebDay == 14 && hebMonth == 1)
            listHolidays.add("ערב פסח");
        if (hebDay == 15 && hebMonth == 1)
            listHolidays.add("פסח");
        if (hebDay == 16 && hebMonth == 1) {
            if (diaspora) {
                listHolidays.add("ב' פסח");
            } else {
                listHolidays.add("חול המועד");
            }
        }
        if (hebDay == 17 && hebMonth == 1)
            listHolidays.add("חול המועד");
        if (hebDay == 18 && hebMonth == 1)
            listHolidays.add("חול המועד");
        if (hebDay == 19 && hebMonth == 1)
            listHolidays.add("חול המועד");
        if (hebDay == 20 && hebMonth == 1)
            listHolidays.add("חול המועד");
        if (hebDay == 21 && hebMonth == 1) {
            if (!diaspora)
                listHolidays.add("שביעי של פסח");
            else
                listHolidays.add("שביעי של פסח");
        }
        if (hebDay == 22 && hebMonth == 1) {
            if (diaspora)
                listHolidays.add("שמיני של פסח");
        }

        // Yom Hashoah

        if (getWeekdayOfHebrewDate(27, 1, hebYear) == 5) {
            if (hebDay == 26 && hebMonth == 1)
                listHolidays.add("יום השואה");
        } else if (hebYear >= 5757 && getWeekdayOfHebrewDate(27, 1, hebYear) == 0) {
            if (hebDay == 28 && hebMonth == 1)
                listHolidays.add("יום השואה");
        } else {
            if (hebDay == 27 && hebMonth == 1)
                listHolidays.add("יום השואה");
        }

        // Holidays in Iyar

        // Yom Hazikaron

        if (getWeekdayOfHebrewDate(4, 2, hebYear) == 5) { // If 4th of Iyar is a Thursday ...
            if (hebDay == 2 && hebMonth == 2) // ... then Yom Hazicaron is on 2th of Iyar
                listHolidays.add("יום הזיכרון");
        } else if (getWeekdayOfHebrewDate(4, 2, hebYear) == 4) {
            if (hebDay == 3 && hebMonth == 2)
                listHolidays.add("יום הזיכרון");
        } else if (hebYear >= 5764 && getWeekdayOfHebrewDate(4, 2, hebYear) == 0) {
            if (hebDay == 5 && hebMonth == 2)
                listHolidays.add("יום הזיכרון");
        } else {
            if (hebDay == 4 && hebMonth == 2)
                listHolidays.add("יום הזיכרון");
        }

        // Yom Ha'Azmaut

        if (getWeekdayOfHebrewDate(5, 2, hebYear) == 6) {
            if (hebDay == 3 && hebMonth == 2)
                listHolidays.add("יום העצמאות");
        } else if (getWeekdayOfHebrewDate(5, 2, hebYear) == 5) {
            if (hebDay == 4 && hebMonth == 2)
                listHolidays.add("יום העצמאות");
        } else if (hebYear >= 5764 && getWeekdayOfHebrewDate(4, 2, hebYear) == 0) {
            if (hebDay == 6 && hebMonth == 2)
                listHolidays.add("יום העצמאות");
        } else {
            if (hebDay == 5 && hebMonth == 2)
                listHolidays.add("יום העצמאות");
        }
        if (hebDay == 14 && hebMonth == 2)
            listHolidays.add("פסח שני");
        if (hebDay == 18 && hebMonth == 2)
            listHolidays.add("לג בעומר");
        if (hebDay == 28 && hebMonth == 2)
            listHolidays.add("יום ירושלים");

        // Holidays in Sivan

        if (hebDay == 5 && hebMonth == 3)
            listHolidays.add("Erev Shavuot");
        if (hebDay == 6 && hebMonth == 3) {
            if (diaspora)
                listHolidays.add("שבועות א'");
            else
                listHolidays.add("שבועות");
        }
        if (hebDay == 7 && hebMonth == 3) {
            if (diaspora)
                listHolidays.add("ב' שבועות");
        }

        // Holidays in Tammuz

        if (getWeekdayOfHebrewDate(17, 4, hebYear) == 6) {
            if (hebDay == 18 && hebMonth == 4)
                listHolidays.add("י''ז בתמוז");
        } else {
            if (hebDay == 17 && hebMonth == 4)
                listHolidays.add("י''ז בתמוז");
        }

        // Holidays in Av

        if (getWeekdayOfHebrewDate(9, 5, hebYear) == 6) {
            if (hebDay == 10 && hebMonth == 5)
                listHolidays.add("תשעה באב (נדחה)");
        } else {
            if (hebDay == 9 && hebMonth == 5)
                listHolidays.add("תשעה באב");
        }
        if (hebDay == 15 && hebMonth == 5)
            listHolidays.add("ט''ו באב");

        // Holidays in Elul

        if (hebDay == 29 && hebMonth == 6)
            listHolidays.add("ערב ראש השנה");

        // Holidays in Tishri

        if (hebDay == 1 && hebMonth == 7)
            listHolidays.add("א' ראש השנה");
        if (hebDay == 2 && hebMonth == 7)
            listHolidays.add("ב' ראש השנה");
        if (getWeekdayOfHebrewDate(3, 7, hebYear) == 6) {
            if (hebDay == 4 && hebMonth == 7)
                listHolidays.add("צום גדלה (נדחה)");
        } else {
            if (hebDay == 3 && hebMonth == 7)
                listHolidays.add("צום גדליה");
        }
        if (hebDay == 9 && hebMonth == 7)
            listHolidays.add("ערב יום כיפור");
        if (hebDay == 10 && hebMonth == 7)
            listHolidays.add("יום כיפור");
        if (hebDay == 14 && hebMonth == 7)
            listHolidays.add("ערב סוכות");
        if (hebDay == 15 && hebMonth == 7) {
            if (diaspora)
                listHolidays.add("א' סוכות");
            else
                listHolidays.add("סוכות");
        }
        if (hebDay == 16 && hebMonth == 7) {
            if (diaspora)
                listHolidays.add("ב' סוכות");
            else
                listHolidays.add("חול המועד");
        }
        if (hebDay == 17 && hebMonth == 7)
            listHolidays.add("חול המועד");
        if (hebDay == 18 && hebMonth == 7)
            listHolidays.add("חול המועד");
        if (hebDay == 19 && hebMonth == 7)
            listHolidays.add("חול המועד");
        if (hebDay == 20 && hebMonth == 7)
            listHolidays.add("חול המועד");
        if (hebDay == 21 && hebMonth == 7)
            listHolidays.add("הושענה רבה");
        if (hebDay == 22 && hebMonth == 7) {
            if (!diaspora) {
                listHolidays.add("שמיני עצרת");
                listHolidays.add("שמחת תורה");
            } else {
                listHolidays.add("שמיני עצרת");
            }
        }
        if (hebDay == 23 && hebMonth == 7) {
            if (diaspora)
                listHolidays.add("שמחת תורה");
        }

        // Holidays in Kislev

        if (hebDay == 25 && hebMonth == 9)
            listHolidays.add("א' חנוכה");
        if (hebDay == 26 && hebMonth == 9)
            listHolidays.add("ב' חנוכה");
        if (hebDay == 27 && hebMonth == 9)
            listHolidays.add("ג' חנוכה");
        if (hebDay == 28 && hebMonth == 9)
            listHolidays.add("ד' חנוכה");
        if (hebDay == 29 && hebMonth == 9)
            listHolidays.add("ה' חנוכה");

        // Holidays in Tevet

        if (hebDay == 10 && hebMonth == 10)
            listHolidays.add("עשרה בטבת");

        if (calendar.getLastDayOfJewishMonth(9, hebYear) == 30) {
            if (hebDay == 30 && hebMonth == 9)
                listHolidays.add("ו' חנוכה");
            if (hebDay == 1 && hebMonth == 10)
                listHolidays.add("ז' חנוכה");
            if (hebDay == 2 && hebMonth == 10)
                listHolidays.add("וזאת חנוכה");
        }
        if (calendar.getLastDayOfJewishMonth(9, hebYear) == 29) {
            if (hebDay == 1 && hebMonth == 10)
                listHolidays.add("ו' חנוכה");
            if (hebDay == 2 && hebMonth == 10)
                listHolidays.add("ז' חנוכה");
            if (hebDay == 3 && hebMonth == 10)
                listHolidays.add("וזאת חנוכה");
        }

        // Holidays in Shevat

        if (hebDay == 15 && hebMonth == 11)
            listHolidays.add("ט''ו בשבט");

        // Holidays in Adar (I)/Adar II

        int monthEsther;
        if (calendar.hebrewLeapYear(hebYear))
            monthEsther = 13;
        else
            monthEsther = 12;

        if (getWeekdayOfHebrewDate(13, monthEsther, hebYear) == 6) {
            if (hebDay == 11 && hebMonth == monthEsther)
                listHolidays.add("תענית אסתר (נדחה)");
        } else {
            if (hebDay == 13 && hebMonth == monthEsther)
                listHolidays.add("תענית אסתר");
        }

        if (hebDay == 14 && hebMonth == monthEsther)
            listHolidays.add("פורים");
        if (hebDay == 15 && hebMonth == monthEsther)
            listHolidays.add("שושן פורים");

        if (calendar.hebrewLeapYear(hebYear)) {
            if (hebDay == 14 && hebMonth == 12)
                listHolidays.add("פורים קטן");
            if (hebDay == 15 && hebMonth == 12)
                listHolidays.add("שושן פורים קטן");
        }
        return listHolidays;
    }

    public static String gimatria(int n) {
        StringBuilder gimatria = new StringBuilder(50);
        if (n % 1000 == 0) {
            gimatria.append(gimatria(n / 1000)).append("אלפים");
            return gimatria.toString();
        } else if (n > 1000) {
            gimatria.append(gimatria(n / 1000)).append(gimatria(n % 1000));
            return gimatria.toString();
        }

        StringBuilder p = new StringBuilder(50);
        while (n >= 400) {
            p.append('ת');
            n -= 400;
        }

        if (n >= 100) {
            p.append("קרשת".charAt(n / 100 - 1));
            n %= 100;
        }

        if (n >= 10) {
            if ((n == 15) || (n == 16)) {
                n -= 9;
            }

            if (n / 10 > 0) {
                p.append("יכלמנסעפצ".charAt(n / 10 - 1));
            } else {
                p.append('ט');
            }
            n %= 10;
        }

        if (n > 0) {
            p.append("אבגדהוזחט".charAt(n - 1));
        }

        if (p.length() <= 1) {
            p.append('\'');
        } else {
            p.insert(p.length() - 1, '"');
        }

        return p.toString();
    }
}
