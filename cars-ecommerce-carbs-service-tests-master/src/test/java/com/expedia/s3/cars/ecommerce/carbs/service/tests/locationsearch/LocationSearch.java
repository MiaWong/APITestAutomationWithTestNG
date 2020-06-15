package com.expedia.s3.cars.ecommerce.carbs.service.tests.locationsearch;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsLocationSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.locationsearch.verification.LocationCommonVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.ClientConfig;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonLocationSearchScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.messages.location.search.defn.v1.CarLocationSearchResponse;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by yyang4 on 1/7/2018.
 */
public class LocationSearch extends SuiteCommon{
    private static final String LOCATIONSEARCH_IATAREQUESTS_CLIENT_CONFIG="LocationSearch.iataRequests/enable";
    private static final String LOCATIONSEARCH_DOWNSTREAMREQUESTS_CLIENT_CONFIG="LocationSearch.downstreamRequests/enable";

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs164679CarBSLocationSearch() throws Exception{
        //delivery false
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.US_100Mi.getTestScenario();
        scenario.setCollectionBoolean("");
        scenario.setDeliveryBoolean("false");
        scenario.setOutOfOfficeHourBoolean("");
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(true);
        testCarBSLocationSearch(scenario, "164679");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs164681CarBSLocationSearch() throws Exception{
        //collection true
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.US_100Mi.getTestScenario();
        scenario.setCollectionBoolean("true");
        scenario.setDeliveryBoolean("");
        scenario.setOutOfOfficeHourBoolean("");
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(true);
        testCarBSLocationSearch(scenario, "164681");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs164705CarBSLocationSearch() throws Exception{
        //collection false delivery_true
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.US_100Mi.getTestScenario();
        scenario.setCollectionBoolean("false");
        scenario.setDeliveryBoolean("true");
        scenario.setOutOfOfficeHourBoolean("");
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(true);
        testCarBSLocationSearch(scenario, "164705");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs164730CarBSLocationSearch() throws Exception{
        //includeDetails true
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.US_100Mi.getTestScenario();
        scenario.setCollectionBoolean("");
        scenario.setDeliveryBoolean("");
        scenario.setOutOfOfficeHourBoolean("");
        scenario.setIncludeLocationDetails(true);
        scenario.setNullIncludeLocation(false);
        testCarBSLocationSearch(scenario, "164730");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs164736CarBSLocationSearch() throws Exception{
        //outOfOfficeHours false
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.US_100Mi.getTestScenario();
        scenario.setCollectionBoolean("");
        scenario.setDeliveryBoolean("");
        scenario.setOutOfOfficeHourBoolean("false");
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(true);
        testCarBSLocationSearch(scenario, "164736");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    //merge cases 229602,238766
    public void tfs229602CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_LatLong_MI.getTestScenario();
        testCarBSLocationSearchLatLong(scenario, "229602","NoFilterFromCVL");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs229603CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLT_OHRT.getTestScenario();
        testCarBSLocationSearchLatLong(scenario, "229603","BVT");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs238764CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP.getTestScenario();
        testCarBSLocationSearchLatLong(scenario, "238764","NoFilterFromCVL");
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs242162CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_LatLong_DELF_COLT_OHRT.getTestScenario();
        testCarBSLocationSearchLatLong(scenario, "242162","NoFilterFromCVL");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs247612CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_LatLong_MI.getTestScenario();
        testCarBSLocationSearchNegative(scenario, "247612","BothLatLongAndIATA");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs486267CarBSLocationSearchLatLong() throws Exception{
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_Vicinity_ON.getTestScenario();
        testCarBSLocationSearchLatLongWithReqVerify(scenario, "486267","NoFilterFromCVL");
    }


    public void testCarBSLocationSearch(LocationSearchTestScenario scenarios, String tuid) throws Exception{
        //send search request
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient));
        ExecutionHelper.locationSearch(scenarios, tuid, httpClient, randomGuid);
    }
    public void testCarBSLocationSearchLatLong(LocationSearchTestScenario scenarios, String tuid, String testType)throws Exception {
        commonLatLong(scenarios, tuid, testType, false);
    }

    public void testCarBSLocationSearchLatLongWithReqVerify(LocationSearchTestScenario scenarios, String tuid, String testType)throws Exception {
        commonLatLong(scenarios, tuid, testType, true);
    }

    public void commonLatLong(LocationSearchTestScenario scenarios, String tuid, String testType, boolean verifyReq)throws Exception {
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String clientId = "3";
        scenarios.setClientCode(CompareUtil.isObjEmpty(carBSHelper.getClientListById(clientId)) ? "" : carBSHelper.getClientListById(clientId).get(0).getClientCode());
        //send search request
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient));
        if (!"RouteToCVL".equals(testType)){
            setClientConfig(clientId,LOCATIONSEARCH_IATAREQUESTS_CLIENT_CONFIG,"1",tuid);
        }
        CarLocationSearchResponse responseCVL = null;
        if ("RouteToCVL".equals(testType))
        {
            //Send old CarVendorLocation search request, then get the car locations from CVL to compare with the results from new iata search
            scenarios.setCollectionBoolean("false");
            scenarios.setDeliveryBoolean("false");
            scenarios.setOutOfOfficeHourBoolean("false");
            scenarios.setIncludeLocationDetails(true);
            scenarios.setNullIncludeLocation(false);
            responseCVL = ExecutionHelper.locationSearch(scenarios, tuid, httpClient, randomGuid).getLocationSearchResponse();
        }
        final CarbsLocationSearchRequestGenerator generator = ExecutionHelper.locationIataSearch(scenarios, tuid, httpClient, randomGuid, null);
        LocationCommonVerify.verifyCarBSLocationSearchRouting(testType,generator.getCarLocationIataSearchResponse(), responseCVL,generator.getSpooferDoc());
        if(verifyReq){
            LocationCommonVerify.verifyLocationSearchFilterInAclqRequest(generator.getSpooferDoc(),scenarios);
        }
    }

    public void testCarBSLocationSearchNegative(LocationSearchTestScenario scenarios, String tuid ,String testType) throws Exception{
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String clientId = "7";
        scenarios.setClientCode(CompareUtil.isObjEmpty(carBSHelper.getClientListById(clientId)) ? "" : carBSHelper.getClientListById(clientId).get(0).getClientCode());
        //send search request
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient));
        setClientConfig(null,LOCATIONSEARCH_IATAREQUESTS_CLIENT_CONFIG,"0",tuid);
        setClientConfig(clientId,LOCATIONSEARCH_DOWNSTREAMREQUESTS_CLIENT_CONFIG,"0",tuid);
        final CarbsLocationSearchRequestGenerator generator = ExecutionHelper.locationIataSearch(scenarios, tuid, httpClient, randomGuid, testType);
        LocationCommonVerify.verifyCarBSLocationSearchRoutingNegative(generator.getCarLocationIataSearchResponse(),testType);
    }

    public void setClientConfig(String clientId, String settingName, String value ,String tuid) throws IOException{
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        clientConfig.setSettingName(settingName);
        clientConfig.setSettingValue(value);
        if(!CompareUtil.isObjEmpty(clientId)) {
            clientConfig.setClientId(Integer.parseInt(clientId));
        }
        ConfigSetUtil.clientConfigSet(clientConfig,httpClient,tuid,SettingsProvider.SERVICE_ADDRESS,true);
    }
}
