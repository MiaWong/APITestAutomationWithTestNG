package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.GetReservationHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;


/**
 * Created by mpaudel on 6/30/16.
 */
public class GetReservation extends SuiteCommon
{
    Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.BVT}, priority = 0)
    public void getReservationBVTTestStandaloneRoundtrip() throws Exception {
        testGetReservation(CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "70005");
    }


    private void testGetReservation(TestScenario scenarios, String tuid) throws Exception {
        //1,search
        final String guid = PojoXmlUtil.getRandomGuid();
        final SearchVerificationInput searchVerificationInput = SearchHelper.bvtSearch(httpClient, scenarios, tuid);
        SearchHelper.searchVerification(searchVerificationInput, null, scenarios, guid, logger, false);

        //2. Reserve
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final ReserveVerificationInput reserveVerificationInput = ReserveHelper.reserve(httpClient, requestGenerator, guid, false);
        ReserveHelper.reserveVerify(reserveVerificationInput, null, scenarios, guid, false, logger, false);

        //3.getReservation
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationHelper.retrieveReservation(httpClient, requestGenerator, guid, false);
        GetReservationHelper.getreservationVerify(getReservationVerificationInput, null, scenarios, guid, false, logger, false);

        //4.cancel
        final CancelVerificationInput cancelVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, requestGenerator.createCancelRequest(), guid);
        CancelHelper.cancelVerify(cancelVerificationInput, null, scenarios, guid, false, logger, false);




    }

}
