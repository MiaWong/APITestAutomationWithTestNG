package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by miawang on 1/23/2017.
 */
public class APCMRsp {
    public void buildReferenceListPNR(CarReservationType reservation, Node nodeApcmPnrAme2Rsp) {
        if (null == reservation.getReferenceList()) {
            reservation.setReferenceList(new ReferenceListType());
        }
        if (null == reservation.getReferenceList().getReference()) {
            reservation.getReferenceList().setReference(new ArrayList<>());
        }

        final Node pnrHearder = PojoXmlUtil.getNodeByTagName(nodeApcmPnrAme2Rsp, "pnrHeader");
        final String referenceCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(
                PojoXmlUtil.getNodeByTagName(pnrHearder, "reservationInfo"), "reservation"), "controlNumber").getTextContent();

        final ReferenceType reference = new ReferenceType();
        reference.setReferenceCategoryCode("PNR");
        reference.setReferenceCode(referenceCode);

        reservation.getReferenceList().getReference().add(reference);
    }
}
