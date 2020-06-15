package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Test;
/**
 * Created by miawang on 8/17/2016.
 */
public class ReserveAndCancel extends SuiteCommon {

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs_43362_ReserveAndCancel_BVT_Test_Agency() throws Exception {
        testReserve(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "43362", PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs_44438_ReserveAndCancel_BVT_Test_GDSP() throws Exception {
        testReserve(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "44438", PojoXmlUtil.getRandomGuid());
    }

    public void testReserve(TestScenario scenarios, String tuid, String guid) throws Exception {
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.bvtSearch(httpClient, scenarios, tuid, guid);

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        ExecutionHelper.getCostAndAvailability(httpClient, requestGenerator, null, scenarios, guid, logger);

        ExecutionHelper.getDetails(httpClient, requestGenerator, null, scenarios, guid, logger);

        final ReserveVerificationInput reserveVerificationInput = ExecutionHelper.reserve(httpClient, requestGenerator, guid);
        ExecutionHelper.reserveVerify(reserveVerificationInput, null, scenarios, guid, logger);

        ExecutionHelper.cancel(httpClient, requestGenerator, guid);
    }
}