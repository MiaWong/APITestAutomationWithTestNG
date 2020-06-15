package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMGetChangeDetailReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMGetChangeDetailSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

/**
 * Created by fehu on 8/30/2016.
 */
public class CarBSOMGetChangeDetailsBVTTest  extends SuiteCommon{

    @Test(groups = {"bvt"})
    public void casss110017OMGetChangeDetailSanityTest() throws Exception {
        final String randomGuid= PojoXmlUtil.getRandomGuid();
        testCarbsOMGetChangeDetail(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "110017", randomGuid);

    }

    private void testCarbsOMGetChangeDetail(TestScenario scenarios, String tuid, String guid) throws Exception {

        final TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (testData);
        //getChangeDetail
        final  CarbsOMGetChangeDetailReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMGetChangeDetailReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMGetChangeDetailSender.carBSOMGetChangeDetailSend(scenarios, guid, httpClient, carbsOMRetrieveReqAndRespGenerator, true);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, guid, httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());


    }
}
