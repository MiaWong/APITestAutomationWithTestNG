package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;

/**
 * Created by asharma1 on 8/10/2016.
 */
public class SearchVerificationInput
        extends BasicVerificationInput<CarSupplyConnectivitySearchRequestType, CarSupplyConnectivitySearchResponseType>
{
    public SearchVerificationInput(CarSupplyConnectivitySearchRequestType carSupplyConnectivitySearchRequestType, CarSupplyConnectivitySearchResponseType carSupplyConnectivitySearchResponseType)
    {
        super(carSupplyConnectivitySearchRequestType, carSupplyConnectivitySearchResponseType);
    }
}
