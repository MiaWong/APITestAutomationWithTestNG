package com.expedia.s3.cars.framework.test.common.transport;

import com.expedia.s3.cars.framework.core.activity.ActivitySystem;
import com.expedia.s3.cars.framework.servicerequest.IServiceRequestContext;
import com.expedia.s3.cars.framework.servicerequest.ServiceRequestContext;
import com.expedia.s3.cars.framework.servicerequest.URLServiceAddress;
import com.expedia.s3.cars.framework.servicerequest.transport.AbstractDOMTransport;
import com.expedia.s3.cars.framework.servicerequest.transport.behaviors.HttpDOMTransportBehavior;
import com.google.common.base.Charsets;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Use this transport to send simple xml messages
 * Intended to be used as a prototype... NOT threadsafe
 * Logging to crs logs is disabled
 * Always propagates originating guid
 * Created by sswaminathan on 8/3/16.
 */
public class SimpleDOMTransport
        extends AbstractDOMTransport
        implements HttpDOMTransportBehavior
{
    public static final String HEADER_ORIG = "orig";
    private static final String DUMMY_TOKEN = "<a/>";

    private final Logger logger = Logger.getLogger(getClass());

    private final HttpClient httpClient;
    private final int requestTimeoutMs;
    private final Map<String, String> requestHeaders;
    private String contentType;

    public SimpleDOMTransport(String contentType, HttpClient httpClient, Map<String, String> requestHeaders,
                              int requestTimeoutMs,
                              String serviceAddress,
                              Element requestPayload)
    {
        this.contentType = contentType;
        this.httpClient = httpClient;
        this.requestHeaders = requestHeaders;
        this.requestTimeoutMs = requestTimeoutMs;

        setupServiceRequestContext(serviceAddress, requestPayload);

    }

    private void setupServiceRequestContext(String serviceAddress, Element requestPayload)
    {

        super.setServiceRequestContext(new ServiceRequestContext<>());

        final URLServiceAddress address = new URLServiceAddress();
        address.setServiceAddressData(serviceAddress);

        getServiceRequestContext().setServiceAddress(address);
        getServiceRequestContext().setRequest(requestPayload);

        try
        {
            getServiceRequestContext().setRequestToken(
                    getDocumentBuilder().parse(new ByteArrayInputStream(DUMMY_TOKEN.getBytes(Charsets.UTF_8)))
                            .getDocumentElement());
        } catch (Exception e)
        {
            //NOP - we'll never hit this
        }
    }

    //a convenience method to get response
    public Element getResponse()
    {
        return getServiceRequestContext().getResponse();
    }

    /**
     * this is set in constructor...
     * @param serviceRequestContext
     */
    @Override
    public void setServiceRequestContext(IServiceRequestContext<Element, Element, Element> serviceRequestContext)
    {
        //nop
    }

    @Override
    protected boolean loggingEnabled()
    {
        return false;
    }

    @Override
    protected boolean isLogFilterEnabled()
    {
        return false;
    }

    @Override
    protected boolean isEnabledInPOSConfig(String settingName)
    {
        return false;
    }

    @Override
    protected boolean shouldForceLoggingOnError()
    {
        return false;
    }

    @Override
    protected Boolean shouldPropagateOriginatingGuid()
    {
        return true;
    }

    @Override
    public void sendMessage() throws IOException
    {
        sendHttpMessage();
    }

    @Override
    public Logger getLogger()
    {
        return logger;
    }

    @Override
    public Map<String, String> getRequestHeaders()
    {
        final Map<String, String> headers = new HashMap<>();

        if(requestHeaders != null)
        {
            headers.putAll(requestHeaders);
        }

        if(!headers.containsKey(HEADER_ORIG) && ActivitySystem.activityIsPresent())
        {
            headers.put(HEADER_ORIG, ActivitySystem.getCurrentActivityContext().getOriginatorActivityId());
        }

        return headers;
    }

    @Override
    public boolean isSuccess(Element response)
    {
        return true;
    }

    @Override
    public long getRequestTimeoutMs()
    {
        return requestTimeoutMs;
    }

    @Override
    public String getContentType()
    {
        return contentType;
    }

    protected void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    @Override
    public HttpClient getHttpClient()
    {
        return httpClient;
    }


    @Override
    public byte[] getRequestBytes(Element request)
    {
        return serializeRequest(request);
    }

    @Override
    protected void logPerfData(long elapsedTimeMs, boolean hadDownstreamCalls)
    {
        //nop
    }
}
