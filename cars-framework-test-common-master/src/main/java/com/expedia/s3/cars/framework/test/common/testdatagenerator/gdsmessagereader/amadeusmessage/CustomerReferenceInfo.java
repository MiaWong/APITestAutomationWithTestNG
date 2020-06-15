package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by miawang on 10/18/2017.
 */
public class CustomerReferenceInfo {
    final private String referenceQualifier;
    final private String referenceNumber;

    public CustomerReferenceInfo(Node customerReferenceInfoNode) {
        referenceQualifier = PojoXmlUtil.getNodeByTagName(customerReferenceInfoNode, "referenceQualifier").getTextContent().toString();
        referenceNumber = PojoXmlUtil.getNodeByTagName(customerReferenceInfoNode, "referenceNumber").getTextContent().toString();
    }

    public String getReferenceQualifier()
    {
        return referenceQualifier;
    }

    public String getReferenceNumber()
    {
        return referenceNumber;
    }
}
