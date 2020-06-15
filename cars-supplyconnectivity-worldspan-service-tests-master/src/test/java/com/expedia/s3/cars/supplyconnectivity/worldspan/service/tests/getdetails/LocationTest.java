package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.getdetails;

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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.GetDetailsBasicVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;

/**
 * Created by yyang4 on 11/3/2016.
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

    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass1000201WithOutactionIdTest() throws Exception {
        final String tuid = "1000201";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource =  DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest,carWorldspanSCSDatasource);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
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
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, randomGuid);
        System.out.println("detail request: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
        System.out.println("detail Response: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput,spooferTransport,testScenario,randomGuid,logger);
        GetDetailsBasicVerification.verifyCarLocationInfo(basicVerificationContext,getDetailsVerificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);


    }

    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass1000202WithLactionIdTest() throws Exception {
        final String tuid = "1000202";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource =  DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest,carWorldspanSCSDatasource);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));


        //2. Generate Details request with a random product from search Response
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
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
        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, randomGuid);
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput,spooferTransport,testScenario,randomGuid,logger);
        GetDetailsBasicVerification.verifyCarLocationInfo(basicVerificationContext,getDetailsVerificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);

    }



}
