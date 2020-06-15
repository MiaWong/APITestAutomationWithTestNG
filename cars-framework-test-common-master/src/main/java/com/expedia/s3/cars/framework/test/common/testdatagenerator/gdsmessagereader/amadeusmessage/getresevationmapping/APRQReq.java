package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.getresevationmapping;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by miawang on 2/14/2017.
 */
public class APRQReq {
    public String type;
    public String pnrNumber;

    public APRQReq(Node aprqRequest) {
        final Node typeNode = PojoXmlUtil.getNodeByTagName(aprqRequest, "type");
        if (null != typeNode) {
            type = typeNode.getTextContent();
        }

        // Amadeus PNR_Retrieve Request reservation/controlNumber.
        final Node pnrNumberNode = PojoXmlUtil.getNodeByTagName(aprqRequest, "controlNumber");
        if (null != pnrNumberNode) {
            pnrNumber = pnrNumberNode.getTextContent();
        }
    }
}