package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities;

import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 2/12/2017.
 */
public class ReserveExecutionHelper
{
    //  SCS reserve with filter on car product
    public static ReserveVerificationInput reserve(TestData parameters, SCSRequestGenerator requestGenerator) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        reserveRequest.getAuditLogTrackingData().setAuditLogForceLogging(true);
        reserveRequest.getAuditLogTrackingData().setAuditLogForceDownstreamTransaction(true);
        if(StringUtil.isNotBlank(parameters.getBillingNumber()))
        {
            reserveRequest.setBillingCode(parameters.getBillingNumber());
        }

        ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, parameters.getGuid());

        requestGenerator.setReserveReq(reserveVerificationInput.getRequest());
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        return reserveVerificationInput;
    }
}