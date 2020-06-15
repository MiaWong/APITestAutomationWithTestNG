package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.costavail;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
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
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification.UapiMapCostAndAvailVerification;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Created by yyang4 on 10/10/2017.
 */
public class UapiMappingGetCostAndAvail extends SuiteCommon {

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1043454,1043450
    public void casss1043454UAPICostAvailSCSMappingAgencyMonthly() throws Exception {
        final String tuid = "1043454";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //Cases merge together 1043451,1043449,1045394,1045395
    public void casss1043451UAPICostAvailSCSMappingAgencyWeekly() throws Exception {
        final String tuid = "1043451";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport_oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays7);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1043455UAPICostAvailSCSMappingAgencyMonthlyExtra() throws Exception {
        final String tuid = "1043455";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays35);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1043453,1045242,1045243,1045244
    public void casss1043453UAPICostAvailSCSMappingAgencyWeeklyExtra() throws Exception {
        final String tuid = "1043453";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_AC);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        final CarRate carRate = new CarRate();
        carRate.setLoyaltyNum("H932DPQ");
        carRate.setPromoCode("U100700");
        testData.setCarRate(carRate);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1045151,1064579,1064578
    public void casss1045151UAPICostAvailSCSMappingGDSPMonthlyExtra() throws Exception {
        final String tuid = "1045151";
        final TestScenario testScenario = CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays37);
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_EP);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045149UAPICostAvailSCSMappingGDSpWeeklyExtra() throws Exception {
        final String tuid = "1045149";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Package_UKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays15);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    public void casss1045148UAPICostAvailSCSMappingGDSPWeekly() throws Exception {
        final String tuid = "1045148";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_nonUKLocation_OnAirport_Oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays7);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {TestGroup.SHOPPING_REGRESSION})
    //cases merge together 1045147,1043456
    public void casss1045147UAPICostAvailSCSMappingGDSPDaily() throws Exception {
        final String tuid = "1045147";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {"Merchant"})
    public void casss1045157UAPICostAvailSCSMapping() throws Exception {
        final String tuid = "1045157";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Package_nonUKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.MounthlyExtDays35);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {"Merchant"})
    //cases merge together 1045155,1045152
    public void casss1045155UAPICostAvailSCSMappingMerchantWeeklyExtra() throws Exception {
        final String tuid = "1045155";
        final TestScenario testScenario = CommonScenarios.Worldspan_FR_Merchant_Standalone_nonFRLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays9);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {"Merchant"})
    //Cases merge together 1045154,1045145,1045146
    public void casss1045154UAPICostAvailSCSMappingMerchantWeekly() throws Exception {
        final String tuid = "1045154";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    @Test(groups= {"Merchant"})
    public void casss1045156UAPICostAvailSCSMappingMerchantMonthly() throws Exception {
        final String tuid = "1045156";
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OffAirport.getTestScenario();
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        testCarSCSGetCostAndAvailUAPIMapping(testScenario,tuid,testData,spooferTransport,guid);
    }

    // <summary>
    // Basic method for UAPI cost&Avail mapping
    // </summary>
    public void testCarSCSGetCostAndAvailUAPIMapping(TestScenario testScenario , String tuid , TestData testData, SpooferTransport spooferTransport, String guid) throws Exception {
        //send search request
       final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(DataSourceHelper.getWSCSDataSourse());
        final CarSupplyConnectivitySearchRequestType searchRequest = scsSearchRequestGenerator.createSearchRequest(testData);
        final SearchVerificationInput verificationInput = TransportHelper.sendReceive(testData.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        System.out.println("search request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getRequest())));
        System.out.println("search response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(verificationInput.getResponse())));

        //send costAndAvail request
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(verificationInput);
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();
        requestGenerator.setCostAndAvailReq(costandavailRequest);
        //For perpay, set RateCategoryCOde to Standard
//        costandavailRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setRateCategoryCode("Standard");
        //set costAndAvailability request param
        costandavailRequest.getAuditLogTrackingData().setAuditLogForceLogging(true);

        //send and rcv
        final String guidCostAndAvail = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, guidCostAndAvail);
        requestGenerator.setCostAndAvailResp(costAndAvailabilityVerificationInput.getResponse());
        System.out.println("costAndAvail request xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getRequest())));
        System.out.println("costAndAvail response xml==> "+ PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse())));
        final Document spooferDoc = spooferTransport.retrieveRecords(guidCostAndAvail);
        System.out.println("spoofer xml==> "+ PojoXmlUtil.toString(spooferDoc));
        //verify
        ExecutionHelper.getCostAndAvailabilityVerification(costAndAvailabilityVerificationInput,spooferTransport,testScenario,guidCostAndAvail,logger);
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, guidCostAndAvail, testScenario);
        UapiMapCostAndAvailVerification.uapiMapVerifierWSCSCostAndAvail(basicVerificationContext,costAndAvailabilityVerificationInput,DataSourceHelper.getWSCSDataSourse(),DataSourceHelper.getCarInventoryDatasource(),httpClient);
    }
}
