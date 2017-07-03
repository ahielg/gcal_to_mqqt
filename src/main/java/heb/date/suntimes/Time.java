package heb.date.suntimes;

/**
 * User: ahiel
 * Date: 29/07/12
 * Time: 15:10
 */

/**
 * %%Ignore-License
 * Java class for representing a time as a simple triple of numbers (hours,
 * minutes, seconds). This class is used by the SunTimes class to return sunrise
 * and sunset times, where the meanings of the hours and minutes are well defined.
 * This class obviates the need to use the JDK Date and Calendar classes, which do
 * all sorts of locale-specific corrections.
 * <p/>
 * This class can be initialized from a pair of values, or a single value which is
 * taken to mean a number of hours with a fraction. In the latter case it will
 * normalize it to the range 0:00:00 to 23:59:59.
 * <p/>
 * (c)2001 Kevin Boone/Web-Tomorrow, all rights reserved
 */
@SuppressWarnings("UnusedDeclaration")
public class Time {
    protected int hour;
    protected int minute;
    protected int second;

    public Time(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public Time(double hour) {
        double theHour = hour;
        while (theHour < 0.0) {
            theHour += 24.0;
        }
        while (theHour >= 24.0) {
            theHour -= 24.0;
        }
        this.hour = (int) theHour;
        minute = (int) ((theHour - this.hour) * 60.0);
        second = (int) (((theHour - this.hour) * 60.0 - minute) * 60.0);
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public double getFractionalHours() {
        return hour + minute / 60.0 + second / 3600.0;
    }

    public String toString() {
        String s = "";
        if (hour < 10) {
            s += "0" + hour;
        } else {
            s += "" + hour;
        }
        s += ":";
        if (minute < 10) {
            s += "0" + minute;
        } else {
            s += "" + minute;
        }
        s += ":";
        if (second < 10) {
            s += "0" + second;
        } else {
            s += "" + second;
        }
        return s;
    }
}
