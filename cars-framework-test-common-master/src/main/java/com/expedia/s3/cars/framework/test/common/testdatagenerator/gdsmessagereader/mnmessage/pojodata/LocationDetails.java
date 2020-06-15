package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/28/2016.
 */
public class LocationDetails {
    public String codeContext;
    public String code;
    public Address address;
    public List<Telephone> telephones;

    public LocationDetails(Node xn)
    {
        codeContext = xn.getAttributes().getNamedItem("CodeContext").getTextContent();
        code = xn.getAttributes().getNamedItem("Code").getTextContent();

        Node xn1 = xn.getAttributes().getNamedItem("Address");
        final NodeList childList = xn.getChildNodes();
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Address".equals(nodeName))
            {
                xn1 = childList.item(i);
                break;
            }
        }
        address = new Address(xn1);

        Node xn2;
        telephones = new ArrayList<>();
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Telephone".equals(nodeName))
            {
                xn2 = childList.item(i);
                final Telephone telephone = new Telephone(xn2);
                telephones.add(telephone);
            }
        }
    }

    public class Telephone
    {
        public String phoneNumber;
        public int phoneTechType;

        public Telephone(Node xn)
        {
            phoneNumber = xn.getAttributes().getNamedItem("PhoneNumber").getTextContent();
            phoneTechType = Integer.valueOf(xn.getAttributes().getNamedItem("PhoneTechType").getTextContent());
        }
    }
}
