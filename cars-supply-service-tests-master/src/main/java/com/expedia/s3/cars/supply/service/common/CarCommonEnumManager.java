package com.expedia.s3.cars.supply.service.common;

/**
 * Created by yyang4 on 8/24/2016.
 */
public class CarCommonEnumManager {
    public static enum ServieProvider
    {
        worldSpanSCS(1),
        expediaSCS(2),
        MNSCS(3),
        Amadeus(6),
        TitaniumSCS(7),
        CarBS,
        CarSS;

        private int value ;

        private ServieProvider(int value) {
            this.value = value;
        }
        private ServieProvider() {
        }

        public int getValue() {
            return value;
        }
    }

    public static enum BookingStateCode
    {
        Booked,
        Pending,
        Confirm,
        Reserved,
        Cancelled,
        NotBooked,
        Unconfirmed,
        PendingCancel
    }

}
