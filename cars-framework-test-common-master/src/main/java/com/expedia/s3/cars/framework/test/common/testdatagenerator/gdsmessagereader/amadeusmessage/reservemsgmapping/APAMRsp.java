package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import org.w3c.dom.Node;

/**
 * Created by miawang on 1/23/2017.
 */
public class APAMRsp {
    public void buildTravelerInfo(CarReservationType reservation, Node nodeApamPnrAme1Rsp) {
        if (null == reservation.getTravelerList()) {
            reservation.setTravelerList(new TravelerListType());
        }
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildTravelerInfo(reservation.getTravelerList(), nodeApamPnrAme1Rsp, "travellerInfo");
    }
}