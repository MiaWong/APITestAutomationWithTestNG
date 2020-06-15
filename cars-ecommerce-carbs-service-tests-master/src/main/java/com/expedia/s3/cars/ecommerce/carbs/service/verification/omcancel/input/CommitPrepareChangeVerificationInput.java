package com.expedia.s3.cars.ecommerce.carbs.service.verification.omcancel.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeRequest;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class CommitPrepareChangeVerificationInput extends BasicVerificationInput<CommitPrepareChangeRequest, CommitPrepareChangeResponseType> {
    public CommitPrepareChangeVerificationInput(CommitPrepareChangeRequest commitPrepareChangeRequest, CommitPrepareChangeResponseType commitPrepareChangeResponseType) {
        super(commitPrepareChangeRequest, commitPrepareChangeResponseType);
    }
}
