package com.expedia.s3.cars.framework.test.common.transport;

import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by sswaminathan on 8/4/16.
 */
public class SimpleE3FIHttpTransportIntegrationTests
{

    @Test
    @Ignore
    public void testSimpleTransportWorks() throws Exception
    {
        final HttpClient httpClient = new HttpClient();
        httpClient.start();

        final SimpleE3FIHttpTransport<CarSupplyConnectivitySearchRequestType, CarSupplyConnectivitySearchResponseType, Object>
                transport = new SimpleE3FIHttpTransport<>(httpClient, "restservice",
                "http://cars-supplyconnectivity-titanium-service.us-west-2.int.expedia.com/restservice", 30000,
                new CarSupplyConnectivitySearchRequestType(), CarSupplyConnectivitySearchResponseType.class);

        transport.execute(null);

        assertNotNull(transport.getServiceRequestContext().getResponse());

    }
}
