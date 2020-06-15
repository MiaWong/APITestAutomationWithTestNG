package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/28/2016.
 */
public class RentalRate {
    public List<VehicleCharge> vehicleCharges;
    public RateRestrictions rateRestrictions;

    public RentalRate(Node xn)
    {
        final NodeList childNodeList = xn.getChildNodes();

        //get VehicleCharge
        getVehicleCharge(childNodeList);

        //get RateRestrictions
        for(int i=0; childNodeList.getLength()> i; i++)
        {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("RateRestrictions".equals(nodeName))
            {
                rateRestrictions = new RateRestrictions(childNodeList.item(i));
            }
        }

    }

    private void getVehicleCharge(NodeList childNodeList) {
        vehicleCharges = new ArrayList<>();
        for(int i=0; childNodeList.getLength()> i; i++)
        {
            String nodeName = childNodeList.item(i).getNodeName();
            if (nodeName.contains(":")) {
                nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
            }
            if ("VehicleCharges".equals(nodeName))
            {
                final NodeList vehicleChargeNodeList = childNodeList.item(i).getChildNodes();
                // for (Node vehicleChargeNode in Node.ChildNodes)
                for(int j=0; vehicleChargeNodeList.getLength()>j; j++)
                {
                    if ("VehicleCharge".equals(vehicleChargeNodeList.item(j).getNodeName()))
                    {
                        final VehicleCharge vehicleCharge = new VehicleCharge(vehicleChargeNodeList.item(j));
                        vehicleCharges.add(vehicleCharge);
                    }
                }
            }
        }
    }

    public class VehicleCharge
    {
        public BigDecimal amount;
        public String currencyCode;
        public String purpose;

        public VehicleCharge(Node xn)
        {
            amount = new BigDecimal(xn.getAttributes().getNamedItem("Amount").getTextContent());
            currencyCode = (null ==  xn.getAttributes().getNamedItem("CurrencyCode")) ?  null : xn.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            purpose = xn.getAttributes().getNamedItem("Purpose").getTextContent();
        }
    }

    public class RateRestrictions
    {
        public MinimumAge minimumAge;
        public MaximumAge maximumAge;

        public RateRestrictions(Node xn)
        {
            final NodeList childNodeList = xn.getChildNodes();
            //get MinimumAge
            getMinimumAge(xn, childNodeList);

            //get MaximumAge
            getMaximumAge(xn, childNodeList);
        }

        private void getMaximumAge(Node xn, NodeList childNodeList) {
            Node xn2 = xn.getAttributes().getNamedItem("MaximumAge");
            for (int i = 0 ; childNodeList.getLength()>i; i++)
            {
                String nodeName = childNodeList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':') + 1);
                }
                if ("MaximumAge".equals(nodeName))
                {
                    xn2 = childNodeList.item(i);
                    break;
                }
            }
            if (xn2 != null)
            {
                maximumAge = new MaximumAge(xn2);
            }
        }

        private void getMinimumAge(Node xn, NodeList childNodeList) {
            Node xn1 = xn.getAttributes().getNamedItem("MinimumAge");
            for(int i=0; childNodeList.getLength()>i; i++)
            {
                String nodeName = childNodeList.item(i).getNodeName();
                if (nodeName.contains(":")) {
                    nodeName = nodeName.substring(nodeName.indexOf(':')+1);
                }
                if ("MinimumAge".equals(nodeName))
                {
                    xn1 = childNodeList.item(i);
                    break;
                }
            }
            if (xn1 != null)
            {
                minimumAge = new MinimumAge(xn1);
            }
        }

        public class MinimumAge
        {
            public int minimumAgeContent;

            public MinimumAge(Node xn)
            {
                minimumAgeContent = Integer.valueOf(xn.getTextContent());
            }
        }

        public class MaximumAge
        {
            public int maximumAgeContent;

            public MaximumAge(Node xn)
            {
                maximumAgeContent = Integer.valueOf(xn.getTextContent());
            }
        }
    }
}
