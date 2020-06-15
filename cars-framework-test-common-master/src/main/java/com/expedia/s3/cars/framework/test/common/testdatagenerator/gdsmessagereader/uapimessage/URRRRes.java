package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 12/15/2016.
 */
@SuppressWarnings("PMD")
public class URRRRes {
    public CarInventoryKeyType carInventoryKey;
    public TravelerListType travelerList;
    public ReferenceListType referenceList;
    public String bookingStateCode;
    public CostListType costList;
    public String freeDistanceRatePeriodCode;

    @SuppressWarnings("CPD-START")
    public URRRRes(Node urrrResp, DataSource scsDataSource, DataSource carsInventoryDs) throws DataAccessException {
        // Read 1 CarInventoryKey
        carInventoryKey = new CarInventoryKeyType();
        //Read 1.1.1 CarInventoryKey->CarCatalogKey->Vehicle/VendorSupplierID
        final Node vehicleNode = PojoXmlUtil.getNodeByTagName(urrrResp, "Vehicle");
        final CarCatalogKeyType carCatalogKey = new CarCatalogKeyType();
        carInventoryKey.setCarCatalogKey(carCatalogKey);
        carCatalogKey.setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, false));
        carCatalogKey.setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs, vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));
        //Read 1.1.2 CarInventoryKey->CarCatalogKey->CarPickupLocationKey
        //Read 1.3 CarInventoryKey->CarPickUpDateTime
        //Read 1.4 CarInventoryKey->CarDropOffDateTime
        final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(urrrResp, "VehicleDateLocation");
        UAPICommonNodeReader.readDateLocation(scsDataSource, carInventoryKey, dateLocationNode);

        //Read 1.5 CarInventoryKey->CarRate
        final Node rateNode = PojoXmlUtil.getNodeByTagName(urrrResp, "VehicleRate");
        final CarRateType carRate = CompareUtil.isObjEmpty(carInventoryKey.getCarRate()) ? new CarRateType() : carInventoryKey.getCarRate();
        carInventoryKey.setCarRate(carRate);
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCategory"))) {
            carRate.setRateCategoryCode(rateNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCode"))) {
            carRate.setRateCode(rateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumber"))) {
            carRate.setCorporateDiscountCode(rateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("PromotionalCode"))) {
            carRate.setPromoCode(rateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
        }
        if (CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RatePeriod"))) {
            carRate.setRatePeriodCode("Trip");
        } else {
            if ("WeekendDay".equals(rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue())) {
                carRate.setRatePeriodCode("Weekend");
            } else if ("Total".equals(rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue())) {
                carRate.setRatePeriodCode("Trip");
            } else if ("Other".equals(rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue())) {
                carRate.setRatePeriodCode("Trip");
            } else {
                carRate.setRatePeriodCode(rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
            }
            freeDistanceRatePeriodCode = UAPICommonNodeReader.readDomainValue(scsDataSource,0,0, CommonConstantManager.DomainType.RATE_PERIOD,"",rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
        }
        // if (rateNode.Attributes["TourCode"] != null) tourCode = rateNode.Attributes["TourCode"].Value;
        final Node loyaltyCardNL = PojoXmlUtil.getNodeByTagName(urrrResp,"LoyaltyCard");
        if (!CompareUtil.isObjEmpty(loyaltyCardNL)) {
            final LoyaltyProgramType loyaltyProgram = CompareUtil.isObjEmpty(carRate.getLoyaltyProgram()) ? new LoyaltyProgramType() : carRate.getLoyaltyProgram();
            carRate.setLoyaltyProgram(loyaltyProgram);
            loyaltyProgram.setLoyaltyProgramCategoryCode("Car");
            loyaltyProgram.setLoyaltyProgramCode(vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
            loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNL.getAttributes().getNamedItem("CardNumber").getNodeValue());
        }

        //Read 2 Traveler
        final List<Node> travNodeList = PojoXmlUtil.getNodesByTagName(urrrResp,"BookingTraveler");
        travelerList = UAPICommonNodeReader.readTravelerList(travNodeList);

        //Read 3 Customer


        //Read 4 ReferenceList
        final Node providerReservationInfo = PojoXmlUtil.getNodeByTagName(urrrResp,"ProviderReservationInfo");
        final Node vehicleReservation = PojoXmlUtil.getNodeByTagName(urrrResp,"VehicleReservation");
        referenceList = UAPICommonNodeReader.readReferenceList(providerReservationInfo, vehicleReservation);

        //Read 5 BookingStateCode
        bookingStateCode = UAPICommonNodeReader.readDomainValue(scsDataSource,0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.BOOKING_ITEM_STATE,"",vehicleReservation.getAttributes().getNamedItem("Status").getNodeValue());

        //Read 6 CostList
        final Node vehicleRate = PojoXmlUtil.getNodeByTagName(urrrResp,"VehicleRate");
        costList = UAPICommonNodeReader.readCostListReserve(scsDataSource, vehicleRate);
    }
    @SuppressWarnings("CPD-END")
    public CarInventoryKeyType getCarInventoryKey() {
        return carInventoryKey;
    }

    public void setCarInventoryKey(CarInventoryKeyType carInventoryKey) {
        this.carInventoryKey = carInventoryKey;
    }

    public TravelerListType getTravelerList() {
        return travelerList;
    }

    public void setTravelerList(TravelerListType travelerList) {
        this.travelerList = travelerList;
    }

    public ReferenceListType getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(ReferenceListType referenceList) {
        this.referenceList = referenceList;
    }

    public String getBookingStateCode() {
        return bookingStateCode;
    }

    public void setBookingStateCode(String bookingStateCode) {
        this.bookingStateCode = bookingStateCode;
    }

    public CostListType getCostList() {
        return costList;
    }

    public void setCostList(CostListType costList) {
        this.costList = costList;
    }

    public String getFreeDistanceRatePeriodCode() {
        return freeDistanceRatePeriodCode;
    }

    public void setFreeDistanceRatePeriodCode(String freeDistanceRatePeriodCode) {
        this.freeDistanceRatePeriodCode = freeDistanceRatePeriodCode;
    }
}
