package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItem;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
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
public class VerifyBookingItem
{
    private VerifyBookingItem()
    {

    }

    public static void verifyBookingItem(PreparePurchaseRequestType request, PreparePurchaseResponseType response, boolean cancelled
            , TestScenario testScenario) throws DataAccessException, IOException, SQLException
    {
        final BookingItemExpDataGenerator bookingItemExpDataGenerator = new BookingItemExpDataGenerator();
        //Get expected values
        final BookingItem expBookingItem = bookingItemExpDataGenerator.getExpBookingItem(request, response, cancelled, testScenario);
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        //Get actual rows
        final BookingItem actBookingItem = carsBookingHelper.getBookingItem(BookingVerificationUtils.getBookingItemID(response));
        //Compare
        final List<String> ignoreList = new ArrayList<>();
        ignoreList.add(BookingItemObjectTag.BOOKDATE);
        ignoreList.add(BookingItemObjectTag.CANCELDATE);
        ignoreList.add(BookingItemObjectTag.CREATEDATE);
        ignoreList.add(BookingItemObjectTag.UPDATEDATE);
        ignoreList.add(BookingItemObjectTag.SUPPLIERBOOKINGCONFIRMATIONDATE);
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(expBookingItem, actBookingItem, ignoreList, erroMsg);
        if (!compared)
        {
            Assert.fail("BookingItem verify failed, BookingItemID : " + expBookingItem.getBookingItemID() + ", " + erroMsg.toString());
        }
    }
}
