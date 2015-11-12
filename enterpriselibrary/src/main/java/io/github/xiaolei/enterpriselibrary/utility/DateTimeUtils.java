package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * TODO: add comment
 */
public class DateTimeUtils {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDate(String date) {
        return parseDateTime(date, DATE_PATTERN);
    }

    public static Date parseDateTime(String datetime) {
        return parseDateTime(datetime, DATE_TIME_PATTERN);
    }

    public static Date parseDateTime(String datetime, String pattern) {
        if (TextUtils.isEmpty(datetime) || TextUtils.isEmpty(pattern)) {
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date replaceWithCurrentTime(Date date) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, cal2.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, cal2.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal2.get(Calendar.DAY_OF_MONTH));

        return cal.getTime();
    }

    public static boolean isDateEqualsIgnoreTime(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return false;
        }

        if (date1 == null && date2 != null) {
            return true;
        }

        if (date2 == null && date1 != null) {
            return true;
        }

        if (date1 == date2) {
            return true;
        }

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        return format.format(date);
    }

    public static String formatShortDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-d");
        return format.format(date);
    }

    public static String formatDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
        return format.format(date);
    }

    public static String getStartTimeStringOfDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return format.format(date);
    }

    public static String getEndTimeStringOfDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        return format.format(date);
    }

    public static Date getStartTimeOfDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
        try {
            return format.parse(getStartTimeStringOfDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date getEndTimeOfDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_PATTERN);
        try {
            return format.parse(getEndTimeStringOfDate(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Date getStartDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return cal.getTime();
    }

    public static Date getStartDayOfNextWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDayOfWeek(date));

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        return cal.getTime();
    }

    public static Date addWeeks(Date date, int weeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);

        return cal.getTime();
    }

    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);

        return cal.getTime();
    }

    public static Date getStartDayOfWeek(Date date, int weekIndex) {
        Date startDay = getStartDayOfWeek(date);
        Date result = addWeeks(startDay, weekIndex);
        return result;
    }

    public static Date getStartDayOfMonth(Date date, int monthIndex) {
        Date startDay = getStartDayOfMonth(date);
        Date result = addMonths(startDay, monthIndex);
        return result;
    }

    public static Date getStartDayOfYear(Date date, int yearIndex) {
        Date startDay = getStartDayOfYear(date);
        Date result = addYears(startDay, yearIndex);
        return result;
    }

    public static Date getEndDayOfWeek(Date date) {
        Date startDayOfWeek = getStartDayOfWeek(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDayOfWeek);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);

        cal.add(Calendar.DAY_OF_WEEK, 6); // Add 6 days, to the end day of the week
        return cal.getTime();
    }

    public static Date getStartDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getStartDayOfNextMonth(Date date) {
        Date startDayOfMonth = getStartDayOfMonth(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDayOfMonth);

        // get start of the next month
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public static Date getEndDayOfMonth(Date date) {
        Date startDayOfNextMonth = getStartDayOfNextMonth(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDayOfNextMonth);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);

        cal.add(Calendar.DAY_OF_MONTH, -1);

        return cal.getTime();
    }

    public static Date getStartDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_YEAR, 1);
        return cal.getTime();
    }

    public static Date getEndDayOfYear(Date date) {
        Date startDayOfWeek = getStartDayOfYear(date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDayOfWeek);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.clear(Calendar.MILLISECOND);

        cal.add(Calendar.YEAR, 1); // Add 1 year
        cal.add(Calendar.DAY_OF_YEAR, -1); // Previous day

        return cal.getTime();
    }

    public static Date getStartDayOfNextYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getStartDayOfYear(date));

        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    public static long betweenDays(Date startDate, Date endDate) {
        long diff = Math.abs(endDate.getTime() - startDate.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(diff);

        double days = hours / 24.0d;
        return days % 1 == 0 ? (int) days : (int) days + 1;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);

        return cal.getTime();
    }
}
