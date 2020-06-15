package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.traveltypes.defn.v4.CustomerType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.*;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCoveragesCostListReader.readCarCoveragesCostListFromPricedCoverages;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarMileageReader.readCarMileage;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.ConditionalCostPriceReader.buildConditionalCostPriceList;
import static com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.SpecialEquipReader.readCarVehicleOptionListFromPricedEquips;

/**
 * Created by fehu on 11/24/2016.
 */
@SuppressWarnings("CPD-START")
public class TVRSRsp {
    public CarProductType carProduct;
    public String primaryLangID;
    public TravelerListType travelerList;
    public ReferenceListType refereceList;
    public String bookStatusCode;
    public AdvisoryTextListType advisoryTextList;


    public TVRSRsp(Node response, CarsSCSDataSource scsDataSource, boolean isCarBS) throws DataAccessException {

        this.carProduct = readReserveCarProduct(response, scsDataSource, isCarBS);

        //Read primaryLangID
        if (response.getAttributes().getNamedItem("PrimaryLangID") == null) {
            this.primaryLangID = null;
        } else {
            this.primaryLangID = response.getAttributes().getNamedItem("PrimaryLangID").getTextContent();
        }

        //Traveler
        final CustomerReader customerReader = new CustomerReader();
        final CustomerType customer = customerReader.readCustomer(response);
        this.travelerList = customerReader.readTravelerList(customer);

        //Address is not returned in TSCS reserve response
        travelerList.getTraveler().get(0).getContactInformation().setAddressList(null);
        if(travelerList.getTraveler().get(0).getContactInformation().getPhoneList().getPhone().isEmpty()
                && travelerList.getTraveler().get(0).getContactInformation().getEmailAddressEntryList().getEmailAddressEntry().isEmpty())
        {
            travelerList.getTraveler().get(0).setContactInformation(null);
        }

        //ReferenceList
        this.refereceList = ReferenceListReader.readReferenceList(response);

        //bookStatusCode - <VehReservation ReservationStatus="Reserved">
        this.bookStatusCode = PojoXmlUtil.getNodeByTagName(response, "VehReservation").getAttributes().getNamedItem("ReservationStatus").getTextContent();
        if ("Reserved".equals(bookStatusCode)) {
            bookStatusCode = "Booked";
        }

        //TODO: AdvisoryTextList - voucher
    }

    /**
     *
     * @param response
     * @return
     */
    public CarProductType readReserveCarProduct(Node response, CarsSCSDataSource scsDataSource, boolean isCarBS
                                               ) throws DataAccessException {
        final CarProductType car = new CarProductType();
        car.setCarInventoryKey(new CarInventoryKeyType());
        final Long supplierID = GDSMsgReadHelper.readVendorSupplierID(scsDataSource, PojoXmlUtil.getNodeByTagName(response, "Vendor").
                getAttributes().getNamedItem("Code").getTextContent());
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

        //TODO: no AvailStatusCode in TVRS response
        car.setAvailStatusCode("A"); // readVehAvailStatus(XmlDocUtil.getChildNode(xmlDoc, "VehRentalCore")[0]);

        //mapping PickupLocation & DropOffLocation & CarPickupLocationKey & CarDropOffLocationKey
        DateLocationReader.readLocationsFromLocationDetails(car, PojoXmlUtil.getNodesByTagName(response, "LocationDetails"),
                scsDataSource,  supplierID);

        //If dropoffLocatonKey is null, copy from pickup
        if (car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey() == null ||
                (StringUtils.isEmpty(car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode())
                && car.getCarDropOffLocation().getCarLocationKey().getCarVendorLocationID() == 0))
        {
            car.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
        }

        //car rate
        car.getCarInventoryKey().setCarRate(new CarRateType());
        car.getCarInventoryKey().getCarRate().setRatePeriodCode("Trip");

        //CarMileage
        car.setCarMileage(readCarMileage(PojoXmlUtil.getNodeByTagName(response, "RateDistance"), PojoXmlUtil.getNodesByTagName(response, "Fee"), scsDataSource));

        //Sepcial equipment mapping
        readCarVehicleOptionListFromPricedEquips(car, scsDataSource, PojoXmlUtil.getNodesByTagName(response, "PricedEquip"), isCarBS);

        //CarRateDetail
        if(CollectionUtils.isNotEmpty(PojoXmlUtil.getNodesByTagName(response, "PricedCoverages"))) {
            buildConditionalCostPriceList(car, PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());
            readCarCoveragesCostListFromPricedCoverages(car,
                    PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());

            //CASSS-9561 Insurance included for GDSP Prepaid inventory - Supply Changes Titanium
            CarCoveragesCostListReader.readCarInsuranceIncludedFromPricedCoverages(car,
                    PojoXmlUtil.getNodesByTagName(response, "PricedCoverages").get(0).getChildNodes());

        }
        else
        {
            final CarRateDetailType carRateDetailType = car.getCarRateDetail();
            if(CompareUtil.isObjEmpty(carRateDetailType)) {
                car.setCarRateDetail(new CarRateDetailType());
            }
            //CASSS-9561 Insurance included for GDSP Prepaid inventory - Supply Changes Titanium
            car.getCarRateDetail().setCarInsuranceIncludedInRate(false);
        }
        ConditionalCostPriceReader.buildConditionalCostPriceListFromFee(car, PojoXmlUtil.getNodesByTagName(response, "Fee"));

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

        //CostList
        CostListReader.buildCostAvailCostList(car, response, false);

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

    public void setPrimaryLangID(String primaryLangID) {
        this.primaryLangID = primaryLangID;
    }

    public AdvisoryTextListType getAdvisoryTextList() {
        return advisoryTextList;
    }

    public void setAdvisoryTextList(AdvisoryTextListType advisoryTextList) {
        this.advisoryTextList = advisoryTextList;
    }

    public String getBookStatusCode() {
        return bookStatusCode;
    }

    public void setBookStatusCode(String bookStatusCode) {
        this.bookStatusCode = bookStatusCode;
    }

    public TravelerListType getTravelerList() {
        return travelerList;
    }

    public void setTravelerList(TravelerListType travelerList) {
        this.travelerList = travelerList;
    }

    public ReferenceListType getRefereceList() {
        return refereceList;
    }
    @SuppressWarnings("CPD-END")

    public void setRefereceList(ReferenceListType refereceList) {
        this.refereceList = refereceList;
    }
}
