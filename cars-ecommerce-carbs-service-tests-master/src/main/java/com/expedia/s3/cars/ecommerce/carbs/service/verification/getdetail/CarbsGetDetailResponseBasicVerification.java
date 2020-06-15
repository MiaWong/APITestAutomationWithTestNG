package com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail;

import com.expedia.s3.cars.ecommerce.carbs.service.verification.common.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;

/**
 * Created by fehu on 11/9/2016.
 */
public class CarbsGetDetailResponseBasicVerification implements IVerification<GetDetailsVerificationInput, BasicVerificationContext> {


    private static final String MESSAGR_NO_GETDETAILS_IN_REQUEST = "No valid car product in GetDetails request.";

    private static final String MESSAGE_SUCCESS = "Success";
    private static final String TAG_DESCRIPTION_RAW_TEXT = "DescriptionRawText";


    private String verifyBasic(CarECommerceGetDetailsResponseType response) {

        if (null == response || null == response.getCarProductList())
        {
            return "response is null or searchresultList is null";
        }

        if (CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
                   return MESSAGR_NO_GETDETAILS_IN_REQUEST;
        }
        return null;
    }

    private String verifyExtra(CarECommerceGetDetailsResponseType response) {
        if (null != response.getDetailsErrorCollection()) {
            return VerificationHelper.verifyExtra(getXmlFieldValue(response.getDetailsErrorCollection(), TAG_DESCRIPTION_RAW_TEXT));
        }

        return null;
    }


    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
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


    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        return true;
    }

}
