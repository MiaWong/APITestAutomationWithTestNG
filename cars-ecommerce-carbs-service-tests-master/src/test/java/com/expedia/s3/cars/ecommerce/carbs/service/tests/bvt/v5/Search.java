package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt.v5;

import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.v5.util.SearchHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
public class Search extends SuiteCommon
{
    Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void searchBVTStandaloneRoundTrip() throws Exception
    {
        testSearch(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "10001");
    }

    void testSearch(TestScenario testScenario, String userGUID) throws Exception
    {
        final V5SearchVerificationInput searchVerificationInput =
                SearchHelper.search(httpClient, testScenario, userGUID);
        SearchHelper.searchVerification(
                searchVerificationInput, null, testScenario, PojoXmlUtil.getRandomGuid(), logger, false);
    }
}
