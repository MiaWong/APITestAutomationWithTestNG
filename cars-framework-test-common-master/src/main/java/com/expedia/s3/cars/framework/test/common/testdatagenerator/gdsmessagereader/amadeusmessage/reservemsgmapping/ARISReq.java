package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by miawang on 2/14/2017.
 */
public class ARISReq {
    public String referenceType;
    public String uniqueReference;

    public ARISReq(Node arisRequest) {
        // Car_RAFCS/bookingIdentifier/referenceType
        final Node referenceTypeNode = PojoXmlUtil.getNodeByTagName(arisRequest, "referenceType");
        if (null != referenceTypeNode) {
            referenceType = referenceTypeNode.getTextContent();
        }

        // Car_RAFCS/bookingIdentifier/uniqueReference
        final Node uniqueReferenceNode = PojoXmlUtil.getNodeByTagName(arisRequest, "uniqueReference");
        if (null != uniqueReferenceNode) {
            uniqueReference = uniqueReferenceNode.getTextContent();
        }
    }
}
