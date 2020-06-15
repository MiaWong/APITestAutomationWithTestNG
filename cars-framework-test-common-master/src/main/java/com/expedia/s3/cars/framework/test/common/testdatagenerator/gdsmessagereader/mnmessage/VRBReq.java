package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;

/**
 * Created by fehu on 12/22/2016.
 */
public class VRBReq {

    final private String idContext;
    final private String surName;

    public String getIdContext() {
        return idContext;
    }

    public String getSurName() {
        return surName;
    }

    public VRBReq(Node request) throws XPathExpressionException {

        final Node uniqueID = PojoXmlUtil.getNodeByTagName(request, "UniqueID");
        this.idContext = uniqueID.getAttributes().getNamedItem("ID_Context").getNodeValue();

        final Node surname = PojoXmlUtil.getNodeByTagName(request, "Surname");
        this.surName = surname.getTextContent();
    }
}
