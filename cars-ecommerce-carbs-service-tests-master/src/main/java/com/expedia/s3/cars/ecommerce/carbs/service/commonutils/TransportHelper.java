package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.v5.V5SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;

import org.eclipse.jetty.client.HttpClient;
@SuppressWarnings("PMD")
public class TransportHelper
{
    private TransportHelper()
    {
        //NOP
    }

    public static V5SearchVerificationInput sendRecieve(HttpClient httpClient, String serviceAddress,
                                                        CarSearchRequest request, String guid)
    {
        final SimpleE3FIHttpTransport<CarSearchRequest, CarSearchResponse, Object> transport =
                new SimpleE3FIHttpTransport<>(
                        httpClient, "search/v5", serviceAddress, 30000, request, CarSearchResponse.class);
        RequestSender.sendWithTransport(transport, guid);
        final CarSearchResponse response = transport.getServiceRequestContext().getResponse();
        final V5SearchVerificationInput verificationInput = new V5SearchVerificationInput(request, response);
        return verificationInput;
    }
}
