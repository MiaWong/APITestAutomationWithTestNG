package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by v-mechen on 9/4/2018.
 */

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Below is the mapping to MonetaryCalculationID as seen in worldspan scs
 * 'Hourly' -> 7
 * 'ExtraHourly' -> 7
 * 'Daily' -> 7
 * 'ExtraDaily' -> 7
 * 'Weekly' -> 8
 * 'ExtraWeekly' -> 8
 * 'Weekend' -> 9
 * 'Monthly' -> 13
 * 'Trip' -> 14
 * otherwise>14
 * ===================================
 * Mapping to RatePlan is from enum RatePlan defined in \\travcore\travobj\carreq.h
 */
@SuppressWarnings("PMD")
public enum RatePeriodMap
{
    /*
     * enum defined in \\travcore\travobj\carreq.h
     enum RatePlan
    {
    kUninitialized = 0
    kIllegal = 1
    kHourly = 2
    kDaily = 3
    kWeekend = 4
    kWeekly = 5
    kMonthly = 6
    kBase = 7
    kTotal = 8
    kPackage = 9
    kNone = 10
    kUnknown = 11
    kMerchantBaseSell = 12

     */

    RATE_PERIOD_HOURLY("Hourly", 2, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_DAILY("Daily", 3, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_NIGHTLY("Nightly", 0, MonetaryCalculationId.AGENCY_CARS_BASE_RATE),
    RATE_PERIOD_WEEKLY("Weekly", 5, MonetaryCalculationId.AGENCY_CARS_WEEKLY_BASE_RATE),
    RATE_PERIOD_WEEKEND("Weekend", 4, MonetaryCalculationId.AGENCY_CARS_WEEKEND_BASE_RATE),
    RATE_PERIOD_THREE_DAY_WKND("ThreeDayWeekend", 4, MonetaryCalculationId.AGENCY_CARS_BASE_RATE), // fix for bug 142983
    RATE_PERIOD_MONTHLY("Monthly", 6, MonetaryCalculationId.AGENCY_CARS_MONTHLY_BASE_RATE),
    RATE_PERIOD_TRIP("Trip", 8, MonetaryCalculationId.AGENCY_CARS_BASE_RATE),
    RATE_PERIOD_XTRA_DAY("ExtraDay", 0, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_XTRA_HOUR("ExtraHour", 0, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_XTRA_WEEK("ExtraWeek", 0, MonetaryCalculationId.AGENCY_CARS_WEEKLY_BASE_RATE),
    RATE_PERIOD_XTRA_DAILY("ExtraDaily", 0, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_XTRA_HOURLY("ExtraHourly", 0, MonetaryCalculationId.AGENCY_CARS_DAILY_BASE_RATE),
    RATE_PERIOD_XTRA_WEEKLY("ExtraWeekly", 0, MonetaryCalculationId.AGENCY_CARS_WEEKLY_BASE_RATE);

    private static final Map<String, Byte> S_RATE_PERIOD_MAP = new HashMap<String, Byte>();

    private static final Map<String, MonetaryCalculationId> S_RATE_PERIOD_MONETARY_CALC_ID_MAP
            = new HashMap<String, MonetaryCalculationId>();

    private static final MonetaryCalculationId DEFAULT_MONETARY_CALC_ID = MonetaryCalculationId.AGENCY_CARS_BASE_RATE;


    static
    {
        for (RatePeriodMap ratePeriodCode : EnumSet.allOf(RatePeriodMap.class))
        {
            S_RATE_PERIOD_MAP.put(ratePeriodCode.getRatePeriodCode(), ratePeriodCode.getRatePlan());
            S_RATE_PERIOD_MONETARY_CALC_ID_MAP
                    .put(ratePeriodCode.getRatePeriodCode(), ratePeriodCode.getMonetaryCalcID());
        }
    }

    private String m_ratePeriodCode;
    private byte m_ratePlan;
    private MonetaryCalculationId m_monetaryCalcID;

    private RatePeriodMap(String ratePeriodCode, int ratePeriodId, MonetaryCalculationId calcId)
    {
        m_ratePeriodCode = ratePeriodCode;
        m_ratePlan = (byte) ratePeriodId;
        m_monetaryCalcID = calcId;
    }

    public String getRatePeriodCode()
    {
        return m_ratePeriodCode;
    }

    public String getStringValue()
    {
        return m_ratePeriodCode;
    }

    public byte getRatePlan()
    {
        return m_ratePlan;
    }

    public static byte getRatePlan(String ratePeriodCode)
    {
        Byte ratePeriodId = S_RATE_PERIOD_MAP.get(ratePeriodCode);

        if (null == ratePeriodId || 0 == ratePeriodId)
        {
            String message = "Could not map a period id for period code : " + ratePeriodCode;

            ratePeriodId = (byte) 0;
        }

        return ratePeriodId;
    }

    public static MonetaryCalculationId getMonetaryCalculationId(String ratePeriodCode)
    {
        MonetaryCalculationId calcId = S_RATE_PERIOD_MONETARY_CALC_ID_MAP.get(ratePeriodCode);

        if (null == calcId)
        {
            calcId = DEFAULT_MONETARY_CALC_ID;
        }

        return calcId;
    }

    public MonetaryCalculationId getMonetaryCalcID()
    {
        return m_monetaryCalcID;
    }

    public void setMonetaryCalcID(MonetaryCalculationId monetaryCalcID)
    {
        m_monetaryCalcID = monetaryCalcID;
    }
}


