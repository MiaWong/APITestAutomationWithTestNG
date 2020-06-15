package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getdetails;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail.GetDetailsMapVerification;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by fehu on 6/26/2017.
 */
public class ReqAndRespMapForGetDetails extends SuiteCommon{

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test116167GDSMapMNXGetDetailsWeeklyCurrency() throws Exception {
        testBasicMNXGetDetails(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.WeeklyDays7, 116167);
    }
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test190938GDSMapMNXGetDetailsDailyNoCurrency() throws Exception {

        testBasicMNXGetDetails(CommonScenarios.MN_GBR_Standalone_OneWay_OffAirport_AGP.getTestScenario(), CommonEnumManager.TimeDuration.Days3, 116168);
    }

    public void testBasicMNXGetDetails(TestScenario scenario, CommonEnumManager.TimeDuration useDays, int tuid) throws Exception {

            final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
            final String searchGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), searchGuid, spooferTransport);
            testData.setUseDays(useDays);

            //Search
            final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport,logger, SettingsProvider.CARMNSCSDATASOURCE);


            //getDetails
            final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
            //set getDetails guid
            testData.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
            final GetDetailsVerificationInput getDetailsVerificationInput =
                    ExecutionHelper.getDetail(httpClient, requestGenerator,testData.getGuid());
            BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(testData.getGuid()), testData.getGuid(),testData.getScenarios());
            GetDetailsMapVerification verification = new GetDetailsMapVerification();
            IVerification.VerificationResult result = verification.verifyForMap(getDetailsVerificationInput, basicVerificationContext);

          if (!result.isPassed())
          {
          Assert.fail(result.toString());
          }
   }

}
