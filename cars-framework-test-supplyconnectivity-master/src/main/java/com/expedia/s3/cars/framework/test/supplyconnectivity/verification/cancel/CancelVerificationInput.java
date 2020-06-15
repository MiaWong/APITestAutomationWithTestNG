package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class CancelVerificationInput extends BasicVerificationInput<CarSupplyConnectivityCancelRequestType,
        CarSupplyConnectivityCancelResponseType>
{
    public CancelVerificationInput(CarSupplyConnectivityCancelRequestType request,CarSupplyConnectivityCancelResponseType response)
    {
        super(request, response);
    }
}
