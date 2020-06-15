package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/11/2018.
 */
@SuppressWarnings("PMD")
public enum BookingItemStateIDPending
{
    NONE(0),
    CANCELPENDING(102);

    private int m_bookingItemStateIDPending;

    private BookingItemStateIDPending(int id)
    {
        m_bookingItemStateIDPending = id;
    }

    public byte byteValue()
    {
        return (byte) m_bookingItemStateIDPending;
    }

    public int intValue()
    {
        return m_bookingItemStateIDPending;
    }
}


