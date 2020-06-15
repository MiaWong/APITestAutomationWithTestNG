package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.OptionListCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;

/**
 * Created by miawang on 8/30/2016.
 */
public class VerifyOptionListInPreparePurchaseResponse implements IVerification<PreparePurshaseVerificationInput, BasicVerificationContext> {

    @Override
    public boolean shouldVerify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {
        final CarProductType carInRequest = input.getRequest().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        if (null == carInRequest) {
            return false;
        } else {
            return carInRequest.getCarVehicleOptionList() != null &&
                    !carInRequest.getCarVehicleOptionList().getCarVehicleOption().isEmpty();
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {
        final CarProductType preparePurchaseReqCar = input.getRequest().getConfiguredProductData().getCarOfferData().
                getCarReservation().getCarProduct();
        final CarProductType preparePurchaseRspCar = input.getResponse().getPreparedItems().getBookedItemList().
                getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        boolean isPassed = false;
        final  ArrayList remarks = new ArrayList();

        final OptionListCommonVerifier optionListCommonVerifier = new OptionListCommonVerifier();
        optionListCommonVerifier.verifyOptionList(input.getRequest().getConfiguredProductData().getCarOfferData().
                        getCarLegacyBookingData().getCurrencyCode(), preparePurchaseReqCar, preparePurchaseRspCar,
                remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT);

        if (CollectionUtils.isEmpty(remarks))
        { isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
