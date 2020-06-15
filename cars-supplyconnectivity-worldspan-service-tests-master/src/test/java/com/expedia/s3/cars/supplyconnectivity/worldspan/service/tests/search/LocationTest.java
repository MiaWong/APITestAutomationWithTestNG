package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.SpecialTestCasesParam;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.SearchResponsesBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapSearchVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 11/1/2016.
 */
public class LocationTest {

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
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass1000101LocationTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        /*OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is VALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "1000101";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        //filter invalid locationId
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest, carWorldspanSCSDatasource);
        //set location code to invalid
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            startCarLocationKeyType.setLocationCode("XXX");
            startCarLocationKeyType.setCarLocationCategoryCode("C");
            startCarLocationKeyType.setSupplierRawText("888");
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            endCarLocationKeyType.setLocationCode("XXX");
            endCarLocationKeyType.setCarLocationCategoryCode("C");
            endCarLocationKeyType.setSupplierRawText("888");
        }

        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",carWorldspanSCSDatasource,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",carWorldspanSCSDatasource,"1","0");

    }

    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass1000102LocationTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        /*OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        VALID location code/category/supplierRawText*/
        final String tuid = "1000102";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        //filter invalid locationId
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest, carWorldspanSCSDatasource);
        //set CarLocationID to NULL or INVALID
        long count = 0;
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            count++;
            if (count % 2 == 0) {
                //set invalid CarVendorLocationID
                startCarLocationKeyType.setCarVendorLocationID(123456L);
                endCarLocationKeyType.setCarVendorLocationID(123456L);
            } else {
                //set null CarVendorLocationID
                startCarLocationKeyType.setCarVendorLocationID(null);
                endCarLocationKeyType.setCarVendorLocationID(null);
            }
        }
        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",carWorldspanSCSDatasource,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",carWorldspanSCSDatasource,"2","0");

    }


    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass1000103LocationTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        /*AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL
        VALID Iata code*/
        final String tuid = "1000103";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",carWorldspanSCSDatasource,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",carWorldspanSCSDatasource,"1","0");
    }

    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass1000104LocationTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
       /*OFF_AIRPORT_SEARCH two search criteria
        Search.FilterInvalidLocationCode/enable=0
        first search criteria has CarLocationID is NULL or INVALID
        and INVALID location code/category/supplierRawText
        second search criteria has VALID CarLocationID
        or has VALID location code/category/supplierRawText*/
        final String tuid = "1000104";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        //filter invalid carVendorLocationId
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest, carWorldspanSCSDatasource);
        //set first search criteria has CarLocationId to null or invalid and location code/category/supplierRawTest to invalid,set sencond search criteria has valid location code/categroy/supplierRawText
        List<CarSearchCriteriaType> searchCriteriaTypeList = new ArrayList<CarSearchCriteriaType>();
        long count = 0;
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            count++;
            if (count == 1) {
                //set  first search criteria has CarLocationID is NULL or INVALID and INVALID location code/category/supplierRawText
                startCarLocationKeyType.setCarVendorLocationID(null);
                startCarLocationKeyType.setLocationCode("XXX");
                startCarLocationKeyType.setCarLocationCategoryCode("C");
                startCarLocationKeyType.setSupplierRawText("888");
                endCarLocationKeyType.setCarVendorLocationID(null);
                endCarLocationKeyType.setLocationCode("XXX");
                endCarLocationKeyType.setCarLocationCategoryCode("C");
                endCarLocationKeyType.setSupplierRawText("888");
                searchCriteriaTypeList.add(criteriaType);
            } else if(count == 2){
                //set second search criteria has VALID CarLocationID or has VALID location code/category/supplierRawText
                startCarLocationKeyType.setLocationCode("XXX");
                startCarLocationKeyType.setCarLocationCategoryCode("C");
                startCarLocationKeyType.setSupplierRawText("888");
                endCarLocationKeyType.setLocationCode("XXX");
                endCarLocationKeyType.setCarLocationCategoryCode("C");
                endCarLocationKeyType.setSupplierRawText("888");
                searchCriteriaTypeList.add(criteriaType);
            }
        }
        searchRequest.getCarSearchCriteriaList().setCarSearchCriteria(searchCriteriaTypeList);
        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",carWorldspanSCSDatasource,"1","1");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",carWorldspanSCSDatasource,"1","0");
    }

    //CASSS-3159 SCS should filter out locations that CarNect/Worldspan/Amadeus does not recognize
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass1000105LocationTest() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
       /* OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "1000105";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        //filter invalid carLocationId
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carWorldspanSCSDatasource = DataSourceHelper.getWSCSDataSourse();
        PropertyResetHelper.filterReqSearchList(searchRequest, carWorldspanSCSDatasource);
        //set CarLocationId to null or invalid number and set location code/category/supplierRawText to invalid
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            startCarLocationKeyType.setCarVendorLocationID(null);
            startCarLocationKeyType.setLocationCode("XXX");
            startCarLocationKeyType.setCarLocationCategoryCode("C");
            startCarLocationKeyType.setSupplierRawText("888");
            endCarLocationKeyType.setCarVendorLocationID(null);
            endCarLocationKeyType.setLocationCode("XXX");
            endCarLocationKeyType.setCarLocationCategoryCode("C");
            endCarLocationKeyType.setSupplierRawText("888");
        }

        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",carWorldspanSCSDatasource,"1","1");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",carWorldspanSCSDatasource,"2","0");

    }

    //verifyType 1 : have GDS request send out ,2: no GDS request send out
    public void locationTestCommon(CarSupplyConnectivitySearchRequestType searchRequest, TestScenario testScenario, String tuid, String posSettingValue, DataSource scsDataSource, String verifyType, String ignoreFlag) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,DataAccessException{
        //set feature disable
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.SEARCH_FILTERINVALIDLOCATIONCODE_ENABLE);
        posConfig.setSettingValue(posSettingValue);
        ConfigSetUtil.posConfigSet(posConfig,null,httpClient,tuid, SettingsProvider.SERVICE_ADDRESS,true);
        //send search request
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        //verify
        if("1".equals(verifyType)) {
            final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
            final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
            System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
            ExecutionHelper.searchVerification(searchVerificationInput, spooferTransport, testScenario, randomGuid, logger);
            SearchResponsesBasicVerification.verifyCarLocationInfo(basicVerificationContext, searchVerificationInput.getResponse(), scsDataSource, carsInventoryDatasource, ignoreFlag,testScenario);
        }else if("2".equals(verifyType)){
            SearchResponsesBasicVerification.verifyExistsResponseError(searchVerificationInput);
        }

    }
}
