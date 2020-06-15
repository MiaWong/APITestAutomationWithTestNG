package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.*;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.AvailStatusReader.readVehAvailStatus;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarMileageReader.readCarMileage;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.DateLocationReader.*;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.ReferenceIDReader.readReferenceID;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.SpecialEquipReader.readCarVehicleOptionListFromPricedEquips;

/**
 * Created by v-mechen on 11/29/2016.
 */
public class TVARRsp {
    private CarProductListType carProductList;
    private String primaryLangID;

    public TVARRsp(Node response, CarsSCSDataSource scsDataSource,
                   boolean isCarBS, boolean forDetail) throws DataAccessException {
        //Read the cars from response
        this.carProductList = readSearchCarProducts(response, scsDataSource,  isCarBS, forDetail);

        //Read primaryLangID
        if (response.getAttributes().getNamedItem("PrimaryLangID") == null){
            this.primaryLangID = null;
        }
        else{
            this.primaryLangID = response.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }
    }

    public CarProductListType readSearchCarProducts(Node response, CarsSCSDataSource scsDataSource,
                                                           boolean isCarBS, boolean forDetail) throws DataAccessException {
        final CarProductListType cars = new CarProductListType();
        cars.setCarProduct(new ArrayList<CarProductType>());
        final List<Node> vendorAvailList = PojoXmlUtil.getNodesByTagName(response, "VehVendorAvail");
        if (vendorAvailList.isEmpty()){
            return cars;
        }
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(response, "VehRentalCore");
        //Pickup, dropoff date time - only one in one response
        final CarInventoryKeyType keyForDateT = new CarInventoryKeyType();
        readDate(keyForDateT, vehRentalCoreNode);

        //Read per vendor
        for(final Node vendorAvail : vendorAvailList)
        {
            //read vendorSupplierID and location info first
            final Long supplierID = GDSMsgReadHelper.readVendorSupplierID(scsDataSource, PojoXmlUtil.getNodeByTagName(vendorAvail, "Vendor").
                    getAttributes().getNamedItem("Code").getTextContent());
            final CarProductType locationCar = new CarProductType(); //CarProduct to take location info
            locationCar.setCarInventoryKey(new CarInventoryKeyType());
            locationCar.getCarInventoryKey().setCarPickUpDateTime(keyForDateT.getCarPickUpDateTime());
            locationCar.getCarInventoryKey().setCarDropOffDateTime(keyForDateT.getCarDropOffDateTime());
            readLocationsFromLocationDetails(locationCar, PojoXmlUtil.getNodesByTagName(vendorAvail, "LocationDetails"), scsDataSource,
                    supplierID);
            //Get all cars for this supplier
            final List<Node> vehAvailNodeList = PojoXmlUtil.getNodesByTagName(vendorAvail, "VehAvail");
            for (final Node vehAvailNode : vehAvailNodeList)
            {
                final CarProductType car = readSearchVehAvail(vehAvailNode, scsDataSource, supplierID,isCarBS, forDetail);
                //Apply vendor level var - supplierID
                car.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(supplierID);
                //Apply response level Pickup, dropoff date time and location
                car.getCarInventoryKey().setCarPickUpDateTime(keyForDateT.getCarPickUpDateTime());
                car.getCarInventoryKey().setCarDropOffDateTime(keyForDateT.getCarDropOffDateTime());

                if (car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID() == null ||
                        car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID() == 0)
                {
                    car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setCarVendorLocationID(
                            locationCar.getCarPickupLocation().getCarLocationKey().getCarVendorLocationID());
                    car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode(
                            locationCar.getCarPickupLocation().getCarLocationKey().getLocationCode());
                    car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setCarLocationCategoryCode(
                            locationCar.getCarPickupLocation().getCarLocationKey().getCarLocationCategoryCode());
                    car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setSupplierRawText(
                            locationCar.getCarPickupLocation().getCarLocationKey().getSupplierRawText());

                }
                if (car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID() == null
                || car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID() == 0)
                {
                    car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setCarVendorLocationID(
                            locationCar.getCarDropOffLocation().getCarLocationKey().getCarVendorLocationID());
                    car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode(
                            locationCar.getCarDropOffLocation().getCarLocationKey().getLocationCode());
                    car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setCarLocationCategoryCode(
                            locationCar.getCarDropOffLocation().getCarLocationKey().getCarLocationCategoryCode());
                    car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setSupplierRawText(
                            locationCar.getCarDropOffLocation().getCarLocationKey().getSupplierRawText());

                }
                car.setCarPickupLocation(locationCar.getCarPickupLocation());
                car.setCarDropOffLocation(locationCar.getCarDropOffLocation());
                cars.getCarProduct().add(car);
            }
        }

        return cars;
    }

