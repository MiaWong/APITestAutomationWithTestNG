package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/12/2017.
 */
public class GetDetailsExecutionHelper {
    //  SCS getDetails with filter on car product
    public static GetDetailsVerificationInput getDetails(TestData parameters,
                                                         SCSRequestGenerator requestGenerator)
            throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        return TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, parameters.getGuid());
    }
}
