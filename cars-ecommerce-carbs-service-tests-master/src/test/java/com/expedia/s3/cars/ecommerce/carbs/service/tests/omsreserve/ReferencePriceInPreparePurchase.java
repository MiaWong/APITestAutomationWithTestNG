package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.*;
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
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 11/19/2018.
 */
@SuppressWarnings("PMD")
public class ReferencePriceInPreparePurchase extends SuiteCommon {

    //
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs1026142MNReferencePriceInPreparePurchase() throws Exception {
        final TestScenario testScenario = CommonScenarios.MicronNexus_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, testScenario, "1026142", guid);
        testReferencePrice(testData);

    }

    private void testReferencePrice(TestData testData) throws Exception {
        //Search
        final CarECommerceSearchRequestType request = CarbsSearchRequestGenerator.createCarbsSearchRequest(testData);
        final CarECommerceSearchResponseType response = CarbsRequestSender.getCarbsSearchResponse(testData.getGuid(), testData.getHttpClient(), request);
        CarProductType selectCarProduct = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, request, response);

        if (null != selectCarProduct && (null == selectCarProduct.getTotalReferencePrice() || (null != selectCarProduct.getTotalReferencePrice() && selectCarProduct.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() == 0))) {
            //select the carProduct that contains TotalReferencePrice node
            for (CarSearchResultType carSearchResultType : response.getCarSearchResultList().getCarSearchResult()) {
                List<CarProductType> carProductTypeTemps = carSearchResultType.getCarProductList().getCarProduct();
                for (CarProductType carProductType : carProductTypeTemps) {
                    if ((null != carProductType.getTotalReferencePrice() && carProductType.getTotalReferencePrice().getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() > 0) && !testData.getScenarios().getPurchaseType().equals(PurchaseType.CarOnly)) {
                        selectCarProduct = carProductType;
                        break;
                    }
                }
                break;
            }
        }

        //getdetails
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(request, response, testData);
        carbsSearchRequestGenerator.setSelectedCarProduct(selectCarProduct);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(testData.getGuid(), testData.getScenarios(), getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        final CarECommerceGetCostAndAvailabilityRequestType costAvailRequest = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType costAvailResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAvailRequest);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(testData.getGuid(), testData.getScenarios(), costAvailRequest, costAvailResponse);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(request, response);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(costAvailResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());
        //Set getDetails request and response in case we need them for verification
        carOMSReqAndRespObj.setGetDetailsRequestType(getDetailsRequestType);
        carOMSReqAndRespObj.setGetDetailsResponseType(getDetailsResponseType);
        //OMS reserve
        CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);

        //Verify reference price is same between PrepaurePurchase and getDetails
        final StringBuilder erroMsg = new StringBuilder();
        final boolean compared = CompareUtil.compareObject(getDetailsResponseType.getCarProductList().getCarProduct().get(0).getReferencePriceList(),
                carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct()
                .getReferencePriceList(),new ArrayList<>(), erroMsg);
        if (!compared) {
            Assert.fail(String.format("ReferencePriceList in PreparePurchase response is not same as details response, compare error: %s!", erroMsg.toString()));
        }

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

    }

}
