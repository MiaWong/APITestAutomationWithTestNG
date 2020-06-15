package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CostPerDistanceType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.CurrencyAmountType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.commonreader.VehAvailNodeHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCatalogMakeModelReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.AvailStatusReader.readVehAvailStatus;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.DateLocationReader.readDate;

/**
 * Created by fehu on 12/15/2016.
 */
public class VARRsp {

    final private CarProductListType carProductList;

    public CarProductListType getCarProductList() {
        return carProductList;
    }

    public VARRsp(Node response,  CarsSCSDataSource scsDataSource) throws Exception {

        this.carProductList = readSearchCarProducts(response,  scsDataSource);
    }

    public CarProductListType readSearchCarProducts(Node response, CarsSCSDataSource scsDataSource) throws Exception {
        final CarProductListType cars = new CarProductListType();
        final List<Node> vehAvailList = PojoXmlUtil.getNodesByTagName(response, "VehAvail");
        if (vehAvailList.isEmpty()) {
            return cars;
        }
        final List<CarProductType> carProductTypes = new ArrayList<>();
        cars.setCarProduct(carProductTypes);
        //Read per vendor
        for (final Node vehAvail : vehAvailList) {
            final CarProductType carProductType = new CarProductType();
            carProductTypes.add(carProductType);
            //CarInventoryKey
            carInventoryKey(response, scsDataSource, vehAvail, carProductType);

            //CarpickupLocation
            carpickupLocation(vehAvail, carProductType);

            //CarMileage
            carMileage(scsDataSource, vehAvail, carProductType);


            //Special Equipment(there is no priceEquip in VAR now, if support added in future)


            //CarCatalogMakeModel
            carProductType.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(vehAvail));
            //Based on CASSS-10368 Micronnexus : Show Number of Doors from GDS for both Min and Max, if min door count not exist, get min door count from max door count
            if(carProductType.getCarCatalogMakeModel().getCarMinDoorCount() == 0)
            {
                carProductType.getCarCatalogMakeModel().setCarMinDoorCount(carProductType.getCarCatalogMakeModel().getCarMaxDoorCount());
            }

            //CarRateDetail
            VehAvailNodeHelper.carRateDetail(vehAvail, carProductType, scsDataSource);

            //AvailStatusCode
            carProductType.setAvailStatusCode(readVehAvailStatus(PojoXmlUtil.getNodeByTagName(vehAvail, "VehAvailCore")));

            //CostList
            createCostList(vehAvail, carProductType, scsDataSource);

            carProductType.setProviderID(3l);
        }

