package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.Booking;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.junit.Assert;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/5/2018.
 */
public class VerifyBooking
{
    private VerifyBooking()
    {

    }

    public static void verifyBooking(PreparePurchaseRequestType request, PreparePurchaseResponseType response) throws DataAccessException, IOException, SQLException
    {
        final BookingExpDataGenerator bookingExpDataGenerator = new BookingExpDataGenerator();
        //Get expected values
        final Booking expBooking = bookingExpDataGenerator.getExpBooking(request, response);
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        //Get actual rows
        final Booking actBooking = carsBookingHelper.getBooking(BookingVerificationUtils.getBookingID(response));
        //Compare
        final List<String> ignoreList = new ArrayList<>();
        ignoreList.add(BookingItemObjectTag.CREATEDATE);
        ignoreList.add(BookingItemObjectTag.UPDATEDATE);
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(expBooking, actBooking, ignoreList, erroMsg);
        if (!compared)
        {
            Assert.fail("Booking verify failed, BookingID : " + expBooking.getBookingID() + ", " + erroMsg.toString());
        }
    }
}