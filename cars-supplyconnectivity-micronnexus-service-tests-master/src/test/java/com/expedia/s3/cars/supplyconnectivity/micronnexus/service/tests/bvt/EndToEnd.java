package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import org.testng.annotations.Test;

/**
 * Created by v-mechen on 8/18/2016.
 */
public class EndToEnd extends SuiteCommon
{

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs518019EndToEndBVTTest() throws Exception
    {
        BasicRequestActions requestActions = new BasicRequestActions();
        TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "12001", PojoXmlUtil.getRandomGuid());
        SCSRequestGenerator requestGenerator = requestActions.search(testData);
        requestActions.getCostAndAvail(requestGenerator, httpClient,testData);
        requestActions.getDetail(requestGenerator, httpClient,testData);
        requestActions.reserve(requestGenerator, httpClient,testData);
        requestActions.getReservation(requestGenerator, httpClient, testData);
        requestActions.cancel(requestGenerator, httpClient, testData);

    }
}

