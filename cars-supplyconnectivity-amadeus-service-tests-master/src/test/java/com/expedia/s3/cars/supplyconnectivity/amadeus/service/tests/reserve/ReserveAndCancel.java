package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities.CostAndAvailExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities.CostAndAvailVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.CancelResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.ReserveVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by yyang4 on 11/3/16.
 */
public class ReserveAndCancel extends SuiteContext {

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void cass6000107WithOutLactionIdTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000107", null);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        logger.info("search request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid, httpClient, searchRequest);
        logger.info("search response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        //4,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        final CarProductType productTypeReserve = reserveRequest.getCarProduct();
        final CarLocationKeyType startCarLocationKeyTypeReserve = productTypeReserve.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyTypeReserve2 = productTypeReserve.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyTypeReserve.setCarVendorLocationID(null);
        startCarLocationKeyTypeReserve2.setCarVendorLocationID(null);
        final CarLocationKeyType endCarLocationKeyTypeReserve = productTypeReserve.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyTypeReserve2 = productTypeReserve.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyTypeReserve.setCarVendorLocationID(null);
        endCarLocationKeyTypeReserve2.setCarVendorLocationID(null);
        final CarSupplyConnectivityReserveResponseType reserveResponse = AmadeusSCSRequestSender.getSCSReserveResponse(randomGuid, httpClient, reserveRequest);
        logger.info("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        logger.info("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveResponse)));
        ReserveVerifier.isReserveWorksVerifier(reserveResponse);
        ReserveVerifier.verifyCarLocationInfo(reserveResponse, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //5,Cancel the booking
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(reserveResponse);
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator4.createCancelRequest();
        final CarSupplyConnectivityCancelResponseType cancelResponse = AmadeusSCSRequestSender.getSCSCancelResponse(randomGuid, httpClient, cancelRequestType);
        CancelResponseVerifier.isCancelWorksVerifier(cancelResponse);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void cass6000108WithLactionIdTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000108", null);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        logger.info("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid, httpClient, searchRequest);
        logger.info("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        //4,reserve
        final SCSRequestGenerator requestGenerator4 = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator4.createReserveRequest();
        final CarProductType productTypeReserve = reserveRequest.getCarProduct();
        final CarLocationKeyType startCarLocationKeyTypeReserve = productTypeReserve.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyTypeReserve2 = productTypeReserve.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyTypeReserve.setLocationCode("XXX");
        startCarLocationKeyTypeReserve.setCarLocationCategoryCode("C");
        startCarLocationKeyTypeReserve.setSupplierRawText("888");
        startCarLocationKeyTypeReserve2.setLocationCode("XXX");
        startCarLocationKeyTypeReserve2.setCarLocationCategoryCode("C");
        startCarLocationKeyTypeReserve2.setSupplierRawText("888");
        final CarLocationKeyType endCarLocationKeyTypeReserve = productTypeReserve.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyTypeReserve2 = productTypeReserve.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyTypeReserve.setLocationCode("XXX");
        endCarLocationKeyTypeReserve.setCarLocationCategoryCode("C");
        endCarLocationKeyTypeReserve.setSupplierRawText("888");
        endCarLocationKeyTypeReserve2.setLocationCode("XXX");
        endCarLocationKeyTypeReserve2.setCarLocationCategoryCode("C");
        endCarLocationKeyTypeReserve2.setSupplierRawText("888");
        final CarSupplyConnectivityReserveResponseType reserveResponse = AmadeusSCSRequestSender.getSCSReserveResponse(randomGuid, httpClient, reserveRequest);
        logger.info("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        logger.info("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveResponse)));
        ReserveVerifier.isReserveWorksVerifier(reserveResponse);
        ReserveVerifier.verifyCarLocationInfo(reserveResponse, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //5,Cancel the booking
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(reserveResponse);
        final CarSupplyConnectivityCancelRequestType cancelRequestType = requestGenerator4.createCancelRequest();
        final CarSupplyConnectivityCancelResponseType cancelResponse = AmadeusSCSRequestSender.getSCSCancelResponse(randomGuid, httpClient, cancelRequestType);
        CancelResponseVerifier.isCancelWorksVerifier(cancelResponse);

    }

    /**
     * Amadues SCS Reserve - Verify CD code(Shopping/Booking) send to GDS request.
     * <p>
     * Shopping and Booking Request with single CD Code, with SupplySubsetID configured CD code.
     * <p>
     * Verify CD code in Request send to GDS request.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs7027SingleCDCodeInRequest() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "702701", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);
        amadeusSCSCDCodeSendToGDSRquest(parameters, "Amadeus_GBR_Standalone_RoundTrip_OffAirport_VLC_MultiEPCDCode_190976");
    }

    //TODO Mia : should remove these function after test case migrate, all 7027 cases, should integrate this logic to logic, cd code is normal logic.

    /**
     * Amadues SCS Reserve - Verify CD code(Shopping/Booking) send to GDS request.
     * <p>
     * Shopping and Booking Request with multiple CD Code, with SupplySubsetID configured CD code.
     * <p>
     * Verify CD code in Request send to GDS request.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs7027MultipleCDCodeInRequest() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "702702", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-40823215");
        parameters.setCarRate(cdCode);
        amadeusSCSCDCodeSendToGDSRquest(parameters, "Amadeus_GBR_Standalone_RoundTrip_OffAirport_VLC_MultiEPCDCode_190976");
    }

    /**
     * Amadues SCS Reserve - Verify CD code(Shopping/Booking) send to GDS request.
     * <p>
     * Shopping and Booking Request without CD Code, with SupplySubsetID configured CD code.
     * <p>
     * Verify CD code in database send to GDS request.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs7027NoCDCodeInRequest() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "702703", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        amadeusSCSCDCodeSendToGDSRquest(parameters, "Amadeus_GBR_Standalone_RoundTrip_OffAirport_VLC_MultiEPCDCode_190976");
    }

    /**
     * Amadues SCS Reserve - Verify CD code(Shopping/Booking) send to GDS request.
     * <p>
     * Shopping and Booking Request without CD Code, with SupplySubsetID do not configured CD code.
     * <p>
     * Verify no CD code send to GDS request.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs7027NoCDCode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "702704", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        amadeusSCSCDCodeSendToGDSRquest(parameters, "Amadeus_GBR_Standalone_RoundTrip_OffAirport_VLC_MultiEPCDCode_190976");
    }

    /**
     * Amadues SCS Reserve - Verify CD code(Shopping/Booking) send to GDS request.
     * <p>
     * Shopping and Booking Request with CD Code, with SupplySubsetID do not configured CD code.
     * <p>
     * Verify CD code in request send to GDS request.
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs7027CDCodeInGDSRequest() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "702705", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);
        amadeusSCSCDCodeSendToGDSRquest(parameters, "Amadeus_GBR_Standalone_RoundTrip_OffAirport_VLC_MultiEPCDCode_190976");
    }

    /**
     * <ConfigEntryList updatedBy="Cars_Release" flushAll="1">
         <ConfigEntry environment="stt01" settingName="Reserve.suppressSpecialEquipment/enable" value="0"/>
         <ConfigEntry environment="stt01" settingName="Reserve.specialEquipmentWhitelist/enable" value="0"/>
         <ConfigEntry environment="stt01" settingName="Reserve.specialEquipmentWhitelist/list" value=""/>
     </ConfigEntryList>

     * @param parameters
     * @param spooferTemplateScenarioName
     * @throws Exception
     */
    private void amadeusSCSCDCodeSendToGDSRquest(TestData parameters, String spooferTemplateScenarioName)
            throws Exception {
        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName+"_search").build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        SearchVerificationHelper.searchRequestGDSMsgMappingVerification(searchVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);

        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        //if have EP car select EP car to verify cd code logic.
        for(CarSearchResultType searchResult : scsRequestGenerator.getSearchResp().getCarSearchResultList().getCarSearchResult())
        {
            if (null != searchResult.getCarProductList() && null != searchResult.getCarProductList().getCarProduct()
                    && !searchResult.getCarProductList().getCarProduct().isEmpty())
            {
                for (CarProductType car : searchResult.getCarProductList().getCarProduct())
                {
                    if (car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID() == 14)
                    {
                        scsRequestGenerator.setSelectedCarProduct(car);
                    }
                }
            }
        }

        //GetDetails and basic verify
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsExecutionHelper.getDetails(parameters, scsRequestGenerator);
        logger.info("getDetails request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("getDetails response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        GetDetailsVerificationHelper.getDetailsRequestGDSMsgMappingVerification(getDetailsVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);

        //CostAndAvail
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = CostAndAvailExecutionHelper.getCostAndAvailability(parameters, scsRequestGenerator);
        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityVerificationInput.getResponse())));

        CostAndAvailVerificationHelper.getCostAndAvailBasicVerification(getCostAndAvailabilityVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        CostAndAvailVerificationHelper.getCostAndAvailRequestGDSMsgMappingVerification(getCostAndAvailabilityVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);

        //Reserve and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final ReserveVerificationInput reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));
        ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        ReserveVerificationHelper.reserveRequestGDSMsgMappingVerification(reserveVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);

        scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());

//        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationExecutionHelper.retrieveReservation(parameters, scsRequestGenerator);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));

        GetReservationVerificationHelper.getReservationBasicVerification(getReservationVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
//        GetReservationVerificationHelper.getReservationRequestGDSMsgMappingVerification(getReservationVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);

        //cancel and basic verify
        if (null != reserveVerificationInput.getResponse().getCarReservation() && null != reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode())
        {
            if (reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode())
                    || reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode()))
            {
                CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);
                CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
            }
        }
    }
}