package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt;

import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators.LocationSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestGenerators.TestScenarios;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.LocationSearchResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.location.search.defn.v1.CarSupplyConnectivityLocationSearchResponseType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by alibadisos on 10/7/16.
 */
public class LocationSearch extends SuiteCommon {
    final private LocationSearchRequestGenerator locationSearchRequestGenerator = new LocationSearchRequestGenerator();;

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_232976_LocationSearch_BVT_Test() throws IOException {
        testLocationSearch(TestScenarios.CDG_LOCATION_SEARCH, "232976", PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_232993_LocationLatLongSearch_BVT_Test() throws IOException
    {
        testLocationLatLongSearch(TestScenarios.HERTZ_LOCATION_SUPPLIER_SEARCH, "232993", PojoXmlUtil.getRandomGuid());
    }

    private void testLocationSearch(TestScenarios scenarios, String tuid, String guid) {
        //1,search
        CarSupplyConnectivityLocationSearchRequestType request =
                locationSearchRequestGenerator.createLocationSearchRequest(scenarios, tuid, null);
        CarSupplyConnectivityLocationSearchResponseType response =
                AmadeusSCSRequestSender.getSCSLocationSearchResponse(guid, httpClient, request);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
            logger.debug("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        }
        LocationSearchResponseVerifier.verifyCarLocationReturned(request, response);
    }

    private void testLocationLatLongSearch(TestScenarios scenarios, String tuid, String guid)
    {
        //1,search
        CarSupplyConnectivityLocationSearchRequestType request =
                locationSearchRequestGenerator.createLocationLatLongSearchRequest(scenarios, tuid, 41.38183, 2.15112, 5, "MI");
        if (logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
        }
        CarSupplyConnectivityLocationSearchResponseType response =
                AmadeusSCSRequestSender.getSCSLocationSearchResponse(guid, httpClient, request);
        if (logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));
        }
        LocationSearchResponseVerifier.verifyCarLocationReturned(request, response);
    }
}