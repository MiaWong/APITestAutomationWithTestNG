package com.expedia.s3.cars.supplyconnectivity.amadeus.tests;

import com.expedia.s3.cars.framework.core.logging.LogHelper;
import com.expedia.s3.cars.framework.keyvalueloghandling.servicerequest.DOMTransportWithPerfMetrics;
import com.expedia.s3.cars.framework.servicerequest.transport.behaviors.HttpDOMTransportBehavior;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;
import org.w3c.dom.Element;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class AmadeusServiceTransport extends DOMTransportWithPerfMetrics implements HttpDOMTransportBehavior
{
    private HttpClient httpClient;

    @Override
    public void sendMessage() throws IOException
    {
        sendHttpMessage();
    }

    public void sendHttpGETMessage() throws IOException
    {
        String url = this.getServiceURL();

        try {
            int e = 1;
            int maxAttempts = this.getEOFRetryCount() + 1;

            while(e <= maxAttempts) {
                try {
                    Request httpreq = this.getHttpClient().newRequest(url).accept(new String[]{this.getContentType()}).method(HttpMethod.GET).timeout(this.getRequestTimeoutMs(), TimeUnit.MILLISECONDS);
                    Map headers = this.getRequestHeaders();
                    if(headers != null) {
                        Iterator response = headers.entrySet().iterator();

                        while(response.hasNext()) {
                            Map.Entry header = (Map.Entry)response.next();
                            httpreq.header((String)header.getKey(), (String)header.getValue());
                        }
                    }

                    ContentResponse var12 = httpreq.send();
                    this.getServiceRequestContext().setResponse(this.extractResponse(var12));
                    this.getServiceRequestContext().setSuccessfulResponse(this.isSuccess(this.getServiceRequestContext().getResponse()));
                    if(e > 1) {
                        LogHelper.log(this.getLogger(), Level.INFO, Integer.valueOf(10000), "Successfully sent on attempt " + e);
                    }

                    return;
                } catch (ExecutionException | EOFException var10) {
                    if(var10 instanceof ExecutionException && !(var10.getCause() instanceof EOFException)) {
                        throw var10;
                    }

                    LogHelper.log(this.getLogger(), Level.WARN, Integer.valueOf(20000), String.format("EOF exception while sending request on attempt %s. Retrying...", new Object[]{Integer.valueOf(e)}), var10);
                    ++e;
                }
            }
        } catch (Exception var11) {
            LogHelper.log(this.getLogger(), Level.ERROR, Integer.valueOf(30000), "Error sending request", var11);
            this.getServiceRequestContext().setException(var11);
            this.getServiceRequestContext().setSuccessfulResponse(false);
        }
    }

    public void sendHttpPOSTMessage() throws IOException
    {

    }
    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return null;
    }

    @Override
    public boolean isSuccess(Element response) {
        return isResponseSuccessful(response);
    }

    @Override
    public long getRequestTimeoutMs() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "application/xml";
    }

    @Override
    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public void setHttpClient(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    @Override
    public byte[] getRequestBytes(Element element) {
        return new byte[0];
    }
}
