package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.core.EventLogBaseID;
import com.expedia.s3.cars.framework.core.logging.LogHelper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.InputStreamResponseListener;
import org.eclipse.jetty.http.HttpHeader;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mpaudel on 6/23/16.
 */
public class SpooferTransport
{
    private static final Gson GSON = new Gson();
    private final Logger logger = Logger.getLogger(this.getClass());

    private static final int MESSAGE_SEND_ERROR = EventLogBaseID.ERROR + 1;
    private static final String FORMAT_SPOOFER_URL = "https://%s:%s/spoofer/xml";

    private final HttpClient httpClient;
    private final int spooferTimeoutMs;

    private final String spooferBaseAddress;

    public SpooferTransport(HttpClient httpClient, String spooferServer, int spooferPort, int spooferTimeoutMs)
    {
        Assert.notNull(httpClient);
        Assert.notNull(spooferServer);

        this.httpClient = httpClient;
        this.spooferTimeoutMs = spooferTimeoutMs;

        spooferBaseAddress = String.format(FORMAT_SPOOFER_URL, spooferServer, spooferPort);
    }

    public SpooferTransport(HttpClient httpClient, String spooferServer, int spooferTimeoutMs)
    {
        this(httpClient, spooferServer, 80, spooferTimeoutMs);
    }

    public void setOverrides(Map<String, String> overrides, String originatingGuid) throws IOException
    {
        final String url = spooferBaseAddress + "/setoverrides/guid/" + originatingGuid;
        final String payload = generateOverridePayload(overrides);

        final ContentResponse post = post(payload, url);

        handleResponse(post);

    }

    public Document retrieveRecords(String originatingGuid) throws IOException
    {
        final String url = spooferBaseAddress + "/retrieve/guid/" + originatingGuid;
        logger.info("retrieveRecords:" +url);

        return PojoXmlUtil.stringToXml(handleResponse(get(url)).replaceAll("\n","").replaceAll(">\\s*<","><"));
    }

    protected String handleResponse(ContentResponse contentResponse) throws IOException
    {
        if (contentResponse.getStatus() != 200)
        {
            throw new IOException("An error occurred when posting to spoofer. Details: " + contentResponse.getContentAsString());
        }

        return contentResponse.getContentAsString();
    }

    protected ContentResponse post(String payload, String url) throws IOException
    {
        try
        {
            return httpClient
                    .POST(url)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                    .content(new BytesContentProvider(MediaType.APPLICATION_JSON_VALUE,
                            payload.getBytes(Charsets.UTF_8)))
                    .timeout(spooferTimeoutMs, TimeUnit.MILLISECONDS)
                    .send();
        } catch (Exception e)
        {
            LogHelper.log(logger, Level.ERROR, MESSAGE_SEND_ERROR, "Error sending message", e);
            throw new IOException(e);
        }
    }

    protected ContentResponse get(String url)
    {
        try
        {

            final InputStreamResponseListener listener = new InputStreamResponseListener();
            httpClient.newRequest(url)
                    .timeout(spooferTimeoutMs, TimeUnit.MILLISECONDS)
                    .send(listener);
            final Response response = listener.get(spooferTimeoutMs, TimeUnit.MILLISECONDS);

            //use response listener because spoofer transactions are usually large
            //the default ContentResponse without a listener uses a moderaly sized buffer that may run out of space for
            //typical list of transactions
            try (final InputStream responseContent = listener.getInputStream())
            {
                final String encoding = Charsets.UTF_8.name();
                final String mediaType = response.getHeaders().getStringField(HttpHeader.CONTENT_TYPE);

                return new HttpContentResponse(response, ByteStreams.toByteArray(responseContent)
                            , mediaType
                            , encoding);

            }

        } catch (Exception e)
        {
            LogHelper.log(logger, Level.ERROR, MESSAGE_SEND_ERROR, "Error http get", e);
        }
        return null;
    }


    //TODO: use the POJO from spoofer to set the overrides directly...
    protected String generateOverridePayload(Map<String, String> overrides)
    {
        final JsonObject overridesMap = new JsonObject();
        for (final Map.Entry<String, String> entry : overrides.entrySet())
        {
            final String key = entry.getKey();
            final String value = entry.getValue();
            overridesMap.addProperty(key, value);
        }
        final JsonObject overridesContainer = new JsonObject();
        overridesContainer.add("overrides", overridesMap);

        return GSON.toJson(overridesContainer);

    }

    //todo: extract common spoofer classes and types to its own module
    public enum ReservedOverrideKeys
    {
        /**
         * The name of the template to choose (instead of DefaultResponse or input payload based name); this template name would be given first precedence
         */
        TEMPLATE_OVERRIDE("templateOverride"),
        /**
         * Set this to "true" if you want to store the transactions for an originating guid
         */
        STORE_TRANSACTIONS("storeTransactions");

        private final String name;

        ReservedOverrideKeys(String keyName)
        {
            name = keyName;
        }

        public String getName()
        {
            return name;
        }
    }

    public static class OverridesBuilder
    {
        final private Map<String, String> overrides;

        private OverridesBuilder()
        {
            overrides = new HashMap<>();
        }

        /**
         * Enables storing transactions by default; invoke withoutTransactions() on it if you don't want to enable storing transactions on the spoofer side
         *
         * @return
         */
        public static OverridesBuilder newBuilder()
        {
            return new OverridesBuilder().withTransactions();
        }

        public OverridesBuilder withTransactions()
        {
            overrides.put(ReservedOverrideKeys.STORE_TRANSACTIONS.getName(), "true");
            return this;
        }

        public OverridesBuilder withoutTransactions()
        {
            overrides.remove(ReservedOverrideKeys.STORE_TRANSACTIONS.getName());
            return this;
        }

        public OverridesBuilder withTemplateOverride(String overrideTemplateName)
        {
            overrides.put(ReservedOverrideKeys.TEMPLATE_OVERRIDE.getName(), overrideTemplateName);
            return this;
        }

        public OverridesBuilder withOverrides(Map<String, String> overrides)
        {
            this.overrides.putAll(overrides);
            return this;
        }

        public OverridesBuilder withOverride(String key, String value)
        {
            overrides.put(key, value);

            return this;
        }

        /**
         * returns an unmodifiable map of the currently available overrides
         *
         * @return
         */
        public Map<String, String> build()
        {
            return ImmutableMap.copyOf(overrides);
        }

    }


}
