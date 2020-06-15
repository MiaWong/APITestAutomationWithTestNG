package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;


/**
 * Created by mpaudel on 6/30/16.
 */
public class GetCostAndAvail extends SuiteCommon
{
    Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void costAvailBVTTestStandaloneRoundTrip() throws Exception {
        testCostAvail(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "70003");
    }


    private void testCostAvail(TestScenario scenarios, String tuid) throws Exception {
        //1,search
        final SearchVerificationInput searchVerificationInput = SearchHelper.bvtSearch(httpClient, scenarios, tuid);
        SearchHelper.searchVerification(searchVerificationInput, null, scenarios, PojoXmlUtil.getRandomGuid(), logger, false);
        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        //logger.warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        //2. Generate costAvail request with a random product from search Response
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final GetCostAndAvailabilityVerificationInput costAvailVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCostAndAvailRequest(), PojoXmlUtil.getRandomGuid());
        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAvailVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAvailVerificationInput.getResponse())));
        CostAndAvailHelper.getCostAndAvailabilityVerification(costAvailVerificationInput, null, scenarios, PojoXmlUtil.getRandomGuid(), logger, false);




    }

}
