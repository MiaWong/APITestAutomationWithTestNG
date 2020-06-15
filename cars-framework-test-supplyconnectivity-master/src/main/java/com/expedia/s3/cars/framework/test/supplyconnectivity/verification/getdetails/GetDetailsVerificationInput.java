package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;

/**
 * Created by asharma1 on 8/11/2016.
 */
public class GetDetailsVerificationInput extends BasicVerificationInput<CarSupplyConnectivityGetDetailsRequestType,
        CarSupplyConnectivityGetDetailsResponseType>
{
    public GetDetailsVerificationInput(CarSupplyConnectivityGetDetailsRequestType request,
                                       CarSupplyConnectivityGetDetailsResponseType response)
    {
        super(request, response);
    }
}
