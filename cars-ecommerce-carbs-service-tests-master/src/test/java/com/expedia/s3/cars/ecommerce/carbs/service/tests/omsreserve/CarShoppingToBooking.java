package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.ErrorListType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.ServiceConfigs;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyShoppingToBooking;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyBooking;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyBookingItemCar;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.OmReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.StatusCodeCategoryType;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.util.List;

/**
 * Created by fehu on 11/6/2018.
 */
@SuppressWarnings("PMD")
public class CarShoppingToBooking extends SuiteCommon {
    final private ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());

    //Booking.referencePriceLog.overridePublishedPrice/enable on
    //Pricing.fixBaseRateFinancialApplicationCodeForAgency/enable on
    @Test(groups = {TestGroup.BOOKING_REGRESSION})//merge 1058589,1078932
    public void tfs1078932testCarBSOMSCorrectBaseRateInPriceList() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarBSOMSInPriceList(testData, null, true);
    }

    private void testCarBSOMSInPriceList(TestData testData, String testType, boolean needSendStanaloneSearch) throws Exception
    {
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        if ("MidNight".equals(testType))
        {
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMinDateTime(new DateTime("2019-04-07T00:00:00"));
            request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMaxDateTime(new DateTime("2019-04-07T00:00:00"));
        }
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        final CarProductType selectCarProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //send standalone search
        CarECommerceSearchResponseType standaloneSearchRsp = null;
        if (needSendStanaloneSearch)
        {
            request.getCarECommerceSearchStrategy().setNeedMerchantBoolean(true);
            request.getCarECommerceSearchStrategy().setNeedPublishedBoolean(true);
            // set purchase type mask to stand alone car search
            request.getCarECommerceSearchStrategy().setProductCategoryCodeList(null);
            request.getCarECommerceSearchStrategy().setPostPurchaseBoolean(null);
            request.getCarECommerceSearchStrategy().setPurchaseTypeMask(128l);
            //For standalone car reference price(prepaid with CD code), set CD code to null
            if (testData.getScenarios().getPurchaseType() == PurchaseType.CarOnly && request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCarRate() != null)
            {
                //for standalone ref pricing, the reference criteria shouldn't contain a CD code
                request.getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCarRate().setCorporateDiscountCode(null);
            }
            //when ss send seperate downstream request for package car and reference car, reference car request is with random guid
            standaloneSearchRsp = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), httpClient, request);
        }

        //getDetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);//details
        carbsSearchRequestGenerator.setSelectedCarProduct(selectCarProductType);
        final CarECommerceGetDetailsRequestType detailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType detailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), detailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), detailsRequestType, detailsResponseType);

        //cost&avail
        final CarECommerceGetCostAndAvailabilityRequestType costAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType costAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(), costAndAvailabilityRequestType, costAndAvailabilityResponse);


        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(costAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(selectCarProductType);
        carOMSReqAndRespObj.setStandaloneSearchResponseType(standaloneSearchRsp);

        if("diffrentTUID".equals(testType))
        {
            omsReserveForDifferentTUID(testData, carOMSReqAndRespObj);
            //cancel
            final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
            CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            return;
        }
        else
        {
            //OMReserve
            CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);
        }
        if ("MidNight".equals(testType))
        {  //cancel
            final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
            CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

            if (detailsResponseType.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarPickUpDateTime().getMinute() != 1 || carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey().getCarPickUpDateTime().getMinute() != 1)
            {
                Assert.fail("\r\nPickuptime in MidNight is not : 00:01:00 in GetDetail or PreParePurchase Response.");

            }

            //verify details total and preparepurchase total
            VerifyShoppingToBooking.totalVerify(detailsResponseType, carOMSReqAndRespObj);

        }
        else
        {

            VerifyBooking.verifyBooking(carOMSReqAndRespObj.getPreparePurchaseRequestType(), carOMSReqAndRespObj.getPreparePurchaseResponseType());

            VerifyBookingItemCar.verifyBookingItemCar(carOMSReqAndRespObj.getPreparePurchaseRequestType(), carOMSReqAndRespObj.getPreparePurchaseResponseType(), testData.getScenarios(), testData.getGuid(), carOMSReqAndRespObj.getStandaloneSearchResponseType(), testData.getSpooferTransport());


            //cancel
            final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
            CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

            final StringBuffer errorMsg = new StringBuffer();
            for (final CarProductType carProductType : response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct())
            {
                VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, carProductType, "search");
            }
            VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, detailsResponseType.getCarProductList().getCarProduct().get(0), "getDetails");
            VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, costAndAvailabilityResponse.getCarProductList().getCarProduct().get(0), "getCostAndAvail");
            VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, carOMSReqAndRespObj.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct(), "getOrderProcess");
            VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(), "PreparePurchase");
            VerifyShoppingToBooking.isBaseRateEqual(testData, errorMsg, omsCancelReqAndRespObj.getConfiguredProductDataType().getCarOfferData().getCarReservation().getCarProduct(), "PrepareChange");

        }

    }

    private void omsReserveForDifferentTUID(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj) throws Exception
    {
        CarbsOMReserveRequestSender.CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);


        //2.Create record
        CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);

        try
        {
            //	3.Build and send PreparePurchaseRequest request to CarBS (Associate Product with bookingID and insert related record into BookingDB).

            sendPreparepuchaseRequest(testData, carOMSReqAndRespObj);

            //	4.Build and send CommitPreparePurchaseRequest request to CarBS (Commit the booking).
            CarbsOMReserveRequestSender.CarBSCommitPreparePurchaseSend(testData, carOMSReqAndRespObj);

        } catch (Exception e)
        {
            //	5.Build and send RollBackPreparePurchaseRequest request to CarBS (Cancel the booking before commit book successfully).
            CarbsOMReserveRequestSender.CarBSRollbackPreparePurchaseSend(testData, carOMSReqAndRespObj);
            throw new Exception(e);
        }
    }

    private void sendPreparepuchaseRequest(TestData testData, CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj) throws Exception
    {
        PreparePurchaseRequest preparePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);
        preparePurchaseRequest.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().setUserID(
                preparePurchaseRequest.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getUserID() + 10);
        preparePurchaseRequest.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getSiteKey().setSiteID(
                preparePurchaseRequest.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getSiteKey().getSiteID() + 10);
        PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), preparePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseRequestType(preparePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseResponseType(preparePurchaseResponse);
        OmReserveVerifier.isPreparePurchaseWorksVerifier(testData.getGuid(),testData.getScenarios(),preparePurchaseRequest,preparePurchaseResponse);
        if (testData.isRegression()) {
            OmReserveVerifier.preparePurchaseRegressionVerifier(testData, carOMSReqAndRespObj);
        }
    }


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1078747testCarBSOMSPickupDateTimeIsMidNight() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        testCarBSOMSInPriceList(testData, "MidNight", false);
    }

    //Book & Cancel with different TUID in PointOfSaleLogonUserIdentifier & PointOfSaleCustomerIdentifier
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1075859OMSAgentIndicatorSupport() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        testCarBSOMSInPriceList(testData, "diffrentTUID", false);
    }

    //User story 1069583 supporting Expanded SIPP on BEX
   //Verify Booking/Retrieved/Cancelling Extended CarCategory car on US POS & Emain site returned in CarBS Search response
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1072462CarBSSearchOMSWorldspanSurfaceEliteCar() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1029333", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"USAgencyStandaloneLatLong"), spooferTransport);
        testData.setClientCode("W0DFCJ");
        testCarOMSCancelSurfaceElite(testData);
    }


    private void testCarOMSCancelSurfaceElite(TestData testData)throws Exception
    {
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);

        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        CarProductType selectCarProductType = null;

        //cost&avail
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);//details
        List<CarProductType> carProductTypeList = carbsSearchRequestGenerator.selectCarListByBusinessModelAndServiceProviderIDFromCarSearchResultList(response.getCarSearchResultList(), testData);
           for(CarProductType carProductType : carProductTypeList)
           {
               if(carProductType.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode() >9)
               {
                   selectCarProductType = carProductType;
                   carbsSearchRequestGenerator.setSelectedCarProduct(selectCarProductType);
               }
           }

        final CarECommerceGetCostAndAvailabilityRequestType costAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType costAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(), costAndAvailabilityRequestType, costAndAvailabilityResponse);


        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(costAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(selectCarProductType);

       //OMReserve
        CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

       //cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());



    }

    //1063602 - Pricing.removeMiscForGDSP/enable  on - verify taxes and fees are removed from PriceList for GDSP merchant car when they exist in costList - US site
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1063602RemoveGDSPMiscOn() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario(), "1063602", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        testData.setClientCode("1FX936");
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZI");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);
        testRemoveGDSPMiscAndProperHandleOfAirlineCode(testData);
    }

    private void testRemoveGDSPMiscAndProperHandleOfAirlineCode(TestData testData)throws Exception
    {
        CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        CarProductType selectCarProduct = carbsOMReserveReqAndRespGenerator.getSelectCarProduct();
        CarECommerceGetDetailsResponseType carECommerceGetDetailsResponseType = carbsOMReserveReqAndRespGenerator.getGetDetailsResponseType();
        PreparePurchaseResponseType preparePurchaseResponseType = carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType();
       GetOrderProcessResponseType getOrderProcessResponseType = carbsOMReserveReqAndRespGenerator.getGetOrderProcessResponseType();
       CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponseType = carbsOMReserveReqAndRespGenerator.getGetCostAndAvailabilityResponseType();
        //cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //verify
        StringBuilder errorMsg = new StringBuilder();

        if(StringUtil.isNotBlank(testData.getSpecialTest()) && testData.getSpecialTest().contains("ProperHandleOfAirlineCode"))
        {
            //Verify service return correct PointOfSaleToPointOfSupplyExchangeRate in CEAR/COPP/COGO response
            VerifyShoppingToBooking.pointOfSaleToPointOfSupplyExchangeRateVerify(preparePurchaseResponseType, getOrderProcessResponseType, getCostAndAvailabilityResponseType, errorMsg);

            String errorList  = preparePurchaseResponseType.getResponseStatus().getSupplyErrorList().getSupplyError().get(0).getErrorDescription();
           if(!"Invalid AirCarrierCode" .contains(errorList))
           {
             errorMsg.append("The actual error message does not contain the expected value when the error type is FieldInvalidError.");
           }
        }
        else
        {
            VerifyShoppingToBooking.verifyForMisc(errorMsg, testData, selectCarProduct, carECommerceGetDetailsResponseType, preparePurchaseResponseType);
        }
        if(StringUtil.isNotBlank(String.valueOf(errorMsg)))
         {
             Assert.fail(errorMsg.toString());
         }


    }

    //Booking successful after retry for request with AirCarrierCode contains "-" and "/", OMS, UK site, WSCS, Standalone, offairport
    //merge 1029154
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1033010ProperHandleOfAirlineCode() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "1033010", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        testData.setSpecialTest("ProperHandleOfAirlineCode|-/|321");
        testRemoveGDSPMiscAndProperHandleOfAirlineCode(testData);
    }

    //Coupon code map when CarBehaviorAttribute=21and CarBehaviorAttributeValue =1  for Hertz Agency Car
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1016884OMSSupportCouponCodeMappingAgencyHertz() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1016884", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZE");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        CarRate carRate = new CarRate();
        carRate.setPromoCode("202930");
        testData.setCarRate(carRate);
        testOMSSupportCouponCodeMapping(testData);

    }

    //1014760 - Verify TDIS is taken as MandatoryCharge in priceList but logged as DiscountAmt in BookingItemCar table when Pricing.surfaceDiscount/enable=0 - currency conversation
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1014760CouponSupportFeatureOffCurrencyExchange() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1014760", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZE");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        CarRate carRate = new CarRate();
        carRate.setPromoCode("2034198");
        testData.setCarRate(carRate);

        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testData.setClientCode("QGPDJ8");

        if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.PRICING_SURFACEDISCOUNT, 2, "0"))
         {
             Assert.fail("The clientConfig Pricing.surfaceDiscount/enable is not expect 0 for client ID 2");
         }

        testCouponSupport(testData, "0");
    }

    //1014762 - Verify TDIS is taken as Discount in priceList and logged as DiscountAmt in BookingItemCar table when Pricing.surfaceDiscount/enable=0 - currency conversation
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1014762CouponSupportFeatureOnCurrencyExchange() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1014762", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZE");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        CarRate carRate = new CarRate();
        carRate.setPromoCode("2034198");
        testData.setCarRate(carRate);

        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testData.setClientCode("1FX936");

        if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.PRICING_SURFACEDISCOUNT, 1, "1"))
        {
            Assert.fail("The clientConfig Pricing.surfaceDiscount/enable is not expect 1 for client ID 1");
        }

        testCouponSupport(testData, "1");
    }

    //1017207 - Verify TDIS is taken as Discount in priceList and logged as DiscountAmt in BookingItemCar table when Pricing.surfaceDiscount/enable=1 - currency conversation - Misc exist in VRD
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1017207CouponSupportFeatureOnCurrencyExchangeMiscExist() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1017207", PojoXmlUtil.generateNewOrigGUID(spooferTransport), spooferTransport);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZE");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        CarRate carRate = new CarRate();
        carRate.setPromoCode("202930");
        testData.setCarRate(carRate);

        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testData.setClientCode("1FX936");

        if(!clientConfigHelper.checkClientConfig(SettingsProvider.ENVIRONMENT_NAME, ServiceConfigs.PRICING_SURFACEDISCOUNT, 1, "1"))
        {
            Assert.fail("The clientConfig Pricing.surfaceDiscount/enable is not expect 1 for client ID 1");
        }

        testCouponSupport(testData, "1");
    }

    private void testOMSSupportCouponCodeMapping(TestData testData)throws Exception
    {
        CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        CarECommerceSearchResponseType carECommerceSearchResponseType = carbsOMReserveReqAndRespGenerator.getSearchResponseType();
        CarECommerceGetDetailsResponseType carECommerceGetDetailsResponseType = carbsOMReserveReqAndRespGenerator.getGetDetailsResponseType();
        PreparePurchaseResponseType preparePurchaseResponseType = carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType();
        //cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //verify
        StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        VerifyShoppingToBooking.checkCouponCodeSearch(errorMsg, spooferDoc, testData, carECommerceSearchResponseType.getCarSearchResultList().getCarSearchResult());

        VerifyShoppingToBooking.checkCouponCodeGetDetails(errorMsg, spooferDoc, testData, carECommerceGetDetailsResponseType.getCarProductList()
                .getCarProduct().get(0).getCarInventoryKey().getCarRate().getPromoCode());

        VerifyShoppingToBooking.checkCouponCodePreparePurchase(errorMsg, spooferDoc, testData, preparePurchaseResponseType);

        if(StringUtil.isNotBlank(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    private void testCouponSupport(TestData testData, String expectFlag)throws Exception
    {
        CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //verify
        StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc = testData.getSpooferTransport().retrieveRecords(testData.getGuid());

        //Verify detail pricing
        VerifyShoppingToBooking.discountInCostPriceListVerifier(spooferDoc, carbsOMReserveReqAndRespGenerator.getGetDetailsRequestType(), carbsOMReserveReqAndRespGenerator.getGetDetailsResponseType(), expectFlag , errorMsg);

        //Verify booking logging
       // string augmentDetailFlag = ServiceConfigUtil.getCarBSClientConfig(ClientConfigurationSettingName.Booking_augmentReservationWithDetails_enable, clientID);
        //CarECommerceGetDetailsResponse detailToLog = (augmentDetailFlag == "1") ? carBSShopingReqAndRespObj.carBSGetDetailsResponse : null;
       //---- augmentDetailFlag default 1
        VerifyShoppingToBooking.isBookingDataCorrectVerifier(testData, carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType(),carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType()
        , carbsOMReserveReqAndRespGenerator.getSearchResponseType());

        //Verify EBL
        //If mergeDetail flag is on, use the priceList in preparePurchase response - EstimatedTaxAndFees instead of Misc in Details response
       // string mergeDetailFlag = ServiceConfigUtil.getCarBSClientConfig(ClientConfigurationSettingName.Booking_mergeDetailsInResponseReservation, clientID);
       // if (mergeDetailFlag == "1" && detailToLog != null) detailToLog.CarProductList[0].PriceList = carOMSReqAndRespObj.preparePurchaseResponse.PreparedItems.BookedItemList[0].ItemData.CarOfferData.CarReservation.CarProduct.PriceList;
        VerifyShoppingToBooking.eblVerifierOMS(testData, errorMsg, carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());

        //cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        if(StringUtil.isNotBlank(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    //Verify OMS return correct error when loyalty number name mismatch for Reserve with loyalty number but request have the price not applied with loyalty number
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs1012662CouponSupportFeatureOnCurrencyExchangeMiscExist() throws Exception
    {
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final TestData testData = new TestData(httpClient, CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "1012662"
                , PojoXmlUtil.generateNewOrigGUID(spooferTransport, "loyaltyMismatch"), spooferTransport);
        TestScenarioSpecialHandleParam testScenarioSpecialHandleParam = new TestScenarioSpecialHandleParam();
        testScenarioSpecialHandleParam.setVendorCode("ZE");
        testData.setTestScenarioSpecialHandleParam(testScenarioSpecialHandleParam);

        CarRate carRate = new CarRate();
        carRate.setLoyaltyNum("51442848");
        testData.setCarRate(carRate);

        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testData.setClientCode("1FX936");

        testLoyaltyNumberOMSMessage(testData);
    }

    private void testLoyaltyNumberOMSMessage(TestData testData)throws Exception
    {
        CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //verify
        StringBuilder errorMsg = new StringBuilder();

        if (!carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getResponseStatus().getStatusCodeCategory().equals(StatusCodeCategoryType.SUCCESS))
        {
            errorMsg.append("PreparePurchase response status should be success");
        }

        ErrorListType errorList_reserve = null;
        if (carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getErrorList() != null)
        {
            errorList_reserve = carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getErrorList();
        }
        if (null != errorList_reserve && CollectionUtils.isEmpty(errorList_reserve.getError()))
        {
            errorMsg.append("There is no CarLoyaltyNumberNotAppliedError error return in the ErrorList in PreparePurchase response!");
        }
        else if (null != errorList_reserve && !errorList_reserve.getError().get(0).getErrorCategoryCode().equals("CarLoyaltyNumberNotAppliedError"))
        {
            errorMsg.append("ErrorCategoryCode return in PreparePurchase response not return as expected! Expected/Actual: CarLoyaltyNumberNotAppliedError/" + errorList_reserve.getError().get(0).getErrorCategoryCode() + "/n");
        }
        else if (null != errorList_reserve && !errorList_reserve.getError().get(0).getDescriptionRawText().equalsIgnoreCase("The car loyalty number was not applied because the name associated with the loyalty number does not match the booking name."))
        {
            errorMsg.append("DescriptionRawText return in PreparePurchase response not return as expected! Expected/Actual: The car loyalty number was not applied because the name associated with the loyalty number does not match the booking name./" + errorList_reserve.getError().get(0).getDescriptionRawText() + "/n");
        }
        //cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        if (StringUtil.isNotBlank(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }


}
