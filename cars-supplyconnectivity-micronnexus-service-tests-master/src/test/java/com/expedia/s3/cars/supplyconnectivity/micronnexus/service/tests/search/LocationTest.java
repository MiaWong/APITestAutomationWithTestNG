package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

/**
 * Created by v-mechen on 8/16/2016.
 */

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.search.SearchVerification;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class LocationTest extends SuiteCommon
{
    final Logger logger = Logger.getLogger(getClass());

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000101LocationTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
       /* OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is VALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "3000101";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
       PropertyResetHelper.filterReqSearchList(searchRequest,SettingsProvider.CARMNSCSDATASOURCE);
        for(CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()){
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
        locationTestCommon(searchRequest,testScenario,tuid,"0",SettingsProvider.CARMNSCSDATASOURCE,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",SettingsProvider.CARMNSCSDATASOURCE,"1","0");
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000102LocationTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
        /*OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        VALID location code/category/supplierRawText*/
        final String tuid = "3000102";
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        PropertyResetHelper.filterReqSearchList(searchRequest, SettingsProvider.CARMNSCSDATASOURCE);
        long count =0;
        for(CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()){
            count++;
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            if(count % 2 ==0) {
                startCarLocationKeyType.setCarVendorLocationID(null);
                endCarLocationKeyType.setCarVendorLocationID(null);
            }else{
                startCarLocationKeyType.setCarVendorLocationID(123456L);
                endCarLocationKeyType.setCarVendorLocationID(123456L);
            }
        }
        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",SettingsProvider.CARMNSCSDATASOURCE,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",SettingsProvider.CARMNSCSDATASOURCE,"2","0");
       }


    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000103LocationTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
         /* OFF_AIRPORT_SEARCH two search criteria
        Search.FilterInvalidLocationCode/enable=0
        first search criteria has CarLocationID is NULL or INVALID and INVALID location code/category/supplierRawText
        second search criteria has VALID CarLocationID
        or has VALID location code/category/supplierRawText*/
        final String tuid = "3000103";
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
         PropertyResetHelper.filterReqSearchList(searchRequest,SettingsProvider.CARMNSCSDATASOURCE);
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
        locationTestCommon(searchRequest,testScenario,tuid,"0",SettingsProvider.CARMNSCSDATASOURCE,"1","1");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",SettingsProvider.CARMNSCSDATASOURCE,"1","0");
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000104LocationTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
          /*OFF_AIRPORT_SEARCH
        Search.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "3000104";
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OffAirport_AGP.getTestScenario();
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource scsDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest,scsDataSource);
        long count =0;
        for(CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()){
            count++;
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
        locationTestCommon(searchRequest,testScenario,tuid,"0",scsDataSource,"1","1");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",scsDataSource,"2","0");
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void cass3000105LocationTest()  throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException
    {
          /*AIRPORT_SEARCH
            Search.FilterInvalidLocationCode/enable=1
            CarLocationID is NULL
            VALID Iata code*/
        final String tuid = "3000105";
        final TestScenario testScenario = CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario();
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource scsDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_SERVER, SettingsProvider.DB_CARS_MICORNNEXUSSCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

        //send request and verify (feature off )
        locationTestCommon(searchRequest,testScenario,tuid,"0",scsDataSource,"1","0");

        //send request and verify (feautre on)
        locationTestCommon(searchRequest,testScenario,tuid,"1",scsDataSource,"1","0");
    }

    //verifyType 1 : have GDS request send out ,2: no GDS request send out
    public void locationTestCommon(CarSupplyConnectivitySearchRequestType searchRequest,TestScenario testScenario,String tuid,String posSettingValue,DataSource scsDataSource,String verifyType,String ignoreFlag) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,DataAccessException {
        //set posconfig
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.SEARCH_FILTERINVALIDLOCATIONCODE_ENABLE);
        posConfig.setSettingValue(posSettingValue);
        ConfigSetUtil.posConfigSet(posConfig,null,httpClient,tuid,SettingsProvider.SERVICE_ADDRESS,true);

        //send request
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, randomGuid);
        logger.info("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        logger.info("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        //verify
        if("1".equals(verifyType)) {
            final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
            final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, randomGuid, testScenario);
            System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
            ExecutionHelper.searchVerification(searchVerificationInput, spooferTransport, testScenario, randomGuid, logger);
            SearchVerification.verifyCarLocationInfo(basicVerificationContext, searchVerificationInput.getResponse(), scsDataSource, "1",testScenario);
        }else if("2".equals(verifyType)){
            SearchVerification.verifyExistsResponseError(searchVerificationInput);
        }

    }

}