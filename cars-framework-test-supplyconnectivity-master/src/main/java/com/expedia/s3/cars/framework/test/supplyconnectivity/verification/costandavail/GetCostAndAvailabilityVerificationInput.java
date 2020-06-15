package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetCostAndAvailabilityVerificationInput
        extends BasicVerificationInput<CarSupplyConnectivityGetCostAndAvailabilityRequestType,
        CarSupplyConnectivityGetCostAndAvailabilityResponseType>
{
    public GetCostAndAvailabilityVerificationInput(CarSupplyConnectivityGetCostAndAvailabilityRequestType request,
                                                   CarSupplyConnectivityGetCostAndAvailabilityResponseType response)
    {
        super(request, response);
    }
}
