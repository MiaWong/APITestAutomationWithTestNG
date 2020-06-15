package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.reserve;

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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.ReserveResponseBasicVerification;
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

    @Test(groups= {TestGroup.BOOKING_REGRESSION})
    public void cass1000401WithOutactionIdTest() throws Exception {
        final String tuid = "1000401";
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

        //2,reserve
        final SCSRequestGenerator requestGenerator4= new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
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
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, randomGuid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.reserveVerify(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        ReserveResponseBasicVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);


        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput= ExecutionHelper.cancel(httpClient, requestGenerator4, randomGuid);
        ExecutionHelper.cancelVerify(cancelVerificationInput,spooferTransport,testScenario,randomGuid,logger);

    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass4000102WithLactionIdTest() throws Exception {
        final String tuid = "4000102";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest,carWorldspanSCSDatasource);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //2,reserve
        final SCSRequestGenerator requestGenerator4= new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());
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
        final ReserveVerificationInput verificationInput = ExecutionHelper.reserve(httpClient, requestGenerator4, randomGuid);
        System.out.println("reserveRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveRequest)));
        System.out.println("reserveResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc,randomGuid,testScenario);
        System.out.println("spooferxml"+PojoXmlUtil.toString(spooferDoc));
        ExecutionHelper.reserveVerify(verificationInput,spooferTransport,testScenario,randomGuid,logger);
        ReserveResponseBasicVerification.verifyCarLocationInfo(basicVerificationContext,verificationInput.getResponse(),carWorldspanSCSDatasource,carsInventoryDatasource);



        //3,Cancel the reserve
        requestGenerator4.setReserveReq(reserveRequest);
        requestGenerator4.setReserveResp(verificationInput.getResponse());
        requestGenerator4.createCancelRequest();
        final CancelVerificationInput cancelVerificationInput= ExecutionHelper.cancel(httpClient, requestGenerator4, randomGuid);
        ExecutionHelper.cancelVerify(cancelVerificationInput,spooferTransport,testScenario,randomGuid,logger);

    }
}
