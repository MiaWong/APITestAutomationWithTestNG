package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.OptionListCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
public class VerifyOptionListInGetDetailsResponse implements IVerification<GetDetailsVerificationInput, BasicVerificationContext> {
    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carListInRequest = input.getRequest().getCarProductList().getCarProduct();
        if (carListInRequest.isEmpty()) {
            return false;
        } else {
            return carListInRequest.get(0).getCarVehicleOptionList() != null &&
                    !carListInRequest.get(0).getCarVehicleOptionList().getCarVehicleOption().isEmpty();
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carListInRequest = input.getRequest().getCarProductList().getCarProduct();
        final CarProductType getDetailReqCar = carListInRequest.get(0);

        final List<CarProductType> carListInResponse = input.getResponse().getCarProductList().getCarProduct();
        final CarProductType getDetailRspCar = carListInResponse.get(0);

        boolean isPassed = false;
        final ArrayList remarks = new ArrayList();

        final OptionListCommonVerifier optionListCommonVerifier = new OptionListCommonVerifier();

        optionListCommonVerifier.verifyOptionList(input.getRequest().getCurrencyCode(), getDetailReqCar, getDetailRspCar,
                remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT);

        if (CollectionUtils.isEmpty(remarks))
        { isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
