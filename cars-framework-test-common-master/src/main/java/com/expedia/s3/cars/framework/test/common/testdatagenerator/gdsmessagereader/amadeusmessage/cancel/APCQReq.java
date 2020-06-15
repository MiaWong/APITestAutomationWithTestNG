package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.cancel;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by miawang on 8/8/2017.
 */
public class APCQReq
{
    public APCQReq(Node apcqRequest, StringBuffer eMsg)
    {
        //verify PNR_cancel request, entryType = E, optionCode = 11, identifier = ST, number in APCQ should equal in APRQ
        final String entryType = PojoXmlUtil.getNodeByTagName(apcqRequest, "entryType").getTextContent();
        final String optionCode = PojoXmlUtil.getNodeByTagName(apcqRequest, "optionCode").getTextContent();
        final String identifier = PojoXmlUtil.getNodeByTagName(apcqRequest, "identifier").getTextContent();
        final String number_APCQ = PojoXmlUtil.getNodeByTagName(apcqRequest, "number").getTextContent();
        final String number_APRQ = PojoXmlUtil.getNodeByTagName(apcqRequest, "number").getTextContent();
        if (!"E".equals(entryType))
        {
            eMsg.append("EntryType " + entryType + " in APCQ not equal 'E'. \n");
        }
        if (!"11".equals(optionCode))
        {
            eMsg.append("OptionCode " + optionCode + " in APCQ not equal '11'. \n");
        }
        if (!"ST".equals(identifier))
        {
            eMsg.append("Identifier " + identifier + " in APCQ not equal 'ST'. \n");
        }
        if (number_APCQ != number_APRQ)
        {
            eMsg.append("number_APRQ " + number_APRQ + " not equal number_APCQ " + number_APCQ);
        }
    }
}
