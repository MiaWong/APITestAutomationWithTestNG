package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;

import java.util.Random;

/**
 * Created by miawang on 12/12/2016.
 */
public class TestTimeData {
    private int searchStartDay;
    private int durationDays;
    private boolean weekendBool;
    private boolean extraHoursBool;
    private String startIntervalDays;

    public TestTimeData(boolean weekendBool, boolean extraHoursBool) {
        this.weekendBool = weekendBool;
        this.extraHoursBool = extraHoursBool;
    }

    public TestTimeData(CommonEnumManager.TimeDuration useDays, boolean merchantBoolean , boolean weekendBool) {
        createTestTimeData(useDays, null, merchantBoolean, weekendBool, false);
    }

    public TestTimeData(CommonEnumManager.TimeDuration useDays, boolean merchantBoolean, boolean weekendBool, boolean extraHoursBool) {
        createTestTimeData(useDays, null, merchantBoolean, weekendBool, extraHoursBool);
    }

    public TestTimeData(CommonEnumManager.TimeDuration useDays, String startIntervalDays, boolean merchantBoolean, boolean weekendBool, boolean extraHoursBool) {
        createTestTimeData(useDays, startIntervalDays, merchantBoolean, weekendBool, extraHoursBool);
    }

    private void createTestTimeData(CommonEnumManager.TimeDuration useDays, String startIntervalDays, boolean merchantBoolean, boolean weekendBool, boolean extraHoursBool) {
        //_Weekend2day = 52,
        //_Weekend3day =  53,
        //_Weekend1day = 51,
        //_days3_extraHours = 54,
        final int durationDays = useDays.getTimeDuration();
        TestTimeData timeData = null;
        switch (durationDays) {
            case 51:
                // 1days ,weekend
                timeData = createTestTimeData(startIntervalDays, 1, merchantBoolean, true, extraHoursBool);
                break;
            case 52:
                // 2days ,weekend
                timeData = createTestTimeData(startIntervalDays, 2, merchantBoolean, true, extraHoursBool);
                break;
            case 53:
                // 3days ,weekend
                timeData = createTestTimeData(startIntervalDays, 3, merchantBoolean, true, extraHoursBool);
                break;
            case 54:
                // 3days ,extra hours
                timeData = createTestTimeData(startIntervalDays, 3, merchantBoolean, false, true);
                break;
            case 55:
                // 3days ,weekend  extra hours
                timeData = createTestTimeData(startIntervalDays, 3, merchantBoolean, true, true);
                break;
            default:
                timeData = createTestTimeData(startIntervalDays, durationDays, merchantBoolean, weekendBool, extraHoursBool);
                break;
        }

        this.durationDays = timeData.getDurationDays();
        this.weekendBool = timeData.isWeekendBool();
        this.searchStartDay = timeData.getSearchStartDay();
        this.extraHoursBool = timeData.isExtraHoursBool();
    }

    /**
     * this method is use to construct parameter for CreateTimeParameter Method
     * @param startIntervalDays
     * @param durationDays
     * @param merchantBoolean
     * @param weekendBool
     * @param extraHoursBool
     * @return
     */
    private TestTimeData createTestTimeData(String startIntervalDays, int durationDays, boolean merchantBoolean, boolean weekendBool, boolean extraHoursBool) {
        final TestTimeData testTimeData = new TestTimeData(weekendBool, extraHoursBool);

        // start search interval days with default 180days  // 6 month
        if (startIntervalDays == null) {
            testTimeData.setSearchStartDay(RequestDefaultValues.SEARCH_START_INTERVAL_DAYS);
        }
        else {
            testTimeData.setSearchStartDay(Integer.parseInt(startIntervalDays));
        }
        if(merchantBoolean){
            testTimeData.setSearchStartDay(RequestDefaultValues.SEARCH_START_INVERVAL_DAYS_MERCHANT);
        }
        // make sure the seart day must be after 6 month.
        if (testTimeData.getSearchStartDay() < 90) {
            testTimeData.setSearchStartDay(testTimeData.getSearchStartDay() + 90);
        }

        // default duration days
        if (durationDays == 0) {
            testTimeData.setDurationDays(new Random().nextInt(9) + 1);
        } else {
            testTimeData.setDurationDays(durationDays);
        }

        return testTimeData;
    }

    public int getSearchStartDay() {
        return searchStartDay;
    }

    public void setSearchStartDay(int searchStartDay) {
        this.searchStartDay = searchStartDay;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public boolean isWeekendBool() {
        return weekendBool;
    }

    public void setWeekendBool(boolean weekendBool) {
        this.weekendBool = weekendBool;
    }

    public boolean isExtraHoursBool() {
        return extraHoursBool;
    }

    public void setExtraHoursBool(boolean extraHoursBool) {
        this.extraHoursBool = extraHoursBool;
    }

    public String getStartIntervalDays() {
        return startIntervalDays;
    }

    public void setStartIntervalDays(String startIntervalDays) {
        this.startIntervalDays = startIntervalDays;
    }
}
