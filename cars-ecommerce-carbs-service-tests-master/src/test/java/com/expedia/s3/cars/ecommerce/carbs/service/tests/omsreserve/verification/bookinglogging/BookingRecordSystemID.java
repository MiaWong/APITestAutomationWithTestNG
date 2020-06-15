package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/10/2018.
 */
@SuppressWarnings("PMD")
public enum BookingRecordSystemID
{
    /*
    .    BookingRecordSystemID    kbsysUnknown            0
    .    BookingRecordSystemID    kbsysWorldspan            1
    .    BookingRecordSystemID    kbsysSabre                8
    .    BookingRecrodSystemID    kbsysAmadeus            9
    .    BookingRecordSystemID    kbsysMicronNexus        17
    .    BookingRecordSystemID    kbsysTitanium            18
    */

    UNKNOWN(0),
    WORLDSPAN(1),
    SABRE(8),
    AMADEUS(9),
    MICRONNEXUS(17),
    TITANIUM(18);

    private int m_bookingRecordSystemID;

    private BookingRecordSystemID(int id)
    {
        m_bookingRecordSystemID = id;
    }

    public byte byteValue()
    {
        return (byte) m_bookingRecordSystemID;
    }

    public int intValue()
    {
        return m_bookingRecordSystemID;
    }
}
