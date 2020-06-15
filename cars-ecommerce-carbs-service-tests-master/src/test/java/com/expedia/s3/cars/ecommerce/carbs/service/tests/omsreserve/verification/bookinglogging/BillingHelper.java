package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.platform.messaging.core.types.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by v-mechen on 9/9/2018.
 */
@SuppressWarnings("PMD")
public class BillingHelper {
    private static final int MILLIS_PER_HOUR = 3600 * 1000;

    private static final int MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    private static final int GRACE_PERIOD_FOR_RENTAL = MILLIS_PER_HOUR;

    private static final TimeZone CARS_SERVICE_DEFAULT_TZ = TimeZone.getDefault();

    private BillingHelper()
    {
        //NOP
    }

    /*
     * This returns the number of days that we bill a merchant car for.
     *
     */
    public static long getNumberOfBillingDays(DateTime pickup, DateTime dropOff)
    {
        long numBillingDays = 0;

        Calendar pickupTime = pickup.toCalendar();
        Calendar dropOffTime = dropOff.toCalendar();

        long pickupInMillis = pickupTime.getTimeInMillis();
        long dropOffInMillis = dropOffTime.getTimeInMillis();

        long billingMillis = (dropOffInMillis - pickupInMillis);

        // check if we are straddling day light savings begin date
        if (isRentalDaysStraddleDayLightBeginDate(pickupTime.getTime(), dropOffTime.getTime()))
        {
            // when day light savings time begins we lose an hour, so add it back.
            billingMillis += MILLIS_PER_HOUR;
        }

        // check if we are straddling day light savings end date
        if (isRentalDaysStraddleDayLightEndDate(pickupTime.getTime(), dropOffTime.getTime()))
        {
            // when day light savings time ends we gain an hour, so deduct it.
            billingMillis -= MILLIS_PER_HOUR;
        }

        long billingDays = billingMillis / MILLIS_PER_DAY;
        long extraTimeOverDays = (billingMillis % MILLIS_PER_DAY);

        // If we have more time leftover than the grace period = 1 hr
        // then we bill for the full extra day
        if (extraTimeOverDays >= GRACE_PERIOD_FOR_RENTAL)
        {
            billingDays++;
        }

        numBillingDays = billingDays;

        return numBillingDays;
    }

    private static boolean isRentalDaysStraddleDayLightBeginDate(Date startDate, Date endDate)
    {
        if (!CARS_SERVICE_DEFAULT_TZ.inDaylightTime(startDate) && CARS_SERVICE_DEFAULT_TZ.inDaylightTime(endDate))
        {
            return true;
        }
        return false;
    }

    private static boolean isRentalDaysStraddleDayLightEndDate(Date startDate, Date endDate)
    {
        if (CARS_SERVICE_DEFAULT_TZ.inDaylightTime(startDate) && !CARS_SERVICE_DEFAULT_TZ.inDaylightTime(endDate))
        {
            return true;
        }
        return false;
    }

    public static long getNumberOfBillingHours(DateTime pickup, DateTime dropOff)
    {
        long numBillingHours = 0;
        Calendar pickupTime = pickup.toCalendar();
        Calendar dropOffTime = dropOff.toCalendar();

        long pickupInMillis = pickupTime.getTimeInMillis();
        long dropOffInMillis = dropOffTime.getTimeInMillis();

        long billingMillis = (dropOffInMillis - pickupInMillis);
        long billingHours = billingMillis / MILLIS_PER_HOUR;
        long extraTimeOverHours = billingMillis % MILLIS_PER_HOUR;
        // if it's over 1/2 hour round up..
        if (extraTimeOverHours >= (MILLIS_PER_HOUR / 2))    // 1/2 hour based rounding
        {
            billingHours++;
        }
        numBillingHours = billingHours;
        return numBillingHours;
    }


    public static long getNumberOfAdvancePurchaseDays(DateTime pickup)
    {
        Calendar pickupTime = pickup.toCalendar();
        Calendar bookTime = Calendar.getInstance();

        long pickupTimeInMillis = pickupTime.getTimeInMillis();
        long bookTimeInMillis = bookTime.getTimeInMillis();

        long advancePurchaseMillis = (pickupTimeInMillis - bookTimeInMillis);
        long advancePurchaseDays = advancePurchaseMillis / MILLIS_PER_DAY;

        return advancePurchaseDays;
    }
}
