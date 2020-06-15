package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
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
public class CarBSSearchBVTTest extends SuiteCommon{


    @Test(groups = {TestGroup.BVT, TestGroup.SHOPPING_BVT}, priority = 0)
    public void casss10001SearchSanityTest() throws IOException, DataAccessException {
        testCarbsSearch(CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario(), "10001");

    }

    //Carbs_Sabre
    @Test(groups = {TestGroup.BVT})
    public void casss1091050testCarBSSearchOnewayGDSP() throws IOException, DataAccessException {
        testCarbsSearch(CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YOW.getTestScenario(), "1091050");
    }

    //Carbs_Sabre
    @Test(groups = {TestGroup.BVT})
    public void casss1091051testCarBSSearchRoundtripGDSP() throws IOException, DataAccessException {
        testCarbsSearch(CommonScenarios.Sabre_CAN_Standalone_Oneway_OnAirport_YYR.getTestScenario(), "1091051");
    }

    private void testCarbsSearch(TestScenario scenarios, String tuid) throws IOException, DataAccessException {
        final  TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(),httpClient,request);
         //verification
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

    }

}
