package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getreservation;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
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
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/8/2017.
 */
public class PerfMetrics extends SuiteCommon {


    //@Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void testMNPerfMetricsOnAirport() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testMNPerfMetrics(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.WeeklyDays7, 116777);
    }

    //@Test(groups = {TestGroup.BOOKING_REGRESSION})
    @Test(groups = {"splunkRegression"})
    public void testMNPerfMetricsOffAirport() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        testMNPerfMetrics(CommonScenarios.MN_GBR_Standalone_OneWay_OffAirport_AGP.getTestScenario(), CommonEnumManager.TimeDuration.Days3, 116778);
    }

   // @Test(groups = {TestGroup.BOOKING_REGRESSION})
   @Test(groups = {"splunkRegression"})
    public void testMNPerfMetricsPackage() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        testMNPerfMetrics(CommonScenarios.MN_GBR_Package_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.Days3, 116778);
    }

    public void testMNPerfMetrics(TestScenario scenario, CommonEnumManager.TimeDuration useDays, int tuid) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, DataAccessException, IllegalAccessException {
       final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
            final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);
            testData.setUseDays(useDays);

            //Search
            final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport,logger, SettingsProvider.CARMNSCSDATASOURCE);
            SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());

           try
           {
            //getDetail
            final GetDetailsVerificationInput getDetailsVerificationInput =
                       ExecutionHelper.getDetail(httpClient, requestGenerator,testData.getGuid());

            //getCostAndAvail
            final GetCostAndAvailabilityVerificationInput costAvailInput = ExecutionHelper.getCostAndAvail(httpClient, requestGenerator, testData.getGuid());

            //reserve
            final ReserveVerificationInput reserveInput =  ExecutionHelper.reserve(httpClient, requestGenerator, testData.getGuid());

            //getReservation
            GetReservationVerificationInput getReservationinput = ExecutionHelper.getReservation(httpClient, requestGenerator,  testData.getGuid());

            //cancel
            final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator,  testData.getGuid());


              logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
              logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

               //Get splunk data
               Thread.sleep(10000);
               final Date endTime = new Date();
               final String splunkQuery = "index=app ServiceName=cars-supplyconnectivity-micronnexus-service OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
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
               searchVerifier.verify(searchVerificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.COSTAVAILREQUEST, splunkResult );

               //GetDetails
               final VerifyGetDetailsPerfmetricsLogging detailsVerifier = new VerifyGetDetailsPerfmetricsLogging();
               detailsVerifier.verify(getDetailsVerificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.DETAILSREQUEST, splunkResult );

               //CostAvail
               final VerifyCostAndAvailPerfmetricsLogging costAvailVerifier = new VerifyCostAndAvailPerfmetricsLogging();
               costAvailVerifier.verify(costAvailInput, verificationContext, CommonConstantManager.MNGDSMessageName.COSTAVAILREQUEST, splunkResult );

               //Reserve
               final VerifyReservePerfmetricsLogging reserveVerifier = new VerifyReservePerfmetricsLogging();
               reserveVerifier.verify(reserveInput, verificationContext, CommonConstantManager.MNGDSMessageName.RESERVEREQUEST, splunkResult );

               //GetReservation
               final VerifyGetReservationPerfmetricsLogging getReservationVerifier = new VerifyGetReservationPerfmetricsLogging();
               getReservationVerifier.verify(getReservationinput, verificationContext, CommonConstantManager.MNGDSMessageName.GETRESERVATIONREQUEST, splunkResult );

               //Cancel
               final VerifyCancelPerfmetricsLogging cancelVerifier = new VerifyCancelPerfmetricsLogging();
               cancelVerifier.verify(cancelVerificationInput, verificationContext, CommonConstantManager.MNGDSMessageName.CANCELREQUEST, splunkResult );

   }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage() + e.getStackTrace());
        }

    }
}
