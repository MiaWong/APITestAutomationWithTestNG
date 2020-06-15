package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.List;


/**
 * Created by v-mechen on 1/8/2017.
 */
public class TVCRRsp {
    private String bookingStateCode;

    public TVCRRsp(Node response) {
        //Read bookingStateCode
        final List<Node> vehCancelRSCoreNodes = PojoXmlUtil.getNodesByTagName(response, "VehCancelRSCore");
        if (null != vehCancelRSCoreNodes && !vehCancelRSCoreNodes.isEmpty())
        {
            if (null != vehCancelRSCoreNodes.get(0).getAttributes().getNamedItem("CancelStatus")) {
                this.bookingStateCode = vehCancelRSCoreNodes.get(0).getAttributes().getNamedItem("CancelStatus").getTextContent();
            }
        }

    }

    public String getBookingStateCode() {
        return bookingStateCode;
    }

    public void setBookingStateCode(String bookingStateCode) {
        this.bookingStateCode = bookingStateCode;
    }

}

