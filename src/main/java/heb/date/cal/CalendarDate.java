package heb.date.cal;


import java.util.Calendar;

/**
 * @author ahiel
 * @date 27/07/12
 */
public class CalendarDate {
    private int day;
    private int month;
    private int year;

    CalendarDate(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    CalendarDate(CalendarDate date) {
        this.day = date.day;
        this.month = date.month;
        this.year = date.year;
    }

    public CalendarDate(Calendar date) {
        this.day = date.get(Calendar.DAY_OF_MONTH);
        this.month = date.get(Calendar.MONTH) + 1;
        this.year = date.get(Calendar.YEAR);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean areDatesEqual(CalendarDate date) {
        return (day == date.day) &&
                (month == date.month) &&
                (year == date.year);
    }

    public int getHashCode() {
        return (year - 1583) * 366 + month * 31 + day;
    }

    public String toString() {
        return day + "." + month + '.' + year;
    }
}