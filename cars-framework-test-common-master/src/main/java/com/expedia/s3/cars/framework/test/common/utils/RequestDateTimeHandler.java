package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.data.timetypes.defn.v4.DateTimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Random;

/**
 * Created by miawang on 12/11/2016.
 */
public class RequestDateTimeHandler {
    private static int startDateFromNow;
    private static int endDateFromNow;
    private static int startHour;
    private static int endHour;

    public int getStartDateFromNow() {
        return startDateFromNow;
    }

    public void setStartDateFromNow(int startDateFromNow) {
        this.startDateFromNow = startDateFromNow;
    }

    public int getEndDateFromNow() {
        return endDateFromNow;
    }

    public void setEndDateFromNow(int endDateFromNow) {
        this.endDateFromNow = endDateFromNow;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    private static SegmentDateTimeRangeType createSegmentDateTimeRange() {
        final SegmentDateTimeRangeType segmentDateTimeRange = new SegmentDateTimeRangeType();
        // start date time
        final DateTimeRangeType startDateTimeRange = new DateTimeRangeType();

        //handle like this cause next will add hour we configured.
        final DateTime dateTimeNow = DateTime.now().plusHours(12 - DateTime.now().getHourOfDay()).plusSeconds(20 - DateTime.now().getSecondOfMinute());
        DateTime startDateTime = dateTimeNow.plusDays(startDateFromNow);

        if (startHour != 0) {
            startDateTime = startDateTime.plusHours(startHour - startDateTime.getHourOfDay());
            startDateTimeRange.setMinDateTime(new com.expedia.e3.platform.messaging.core.types.DateTime(startDateTime.toString()));
        }
        startDateTimeRange.setMaxDateTime(startDateTimeRange.getMinDateTime());
        segmentDateTimeRange.setStartDateTimeRange(startDateTimeRange);

        // end date time
        final DateTimeRangeType endDateTimeRange = new DateTimeRangeType();
        DateTime endDateTime = dateTimeNow.plusDays(endDateFromNow);

        if (endHour != 0) {
            endDateTime = endDateTime.plusHours(endHour - endDateTime.getHourOfDay());
            endDateTimeRange.setMinDateTime(new com.expedia.e3.platform.messaging.core.types.DateTime(endDateTime.toString()));
        }
        endDateTimeRange.setMaxDateTime(endDateTimeRange.getMinDateTime());
        segmentDateTimeRange.setEndDateTimeRange(endDateTimeRange);
        return segmentDateTimeRange;
    }

    public SegmentDateTimeRangeType dateTimeSpecialHandler(TestTimeData testTimeData) {
        // set the time
        this.startDateFromNow = testTimeData.getSearchStartDay();
        this.endDateFromNow = testTimeData.getSearchStartDay() + testTimeData.getDurationDays();

        //handle Extra hours
        if (testTimeData.isExtraHoursBool()) {
            this.startHour = 22;
            this.endHour = 24;
        } else {
            this.startHour = 10;
            this.endHour = 10;
        }

        //handle weekend
        if (testTimeData.isWeekendBool()) {
            final DateTime startDate = DateTime.now().plusDays(startDateFromNow);
            //.plusHours(12-DateTime.now().getHourOfDay()).plusSeconds(20 - DateTime.now().getSecondOfMinute());

            if (startDate.getDayOfWeek() != DateTimeConstants.FRIDAY && startDate.getDayOfWeek() != DateTimeConstants.SATURDAY) {
                if (testTimeData.getDurationDays() == 3) {
                    this.startDateFromNow = this.startDateFromNow + (DateTimeConstants.FRIDAY - startDate.getDayOfWeek());
                    this.endDateFromNow = this.startDateFromNow + 3;
                } else if (testTimeData.getDurationDays() == 2) {
                    final Random rd = new Random();
                    //For Weekend two days, it may start from Friday or Saturday.
                    final int ranNum = rd.nextInt(1);
                    this.startDateFromNow = this.startDateFromNow + DateTimeConstants.FRIDAY - startDate.getDayOfWeek() + ranNum;
                    this.endDateFromNow = this.startDateFromNow + 2;
                } else if (testTimeData.getDurationDays() == 1) {
                    //For Weekend 1 days, there is also possible that it start from Sunday(time < 12:00 ), then add extra hours, but end time must <= 12 on Monday.
                    final Random rd = new Random();
                    final int ranNum = rd.nextInt(2);
                    this.startDateFromNow = this.startDateFromNow + (DateTimeConstants.FRIDAY - startDate.getDayOfWeek()) + ranNum;
                    this.endDateFromNow = this.startDateFromNow + 1;
                }
            }
        }

        return this.createSegmentDateTimeRange();
    }
}