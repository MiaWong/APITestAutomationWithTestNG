package com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.OptionListCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;

/**
 * Created by miawang on 8/30/2016.
 */
public class VerifyOptionListInGetOrderProcessResponse implements IVerification<GetOrderProcessVerificationInput, BasicVerificationContext> {

    @Override
    public boolean shouldVerify(GetOrderProcessVerificationInput input, BasicVerificationContext verificationContext) {
        final CarProductType carInRequest = input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct();
        if (null == carInRequest) {
            return false;
        } else {
            return carInRequest.getCarVehicleOptionList() != null &&
                    !carInRequest.getCarVehicleOptionList().getCarVehicleOption().isEmpty();
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(GetOrderProcessVerificationInput input, BasicVerificationContext verificationContext) {
        final CarProductType getOrderProcessReqCar = input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct();
        final CarProductType getOrderProcessRspCar = input.getResponse().getOrderProductList().getOrderProduct().get(0)
                .getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();

        boolean isPassed = false;
        final ArrayList remarks = new ArrayList();

        final OptionListCommonVerifier optionListCommonVerifier = new OptionListCommonVerifier();
        optionListCommonVerifier.verifyOptionList(input.getRequest().getConfiguredOfferData().getCarOfferData().getCarLegacyBookingData().getCurrencyCode(),
                getOrderProcessReqCar, getOrderProcessRspCar, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT);

        if (CollectionUtils.isEmpty(remarks))
        {  isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
