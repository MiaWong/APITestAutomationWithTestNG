package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by miawang on 8/17/2016.
 */
public class EndToEnd extends SuiteCommon {

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void tfs_518021_EndToEnd_BVT_Test() throws Exception {
        final TestScenario wscsTS = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();

        final String randomGuid = UUID.randomUUID().toString();
        final String tuid = "518021";

        final SearchVerificationInput searchVerificationInput = ExecutionHelper.bvtSearch(httpClient, wscsTS, tuid, randomGuid);
        ExecutionHelper.searchVerification(searchVerificationInput, null, wscsTS, randomGuid, logger);

        logger.warn("search request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.warn("search response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        final GetCostAndAvailabilityVerificationInput costAndAvailabilityVerificationInput =
                ExecutionHelper.getCostAndAvailability(httpClient, requestGenerator, null, wscsTS, randomGuid, logger);
        logger.warn("costAndAvailabilityVerificationInput request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getRequest())));
        logger.warn("costAndAvailabilityVerificationInput response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailabilityVerificationInput.getResponse())));

        ExecutionHelper.getCostAndAvailabilityVerification(costAndAvailabilityVerificationInput, null, wscsTS, randomGuid, logger);

        final GetDetailsVerificationInput getDetailsVerificationInput =
                ExecutionHelper.getDetails(httpClient, requestGenerator, null, wscsTS, randomGuid, logger);
        logger.warn("getDetailsVerificationInput request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.warn("getDetailsVerificationInput response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        ExecutionHelper.getDetailsVerification(getDetailsVerificationInput, null, wscsTS, randomGuid, logger);

        final ReserveVerificationInput reserveVerificationInput = ExecutionHelper.reserve(httpClient, requestGenerator, null, wscsTS, randomGuid, logger);
        logger.warn("reserveVerificationInput request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
        logger.warn("reserveVerificationInput response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        ExecutionHelper.reserveVerify(reserveVerificationInput, null, wscsTS, randomGuid, logger);

        final GetReservationVerificationInput getReservationVerificationInput = ExecutionHelper.getReservation(
                httpClient, requestGenerator, randomGuid);
        logger.warn("getReservationVerificationInput request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        logger.warn("getReservationVerificationInput response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));

        ExecutionHelper.getreservationVerify(getReservationVerificationInput, null, wscsTS, randomGuid, logger);

        final CancelVerificationInput cancelVerificationInput = ExecutionHelper.cancel(httpClient, requestGenerator, randomGuid);
        logger.warn("cancelVerificationInput request xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getRequest())));
        logger.warn("cancelVerificationInput response xml ===>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(cancelVerificationInput.getResponse())));

        ExecutionHelper.cancelVerify(cancelVerificationInput, null, wscsTS, randomGuid, logger);
    }
}