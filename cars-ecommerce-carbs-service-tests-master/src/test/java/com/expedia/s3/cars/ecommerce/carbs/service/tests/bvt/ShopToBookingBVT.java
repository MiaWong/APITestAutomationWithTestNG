package com.expedia.s3.cars.ecommerce.carbs.service.tests.bvt;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenaratorFromSample;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel.CarBSCancelVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.reserve.CarBSReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveRequestType;
import com.expedia.s3.cars.ecommerce.messages.reserve.defn.v4.CarECommerceReserveResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by fehu on 3/16/2017.
 */
public class ShopToBookingBVT  extends SuiteCommon {


    //Emain ClientID=7 CarBS level Agency
    @Test(groups = {"bvt"})
    public void casss518058CarBSMerchantEmainTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
        testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "518058" ,"ZCS52L");

    }

    //Emain ClientID=7 CarBS level GDSP
    @Test(groups = {"bvt"})
    public void casss518059CarBSGDSPExpwebTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
        testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario(),
                "518059","ZCS52L") ;
    }

    //Expweb ClientID=1: Verify CarBS Search,GetDetails,CostAndAvail,Reserve,GetReservation and Cancel works for Ageny car
    @Test(groups = {"bvt"})
    public void casss518040CarBSAgencyExpwebTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
        testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "518040","1FX936");
    }

    //Expweb ClientID=1 GDSP
    @Test(groups = {"bvt"})
    public void casss518042CarBSGDSPExpwebTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
         testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario(),
                "518042", "1FX936");
    }


     //Trip ClientID=5  CarBS level Agency
     @Test(groups = {"bvt"})
    public void casss518101CarBSAgencyTripTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
        testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),
                "518101", "0Q7XRN");
    }

    //Trip ClientID=5  CarBS level GDSP
    @Test(groups = {"bvt"})
    public void casss518103CarBSGDSPTripTestFromShoppingpathToBookingPath() throws IOException, DataAccessException {
        testCarBSFromShoppingPathToGetReservationAndCancel(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OffAirport.getTestScenario(),
                "518103", "0Q7XRN");
    }

    private void testCarBSFromShoppingPathToGetReservationAndCancel(TestScenario scenarios, String tuid, String clientCode) throws IOException, DataAccessException {
        final  TestData testData = new TestData(httpClient, scenarios, tuid, PojoXmlUtil.getRandomGuid());
        final CarECommerceSearchRequestType request = CarbsRequestGenaratorFromSample.createCarbsSearchRequest(testData.getScenarios(), tuid);
        request.setClientCode(clientCode);
        final  CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(),httpClient,request);
        //verification
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request,response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(),httpClient,getDetailsRequestType);
        //verification
       CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), scenarios, getDetailsRequestType, getDetailsResponseType);

        //getCostandAvail
        final  CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), scenarios, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);
        carbsSearchRequestGenerator.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);

        //firstReserve
        final CarECommerceReserveRequestType firstReserveRequestType = carbsSearchRequestGenerator.createCarbsFirstReserveRequest();
        final  CarECommerceReserveResponseType firstServeResponseType = CarbsRequestSender.getCarbsReserveResponse(testData.getGuid(), httpClient, firstReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(testData.getGuid(), scenarios, firstReserveRequestType, firstServeResponseType);
        //secondReserve
        carbsSearchRequestGenerator.setReserveResponseType(firstServeResponseType);
        final CarECommerceReserveRequestType secondReserveRequestType = carbsSearchRequestGenerator.createCarbsSecondReserveRequest(firstServeResponseType);
        final  CarECommerceReserveResponseType secondReserveResponseType = CarbsRequestSender.getCarbsReserveResponse(testData.getGuid(), httpClient, secondReserveRequestType);
        CarBSReserveVerifier.isCarbsReserveWorksVerifier(testData.getGuid(), scenarios, secondReserveRequestType, secondReserveResponseType);
        carbsSearchRequestGenerator.setReserveResponseType(secondReserveResponseType);
        //cancel
        final CarECommerceCancelRequestType cancelRequestType = carbsSearchRequestGenerator.createCarbsCancelRequest();
        final  CarECommerceCancelResponseType cancelResponseType = CarbsRequestSender.getCarbsCancelResponse(testData.getGuid(), httpClient, cancelRequestType);
        CarBSCancelVerifier.isCarbsCancelWorksVerifier(testData.getGuid(), scenarios, cancelRequestType, cancelResponseType);


    }

}
