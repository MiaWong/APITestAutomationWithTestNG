package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/**
 * Created by fehu on 11/22/2016.
 */
public class CurrencyConvertUtil {

    private static Client client;
    private static String serviceUrl = "http://fxrswebservice.int-maui.karmalab.net/xmlfxrs/fxrs/v1";

    private CurrencyConvertUtil() {
    }

    @SuppressWarnings("PMD")
    public static Document toXml(String xmlStr) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = null;
        if (!StringUtils.isEmpty(xmlStr)) {
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        }

        return doc;

    }

    public static String getExchangeRate(String baseCurCode, String targetCurCode) throws Exception {

        final Document s = toXml(sendMessage(createSOAPRequest(baseCurCode, targetCurCode)).toString());
        final XPath xpath = XPathFactory.newInstance().newXPath();
        final Node rateNode = (Node) xpath.compile
                ("//Envelope/Body/FXRatesResponse/FXRateList/FXRate/Rate")
                .evaluate(s, XPathConstants.NODE);

        return rateNode.getTextContent();

    }

    public static Object sendMessage(String content) {

        client = Client.create();
        final WebResource resource = client.resource(serviceUrl);

        final ClientResponse response = resource
                .type("application/soap+xml")
                .accept("application/soap+xml")
                .post(ClientResponse.class, content);
        return response.getEntity(String.class);

    }

    private static String createSOAPRequest(String baseCurCode, String targetCurCode) throws Exception {
        if (baseCurCode == null) {
            throw new Exception("baseCurCode");
        }

        if (targetCurCode == null) {
            throw new Exception("targetCurCode");
        }

        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        final Document requedocRequestst = dbf.newDocumentBuilder().newDocument();

        final Element rootElement = requedocRequestst.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Envelope");

        final Node body = requedocRequestst.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Body");
        rootElement.appendChild(body);

        final Node fXRatesRequest = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "FXRatesRequest");
        body.appendChild(fXRatesRequest);
        final Node messageInfo = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "MessageInfo");
        final Node createDateTime = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "CreateDateTime");
        createDateTime.setTextContent(DateTime.getInstanceByDateTime(new Date()).getDateTimeString());
        messageInfo.appendChild(createDateTime);
        final Node fXRateQueryList = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "FXRateQueryList");
        final Node fXRateQuery = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "FXRateQuery");
        final Node baseCurrencyCode = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "BaseCurrencyCode");
        baseCurrencyCode.setTextContent(baseCurCode);
        final Node targetCurrencyCode = requedocRequestst.createElementNS("urn:expedia:xmlapi:fxrs:v1", "TargetCurrencyCode");
        targetCurrencyCode.setTextContent(targetCurCode);
        fXRateQueryList.appendChild(fXRateQuery);
        fXRateQuery.appendChild(baseCurrencyCode);
        fXRateQuery.appendChild(targetCurrencyCode);
        fXRatesRequest.appendChild(messageInfo);
        fXRatesRequest.appendChild(fXRateQueryList);

        return PojoXmlUtil.toString(rootElement);

    }
}
