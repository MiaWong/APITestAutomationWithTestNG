package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.cache.jaxb.DynamicJAXBCache;
import com.sun.xml.fastinfoset.dom.DOMDocumentParser;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mpaudel on 5/22/16.
 */
public class PojoXmlUtil
{
    private static final DynamicJAXBCache JAXB_CACHE = new DynamicJAXBCache();
    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY;
    private static final Logger LOGGER = Logger.getLogger(PojoXmlUtil.class);

    static
    {
        DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    }

    private PojoXmlUtil()
    {
        //Nop
    }

    public static String toString(Node doc)
    {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try
        {
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            final StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return  writer.getBuffer().toString();
        } catch (TransformerException e)
        {
            LOGGER.warn("An error occurred.", e);
        }
        return null;
    }

    public static Document stringToXml(String xmlStr)
    {
        try
        {
            return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e)
        {
            LOGGER.warn("An error occurred.", e);
        }
        return null;
    }

    public static byte[] xml2bytes(Node node)
    {
        try
        {
            final Source source = new DOMSource(node);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Result result = new StreamResult(out);
            final TransformerFactory factory = TransformerFactory.newInstance();
            final Transformer transformer = factory.newTransformer();
            transformer.transform(source, result);
            return out.toByteArray();
        } catch (Exception e)
        {
            LOGGER.warn("An error occurred.", e);
        }
        return new byte[0];
    }

    public static <T> T docToPojo(Node response, Class<T> reqType)
    {
        try
        {
            final JAXBContext jaxbContext = (JAXBContext) JAXB_CACHE.get(reqType);
            final byte[] responseByteArray = xml2bytes(response);
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(responseByteArray);
            final Unmarshaller e = jaxbContext.createUnmarshaller();
            return (T) e.unmarshal(inputStream);
        } catch (Exception e)
        {
            LOGGER.warn("An error occurred.", e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Document pojoToDoc(Object requestMessage)
    {
        final String bindingPackageName = requestMessage.getClass().getPackage().getName();
        String bindingElementName = requestMessage.getClass().getSimpleName();
        try
        {
            // trim "Type" from the end of the name
            if (bindingElementName.endsWith("Type"))
            {
                bindingElementName = bindingElementName.substring(0, bindingElementName
                        .lastIndexOf("Type"));
            }

            final String bindingNamespace = "urn:" + bindingPackageName.replace('.', ':');
            final JAXBContext jaxbContext = (JAXBContext) JAXB_CACHE.get(bindingPackageName);

            final Marshaller marshaller = jaxbContext.createMarshaller();
            final Document request = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();

            final JAXBElement jaxbElement = new JAXBElement(new QName(bindingNamespace, bindingElementName),
                    requestMessage.getClass(), requestMessage);
            marshaller.marshal(jaxbElement, request);

            return request;
        } catch (Exception e)
        {
            LOGGER.warn("An error occurred.", e);
        }
        return null;
    }

    public static List<String> getXmlFieldValue(Object objDoc, String fieldName)
    {
        final List<String> fieldValueList = new ArrayList<String>();
        final Document xmlDoc = pojoToDoc(objDoc);
        if(null != xmlDoc) {
            final NodeList nodeList = xmlDoc.getElementsByTagNameNS("*",fieldName);
            for (int i = 0; i < nodeList.getLength(); i++) {
                final Node node = nodeList.item(i);
                //Ignore CarProductTokenInvalidError
                if (!node.getTextContent().contains("Invalid CarProductToken")) {
                    fieldValueList.add(node.getTextContent());
                }
            }
        }
        return fieldValueList;
    }
    /**
     * @param node
     * @param name
     * @return
     */
    public static Node getNodeByTagName(Node node, String name, String nameSpace) {
        final Element elem = (Element) node;
        return elem.getElementsByTagNameNS(nameSpace,name).item(0);
    }

    /**
     * @param node
     * @param name
     * @return
     */
    public static List<Node> getNodesByTagName(Node node, String name, String nameSpace) {
        if(null != node) {
            final Element elem = (Element) node;
            final NodeList nodelist = elem.getElementsByTagNameNS(nameSpace, name);
            final List<Node> result = new ArrayList<Node>();
            for (int i = 0; i < nodelist.getLength(); i++) {
                result.add(nodelist.item(i));
            }
            return result;
        }
        return null;
    }

    /**
     * @param node
     * @param name
     * @return
     */
    public static Node getNodeByTagName(Node node, String name) {
        final Element elem = (Element) node;
        return elem.getElementsByTagNameNS("*",name).item(0);
    }

    /**
     * @param node
     * @param name
     * @return
     */
    public static List<Node> getNodesByTagName(Node node, String name) {
        if(null != node) {
            final Element elem = (Element) node;
            final NodeList nodelist = elem.getElementsByTagNameNS("*", name);
            final List<Node> result = new ArrayList<Node>();
            for (int i = 0; i < nodelist.getLength(); i++) {
                result.add(nodelist.item(i));
            }
            return result;
        }
        return null;
    }

    public static Node getSpecifiedXMLNode(NodeList nodeList, String nodeName) {
        for (int i = 0; nodeList.getLength() > i; i++) {
            if (nodeName.compareToIgnoreCase(nodeList.item(i).getNodeName()) == 0) {
                return nodeList.item(i);
            }
        }
        return null;
    }

    public static String getRandomGuid()
    {
        return UUID.randomUUID().toString();
    }

    public static String fiBytesToString(byte[] fiData) {
        String formattedOutput = null;
        if(null != fiData) {
            final ByteArrayInputStream is = new ByteArrayInputStream(fiData);
            final DOMDocumentParser parser = new DOMDocumentParser();

            try {
                final DocumentBuilderFactory e = DocumentBuilderFactory.newInstance();
                e.setNamespaceAware(true);
                final Document buffer1 = e.newDocumentBuilder().newDocument();
                parser.parse(buffer1, is);
                formattedOutput = toString(buffer1.getDocumentElement());
            } catch (Exception var7) {
                final StringBuffer buffer = new StringBuffer(32);
                buffer.append("Unable to parse FI data (").append(var7.getClass().getCanonicalName()).append("): ").append(var7.getLocalizedMessage());
                formattedOutput = buffer.toString();
            }
        }

        return formattedOutput;
    }

    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }
    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport, String scenarioName) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        final SpooferTransport.OverridesBuilder overridesBuilder = SpooferTransport.OverridesBuilder.newBuilder();

        overridesBuilder.withOverride("ScenarioName", scenarioName);

        spooferTransport.setOverrides(overridesBuilder.build(), randomGuid);

        return randomGuid;
    }

    public static String generateNewOrigGUIDwithOverrideTemplate(SpooferTransport spooferTransport, String overrideTemplateName) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        final SpooferTransport.OverridesBuilder overridesBuilder = SpooferTransport.OverridesBuilder.newBuilder();
        overridesBuilder.withTemplateOverride(overrideTemplateName);
        spooferTransport.setOverrides(overridesBuilder.build(), randomGuid);

        return randomGuid;
    }

    public  static String getEnvironment()
    {
       final  String env =  AppConfig.resolveStringValue("${environment.name}");
         if (StringUtil.isNotBlank(env))
         {
             return env;
         }
         else
         {
             return AppConfig.getEnvironment();
         }

    }
}