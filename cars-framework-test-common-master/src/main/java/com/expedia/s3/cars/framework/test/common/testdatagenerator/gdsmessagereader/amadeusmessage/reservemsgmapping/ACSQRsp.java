package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSReaderUtil;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 1/23/2017.
 */
public class ACSQRsp {
    public void buildCarInventory(CarProductType carProduct,
                                  Node nodeAcsqCarSellRsp) throws DataAccessException {
        // Car_sell response
        // 5.Get CarPickUpDateTime (For example: 2012-11-24T12:00:00)
        // 6.Get CarDropOffDateTime
        final Node pickupDropoffTimeNode = PojoXmlUtil.getNodeByTagName(nodeAcsqCarSellRsp, "pickupDropoffTimes");

        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarPickUpAndDropOffDateTime(carProduct.getCarInventoryKey(), pickupDropoffTimeNode);

        // CarRate node
        if (null == carProduct.getCarInventoryKey().getCarRate()) {
            carProduct.getCarInventoryKey().setCarRate(new CarRateType());
        }

        final Node carSegmentNode = PojoXmlUtil.getNodeByTagName(nodeAcsqCarSellRsp, "carSegment");
        final Node typicalDataNode = PojoXmlUtil.getNodeByTagName(carSegmentNode, "typicalCarData");

        final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(typicalDataNode, "rateInfo"), "tariffInfo");

        // 8.Get RatePeriodCode
        final String ratePeriodCode = ASCSGDSReaderUtil.getFinanceApplicationCodeByRatePlanIndicator(PojoXmlUtil.getNodeByTagName(tariffInfoNode, "ratePlanIndicator").getTextContent());
        carProduct.getCarInventoryKey().getCarRate().setRatePeriodCode(ratePeriodCode);

        // 9.CarRateQualifierCode
        String rateQualifierCode = "";
        if (null != PojoXmlUtil.getNodeByTagName(tariffInfoNode, "rateType"))
        {
            rateQualifierCode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "rateType").getTextContent();
        }
        carProduct.getCarInventoryKey().getCarRate().setCarRateQualifierCode(rateQualifierCode);

        // 10. Get RateCode  and CorporateDiscountCode
            buildRateCodeAndCorporateDiscountCode(carProduct, typicalDataNode);

            // 11. RateCategoryCode
            //String rateCategoryCode = ASCSUtil.GetRateCatalogCode(TypicalData["rateInfo"]["rateInformation"].InnerText);
            commonNodeReader.readRateCategoryCode(carProduct.getCarInventoryKey(), typicalDataNode, "rateInfo", "rateInformation");

            /// 12. LoyaltyProgram
            final Node fFlyerNbrNode = PojoXmlUtil.getNodeByTagName(typicalDataNode, "fFlyerNbr");
            if (fFlyerNbrNode != null) {
                final LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
                loyaltyProgram.setLoyaltyProgramCode("FrequentFlyerNumber");
                loyaltyProgram.setLoyaltyProgramMembershipCode(PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(fFlyerNbrNode, "frequentTravellerDetails"), "number").getTextContent());

                carProduct.getCarInventoryKey().getCarRate().setLoyaltyProgram(loyaltyProgram);
            }
    }

    private void buildRateCodeAndCorporateDiscountCode(CarProductType carProduct, Node typicalData) {
        final String rateCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(typicalData, "rateCodeInfo"),
                "fareCategories"), "fareType").getTextContent();
        carProduct.getCarInventoryKey().getCarRate().setRateCode(rateCode);

        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readRateCodeAndCorporateDiscountCode(carProduct.getCarInventoryKey(), typicalData, "customerInfo");
    }

    // reference and booking status
    public void buildReferenceList(CarReservationType reservation, Node nodeAcsqCarSellRsp) {
        if (null == reservation.getReferenceList()) {
            reservation.setReferenceList(new ReferenceListType());
        }
        if (null == reservation.getReferenceList().getReference()) {
            reservation.getReferenceList().setReference(new ArrayList<>());
        }

        final List<Node> reservationList = PojoXmlUtil.getNodesByTagName(nodeAcsqCarSellRsp, "reservation");
        for (final Node reservationNode : reservationList) {
            if (PojoXmlUtil.getNodeByTagName(reservationNode, "controlType").getTextContent().equals("2")) {
                final ReferenceType referenceVendor = new ReferenceType();
                referenceVendor.setReferenceCategoryCode("Vendor");
                referenceVendor.setReferenceCode(PojoXmlUtil.getNodeByTagName(reservationNode, "controlNumber").getTextContent());
                reservation.getReferenceList().getReference().add(referenceVendor);
            }
        }
    }

    public void buildBookingStateCode(CarReservationType reservation, Node nodeAcsqCarSellRsp) {
        // Booking status
        //car_segment/rateStatus/statusCode
        final Node segment = PojoXmlUtil.getNodeByTagName(nodeAcsqCarSellRsp, "carSegment");
        final String statusCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(segment, "rateStatus"), "statusCode").getTextContent();
        if (statusCode != null && statusCode.equals("HK")) {
            reservation.setBookingStateCode(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode());
        } else if (statusCode != null && statusCode.equals("NN")) {
            reservation.setBookingStateCode(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode());
        } else if (statusCode != null && statusCode.equals("SS")) {
            reservation.setBookingStateCode(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode());
        }
    }

    public String getstatusCodeFromAcsqCarSellRsp(Node nodeAcsqCarSellRsp) {
        final Node segment = PojoXmlUtil.getNodeByTagName(nodeAcsqCarSellRsp, "carSegment");
        final String statusCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(segment, "rateStatus"), "statusCode").getTextContent();

        return statusCode;
    }
}