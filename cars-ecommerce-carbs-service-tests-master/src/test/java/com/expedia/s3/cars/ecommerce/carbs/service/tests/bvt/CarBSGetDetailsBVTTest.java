package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by fehu on 8/19/2016.
 */
public class CarBSGetDetailsBVTTest extends SuiteCommon{

    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void casss100011getDetailSanityTest() throws IOException, DataAccessException {
        final String randomGuid= PojoXmlUtil.getRandomGuid();
        testCarbsGetdetails(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "100011", randomGuid);

}

    private void testCarbsGetdetails(TestScenario scenarios, String tuid,String guid) throws IOException, DataAccessException {
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        //search
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid,httpClient,request);
        //verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response, testData);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid,httpClient,getDetailsRequestType);
        //verification
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, scenarios, getDetailsRequestType, getDetailsResponseType);
    }

    @Test(groups = {TestGroup.BVT})
    public void casss1091052testCarBSGetDetailsOnewayGDSP() throws IOException, DataAccessException {
        testCarbsGetdetails(CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario(), "1091052", PojoXmlUtil.getRandomGuid());
    }

    @Test(groups = {TestGroup.BVT})
    public void  casss1091053testCarBSGetDetailsRoundTripGDSP() throws IOException, DataAccessException {
        testCarbsGetdetails(CommonScenarios.Sabre_CAN_Standalone_RoundTrip_OFFAirport_YYC.getTestScenario(), "1091053", PojoXmlUtil.getRandomGuid());
    }

}
