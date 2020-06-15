package com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;

/**
 * Created by fehu on 11/14/2016.
 */
public class RetrieveVerificationInput extends BasicVerificationInput<RetrieveRequest, RetrieveResponseType> {
    public RetrieveVerificationInput(RetrieveRequest retrieveRequest, RetrieveResponseType retrieveResponseType) {
        super(retrieveRequest, retrieveResponseType);
    }

    public CarProductType carProductType;

    public CarProductType getCarProductType() {
        return carProductType;
    }

    public void setCarProductType(CarProductType carProductType) {
        this.carProductType = carProductType;
    }
}
