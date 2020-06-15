package com.expedia.s3.cars.framework.test.common.transport;

import com.google.common.base.Charsets;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Created by sswaminathan on 8/3/16.
 */
public class SimpleHttpTransportIntegrationTests
{

    private static final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
    private static final String SAMPLEREQUEST = "<CarSupplyConnectivitySearchRequest xmlns=\"urn:com:expedia:s3:cars:supplyconnectivity:messages:search:defn:v4\" />";

    /**
     * this is an integration test...
     */
    @Ignore
    @Test
    public void testSendingRequestWorks() throws Exception
    {
        final HttpClient httpClient = new HttpClient();

        httpClient.start();

        final SimpleDOMTransport trans = new SimpleDOMTransport(MediaType.APPLICATION_XML_VALUE, httpClient, null, 30000,
                "http://cars-supplyconnectivity-titanium-service.us-west-2.int.expedia.com/restservice",
                DBF.newDocumentBuilder().parse(
                        new ByteArrayInputStream(SAMPLEREQUEST.getBytes(Charsets.UTF_8)))
                        .getDocumentElement());

        trans.execute(null);

        //System.out.println(PojoXmlUtil.toString(trans.getResponse()));

        httpClient.stop();

    }

}
