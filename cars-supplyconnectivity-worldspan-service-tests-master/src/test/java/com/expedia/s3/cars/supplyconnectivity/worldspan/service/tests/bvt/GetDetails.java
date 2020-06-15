package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Test;
/**
 * Created by miawang on 8/17/2016.
 */
public class GetDetails extends SuiteCommon {

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_43360_Details_BVT_Test_Agency() throws Exception {
        testDetails(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "43360", PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_44436_Details_BVT_Test_GDSP() throws Exception {
        testDetails(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "44436", PojoXmlUtil.getRandomGuid());
    }

    private void testDetails(TestScenario scenarios, String tuid, String guid) throws Exception {
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.bvtSearch(httpClient, scenarios, tuid, guid);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        logger.warn("search request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.warn("search response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        final GetDetailsVerificationInput getDetailsVerificationInput = ExecutionHelper.getDetails(httpClient, requestGenerator, guid);

        logger.warn("getDetail request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.warn("getDetail response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput, null, scenarios, guid, logger);
    }
}