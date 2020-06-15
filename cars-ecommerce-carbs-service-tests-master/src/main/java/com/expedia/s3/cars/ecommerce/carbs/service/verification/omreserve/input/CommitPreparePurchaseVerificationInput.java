package com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.CommitPreparePurchaseResponseType;

/**
 * Created by fehu on 11/13/2016.
 */
public class CommitPreparePurchaseVerificationInput extends BasicVerificationInput<CommitPreparePurchaseRequest, CommitPreparePurchaseResponseType>{

    public CommitPreparePurchaseVerificationInput(CommitPreparePurchaseRequest commitPreparePurchaseRequest, CommitPreparePurchaseResponseType commitPreparePurchaseResponseType) {
        super(commitPreparePurchaseRequest, commitPreparePurchaseResponseType);
    }
}
