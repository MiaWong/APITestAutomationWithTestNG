package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.customized.VerifyCancelPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyGetDetailsPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.customized.VerifyGetReservationPerfmetricsLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized.VerifyReserveErrorAnalysisLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.VerifySearchErrorAnalysisLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.customized.VerifySearchPerfmetricsLogging;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yyang4 on 1/2/2018.
 */
public class TP95CommonVerification {
    public static void tp95PerfMetricsSearchVerify(TestData testData, SearchVerificationInput searchVerificationInput) throws Exception {
        Thread.sleep(10000);

        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - Search
        final VerifySearchPerfmetricsLogging searchVerifier = new VerifySearchPerfmetricsLogging();
        searchVerifier.verify(searchVerificationInput, verificationContext, GDSMsgNodeTags.WorldSpanNodeTags.VSAR_REQUEST_TYPE, splunkResult);
    }

    public static void tp95PerfMetricsGetDetailsVerify(TestData testData, GetDetailsVerificationInput detailsVerificationInput) throws Exception {
        Thread.sleep(10000);

        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - getDetails
        final VerifyGetDetailsPerfmetricsLogging getDetailsPerfmetricsLogging = new VerifyGetDetailsPerfmetricsLogging();
        getDetailsPerfmetricsLogging.verify(detailsVerificationInput, verificationContext, GDSMsgNodeTags.WorldSpanNodeTags.VRUR_REQUEST_TYPE, splunkResult);
    }


    public static void tp95ErrorAnalysisReserveVerify(TestData testData, ReserveVerificationInput reserveVerificationInput) throws Exception {

        //Get splunk data
        Thread.sleep(10000);
        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " ActionType=Reserve LogType=ErrorAnalysis  TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - reserve
        final VerifyReserveErrorAnalysisLogging reserveErrorAnalysisLogging = new VerifyReserveErrorAnalysisLogging();
        reserveErrorAnalysisLogging.verify(reserveVerificationInput, verificationContext, splunkResult);
    }

    public static void tp95PerfMetricsGetReservationVerify(TestData testData, GetReservationVerificationInput reservationVerificationInput) throws Exception {
        if (CompareUtil.isObjEmpty(reservationVerificationInput.getResponse().getCarReservationList().getCarReservation())) {
            Assert.fail("No CarReservation list return.");
        }
        for (CostType cost : reservationVerificationInput.getResponse().getCarReservationList().getCarReservation().get(0).getCarProduct().getCostList().getCost()) {
            if ("Total".equals(cost.getFinanceCategoryCode())) {
                for (CostType costReq : reservationVerificationInput.getRequest().getCarReservationList().getCarReservation().get(0).getCarProduct().getCostList().getCost())
                    if ("Total".equals(costReq.getFinanceCategoryCode())) {
                        cost.getLegacyFinanceKey().setLegacyMonetaryClassID(costReq.getLegacyFinanceKey().getLegacyMonetaryClassID());
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(costReq.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID());
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(costReq.getLegacyFinanceKey().getLegacyMonetaryCalculationID());
                    }
            }
        }

        Thread.sleep(10000);
        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - reservation
        final VerifyGetReservationPerfmetricsLogging verifyGetReservationPerfmetricsLogging = new VerifyGetReservationPerfmetricsLogging();
        verifyGetReservationPerfmetricsLogging.verify(reservationVerificationInput, verificationContext, GDSMsgNodeTags.WorldSpanNodeTags.URRR_REQUEST_TYPE, splunkResult);
    }

    public static void tp95PerfMetricsCancelVerify(TestData testData, CancelVerificationInput cancelVerificationInput) throws Exception {
        Thread.sleep(10000);
        final Date endTime = new Date();
        final String splunkQuery = "index=app  OriginatingGUID=" + testData.getGuid() + " LogType=PerfMetrics TUID=" + testData.getTuid() +
                " earliest=" + (endTime.getTime() / 1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
        // Splunk host address
        final String hostName = "https://splunklab6";
        // Splunk host port, default value is 8089
        final int hostPort = 8089;
        final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);

        //Verify logging
        final Document spooferTransactions = testData.getSpooferTransport().retrieveRecords(testData.getGuid());
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, testData.getGuid(),
                testData.getScenarios());
        // - cancel
        final VerifyCancelPerfmetricsLogging cancelPerfmetricsLogging = new VerifyCancelPerfmetricsLogging();
        cancelPerfmetricsLogging.verify(cancelVerificationInput, verificationContext, GDSMsgNodeTags.WorldSpanNodeTags.VCRQ_REQUEST_TYPE, splunkResult);
    }
}
