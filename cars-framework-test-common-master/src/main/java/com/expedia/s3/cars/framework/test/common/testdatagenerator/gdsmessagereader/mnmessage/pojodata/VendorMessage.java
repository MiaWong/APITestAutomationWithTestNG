package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by fehu on 12/28/2016.
 */
public class VendorMessage {

    public SubSection subSection;
    public Title title;

    public VendorMessage(Node xn)
    {
        Node xn1 = xn.getAttributes().getNamedItem("SubSection");
        final NodeList childList = xn.getChildNodes();
        for(int i=0; childList.getLength()>i; i++)
        {
            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("SubSection".equals(nodeName))
            {
                xn1 = childList.item(i);
                break;
            }
        }
        subSection = new SubSection(xn1);


        Node xn2 = xn.getAttributes().getNamedItem("Title");
        for(int i=0; childList.getLength()>i; i++)
        {

            String nodeName = childList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Title".equals(nodeName))
            {
                xn2 = childList.item(i);
                break;
            }
        }
        title = new Title(xn2);

    }
    public class Title
    {
        public String titleContent;

        public Title(Node xn)
        {
            titleContent = xn.getTextContent();
        }
    }

    public class SubSection
    {
        public Paragraph paragraph;

        public SubSection(Node xn)
        {
            Node xn1 = xn.getAttributes().getNamedItem("Paragraph");
            final NodeList childList = xn.getChildNodes();
            for(int i=0; childList.getLength()>i; i++)
            {
                String nodeName = childList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                }
                if ("Paragraph".equals(nodeName))
                {
                    xn1 = childList.item(i);
                    break;
                }
            }
            paragraph = new Paragraph(xn1);
        }

        public class Paragraph
        {
            public Text text;

            public Paragraph(Node xn)
            {
                Node xn1 = xn.getAttributes().getNamedItem("Text");
                final NodeList childList = xn.getChildNodes();
                for(int i=0; childList.getLength()>i; i++)
                {
                    String nodeName = childList.item(i).getNodeName();
                    if (nodeName.contains(":")) {
                        nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                    }
                    if ("Text".equals(nodeName))
                    {
                        xn1 = childList.item(i);
                        break;
                    }
                }
                text = new Text(xn1);
            }

            public class Text
            {
                public String textContent;

                public Text(Node xn)
                {
                    textContent = xn.getTextContent();
                }
            }
        }
    }
}