    /// <summary>
    /// Build CarProduct from VehAvail
    /// </summary>
    /// <param name="VehAvailNode"></param>
    /// <param name="domainValueMapHelper"></param>
    /// <returns></returns>
    public CarProductType readSearchVehAvail(Node vehAvailNode, CarsSCSDataSource scsDataSource, Long supplierID, boolean isCarBS, boolean forDetail) throws DataAccessException {
        final CarProductType car = new CarProductType();
        car.setCarInventoryKey(new CarInventoryKeyType());
        //SIPP
        final Node externalSIPPNode = PojoXmlUtil.getNodeByTagName(vehAvailNode, "VehMakeModel").getAttributes().getNamedItem("Code");
        if (externalSIPPNode != null) {
            final String externalSIPP = externalSIPPNode.getTextContent().trim();
            final VehMakeModel sippObj = new VehMakeModel(externalSIPP.substring(0, 1), externalSIPP.substring(1, 2), externalSIPP.substring(2, 3),
                    externalSIPP.substring(3), true);

            car.setCarInventoryKey(GDSMsgReadHelper.readVehMakeModel(car.getCarInventoryKey(), scsDataSource, sippObj));

        }
        //Location - VendorLocation for pickup and DropOffLocation for dropoff
        final CarInventoryKeyType carInventoryKey = car.getCarInventoryKey();
        readLocationFromVehAvail(carInventoryKey, vehAvailNode, scsDataSource, supplierID);
        car.setCarInventoryKey(carInventoryKey);
        //CostList
        CostListReader.buildCostAvailCostList(car, vehAvailNode, forDetail);
        //CarMileage
        car.setCarMileage(readCarMileage(PojoXmlUtil.getNodeByTagName(vehAvailNode, "RateDistance"), PojoXmlUtil.getNodesByTagName(vehAvailNode, "Fee"), scsDataSource));
        //AvailStatusCode
        car.setAvailStatusCode(readVehAvailStatus(PojoXmlUtil.getNodeByTagName(vehAvailNode, "VehAvailCore")));
        //Sepcial equipment mapping
        readCarVehicleOptionListFromPricedEquips(car, scsDataSource, PojoXmlUtil.getNodesByTagName(vehAvailNode, "PricedEquip"), isCarBS);
        //CarRate
        car.getCarInventoryKey().setCarRate(new CarRateType());
        car.getCarInventoryKey().getCarRate().setRatePeriodCode("Trip");
        car.getCarInventoryKey().getCarRate().setCarRateQualifierCode(readReferenceID(vehAvailNode));
        //Car door count - VehType/@DoorCount
        String carDoorS = PojoXmlUtil.getNodeByTagName(vehAvailNode, "VehType").getAttributes().getNamedItem("DoorCount").getTextContent();
        if (carDoorS.contains("-")){
            carDoorS = carDoorS.split("-")[carDoorS.split("-").length - 1];
        }
        if (carDoorS.contains("/")){
            carDoorS = carDoorS.split("/")[carDoorS.split("/").length - 1];
        }
        car.setCarDoorCount(Long.parseLong(carDoorS));
        //CarCatalogMakeModel
        car.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(vehAvailNode));

        //CarRateDetails - note: Additional fee is already read in CostList reader
        ConditionalCostPriceReader.buildConditionalCostPriceList(car, PojoXmlUtil.getNodesByTagName(vehAvailNode, "PricedCoverages").get(0).getChildNodes());
        ConditionalCostPriceReader.buildConditionalCostPriceListFromFee(car, PojoXmlUtil.getNodesByTagName(vehAvailNode, "Fee"));
        CarCoveragesCostListReader.readCarCoveragesCostListFromPricedCoverages(car,
                        PojoXmlUtil.getNodesByTagName(vehAvailNode, "PricedCoverages").get(0).getChildNodes());
        CarInsuranceIncludedInRateReader.readCarInsuranceIncludedInRate(car, PojoXmlUtil.getNodesByTagName(vehAvailNode, "PricedCoverages").get(0).getChildNodes());
        //CASSS-9561 Insurance included for GDSP Prepaid inventory - Supply Changes Titanium
        CarCoveragesCostListReader.readCarInsuranceIncludedFromPricedCoverages(car,
                PojoXmlUtil.getNodesByTagName(vehAvailNode, "PricedCoverages").get(0).getChildNodes());


        return car;
    }


    public CarProductListType getCarProduct() {
        return this.carProductList;
    }

    public void setCarCarProduct(CarProductListType carProductList) {
        this.carProductList = carProductList;
    }

    public String getPrimaryLangID() {
        return this.primaryLangID;
    }

    public void setPrimaryLangID(String primaryLangID) {
        this.primaryLangID = primaryLangID;
    }

}


