package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class RollbackPrepareChangeVerificationInput extends BasicVerificationInput<RollbackPrepareChangeRequest, RollbackPrepareChangeResponseType>{
    public RollbackPrepareChangeVerificationInput(RollbackPrepareChangeRequest rollbackPrepareChangeRequest, RollbackPrepareChangeResponseType rollbackPrepareChangeResponseType) {
        super(rollbackPrepareChangeRequest, rollbackPrepareChangeResponseType);
    }
}
