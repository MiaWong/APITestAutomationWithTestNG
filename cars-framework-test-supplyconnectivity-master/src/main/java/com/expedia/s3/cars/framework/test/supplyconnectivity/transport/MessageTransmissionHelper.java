package com.expedia.s3.cars.framework.test.supplyconnectivity.transport;

import com.expedia.s3.cars.framework.test.common.transport.SimpleE3FIHttpTransport;
import com.expedia.s3.cars.framework.test.common.utils.RequestSender;
import org.eclipse.jetty.client.HttpClient;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by jiyu on 8/31/16.
 */
public class MessageTransmissionHelper<Request, Response, VerificationInput>
{
    public MessageTransmissionHelper(Class<Request> requestClass, Class<Response> responseClass, Class<VerificationInput> verificationInputClass)
    {
        requestType = requestClass;
        responseType = responseClass;
        verificationInputType = verificationInputClass;
    }

    final private Class<Request> requestType;
    Class<Request> getRequestType()
    {
        return requestType;
    }

    final private Class<Response> responseType;
    Class<Response> getResponseType()
    {
        return responseType;
    }

    final private Class<VerificationInput> verificationInputType;
    Class<VerificationInput> getVerificationInputType()
    {
        return verificationInputType;
    }

    private VerificationInput createVerificationInputInstance(Class<VerificationInput> vi, Request rq, Response rs)
            throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return vi.getConstructor(getRequestType(), getResponseType()).newInstance(rq, rs);
    }

    public VerificationInput sendReceive(HttpClient httpClient,
                                         String serviceAddr,
                                         String e3destination,
                                         Request request,
                                         String guid) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final SimpleE3FIHttpTransport<Request, Response, Object> transport =
                new SimpleE3FIHttpTransport<>(  httpClient,
                                                e3destination,
                                                serviceAddr,
                                                30000,
                                                request,
                                                getResponseType());

        RequestSender.sendWithTransport(transport, guid);
        final Response response = transport.getServiceRequestContext().getResponse();

        return createVerificationInputInstance(getVerificationInputType(), request, response);
    }
}
