package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail;

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
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.GetCostAndAvailResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by yyang4 on 11/3/16.
 */
public class GetCostAndAvail extends SuiteContext {

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass6000105WithOutLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);

        TestData testData = new TestData(httpClient,CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000105", null);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        System.out.println("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        System.out.println("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        //2. Generate GetCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest  = requestGenerator3.createCostAndAvailRequest();
        final CarProductType productTypeCost = costAndAvailRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyTypeCost2 = productTypeCost.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyTypeCost.setCarVendorLocationID(null);
        startCarLocationKeyTypeCost2.setCarVendorLocationID(null);
        final CarLocationKeyType endCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyTypeCost2 = productTypeCost.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyTypeCost.setCarVendorLocationID(null);
        endCarLocationKeyTypeCost2.setCarVendorLocationID(null);
        final CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResponse = AmadeusSCSRequestSender.getSCSGetCostAndAvailabilityResponse(randomGuid, httpClient, costAndAvailRequest) ;
        System.out.println("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        System.out.println("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailResponse)));
        GetCostAndAvailResponseVerifier.isGetCostAndAvailWorksVerifier(costAndAvailResponse);
        GetCostAndAvailResponseVerifier.verifyCarLocationInfo(costAndAvailRequest,costAndAvailResponse);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass6000106WithLactionIdTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE
        );

        TestData testData = new TestData(httpClient,CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario(), "6000106", null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,carsAmadeusSCSDataSource);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        System.out.println("request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        System.out.println("response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);


        //2. Generate GetCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest  = requestGenerator3.createCostAndAvailRequest();
        final CarProductType productTypeCost = costAndAvailRequest.getCarProductList().getCarProduct().get(0);
        final CarLocationKeyType startCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startCarLocationKeyTypeCost2 = productTypeCost.getCarPickupLocation().getCarLocationKey();
        startCarLocationKeyTypeCost.setLocationCode("XXX");
        startCarLocationKeyTypeCost.setCarLocationCategoryCode("C");
        startCarLocationKeyTypeCost.setSupplierRawText("888");
        startCarLocationKeyTypeCost2.setLocationCode("XXX");
        startCarLocationKeyTypeCost2.setCarLocationCategoryCode("C");
        startCarLocationKeyTypeCost2.setSupplierRawText("888");
        final CarLocationKeyType endCarLocationKeyTypeCost = productTypeCost.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endCarLocationKeyTypeCost2 = productTypeCost.getCarDropOffLocation().getCarLocationKey();
        endCarLocationKeyTypeCost.setLocationCode("XXX");
        endCarLocationKeyTypeCost.setCarLocationCategoryCode("C");
        endCarLocationKeyTypeCost.setSupplierRawText("888");
        endCarLocationKeyTypeCost2.setLocationCode("XXX");
        endCarLocationKeyTypeCost2.setCarLocationCategoryCode("C");
        endCarLocationKeyTypeCost2.setSupplierRawText("888");
        final CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResponse = AmadeusSCSRequestSender.getSCSGetCostAndAvailabilityResponse(randomGuid, httpClient, costAndAvailRequest) ;
        GetCostAndAvailResponseVerifier.isGetCostAndAvailWorksVerifier(costAndAvailResponse);
        GetCostAndAvailResponseVerifier.verifyCarLocationInfo(costAndAvailRequest,costAndAvailResponse);
        System.out.println("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        System.out.println("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailResponse)));
    }

    /**
     *
     * Loyalty implementation check in Amadeus costandavail request
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void verifyLoyaltyInformationInCostAndAvailService()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException,
            SQLException
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


        //2. Generate GetCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, response);
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest  = requestGenerator3.createCostAndAvailRequest();
        final CarProductType productTypeCost = costAndAvailRequest.getCarProductList().getCarProduct().get(0);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        final CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResponse = AmadeusSCSRequestSender.getSCSGetCostAndAvailabilityResponse(randomGuid, httpClient, costAndAvailRequest) ;
        logger.info("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        logger.info("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailResponse)));

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);
        boolean enableLoyalty = configHelper.checkPosConfigFeatureEnable(testData.getScenarios(),"1","LoyaltyInformation/enable");

        GetCostAndAvailResponseVerifier.verifyLoyaltyInformation(costAndAvailRequest,costAndAvailResponse, enableLoyalty);
    }

}
