package com.expedia.s3.cars.ecommerce.carbs.service.verification.cancel;

import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelRequestType;
import com.expedia.s3.cars.ecommerce.messages.cancel.defn.v4.CarECommerceCancelResponseType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;

/**
 * Created by fehu on 11/14/2016.
 */
public class CancelVerificationInput extends BasicVerificationInput<CarECommerceCancelRequestType, CarECommerceCancelResponseType> {

    public CancelVerificationInput(CarECommerceCancelRequestType carECommerceCancelRequestType, CarECommerceCancelResponseType carECommerceCancelResponseType) {
        super(carECommerceCancelRequestType, carECommerceCancelResponseType);
    }
}
