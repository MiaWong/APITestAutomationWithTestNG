package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.reserve;

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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.book.ReserveMapVerification;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by fehu on 8/15/2017.
 */
public class ReqAndRespMapForReserve extends SuiteCommon {


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void test116777GDSMapMNXReserveWeeklyCurrency() throws Exception {
        testBasicMNXReserve(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.WeeklyDays7, 116777);
    }
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void test116778GDSMapMNXReserveDailyNoCurrency() throws Exception {

        testBasicMNXReserve(CommonScenarios.MN_GBR_Standalone_OneWay_OffAirport_AGP.getTestScenario(), CommonEnumManager.TimeDuration.Days3, 116778);
    }

    public void testBasicMNXReserve(TestScenario scenario, CommonEnumManager.TimeDuration useDays, int tuid) throws Exception {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);
        testData.setUseDays(useDays);

        //Search
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport,logger, SettingsProvider.CARMNSCSDATASOURCE);
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
        BasicRequestActions requestActions = new BasicRequestActions();
        try
        {
            //getDetail
            requestActions.getDetail(requestGenerator, httpClient, testData);
            //getCostAndAvail
            requestActions.getCostAndAvail(requestGenerator, httpClient, testData);
            //Reserve
            String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            ReserveVerificationInput reserveVerificationInput = ExecutionHelper.reserve(httpClient, requestGenerator, reserveGuid);
            requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
            ReserveMapVerification verification = new ReserveMapVerification();
            BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(reserveGuid), reserveGuid ,scenario);
            IVerification.VerificationResult result = verification.verify(reserveVerificationInput, basicVerificationContext);
            if(!result.isPassed())
            {
                Assert.fail(result.toString());
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage() + e.getStackTrace());
            throw new Exception(e);

        }
        finally {
            //cancel
            requestActions.cancel(requestGenerator, httpClient, testData);
        }

    }
}
