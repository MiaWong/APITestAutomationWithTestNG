package com.expedia.www.cars.bugatti.service.tests.bvt;

import com.expedia.www.cars.bugatti.service.tests.requestgenerators.SearchRequestGenerator;
import com.expedia.www.cars.bugatti.service.tests.requestsender.BugattiRequestSender;
import com.expedia.www.cars.bugatti.service.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by miawang on 3/21/2017.
 */
public class BugattiSearchBVT
{
    Logger logger = Logger.getLogger(getClass());
    final private HttpClient httpClient = new HttpClient(new SslContextFactory(true));

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception
    {
        httpClient.start();
    }

    @AfterClass
    public void tearDown() throws Exception
    {
        httpClient.stop();
    }

    //Send search request to Bugatti for package car - US POS/HC package/US location/Weekly
    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void tfs_1079332_BVT_Test_Standalone_RoundTrip_USPOS_USPickup()
    {
        testSearch(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void tfs_1079332_BVT_Test_Standalone_Oneway_USPOS_USPickup()
    {
        testSearch(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_SFO_OnAirport.getTestScenario(), PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    @SuppressWarnings("PMD.MethodNamingConventions")
    public void tfs_1079332_BVT_Test_Package_RoundTrip_USPOS_USPickup()
    {
        testSearch(CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario(), PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void testStandaloneRoundTripDefaultDriverAge()
    {
        final TestScenario scenarios = CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        final String guid = PojoXmlUtil.getRandomGuid();
        final CarSearchRequest request = generateSearchRequest(scenarios, guid);
        final CarSearchResponse response = sendSearchRequest(request, guid);

        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchResponseVerifier.verifyCarProductReturned(response);
        searchResponseVerifier.verifyDriverAgeInCarProduct(request, response);
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void testStandaloneRoundTripYoungDriverAge()
    {
        final TestScenario scenarios = CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        final String guid = PojoXmlUtil.getRandomGuid();
        final CarSearchRequest request = generateSearchRequest(scenarios, guid, 22l);
        final CarSearchResponse response = sendSearchRequest(request, guid);

        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchResponseVerifier.verifyCarProductReturned(response);
        searchResponseVerifier.verifyDriverAgeInCarProduct(request, response);
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void testStandaloneRoundTripAdditionalFees()
    {
        final TestScenario scenarios = CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        final String guid = PojoXmlUtil.getRandomGuid();
        // additional fees are returned for young driver
        final CarSearchRequest request = generateSearchRequest(scenarios, guid, 22l);
        final CarSearchResponse response = sendSearchRequest(request, guid);

        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchResponseVerifier.verifyCarProductReturned(response);
        searchResponseVerifier.verifyDriverAgeInCarProduct(request, response);
        searchResponseVerifier.verifyAdditionalFeesInCarProduct(response);
    }

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void testStandaloneRoundTripCoveragesCosts()
    {
        final TestScenario scenarios = CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        final String guid = PojoXmlUtil.getRandomGuid();
        // additional fees are returned for young driver
        final CarSearchRequest request = generateSearchRequest(scenarios, guid, 22l);
        final CarSearchResponse response = sendSearchRequest(request, guid);

        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchResponseVerifier.verifyCarProductReturned(response);
        searchResponseVerifier.verifyDriverAgeInCarProduct(request, response);
        searchResponseVerifier.verifyCoveragesCostInCarProduct(response);
    }

    private void testSearch(TestScenario scenarios, String guid)
    {
        final CarSearchResponse response = sendSearchRequest(scenarios, guid);

        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchResponseVerifier.verifyCarProductReturned(response);
        searchResponseVerifier.verifyPackageInfoAndCDCodesInCarProduct(response);
    }

    private CarSearchRequest generateSearchRequest(TestScenario scenarios, String guid)
    {
        final SearchRequestGenerator searchRequestGenerator = new SearchRequestGenerator();
        final CarSearchRequest request = searchRequestGenerator.createSearchRequest(scenarios, guid);
        logger.warn("request xml ==================>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));

        return request;
    }

    private CarSearchRequest generateSearchRequest(TestScenario scenarios, String guid, Long driverAgeYearCount)
    {
        final SearchRequestGenerator searchRequestGenerator = new SearchRequestGenerator();
        final CarSearchRequest request = searchRequestGenerator.createSearchRequest(scenarios, guid, driverAgeYearCount);
        logger.warn("request xml ==================>" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(request)));

        return request;
    }

    private CarSearchResponse sendSearchRequest(TestScenario scenarios, String guid)
    {
        //1,search
        final CarSearchRequest request = generateSearchRequest(scenarios, guid);

        return sendSearchRequest(request, guid);
    }

    private CarSearchResponse sendSearchRequest(CarSearchRequest request, String guid)
    {
        final BugattiRequestSender requestSender = new BugattiRequestSender();
        final CarSearchResponse response = requestSender.getSearchResponse(guid, httpClient, request);
        logger.warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(response)));

        return response;
    }
}