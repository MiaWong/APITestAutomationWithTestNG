package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.SupportLocalGetDeatils;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class VerifyLocationManagementCase {

    private VerifyLocationManagementCase()
    {

    }

    public static void verifyAllResponse(CarbsOMRetrieveReqAndRespGenerator retrieveReqAndRespGenerator) throws DataAccessException
    {
        final GetOrderProcessResponseType getOrderProcessResponse = retrieveReqAndRespGenerator.getCarbsOMReserveReqAndRespGenerator().getGetOrderProcessResponseType();
        final PreparePurchaseResponseType preparePurchaseResponse = retrieveReqAndRespGenerator.getCarbsOMReserveReqAndRespGenerator().getPreparePurchaseResponseType();
        final RetrieveResponseType retrieveResponse = retrieveReqAndRespGenerator.getRetrieveResponseType();
        final CarProductType selectedCarProduct = retrieveReqAndRespGenerator.getCarbsOMReserveReqAndRespGenerator().getSelectCarProduct();

        final CarInventoryKeyType carInventoryKeyInGOP = getOrderProcessResponse.getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey();
        final CarInventoryKeyType carInventoryKeyInPP = preparePurchaseResponse.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey();
        final CarInventoryKeyType carInventoryKeyInRetrieve = retrieveResponse.getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey();
        locationManagementForOMSGetReservationVerifier(carInventoryKeyInGOP, selectedCarProduct, "GetOrderProcess");
        locationManagementForOMSGetReservationVerifier(carInventoryKeyInPP, selectedCarProduct, "PreparePurchase");
        locationManagementForOMSGetReservationVerifier(carInventoryKeyInRetrieve, selectedCarProduct, "Retrive");

    }

    private static void locationManagementForOMSGetReservationVerifier(CarInventoryKeyType carInventoryKey, CarProductType selectedCarProduct, String type) throws DataAccessException
    {
        final List<String> remarks = new ArrayList<>();
        SupportLocalGetDeatils.existCorrectCarVendorLocationIDInCarInventoryKey(carInventoryKey, selectedCarProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID(), selectedCarProduct.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID(), true, remarks);
        if (!SupportLocalGetDeatils.existCarPickupAndDropOffLocationKeyInCarInventoryKey(carInventoryKey))
        {
            remarks.add("\n\nCarCataLogKey in "+ type + " should exist.");
        }
        final StringBuilder errorMsgBuilderTemp = new StringBuilder();
        if (!SupportLocalGetDeatils.correctCarPickupAndDropOffLocationKeyInCarInventoryKey(carInventoryKey, selectedCarProduct.getCarInventoryKey(), errorMsgBuilderTemp))
        {
            remarks.add(errorMsgBuilderTemp.toString());
        }

        if (CollectionUtils.isNotEmpty(remarks))
        {
            Assert.fail(remarks.toString());
        }

    }
}