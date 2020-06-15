package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.basetypes.defn.v4.ProductCategoryCodeListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SuiteContext;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyBookingLogging;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging
        .VerifyCarReservationData;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn
        .v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItemCar;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.CarBookingDatasource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.GDSPCarType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.ResultFilter;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter
        .TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper.getCarInventoryDatasource;

/**
 * CASSS-5498 CarMIP booking logging
 * Created by fehu on 6/19/2017.
 */
@SuppressWarnings("PMD")
public class CarbsOMSBookingLogging extends SuiteContext
{
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281503AmadeusBookingLoggingCurrencyConversation() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.WeeklyExtDays, testScenario, "281503", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    //EP-51354174
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281500AmadeusBookingLoggingCDNoCurrencyConversation() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "281500", guid, false);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorSupplierID(14l);
        testdata.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
        final CarRate carRate = new CarRate();
        carRate.setCdCode("EP-51354174");
        testdata.setCarRate(carRate);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281508tfs1068365AmadeusBookingLoggingSpecialEquipmentEliteCar() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_LHR.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "281508", guid, false);
        testdata.setSpecialTest("SpecialEquipment");
        testdata.setSpecificCarCategoryCode(10l);
        testdata.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_3));
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    //Meichun: test case failed by carCorpDiscountCodeCRSReq is not logged for CD code in DB, it should be a bug to me
    //Ankit: disabling this test case for now because of above comment, will have to revisit the test case
    @Test(groups = {TestGroup.BOOKING_REGRESSION}, enabled = true)
    public void tfs474848AmadeusBookingLoggingCCCard() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_FRPos_FRLocation_Offairport_CCRequired");
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "474848", guid, false);
        testdata.setSpecialTest("CCCard");
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //EBL: enhanced booking logging - CarBS..CarReservationData log
    public void casss11091tfs459655AgencyStandaloneOMSBookingLoggingEBL() throws Exception {
      final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
      final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
      final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "100981", guid, false);
      TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
      testScenarioSpecialHandleParam.setVendorSupplierID(33l);
      testdata.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
      final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata,guid,null, false);

      VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

      VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
              carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }

    //200002
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs200002AgencyStandaloneOMSBookingOnewayCurrency() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MCO_LAX_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "200002", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata,guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss11092tfs281468GDSPStandaloneOMSBooking() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_GDSP_Standalone_nonFRLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.WeeklyDays5, testScenario, "11092", guid, false);
        final ResultFilter filter = new ResultFilter();
        filter.setCarType(GDSPCarType.GDSPCommission);
        testdata.setResultFilter(filter);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid, null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs200065AgencyStandaloneOMSBookingAirFlight() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Mounthly, testScenario, "200065", guid, false);
        testdata.setSpecialTest("AirFlight");
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata,guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs200067AgencyStandaloneOMSBookingFrequentFlyerNumber() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Mounthly, testScenario, "200067", guid, false);
        testdata.setNeedTravelerLoyalty(true);
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testdata, getCarInventoryDatasource(), true, "EP");
        ExecutionHelper.setCarRateOfTestData(testdata, true, "Air-123456", null);

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata,guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs200069AgencyStandaloneOMSBookingCarLoyalty() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Days3, testScenario, "200069", guid, false);
        testdata.setNeedLoyaltyCard(true);
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testdata, getCarInventoryDatasource(), true, "EP");
        ExecutionHelper.setCarRateOfTestData(testdata, true, "Car-123456", null);

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata,guid,null, false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281470OMSBookingLoggingGDSPNetRatesNoCurrencyConv() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "POSuGBP_Weekly");
        final TestScenario testScenario =  CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.WeeklyExtDays, testScenario, "281470", guid, false);
        final ResultFilter filter = new ResultFilter();
        filter.setCarType(GDSPCarType.GDSPNetRate);
        testdata.setResultFilter(filter);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs281460OMSBookingLoggingGDSPCommissionPackage() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "POSuGBP_Daily");
        final TestScenario testScenario =  CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Days4, testScenario, "281460", guid, false);
        final ResultFilter filter = new ResultFilter();
        filter.setCarType(GDSPCarType.GDSPCommission);
        testdata.setResultFilter(filter);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION}, enabled = false)
    public void casss11093MerchantStandaloneOMSBooking() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient));
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario(),
                "11093", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, false);

        final BookingItemCar bookingItemCar = getBookingItemCar(carbsOMReserveReqAndRespGenerator);

        if (!"0".equalsIgnoreCase(bookingItemCar.getIsCommissionable()) || !"0".equalsIgnoreCase(bookingItemCar.getIsMarkup())
                || !"Car".equalsIgnoreCase(bookingItemCar.getProductCategoryCodes()))
        {
            Assert.fail("IsCommissionable,IsMarkup,ProductCategoryCodes should be 0,0,car, actual is" + bookingItemCar.getIsCommissionable() +"," + bookingItemCar.getIsMarkup() +","
            + bookingItemCar.getProductCategoryCodes());
        }
    }


     //EBL: enhanced booking logging - CarBS..CarReservationData log
     @Test(groups = {TestGroup.BOOKING_REGRESSION})
     public void casss11094tfs281469tfs459663GDSPNetRatePackageOMSBookingEBL() throws Exception {
         final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
         final TestScenario testScenario =  CommonScenarios.Worldspan_US_GDSP_HCPackage_nonUSLocation_OnAirport.getTestScenario();
         final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "11094", guid, false);
         final ResultFilter filter = new ResultFilter();
         filter.setCarType(GDSPCarType.GDSPNetRate);
         testdata.setResultFilter(filter);
         final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,true);

         VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);

         VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                 carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());

     }


    @Test(groups = {TestGroup.BOOKING_REGRESSION}, enabled = false)
    public void casss11096MerchantPackageOMSBooking() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient));
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, CommonScenarios.Worldspan_UK_Merchant_Package_nonUKLocation_OnAirport.getTestScenario(),
                "11096", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,null, true);

        final  BookingItemCar bookingItemCar = getBookingItemCar(carbsOMReserveReqAndRespGenerator);

        if (!"0".equalsIgnoreCase(bookingItemCar.getIsCommissionable()) || !"0".equalsIgnoreCase(bookingItemCar.getIsMarkup())
                || !"Air,Car".equalsIgnoreCase(bookingItemCar.getProductCategoryCodes()))
        {
            Assert.fail("IsCommissionable,IsMarkup,ProductCategoryCodes should be 0,0,Car,Air" + bookingItemCar.getIsCommissionable() +"," + bookingItemCar.getIsMarkup() +","
                    + bookingItemCar.getProductCategoryCodes());
        }
    }

    private BookingItemCar getBookingItemCar(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) throws DataAccessException {
        final CarBookingDatasource carBookingDatasource = new CarBookingDatasource(DatasourceHelper.getCarsBookingDatasource());
        final int BookingItemID = carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getLegacyItineraryBookingDirectoryRow().getBookingItemID();
        return carBookingDatasource.getBookingItemCarByBookingItemID(BookingItemID);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
   public void casss11097MIPOMSBooking() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport,"CarMIP_booking");
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Daily, testScenario, "11097", guid, false);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorSupplierID(40l);
        testdata.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testdata, guid,"MIP", true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
   }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs458476tfs955634OMSBookingLoggingMNOnAirportPackage() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "MN_EUR");
        final TestScenario testScenario =  CommonScenarios.MN_GBR_Package_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.Days4, testScenario, "458476", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs459648OMSBookingLoggingMNOffairport() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario =  CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.WeeklyExtDays, testScenario, "459648", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1080134OMSBookingLoggingTitaniumCar() throws Exception {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestScenario testScenario =  CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        final TestData testdata = new TestData(httpClient, CommonEnumManager.TimeDuration.WeeklyExtDays, testScenario, "1080134", guid, false);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata,guid, null,false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }

    //on-airport round-trip standalone, US POS, US pickup location.
    //bug CASSS-11909
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidORSBookingLogging() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "1007601", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testData, testData
                .getGuid(), null, true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testData, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator
                        .getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }

    //On-airport, oneWay, posu
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidOOSBookingLogging() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport_oneway.getTestScenario(),
                "1007602", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testData, testData
                .getGuid(), null, true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testData, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator
                        .getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }

    //Off-airport, Roundtrip
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss10076HertzPrePaidOffRSBookingLogging() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.
                Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario(),
                "1007603", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setHertzPrepayTestCase(true);
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays5);

        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = testOMSBooking(testData, testData
                .getGuid(), null, true);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testData, spooferTransport);
        VerifyCarReservationData.verifyCarReservationData(carbsOMReserveReqAndRespGenerator
                        .getPreparePurchaseRequestType(),
                carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
    }

    private CarbsOMReserveReqAndRespGenerator testOMSBooking(TestData testdata,  String guid, String bookingType,
                                                             boolean sendStandaloneOrPublishSearchReq) throws Exception {
        //search
        final  CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testdata);
        final TestScenario scenarios= testdata.getScenarios();
        if(StringUtil.isNotBlank(bookingType) && "MIP".equalsIgnoreCase(bookingType))
        {
            request.getCarECommerceSearchStrategy().setPostPurchaseBoolean(true);
            ProductCategoryCodeListType productCategorys =  new ProductCategoryCodeListType();
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(productCategorys);
            List<String> productCategoryList = new ArrayList<>();
            productCategoryList.add("Car");
            productCategoryList.add("Air");
            productCategorys.setProductCategoryCode(productCategoryList);
            request.setOptimizationStrategyCode("1");
            request.setClientCode("1FX936");
            request.getCarSearchStrategy().setPackageBoolean(null);
            request.getCarECommerceSearchStrategy().setNeedReferencePricesBoolean(true);
            request.getCarECommerceSearchStrategy().setNeedPublishedBoolean(true);
            request.getCarECommerceSearchStrategy().setNeedMerchantBoolean(true);
            request.getCarECommerceSearchStrategy().setPurchaseTypeMask(null);

        }


        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        CarProductType selectCarProduct = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testdata, request, response);

        //Do standalone car search request if needed
        CarECommerceSearchResponseType standaloneSearchRsp = null;
        if(sendStandaloneOrPublishSearchReq)
        {
            request.getCarECommerceSearchStrategy().setNeedMerchantBoolean(true);
            request.getCarECommerceSearchStrategy().setNeedPublishedBoolean(true);

            // set purchase type mask to stand alone car search
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(null);
            request.getCarECommerceSearchStrategy().setPostPurchaseBoolean(null);
            request.getCarECommerceSearchStrategy().setPurchaseTypeMask(128l);

            //For standalone car reference price(prepaid with CD code), set CD code to null
            if(scenarios.getPurchaseType() == PurchaseType.CarOnly && request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria()
                    .get(0).getCarRate() != null)
            {
                //for standalone ref pricing, the reference criteria shouldn't contain a CD code
                request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria()
                        .get(0).getCarRate().setCorporateDiscountCode(null);
            }
            //when ss send seperate downstream request for package car and reference car, reference car request is with random guid
            standaloneSearchRsp = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        }

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testdata);
        carbsSearchRequestGenerator.setSelectedCarProduct(selectCarProduct);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testdata);

        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, scenarios, getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, scenarios, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());
        carOMSReqAndRespObj.setStandaloneSearchResponseType(standaloneSearchRsp);
        carOMSReqAndRespObj.setGetDetailsResponseType(getDetailsResponseType);

        //OMReserve
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testdata);

        if (!StatusCodeCategoryType.SUCCESS.equals(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getResponseStatus().getStatusCodeCategory())
        && null != carbsOMReserveReqAndRespGenerator.getRollbackPreparePurchaseResponseType())
        {
           Assert.fail("Booking failed , caused by  " + carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getResponseStatus().getStatusMessage());
        }
        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, testdata.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        return  carbsOMReserveReqAndRespGenerator;

    }

    //Booking.augmentReservationWithDetails/enable = 1
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1077530LoggingStandaloneTitaniuOnairport() throws Exception
    {
        testCarBSReserveLoggingAndPrice(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "1077530");
    }

    //Booking.augmentReservationWithDetails/enable = 1
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1077529LogginRountripTitaniumOffairport() throws Exception
    {
        testCarBSReserveLoggingAndPrice(CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(), "1077529");
    }

    public  void testCarBSReserveLoggingAndPrice(TestScenario scenarioName, String tuid) throws Exception
    {
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "TSCS_GBP");
        final TestData testdata = new TestData(httpClient,  scenarioName, tuid, guid);
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator =  testOMSBooking(testdata, guid, null,false);

        VerifyBookingLogging.verifyBookingLogging(carbsOMReserveReqAndRespGenerator, testdata, spooferTransport);
        List<String> errorMsg = new ArrayList<>();

        TotalPriceVerifier.verifyTotalPriceEqual(carbsOMReserveReqAndRespGenerator.getSelectCarProduct()
                ,carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                        .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(), scenarioName.getSupplierCurrencyCode(), errorMsg, false);
        TotalPriceVerifier.verifyTotalPriceEqual(carbsOMReserveReqAndRespGenerator.getSelectCarProduct()
                ,carbsOMReserveReqAndRespGenerator.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0), scenarioName.getSupplierCurrencyCode(), errorMsg, false);

    }
}
