package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/12/2017.
 */
public class CostAndAvailExecutionHelper {
    //  SCS GetCostAndAvailability
    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(TestData parameters,
                                                                                 SCSRequestGenerator requestGenerator)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();
        return TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, parameters.getGuid());
    }
}