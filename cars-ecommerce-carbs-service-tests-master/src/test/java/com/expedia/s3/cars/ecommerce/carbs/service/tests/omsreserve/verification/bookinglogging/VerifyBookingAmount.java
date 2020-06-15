package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.action.ActionSequenceAbortException;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingAmount;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/5/2018.
 */
public class VerifyBookingAmount {
    private VerifyBookingAmount(){

    }

    public static void verifyBookingAmount(PreparePurchaseRequestType request, PreparePurchaseResponseType response) throws DataAccessException, ActionSequenceAbortException {
        final BookingAmountExpDataGenerator bookingAmountExpDataGenerator = new BookingAmountExpDataGenerator();
        //Get expected rows
        final List<BookingAmount> expRows = bookingAmountExpDataGenerator.getExpBookingAmountRows(request, response);
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        //Get actual rows
        final List<BookingAmount> actRows = carsBookingHelper.getBookingAmountList(BookingVerificationUtils.getBookingItemID(response), false);
        setMeneyScale(actRows);
        //Compare
        final List<String> ignoreList = new ArrayList<>();
        ignoreList.add(BookingAmountObjectTag.BOOKINGAMOUNTROWGUID);
        ignoreList.add(BookingAmountObjectTag.BOOKINGAMOUNTSEQNBR);
        ignoreList.add(BookingAmountObjectTag.CREATEDATE);
        //ignoreList.add(BookingAmount)
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(expRows, actRows, ignoreList, erroMsg);
        if (!compared)
        {
            Assert.fail("BookingAmount verify failed, BookingItemID : " + BookingVerificationUtils.getBookingItemID(response) + ", " + erroMsg.toString());
        }
    }

    public static void setMeneyScale(List<BookingAmount> amountRows)
    {
        for(final BookingAmount amount : amountRows)
        {
            amount.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(amount.getTransactionAmtCost()));
            amount.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(amount.getTransactionAmtPrice()));
        }
    }
}
