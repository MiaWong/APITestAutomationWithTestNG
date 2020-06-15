package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by miawang on 11/29/2016.
 */
public class TiSCSGDSMsgReadHelper {
    private TiSCSGDSMsgReadHelper() {
    }

    @Deprecated
    public static String getReferenceIdFromTVRRReq(BasicVerificationContext verificationContext) {
        final NodeList tvrrReqs = verificationContext.getSpooferTransactions().getElementsByTagName
                (GDSMsgNodeTags.TitaniumNodeTags.TVRR_REQUEST_TYPE);
        if (tvrrReqs.getLength() > 0) {
            return PojoXmlUtil.getNodeByTagName(tvrrReqs.item(tvrrReqs.getLength() - 1),
                    GDSMsgNodeTags.TitaniumNodeTags.REFERENCE).getAttributes().getNamedItem
                    (GDSMsgNodeTags.TitaniumNodeTags.ID).getTextContent();
        }
        return "";
    }

    public static String getRateReferenceFromTVRRReq(BasicVerificationContext verificationContext) {
        final NodeList tvrrReqs = verificationContext.getSpooferTransactions().getElementsByTagName
                (GDSMsgNodeTags.TitaniumNodeTags.TVRR_REQUEST_TYPE);
        if (tvrrReqs.getLength() > 0) {
            return PojoXmlUtil.getNodeByTagName(tvrrReqs.item(tvrrReqs.getLength() - 1),
                    GDSMsgNodeTags.TitaniumNodeTags.RATE_REFERENCE).getTextContent();
        }
        return "";
    }

    public static Node getTVRRRspVehAvailFromTVARByReferenceID(BasicVerificationContext verificationContext, String referenceId) {
        if (!StringUtils.isEmpty(referenceId)) {
            final NodeList tvarRsp = verificationContext.getSpooferTransactions().getElementsByTagName
                    (GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);
            if (tvarRsp.getLength() > 0) {
                final List<Node> vehAvails = PojoXmlUtil.getNodesByTagName(tvarRsp.item(tvarRsp.getLength() - 1),
                        GDSMsgNodeTags.TitaniumNodeTags.VEHAVAIL);
                return getTVRRRspVehAvailFromTVARByReferenceIDLoop(vehAvails, referenceId);
            }
        }

        return null;
    }

    private static Node getTVRRRspVehAvailFromTVARByReferenceIDLoop(List<Node> vehAvails, String referenceId) {
        for (final Node vehAvail : vehAvails) {
            final Node reference = PojoXmlUtil.getNodeByTagName(vehAvail, GDSMsgNodeTags.TitaniumNodeTags.REFERENCE);
            if (reference != null && reference.getAttributes().getNamedItem(GDSMsgNodeTags.TitaniumNodeTags.ID).
                    getTextContent().equals(referenceId)) {
                return vehAvail;
            }
        }
        return null;
    }

    public static Node getTVRRRspVehAvailFromTVARBySIPP(Node otaVehAvailRateRS, String sipp) {
        if (!StringUtils.isEmpty(sipp) && otaVehAvailRateRS != null) {
            final List<Node> vehAvails = PojoXmlUtil.getNodesByTagName(otaVehAvailRateRS, GDSMsgNodeTags.TitaniumNodeTags.VEHAVAIL);
            for (final Node vehAvail : vehAvails) {
                final Node vehMakeModel = PojoXmlUtil.getNodeByTagName(vehAvail, GDSMsgNodeTags.TitaniumNodeTags.VEHMAKEMODEL);
                if (vehMakeModel != null && vehMakeModel.getAttributes().getNamedItem
                        (GDSMsgNodeTags.TitaniumNodeTags.CODE).getTextContent().equals(sipp)) {
                    return vehAvail;
                }
            }
        }

        return null;
    }
}
