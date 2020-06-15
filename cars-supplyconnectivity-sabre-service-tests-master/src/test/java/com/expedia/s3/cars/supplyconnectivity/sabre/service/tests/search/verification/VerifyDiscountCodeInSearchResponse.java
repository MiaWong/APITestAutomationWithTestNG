package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;

import java.util.Arrays;
import java.util.List;

/**
 * Created by aaniyath on 17-10-2016.
 */
@SuppressWarnings("PMD")
public class VerifyDiscountCodeInSearchResponse implements ISearchVerification
{
    private static final String MESSAGE_SUCCESS = "Success";
    private static final String MESSAGE_DISCOUNT_CODE_MISMATCH = "Discount code not found in SCS response";

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        //Only the discountSearch SCS request will have CorporateDiscountCode and PromoCode.
        return null != input && null != input.getRequest()
                && null != input.getRequest().getCarSearchCriteriaList()
                && null != input.getRequest().getCarSearchCriteriaList().getCarSearchCriteria()
                && null != input.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().get(1).getCarRate().getCorporateDiscountCode()
                && null != input.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().get(1).getCarRate().getPromoCode();
    }

    @Override
    public IVerification.VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        String errorMessage = "";

        errorMessage = verifyDiscountSearchResponse(input);
        if (errorMessage != null) {
            return new IVerification.VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new IVerification.VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));

    }

    public String verifyDiscountSearchResponse(SearchVerificationInput input)
    {
        CarRateType carRateTypeInSCSRequest = input.getRequest().getCarSearchCriteriaList()
                .getCarSearchCriteria().get(1).getCarRate();

        List<CarProductType> carProductTypesInSCSResponse = input.getResponse().getCarSearchResultList()
                .getCarSearchResult().get(1).getCarProductList().getCarProduct();

        for(CarProductType carProductType : carProductTypesInSCSResponse)
        {
            if (carProductType.getCarInventoryKey().getCarRate().getPromoCode().equals(carRateTypeInSCSRequest.getPromoCode()) &&
                    carProductType.getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals(carRateTypeInSCSRequest.getCorporateDiscountCode()) )
            {
                continue;
            }
            return MESSAGE_DISCOUNT_CODE_MISMATCH;
        }
        return null;
    }
}
