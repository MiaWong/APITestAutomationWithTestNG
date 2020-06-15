package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by miawang on 11/29/2016.
 */
public class ASCSGDSMsgReadHelper {
    private ASCSGDSMsgReadHelper() {
    }

    /**
     * @param verificationContext
     * @param nodeTag             like GDSMsgNodeTags.RESPONSE    GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE
     * @return
     */
    public static Node getSpecifyNodeFromSpoofer(BasicVerificationContext verificationContext, String action, String nodeTag) {
        final NodeList specifyNodes = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", nodeTag);

        if (specifyNodes.getLength() > 0) {
            if(specifyNodes.getLength() > 1 && CommonConstantManager.ActionType.RESERVE.equals(action) && nodeTag.equals(GDSMsgNodeTags.AmadeusNodeTags.APCM_PNR_AME2_RESPONSE_TYPE))
            {
//                System.out.println("spooferxml reserve" + PojoXmlUtil.toString(specifyNodes.item(1)));
                return specifyNodes.item(1);
            }

            if(specifyNodes.getLength() > 2 && CommonConstantManager.ActionType.GETRESERVATION.equals(action) && nodeTag.equals(GDSMsgNodeTags.AmadeusNodeTags.APCM_PNR_AME2_RESPONSE_TYPE))
            {
//                System.out.println("spooferxml reservation" + PojoXmlUtil.toString(specifyNodes.item(specifyNodes.getLength()-1)));
                return specifyNodes.item(specifyNodes.getLength()-3);
            }
            return specifyNodes.item(0);
        }
        return null;
    }

    public static NodeList getSpecifyNodeListFromSpoofer(BasicVerificationContext verificationContext, String nodeTag) {
        final NodeList specifyNodes = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", nodeTag);

        if (specifyNodes.getLength() > 0) {
            return specifyNodes;
        }
        return null;
    }
}
