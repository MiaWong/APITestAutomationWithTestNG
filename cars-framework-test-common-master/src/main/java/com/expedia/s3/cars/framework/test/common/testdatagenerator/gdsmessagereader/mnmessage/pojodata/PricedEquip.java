package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/28/2016.
 */
public class PricedEquip {
    public Equipment equipment;
    public Charge charge;

    public PricedEquip(Node xn)
    {
        Node xn1 = xn.getAttributes().getNamedItem("Equipment");
        final NodeList childNodeList = xn.getChildNodes();
        // foreach (Node Node in xn.ChildNodes)
        for(int i=0; childNodeList.getLength()>0; i++)
        {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":"))
            {
                nodeName = nodeName.substring(nodeName.indexOf(':')+1);
            }
            if ("Equipment".equals(nodeName))
            {
                xn1 = childNodeList.item(i);
                break;
            }
        }
        equipment = new Equipment(xn1);

        Node xn2 = xn.getAttributes().getNamedItem("Charge");
        // for (Node Node in xn.ChildNodes)
        for(int i=0; childNodeList.getLength()>0; i++)
        {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("Charge".equals(nodeName))
            {
                xn2 = childNodeList.item(i);
                break;
            }
        }
        charge = new Charge(xn2);
    }

    public class Equipment
    {
        public int equipType;
        public Description description;

        public Equipment(Node xn)
        {
            Node xn1 = xn.getAttributes().getNamedItem("Description");
            final NodeList childnodeList = xn.getChildNodes();
            //for (Node Node in xn.ChildNodes)
            for(int i=0; childnodeList.getLength()>i; i++)
            {
                String nodeName = childnodeList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                }
                if ("Description".equals(nodeName))
                {
                    xn1 = childnodeList.item(i);
                    break;
                }
            }
            description = new Description(xn1);
            equipType = Integer.valueOf(xn.getAttributes().getNamedItem("EquipType").getTextContent());
        }

        public class Description
        {
            public String descriptionContent;

            public Description(Node xn)
            {
                descriptionContent = xn.getTextContent();
            }
        }
    }

    public class Charge
    {
        public BigDecimal amount;
        public String currencyCode;
        public MinMax minMax;
        public List<Calculation> calculations;

        public Charge(Node xn)
        {
            amount = new BigDecimal(xn.getAttributes().getNamedItem("Amount").getTextContent());
            currencyCode = xn.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            Node xn1 = xn.getAttributes().getNamedItem("MinMax");
            final NodeList chileNodeList = xn.getChildNodes();
            // foreach (Node Node in xn.ChildNodes)
            for(int i=0;chileNodeList.getLength()>i; i++ )
            {
                String nodeName = chileNodeList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                }
                if ("MinMax".equals(nodeName))
                {
                    xn1 = chileNodeList.item(i);
                    break;
                }
            }
            if (xn1 != null)
            {
                minMax = new MinMax(xn1);
            }
            calculations = new ArrayList<>();
            //for (Node Node in xn.ChildNodes)
            for(int i=0; chileNodeList.getLength()>i;i++)
            {
                String nodeName = chileNodeList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                }
                if ("Calculation".equals(nodeName))
                {
                    final Node xn2 = chileNodeList.item(i);
                    final Calculation calculation = new Calculation(xn2);
                    calculations.add(calculation);
                }
            }
        }

        public class MinMax
        {
            public BigDecimal maxCharge;


            public MinMax(Node xn)
            {
                maxCharge = new BigDecimal(xn.getAttributes().getNamedItem("MaxCharge").getTextContent());

            }
        }

        public class Calculation
        {
            public String quantity;
            public BigDecimal unitCharge;
            public String unitName;

            public Calculation(Node xn)
            {
                quantity = xn.getAttributes().getNamedItem("Quantity").getTextContent();
                unitCharge = new BigDecimal(xn.getAttributes().getNamedItem("UnitCharge").getTextContent());
                unitName = xn.getAttributes().getNamedItem("UnitName").getTextContent();
            }
        }
    }
}
