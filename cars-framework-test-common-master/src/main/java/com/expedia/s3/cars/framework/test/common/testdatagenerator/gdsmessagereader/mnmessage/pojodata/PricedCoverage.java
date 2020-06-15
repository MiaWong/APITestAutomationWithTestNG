package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by fehu on 12/28/2016.
 */
public class PricedCoverage {

    public Charge charge;
    public Coverage coverage;

    public PricedCoverage(Node xn) {
        Node xn1 = xn.getAttributes().getNamedItem("Charge");
        Node xn2 = xn.getAttributes().getNamedItem("Coverage");
        final NodeList childNodeList = xn.getChildNodes();
        //foreach (Node Node in xn.ChildNodes)
        for (int i = 0; childNodeList.getLength() > i; i++) {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Charge".equals(nodeName)) {
                xn1 = childNodeList.item(i);
                break;
            }
        }
        for (int i = 0; childNodeList.getLength() > i; i++) {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Coverage".equals(nodeName)) {
                xn2 = childNodeList.item(i);
                break;
            }
        }

        charge = new Charge(xn1);

        coverage = new Coverage(xn2);
    }



}