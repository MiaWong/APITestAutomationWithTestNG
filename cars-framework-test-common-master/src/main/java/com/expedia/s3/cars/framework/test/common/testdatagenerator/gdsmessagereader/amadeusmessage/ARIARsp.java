package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarMileageType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyListType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.financetypes.defn.v4.CostPerDistanceType;
import com.expedia.e3.data.financetypes.defn.v4.CurrencyAmountType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 1/12/2017.
 */
public class ARIARsp {
    CarProductType car;

    public static final String GUARANTEE_REQUIRED_AT_BOOKING = "FGR";
    public static final String CREDIT_CARD_GUARANTEE = "907";

    public CarProductType getCar() {
        return car;
    }

    public ARIARsp(Node response, CarsSCSDataSource amadeusSCSDataSource, CarInventoryKeyType inventoryRequest) throws DataAccessException {
        final Node rateDetailsNode = PojoXmlUtil.getNodeByTagName(response, "rateDetails");

        if(rateDetailsNode != null)
        {
            readCarProduct4AmadeusGetDetailFromARIA(rateDetailsNode, amadeusSCSDataSource, inventoryRequest);
        }
    }

    private void readCarProduct4AmadeusGetDetailFromARIA(Node rateDetailsNode, CarsSCSDataSource amadeusSCSDataSource,
                                                         CarInventoryKeyType inventoryRequest) throws DataAccessException {
        car = new CarProductType();

        //1. Inventory key
        buildCarsInventory(car, rateDetailsNode, amadeusSCSDataSource, inventoryRequest);

        //2. AvailStatusCode
        car.setAvailStatusCode("A");

        //3.CarCatalogMakeModel
        buildCarCatalogMakeModel(car, rateDetailsNode);

        //4. carDoor
        car.setCarDoorCount(car.getCarCatalogMakeModel().getCarMinDoorCount());

        //5. carMileage
        buildCarMileage(car, rateDetailsNode);

        buildReservationGuaranteeCategory(car, rateDetailsNode);

        //Pickup & Droppoff location.
        buildPickupOrDropOffLocation(car, rateDetailsNode);

        //CarVehicleOptionList
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildCarVehicleOption(car, rateDetailsNode, amadeusSCSDataSource);

        //rateDetail
        commonNodeReader.buildCarRateDetail(car, rateDetailsNode);

        //CarPolicyList
        buildPolicyRules(car, rateDetailsNode);

        //CostList
        commonNodeReader.buildCostList(car, rateDetailsNode, false, true);
    }


    private static void buildCarsInventory(CarProductType carproduct, Node rateDetails, CarsSCSDataSource amadeusSCSDataSource,
                                           CarInventoryKeyType inventoryRequest) throws DataAccessException {
        if (null == carproduct.getCarInventoryKey()) {
            carproduct.setCarInventoryKey(new CarInventoryKeyType());
        }
        if (null == carproduct.getCarInventoryKey().getCarCatalogKey()) {
            carproduct.getCarInventoryKey().setCarCatalogKey(new CarCatalogKeyType());
        }
        final Node carCompanyDataNode = PojoXmlUtil.getNodeByTagName(rateDetails, "companyIdentification");

        /// 1.Get VendorSupplierID
        carproduct.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(
                GDSMsgReadHelper.readVendorSupplierID(amadeusSCSDataSource,
                        PojoXmlUtil.getNodeByTagName(carCompanyDataNode, "companyCode").getTextContent()));

        /// 2.Get CarVehicle
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarVehicle(carproduct.getCarInventoryKey(), rateDetails, amadeusSCSDataSource, false);

        // 3.Get CarPickupLocationKey // 4.Get CarDropOffLocationKey
        commonNodeReader.readCarPickupAndDropOffLocationKey(carproduct.getCarInventoryKey(), rateDetails, "pickupDropoffLocation");

        /// 5 pick/dropp off time
        carproduct.getCarInventoryKey().setCarPickUpDateTime(inventoryRequest.getCarPickUpDateTime());
        carproduct.getCarInventoryKey().setCarDropOffDateTime(inventoryRequest.getCarDropOffDateTime());

        /// 6 Car Rate .
        if (null == carproduct.getCarInventoryKey().getCarRate()) {
            carproduct.getCarInventoryKey().setCarRate(new CarRateType());
        }
        //carproduct.getCarInventoryKey().getCarRate().RateCategoryCode = "Standard";
        carproduct.getCarInventoryKey().getCarRate().setCarRateQualifierCode(inventoryRequest.getCarRate().getCarRateQualifierCode());
        carproduct.getCarInventoryKey().getCarRate().setCorporateDiscountCode(inventoryRequest.getCarRate().getCorporateDiscountCode());
        carproduct.getCarInventoryKey().getCarRate().setRatePeriodCode(inventoryRequest.getCarRate().getRatePeriodCode());

        //build rate code.
        final Node rateCodeGroupNode = PojoXmlUtil.getNodeByTagName(rateDetails, "rateCodeGroup");
        if(null != rateCodeGroupNode)
        {
            carproduct.getCarInventoryKey().getCarRate().setRateCode(PojoXmlUtil.getNodeByTagName(rateCodeGroupNode, "fareType").getTextContent());
        }

        carproduct.getCarInventoryKey().getCarRate().setRateCategoryCode(inventoryRequest.getCarRate().getRateCategoryCode());

        /// 7 supplierID ,subsetID
        carproduct.getCarInventoryKey().setSupplySubsetID(inventoryRequest.getSupplySubsetID());
        carproduct.getCarInventoryKey().setCarItemID(inventoryRequest.getCarItemID());
    }

