package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getcostandavail;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by wixie on 11/3/16.
 */
public class CarRateDetail extends SuiteCommon
{
    final Logger logger = Logger.getLogger(getClass());

    private void testGetCostAndAvailCarRateDetail(TestData testData) throws Exception {
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData,
                testData.getSpooferTransport(),
                logger,
                SettingsProvider.CARMNSCSDATASOURCE
        );

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        //set costandAvail guid
        testData.setGuid(ExecutionHelper.generateNewOrigGUID(testData.getSpooferTransport()));
        final GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInputVerificationInput =
                ExecutionHelper.getCostAndAvail(
                        httpClient,
                        requestGenerator,
                        testData.getGuid()
                );

        ExecutionHelper.getCostAndAvailCarRateDetailVerification(
                getCostAndAvailVerificationInputVerificationInput,
                testData.getSpooferTransport(),
                testData.getScenarios(),
                testData.getGuid(),
                logger
        );

        //CASSS-4852 Micronnexus scs - Always renew ID_Context before performing Rate Rules (details) request
        IVerification.VerificationResult result = ExecutionHelper.retrySearchVerify(
                getCostAndAvailVerificationInputVerificationInput,
                testData.getSpooferTransport(),
                testData.getScenarios(),
                testData.getGuid(),
                logger
        );
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }

    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void testGetCostAndAvailRoundTrip() throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "12001", ExecutionHelper.generateNewOrigGUID(spooferTransport), spooferTransport);
        testGetCostAndAvailCarRateDetail(testData);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void testGetCostAndAvailOneWay() throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_Oneway_OnAirport_AGP.getTestScenario(), "12002", ExecutionHelper.generateNewOrigGUID(spooferTransport), spooferTransport);
        testGetCostAndAvailCarRateDetail(testData);
    }
}
