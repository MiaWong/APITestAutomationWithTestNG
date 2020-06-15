package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/11/2018.
 */
@SuppressWarnings("PMD")
public enum RevenueReportingTypeID
{
    INVALID(0),
    AGENT_BOOKING(1),
    AGENT_USE(2),
    MERCHANT_BOOKING(3),
    MERCHANT_USE(4),
    GDSPMERCHANT(5),
    AGENCY_PREPAID_BOOKING(9),
    MERCHANT_COMMISSION_MARKUP(10),
    LAST(11);

    private int m_revenueReportingID;

    private RevenueReportingTypeID(int id)
    {
        m_revenueReportingID = id;
    }

    public int intValue()
    {
        return m_revenueReportingID;
    }

    public static RevenueReportingTypeID asRevenueReportingTypeID(int value)
    {
        RevenueReportingTypeID revenueReportingType = INVALID;
        if (value < LAST.intValue())
        {
            if (AGENT_BOOKING.intValue() == value)
            {
                revenueReportingType = AGENT_BOOKING;
            }
            else if (AGENT_USE.intValue() == value)
            {
                revenueReportingType = AGENT_USE;
            }
            else if (MERCHANT_BOOKING.intValue() == value)
            {
                revenueReportingType = MERCHANT_BOOKING;
            }
            else if (MERCHANT_USE.intValue() == value)
            {
                revenueReportingType = MERCHANT_USE;
            }
            else if (GDSPMERCHANT.intValue() == value)
            {
                revenueReportingType = GDSPMERCHANT;
            }
            else if (AGENCY_PREPAID_BOOKING.intValue() == value)
            {
                revenueReportingType = AGENCY_PREPAID_BOOKING;
            }
            else if (MERCHANT_COMMISSION_MARKUP.intValue() == value)
            {
                revenueReportingType = MERCHANT_COMMISSION_MARKUP;
            }
        }

        return revenueReportingType;
    }
}

