package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by yyang4 on 12/15/2016.
 */
public class URRRReq {
    public String lastName;
    public String pnr;

    public URRRReq(Node vsarReq)
    {
        // Get PNR
        pnr = PojoXmlUtil.getNodeByTagName(vsarReq,"ProviderReservationInfo").getAttributes().getNamedItem("ProviderLocatorCode").getNodeValue();

    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
}
