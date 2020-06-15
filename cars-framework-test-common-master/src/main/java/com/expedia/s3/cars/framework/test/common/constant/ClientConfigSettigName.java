package com.expedia.s3.cars.framework.test.common.constant;

/**
 * Created by v-mechen on 9/9/2018.
 */
@SuppressWarnings("PMD")
public enum ClientConfigSettigName {
    PRICING_SURFACE_DISCOUNT("Pricing.surfaceDiscount/enable"),
    BOOKING_SOFTERRORHANDLING_RETRY("Booking.softErrorHandling.retry/enable"),
    BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS("Booking.softErrorHandling.returnSoftErrors/enable"),
    GETORDERPROCESS_RETURNREQUESTEDSPECIALEQUIPMENT("GetOrderProcess.returnRequestedSpecialEquipment/enable");


    private String m_ClientConfigName;

    private ClientConfigSettigName(String clientConfigName)
    {
        m_ClientConfigName = clientConfigName;
    }

    @Override
    public String toString()
    {
        return stringValue();
    }

    public String stringValue()
    {
        return m_ClientConfigName;
    }
}
