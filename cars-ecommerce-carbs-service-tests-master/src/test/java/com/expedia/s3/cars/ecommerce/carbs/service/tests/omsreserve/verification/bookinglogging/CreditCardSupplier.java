package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by v-mechen on 9/10/2018.
 */
@SuppressWarnings("PMD")
public enum CreditCardSupplier
{
    INVALID("INVALID", -1),
    VISA("Visa", 0),
    MASTER_CARD("MasterCard", 1),
    AMEX("AmericanExpress", 2),
    DISCOVER("DiscoverNetwork", 3),
    DINERS_CLUB("DinersClub", 4),
    UATP("UATP", 5),
    CARTE_BLANCHE("CarteBlanche", 6),
    JCB("JCB", 8),
    SWITCH("Switch", 12),
    SOLO("Solo", 13);

    private static final ConcurrentHashMap<String, CreditCardSupplier> S_CODE_MAP
            = new ConcurrentHashMap<String, CreditCardSupplier>();

    static
    {
        for (CreditCardSupplier provider : EnumSet.allOf(CreditCardSupplier.class))
        {
            S_CODE_MAP.put(provider.getCode(), provider);
        }
    }

    private String m_code;
    private int m_id;

    private CreditCardSupplier(String code, int providerId)
    {
        m_code = code;
        m_id = providerId;
    }

    public byte getId()
    {
        return (byte) m_id;
    }

    public String getCode()
    {
        return m_code;
    }

    public static CreditCardSupplier fromCode(String providerCode)
    {
        CreditCardSupplier provider = INVALID;

        CreditCardSupplier retrieved = S_CODE_MAP.get(providerCode);

        if (null != retrieved)
        {
            provider = retrieved;
        }

        return provider;
    }
}
