package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.pojodata;

import org.w3c.dom.Node;

import java.math.BigDecimal;

/**
 * Created by fehu on 12/28/2016.
 */
public class Charge {

    public BigDecimal amount;
    public String currencyCode;
    public String description;
    public boolean includedInRate;

    public Charge(Node xn)
    {
        amount = new BigDecimal(-1);
        currencyCode = "";
        description = "";
        includedInRate = false;
        if (xn.getAttributes().getNamedItem("Amount") != null)
        {
            amount = new BigDecimal(xn.getAttributes().getNamedItem("Amount").getTextContent().trim());
        }
        if (xn.getAttributes().getNamedItem("CurrencyCode") != null)
        {
            currencyCode = xn.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();
        }
        if (xn.getAttributes().getNamedItem("Description") != null)
        {
            description = xn.getAttributes().getNamedItem("Description").getTextContent().trim();
        }
        if (xn.getAttributes().getNamedItem("IncludedInRate") != null)
        {
            if ("true".equals(xn.getAttributes().getNamedItem("IncludedInRate").getTextContent().trim()))
            {
                includedInRate = true;
            }
        }
    }
}
