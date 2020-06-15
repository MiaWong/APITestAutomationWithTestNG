package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.utils.PropertyResetHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;

import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;


/**
 * Created by yyang4 on 11/3/16.
 * CASSS 3159 SCS should filter out locations that GDS does not recognize
 */

public class Search extends SuiteContext
{
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3159LocationTestValidLocationIDInvalidLocatoinInfo() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
       /* OFF_AIRPORT_SEARCH
        SearchGDSMsgMap.FilterInvalidLocationCode/enable=0
        CarLocationID is VALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "6000101";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario();
        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
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
        locationTestCommon(searchRequest,tuid,"0","1","0");
        //send request and verify (feautre on)
        locationTestCommon(searchRequest,tuid,"1","1","0");
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3159LocationTestInValidLocationIDValidLocatoinInfo() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        /*OFF_AIRPORT_SEARCH
        SearchGDSMsgMap.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        VALID location code/category/supplierRawText*/
        final String tuid = "6000102";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            startCarLocationKeyType.setCarVendorLocationID(null);
            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            endCarLocationKeyType.setCarVendorLocationID(null);
        }
        //send request and verify (feature off )
        locationTestCommon(searchRequest,tuid,"0","1","0");
        //send request and verify (feautre on)
        locationTestCommon(searchRequest,tuid,"1","2","0");

    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3159LocationTestValidAndInvalidLocationID() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
       /* OFF_AIRPORT_SEARCH two search criteria
        SearchGDSMsgMap.FilterInvalidLocationCode/enable=0
        first search criteria has CarLocationID is NULL or INVALID and INVALID location code/category/supplierRawText
        second search criteria has VALID CarLocationID
        or has VALID location code/category/supplierRawText*/
        final String tuid = "6000103";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
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
            } else if (count == 2) {
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
        locationTestCommon(searchRequest,tuid,"0","1","1");
        //send request and verify (feautre on)
        locationTestCommon(searchRequest,tuid,"1","1","0");
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss3159LocationTestInvalidLocationIDAndLocatoinInfo() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        /*OFF_AIRPORT_SEARCH
        SearchGDSMsgMap.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL or INVALID number
        INVALID location code/category/supplierRawText*/
        final String tuid = "6000104";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_LYS.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final DataSource carsAmadeusSCSDataSource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        PropertyResetHelper.filterReqSearchList(searchRequest, carsAmadeusSCSDataSource);
        for (CarSearchCriteriaType criteriaType : searchRequest.getCarSearchCriteriaList().getCarSearchCriteria()) {
            CarLocationKeyType startCarLocationKeyType = criteriaType.getCarTransportationSegment().getStartCarLocationKey();
            startCarLocationKeyType.setCarVendorLocationID(null);
            startCarLocationKeyType.setLocationCode("XXX");
            startCarLocationKeyType.setCarLocationCategoryCode("C");
            startCarLocationKeyType.setSupplierRawText("888");

            CarLocationKeyType endCarLocationKeyType = criteriaType.getCarTransportationSegment().getEndCarLocationKey();
            endCarLocationKeyType.setCarVendorLocationID(null);
            endCarLocationKeyType.setLocationCode("XXX");
            endCarLocationKeyType.setCarLocationCategoryCode("C");
            endCarLocationKeyType.setSupplierRawText("888");

        }
        //send request and verify (feature off )
        //locationTestCommon(searchRequest,tuid,"0","2","1");
        //send request and verify (feautre on)
        locationTestCommon(searchRequest,tuid,"1","2","0");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casss11360ShuttleInformation() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG.getTestScenario(),
                "6000101", null);

        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(parameters);
        final DataSource carsAmadeusSCSDataSource =  DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
                SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        logger.info("search request xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid,httpClient,searchRequest);
        logger.info("search response xml ===>"+PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        SearchResponseVerifier.verifyCarProductReturned(response);

        final PosConfigHelper configHelper = new PosConfigHelper(carsAmadeusSCSDataSource, SettingsProvider.SERVICE_ADDRESS);

        boolean enableShuttleInfo = configHelper.checkPosConfigFeatureEnable(parameters.getScenarios(),"1","populateShuttleInfoFromGDS/enable");

        SearchResponseVerifier.verifyShuttleInfo(response, enableShuttleInfo);

    }

    /*@Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void cass6000105LocationTestOnAirport() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        *//*AIRPORT_SEARCH
        SearchGDSMsgMap.FilterInvalidLocationCode/enable=0
        CarLocationID is NULL
        VALID Iata code*//*
        final String tuid = "6000105";
        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARAMADEUSSCSDATASOURCE);
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();

        TestData testData = new TestData(httpClient, testScenario, tuid, null);

        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        //send request and verify (feature off )
        locationTestCommon(searchRequest,tuid,"0","1","0");
        //send request and verify (feautre on)
        locationTestCommon(searchRequest,tuid,"1","1","0");
    }*/

    //verifyType 1 : have correct car return ,2: no car return
    public void locationTestCommon(CarSupplyConnectivitySearchRequestType searchRequest, String tuid, String posSettingValue, String verifyType, String ignoreFlag) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.SEARCH_FILTERINVALIDLOCATIONCODE_ENABLE);
        posConfig.setSettingValue(posSettingValue);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
        final String randomGuid = PojoXmlUtil.getRandomGuid();
        System.out.println("request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        final CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(randomGuid, httpClient, searchRequest);
        System.out.println("response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        if("1".equals(verifyType)) {
            SearchResponseVerifier.verifyCarProductReturned(response);
            SearchResponseVerifier.verifyCarLocationInfo(response, SettingsProvider.CARAMADEUSSCSDATASOURCE, ignoreFlag);
        }else if("2".equals(verifyType)){
            final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(searchRequest,response);
            SearchResponseVerifier.verifyExistsResponseError(searchVerificationInput);
        }
    }

}
