package com.expedia.s3.cars.framework.test.common.constant;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v-mechen on 9/4/2018.
 */
@SuppressWarnings("PMD")
public enum MonetaryCalculationId
{

    INVALID(-1),
    /**
     * 0
     */
    AGENCY_CARS_TOTAL(0),
    /**
     * 1
     */
    ADJUSTMENTS(1),
    /**
     * 2
     */
    MERCHANT_CARS_VARIABLE_COST(2),
    /**
     * 3
     */
    MERCHANT_CARS_EXPEDIA_CANCELLATION_FEE(3),
    /**
     * 4
     */
    MERCHANT_CARS_FIXED_COST(4),
    /**
     * 5
     */
    GENERIC_ADJUSTMENTS(5),
    /**
     * 6
     */
    AGENCY_CARS_ESTIMATED_TOTAL_BASE_RATE(6),
    /**
     * 7
     */
    AGENCY_CARS_DAILY_BASE_RATE(7),
    /**
     * 8
     */
    AGENCY_CARS_WEEKLY_BASE_RATE(8),
    /**
     * 9
     */
    AGENCY_CARS_WEEKEND_BASE_RATE(9),
    /**
     * 10
     */
    MONETARY_GENERIC_TAX(10),
    /**
     * 13
     */
    AGENCY_CARS_MONTHLY_BASE_RATE(13),
    /**
     * 14
     */
    AGENCY_CARS_BASE_RATE(14),
    /**
     * 15
     */
    TRIP_TOTAL(15),
    /**
     * 21
     */
    MONETARY_GENERIC_SUPPLIER_FEE(21),
    /**
     * 27
     */
    MONETARY_DROPOFF_CHARGE(27);

    private static final Map<Integer, MonetaryCalculationId> S_LOOKUP = new HashMap<Integer, MonetaryCalculationId>();
    private int m_calcId;

    private MonetaryCalculationId(int calcId)
    {
        m_calcId = calcId;
    }

    static
    {
        for (MonetaryCalculationId s : EnumSet.allOf(MonetaryCalculationId.class))
        {
            S_LOOKUP.put(Integer.valueOf(s.intValue()), s);
        }
    }

    public static MonetaryCalculationId asEnum(int calcId)
    {
        MonetaryCalculationId t = S_LOOKUP.get(Integer.valueOf(calcId));
        if (t != null)
        {
            return t;
        }

        return INVALID;
    }

    public int intValue()
    {
        return m_calcId;
    }

    public byte byteValue()
    {
        return (byte) m_calcId;
    }

}