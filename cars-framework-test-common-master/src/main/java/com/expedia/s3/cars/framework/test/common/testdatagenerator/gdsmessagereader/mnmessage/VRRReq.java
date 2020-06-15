package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

/**
 * Created by fehu on 12/26/2016.
 */
public class VRRReq {

    private String idContext;

    public String getIdContext() {
        return idContext;
    }

    public VRRReq(Node node) throws XPathExpressionException {
         final Node referenceCode = PojoXmlUtil.getNodeByTagName(node,"Reference");
        if(referenceCode!=null)
        {
            this.idContext = referenceCode.getAttributes().getNamedItem("ID_Context").getTextContent();
        }
    }
}
