package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.platform.messaging.core.types.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by yyang4 on 12/11/2016.
 */
@SuppressWarnings("PMD")
public class DateTimeUtil {
    public static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT2 = "yyyy-MM-dd";
    public static final String FORMAT3 = "HH:mm:ss";
    private DateTimeUtil(){}

    public static long getDiffDays(DateTime dt1, DateTime dt2) {
        org.joda.time.DateTime dateTime1 = new org.joda.time.DateTime(dt1.getDateTimeString());
        org.joda.time.DateTime dateTime2 = new org.joda.time.DateTime(dt2.getDateTimeString());
        return Days.daysBetween(dateTime2,dateTime1).getDays();
    }

    public static long getDiffHours(DateTime dt1, DateTime dt2) {
        org.joda.time.DateTime dateTime1 = new org.joda.time.DateTime(dt1.getDateTimeString());
        org.joda.time.DateTime dateTime2 = new org.joda.time.DateTime(dt2.getDateTimeString());
        return Hours.hoursBetween(dateTime2,dateTime1).getHours();
    }

    public static long getDiffMinutes(DateTime dt1, DateTime dt2) {
        org.joda.time.DateTime dateTime1 = new org.joda.time.DateTime(dt1.getDateTimeString());
        org.joda.time.DateTime dateTime2 = new org.joda.time.DateTime(dt2.getDateTimeString());
        return Minutes.minutesBetween(dateTime2,dateTime1).getMinutes();
    }

    public static long getDiffSeconds(DateTime dt1, DateTime dt2) {
        org.joda.time.DateTime dateTime1 = new org.joda.time.DateTime(dt1.getDateTimeString());
        org.joda.time.DateTime dateTime2 = new org.joda.time.DateTime(dt2.getDateTimeString());
        return Seconds.secondsBetween(dateTime2,dateTime1).getSeconds();
    }

    public static long getDiffMillis(DateTime dt1,DateTime dt2){
        if(null != dt1 && null != dt2) {
            final Calendar calendar1 = dt1.toCalendar();
            final Calendar calendar2 = dt2.toCalendar();
            calendar1.setTimeZone(TimeZone.getDefault());
            calendar2.setTimeZone(TimeZone.getDefault());
            return Math.abs(calendar1.getTimeInMillis() - calendar2.getTimeInMillis());
        }
        return 1000;
    }

    public static String getFormatString(DateTime datetime,String format){
        final Calendar calendar = datetime.toCalendar();
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(calendar.getTime());
    }

    public static DateTime  addDays(DateTime dateTime, int days)
    {
       org.joda.time.DateTime dateTime1 = new org.joda.time.DateTime(dateTime.getDateTimeString());
        dateTime1.plusDays(days);
       return DateTime.getInstanceByDateTime(dateTime.toCalendar());

    }
}
