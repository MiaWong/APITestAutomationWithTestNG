package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.costavail;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.CostAndAvailResponseBasicVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;

/**
 * Created by yyang4 on 10/13/2016.
 */
public class LocationTest {
    //  future use for test live
    //  in case of using spoofer : randomized to pick a car product
    //  in case of using live site like INT, STT01 etc, we need to add car supplier ID/Car vehicle type etc filters
    private boolean isLive = false;

    public boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(boolean live) {
        isLive = live;
    }

    //  data members for common lib accessors.
    SpooferTransport spooferTransport;
    private HttpClient httpClient = new HttpClient(new SslContextFactory(true));
    Logger logger = Logger.getLogger(getClass());

    private DataSource carsInventoryDatasource;

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        carsInventoryDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass1000301WithOutactionIdTest() throws Exception {
        final String tuid = "1000301";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource =  DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest,carWorldspanSCSDatasource);

        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2. Generate getCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
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
        final GetCostAndAvailabilityVerificationInput verificationInput = ExecutionHelper.getCostAndAvailability(httpClient, requestGenerator3, randomGuid);
        System.out.println("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        System.out.println("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getCostAndAvailabilityVerification(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        CostAndAvailResponseBasicVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass1000302WithLactionIdTest() throws Exception {
        final String tuid = "1000302";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource =  DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest,carWorldspanSCSDatasource);

        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2. Generate getCostAndAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator3 = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
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
        final GetCostAndAvailabilityVerificationInput verificationInput = ExecutionHelper.getCostAndAvailability(httpClient, requestGenerator3, randomGuid);
        System.out.println("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
        System.out.println("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getCostAndAvailabilityVerification(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        CostAndAvailResponseBasicVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);
    }


}
