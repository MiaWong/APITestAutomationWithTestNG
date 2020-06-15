package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by fehu on 12/28/2016.
 */
public class Coverage {
    public String coverageType;
    public String code;
    public Details details;

    public Coverage(Node xn)
    {
        if (null == xn.getAttributes().getNamedItem("CoverageType"))
        {
            coverageType = "";
        }
        else
        {
            coverageType = xn.getAttributes().getNamedItem("CoverageType").getTextContent();

        }
        if (null == xn.getAttributes().getNamedItem("Code"))
        {
            code = "";
        }
        else
        {
            code = xn.getAttributes().getNamedItem("Code").getTextContent();
        }
        Node xn1 = xn.getAttributes().getNamedItem("Details");
        final NodeList childNodeList = xn.getChildNodes();
        // foreach (Node Node in xn.ChildNodes)
        for(int i=0; childNodeList.getLength()>i; i++ )
        {
            String nodeName = childNodeList.item(i).getNodeName();

            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Details".equals(nodeName))
            {
                xn1 = childNodeList.item(i);
                break;
            }
        }
        details = new Details(xn1);
    }
}
