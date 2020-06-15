package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/11/2018.
 */
@SuppressWarnings("PMD")
public enum BookingItemState
{
    /*
    .    BookingItemState    kbstBooked            1
    .    BookingItemState    kbstCancelled        2
    .    BookingItemState    kbstReserved        3
    */

    BOOKED(1),
    CANCELLED(2),
    RESERVED(3);

    private int m_bookingItemState;

    private BookingItemState(int id)
    {
        m_bookingItemState = id;
    }

    public byte byteValue()
    {
        return (byte) m_bookingItemState;
    }

    public int intValue()
    {
        return m_bookingItemState;
    }
}
