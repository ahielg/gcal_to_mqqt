package heb.date;

import java.util.Calendar;
import java.util.Date;

/**
 * The RegularHebrewDate class is a subclass of the HebrewDate class that adds the following
 * functionality to it.<P>
 * <UL>
 * <LI>Getting the corresponding jewish holiday or fast day. (no "modern" holidays)
 * <LI>Getting the regular parsha (Torah or sedra) reading for Shabbos.
 * <LI>Getting the current Omer count.
 * </UL>
 * In addition, This class has settings for Ashkenazi or Sephardi pronunciations and
 * whether the parsha and holiday scheme follows the Israel scheme or outside Israel scheme.
 * <p/>
 * The methods used to obtain the parsha were derived from
 * the source code of hebcal by Danny Sadinoff and JCal for the Mac
 * by Frank Yellin. Both based their code on routines by Nachum
 * Dershowitz and Edward M. Reingold.
 * <p/>
 * hebcal web page is at <A HREF="http://www.sadinoff.com/hebcal/">www.sadinoff.com/hebcal</A>
 */
@SuppressWarnings("UnusedDeclaration")
public class RegularHebrewDate extends HebrewDate {
    private boolean israeli = true;
    private static String[] DAYS = {"ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת"};
    // parsha names in both ashkenazi and sephardi pronunciation
    // Somewhat redundant (don't you think?)
    private static final String[] parshios =
            {"בראשית", "נח", "לך לך", "וירא", "חיי שרה", "תולדות", "ויצא", "וישלח", "וישב", "מקץ", "ויגש", "ויחי",
                    "שמות", "וארא", "בא", "בשלח", "יתרו", "משפטים", "תרומה", "תצוה", "כי תשא", "ויקהל", "פקודי",
                    "ויקרא", "צו", "שמיני", "תזריע", "מצורע", "אחרי מות", "קדושים", "אמור", "בהר", "בחוקותי"
                    , "במדבר", "נשא", "בהעלותך", "שלח", "קרח", "חוקת", "בלק", "פנחס", "מטות", "מסעי",
                    "דברים", "ואתחנן", "עקב", "ראה", "שופטים", "כי תצא", "כי תבוא", "נצבים", "וילך", "האזינו",
                    "ויקהל-פקודי", "תזריע-מצורע", "אחרי מות-קדושים", "בהר-בחוקותי", "חוקת-בלק", "מטות-מסעי", "נצבים-וילך"};

    public static final String[] importantHolidays = {"פסח",
            "שבועות",
            "יז בתמוז",
            "תשעה באב",
            "טו באב",
            "ראש השנה",
            "יום כיפור",
            "סוכות",
            "שמחת תורה",
            "חנוכה",
            "עשרה בטבת",
            "טו בשבט",
            "פורים"
    };


    // These indices were originaly included in the emacs 19 distribution.
    // These arrays determine the correct indices into the parsha names
    // -1 means no parsha that week, values > 52 means it is a double parsha


    public static String[] getParshios() {
        return parshios;
    }

    private static final int[] Sat_short =
            {-1, 52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, 25,
                    54, 55, 30, 56, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44,
                    45, 46, 47, 48, 49, 50};

    private static final int[] Sat_long =
            {-1, 52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, 25,
                    54, 55, 30, 56, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44,
                    45, 46, 47, 48, 49, 59};

