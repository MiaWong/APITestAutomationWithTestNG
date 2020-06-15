package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.PrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.PrepareChangeResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class PrepareChangeVerificationInput extends BasicVerificationInput<PrepareChangeRequest, PrepareChangeResponseType>{
    public PrepareChangeVerificationInput(PrepareChangeRequest prepareChangeRequest, PrepareChangeResponseType prepareChangeResponseType) {
        super(prepareChangeRequest, prepareChangeResponseType);
    }
}
