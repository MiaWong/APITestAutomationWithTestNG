package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests;

import com.expedia.s3.cars.framework.servicerequest.IServiceRequestContext;
import com.expedia.s3.cars.framework.servicerequest.ServiceRequestContext;
import com.expedia.s3.cars.framework.servicerequest.URLServiceAddress;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.AmadeusServiceTransport;
import org.apache.xerces.dom.DeferredElementNSImpl;
import org.eclipse.jetty.client.HttpClient;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.FileAssert.fail;

/**
 * Created by alibadisos on 5/16/16.
 */
public class BuildVerificationTests
{
    private AmadeusServiceTransport transport;
    HttpClient httpClient = new HttpClient();

    @BeforeSuite
    public void suiteSetup() throws Exception
    {
        transport = new AmadeusServiceTransport();
        httpClient.start();
        transport.setHttpClient(httpClient);
    }

    @AfterSuite
    public void suiteTeardown() throws Exception
    {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.BVT}, enabled = false)
    public void testServiceHealth() throws Exception
    {
        IServiceRequestContext<Element, Element, Element> requestContext =
                getServiceRequestContext("http://cars-supplyconnectivity-titanium-service.us-west-2.int.expedia.com/ServiceHealth", null, null);

        // make a call out to service health
        this.transport.setServiceRequestContext(requestContext);

        this.transport.sendHttpGETMessage();

        // check for operational in the response
        if ( requestContext.isSuccessfulResponse() ) {
            Element responseElement = requestContext.getResponse();

            Element statusNode = null;
            for ( int i=0; i< ((DeferredElementNSImpl)responseElement).getLength(); i++)
            {
                Element node = (Element)((DeferredElementNSImpl) responseElement).item(i);
                if ( node.getLocalName().equals("Status") )
                {
                    statusNode = node;
                    break;
                }
            }

            assertNotNull(statusNode);
            assertEquals(statusNode.getTextContent(), "Operational");

        }
        else
        {
            fail(requestContext.getException().getMessage());
        }


    }

    private IServiceRequestContext<Element, Element, Element> getServiceRequestContext(
            String url, Element requestToken, Element request)
    {
        return new ServiceRequestContext<Element, Element, Element>()
        {
            {
                setServiceAddress(new URLServiceAddress() {
                    {
                        setServiceAddressData(url);
                        setRequestToken(requestToken);
                    }
                });

                setRequest(request);
            }
        };
    }

}
