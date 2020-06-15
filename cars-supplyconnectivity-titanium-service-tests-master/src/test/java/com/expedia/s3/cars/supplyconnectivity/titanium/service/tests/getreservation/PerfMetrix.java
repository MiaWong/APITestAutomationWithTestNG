package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized.VerifyCancelPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.customized.VerifyCostAndAvailPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyGetDetailsPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.customized.VerifyGetReservationPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized.VerifyReservePerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.VerifySearchPerfmetricsLogging;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

/**
 * Created by jiyu on 8/30/16.
 */
public class PerfMetrix extends SuiteContext
{
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void titaniumTP95TestOffairportRoundtrip() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR.getTestScenario(),
                "1077559", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIPerfMetrics(testData, false);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void titaniumTP95TestTP95Oneway() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1077558", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testTIPerfMetrics(testData, false);
    }

    @Test(groups = {TestGroup.PERFMETRICS_REGRESSION})
    public void titaniumTP95TestPackage() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario(),
                "1083130", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);
        testTIPerfMetrics(testData, false);
    }

    //Because it's taking long to retreive splunk data to for PerfMetrics logging testing, so test search to cancel in one case
    private void testTIPerfMetrics(TestData testData, boolean needSpecialEquipment) throws  Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //GetDetails
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator , testData.getGuid());

        //GetCostAvail
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();
        costandavailRequest.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
        final GetCostAndAvailabilityVerificationInput costAvailInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, testData.getGuid());
        //final GetCostAndAvailabilityVerificationInput costAvailInput = CostAndAvailHelper.getCostAndAvailability(httpClient, requestGenerator, testData.getGuid());

        //Reserve
        final ReserveVerificationInput reserveInput = ReserveHelper.reserve(httpClient, requestGenerator, testData.getGuid(), needSpecialEquipment);

        //GetReservation
        final GetReservationVerificationInput getReservationinput = GetReservationHelper.retrieveReservation(httpClient, requestGenerator, testData.getGuid(), needSpecialEquipment);

        //cancel
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCancelRequest(), testData.getGuid());
        CancelHelper.cancelVerify(cancelVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger, false);

        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));

        //Get splunk data
        Thread.sleep(10000);
        final Date endTime = new Date();
        final String splunkQuery = "index=app ServiceName=cars-supplyconnectivity-titanium-service OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - Search
        final VerifySearchPerfmetricsLogging searchVerifier = new VerifySearchPerfmetricsLogging();
        searchVerifier.verify(searchVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST, splunkResult );

        //GetDetails
        final VerifyGetDetailsPerfmetricsLogging detailsVerifier = new VerifyGetDetailsPerfmetricsLogging();
        detailsVerifier.verify(getDetailsVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.DETAILSREQUEST, splunkResult );

        //CostAvail
        final VerifyCostAndAvailPerfmetricsLogging costAvailVerifier = new VerifyCostAndAvailPerfmetricsLogging();
        costAvailVerifier.verify(costAvailInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST, splunkResult );

        //Reserve
        final VerifyReservePerfmetricsLogging reserveVerifier = new VerifyReservePerfmetricsLogging();
        reserveVerifier.verify(reserveInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.RESERVEREQUEST, splunkResult );

        //GetReservation
        final VerifyGetReservationPerfmetricsLogging getReservationVerifier = new VerifyGetReservationPerfmetricsLogging();
        getReservationVerifier.verify(getReservationinput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.GETRESERVATIONREQUEST, splunkResult );

        //Cancel
        final VerifyCancelPerfmetricsLogging cancelVerifier = new VerifyCancelPerfmetricsLogging();
        cancelVerifier.verify(cancelVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.CANCELREQUEST, splunkResult );



    }


    @Test(groups = {"TP95"})
    public void titaniumErrorHandlingTP95Test() throws  Exception
    {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GBP").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OffAirport_LHR.getTestScenario(),
                "1076916", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Days3);
        testTIErrorMapPerfMetrics(testData, false);
    }

    private void testTIErrorMapPerfMetrics(TestData testData, boolean needSpecialEquipment) throws IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,DataAccessException, Exception {
        //Get guid for normal request
        final String normalGUID = testData.getGuid();

        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = SuiteContext.createSearchRequest(testData);

        //Normal search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, normalGUID);

        //Create a new GUID for error map requests
        final String errorMapGUID = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        //Search error handling request
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CostAvailError30").build(), errorMapGUID);
        final SearchVerificationInput searchErrorHandlingVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, errorMapGUID);

        //GetDetails error handling
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_DetailError16").build(), errorMapGUID);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsHelper.getDetails( httpClient, requestGenerator , errorMapGUID);

        //GetCostAvail error handling
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CostAvailError2").build(), errorMapGUID);
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();
        costandavailRequest.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
        final GetCostAndAvailabilityVerificationInput costAvailInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, errorMapGUID);

        //normal Reserve
        ReserveHelper.reserve(httpClient, requestGenerator, normalGUID, needSpecialEquipment);

        //Reserve error handling request
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_ReserveError").build(), errorMapGUID);
        final ReserveVerificationInput reserveErrorHandlingInput = ReserveHelper.reserve(httpClient, requestGenerator, errorMapGUID, "EMreserve8");

        //GetReservation error handling request
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_GetReservationError").build(), errorMapGUID);
        final GetReservationVerificationInput getReservationinput = GetReservationHelper.retrieveReservation(httpClient, requestGenerator, errorMapGUID, "EMgetReservation19");

        //Normal cancel
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCancelRequest(), normalGUID);
        CancelHelper.cancelVerify(cancelVerificationInput, spooferTransport, testData.getScenarios(), normalGUID, false, logger, false);

        //Cancel error handling request
        final CancelVerificationInput cancelErrorHandlingVerificationInput = CancelHelper.cancel(httpClient, requestGenerator, errorMapGUID, "25");

        /*System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAvailInput.getRequest())));
        System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAvailInput.getResponse())));*/

        //Get splunk data
        Thread.sleep(10000);
        final Date endTime = new Date();
        final String splunkQuery = "index=app ServiceName=cars-supplyconnectivity-titanium-service OriginatingGUID=" + errorMapGUID + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = spooferTransport.retrieveRecords(errorMapGUID);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, errorMapGUID,
                testData.getScenarios());
        // - Search
        final VerifySearchPerfmetricsLogging searchVerifier = new VerifySearchPerfmetricsLogging();
        searchVerifier.verify(searchErrorHandlingVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST, splunkResult );

        //GetDetails
        final VerifyGetDetailsPerfmetricsLogging detailsVerifier = new VerifyGetDetailsPerfmetricsLogging();
        detailsVerifier.verify(getDetailsVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.DETAILSREQUEST, splunkResult );

        //CostAvail
        final VerifyCostAndAvailPerfmetricsLogging costAvailVerifier = new VerifyCostAndAvailPerfmetricsLogging();
        costAvailVerifier.verify(costAvailInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST, splunkResult );

        //Reserve
        final VerifyReservePerfmetricsLogging reserveVerifier = new VerifyReservePerfmetricsLogging();
        reserveVerifier.verify(reserveErrorHandlingInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.RESERVEREQUEST, splunkResult );

        //GetReservation
        final VerifyGetReservationPerfmetricsLogging getReservationVerifier = new VerifyGetReservationPerfmetricsLogging();
        getReservationVerifier.verify(getReservationinput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.GETRESERVATIONREQUEST, splunkResult );

        //Cancel
        final VerifyCancelPerfmetricsLogging cancelVerifier = new VerifyCancelPerfmetricsLogging();
        cancelVerifier.verify(cancelErrorHandlingVerificationInput, verificationContext, CommonConstantManager.TitaniumGDSMessageName.CANCELREQUEST, splunkResult );



    }



}
