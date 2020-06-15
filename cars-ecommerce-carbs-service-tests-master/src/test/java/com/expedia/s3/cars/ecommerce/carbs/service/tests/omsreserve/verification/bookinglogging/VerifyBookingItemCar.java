package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItemCar;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.junit.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/5/2018.
 */
public class VerifyBookingItemCar {
    private VerifyBookingItemCar(){

    }

    public static void verifyBookingItemCar(PreparePurchaseRequestType request, PreparePurchaseResponseType response,
                                            TestScenario testScenario, String guid,
                                            CarECommerceSearchResponseType standaloneSearchRsp, SpooferTransport spooferTransport) throws DataAccessException, IOException, SQLException {
        final BookingItemCarExpDataGenerator bookingItemCarExpDataGenerator = new BookingItemCarExpDataGenerator();
        //Get expected values
        final BookingItemCar expBookingItemCar = bookingItemCarExpDataGenerator.getExpBookingItemCar(request, response,
                testScenario, guid, standaloneSearchRsp, spooferTransport);
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        //Get actual rows
        final BookingItemCar actBookingItemCar = carsBookingHelper.getBookingItemCar(BookingVerificationUtils.getBookingItemID(response));
        setMeneyScale(actBookingItemCar);
        setMeneyScale(expBookingItemCar);
        //Compare
        final List<String> ignoreList = new ArrayList<>();
        /*ignoreList.add(BookingAmountObjectTag.BOOKINGAMOUNTROWGUID);
        ignoreList.add(BookingAmountObjectTag.BOOKINGAMOUNTSEQNBR);
        ignoreList.add(BookingAmountObjectTag.CREATEDATE);*/
        //ignoreList.add(BookingAmount)
        //TODO CASSS-11909 PublishedPriceAmt and CurrencyCodePublishedPrice are not logged for prepaid agency car
        //After bug fix, we should remove below code:
        /*if(testScenario.getBusinessModel() == 1 &&
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean())
        {
            ignoreList.add("publishedPriceAmt");
        }*/
        if(testScenario.getBusinessModel() == 1 &&
                request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getPrePayBoolean().booleanValue())
        {
            ignoreList.add("publishedPriceAmt");
        }
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(expBookingItemCar, actBookingItemCar, ignoreList, erroMsg);
        if(!compared){
            Assert.fail("BookingItemCar verify failed, BookingItemID : "+expBookingItemCar.getBookingItemID() + ", "+erroMsg.toString());
        }
    }

    public static void setMeneyScale(BookingItemCar bookingItemCar)
    {
        bookingItemCar.setExchangeRate(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getExchangeRate())).toPlainString());
        bookingItemCar.setEstimatedTotalTaxAndFeeAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getEstimatedTotalTaxAndFeeAmt())).toPlainString());
        bookingItemCar.setMarkupPct(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getMarkupPct())).toPlainString());
        bookingItemCar.setDiscountAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getDiscountAmt())).toPlainString());
        if(null == bookingItemCar.getPublishedPriceAmt()) {
            bookingItemCar.setPublishedPriceAmt(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO).toPlainString());
        }
        else
        {
            bookingItemCar.setPublishedPriceAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getPublishedPriceAmt())).toPlainString());
        }

        if(null == bookingItemCar.getMarginMaxAmt())
        {
            bookingItemCar.setMarginMaxAmt(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO).toPlainString());
        }
        else
        {
            bookingItemCar.setMarginMaxAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getMarginMaxAmt())).toPlainString());
        }

        if(null == bookingItemCar.getMarginMinAmt())
        {
            bookingItemCar.setMarginMinAmt(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO).toPlainString());
        }
        else
        {
            bookingItemCar.setMarginMinAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getMarginMinAmt())).toPlainString());
        }


        if(null == bookingItemCar.getMileageChargeAmt())
        {
            bookingItemCar.setMileageChargeAmt(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO).toPlainString());
        }
        else
        {
            bookingItemCar.setMileageChargeAmt(CostPriceCalculator.toMoneyScale(new BigDecimal(bookingItemCar.getMileageChargeAmt())).toPlainString());
        }
    }
}
