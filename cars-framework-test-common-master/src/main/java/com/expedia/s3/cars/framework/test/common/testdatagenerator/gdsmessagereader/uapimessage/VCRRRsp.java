package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.cartypes.defn.v5.AdvisoryTextListType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 12/8/2016.
 */
@SuppressWarnings("PMD")
public class VCRRRsp {
    public CarInventoryKeyType carInventoryKey;
    public TravelerListType travelerListType;
    public ReferenceListType referenceList;
    public AdvisoryTextListType advisoryTextListType;
    public CostListType costListType;
    public String bookingStateCode;
    public String tourCode;
    public CarSpecialEquipmentListType carSpecialEquipmentListType;
    public boolean corporateRate;
    public String discountNumberApplied = "";

    public VCRRRsp(Node vcrrRsp, String vendorCode, DataSource scsDataSource, DataSource carsInventoryDs) throws DataAccessException {
        carInventoryKey = new CarInventoryKeyType();

        //Read dateLocation
        if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(vcrrRsp, "VehicleDateLocation"))) {
            Assert.fail(String.format("No VehicleDateLocation found in VCRR response, nameSpace: {0}!", "*"));
        }
        //Location
        final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(vcrrRsp, "VehicleDateLocation");
        UAPICommonNodeReader.readDateLocation(scsDataSource, carInventoryKey, dateLocationNode);

        //Read Vehicle/Vendorcode
        final Node vehicleNode = PojoXmlUtil.getNodeByTagName(vcrrRsp, "Vehicle");
        carInventoryKey.getCarCatalogKey().setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, false));
        carInventoryKey.getCarCatalogKey().setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs, vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));

        //Read Rate
        //    and $vehicle/veh:VehicleRate/@CorporateRate='true'
        //and not($vehicle/veh:VehicleRate/@DiscountNumberApplied='false')">
        //For Bug CASSS-827 fix, not all CD returned is returned to SCS, need to check CorporateRate and DiscountNumberApplied
        final Node rateNode = PojoXmlUtil.getNodeByTagName(vcrrRsp, "VehicleRate");
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("CorporateRate"))) {
            corporateRate = Boolean.parseBoolean(rateNode.getAttributes().getNamedItem("CorporateRate").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumberApplied"))) {
            discountNumberApplied = rateNode.getAttributes().getNamedItem("DiscountNumberApplied").getNodeValue();
        }
        final CarRateType carRate = carInventoryKey.getCarRate() == null ? new CarRateType() : carInventoryKey.getCarRate();
        carInventoryKey.setCarRate(carRate);
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCategory"))) {
            carRate.setRateCategoryCode(rateNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCode"))) {
            carRate.setRateCode(rateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
        }
        // CASSS-899 Passing CD code down in every message for hertz via uAPI
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumber")) && "ZE".equals(vendorCode)) {
            carRate.setCorporateDiscountCode(rateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        } else if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumber")) && corporateRate && Boolean.parseBoolean(discountNumberApplied)) {
            carRate.setCorporateDiscountCode(rateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("PromotionalCode"))) {
            carRate.setPromoCode(rateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RatePeriod"))) {
            carRate.setRatePeriodCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, 0, CommonConstantManager.DomainType.RATE_PERIOD, "", rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue()));
        }
        if (StringUtils.isEmpty(carRate.getRatePeriodCode())) {
            carRate.setRatePeriodCode("Trip");//Default value is Trip
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("TourCode"))) {
            tourCode = rateNode.getAttributes().getNamedItem("TourCode").getNodeValue();
        }
        final Node loyaltyCardNL = PojoXmlUtil.getNodeByTagName(vcrrRsp, "LoyaltyCard");
        if (!CompareUtil.isObjEmpty(loyaltyCardNL)) {
            final LoyaltyProgramType loyaltyProgram = carRate.getLoyaltyProgram() == null ? new LoyaltyProgramType() : carRate.getLoyaltyProgram();
            loyaltyProgram.setLoyaltyProgramCategoryCode("Car");
            loyaltyProgram.setLoyaltyProgramCode(vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
            loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNL.getAttributes().getNamedItem("CardNumber").getNodeValue());
            carRate.setLoyaltyProgram(loyaltyProgram);
        }

        //Read Traveler
        final List<Node> travNodeList = PojoXmlUtil.getNodesByTagName(vcrrRsp, "BookingTraveler");
        travelerListType = UAPICommonNodeReader.readTravelerList(travNodeList);

        //Get  ProviderReservationInfo and VehicleReservation node
        final Node providerReservationInfo = PojoXmlUtil.getNodeByTagName(vcrrRsp, "ProviderReservationInfo");
        final Node vehicleReservation = PojoXmlUtil.getNodeByTagName(vcrrRsp, "VehicleReservation");

        //Read ReferenceList
        //referenceList = UAPICommonNodeReader.readReferenceList(providerReservationInfo, vehicleReservation);

        //Read booking status code
        bookingStateCode = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.BOOKING_ITEM_STATE, "", vehicleReservation.getAttributes().getNamedItem("Status").getNodeValue());
        if (StringUtils.isEmpty(bookingStateCode)) {
            bookingStateCode = "Unknown";
        }

        //Read CostList
        final Node vehicleRate = PojoXmlUtil.getNodeByTagName(vcrrRsp, "VehicleRate");
        costListType = UAPICommonNodeReader.readCostListReserve(scsDataSource, vehicleRate);

        //Read AdvisoryTextList
        advisoryTextListType = UAPICommonNodeReader.readAdvisoryTextList(vcrrRsp);

        //Special equipment
        carSpecialEquipmentListType = UAPICommonNodeReader.readSpecialEquipmentList(scsDataSource, vcrrRsp);

    }

    public CarInventoryKeyType getCarInventoryKey() {
        return carInventoryKey;
    }

    public void setCarInventoryKey(CarInventoryKeyType carInventoryKey) {
        this.carInventoryKey = carInventoryKey;
    }

    public TravelerListType getTravelerListType() {
        return travelerListType;
    }

    public void setTravelerListType(TravelerListType travelerListType) {
        this.travelerListType = travelerListType;
    }

    public ReferenceListType getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(ReferenceListType referenceList) {
        this.referenceList = referenceList;
    }

    public AdvisoryTextListType getAdvisoryTextListType() {
        return advisoryTextListType;
    }

    public void setAdvisoryTextListType(AdvisoryTextListType advisoryTextListType) {
        this.advisoryTextListType = advisoryTextListType;
    }

    public CostListType getCostListType() {
        return costListType;
    }

    public void setCostListType(CostListType costListType) {
        this.costListType = costListType;
    }

    public String getBookingStateCode() {
        return bookingStateCode;
    }

    public void setBookingStateCode(String bookingStateCode) {
        this.bookingStateCode = bookingStateCode;
    }

    public String getTourCode() {
        return tourCode;
    }

    public void setTourCode(String tourCode) {
        this.tourCode = tourCode;
    }

    public CarSpecialEquipmentListType getCarSpecialEquipmentListType() {
        return carSpecialEquipmentListType;
    }

    public void setCarSpecialEquipmentListType(CarSpecialEquipmentListType carSpecialEquipmentListType) {
        this.carSpecialEquipmentListType = carSpecialEquipmentListType;
    }

    public boolean isCorporateRate() {
        return corporateRate;
    }

    public void setCorporateRate(boolean corporateRate) {
        this.corporateRate = corporateRate;
    }

    public String getDiscountNumberApplied() {
        return discountNumberApplied;
    }

    public void setDiscountNumberApplied(String discountNumberApplied) {
        this.discountNumberApplied = discountNumberApplied;
    }
}
