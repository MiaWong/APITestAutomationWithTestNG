package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 12/6/2016.
 */
@SuppressWarnings("PMD")
public class VSARReq {
    public CarInventoryKeyType carInventoryKey;
    public CarSearchCriteriaType carSearchCriteria;
    public String bsCode;
    public String branchCode;
    public boolean ccGuarantee;
    public String tourCode;
    public List<String> tourCodeList;

    public String RateCategory;

    public VSARReq(Node vsarReqNode, DataSource scsDataSource, DataSource carsInventoryDs, TestScenario scenario) throws DataAccessException {
        carInventoryKey = new CarInventoryKeyType();
        carSearchCriteria = new CarSearchCriteriaType();
        //Read dateLocation
        if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(vsarReqNode, "VehicleDateLocation"))) {
            Assert.fail(String.format("No VehicleDateLocation found in VSAR request, nameSpace: {0}!", "*"));
        }

        //Location
        final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(vsarReqNode, "VehicleDateLocation");
        UAPICommonNodeReader.readDateLocation(scsDataSource, carInventoryKey, dateLocationNode); // for GetCostAndAvail
        UAPICommonNodeReader.readDateLocationForCarSearchCriteria(scsDataSource, carSearchCriteria, dateLocationNode); // for Search

        //Read Vehicle
        final List<Node> vehicleNodeList = PojoXmlUtil.getNodesByTagName(vsarReqNode, "VehicleModifier");
        final CarCatalogKeyType carCatalogKeyType = CompareUtil.isObjEmpty(carInventoryKey.getCarCatalogKey()) ? new CarCatalogKeyType() : carInventoryKey.getCarCatalogKey();
        carInventoryKey.setCarCatalogKey(carCatalogKeyType);
        if (!CompareUtil.isObjEmpty(vehicleNodeList)) {
            carCatalogKeyType.setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNodeList.get(0), false));
            // for Search
            final CarVehicleListType carVehicleListType = CompareUtil.isObjEmpty(carSearchCriteria.getCarVehicleList()) ? new CarVehicleListType() : carSearchCriteria.getCarVehicleList();
            final List<CarVehicleType> carVehicleTypeList = CompareUtil.isObjEmpty(carVehicleListType.getCarVehicle()) ? new ArrayList<CarVehicleType>() : carVehicleListType.getCarVehicle();
            for (final Node vehicleNode : vehicleNodeList) {
                carVehicleTypeList.add(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, true));
            }
            carVehicleListType.setCarVehicle(carVehicleTypeList);
            carSearchCriteria.setCarVehicleList(carVehicleListType);
        }

        //Read Rate/VendorCode/CCGuarantee
        final Node searchModifiersNode = PojoXmlUtil.getNodeByTagName(vsarReqNode, "VehicleSearchModifiers");
        final CarRateType carRateType = carInventoryKey.getCarRate() == null ? new CarRateType() : carInventoryKey.getCarRate();
        carInventoryKey.setCarRate(carRateType);
        carSearchCriteria.setCarRate(carRateType);
        if (!CompareUtil.isObjEmpty(searchModifiersNode.getAttributes().getNamedItem("RateCategory"))) {
            carRateType.setRateCategoryCode(searchModifiersNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
        }

        if (CompareUtil.isObjEmpty(searchModifiersNode.getAttributes().getNamedItem("TourCode"))) {
            tourCode = "";
        } else {
            tourCode = searchModifiersNode.getAttributes().getNamedItem("TourCode").getNodeValue();
        }

        if (CompareUtil.isObjEmpty(searchModifiersNode.getAttributes().getNamedItem("RateCategory"))) {
            RateCategory = "";
        } else {
            RateCategory = searchModifiersNode.getAttributes().getNamedItem("RateCategory").getNodeValue();
        }

        if (!CompareUtil.isObjEmpty(searchModifiersNode.getAttributes().getNamedItem("RatePeriod"))) {
            final String periodCode = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.RATE_PERIOD, "", searchModifiersNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
            carRateType.setRatePeriodCode(periodCode);
        }
        final Node rateNode = PojoXmlUtil.getNodeByTagName(vsarReqNode, "RateModifiers");
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCode"))) {
            carRateType.setRateCode(rateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumber"))) {
            carRateType.setCorporateDiscountCode(rateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("PromotionalCode"))) {
            carRateType.setPromoCode(rateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("TourCode"))) {
            tourCode = rateNode.getAttributes().getNamedItem("TourCode").getNodeValue();
        }
        final Node loyaltyCardNL = PojoXmlUtil.getNodeByTagName(vsarReqNode, "LoyaltyCard");
        if (!CompareUtil.isObjEmpty(loyaltyCardNL)) {
            final LoyaltyProgramType loyaltyProgramType = CompareUtil.isObjEmpty(carRateType.getLoyaltyProgram()) ? new LoyaltyProgramType() : carRateType.getLoyaltyProgram();
            loyaltyProgramType.setLoyaltyProgramCategoryCode("Car");
            if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("VendorCode"))) {
                loyaltyProgramType.setLoyaltyProgramCode(rateNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
            }
            loyaltyProgramType.setLoyaltyProgramMembershipCode(loyaltyCardNL.getAttributes().getNamedItem("CardNumber").getNodeValue());
            carRateType.setLoyaltyProgram(loyaltyProgramType);
        }
        //For GetCostAndAvail
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("VendorCode"))) {
            carCatalogKeyType.setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs,rateNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));
        }

        // for search vendorsupplierIDList - read from permittedVendorsList or RateModifiers
        final List<Node> permittedVendorsList = PojoXmlUtil.getNodesByTagName(vsarReqNode, "Vendor");
        if (CompareUtil.isObjEmpty(permittedVendorsList)) {
            final List<Node> rateModifiersList = PojoXmlUtil.getNodesByTagName(vsarReqNode, "RateModifiers");
            final VendorSupplierIDListType supplierIDListType = CompareUtil.isObjEmpty(carSearchCriteria.getVendorSupplierIDList()) ? new VendorSupplierIDListType() : carSearchCriteria.getVendorSupplierIDList();
            final List<Long> supplierIdList = CompareUtil.isObjEmpty(supplierIDListType.getVendorSupplierID()) ? new ArrayList<Long>() : supplierIDListType.getVendorSupplierID();
            if(!CollectionUtils.isEmpty(rateModifiersList)) {
                tourCodeList = new ArrayList<String>();
                for (final Node vendor : rateModifiersList) {
                    if (!CompareUtil.isObjEmpty(vendor.getAttributes().getNamedItem("VendorCode"))) {
                        final String supplierId = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, 0, CommonConstantManager.DomainType.CAR_VENDOR, "", vendor.getAttributes().getNamedItem("VendorCode").getNodeValue());
                        supplierIdList.add(Long.valueOf(supplierId));
                    }
                    if(!CompareUtil.isObjEmpty(vendor.getAttributes().getNamedItem("TourCode")) && !StringUtils.isEmpty(vendor.getAttributes().getNamedItem("TourCode").getNodeValue())){
                        tourCodeList.add(vendor.getAttributes().getNamedItem("TourCode").getNodeValue());
                    }
                }
            }
            supplierIDListType.setVendorSupplierID(supplierIdList);
            carSearchCriteria.setVendorSupplierIDList(supplierIDListType);
        } else {
            carSearchCriteria.setVendorSupplierIDList(UAPICommonNodeReader.readVendorSupplierIDList(scsDataSource, permittedVendorsList));
        }

        //only for off airport,get the carvendorLocationId
        if(!scenario.isOnAirPort()){
            UAPICommonNodeReader.readCarVendorLocationId(scsDataSource,carCatalogKeyType.getCarPickupLocationKey(),carCatalogKeyType.getCarDropOffLocationKey(),carCatalogKeyType.getVendorSupplierID());
        }

        //Read bookingSource
        bsCode = PojoXmlUtil.getNodeByTagName(vsarReqNode, "BookingSource").getAttributes().getNamedItem("Code").getNodeValue();

        //Read branchCode
        branchCode = UAPICommonNodeReader.readBranchCode(vsarReqNode);

    }


    public CarInventoryKeyType getCarInventoryKey() {
        return carInventoryKey;
    }

    public void setCarInventoryKey(CarInventoryKeyType carInventoryKey) {
        this.carInventoryKey = carInventoryKey;
    }

    public CarSearchCriteriaType getCarSearchCriteria() {
        return carSearchCriteria;
    }

    public void setCarSearchCriteria(CarSearchCriteriaType carSearchCriteria) {
        this.carSearchCriteria = carSearchCriteria;
    }

    public String getBsCode() {
        return bsCode;
    }

    public void setBsCode(String bsCode) {
        this.bsCode = bsCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public boolean isCcGuarantee() {
        return ccGuarantee;
    }

    public void setCcGuarantee(boolean ccGuarantee) {
        this.ccGuarantee = ccGuarantee;
    }

    public String getTourCode() {
        return tourCode;
    }

    public void setTourCode(String tourCode) {
        this.tourCode = tourCode;
    }

    public List<String> getTourCodeList() {
        return tourCodeList;
    }

    public void setTourCodeList(List<String> tourCodeList) {
        this.tourCodeList = tourCodeList;
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
