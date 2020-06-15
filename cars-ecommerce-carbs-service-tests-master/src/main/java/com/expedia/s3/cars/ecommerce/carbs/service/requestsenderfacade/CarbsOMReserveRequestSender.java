package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;


import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.OmReserveVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.CreateRecordRequest;
import expedia.om.supply.messages.defn.v1.CreateRecordResponseType;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.RollbackPreparePurchaseResponseType;
import org.eclipse.jetty.client.HttpClient;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by fehu on 8/30/2016.
 */
@SuppressWarnings("PMD")
public class CarbsOMReserveRequestSender
{
    public static CarbsOMReserveReqAndRespGenerator sendShopMsgForOMSReserve(TestScenario testScenario,
                                                                             String tuid, String guid, HttpClient httpClient, boolean needSendDetails) throws Exception
    {
        TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);

        //getdetails
        if (needSendDetails)
        {
            CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
            CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
            CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, testScenario, getDetailsRequestType, getDetailsResponseType);
        }

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, testScenario, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //OMReserve
        return carOMSReqAndRespObj;
    }

    public static CarbsOMReserveReqAndRespGenerator sendShopMsgForOMSReserve(TestData testData, boolean needSendDetails) throws Exception

    {
        //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);

        //getdetails
        if (needSendDetails)
        {
            CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
            CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
            CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);
        }

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse =
                CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(),
                getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //OMReserve
        return carOMSReqAndRespObj;
    }

    public static CarbsOMReserveReqAndRespGenerator oMSReserveSendWithShopMsg(TestScenario testScenario, DataSource dataSource,
                                                                              String tuid, String guid, HttpClient httpClient, SpooferTransport spooferTransport) throws Exception
    {
        TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, request);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, testScenario, getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, testScenario, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //OMReserve
        return oMSReserveSend(carOMSReqAndRespObj, testData);
    }

    public static CarbsOMReserveReqAndRespGenerator oMSReserveSendWithShopMsg(TestData testData) throws Exception
    {

        //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        if (testData.getClientCode() != null)
        {
            request.setClientCode(testData.getClientCode());
        }
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(), getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());
        //Set getDetails request and response in case we need them for verification
        carOMSReqAndRespObj.setGetDetailsRequestType(getDetailsRequestType);
        carOMSReqAndRespObj.setGetDetailsResponseType(getDetailsResponseType);

        //OMReserve
        return oMSReserveSend(carOMSReqAndRespObj, testData);
    }

    public static CarbsOMReserveReqAndRespGenerator oMSReserveSendWithShopMsgByBusinessModelIDAndServiceProviderID(TestData testData) throws Exception
    {
        //search
        CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        //getdetails
        CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(carProductType);
        CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.
                createCarbsDetailsRequestByBusinessModelIDAndServiceProviderID(testData);
        CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse
                (testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(),
                getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.
                createCarbsCostAndAvailRequestByBusinessModelIDAndServiceProviderID(testData);
        CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.
                getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(),
                getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        //OMReserve
        return oMSReserveSend(carOMSReqAndRespObj, testData);
    }

    public static CarbsOMReserveReqAndRespGenerator oMSReserveSend(CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj,
                                                                   TestData testData) throws Exception
    {

        //  Send a reserve(1.GetOrderProcess -> 2.Create record -> 3.Prepare purchase -> 4.Commit purchase or Rollback purchase) request and verify if no error exist in response.
        //  1.Build and send GetOrderProcess request to CarBS (Verify if the product can be reserved).
        CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);


        //2.Create record
        CarBSCreateRecordSend(testData, carOMSReqAndRespObj);

        try
        {
            //	3.Build and send PreparePurchaseRequest request to CarBS (Associate Product with bookingID and insert related record into BookingDB).

            CarBSPreparePurchaseSend(testData, carOMSReqAndRespObj);
            //	4.Build and send CommitPreparePurchaseRequest request to CarBS (Commit the booking).
            CarBSCommitPreparePurchaseSend(testData, carOMSReqAndRespObj);

        } catch (Exception e)
        {
            //	5.Build and send RollBackPreparePurchaseRequest request to CarBS (Cancel the booking before commit book successfully).
            CarBSRollbackPreparePurchaseSend(testData, carOMSReqAndRespObj);
            throw new Exception(e);
        }

        return carOMSReqAndRespObj;
    }

    public static void CarBSGetOrderProcessSend(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException, SQLException
    {
        // Build and send GetOrderProcess request to CarBS (Verify if the product can be reserved).
        GetOrderProcessRequest getOrderProcessRequest = omRequestGenerater.createGetOrderProcessRequest(testData);
        GetOrderProcessResponseType getOrderProcessResponse = CarbsOMServiceSender.sendGetOrderProcessResponse(testData.getGuid(), testData.getHttpClient(), getOrderProcessRequest);
        omRequestGenerater.setGetOrderProcessRequestType(getOrderProcessRequest);
        omRequestGenerater.setGetOrderProcessResponseType(getOrderProcessResponse);
        OmReserveVerifier.isGetOrderProcessWorksVerifier(testData.getGuid(), testData.getScenarios(), getOrderProcessRequest, getOrderProcessResponse);

        if (testData.isRegression())
        {
            OmReserveVerifier.getOrderRegressionVerifier(testData, omRequestGenerater);
        }
    }

    public static void CarBSCreateRecordSend(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws IOException, DataAccessException
    {

        CreateRecordRequest createRecordRequest = omRequestGenerater.createCreateRecordRequest(testData);
        CreateRecordResponseType createRecordResponse = CarbsOMServiceSender.sendCreateRecordResponse(testData.getGuid(), testData.getHttpClient(), createRecordRequest);
        omRequestGenerater.setCreateRecordRequestType(createRecordRequest);
        omRequestGenerater.setCreateRecordResponseType(createRecordResponse);
        OmReserveVerifier.isCreateRecordWorksVerifier(testData.getGuid(), testData.getScenarios(), createRecordRequest, createRecordResponse);

    }

    public static void CarBSPreparePurchaseSend(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws Exception
    {
        PreparePurchaseRequest preparePurchaseRequest = omRequestGenerater.createPreparePurchaseRequest(testData);
        ////
        preparePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().setCarProductToken(null);

        ///
        PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), preparePurchaseRequest);
        omRequestGenerater.setPreparePurchaseRequestType(preparePurchaseRequest);
        omRequestGenerater.setPreparePurchaseResponseType(preparePurchaseResponse);
        OmReserveVerifier.isPreparePurchaseWorksVerifier(testData.getGuid(), testData.getScenarios(), preparePurchaseRequest, preparePurchaseResponse);
        if (testData.isRegression())
        {
            OmReserveVerifier.preparePurchaseRegressionVerifier(testData, omRequestGenerater);
        }
    }

    public static String CarBSCommitPreparePurchaseSend(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws Exception
    {
        String errorMsg = "";
        CommitPreparePurchaseRequest commitPreparePurchaseRequest = omRequestGenerater.createCommitPreparePurchaseRequest(testData);

        CommitPreparePurchaseResponseType commitPreparepPurchaseResponse = CarbsOMServiceSender.sendCommitPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), commitPreparePurchaseRequest);
        omRequestGenerater.setCommitPreparePurchaseRequestType(commitPreparePurchaseRequest);
        omRequestGenerater.setCommitPreparePurchaseResponseType(commitPreparepPurchaseResponse);
        OmReserveVerifier.isCommitPreparePurchaseWorksVerifier(testData.getGuid(), testData.getScenarios(), commitPreparePurchaseRequest, commitPreparepPurchaseResponse);

        return errorMsg;
    }

    public static String CarBSRollbackPreparePurchaseSend(TestData testData, CarbsOMReserveReqAndRespGenerator omRequestGenerater) throws Exception
    {
        String errorMsg = "";
        RollbackPreparePurchaseRequest rollbackPreparePurchaseRequest = omRequestGenerater.createRollbackPreparePurchaseRequest(testData);
        RollbackPreparePurchaseResponseType rollbackPreparePurchaseResponse = CarbsOMServiceSender.sendRollbackPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), rollbackPreparePurchaseRequest);
        omRequestGenerater.setRollbackPreparePurchaseRequestType(rollbackPreparePurchaseRequest);
        omRequestGenerater.setRollbackPreparePurchaseResponseType(rollbackPreparePurchaseResponse);
        OmReserveVerifier.isRollbackPreparePurchaseWorksVerifier(testData.getGuid(), testData.getScenarios(), rollbackPreparePurchaseRequest, rollbackPreparePurchaseResponse);

        return errorMsg;
    }
}