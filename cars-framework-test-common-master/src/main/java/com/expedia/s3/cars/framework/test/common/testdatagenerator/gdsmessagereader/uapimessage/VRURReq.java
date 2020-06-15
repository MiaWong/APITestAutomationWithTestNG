package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.UAPICommonNodeReader.uapiMessageSystemID;

/**
 * Created by yyang4 on 12/13/2016.
 */
@SuppressWarnings("PMD")
public class VRURReq {
    public boolean ccGuarantee;
    public CarInventoryKeyType inventoryKey;
    public BookingSource bookingSource;
    // TourCode
    public String itCode;
    public String carPolicyCategoryCodeList;

    public String RateCategory;

    public VRURReq(Node vrurReq, DataSource scsDataSource, DataSource carsInventoryDs) throws DataAccessException {
        final Node vehicleDateLocationNode = PojoXmlUtil.getNodeByTagName(vrurReq, "VehicleDateLocation");
        final Node vehicleSearchModifiersNode = PojoXmlUtil.getNodeByTagName(vrurReq, "VehicleSearchModifiers");
        final Node vehicleModifierNode = PojoXmlUtil.getNodeByTagName(vrurReq, "VehicleModifier");
        final Node rateHostIndicatorNode = PojoXmlUtil.getNodeByTagName(vrurReq, "RateHostIndicator");
        final Node rateModifiersNode = PojoXmlUtil.getNodeByTagName(vrurReq, "RateModifiers");
        final Node loyaltyCardNode = PojoXmlUtil.getNodeByTagName(vrurReq, "LoyaltyCard");
        final List<Node> policyNodeList = PojoXmlUtil.getNodesByTagName(vrurReq, "Policy");
        inventoryKey = new CarInventoryKeyType();
        final CarCatalogKeyType carCatalogKey = new CarCatalogKeyType();
        inventoryKey.setCarCatalogKey(carCatalogKey);
        if (!CompareUtil.isObjEmpty(vehicleDateLocationNode)) {
            final CarLocationKeyType pickKey = new CarLocationKeyType();
            final CarLocationKeyType dropKey = new CarLocationKeyType();
            carCatalogKey.setCarPickupLocationKey(pickKey);
            carCatalogKey.setCarDropOffLocationKey(dropKey);
            pickKey.setLocationCode(vehicleDateLocationNode.getAttributes().getNamedItem("PickupLocation").getNodeValue());
            final String rawText = vehicleDateLocationNode.getAttributes().getNamedItem("PickupLocationNumber").getNodeValue();
            pickKey.setSupplierRawText(rawText.length() == 2 ? "0" + rawText : rawText);
            pickKey.setCarLocationCategoryCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", vehicleDateLocationNode.getAttributes().getNamedItem("PickupLocationType").getNodeValue()));
            dropKey.setLocationCode(vehicleDateLocationNode.getAttributes().getNamedItem("ReturnLocation").getNodeValue());
            final String rawTextDrop = vehicleDateLocationNode.getAttributes().getNamedItem("ReturnLocationNumber").getNodeValue();
            dropKey.setSupplierRawText(rawTextDrop.length() == 2 ? "0" + rawTextDrop : rawTextDrop);
            dropKey.setCarLocationCategoryCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", vehicleDateLocationNode.getAttributes().getNamedItem("ReturnLocationType").getNodeValue()));
            //inventoryKey.CarPickUpDateTime=
            final String pickTimeString = vehicleDateLocationNode.getAttributes().getNamedItem("PickupDateTime").getNodeValue();
            final String dropTimeString = vehicleDateLocationNode.getAttributes().getNamedItem("ReturnDateTime").getNodeValue();
            inventoryKey.setCarPickUpDateTime(new DateTime(pickTimeString));
            inventoryKey.setCarDropOffDateTime(new DateTime(dropTimeString));
        }

        final CarRateType carRate = new CarRateType();
        inventoryKey.setCarRate(carRate);
        if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode)) {
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("RateGuaranteed"))) {
                ccGuarantee = Boolean.parseBoolean(vehicleSearchModifiersNode.getAttributes().getNamedItem("RateGuaranteed").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("CarAgreementID"))) {
                carRate.setCarAgreementID(Long.valueOf(vehicleSearchModifiersNode.getAttributes().getNamedItem("CarAgreementID").getNodeValue()));
            }
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("RateCategory"))) {
                carRate.setRateCategoryCode(vehicleSearchModifiersNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("RatePeriod"))) {
                carRate.setRatePeriodCode(vehicleSearchModifiersNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
                if (carRate.getRatePeriodCode().contains("Weekend")) {
                    carRate.setRatePeriodCode("Weekend");
                }
                if (carRate.getRatePeriodCode().contains("Total")) {
                    carRate.setRatePeriodCode("Trip");
                }
            }
        }

        //PolicyList
        final List<String> policyList = new ArrayList<String>();
        for (final Node policy : policyNodeList) {
            //If not ALL, map to internal value for policy code
            if ("ALL".equals(policy.getAttributes().getNamedItem("Name").getNodeValue())) {
                policyList.add(policy.getAttributes().getNamedItem("Name").getNodeValue());
            } else {
                policyList.add(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, uapiMessageSystemID, CommonConstantManager.DomainType.CAR_POLICY_CATEGORY, "", policy.getAttributes().getNamedItem("Name").getNodeValue()));
            }
        }
        if (CompareUtil.isObjEmpty(policyList)) {
            this.carPolicyCategoryCodeList = "";
        } else {
            //Sort and join to string to compare
            Collections.sort(policyList);
            this.carPolicyCategoryCodeList = StringUtils.join(policyList, ",");
        }

        if (!CompareUtil.isObjEmpty(loyaltyCardNode)) {
            final LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
            loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNode.getAttributes().getNamedItem("CardNumber").getNodeValue());
            carRate.setLoyaltyProgram(loyaltyProgram);
        }

        if (!CompareUtil.isObjEmpty(rateModifiersNode)) {
            if (!CompareUtil.isObjEmpty(rateModifiersNode.getAttributes().getNamedItem("RateCode"))) {
                carRate.setRateCode(rateModifiersNode.getAttributes().getNamedItem("RateCode").getNodeValue());
            }
            carCatalogKey.setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs, rateModifiersNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));
            if (!CompareUtil.isObjEmpty(rateModifiersNode.getAttributes().getNamedItem("DiscountNumber"))) {
                carRate.setCorporateDiscountCode(rateModifiersNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(rateModifiersNode.getAttributes().getNamedItem("PromotionalCode"))) {
                carRate.setPromoCode(rateModifiersNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(rateModifiersNode.getAttributes().getNamedItem("TourCode"))) {
                this.itCode = rateModifiersNode.getAttributes().getNamedItem("TourCode").getNodeValue();
            }

        }

        if (!CompareUtil.isObjEmpty(rateHostIndicatorNode)) {
            carRate.setCarRateQualifierCode(rateHostIndicatorNode.getAttributes().getNamedItem("InventoryToken").getNodeValue() + "          " + rateHostIndicatorNode.getAttributes().getNamedItem("RateToken").getNodeValue() + " ");
        }

        if (!CompareUtil.isObjEmpty(vehicleModifierNode)) {
            carCatalogKey.setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleModifierNode, false));
        }
        this.bookingSource = getBookingSouceFromVRUR(vrurReq);
    }

    public String getItCodeFromVRUR(Node vrurReq) {
        String itCode = null;
        final Node vehicleSearchModifiersNode = PojoXmlUtil.getNodeByTagName(vrurReq, "VehicleSearchModifiers");
        if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode)) {
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("TourCode"))) {
                itCode = vehicleSearchModifiersNode.getAttributes().getNamedItem("TourCode").getNodeValue();
            }
        }
        return itCode;
    }

    public String getRateCategoryFromVRUR(Node vrurReq) {
        String rateCategory = null;
        final Node vehicleSearchModifiersNode = PojoXmlUtil.getNodeByTagName(vrurReq, "VehicleSearchModifiers");
        if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode)) {
            if (!CompareUtil.isObjEmpty(vehicleSearchModifiersNode.getAttributes().getNamedItem("RateCategory"))) {
                rateCategory = vehicleSearchModifiersNode.getAttributes().getNamedItem("RateCategory").getNodeValue();
                this.setRateCategory(rateCategory);
            }
        }
        return rateCategory;
    }

    public BookingSource getBookingSouceFromVRUR(Node vrurReq) {
        final BookingSource bs = new BookingSource();
        final Node bookingSourceNode = PojoXmlUtil.getNodeByTagName(vrurReq, "BookingSource");
        if (!CompareUtil.isObjEmpty(bookingSourceNode)) {
            if (!CompareUtil.isObjEmpty(bookingSourceNode.getAttributes().getNamedItem("Code"))) {
                bs.setCode(bookingSourceNode.getAttributes().getNamedItem("Code").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(bookingSourceNode.getAttributes().getNamedItem("Type"))) {
                bs.setType(bookingSourceNode.getAttributes().getNamedItem("Type").getNodeValue());
            }
        }
        return bs;
    }


    public class BookingSource {
        public String code;
        public String type;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public boolean isCcGuarantee() {
        return ccGuarantee;
    }

    public void setCcGuarantee(boolean ccGuarantee) {
        this.ccGuarantee = ccGuarantee;
    }

    public CarInventoryKeyType getInventoryKey() {
        return inventoryKey;
    }

    public void setInventoryKey(CarInventoryKeyType inventoryKey) {
        this.inventoryKey = inventoryKey;
    }

    public BookingSource getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(BookingSource bookingSource) {
        this.bookingSource = bookingSource;
    }

    public String getItCode() {
        return itCode;
    }

    public void setItCode(String itCode) {
        this.itCode = itCode;
    }

    public String getCarPolicyCategoryCodeList() {
        return carPolicyCategoryCodeList;
    }

    public void setCarPolicyCategoryCodeList(String carPolicyCategoryCodeList) {
        this.carPolicyCategoryCodeList = carPolicyCategoryCodeList;
    }

    public String getRateCategory()
    {
        return RateCategory;
    }

    public void setRateCategory(String rateCategory)
    {
        RateCategory = rateCategory;
    }
}
