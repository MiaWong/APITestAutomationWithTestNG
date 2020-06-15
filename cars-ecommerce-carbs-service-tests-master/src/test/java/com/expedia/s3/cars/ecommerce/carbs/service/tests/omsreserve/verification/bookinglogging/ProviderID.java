package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/11/2018.
 */
@SuppressWarnings("PMD")
public enum ProviderID
{
    UNKNOWN(0),
    WORLDSPAN(1),
    //SABRE(8),
    AMADEUS(6),
    MICRONNEXUS(3),
    TITANIUM(7);

    private int m_providerID;

    private ProviderID(int id)
    {
        m_providerID = id;
    }

    public byte byteValue()
    {
        return (byte) m_providerID;
    }

    public int intValue()
    {
        return m_providerID;
    }
}

