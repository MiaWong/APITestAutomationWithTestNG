package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.financetypes.defn.v4.PriceType;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.testng.Assert;

import java.util.List;

/**
 * Created by yyang4 on 1/22/2018.
 */
@SuppressWarnings("PMD")
public class VerifyReserveIncludePrice {
    public static void includePriceVerify(CarbsOMReserveReqAndRespGenerator generator, double expectAmout, Boolean expectCustomerPaymentRequired){
        final StringBuilder errorMsg = new StringBuilder();
        final GetOrderProcessResponseType orderResponse = generator.getGetOrderProcessResponseType();
        final PreparePurchaseResponseType purchaseResponse = generator.getPreparePurchaseResponseType();
        if(CompareUtil.isObjEmpty(orderResponse) || CompareUtil.isObjEmpty(purchaseResponse)){
            Assert.fail("GetOrderProcessResponse or PreparePurchaseResponse return null.");
        }
        final double actualAmout = Double.valueOf(orderResponse.getOrderProductList().getOrderProduct().get(0).getOrderProcess().getTotalPriceWithTaxAmount().getSimpleAmount());
        final Boolean actualCustomerPaymentRequired = orderResponse.getOrderProductList().getOrderProduct().get(0).getOrderProcess().getCustomerPaymentInstrumentRequiredBySupplier();
        //1.verify TotalPriceWithTaxAmount
        //Verify TotalPriceWithTaxAmount is returned as Zero in GetOrderProcess response
        if(expectAmout == 0){
            if(!CompareUtil.compareObject(expectAmout,actualAmout,null,errorMsg.append("TotalPriceWithTaxAmount return error: "))){
                Assert.fail(errorMsg.toString());
            }
        }else{
            //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
            final List<PriceType> priceList = purchaseResponse.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct().getPriceList().getPrice();
            for(PriceType price : priceList) {
                if("Total".equalsIgnoreCase(price.getFinanceCategoryCode()) && "Total".equals(price.getDescriptionRawText())) {
                   final int amoutDecimal = price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
                   final int amoutDecimalPlaceCount = (int)price.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                    expectAmout = amoutDecimal/Math.pow(10,amoutDecimalPlaceCount);
                    break;
                }
            }
            if(!CompareUtil.compareObject(expectAmout,actualAmout,null,errorMsg.append("TotalPriceWithTaxAmount return error: "))){
                Assert.fail(errorMsg.toString());
            }
        }

        //2. verify customerPaymentInstrumentRequiredBySupplier
        if(!CompareUtil.compareObject(expectCustomerPaymentRequired,actualCustomerPaymentRequired,null,errorMsg.append("customerPaymentInstrumentRequiredBySupplier return error: "))){
            Assert.fail(errorMsg.toString());
        }
    }
}
