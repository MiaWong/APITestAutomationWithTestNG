package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;

/**
 * Created by sswaminathan on 8/15/16.
 */
public class ReserveVerificationInput extends BasicVerificationInput<CarSupplyConnectivityReserveRequestType,
        CarSupplyConnectivityReserveResponseType>
{
    public ReserveVerificationInput(CarSupplyConnectivityReserveRequestType request,
                                    CarSupplyConnectivityReserveResponseType response)
    {
        super(request, response);
    }
}
