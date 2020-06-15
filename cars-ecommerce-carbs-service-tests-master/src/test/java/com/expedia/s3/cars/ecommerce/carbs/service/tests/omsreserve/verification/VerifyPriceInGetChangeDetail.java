package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.e3.data.financetypes.defn.v4.PriceListType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import org.testng.Assert;

public class VerifyPriceInGetChangeDetail {

    private VerifyPriceInGetChangeDetail(){}

    public static void totalPriceForOMSGetChangeDetailVerifier(String actPrice, String actCurrencyCode, CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator) {

        final CarCatalogKeyType carCatalogKey = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey().getCarCatalogKey();

        final CarProductType selectedCar =  selectCarByVendorSupplierIDAndCarVehicle(carbsOMReserveReqAndRespGenerator.getSearchResponseType(), carCatalogKey);

        if (null != selectedCar)
        {
            verifyTotalPriceInPriceList(selectedCar.getPriceList(), actPrice, actCurrencyCode, "SearchResponse");

        }
        verifyTotalPriceInPriceList(carbsOMReserveReqAndRespGenerator.getGetCostAndAvailabilityResponseType().getCarProductList().getCarProduct().get(0).getPriceList(), actPrice, actCurrencyCode, "CostAndAvailability");
        verifyTotalPriceInPriceList(carbsOMReserveReqAndRespGenerator.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getPriceList(), actPrice, actCurrencyCode, "GetOrderProcess");
        verifyTotalPriceInPriceList(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getPriceList(), actPrice, actCurrencyCode, "PreparePurchase");
    }

    private static void verifyTotalPriceInPriceList(PriceListType expPriceList, String actualPrice, String actualCurrencyCode, String type) {
        final double expectedPrice = CostPriceCalculator.getPosTotalPrice(expPriceList, actualCurrencyCode, true);
        final String finalExpPrice = String.format("%.2f", expectedPrice);
        if (!actualPrice.equals(finalExpPrice))
        {
            Assert.fail("Compare"+type +":The actual value in total price= " + actualPrice + " is not equal the expected value= " + finalExpPrice);
        }
    }

    private static CarProductType selectCarByVendorSupplierIDAndCarVehicle(CarECommerceSearchResponseType searchRsp, CarCatalogKeyType carCatalogKey)
    {
        CarProductType selectedCar = null;

        for (final CarSearchResultType carSearchResult : searchRsp.getCarSearchResultList().getCarSearchResult())
        {
            for (final CarProductType carProduct : carSearchResult.getCarProductList().getCarProduct())
            {
                if (CarNodeComparator.isVendorIdAndSIPPEqual(carCatalogKey, carProduct.getCarInventoryKey().getCarCatalogKey(),new StringBuilder()))
                {
                    selectedCar = carProduct;
                    break;
                }
            }
        }
        return selectedCar;
    }
}
