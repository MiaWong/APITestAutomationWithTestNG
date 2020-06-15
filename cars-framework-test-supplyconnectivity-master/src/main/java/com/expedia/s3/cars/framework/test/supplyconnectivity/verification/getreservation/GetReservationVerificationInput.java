package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetReservationVerificationInput extends BasicVerificationInput<CarSupplyConnectivityGetReservationRequestType,
        CarSupplyConnectivityGetReservationResponseType>
{
    public GetReservationVerificationInput(CarSupplyConnectivityGetReservationRequestType request,
                                           CarSupplyConnectivityGetReservationResponseType response)
    {
        super(request, response);
    }
}