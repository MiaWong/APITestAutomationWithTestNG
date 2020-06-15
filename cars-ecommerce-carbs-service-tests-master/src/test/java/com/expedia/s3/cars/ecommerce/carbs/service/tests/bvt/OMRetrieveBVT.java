package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

/**
 * Created by fehu on 3/17/2017.
 */
public class OMRetrieveBVT extends SuiteCommon{

    //Trip ClientID=5  CarBS level Agency
    @Test(groups = {"bvt"})
    public void casss518109OMSAgencyTripTestRetrieve() throws Exception {
        testOMSRetrieve(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),"518109", "0Q7XRN");
    }


    private void testOMSRetrieve(TestScenario scenarios, String tuid, String clientCode) throws Exception {

        final TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        testData.setClientCode(clientCode);
        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        //retrieve
        final  CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(scenarios, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);

        //Cancel
       final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }
}
