package com.expedia.s3.cars.framework.test.common.execution.verification;

/**
 * A simple verification Input class that contains a request and a response
 * Created by sswaminathan on 8/9/16.
 */
public class BasicVerificationInput<Request, Response>
{
    private final Request request;
    private final Response response;

    public BasicVerificationInput(Request request, Response response)
    {
        this.request = request;
        this.response = response;
    }

    public Request getRequest()
    {
        return request;
    }

    public Response getResponse()
    {
        return response;
    }
}
