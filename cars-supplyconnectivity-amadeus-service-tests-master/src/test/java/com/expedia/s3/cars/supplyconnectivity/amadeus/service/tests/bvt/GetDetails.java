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
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.GetDetailsResponseVerifier;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.apache.log4j.Priority;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by mpaudel on 6/30/16.
 */
public class GetDetails extends SuiteCommon {

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void tfs_185113_Details_BVT_Test() throws IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, DataAccessException {
        testDetails(CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(), "185113", PojoXmlUtil.getRandomGuid());
    }


    private void testDetails(TestScenario scenarios, String tuid, String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException {
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

        //2. Generate Details request with a random product from search Response
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(request, searchResponse);
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        CarSupplyConnectivityGetDetailsResponseType detailsResponse = AmadeusSCSRequestSender.getSCSDetailsResponse(guid, httpClient, detailsRequest);
        if(logger.isEnabledFor(Priority.DEBUG))
        {
            logger.debug("detalirequest: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsRequest)));
            logger.debug("detailResponse: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(detailsResponse)));
        }

        GetDetailsResponseVerifier.isGetDetailslWorksVerifier(detailsResponse);
    }
}