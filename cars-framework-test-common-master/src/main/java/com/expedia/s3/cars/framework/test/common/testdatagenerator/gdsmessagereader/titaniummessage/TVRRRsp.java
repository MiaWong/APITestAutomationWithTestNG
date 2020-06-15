package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.*;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCoveragesCostListReader.readCarCoveragesCostListFromPricedCoverages;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarMileageReader.readCarMileage;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.ConditionalCostPriceReader.buildConditionalCostPriceList;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CostListReader.readCostListFromXmlDoc;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.SpecialEquipReader.readCarVehicleOptionListFromPricedEquips;

/**
 * Created by fehu on 11/24/2016.
 */
@SuppressWarnings("CPD-START")
public class TVRRRsp {
    public CarProductType carProduct;
    public String primaryLangID;

    public TVRRRsp(Node response, CarsSCSDataSource scsDataSource, boolean isCarBS, Long supplierID) throws DataAccessException {

        //ExternalSupplyServiceDomainValueMapHelper domainValueMapHelper = new ExternalSupplyServiceDomainValueMapHelper(CarCommonEnumManager.ServieProvider.TitaniumSCS);
        this.carProduct = readDetailCarProduct(response, scsDataSource, isCarBS, supplierID);

        //Read primaryLangID
        if (response.getAttributes().getNamedItem("PrimaryLangID") == null) {
            this.primaryLangID = null;
        } else {
            this.primaryLangID = response.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }
    }

    /**
     *
     * @param response
     * @return
     */
    public CarProductType readDetailCarProduct(Node response, CarsSCSDataSource scsDataSource, boolean isCarBS,
                                               Long supplierID) throws DataAccessException {
        final CarProductType car = new CarProductType();
        car.setCarInventoryKey(new CarInventoryKeyType());
        //SIPP
        final Node externalSIPPNode = PojoXmlUtil.getNodeByTagName(response, "VehMakeModel").getAttributes().getNamedItem("Code");
        if (externalSIPPNode != null) {
            final String externalSIPP = externalSIPPNode.getTextContent().trim();
            final VehMakeModel sippObj = new VehMakeModel(externalSIPP.substring(0, 1), externalSIPP.substring(1, 2), externalSIPP.substring(2, 3),
                    externalSIPP.substring(3), true);

            car.setCarInventoryKey(GDSMsgReadHelper.readVehMakeModel(car.getCarInventoryKey(), scsDataSource, sippObj));

        }

        //Date
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(response, "VehRentalCore");
        car.setCarInventoryKey(DateLocationReader.readDate(car.getCarInventoryKey(), vehRentalCoreNode));

        //AvailStatusCode, now get from TVAR
        //car.AvailStatusCode = readVehAvailStatus(XmlDocUtil.getChildNode(xmlDoc, "VehRentalCore")[0]);

        //mapping PickupLocation & DropOffLocation & CarPickupLocationKey & CarDropOffLocationKey
        DateLocationReader.readLocationsFromLocationDetails(car, PojoXmlUtil.getNodesByTagName(response, "LocationDetails"),
                scsDataSource,  supplierID);

        //LocationKey in CarInventoryKey from Location
        car.getCarInventoryKey().getCarCatalogKey().setVendorSupplierID(supplierID);
        car.getCarInventoryKey().getCarCatalogKey().setCarPickupLocationKey(car.getCarPickupLocation().getCarLocationKey());
        car.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(car.getCarDropOffLocation().getCarLocationKey());

        //CarMileage
        car.setCarMileage(readCarMileage(PojoXmlUtil.getNodeByTagName(response, "RateDistance"), PojoXmlUtil.getNodesByTagName(response, "Fee"), scsDataSource));

        //Sepcial equipment mapping
        readCarVehicleOptionListFromPricedEquips(car, scsDataSource, PojoXmlUtil.getNodesByTagName(response, "PricedEquip"), isCarBS);

        //Car door count - VehType/@DoorCount
        String carDoorS = PojoXmlUtil.getNodeByTagName(response, "VehType").getAttributes().getNamedItem("DoorCount").getTextContent();
        if (carDoorS.contains("-"))
        {
            carDoorS = carDoorS.split("-")[carDoorS.split("-").length - 1];
        }
        if (carDoorS.contains("/")) {
            carDoorS = carDoorS.split("/")[carDoorS.split("/").length - 1];
        }
        car.setCarDoorCount(Long.parseLong(carDoorS));

        //CarCatalogMakeModel
        car.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(response));

        //TODO mia CarRentalLimits Bug 1077728 by design
        //BuildCarRentalLimits(carProduct, StartEndTimes_Node, RentalPeriodRules_Node);

        //CarRateDetail
        buildConditionalCostPriceList(car, PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());
        ConditionalCostPriceReader.buildConditionalCostPriceListFromFee(car, PojoXmlUtil.getNodesByTagName(response, "Fee"));
        //CASSS-9561 Insurance included for GDSP Prepaid inventory - Supply Changes Titanium
        CarCoveragesCostListReader.readCarInsuranceIncludedFromPricedCoverages(car,
                PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());


        CarPolicyReader.readCarPolicyListFromGDSRsp(car, response);

        //TODO mia ReservationGuaranteeCategory Bug 1078433, value is None for now, by design.
        //readReservationGuaranteeCategory(car);

        readCarCoveragesCostListFromPricedCoverages(car,
                PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());

        //CostList
        readCostListFromXmlDoc(car, response);

        return car;
    }


    public CarProductType getCarProduct() {
        return carProduct;
    }

    public void setCarProduct(CarProductType carProduct) {
        this.carProduct = carProduct;
    }

    public String getPrimaryLangID() {
        return primaryLangID;
    }
    @SuppressWarnings("CPD-END")

    public void setPrimaryLangID(String primaryLangID) {
        this.primaryLangID = primaryLangID;
    }

}
