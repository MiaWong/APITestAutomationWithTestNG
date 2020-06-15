package com.expedia.www.cars.bugatti.service.tests.requestsender;

import com.expedia.www.cars.bugatti.service.tests.common.SettingsProvider;
import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchRequest;
import com.expedia.cars.schema.ecommerce.shopping.v1.CarSearchResponse;
import org.eclipse.jetty.client.HttpClient;

/**
 * Created by miawang on 3/21/2017.
 */
@SuppressWarnings("PMD")
public class BugattiRequestSender {

    public CarSearchResponse getSearchResponse(String guid, HttpClient httpClient, CarSearchRequest searchRequestType) {
        final SimpleE3FIHttpTransport<CarSearchRequest, CarSearchResponse, Object> transport
                = new SimpleE3FIHttpTransport<CarSearchRequest, CarSearchResponse, Object>(httpClient, SettingsProvider.SERVICE_E3DESTINATION,
                SettingsProvider.SERVICE_ADDRESS, 30000, searchRequestType, CarSearchResponse.class);

        System.out.println(SettingsProvider.SERVICE_E3DESTINATION + SettingsProvider.SERVICE_ADDRESS);
        RequestSender.sendWithTransport(transport, guid);
        return transport.getServiceRequestContext().getResponse();
    }
}