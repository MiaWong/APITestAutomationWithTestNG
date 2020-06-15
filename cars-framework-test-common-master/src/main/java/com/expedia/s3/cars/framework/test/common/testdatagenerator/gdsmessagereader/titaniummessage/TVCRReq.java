package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.List;


/**
 * Created by v-mechen on 1/8/2017.
 */
public class TVCRReq {
    private String pnr;

    public TVCRReq(Node request) {
        //Read PNR
        final List<Node> uniqueIDNodes = PojoXmlUtil.getNodesByTagName(request, "UniqueID");
        if (null != uniqueIDNodes && !uniqueIDNodes.isEmpty())
        {
            if (null != uniqueIDNodes.get(0).getAttributes().getNamedItem("ID")) {
                this.pnr = uniqueIDNodes.get(0).getAttributes().getNamedItem("ID").getTextContent();
            }
        }

    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }

}

