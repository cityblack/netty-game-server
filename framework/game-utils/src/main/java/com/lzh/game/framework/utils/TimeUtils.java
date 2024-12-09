package com.lzh.game.framework.utils;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class TimeUtils {

    private static final String DATA_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(DATA_FORMAT_STR);

    public static final String FIVE_CLOCK_CRON = "0 0 5 * * ?";

    public static final String ZERO_CLOCK_CRON = "0 0 0 * * ?";

    private static final CronExpression FIVE_EXPRESSION = CronExpression.parse(FIVE_CLOCK_CRON);

    private static final CronExpression ZERO_EXPRESSION = CronExpression.parse(ZERO_CLOCK_CRON);

    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * According "yyyy-MM-dd HH:mm:ss" to parse date string.
     * <p>
     * str2Date("2024-10-01 12:00:00") is right
     * <p>
     * str2Date("2024-10-01") is error
     */
    public static Date str2Date(String dataString) {
        try {
            return DATE_FORMAT.parse(dataString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see #str2Date
     */
    public static String now2Str() {
        return date2Str(new Date());
    }

    /**
     * @see #str2Date
     */
    public static String timestamp2Str(long timestamp) {
        return date2Str(new Date(timestamp));
    }

    /**
     * Formats a {@link Date} object using a {@link GregorianCalendar}.
     *
     * @param date the date to format
     * @return the formatted string
     */
    public static String date2Str(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * (non-javadoc)
     */
    public static boolean isSameDay(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * (non-javadoc)
     */
    public static boolean isSameDayWithNow(long timestamp) {
        return isSameDay(timestamp, now());
    }

    /**
     * (non-javadoc)
     */
    public static boolean isSameMonth(long time1, long time2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);

        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

    /**
     * Get the timestamp of tomorrow's zero clock
     *
     * @return Zero clock of tomorrow
     */
    public static long getZeroTimeOfTomorrow() {
        return getHourMinuteTimeOfTomorrow(0, 0);
    }

    /**
     * Get the timestamp of today's zero clock
     *
     * @return Zero clock of today
     */
    public static long getZeroTimeOfToDay() {
        return getHourMinuteTimeOfToday(0, 0);
    }

    /**
     * @see #hourMinuteAddDay
     */
    public static long getHourMinuteTimeOfTomorrow(int hour, int minute) {
        return hourMinuteAddDay(hour, minute, 1);
    }

    /**
     * @see #hourMinuteAddDay
     */
    public static long getHourMinuteTimeOfToday(int hour, int minute) {
        return hourMinuteAddDay(hour, minute, 0);
    }

    /**
     * Get timestamp of a few days later (hour:minute).
     * <p>
     * Today is "2024-10-18"
     * <p>
     * hourMinuteAddDay(10,10,1) return timestamp of "2024-10-19 10:10:0"
     * <p>
     * hourMinuteAddDay(22,30,3) return timestamp of "2024-10-21 22:30:0"
     *
     * @param addDays a few days later
     * @return - timestamp
     */
    public static long hourMinuteAddDay(int hour, int minute, int addDays) {
        Calendar calendar = Calendar.getInstance();
        if (addDays != 0) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @see #isNowGoneCronFromTime
     */
    public static boolean isNowGoneFiveClockFromTime(long timestamp) {
        return isNowGoneCronFromTime(FIVE_EXPRESSION, timestamp);
    }

    /**
     * @see #isNowGoneCronFromTime
     */
    public static boolean isNowGoneZeroClockFromTime(long timestamp) {
        return isNowGoneCronFromTime(ZERO_EXPRESSION, timestamp);
    }

    /**
     * @see #isNowGoneCronFromTime(CronExpression, long)
     */
    public static boolean isNowGoneCronFromTime(String cron, long timestamp) {
        return isNowGoneCronFromTime(CronExpression.parse(cron), timestamp);
    }

    /**
     * Current time is over the next time of timestamp
     * @param expression cron
     */
    private static boolean isNowGoneCronFromTime(CronExpression expression, long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        var next = expression.next(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()));
        Assert.notNull(next, timestamp + " cannot find next time.");
        return !next.isBefore(LocalDateTime.now());
    }

    /**
     * @see CronExpression#next(Temporal) 
     */
    public static LocalDateTime getCronNextTime(String cron, long timestamp) {
        var cronExpression = CronExpression.parse(cron);
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        var next = cronExpression.next(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()));
        Assert.notNull(next, timestamp + " cannot find next time.");
        return next;
    }

    private TimeUtils() {
    }

}
