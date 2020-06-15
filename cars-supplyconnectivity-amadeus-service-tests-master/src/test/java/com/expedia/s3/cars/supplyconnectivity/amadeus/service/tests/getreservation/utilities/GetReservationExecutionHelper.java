package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/12/2017.
 */
public class GetReservationExecutionHelper {

    public static GetReservationVerificationInput retrieveReservation(TestData parameters,
                                                                      SCSRequestGenerator requestGenerator) throws IOException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator.createGetReservationRequest();

        return TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, parameters.getGuid());
    }
}
