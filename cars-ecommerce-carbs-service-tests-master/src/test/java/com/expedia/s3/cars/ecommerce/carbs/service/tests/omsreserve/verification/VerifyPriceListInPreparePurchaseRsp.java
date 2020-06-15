package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.CostItemsRetriever;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.PriceListVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by v-mechen on 12/28/2017.
 */
public class VerifyPriceListInPreparePurchaseRsp implements IVerification<PreparePurshaseVerificationInput, BasicVerificationContext> {

    @Override
    public boolean shouldVerify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {
        return true;
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {
        boolean isPassed = false;
        final ArrayList remarks = new ArrayList();

        final CarProductType preparePurchaseRspCar = input.getResponse().getPreparedItems().getBookedItemList().
                getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        //Get request currency
        final String requestCurrency = input.getRequest().getConfiguredProductData().getCarOfferData().getCarLegacyBookingData().getCurrencyCode();
        //Get vendor currency
        final String vendorCurrency = CostPriceCalculator.getCostPosuCurrencyCode(preparePurchaseRspCar.getCostList(), requestCurrency);

        final int businessModel = verificationContext.getScenario().getBusinessModel();
        if(businessModel == 1) {
            //Get expected cost value calculated by specified formulas
            final Map<String, Double> costItems = CostItemsRetriever.getCostItemsFromCostListForAgencyGetDetail(
                    requestCurrency, vendorCurrency, preparePurchaseRspCar.getCostList(), false, remarks);

            if (costItems.isEmpty()) {
                remarks.add("There is no Car cost exit in car prodcut for Reserve .");
            }

            //Verify pricelist
            PriceListVerifier.verifyPriceListForAgencyGetDetail(costItems, preparePurchaseRspCar, requestCurrency, vendorCurrency, remarks);
        }
        if (CollectionUtils.isEmpty(remarks))
        { isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}

