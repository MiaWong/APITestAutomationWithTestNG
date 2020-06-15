package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.GetDetailsResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;

import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sql.DataSource;

/**
 * Created by yyang4 on 11/3/16.
 */
public class GetDetails extends SuiteContext {
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass6000103WithOutLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000103", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        System.out.println("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        System.out.println("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        final CarProductType productType = detailsRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyType2 = productType.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyType.setCarVendorLocationID(null);
        startCarLocationKeyType2.setCarVendorLocationID(null);
        final CarLocationKeyType endCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyType2 = productType.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyType.setCarVendorLocationID(null);
        endCarLocationKeyType2.setCarVendorLocationID(null);
        final CarSupplyConnectivityGetDetailsResponseType detailsResponse = AmadeusSCSRequestSender.getSCSDetailsResponse(randomGuid, httpClient, detailsRequest);
        System.out.println("detail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        System.out.println("detail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsResponse)));
        GetDetailsResponseVerifier.isGetDetailslWorksVerifier(detailsResponse);
        GetDetailsResponseVerifier.verifyCarLocationInfo(detailsResponse,SettingsProvider.CARAMADEUSSCSDATASOURCE);

    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass6000104WithLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000104", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        System.out.println("request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        System.out.println("response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        final CarProductType productType = detailsRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyType2 = productType.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyType.setLocationCode("XXX");
        startCarLocationKeyType.setCarLocationCategoryCode("C");
        startCarLocationKeyType.setSupplierRawText("888");
        startCarLocationKeyType2.setLocationCode("XXX");
        startCarLocationKeyType2.setCarLocationCategoryCode("C");
        startCarLocationKeyType2.setSupplierRawText("888");
        final CarLocationKeyType endCarLocationKeyType = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyType2 = productType.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyType.setLocationCode("XXX");
        endCarLocationKeyType.setCarLocationCategoryCode("C");
        endCarLocationKeyType.setSupplierRawText("888");
        endCarLocationKeyType2.setLocationCode("XXX");
        endCarLocationKeyType2.setCarLocationCategoryCode("C");
        endCarLocationKeyType2.setSupplierRawText("888");
        final CarSupplyConnectivityGetDetailsResponseType detailsResponse = AmadeusSCSRequestSender.getSCSDetailsResponse(randomGuid, httpClient, detailsRequest);
        System.out.println("detaiL request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        System.out.println("detail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsResponse)));
        GetDetailsResponseVerifier.isGetDetailslWorksVerifier(detailsResponse);
        GetDetailsResponseVerifier.verifyCarLocationInfo(detailsResponse,SettingsProvider.CARAMADEUSSCSDATASOURCE);

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass7443PassCarRateQualifierInAmadeusDetail() throws Exception
    {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "7443", ExecutionHelper.generateNewOrigGUID(spooferTransport));
        this.passCarRateQualifierInAmadeusDetail(testData);
    }

    public void passCarRateQualifierInAmadeusDetail(TestData parameters) throws Exception
    {
        final String invalidCarRateQualifierCode = "1111111111";
        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadues_Dynamic_daily_GB_POSU_GBP").build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //GetDetails and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadues_Dynamic_daily_GB_POSU_GBP").build(), parameters.getGuid());
        SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequestWithCarRateQualifier();
        if(!detailsRequest.getCarProductList().getCarProduct().isEmpty() && null != detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate())
        {
            detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCarRateQualifierCode(invalidCarRateQualifierCode);
        }
        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, parameters.getGuid());

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        GetDetailsVerificationHelper.getDetailsCarRateQualifierPassingVerification(getDetailsVerificationInput, spooferTransport, parameters, invalidCarRateQualifierCode, logger);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss11360ShuttleInformation() throws Exception
    {
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000101", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        logger.info("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        logger.info("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, testData.getGuid());
        logger.info("getdetail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        logger.info("getdetail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, testData.getScenarios(), testData.getGuid(), logger);

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);

        boolean enableShuttleInfo = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),"1","populateShuttleInfoFromGDS/enable");

        GetDetailsResponseVerifier.verifyShuttleInformation(getDetailsVerificationInput.getResponse(),enableShuttleInfo);

    }

    // Test case added for casss-11492
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss11492PhoneInformation() throws Exception
    {
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000101", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        logger.info("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        logger.info("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, testData.getGuid());
        logger.info("getdetail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        logger.info("getdetail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, testData.getScenarios(), testData.getGuid(), logger);

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);

        boolean mapPhoneCategoryCode = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),"1","mapPhoneCategoryCodeFromGDS/enable");

        GetDetailsResponseVerifier.verifyMapPhoneCategoryCode(getDetailsVerificationInput.getResponse(), mapPhoneCategoryCode);

    }


    /**
     *
     * Loyalty implementation check in Amadeus getdetails request
     *
     * @throws Exception
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void verifyLoyaltyInformationInGetDetailsService() throws Exception
    {
        TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(), "37233645", null);
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //1. Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "ACAQ_Amadeus_FR_Agency_Standalone_Loyalty_Code_Scenario").build(), "269d6dc8-4077-4224-bd37-562ceb5ce94f");

        CarRate carRateType = new CarRate();
        carRateType.setLoyaltyNum("1012");
        testData.setCarRate(carRateType);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        logger.info("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse("269d6dc8-4077-4224-bd37-562ceb5ce94f",httpClient,searchRequest);

        logger.info("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();

        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, testData.getGuid());
        logger.info("getdetail request xml: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        logger.info("getdetail response xml: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, testData.getScenarios(), "269d6dc8-4077-4224-bd37-562ceb5ce94f", logger);

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);

        boolean enableLoyalty = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),"1","LoyaltyInformation/enable");

        GetDetailsResponseVerifier.verifyLoyaltyInformation(getDetailsVerificationInput.getResponse(), enableLoyalty);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass11593ExpiredCarRateCodeInAmadeusDetail() throws Exception {
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000101", null);
        boolean useAvailRateCodeWhenPresentEnabled = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "1", "GetDetails.useAvailRateCodeWhenPresent/enable");

        //This test needs to run only when GetDetails.useAvailRateCodeWhenPresent flag is on, if the flag is off getDetails response won't be created as the RateCode had already expired.
        if (useAvailRateCodeWhenPresentEnabled) {

            final String expiredRateCode = "AAAASS";

            //Search and basic verify
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                    "Amadues_Invalid_RateCode_Scenario").build(), testData.getGuid());
            final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(testData, SettingsProvider.CARAMADEUSSCSDATASOURCE);

            SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, testData.getScenarios(), testData.getGuid(), logger);

            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

            //GetDetails and verifiers
            testData.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                    "Amadues_Invalid_RateCode_Scenario").build(), testData.getGuid());
            SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
            CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequestWithCarRateQualifier();

            //Set invalid rate code in request
            detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setRateCode(expiredRateCode);

            final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                    SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, testData.getGuid());

            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

            //Returned getDetails Rate code will be different as the requested RateCode had expired.
            GetDetailsResponseVerifier.verifyRateCode(getDetailsVerificationInput.getResponse(), detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getRateCode(), false);

        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass11593ValidCarRateCodeInAmadeusDetail() throws Exception {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000101", null);

        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadues_Invalid_RateCode_Scenario").build(), testData.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(testData, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, testData.getScenarios(), testData.getGuid(), logger);

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //GetDetails and verifiers
        testData.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                "Amadues_Invalid_RateCode_Scenario").build(), testData.getGuid());
        SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequestWithCarRateQualifier();

        final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, testData.getGuid());

        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        //Irrespective of GetDetails.useAvailRateCodeWhenPresent is true or false, RateCode in request and response should match if it was a valid rate code.
        GetDetailsResponseVerifier.verifyRateCode(getDetailsVerificationInput.getResponse(), detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().getRateCode(), true);

    }
}
