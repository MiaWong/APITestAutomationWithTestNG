package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierConfiguration;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
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
public class VSARRsp {
    public CarInventoryKeyType carInventoryKey;
    public boolean ccGuarantee;
    public String availStatusCode;
    public CostListType costList;
    public CarMileageType carMileage;
    public boolean corporateRate;
    public String discountNumberApplied;

    // Parse VSAR response for GetCostAndAvail
    public VSARRsp(Node vsarRspNode, CarInventoryKeyType reqCarInventoryKey, DataSource scsDataSource, DataSource carsInventoryDs, TestScenario scenario) throws DataAccessException {
        final StringBuilder errorMsg = new StringBuilder();
        carInventoryKey = new CarInventoryKeyType();
        costList = new CostListType();
        carMileage = new CarMileageType();

        //Read dateLocation
        if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(vsarRspNode, "VehicleDateLocation"))) {
            Assert.fail(String.format("No VehicleDateLocation found in VSAR response, nameSpace: {0}!", "*"));
        }

        //Location
        final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(vsarRspNode, "VehicleDateLocation");
        UAPICommonNodeReader.readDateLocation(scsDataSource, carInventoryKey, dateLocationNode);

        //vehicle:Vehicle - VSAR may have multiple cars returned.
        final List<Node> vehicleListNL = PojoXmlUtil.getNodesByTagName(vsarRspNode, "Vehicle");
        for (final Node vehicleNode : vehicleListNL) {
            //Read Vehicle/VendorCode
            final CarVehicleType carVehicle = UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, false);
            final Long vendorSupplierID = UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs, vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
            //filter the correct car
            if (vendorSupplierID.longValue() == reqCarInventoryKey.getCarCatalogKey().getVendorSupplierID() && CompareUtil.compareObject(carVehicle, reqCarInventoryKey.getCarCatalogKey().getCarVehicle(), null,errorMsg)) {
                //assign carVehicle and vendorSupplierID
                carInventoryKey.getCarCatalogKey().setCarVehicle(carVehicle);
                carInventoryKey.getCarCatalogKey().setVendorSupplierID(vendorSupplierID);

                //Read AvailStatusCode/carRate/ccGurantee/costList/ from VehicleRate
                //AvailStatusCode
                final Node vehicleRateNode = PojoXmlUtil.getNodeByTagName(vehicleNode, "VehicleRate");
                availStatusCode = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.AVAIL_STATUS, "", vehicleRateNode.getAttributes().getNamedItem("RateAvailability").getNodeValue());
                //CarRate
                final CarRateType carRate = CompareUtil.isObjEmpty(carInventoryKey.getCarRate()) ? new CarRateType() : carInventoryKey.getCarRate();
                carInventoryKey.setCarRate(carRate);
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCategory"))) {
                    carRate.setRateCategoryCode(vehicleRateNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RatePeriod"))) {
                    carRate.setRatePeriodCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.RATE_PERIOD, "", vehicleRateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue()));
                }
                if (CompareUtil.isObjEmpty(carRate.getRatePeriodCode())) {
                    carRate.setRatePeriodCode("Trip");
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCode"))) {
                    carRate.setRateCode(vehicleRateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber"))) {
                    carRate.setCorporateDiscountCode(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("PromotionalCode"))) {
                    carRate.setPromoCode(vehicleRateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
                }
                //<vehicle:VehicleRate CorporateRate="true"
                corporateRate = false;
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("CorporateRate"))) {
                    corporateRate = Boolean.parseBoolean(vehicleRateNode.getAttributes().getNamedItem("CorporateRate").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("DiscountNumberApplied"))) {
                    discountNumberApplied = vehicleRateNode.getAttributes().getNamedItem("DiscountNumberApplied").getNodeValue();
                }
                final Node loyaltyCardNL = PojoXmlUtil.getNodeByTagName(vsarRspNode, "LoyaltyCard");
                if (!CompareUtil.isObjEmpty(loyaltyCardNL)) {
                    final LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
                    loyaltyProgram.setLoyaltyProgramCategoryCode("Car");
                    loyaltyProgram.setLoyaltyProgramCode(vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
                    loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNL.getAttributes().getNamedItem("CardNumber").getNodeValue());
                    carRate.setLoyaltyProgram(loyaltyProgram);
                }

                //only for off airport,get the carvendorLocationId
                if(!scenario.isOnAirPort()){
                    UAPICommonNodeReader.readCarVendorLocationId(scsDataSource,carInventoryKey.getCarCatalogKey().getCarPickupLocationKey(),carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey(),carInventoryKey.getCarCatalogKey().getVendorSupplierID());
                }

                //CCGurantee RequiredPayment="Guarantee"
                ccGuarantee = false;
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RequiredPayment")) && "Guarantee".equals(vehicleRateNode.getAttributes().getNamedItem("RequiredPayment").getNodeValue())) {
                    ccGuarantee = true;
                }
                //carMileage
                final Node supplierRateNode = PojoXmlUtil.getNodeByTagName(vehicleRateNode, "SupplierRate");
                carMileage = UAPICommonNodeReader.readCarMileage(vehicleRateNode, supplierRateNode);
                //Cost values
                final long rentalDays = DateTimeUtil.getDiffDays(carInventoryKey.getCarDropOffDateTime(), carInventoryKey.getCarPickUpDateTime());
                costList = UAPICommonNodeReader.readCostList(supplierRateNode, carInventoryKey.getCarRate().getRatePeriodCode(), rentalDays);
            }
        }

    }

    // Parse VSAR response for Search
    public static List<CarProductType> readProductList(Node vsarRspNode, String cdCode, DataSource scsDataSource, DataSource carsInventoryDS, String env) throws DataAccessException {

        final List<CarProductType> carProductList = new ArrayList<CarProductType>();

        //Read dateLocation
        if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(vsarRspNode, "VehicleDateLocation"))) {
            Assert.fail(String.format("No VehicleDateLocation found in VSAR response, nameSpace: {0}!", "*"));
        } else {
            //Location
            final Node dateLocationNode = PojoXmlUtil.getNodeByTagName(vsarRspNode, "VehicleDateLocation");

            //vehicle:Vehicle - VSAR may have multiple cars returned.
            final List<Node> vehicleListNL = PojoXmlUtil.getNodesByTagName(vsarRspNode, "Vehicle");
            for (final Node vehicleNode : vehicleListNL) {
                final CarProductType carProduct = new CarProductType();
                //Read VendorLocationKey
                final CarInventoryKeyType carInventoryKey = UAPICommonNodeReader.readDateLocationForMultiVendorLocation(carsInventoryDS, scsDataSource, dateLocationNode).get(vehicleNode.getAttributes().getNamedItem("VendorLocationKey").getNodeValue());
                //Read Vehicle/VendorCode
                carInventoryKey.getCarCatalogKey().setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, false));
                carInventoryKey.getCarCatalogKey().setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDS, vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));

                //Read AvailStatusCode/carRate/ccGurantee/costList/ from VehicleRate
                //AvailStatusCode
                final Node vehicleRateNode = PojoXmlUtil.getNodeByTagName(vehicleNode, "VehicleRate");
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("CorporateRate")) && "true".equals(vehicleRateNode.getAttributes().getNamedItem("CorporateRate").getNodeValue())) {
                    continue;
                }
                carProduct.setAvailStatusCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.AVAIL_STATUS, "", vehicleRateNode.getAttributes().getNamedItem("RateAvailability").getNodeValue()));

                //CarRate
                final CarRateType carRate = carInventoryKey.getCarRate() == null ? new CarRateType() : carInventoryKey.getCarRate();
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCategory"))) {
                    carRate.setRateCategoryCode(vehicleRateNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RatePeriod"))) {
                    carRate.setRatePeriodCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, 0, CommonConstantManager.DomainType.RATE_PERIOD, "", vehicleRateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue()));
                }
                if (StringUtils.isEmpty(carRate.getRatePeriodCode())) {
                    carRate.setRatePeriodCode("Trip");
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCode"))) {
                    carRate.setRateCode(vehicleRateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
                }
                //if PropagateCDCodeUpstream_enable == 1, we will also return CD code.
                final CarsSCSHelper scsHelper = new CarsSCSHelper(scsDataSource);
                final long supplierId = carInventoryKey.getCarCatalogKey().getVendorSupplierID();
                SupplierConfiguration supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, env, supplierId);
                String supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
                if (CompareUtil.isObjEmpty(supplierConfig)) {
                    supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, env, 0L);
                    supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
                }
                if (CompareUtil.isObjEmpty(supplierConfig)) {
                    supplierConfiguration = scsHelper.getSupplierSetting(CommonConstantManager.SupplierConfigurationSettingName.PROPAGATE_CDCODE_UPSTREAM_ENABLE, null, 0L);
                    supplierConfig = CompareUtil.isObjEmpty(supplierConfiguration) ? "" : supplierConfiguration.getSettingValue();
                }

                if (CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber"))) {
                    if (((!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("CorporateRate"))
                            && "true".equals(vehicleRateNode.getAttributes().getNamedItem("CorporateRate").getNodeValue()))
                            || "1".equals(supplierConfig))
                            && !CompareUtil.isObjEmpty(cdCode)) {
                        carRate.setCorporateDiscountCode(cdCode);
                    }
                } else {
                    carRate.setCorporateDiscountCode(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
                }
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("PromotionalCode"))) {
                    carRate.setPromoCode(vehicleRateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
                }
                final Node loyaltyCardNL = PojoXmlUtil.getNodeByTagName(vsarRspNode,"LoyaltyCard");
                if (!CompareUtil.isObjEmpty(loyaltyCardNL)) {
                    final LoyaltyProgramType loyaltyProgram = CompareUtil.isObjEmpty(carRate.getLoyaltyProgram()) ? new LoyaltyProgramType() : carRate.getLoyaltyProgram();
                    loyaltyProgram.setLoyaltyProgramCategoryCode("Car");
                    loyaltyProgram.setLoyaltyProgramCode(vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue());
                    loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyCardNL.getAttributes().getNamedItem("CardNumber").getNodeValue());
                    carRate.setLoyaltyProgram(loyaltyProgram);
                }
                carInventoryKey.setCarRate(carRate);
                carProduct.setCarInventoryKey(carInventoryKey);
                //CarDoorCount
                if (!CompareUtil.isObjEmpty(vehicleNode.getAttributes().getNamedItem("DoorCount"))) {
                    carProduct.setCarDoorCount(Long.valueOf(UAPICommonNodeReader.readDomainValue(scsDataSource,0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_TYPE,"",vehicleNode.getAttributes().getNamedItem("DoorCount").getNodeValue())));
                }

                // CarShuttleCategoryCode
                //carProduct.CarPickupLocation.CarShuttleCategoryCode = CarsInventory.GetCarShuttleCategoryCode(carProduct.CarInventoryKey);
                // SCS mapping is coming from CarWorldspanSCS..[ExternalSupplyServiceDomainValueMap]
                if (!CompareUtil.isObjEmpty(vehicleNode.getAttributes().getNamedItem("Location"))) {
                    final CarLocationType carLocation = new CarLocationType();
                    carLocation.setCarShuttleCategoryCode(UAPICommonNodeReader.readDomainValue(scsDataSource,0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_SHUTTLE_CATEGORY,"",vehicleNode.getAttributes().getNamedItem("Location").getNodeValue()));
                    carProduct.setCarPickupLocation(carLocation);
                }
                //CCGurantee RequiredPayment="Guarantee"
                if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RequiredPayment")) && "Guarantee".equals(vehicleRateNode.getAttributes().getNamedItem("RequiredPayment").getNodeValue())) {
                    carProduct.setReservationGuaranteeCategory("Required");
                }

                //carMileage
                final Node supplierRateNode = PojoXmlUtil.getNodeByTagName(vehicleRateNode,"SupplierRate");
                carProduct.setCarMileage(UAPICommonNodeReader.readCarMileage(vehicleRateNode, supplierRateNode));
                //Cost values
                final long rentalDays = DateTimeUtil.getDiffDays(carInventoryKey.getCarDropOffDateTime(),carInventoryKey.getCarPickUpDateTime());
                carProduct.setCostList(UAPICommonNodeReader.readCostList(supplierRateNode, carProduct.getCarInventoryKey().getCarRate().getRatePeriodCode(), rentalDays));

                carProductList.add(carProduct);
            }
        }
        return carProductList;
    }

    public CarInventoryKeyType getCarInventoryKey() {
        return carInventoryKey;
    }

    public void setCarInventoryKey(CarInventoryKeyType carInventoryKey) {
        this.carInventoryKey = carInventoryKey;
    }

    public boolean isCcGuarantee() {
        return ccGuarantee;
    }

    public void setCcGuarantee(boolean ccGuarantee) {
        this.ccGuarantee = ccGuarantee;
    }

    public String getAvailStatusCode() {
        return availStatusCode;
    }

    public void setAvailStatusCode(String availStatusCode) {
        this.availStatusCode = availStatusCode;
    }

    public CostListType getCostList() {
        return costList;
    }

    public void setCostList(CostListType costList) {
        this.costList = costList;
    }

    public CarMileageType getCarMileage() {
        return carMileage;
    }

    public void setCarMileage(CarMileageType carMileage) {
        this.carMileage = carMileage;
    }

    public boolean isCorporateRate() {
        return corporateRate;
    }

    public void setCorporateRate(boolean corporateRate) {
        this.corporateRate = corporateRate;
    }

    public String  getDiscountNumberApplied() {
        return discountNumberApplied;
    }

    public void setDiscountNumberApplied(String discountNumberApplied) {
        this.discountNumberApplied = discountNumberApplied;
    }
}
