package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

/**
 * Created by fehu on 3/17/2017.
 */
public class OMShopToBookingBVT  extends SuiteCommon{

    //BVT_Expweb ClientID=1  CarBS level Agency
    @Test(groups = {"bvt"})
    public void casss518048OMSAgencyExpwebTestFromShoppingpathToBookingPath() throws Exception {
        testOMSFromShoppingPathToReserveAndCancel(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "518048", "1FX936");
    }

    //BVT_Expweb ClientID=1  CarBS level GDSP
    @Test(groups = {"bvt"})
    public void casss518049OMSGDSPExpwebTestFromShoppingpathToBookingPath() throws Exception {
        testOMSFromShoppingToReserveAndCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario(),
                "518049", "1FX936");
    }

    //147421: Verify success message returned in response if commit purchase is successful for worldspan GDSP cars on US site.
    @Test(groups = {"bvt"})
    public void casss147421SuccessMessageCommitPurchaseGDSPUS() throws Exception {
        testOMSFromShoppingPathToReserveAndCancel(CommonScenarios.Worldspan_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario(),
                "147421", null);

    }
    @Test(groups = {"bvt"})
    public void casss518061OMSAgencyEgenciaTestFromShoppingpathToBookingPath() throws Exception {
        testOMSFromShoppingPathToReserveAndCancel(CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario(),
                "518061", "W0DFCJ");
    }
    
    private void testOMSFromShoppingPathToReserveAndCancel(TestScenario scenarios, String tuid, String clientCode) throws Exception {

        final TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        testData.setClientCode(clientCode);

        //booking
        final  CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (testData);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }

    private void testOMSFromShoppingToReserveAndCancel(TestScenario scenarios, String tuid, String clientCode) throws Exception {

        //search
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(scenarios, tuid);
        if (clientCode != null)
        {
            request.setClientCode(clientCode);
        }
        final String guid = PojoXmlUtil.getRandomGuid();
        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        final CarProductType selectCar = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(selectCar);
        final  CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, scenarios, getDetailsRequestType, getDetailsResponseType);
        carbsSearchRequestGenerator.setSelectedCarProduct(getDetailsResponseType.getCarProductList().getCarProduct().get(0));

        //getCostAndAvail
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final  CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, scenarios, getCostAndAvailRequestType, getCostAndAvailResponse);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //OMReserve
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }
}