    private static final int[] Mon_short =
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, 25, 54,
                    55, 30, 56, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44, 45,
                    46, 47, 48, 49, 59};

    private static final int[] Mon_long = // split //
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, 25, 54,
                    55, 30, 56, 33, -1, 34, 35, 36, 37, 57, 40, 58, 43, 44, 45,
                    46, 47, 48, 49, 59};


    private static final int[] Thu_normal =
            {52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, -1, 25,
                    54, 55, 30, 56, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44,
                    45, 46, 47, 48, 49, 50};
    private static final int[] Thu_normal_Israel =
            {52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 53, 23, 24, -1, 25, 54,
                    55, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44,
                    45, 46, 47, 48, 49, 50};

    private static final int[] Thu_long =
            {52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, -1, 25,
                    54, 55, 30, 56, 33, 34, 35, 36, 37, 38, 39, 40, 58, 43, 44,
                    45, 46, 47, 48, 49, 50};

    private static final int[] Sat_short_leap =
            {-1, 52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                    26, 27, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                    40, 58, 43, 44, 45, 46, 47, 48, 49, 59};

    private static final int[] Sat_long_leap =
            {-1, 52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
                    26, 27, -1, 28, 29, 30, 31, 32, 33, -1, 34, 35, 36, 37, 57,
                    40, 58, 43, 44, 45, 46, 47, 48, 49, 59};

    private static final int[] Mon_short_leap =
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, -1, 28, 29, 30, 31, 32, 33, -1, 34, 35, 36, 37, 57, 40,
                    58, 43, 44, 45, 46, 47, 48, 49, 59};
    private static final int[] Mon_short_leap_Israel =
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                    58, 43, 44, 45, 46, 47, 48, 49, 59};

    private static final int[] Mon_long_leap =
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, -1, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                    40, 58, 43, 44, 45, 46, 47, 48, 49, 50};
    private static final int[] Mon_long_leap_Israel =
            {51, 52, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, -1, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                    41, 42, 43, 44, 45, 46, 47, 48, 49, 50};

    private static final int[] Thu_short_leap =
            {52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, 28, -1, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                    41, 42, 43, 44, 45, 46, 47, 48, 49, 50};

    private static final int[] Thu_long_leap =
            {52, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                    27, 28, -1, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                    41, 42, 43, 44, 45, 46, 47, 48, 49, 59};

    /**
     * Initializes based on a month, date, year and whether it is ashkenaz pronunciation
     * or whether it is Israel parsha scheme.
     */
    public RegularHebrewDate(int m, int d, int y, boolean israeli) throws HebrewDateException {
        super(m, d, y);
        this.israeli = israeli;
    }

    /**
     * Initializes based on a month, date, year
     */
    public RegularHebrewDate(int m, int d, int y) throws HebrewDateException {
        super(m, d, y);
    }

    /**
     * Initializes a date based on the current system date.
     */
    public RegularHebrewDate() {
        super();
    }

    /**
     * Initializes a date based on java.util.Date object.
     */

    public RegularHebrewDate(Date date) {
        super(date);
    }

    /**
     * Initializes a date based on java.util.Calendar object.
     */

    public RegularHebrewDate(Calendar cal) {
        super(cal);
    }

    /**
     * Sets whether to use Israel parsha and holiday scheme or not. Default is "false".
     */
    public void setIsraeli(boolean israeli) {
        this.israeli = israeli;
    }

    /**
     * Gets whether Israel parsha and holiday scheme is used or not. Default is "false".
     */
    public boolean isIsraeli() {
        return israeli;
    }

    /**
     * returns a String of the holiday or fast day for the current day, or an empty string
     * if there is no holiday for this day. Has no "modern" holidays.
     *
     * @return A String containing the holiday name or an empty string.
     */
    public String getHoliday() {
        // check by month (starts from Nissan)
        switch (hebrewMonth) {
            case 1:
                if (hebrewDate == 14)
                    return "ערב פסח";

                if (hebrewDate == 15 || hebrewDate == 21 || (!israeli && (hebrewDate == 16 || hebrewDate == 22)))
                    return "פסח";

                if (hebrewDate >= 17 && hebrewDate <= 20 || (hebrewDate == 16 && israeli))
                    return "חול המועד פסח";
                break;
            case 2:
                if (hebrewDate == 14)
                    return "פסח שני";
                break;
            case 3:
                if (hebrewDate == 5) {
                    return "ערב שבועות";
                }
                if (hebrewDate == 6 || (hebrewDate == 7 && !israeli)) {
                    return "שבועות";
                }
                break;
            case 4:
                // push off the fast day if it falls on shabbos//
                if ((hebrewDate == 17 && day != 7) || (hebrewDate == 18 && day == 1))
                    return "יז בתמוז";
                break;
            case 5:
                // if tisha b'av falls on shabbos, push off until sunday//
                if ((day == 1 && hebrewDate == 10) || (day != 7 && hebrewDate == 9))
                    return "תשעה באב";
                if (hebrewDate == 15)
                    return "טו באב";
                break;
            case 6:
                if (hebrewDate == 29)
                    return "ערב ראש השנה";
                break;
            case 7:
                if (hebrewDate == 1 || hebrewDate == 2)
                    return "ראש השנה";
                // push off tzom gedalia if it falls on Shabbos //
                if ((hebrewDate == 3 && day != 7) || (hebrewDate == 4 && day == 1))
                    return "צום גדליה";
                if (hebrewDate == 9)
                    return "ערב יום כיפור";
                if (hebrewDate == 10)
                    return "יום כיפור";
                if (hebrewDate == 14) {
                    return "ערב סוכות";
                }
                if (hebrewDate == 15 || (hebrewDate == 16 && !israeli)) {
                    return "סוכות";
                }
                if (hebrewDate >= 17 && hebrewDate <= 20 || (hebrewDate == 16 && israeli)) {
                    return "חול המועד סוכות";
                }
                if (hebrewDate == 21)
                    return "הושענה רבה";
                if (hebrewDate == 22) {
                    return "שמיני עצרת";
                }
                if (hebrewDate == 23 && !israeli) {
                    return "שמחת תורה";
                }
                break;
            case 9:
                if (hebrewDate == 24)
                    return "ערב חנוכה";
                if (hebrewDate >= 25)
                    return "חנוכה";
                break;
            case 10:
                if (hebrewDate == 1 || hebrewDate == 2 || (hebrewDate == 3 && isKislevShort()))
                    return "חנוכה";
                if (hebrewDate == 10) {
                    return "עשרה בטבת";
                }
                break;
            case 11:
                if (hebrewDate == 15)
                    return "טו בשבט";
                break;
            case 12:
                if (!isHebrewLeapYear()) {
                    // if 13th adar falls on fri or shabbos, push back to thursday
                    if (((hebrewDate == 11 || hebrewDate == 12) && day == 5) ||
                            (hebrewDate == 13 && !(day == 6 || day == 7))) {
                        return "תענית אסתר";
                    }
                    if (hebrewDate == 14)
                        return "פורים";
                    if (hebrewDate == 15)
                        return "שושן פורים";
                }
                // else if a leap year //
                else {
                    if (hebrewDate == 14)
                        return "פורים קטן";
                }
                break;
            case 13:
                // if 13th adar falls on fri or shabbos, push back to thursday
                if (((hebrewDate == 11 || hebrewDate == 12) && day == 5) ||
                        (hebrewDate == 13 && !(day == 6 || day == 7))) {
                    return "תענית אסתר";
                }
                if (hebrewDate == 14)
                    return "פורים";
                if (hebrewDate == 15)
                    return "שושן פורים";
                break;
        }
        // if we get to this stage, then there are no holidays for the given date
        return "";
    }

    public boolean isShabatonHoliday() {
        // check by month (starts from Nissan)
        switch (hebrewMonth) {
            case 1:
                if (hebrewDate == 15 || hebrewDate == 21 || (!israeli && (hebrewDate == 16 || hebrewDate == 22)))
                    return true;
                break;
            case 3:
                if (hebrewDate == 6 || (hebrewDate == 7 && !israeli)) {
                    return true;
                }
                break;
            case 7:
                if (hebrewDate == 1 || hebrewDate == 2)
                    return true;

                if (hebrewDate == 10)
                    return true;

                if (hebrewDate == 15 || (hebrewDate == 16 && !israeli)) {
                    return true;
                }
                if (hebrewDate == 22) {
                    return true;
                }
                if (hebrewDate == 23 && !israeli) {
                    return true;
                }
                break;
        }
        // if we get to this stage, then there are no holidays for the given date
        return false;
    }

    /**
     * returns the int value of the omer day or zero if the day is not in the omer
     *
     * @return The omer count as an int or zero.
     */
    public int getOmer() {
        int omer = 0;

        // if nissan and second day of pesach and on //
        if (hebrewMonth == 1 && hebrewDate >= 16)
            omer = hebrewDate - 15;
        // if iyar //
        if (hebrewMonth == 2)
            omer = hebrewDate + 15;
        // if sivan and before shavuos //
        if (hebrewMonth == 3 && hebrewDate < 6)
            omer = hebrewDate + 44;

        return omer;
    }

    /**
     * returns a String of the omer day in the form "Omer X" or "Lag B'Omer"
     * or an empty string if there is no omer this day
     */
    public String getOmerAsString() {
        int omer = getOmer();

        // if not omer day //
        if (omer == 0)
            return "";
            // if lag b'omer //
        else if (omer == 33)
            return "לג בעומר";
        else
            return omer + " ימים לעומר";

    }

    /**
     * returns a string of today's parsha(ios) or an empty string if there are none
     */
    public String getParsha() {
        int index = getParshaNum();

        // If no Parsha this week
        if (index == -1) {
            return "";
        } else { // if parsha this week
/*            int subindex;*/
            return getParasha(index);
        }
    }

    public static String getParasha(int parashaNum) {
        return parshios[parashaNum];
    }

    public static String getParashaEng(int parashaNum) {
        return parshiosEng[parashaNum][0];
    }

    public static int getNumFromParasha(String parasha) {
        for (int i = 0; i < parshios.length; i++) {
            if (parshios[i].equals(parasha)) {
                return i;
            }
        }

        return -1;
    }

    public static String getParashaAsString(int parashaNum) {
        String parasha = "פרשת " + RegularHebrewDate.getParasha(parashaNum);
        if (parashaNum > 52) {
            String parasha1 = RegularHebrewDate.getParasha(parashaNum);
            parasha = "פרשות " + RegularHebrewDate.getParasha(parashaNum);
        }
        return parasha;
    }

    public int getParshaNum() {
        // if today is not shabbos, then there is no normal parsha reading
        if (getDayOfWeek() != 7)
            return -1;

        // kvia= whether a hebrew year is short/regular/long (0/1/2)
        // roshHashana= Rosh Hashana of this hebrew year
        // roshHashanaDay= day of week Rosh Hashana was on this year
        // week= current week in hebrew calendar from Rosh Hashana
        // array= the correct index array for this hebrew year
        // index= the index number of the parsha name
        int kvia;
        int roshHashanaDay;
        int week;
        int[] array = null;
        int index;
        // create a clone of this date
        HebrewDate roshHashana = (HebrewDate) this.clone();
        try {
            // set it to Rosh Hashana of this year
            roshHashana.setHebrewDate(7, 1, CURRENT_YEAR);
        } catch (HebrewDateException e) {
            e.printStackTrace();
        }

        // get day Rosh Hashana was on
        roshHashanaDay = roshHashana.getDayOfWeek();

        // week is the week since the first Shabbos on or after Rosh Hashana
        week = (((absDate - roshHashana.getAbsDate()) - (7 - roshHashanaDay)) / 7);

        // get kvia
        if (isCheshvanLong() && !isKislevShort())
            kvia = 2;
        else if (!isCheshvanLong() && isKislevShort())
            kvia = 0;
        else
            kvia = 1;

        // determine appropriate array
        if (!isHebrewLeapYear()) {
            switch (roshHashanaDay) {
                case 7:  // RH was on a Saturday
                    if (kvia == 0) array = Sat_short;
                    else if (kvia == 2) array = Sat_long;
                    break;
                case 2:  // RH was on a Monday
                    if (kvia == 0) array = Mon_short;
                    else if (kvia == 2) array = israeli ? Mon_short : Mon_long;
                    break;
                case 3:  // RH was on a Tuesday
                    if (kvia == 1) array = israeli ? Mon_short : Mon_long;
                    break;
                case 5:  // RH was on a Thursday
                    if (kvia == 1) array = israeli ? Thu_normal_Israel : Thu_normal;
                    else if (kvia == 2) array = Thu_long;
                    break;
            }
        }

        // if leap year //
        else {
            switch (roshHashanaDay) {
                case 7:  // RH was on a Sat
                    if (kvia == 0) array = Sat_short_leap;
                    else if (kvia == 2) array = israeli ? Sat_short_leap : Sat_long_leap;
                    break;
                case 2:  // RH was on a Mon
                    if (kvia == 0) array = israeli ? Mon_short_leap_Israel : Mon_short_leap;
                    else if (kvia == 2) array = israeli ? Mon_long_leap_Israel : Mon_long_leap;
                    break;
                case 3:  // RH was on a Tue
                    if (kvia == 1) array = israeli ? Mon_long_leap_Israel : Mon_long_leap;
                    break;
                case 5:  // RH was on a Thu
                    if (kvia == 0) array = Thu_short_leap;
                    else if (kvia == 2) array = Thu_long_leap;
                    break;
            }
        }
        // if something goes wrong
        if (array == null)
            throw new RuntimeException("Was not able to set the index array to any of the known types.");

        // get index from array
        return array[week];
    }

    /**
     * Create a copy of this date.
     */
    @Override
    public Object clone() {
        try {
            return new RegularHebrewDate(month, date, year, israeli);
        } catch (HebrewDateException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static int getNextHolidayNum(Calendar calendar, int daysToCheck) {
        Calendar c = (Calendar) calendar.clone();
        RegularHebrewDate h1 = new RegularHebrewDate(c.getTime());
        while (daysToCheck > 0 && h1.getImportantHoliday() == -1) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            h1 = new RegularHebrewDate(c.getTime());
            daysToCheck--;
        }
        return h1.getImportantHoliday();
    }


    public static String getNextParsha() {
        Calendar c = Calendar.getInstance();
        RegularHebrewDate h1 = new RegularHebrewDate(c.getTime());
        while ("".equals(h1.getParsha())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            h1 = new RegularHebrewDate(c.getTime());
        }

        return h1.getParsha();
    }

    public static String getNextParshaOrHoliday() {
        Calendar c = Calendar.getInstance();
        RegularHebrewDate h1 = new RegularHebrewDate(c.getTime());
        while ("".equals(h1.getParsha()) && "".equals(h1.getHoliday())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            h1 = new RegularHebrewDate(c.getTime());
        }

        return h1.getParsha() + h1.getHoliday();
    }


    public static String getNextParashaOnShabat() {
        Calendar c = Calendar.getInstance();
        while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        RegularHebrewDate h1 = new RegularHebrewDate(c.getTime());
        return h1.getParsha() + " " + h1.getHoliday();
    }

    public static String getDayInWord(Calendar c) {
        return DAYS[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static int getNextParashaNumOnShabat(Calendar calendar) {
        RegularHebrewDate h1 = getNextShabatDate(calendar);
        return h1.getParshaNum();
    }

    public static RegularHebrewDate getNextShabatDate(Calendar calendar) {
        Calendar c = (Calendar) calendar.clone();

        while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
        return new RegularHebrewDate(c.getTime());
    }

    public static int getNextParashaNumOnShabat() {
        return getNextParashaNumOnShabat(Calendar.getInstance());
    }

    public static String getCandleAndHavdala() {
        StringBuilder sb = new StringBuilder("הדלקת נרות:");
        Calendar c = Calendar.getInstance();
        CandleLighting cl = new CandleLighting(new HebrewDate(c));
        while ("".equals(cl.getCandleLighting())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            cl = new CandleLighting(new HebrewDate(c));
        }
        sb.append(cl.getCandleLighting()).append(", צאת שבת:");
        while ("".equals(cl.getHavdala())) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            cl = new CandleLighting(new HebrewDate(c));
        }
        sb.append(cl.getHavdala()).append(" עדכני ל").append(cl.getDefaultLocale().getName());

        return sb.toString();
    }

    private static final String[][] parshiosEng =
            {
                    {"Bereshit", "Bereshis"},
                    {"Noach", "Noach"},
                    {"Lech-Lecha", "Lech-Lecha"},
                    {"Vayera", "Vayera"},
                    {"Chayei Sara", "Chayei Sara"},
                    {"Toldot", "Toldos"},
                    {"Vayetzei", "Vayetzei"},
                    {"Vayishlach", "Vayishlach"},
                    {"Vayeshev", "Vayeshev"},
                    {"Miketz", "Miketz"},
                    {"Vayigash", "Vayigash"},
                    {"Vayechi", "Vayechi"},
                    {"Shemot", "Shemos"},
                    {"Vaera", "Vaera"},
                    {"Bo", "Bo"},
                    {"Beshalach", "Beshalach"},
                    {"Yitro", "Yisro"},
                    {"Mishpatim", "Mishpatim"},
                    {"Terumah", "Terumah"},
                    {"Tetzaveh", "Tetzaveh"},
                    {"Ki Tisa", "Ki Sisa"},
                    {"Vayakhel", "Vayakhel"},
                    {"Pekudei", "Pekudei"},
                    {"Vayikra", "Vayikra"},
                    {"Tzav", "Tzav"},
                    {"Shmini", "Shmini"},
                    {"Tazria", "Sazria"},
                    {"Metzora", "Metzora"},
                    {"Achrei Mot", "Achrei Mos"},
                    {"Kedoshim", "Kedoshim"},
                    {"Emor", "Emor"},
                    {"Behar", "Behar"},
                    {"Bechukotai", "Bechukosai"},
                    {"Bamidbar", "Bamidbar"},
                    {"Nasso", "Nasso"},
                    {"Beha'alotcha", "Beha'aloscha"},
                    {"Sh'lach", "Sh'lach"},
                    {"Korach", "Korach"},
                    {"Chukat", "Chukas"},
                    {"Balak", "Balak"},
                    {"Pinchas", "Pinchas"},
                    {"Matot", "Matos"},
                    {"Masei", "Masei"},
                    {"Devarim", "Devarim"},
                    {"Vaetchanan", "Vaeschanan"},
                    {"Eikev", "Eikev"},
                    {"Re'eh", "Re'eh"},
                    {"Shoftim", "Shoftim"},
                    {"Ki Teitzei", "Ki Seitzei"},
                    {"Ki Tavo", "Ki Savo"},
                    {"Nitzavim", "Nitzavim"},
                    {"Vayeilech", "Vayeilech"},
                    {"Ha'Azinu", "Ha'Azinu"},
                    {"Vayakhel Pekudei", "Vayakhel Pekudei"},
                    {"Tazria Metzora", "Sazria Metzora"},
                    {"Achrei Mot Kedoshim", "Achrei Mos Kedoshim"},
                    {"Behar Bechukotai", "Behar Bechukosai"},
                    {"Chukat Balak", "Chukas Balak"},
                    {"Matot Masei", "Matos Masei"},
                    {"Nitzavim Vayeilech", "Nitzavim Vayeilech"}
            };

    /**
     * returns a String of the holiday or fast day for the current day, or an empty string
     * if there is no holiday for this day. Has no "modern" holidays.
     *
     * @return A String containing the holiday name or an empty string.
     */
    public int getImportantHoliday() {
        // check by month (starts from Nissan)
        switch (hebrewMonth) {
            case 1:
                if (hebrewDate >= 14 && hebrewDate <= 21)
                    return 1;
                break;
            case 3:
                if (hebrewDate == 6 || hebrewDate == 5) {
                    return 2;
                }
                break;
/*            case 4:
                // push off the fast day if it falls on shabbos//
                if ((hebrewDate == 17 && day != 7) || (hebrewDate == 18 && day == 1))
                    return "יז בתמוז";
                break;*/
/*            case 5:
                // if tisha b'av falls on shabbos, push off until sunday//
                if ((day == 1 && hebrewDate == 10) || (day != 7 && hebrewDate == 9))
                    return "תשעה באב";
                if (hebrewDate == 15)
                    return "טו באב";
                break;*/
            case 6:
                if (hebrewDate == 29)
                    return 6;
                break;
            case 7:
                if (hebrewDate == 1 || hebrewDate == 2)
                    return 6;

                if (hebrewDate == 10 || hebrewDate == 9)
                    return 7;

                if (hebrewDate >= 14 && hebrewDate <= 20) {
                    return 8;
                }
                if (hebrewDate == 22) {
                    return 9;
                }
                break;
            case 9:
                if (hebrewDate >= 24)
                    return 10;
                break;
            case 10:
                if (hebrewDate == 1 || hebrewDate == 2 || (hebrewDate == 3 && isKislevShort()))
                    return 10;
                if (hebrewDate == 10) {
                    return 11;
                }
                break;
            case 11:
                if (hebrewDate == 15)
                    return 12;
                break;
            case 12:
                if (!isHebrewLeapYear()) {
                    if (hebrewDate == 14 || hebrewDate == 15)
                        return 13;
                }
                // else if a leap year //
                break;
            case 13:
                if (hebrewDate == 14 || hebrewDate == 15)
                    return 13;
                break;
        }
        // if we get to this stage, then there are no holidays for the given date
        return -1;
    }

    public static String getImportantHolidayByNum(int nextHolidayNum) {
        return nextHolidayNum == -1 ? null : RegularHebrewDate.importantHolidays[nextHolidayNum - 1];
    }


}