    //CatalogMakeModel & DoorCount
    private void buildCarCatalogMakeModel(CarProductType carproduct, Node rateDetailsNode) {
        if (null == carproduct.getCarCatalogMakeModel()) {
            carproduct.setCarCatalogMakeModel(new CarCatalogMakeModelType());
        }

        final Node vehicleInfoGroup = PojoXmlUtil.getNodeByTagName(rateDetailsNode, "vehicleInfoGroup");
        final Node vehicleDetails = PojoXmlUtil.getNodeByTagName(vehicleInfoGroup, "vehicleDetails");

        carproduct.getCarCatalogMakeModel().setCarMakeString(PojoXmlUtil.getNodeByTagName(vehicleDetails, "carModel").getTextContent());
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.readCarDoorCount(carproduct.getCarCatalogMakeModel(), vehicleDetails);
    }

    private void buildCarMileage(CarProductType carproduct, Node rateDetailsNode) {
        if (null == carproduct.getCarMileage()) {
            carproduct.setCarMileage(new CarMileageType());
        }

        if (null == carproduct.getCarMileage().getFreeDistance()) {
            carproduct.getCarMileage().setFreeDistance(new DistanceType());
        }

        carproduct.getCarMileage().getFreeDistance().setDistanceUnitCount(-1);

        /// rateDetails/rateDetail/chargeDetail[type=31 or 32]/amount
        final List<Node> rateDetailNodes = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "rateDetail");
        Node expectRateDetailNode = null;
        for (final Node rateDetail : rateDetailNodes)
        {
            if ("RP".equals(PojoXmlUtil.getNodeByTagName(rateDetail, "amountType").getTextContent()))
            {
                expectRateDetailNode = rateDetail;
            }
        }
        if(null != expectRateDetailNode)
        {
            final List<Node> chargeDetailList = PojoXmlUtil.getNodesByTagName(expectRateDetailNode, "chargeDetails");

            for (final Node chargeDetail : chargeDetailList)
            {
                buildCarMileageDistance(carproduct.getCarMileage(), chargeDetail);
            }
            final String currencyCode = PojoXmlUtil.getNodeByTagName(expectRateDetailNode, "currency").getTextContent();
            if(null != carproduct.getCarMileage().getExtraCostPerDistance() && null !=  carproduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount())
            {
                carproduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().setCurrencyCode(currencyCode);
            }
        }
    }

    private void buildCarMileageDistance(CarMileageType mileageType, Node chargeDetail) {
        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();
        switch (type) {
            case "033":
                mileageType.getFreeDistance().setDistanceUnitCount(Integer.parseInt(PojoXmlUtil.getNodeByTagName(chargeDetail, "numberInParty").getTextContent()));
                mileageType.getFreeDistance().setDistanceUnit(CommonEnumManager.DistanceUnit.MI.getDistanceUnit());
                break;
            case "034":
                //final Node amoutNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");
                //if (null != amoutNode) {
                //    final String unitCount = amoutNode.getTextContent();
                   // if (null != unitCount && !StringUtils.isEmpty(unitCount)) {
                final Node numberInPartyNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "numberInParty");
                if(null != numberInPartyNode)
                {
                    mileageType.getFreeDistance().setDistanceUnitCount(Integer.parseInt(numberInPartyNode.getTextContent()));
                }
                mileageType.getFreeDistance().setDistanceUnit(CommonEnumManager.DistanceUnit.KM.getDistanceUnit());

                break;
            case "031":
                buildExtraCostPerDistance(mileageType, chargeDetail, CommonEnumManager.DistanceUnit.MI.getDistanceUnit());
                break;
            case "032":
                buildExtraCostPerDistance(mileageType, chargeDetail, CommonEnumManager.DistanceUnit.KM.getDistanceUnit());
                break;
            default:
                break;
        }
    }

    private void buildExtraCostPerDistance(CarMileageType mileageType, Node chargeDetail, String distanceUnit) {
        if (null == mileageType.getExtraCostPerDistance()) {
            mileageType.setExtraCostPerDistance(new CostPerDistanceType());
        }
        if (null == mileageType.getExtraCostPerDistance().getDistance()) {
            mileageType.getExtraCostPerDistance().setDistance(new DistanceType());
        }
        mileageType.getExtraCostPerDistance().getDistance().setDistanceUnitCount(1);
        mileageType.getExtraCostPerDistance().getDistance().setDistanceUnit(distanceUnit);

        if (null == mileageType.getExtraCostPerDistance().getCostCurrencyAmount()) {
            mileageType.getExtraCostPerDistance().setCostCurrencyAmount(new CurrencyAmountType());
        }
        if (null == mileageType.getExtraCostPerDistance().getCostCurrencyAmount().getAmount()) {
            mileageType.getExtraCostPerDistance().getCostCurrencyAmount().setAmount(new AmountType());
        }


        final double amountdouble = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeDetail, "amount").getTextContent()) * Math.pow(10, 2);
        mileageType.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimal(new Double(amountdouble).intValue());
        mileageType.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimalPlaceCount(2);
    }

    private void buildReservationGuaranteeCategory(CarProductType carproduct, Node rateDetailsNode) {
        /// IF rateDetails/otherRulesGroup/otherRules/ruleDetails [type = 'FGR' and qualifier = '907'] EXISTS for 'Required'
        final Node otherRulesGroup = PojoXmlUtil.getNodeByTagName(rateDetailsNode, "otherRulesGroup");
        final List<Node> otherRules = PojoXmlUtil.getNodesByTagName(otherRulesGroup, "otherRules");
        for (final Node otherRule : otherRules) {
            if (PojoXmlUtil.getNodeByTagName(otherRule, "type").getTextContent().equals(GUARANTEE_REQUIRED_AT_BOOKING) &&
                    PojoXmlUtil.getNodeByTagName(otherRule, "qualifier").getTextContent().equals(CREDIT_CARD_GUARANTEE)) {
                carproduct.setReservationGuaranteeCategory("required");
                break;
            }
        }
    }

    private void buildPickupOrDropOffLocation(CarProductType carproduct, Node rateDetailsNode) {
        final List<Node> pickupDropoffLocationsNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "pickupDropoffLocation");
        final Node pickupLocationNode = pickupDropoffLocationsNodeList.get(0);
        Node dropOffLocationNode = pickupLocationNode;
        if (pickupDropoffLocationsNodeList.size() > 1) {
            dropOffLocationNode = pickupDropoffLocationsNodeList.get(1);
        }

        // 1.CarPickupLocation
        buildPickUpOrDropOffLocation(carproduct, pickupLocationNode, false);

        // 2.CarDropOffLocation
        buildPickUpOrDropOffLocation(carproduct, dropOffLocationNode, true);

        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildPickupAndDropOffLocationPhoneList(car, rateDetailsNode);
    }

    private void buildPickUpOrDropOffLocation(CarProductType carproduct,
                                              Node pickUpDropOffLocationNode, boolean isDropOffLocation) {
        final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
        commonNodeReader.buildCarPickupAndDropOffLocationKey(carproduct, isDropOffLocation);
        CarLocationType carLocation = carproduct.getCarPickupLocation();
        if (isDropOffLocation) {
            carLocation = carproduct.getCarDropOffLocation();
        }

        /// address
        commonNodeReader.buildPickUpDropOffAddress(carLocation, pickUpDropOffLocationNode);
        ///build Open Time schedules
        commonNodeReader.buildPickUpDropOffRecurringPeriod(carLocation, carproduct.getCarInventoryKey(), pickUpDropOffLocationNode, isDropOffLocation);
    }

    private void buildPolicyRules(CarProductType carproduct, Node rateDetailsNode) {
        /// CarPolicyList  --  otherRules
        if (null == carproduct.getCarPolicyList()) {
            carproduct.setCarPolicyList(new CarPolicyListType());
        }

        buildCarPolicyListFromOtherRulesGroupNode(carproduct.getCarPolicyList(), rateDetailsNode);

        buildCarPolicyListFromRemarksNode(carproduct.getCarPolicyList(), rateDetailsNode);

        buildCarPolicyListFromTaxCovSurchargeGroupNode(carproduct.getCarPolicyList(), rateDetailsNode);
    }

    private void buildCarPolicyListFromOtherRulesGroupNode(CarPolicyListType carPolicyList, Node rateDetailsNode) {
        final List<Node> otherRuleGroups = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "otherRulesGroup");

        for (final Node ruleNode : otherRuleGroups) {
            final String type = PojoXmlUtil.getNodeByTagName(ruleNode, "type").getTextContent();
            if (null != type && type.equals("OWI")) {
                buildCarPolicy(carPolicyList, "One Way Information", ruleNode);
            } else if (null != type && type.equals("POL")) {
                buildCarPolicy(carPolicyList, "Policy Information", ruleNode);
            } else if (null != type && type.equals("GUA")) {
                buildCarPolicy(carPolicyList, "Guarantee Information", ruleNode);
            }
        }
    }

    private void buildCarPolicyListFromRemarksNode(CarPolicyListType carPolicyList, Node rateDetailsNode) {
        /// Milscellaneous
        final Node remarksNode = PojoXmlUtil.getNodeByTagName(rateDetailsNode, "remarks");
        if (remarksNode != null) {
            final List<Node> freeTextRemarks = PojoXmlUtil.getNodesByTagName(remarksNode, "freeText");
            if (null == carPolicyList.getCarPolicy()) {
                carPolicyList.setCarPolicy(new ArrayList<CarPolicyType>());
            }
            if (!freeTextRemarks.isEmpty()) {
                for (final Node freeText : freeTextRemarks) {
                    final CarPolicyType policyRemarks = new CarPolicyType();
                    policyRemarks.setCarPolicyCategoryCode("Miscellaneous");
                    policyRemarks.setCarPolicyRawText(freeText.getTextContent());
                    carPolicyList.getCarPolicy().add(policyRemarks);
                }
            }
        }
    }

    private void buildCarPolicyListFromTaxCovSurchargeGroupNode(CarPolicyListType carPolicyList, Node rateDetailsNode) {
        final List<Node> covNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        for (final Node covNode : covNodeList) {
            final String typeCov = PojoXmlUtil.getNodeByTagName(covNode, "type").getTextContent();
            if (typeCov != null && typeCov.equals("COV")) {
                buildCarPolicy(carPolicyList, PojoXmlUtil.getNodeByTagName(covNode, "comment").getTextContent(), covNode);
            }
        }
    }

    private void buildCarPolicy(CarPolicyListType carPolicyList, String carPolicyCategoryCode, Node ruleNode) {
        final CarPolicyType policy = new CarPolicyType();
        policy.setCarPolicyCategoryCode(carPolicyCategoryCode);
        policy.setCarPolicyRawText("");

        final List<Node> freeTextList = PojoXmlUtil.getNodesByTagName(ruleNode, "freeText");
        if (!freeTextList.isEmpty()) {
            for (final Node freeText : freeTextList) {
                policy.setCarPolicyRawText(policy.getCarPolicyRawText() + freeText.getTextContent() + " ");
            }
        }
        if (null == carPolicyList.getCarPolicy()) {
            carPolicyList.setCarPolicy(new ArrayList<CarPolicyType>());
        }
        carPolicyList.getCarPolicy().add(policy);
    }
}