package com.expedia.s3.cars.framework.test.common.transport;

import com.expedia.s3.cars.framework.core.cache.jaxb.DynamicJAXBCache;
import com.expedia.s3.cars.framework.servicerequest.ServiceRequestContext;
import com.expedia.s3.cars.framework.servicerequest.URLServiceAddress;
import com.expedia.s3.cars.framework.servicerequest.transport.AbstractE3HttpTransport;
import org.eclipse.jetty.client.HttpClient;

import javax.xml.bind.JAXBContext;

/**
 * A simple implementation of E# FI transport with the following default behavior
 * 1. Doesn't support request/response filtering
 * 2. Doesn't log to CRS logs or KV metrics
 * 3. Sends IntegrationTest as the server name
 * 4. Always propagates originating guid, and like all transports, needs to be run within an activity with the appropriate originating guid
 * 5.
 * Created by sswaminathan on 8/4/16.
 */
public class SimpleE3FIHttpTransport<Request, Response, Token> extends AbstractE3HttpTransport<Request, Response, Token>
{
    private final String e3DestinationName;
    private final long requestTimeOutMs;
    private final Class<? extends Request> requestClass;
    private final Class<? extends Response> responseClass;

    private final DynamicJAXBCache jaxbCache = new DynamicJAXBCache();


    public SimpleE3FIHttpTransport(HttpClient httpClient, String e3DestinationName,
                                   String serviceAddress, long requestTimeOutMs,
                                   Request request, Class<? extends Response> responseClass)
    {
        super.setHttpClient(httpClient);
        this.e3DestinationName = e3DestinationName;
        this.requestTimeOutMs = requestTimeOutMs;
        this.requestClass = (Class<? extends Request>) request.getClass();
        this.responseClass = responseClass;

        setupServiceRequestContext(serviceAddress, request);
    }

    private void setupServiceRequestContext(String serviceAddress, Request requestPayload)
    {
        super.setServiceRequestContext(new ServiceRequestContext<>());

        final URLServiceAddress address = new URLServiceAddress();
        address.setServiceAddressData(serviceAddress);

        getServiceRequestContext().setServiceAddress(address);
        getServiceRequestContext().setRequest(requestPayload);
    }

    @Override
    protected JAXBContext getRequestJaxbContext()
    {
        return (JAXBContext) jaxbCache.get(requestClass);
    }

    @Override
    protected JAXBContext getResponseJaxbContext()
    {
        return (JAXBContext) jaxbCache.get(responseClass);
    }

    @Override
    protected String getE3DestinationName()
    {
        return e3DestinationName;
    }

    @Override
    protected long getRequestTimeoutMs()
    {
        return requestTimeOutMs;
    }

    @Override
    protected String getServerName()
    {
        return "IntegrationTest";
    }

    @Override
    protected int getEAPID()
    {
        return 0;
    }

    @Override
    protected int getTPID()
    {
        return 0;
    }

    @Override
    protected int getTRL()
    {
        return 0;
    }

    @Override
    protected int getTUID()
    {
        return 0;
    }

    @Override
    protected String getSendingServerName()
    {
        return getServerName();
    }

    @Override
    protected Request filterRequest(Request input)
    {
        return input;
    }

    @Override
    protected Response filterResponse(Response input)
    {
        return input;
    }

    @Override
    protected String getTransactionType()
    {
        return null;
    }

    @Override
    protected String getTransactionNumber()
    {
        return null;
    }

    @Override
    protected String getTransactionName()
    {
        return null;
    }

    @Override
    protected String getLogActionType()
    {
        return null;
    }

    @Override
    protected void logPerfData(long elapsedTimeMs, boolean hadDownstreamCalls)
    {
        //NOP
    }

    @Override
    protected void handleSendMessageException(Exception e)
    {
        getServiceRequestContext().setException(e);
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

}
