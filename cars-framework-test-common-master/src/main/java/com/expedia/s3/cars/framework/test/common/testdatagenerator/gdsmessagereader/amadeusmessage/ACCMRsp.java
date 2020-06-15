package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

/**
 * Created by miawang on 2/24/2017.
 */
public class ACCMRsp {
    public String getCommandCrypticReplyDetails(Node doc) {
        String commandCrypticReplyDetails = null;
        final Node commandCrypticReplyNode = PojoXmlUtil.getNodeByTagName(doc, "Command_CrypticReply");
        if (null != commandCrypticReplyNode) {
            commandCrypticReplyDetails = PojoXmlUtil.getNodeByTagName(commandCrypticReplyNode, "longTextString").getTextContent();
        }
        return commandCrypticReplyDetails;
    }
}
