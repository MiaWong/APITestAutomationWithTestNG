package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getreservation;

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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getreservation.GetReservationMapVerification;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by fehu on 8/8/2017.
 */
public class ReqAndRespMapForGetReservation extends SuiteCommon {


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void test116777GDSMapMNXGetReservationWeeklyCurrency() throws Exception {
        testBasicMNXGetReservation(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.WeeklyDays7, 116777);
    }
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void test116778GDSMapMNXGetReservationDailyNoCurrency() throws Exception {

        testBasicMNXGetReservation(CommonScenarios.MN_GBR_Standalone_OneWay_OffAirport_AGP.getTestScenario(), CommonEnumManager.TimeDuration.Days3, 116778);
    }

    public void testBasicMNXGetReservation(TestScenario scenario, CommonEnumManager.TimeDuration useDays, int tuid) throws Exception {
           final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
            final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);
            testData.setUseDays(useDays);

            //Search
            final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport,logger, SettingsProvider.CARMNSCSDATASOURCE);
            SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
            BasicRequestActions requestActions = new BasicRequestActions();

            //getDetail
            requestActions.getDetail(requestGenerator, httpClient, testData);
            //getCostAndAvail
            requestActions.getCostAndAvail(requestGenerator, httpClient, testData);
            //reserve
            requestActions.reserve(requestGenerator, httpClient, testData);

            //getReservation
            String getReservationGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            GetReservationVerificationInput getReservationVerificationInput = ExecutionHelper.getReservation(httpClient, requestGenerator, getReservationGuid);
            GetReservationMapVerification verification = new GetReservationMapVerification();
            BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(getReservationGuid), getReservationGuid ,scenario);
            IVerification.VerificationResult result = verification.verifyGetReservationMap(getReservationVerificationInput, basicVerificationContext);

            //cancel
            requestActions.cancel(requestGenerator, httpClient, testData);

          if(!result.isPassed())
          {
            Assert.fail(result.toString());
          }



    }
}
