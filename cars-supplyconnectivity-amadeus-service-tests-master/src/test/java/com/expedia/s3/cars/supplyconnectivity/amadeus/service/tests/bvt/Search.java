package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSBvtSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;


/**
 * Created by mpaudel on 5/18/16.
 */

public class Search extends SuiteCommon {

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_161811_Search_BVT_Test() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        testSearch(CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(), "161811", PojoXmlUtil.getRandomGuid());
    }

    private void testSearch(TestScenario scenarios, String tuid, String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        //1,search
        SCSBvtSearchRequestGenerator bvtSearchRequestGenerator = new SCSBvtSearchRequestGenerator();
        CarSupplyConnectivitySearchRequestType request = bvtSearchRequestGenerator.createSearchRequest(scenarios, tuid,
                "11684", SettingsProvider.BVTTEST_OFFAIRPORTLOCATIONLIST, SettingsProvider.BVTTEST_VENDORLIST);
        CarSupplyConnectivitySearchResponseType response = AmadeusSCSRequestSender.getSCSSearchResponse(guid, httpClient, request);

        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("request xml ==================>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
            logger.debug("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        }
        SearchResponseVerifier.verifyCarProductReturned(response);
    }
}
