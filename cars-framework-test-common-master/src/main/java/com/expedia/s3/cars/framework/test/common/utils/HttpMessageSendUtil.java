package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.core.logging.LogHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PMD")
public class HttpMessageSendUtil
{
    private HttpClient httpClient;
    private String serviceUrl;
    private Map<String, String> requestHeaders;
    private String method;
    private ContentProvider content;
    private long timeOut;
    Logger logger = Logger.getLogger(getClass());
    public HttpMessageSendUtil()
    {

    }

    public HttpMessageSendUtil(HttpClient httpClient, String serviceUrl, Map<String, String> requestHeaders, String method, ContentProvider content, long timeOut){
        this.httpClient = httpClient;
        this.serviceUrl = serviceUrl;
        this.requestHeaders = requestHeaders == null ? getRequestHeaders() : requestHeaders;
        this.method = StringUtils.isEmpty(method) ? getMethod() : method;
        this.content = content;
        this.timeOut = timeOut == 0 ? getTimeOut() : timeOut;

    }

    public ContentResponse sendHttpMessage() throws IOException
    {
        String url = this.getServiceUrl();
        try {
            Request httpreq = this.getHttpClient().newRequest(url).method(this.getMethod()).timeout(timeOut, TimeUnit.SECONDS);
            Map headers = this.getRequestHeaders();
            if(headers != null) {
                Iterator response = headers.entrySet().iterator();
                while(response.hasNext()) {
                    Map.Entry header = (Map.Entry)response.next();
                    httpreq.header((String)header.getKey(), (String)header.getValue());
                }
            }
            if(content != null){
                httpreq.content(content);
            }
            return httpreq.send();
        } catch (Exception e) {
            LogHelper.log(logger, Level.WARN, Integer.valueOf(20000), "Error sening http request");
        }
        return null;
    }

    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<String,String>();
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        return requestHeaders == null || requestHeaders.isEmpty() ? headers : requestHeaders;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getMethod() {
        return StringUtils.isEmpty(method) ? HttpMethod.GET.asString() : method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ContentProvider getContent() {
        return content;
    }

    public void setContent(ContentProvider content) {
        this.content = content;
    }

    public long getTimeOut()
    {
        timeOut = timeOut == 0 ? 100 : timeOut;
        return timeOut;
    }

    public void setTimeOut(long timeOut)
    {
        this.timeOut = timeOut;
    }
}
