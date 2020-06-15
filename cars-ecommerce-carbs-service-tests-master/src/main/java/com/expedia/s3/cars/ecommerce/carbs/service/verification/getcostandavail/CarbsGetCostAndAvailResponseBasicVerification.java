package com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarbsGetCostAndAvailResponseBasicVerification implements IVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> {

    private static final String MESSAGR_NO_COSTAVAIL_RESULT_IN_REQUEST = "No valid car product in GetCostAndAvailability request.";

    private static final String MESSAGE_SUCCESS = "Success";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";


    private String verifyBasic(CarECommerceGetCostAndAvailabilityResponseType response) {

        if (null == response || null == response.getCarProductList())
        {
            return "response is null or carProductList is null";
        }

        if (CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
            return MESSAGR_NO_COSTAVAIL_RESULT_IN_REQUEST;
        }
        return null;

    }

    private String verifyExtra(CarECommerceGetCostAndAvailabilityResponseType response) {
        if (null != response.getCostAndAvailabilityErrorCollection()) {
            if(null != response.getCostAndAvailabilityErrorCollection().getDownstreamServiceUnavailableError()) {
                return VerificationHelper.verifyExtra(PojoXmlUtil.getXmlFieldValue(response.getCostAndAvailabilityErrorCollection().
                        getDownstreamServiceUnavailableError(), TAG_DESCRIPTION_RAW_TEXT));
            }
            if(null != response.getCostAndAvailabilityErrorCollection().getDownstreamServiceTimeoutError()){
                return VerificationHelper.verifyExtra(PojoXmlUtil.getXmlFieldValue(response.getCostAndAvailabilityErrorCollection().
                        getDownstreamServiceTimeoutError(), TAG_DESCRIPTION_RAW_TEXT));
            }

        }

        return null;
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) {
        String errorMessage = "";

        errorMessage = verifyBasic(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        errorMessage = verifyExtra(input.getResponse());
        if (errorMessage != null) {
            return new VerificationResult(getName(), false, Arrays.asList(errorMessage));
        }

        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }
}
