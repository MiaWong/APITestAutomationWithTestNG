package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

/**
 * Created by v-mechen on 9/4/2018.
 */
/*
# //-----------------------------------------------------------------------------
# // AmountLevelId
# //-----------------------------------------------------------------------------

# // SQL_DOMAIN_TYPE_SYNC
# //
# // ID: 305
# // Type: Booking Amount Level
# // Desc: Identifies level (i.e., Booking, Item, Inventory) of Booking Amount

.    AmountLevelId    kamlInvalid                        0
# // DOMAIN_VALUE_START
.    AmountLevelId    kamlBooking                        1
.    AmountLevelId    kamlBookingItem                    2
.    AmountLevelId    kamlBookingItemInventory        3
.    AmountLevelId    kamlBookingItemBookingTraveler    4
# // DOMAIN_VALUE_END

.    AmountLevelId    kamlLast_R17                        5

 */
@SuppressWarnings("PMD")
public enum AmountLevelId
{
    INVALID(0),
    BOOKING(1),
    BOOKING_ITEM(2),
    BOOKING_ITEM_INVENTORY(3),
    BOOKING_ITEM_BOOKING_TRAVELER(4);

    private int m_amld;

    private AmountLevelId(int amlId)
    {
        m_amld = amlId;
    }

    public int intValue()
    {
        return m_amld;
    }

    public byte byteValue()
    {
        return (byte) m_amld;
    }
}

