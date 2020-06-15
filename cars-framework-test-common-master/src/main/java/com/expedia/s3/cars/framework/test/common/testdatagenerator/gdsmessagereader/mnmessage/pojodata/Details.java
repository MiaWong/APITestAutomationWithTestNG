package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;

/**
 * Created by fehu on 12/28/2016.
 */
public class Details {
    public Charge charge;
    public Details(Node xn)
    {
        Node xn1 = xn.getAttributes().getNamedItem("Charge");
        final NodeList childNodeList = xn.getChildNodes();
        // foreach (Node Node in xn.ChildNodes)
        for(int i=0;childNodeList.getLength()>i; i++)
        {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Charge".equals(nodeName))
            {
                xn1 = childNodeList.item(i);
                break;
            }
        }
        charge = new Charge(xn1);
    }

    public class Charge
    {
        public BigDecimal amount;
        public boolean includedInRate;

        public Charge(Node xn)
        {
            amount = new BigDecimal(xn.getAttributes().getNamedItem("Amount").getTextContent().trim());
            if (xn.getAttributes().getNamedItem("IncludedInRate") == null)
            {
                includedInRate = false;
            }
            else
            {
                final String s = xn.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim();
                if ("true".equals(s))
                {
                    includedInRate = true;
                }
                else
                {
                    includedInRate = false;
                }

            }
        }
    }
}
