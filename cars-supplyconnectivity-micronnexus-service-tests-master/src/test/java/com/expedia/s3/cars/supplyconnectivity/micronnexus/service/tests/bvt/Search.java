package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.bvt;

/**
 * Created by v-mechen on 8/16/2016.
 */

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import org.testng.annotations.Test;

public class Search extends SuiteCommon
{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs186874SearchBVTTest() throws  Exception
    {
        BasicRequestActions requestActions = new BasicRequestActions();
        final TestData testData = new TestData(httpClient, CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), "186874", PojoXmlUtil.getRandomGuid());

        requestActions.search(testData);
    }


}