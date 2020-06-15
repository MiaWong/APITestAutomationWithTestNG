package com.expedia.s3.cars.framework.test.common.execution.requestgeneration;

import com.expedia.s3.cars.framework.test.common.utils.HttpMessageSendUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miawang on 3/10/2017.
 */
public class ServiceHealthCheck {
    Logger logger = Logger.getLogger(getClass());

    /**
     *
     * @param url   value should be SettingsProvider.SERVICE_ADDRESS,
     * @param httpClient
     * @return
     * @throws IOException
     */
    public String isSserviceHealthOperational(String url, HttpClient httpClient) throws IOException {
        final NodeList txtResponse = getWebResponse(url, httpClient);
        final String svcResponse = verifyResponse(txtResponse);
        String eMsg = null;
        if (null != svcResponse && !svcResponse.equals("Operational")) {
            eMsg = "Service Health of " + url.substring(7, url.lastIndexOf('/')) + " is not Operational. Value returned is: " + svcResponse;
        }
        return eMsg;
    }

    private NodeList getWebResponse(String url, HttpClient httpClient) throws IOException {
        //send set request
        final HttpMessageSendUtil httpMessageSendUtil = new HttpMessageSendUtil();
        httpMessageSendUtil.setHttpClient(httpClient);
        httpMessageSendUtil.setServiceUrl(url.substring(0, url.lastIndexOf('/')) + "/ServiceHealth");
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/xml");
        httpMessageSendUtil.setRequestHeaders(headers);

        final ContentResponse response = httpMessageSendUtil.sendHttpMessage();
        if(logger.isDebugEnabled()) {
            logger.debug("detalirequest: " + response.getContentAsString());
        }

        // Load Response in doc
        final Document docResponse = PojoXmlUtil.stringToXml(response.getContentAsString());
        if (null != docResponse) {
            return docResponse.getElementsByTagNameNS("*", "ServiceHealthReport");
        }
        return null;
    }

    private String verifyResponse(NodeList txtResponse) {
        if (txtResponse != null) {
            final Node serviceHealthNode = PojoXmlUtil.getNodeByTagName(txtResponse.item(0), "Status");
            if (serviceHealthNode != null) {
                return serviceHealthNode.getTextContent();
            }
        }
        return null;
    }
}