        return cars;
    }


    private void carInventoryKey(Node response, CarsSCSDataSource scsDataSource, Node vehAvail, CarProductType carProductType) throws Exception {
        final CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
        carProductType.setCarInventoryKey(carInventoryKey);

        //set PickupTime and dropUptime
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(response, "VehRentalCore");
        readDate(carInventoryKey, vehRentalCoreNode);

        final CarCatalogKeyType carCatalogKeyType = new CarCatalogKeyType();
        carInventoryKey.setCarCatalogKey(carCatalogKeyType);

        //get VendorCode
        final String vendorCode = PojoXmlUtil.getNodeByTagName(vehAvail, "Vendor").
                getAttributes().getNamedItem("Code").getTextContent();

        //setSupplierId
        carCatalogKeyType.setVendorSupplierID(Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(vendorCode)));


        final CarLocationKeyType carPickupLocationKey = new CarLocationKeyType();
        carCatalogKeyType.setCarPickupLocationKey(carPickupLocationKey);


        //get PickupLocationCode
        final String pickupLocationCode = PojoXmlUtil.getNodeByTagName(vehAvail, "VendorLocation").
                getAttributes().getNamedItem("LocationCode").getTextContent();

        //get dropoffLoctionCode
        final String dropoffLocationCode = PojoXmlUtil.getNodeByTagName(vehAvail, "DropOffLocation").
                getAttributes().getNamedItem("LocationCode").getTextContent();


        final CarLocationKeyType carDropoffLocationKey = new CarLocationKeyType();
        carCatalogKeyType.setCarDropOffLocationKey(carDropoffLocationKey);

        VehAvailNodeHelper.setLocationCodes(carPickupLocationKey, pickupLocationCode);

        VehAvailNodeHelper.setLocationCodes(carDropoffLocationKey, dropoffLocationCode);

        final String carModel = PojoXmlUtil.getNodeByTagName(vehAvail, "VehMakeModel").
                getAttributes().getNamedItem("Code").getTextContent();
        final VehMakeModel vehMakeModel = new VehMakeModel(carModel.substring(0, 1), carModel.substring(1, 2), carModel.substring(2, 3), carModel.substring(3, 4), true);
        GDSMsgReadHelper.readVehMakeModel(carInventoryKey, scsDataSource, vehMakeModel);


        //set CarRate
        final CarRateType carRateType = new CarRateType();
        carInventoryKey.setCarRate(carRateType);
        carRateType.setRatePeriodCode("Trip");
        final Node corpDiscountNmbr = PojoXmlUtil.getNodeByTagName(vehAvail, "RateQualifier").
                getAttributes().getNamedItem("CorpDiscountNmbr");
        if (null != corpDiscountNmbr) {
            carRateType.setCorporateDiscountCode(corpDiscountNmbr.getTextContent());
        }
        final Node promoDesc = PojoXmlUtil.getNodeByTagName(vehAvail, "RateQualifier").
                getAttributes().getNamedItem("PromoDesc");
        if (null != promoDesc) {
            carRateType.setPromoCode(promoDesc.getTextContent());
        }
        final Node idContext = PojoXmlUtil.getNodeByTagName(vehAvail, "Reference").
                getAttributes().getNamedItem("ID_Context");
        carRateType.setCarRateQualifierCode(idContext.getTextContent());

    }

    private void carMileage(CarsSCSDataSource scsDataSource, Node vehAvail, CarProductType carProductType) throws DataAccessException {
        final CarMileageType carMileageType = new CarMileageType();

        final DistanceType freeDistanceType = new DistanceType();
        carMileageType.setFreeDistance(freeDistanceType);

        carProductType.setCarMileage(carMileageType);

        if ("true".equalsIgnoreCase(PojoXmlUtil.getNodeByTagName(vehAvail, "RateDistance").
                getAttributes().getNamedItem("Unlimited").getTextContent())) {
            freeDistanceType.setDistanceUnitCount(-1);
        } else {   //set freeDistanceUnit
            freeDistanceType.setDistanceUnit(VehAvailNodeHelper.getDistanceUnit(vehAvail));
            freeDistanceType.setDistanceUnitCount(Integer.parseInt(PojoXmlUtil.getNodeByTagName(vehAvail, "RateDistance").
                    getAttributes().getNamedItem("Quantity").getTextContent()));

            //set CostPerDistance
            final CostPerDistanceType costPerDistanceType = new CostPerDistanceType();
            final DistanceType distanceType = new DistanceType();
            costPerDistanceType.setDistance(distanceType);
            carMileageType.setExtraCostPerDistance(costPerDistanceType);
            distanceType.setDistanceUnit(VehAvailNodeHelper.getDistanceUnit(vehAvail));
            distanceType.setDistanceUnitCount(1);

            final List<Node> vehicleCharges = PojoXmlUtil.getNodesByTagName(vehAvail, "VehicleCharge");
            String amount = "0";
            String currencyCode = "";
            for (final Node vehicleCharge : vehicleCharges) {
                if ("8".equals(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent())) {
                    if (vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent().trim().length() > 0) {
                        amount = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                    }
                    if (vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim().length() > 0) {
                        currencyCode = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent().trim();
                    }

                }
            }
            final CurrencyAmountType currencyAmountType = VehAvailNodeHelper.getCurrencyAmountType(amount, currencyCode);

            costPerDistanceType.setCostCurrencyAmount(currencyAmountType);
        }
        final Node VehiclePeriodUnitName = PojoXmlUtil.getNodeByTagName(vehAvail, "RateDistance").getAttributes().getNamedItem("VehiclePeriodUnitName");
        if (null == VehiclePeriodUnitName) {
            carMileageType.setFreeDistanceRatePeriodCode("Daily");
        } else {
            final List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMaps = scsDataSource.getExternalSupplyServiceDomainValueMap(CommonConstantManager.DomainType.CAR_MILEAGE_RATE_PERIOD, VehiclePeriodUnitName.getTextContent());
            if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMaps)) {
                carMileageType.setFreeDistanceRatePeriodCode(externalSupplyServiceDomainValueMaps.get(0).getDomainValue());
            }
        }
    }

    private void carpickupLocation(Node vehAvail, CarProductType carProductType) {
        final CarLocationType carPickupLocation = new CarLocationType();
        carProductType.setCarPickupLocation(carPickupLocation);
        carPickupLocation.setCarLocationKey(carProductType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
        carPickupLocation.setCarVendorLocationID(carProductType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID());
        final String countLocation = PojoXmlUtil.getNodesByTagName(vehAvail, "VendorLocation").get(0).getAttributes().getNamedItem("CounterLocation").getTextContent();
        if ("14".equals(countLocation)) {
            carPickupLocation.setCarShuttleCategoryCode("NoShuttle");
        }
        if ("10".equals(countLocation)) {
            carPickupLocation.setCarShuttleCategoryCode("ShuttleToCounter");
        }
    }

    public static void createCostList(Node nodeObject, CarProductType carProductType, CarsSCSDataSource scsDataSource) throws DataAccessException {
        // all PricedCoverage items are already included in Total.
        final List<CostType> costTypes = VehAvailNodeHelper.getCostTypes(carProductType);

        //MN does not return base rate breakdown. We always get Total per trip
        String mnTotalCost = "";
        String currency = "";
        String mnOriginalTotal = "";
        String mnbaseRate = "";
        String supplierCurrency = "";
        final List<Node> vehicleCharges = PojoXmlUtil.getNodesByTagName(nodeObject, "VehicleCharge");
        for (final Node vehicleCharge : vehicleCharges)
        {
            if ("baserate".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnbaseRate = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
            }
            if ("preferred".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnTotalCost = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                currency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            }
            if ("original".equalsIgnoreCase(vehicleCharge.getAttributes().getNamedItem("Purpose").getTextContent()))
            {
                mnOriginalTotal = vehicleCharge.getAttributes().getNamedItem("Amount").getTextContent();
                supplierCurrency = vehicleCharge.getAttributes().getNamedItem("CurrencyCode").getTextContent();
            }
        }
        //create total cost Cost element
        VehAvailNodeHelper.totalCost(costTypes, mnTotalCost, currency);

        //create base rate Cost element
        VehAvailNodeHelper.costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(mnTotalCost, currency), "Trip", "Base", 1l, "base rate (vendor currency)", VehAvailNodeHelper.getLegacyFinanceKeyType(1l, 14l, 1l));

        //MN Commission: Calculate Commission when Total and BaseRate are provided by MN and the difference is greater than 0
        final BigDecimal mnCommission = new BigDecimal(mnOriginalTotal).subtract(new BigDecimal(mnbaseRate));
        if (mnCommission.compareTo(BigDecimal.ZERO) > 0)
        {
            VehAvailNodeHelper.costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(String.valueOf(mnCommission),supplierCurrency), "Included", "Commission", 0l, "", VehAvailNodeHelper.getLegacyFinanceKeyType(19l, 0, 93l));
        }

        //MN Transaction Fees
        VehAvailNodeHelper.transactionFees(costTypes, mnTotalCost, currency);

        //misc charges
        VehAvailNodeHelper.costList(costTypes, VehAvailNodeHelper.getCurrencyAmountType(String.valueOf(new BigDecimal(mnOriginalTotal).subtract(new BigDecimal(mnTotalCost))), currency), "Included", "Misc", 0l, "misc charges (taxes, fees, extra days, etc) (vendor currency)", VehAvailNodeHelper.getLegacyFinanceKeyType(8l, 6l, 1l));
    }
}

