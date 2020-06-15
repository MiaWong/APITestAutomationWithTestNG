package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by fehu on 12/28/2016.
 */
public class Address {
    public StreetNmbr streetNmbr;
    public AddressLine addressLine;
    public CityName cityName;
    public PostalCode postalCode;
    public CountryName countryName;

    public Address(Node xn)
    {
        final NodeList childList = xn.getChildNodes();

        //get StreetNmbr
        getStreetNum(xn, childList);

        //get AddressLine
        getAddressLine(xn, childList);

        //get CityName
        getCityName(xn, childList);

        //get PostalCode
        getPostalCode(xn, childList);

        //get CountryName
        getCountryName(xn, childList);
    }

    private void getCountryName(Node xn, NodeList childList) {
        Node xn5 = xn.getAttributes().getNamedItem("CountryName");
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName =  childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("CountryName".equals(nodeName))
            {
                xn5 = childList.item(i);
                break;
            }
        }
        countryName = new CountryName(xn5);
    }

    private void getPostalCode(Node xn, NodeList childList) {
        Node xn4 = xn.getAttributes().getNamedItem("PostalCode");
        for(int i=0; childList.getLength()>i; i++)
        {

            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("PostalCode".equals(nodeName))
            {
                xn4 = childList.item(i);
                break;
            }
        }
        postalCode = new PostalCode(xn4);
    }

    private void getCityName(Node xn, NodeList childList) {
        Node xn3 = xn.getAttributes().getNamedItem("CityName");
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("CityName".equals(nodeName))
            {
                xn3 = childList.item(i);
                break;
            }
        }
        cityName = new CityName(xn3);
    }

    private void getAddressLine(Node xn, NodeList childList) {
        Node xn2 = xn.getAttributes().getNamedItem("AddressLine");
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("AddressLine".equals(nodeName))
            {
                xn2 = childList.item(i);
                break;
            }
        }
        addressLine = new AddressLine(xn2);
    }

    private void getStreetNum(Node xn, NodeList childList) {
        Node xn1 = xn.getAttributes().getNamedItem("StreetNmbr");
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("StreetNmbr".equals(nodeName))
            {
                xn1 = childList.item(i);
                break;
            }
        }
        streetNmbr = new StreetNmbr(xn1);
    }

    public class StreetNmbr
    {
        public String streetNmbrContent;

        public StreetNmbr(Node xn)
        {
            streetNmbrContent = xn.getTextContent();
        }
    }

    public class AddressLine
    {
        public String addressLineContent;

        public AddressLine(Node xn)
        {
            addressLineContent = xn.getTextContent();
        }
    }

    public class CityName
    {
        public String cityNameContent;

        public CityName(Node xn)
        {
            cityNameContent = xn.getTextContent();
        }
    }

    public class PostalCode
    {
        public String postalCodeContent;

        public PostalCode(Node xn)
        {
            postalCodeContent = xn.getTextContent();
        }
    }

    public class CountryName
    {
        public String code;

        public CountryName(Node xn)
        {
            code = xn.getAttributes().getNamedItem("Code").getTextContent();
        }
    }
}