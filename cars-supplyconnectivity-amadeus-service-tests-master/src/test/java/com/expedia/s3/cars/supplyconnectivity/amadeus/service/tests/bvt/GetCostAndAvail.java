package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.bvt;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSBvtSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.requestSender.AmadeusSCSRequestSender;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.GetCostAndAvailResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 8/4/2016.
 */
public class GetCostAndAvail extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_182825_CostAndAvail_BVT_Test() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        testCostAndAvail(CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(), "182825", PojoXmlUtil.getRandomGuid());
    }

    private void testCostAndAvail(TestScenario scenarios, String tuid, String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
        //1,search
        SCSBvtSearchRequestGenerator bvtSearchRequestGenerator = new SCSBvtSearchRequestGenerator();
        CarSupplyConnectivitySearchRequestType request = bvtSearchRequestGenerator.createSearchRequest(scenarios, tuid,
                "11684", SettingsProvider.BVTTEST_OFFAIRPORTLOCATIONLIST, SettingsProvider.BVTTEST_VENDORLIST);
        CarSupplyConnectivitySearchResponseType searchResponse = AmadeusSCSRequestSender.getSCSSearchResponse(guid, httpClient, request);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("searchrequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));
            logger.debug("searchResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchResponse)));
        }

        //2,Generate Details request with a random product from search Response
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(request, searchResponse);
        CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest = requestGenerator.createCostAndAvailRequest();
        CarSupplyConnectivityGetCostAndAvailabilityResponseType costAndAvailResponse = AmadeusSCSRequestSender.getSCSGetCostAndAvailabilityResponse(guid, httpClient, costAndAvailRequest);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("costAndAvailRequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailRequest)));
            logger.debug("costAndAvailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(costAndAvailResponse)));
        }
        GetCostAndAvailResponseVerifier.isGetCostAndAvailWorksVerifier(costAndAvailResponse);
    }
}