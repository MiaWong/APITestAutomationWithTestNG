package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationRemarkListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.Assert;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 12/8/2016.
 */
@SuppressWarnings("PMD")
public class VCRRReq {
    public CarInventoryKeyType carInventoryKey;
    public TravelerListType travelerListType;
    public CarReservationRemarkListType carReservationRemarkListType;
    public String tourCode;
    public String voucher;
    public String billingNumber;
    public String siCode;
    public String ccCardText;
    public String bsCode;
    public String iataNum;
    public String branchCode;
    public CarSpecialEquipmentListType carSpecialEquipmentListType;

    @SuppressWarnings("CPD-START")
    public VCRRReq(Node vcrrReq, DataSource scsDataSource, DataSource carsInventoryDs) throws DataAccessException {
        carInventoryKey = new CarInventoryKeyType();
        //Read dateLocation
        if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(vcrrReq,"VehicleDateLocation"))) {
            Assert.fail(String.format("No VehicleDateLocation found in VCRR request, nameSpace: {0}!", "*"));
        }

        //Location
        final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(vcrrReq,"VehicleDateLocation");
        UAPICommonNodeReader.readDateLocation(scsDataSource, carInventoryKey, dateLocationNode);

        //Read Vehicle/Vendorcode
        final Node vehicleNode = PojoXmlUtil.getNodeByTagName(vcrrReq,"Vehicle");
        carInventoryKey.getCarCatalogKey().setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode,false)) ;
        carInventoryKey.getCarCatalogKey().setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs,vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));

        //Read Rate
        final Node rateNode = PojoXmlUtil.getNodeByTagName(vcrrReq,"VehicleRate");
        final CarRateType carRate = CompareUtil.isObjEmpty(carInventoryKey.getCarRate())? new CarRateType() : carInventoryKey.getCarRate();
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
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RatePeriod"))) {
            carRate.setRatePeriodCode(UAPICommonNodeReader.readDomainValue(scsDataSource,0,0, CommonConstantManager.DomainType.RATE_PERIOD,"",rateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue()));
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("TourCode"))){
            tourCode = rateNode.getAttributes().getNamedItem("TourCode").getNodeValue();
        }

        carInventoryKey.setCarRate(carRate);
        //Read Traveler
        final List<Node> travNodeList = PojoXmlUtil.getNodesByTagName(vcrrReq,"BookingTraveler");
        travelerListType = UAPICommonNodeReader.readTravelerList(travNodeList);

        //Read carReservationRemarkList
        final List<Node> generalRemarkNL =  PojoXmlUtil.getNodesByTagName(vcrrReq,"GeneralRemark");
        carReservationRemarkListType = UAPICommonNodeReader.readCarReservationRemarkList(generalRemarkNL);

        //Read CCText
        ccCardText = UAPICommonNodeReader.readCCInfo(vcrrReq);

        //Read voucher
        voucher = UAPICommonNodeReader.readVoucher(vcrrReq);

        //Read billingNumber
        billingNumber = UAPICommonNodeReader.readBillingNumber(vcrrReq);

        //Read SI
        final Node vehicleSpecialRequest = PojoXmlUtil.getNodeByTagName(vcrrReq,"VehicleSpecialRequest");
        if (!CompareUtil.isObjEmpty(vehicleSpecialRequest)  && !CompareUtil.isObjEmpty(vehicleSpecialRequest.getAttributes().getNamedItem("Key"))) {
            siCode = vehicleSpecialRequest.getAttributes().getNamedItem("Key").getNodeValue();
        }

        //Special equipment
        carSpecialEquipmentListType = UAPICommonNodeReader.readSpecialEquipmentList(scsDataSource, vcrrReq);

        //Read bookingSource
        bsCode = PojoXmlUtil.getNodeByTagName(vcrrReq,"BookingSource").getAttributes().getNamedItem("Code").getNodeValue();

        //Read IATA number under AgencyContactInfo
        final Node agencyContactInfo = PojoXmlUtil.getNodeByTagName(vcrrReq,"AgencyContactInfo");
        if (!CompareUtil.isObjEmpty(agencyContactInfo)) {
            iataNum = agencyContactInfo.getFirstChild().getAttributes().getNamedItem("Number").getNodeValue().split("/")[0];
        }
        //Read branchCode
        branchCode = UAPICommonNodeReader.readBranchCode(vcrrReq);
    }
    @SuppressWarnings("CPD-END")
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

    public CarReservationRemarkListType getCarReservationRemarkListType() {
        return carReservationRemarkListType;
    }

    public void setCarReservationRemarkListType(CarReservationRemarkListType carReservationRemarkListType) {
        this.carReservationRemarkListType = carReservationRemarkListType;
    }

    public String getTourCode() {
        return tourCode;
    }

    public void setTourCode(String tourCode) {
        this.tourCode = tourCode;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getBillingNumber() {
        return billingNumber;
    }

    public void setBillingNumber(String billingNumber) {
        this.billingNumber = billingNumber;
    }

    public String getSiCode() {
        return siCode;
    }

    public void setSiCode(String siCode) {
        this.siCode = siCode;
    }

    public String getCcCardText() {
        return ccCardText;
    }

    public void setCcCardText(String ccCardText) {
        this.ccCardText = ccCardText;
    }

    public String getBsCode() {
        return bsCode;
    }

    public void setBsCode(String bsCode) {
        this.bsCode = bsCode;
    }

    public String getIataNum() {
        return iataNum;
    }

    public void setIataNum(String iataNum) {
        this.iataNum = iataNum;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public CarSpecialEquipmentListType getCarSpecialEquipmentListType() {
        return carSpecialEquipmentListType;
    }

    public void setCarSpecialEquipmentListType(CarSpecialEquipmentListType carSpecialEquipmentListType) {
        this.carSpecialEquipmentListType = carSpecialEquipmentListType;
    }
}
