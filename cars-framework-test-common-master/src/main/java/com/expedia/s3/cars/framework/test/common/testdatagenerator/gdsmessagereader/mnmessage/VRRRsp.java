package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage;

import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.Country;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.commonreader.VehAvailNodeHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader.CarCatalogMakeModelReader;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 12/16/2016.
 */
public class VRRRsp {

    final private CarsSCSDataSource scsDataSource;
    final private CarProductType carProduct;

    public CarProductType getCarProduct() {
        return carProduct;
    }

    public VRRRsp(Node node, CarsSCSDataSource scsDataSource) throws Exception {
        this.scsDataSource = scsDataSource;
        this.carProduct = readGetDetailCarProducts(node, scsDataSource);

    }

    private CarProductType readGetDetailCarProducts(Node response, CarsSCSDataSource scsDataSource) throws Exception {
        final CarProductType carProductType = new CarProductType();
        //CarInventoryKey
        carInventoryKey(response, scsDataSource, carProductType);

        carProductType.setAvailStatusCode("A");

         //CarPickupLocation/CarDropOffLocation
        carLocation(carProductType, response);

        //CarMileage
        carMileage(response, carProductType);

       //Car Rate
       VehAvailNodeHelper.carRateDetail(response, carProductType, scsDataSource);

        carProductType.setProviderID(3l);

        //CarCatalogMakeModel
        carProductType.setCarCatalogMakeModel(CarCatalogMakeModelReader.readCarCatalogMakeModel(response));
        //Based on CASSS-10368 Micronnexus : Show Number of Doors from GDS for both Min and Max, if min door count not exist, get min door count from max door count
        if(carProductType.getCarCatalogMakeModel().getCarMinDoorCount() == 0)
        {
            carProductType.getCarCatalogMakeModel().setCarMinDoorCount(carProductType.getCarCatalogMakeModel().getCarMaxDoorCount());
        }

        //costList
        VehAvailNodeHelper.detailsCostList(response, carProductType, scsDataSource);

        //carPolicy
        carPolicy(response,carProductType);

        //priceEquip
        VehAvailNodeHelper.carVehicleOption(response, scsDataSource, carProductType);


        return  carProductType;

    }


    private void carPolicy(Node response, CarProductType carProduct) {

        final CarPolicyListType carPolicyListType =  new CarPolicyListType();
        carProduct.setCarPolicyList(carPolicyListType);
        final  List<CarPolicyType> carPolicyTypes = new ArrayList<>();
        carPolicyListType.setCarPolicy(carPolicyTypes);

        //payment Rule
        final String paymentRule = PojoXmlUtil.getNodeByTagName(response, "PaymentRule").getTextContent();
        final CarPolicyType carPolicyPayment = new CarPolicyType();
        carPolicyPayment.setCarPolicyCategoryCode("AcceptedFormsOfPayment");
        carPolicyPayment.setCarPolicyRawText(paymentRule);
        carPolicyTypes.add(carPolicyPayment);

        //Arrival Info
        final CarPolicyType carPolicyArrival = new CarPolicyType();
        final String arrivalInfo = PojoXmlUtil.getNodeByTagName(response, "ParkLocation").getAttributes().getNamedItem("Location").getTextContent();
        carPolicyArrival.setCarPolicyCategoryCode("Arrival");
        carPolicyArrival.setCarPolicyRawText(arrivalInfo);
        carPolicyTypes.add(carPolicyArrival);
    }


    private void carMileage(Node response, CarProductType carProductType) {
        final CarMileageType carMileageType = new CarMileageType();

        final DistanceType freeDistanceType = new DistanceType();
        carMileageType.setFreeDistance(freeDistanceType);

        carProductType.setCarMileage(carMileageType);

        if ("true".equalsIgnoreCase(PojoXmlUtil.getNodeByTagName(response, "RateDistance").
                getAttributes().getNamedItem("Unlimited").getTextContent())) {
            freeDistanceType.setDistanceUnitCount(-1);
        } else {   //set freeDistanceUnit
            freeDistanceType.setDistanceUnit(VehAvailNodeHelper.getDistanceUnit(response));
            freeDistanceType.setDistanceUnitCount(Integer.parseInt(PojoXmlUtil.getNodeByTagName(response, "RateDistance").
                    getAttributes().getNamedItem("Quantity").getTextContent()));
        }
    }


    private void carLocation(CarProductType carProductType, Node node) throws DataAccessException {

        final List<Node> locationDetailsNodes = PojoXmlUtil.getNodesByTagName(node, "LocationDetails");
        final CarLocationType carPickupLocation = new CarLocationType();
        carProductType.setCarPickupLocation(carPickupLocation);
        carPickupLocation.setCarLocationKey(carProductType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
        carPickupLocation.setCarVendorLocationID(carProductType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID());
        carPickupLocation.setAddress(generateAddress(0, locationDetailsNodes));
        carPickupLocation.setPhoneList( generatePhone(0, locationDetailsNodes));


        final CarLocationType carDropoffLocation = new CarLocationType();
        carProductType.setCarDropOffLocation(carDropoffLocation);
        carDropoffLocation.setCarLocationKey(carProductType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey());
        carDropoffLocation.setCarVendorLocationID(carProductType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID());
        carDropoffLocation.setAddress(generateAddress(1, locationDetailsNodes));
        carPickupLocation.setPhoneList( generatePhone(1, locationDetailsNodes));

    }

    private PhoneListType generatePhone(int num, List<Node> locationDetailsNodes) {

        final PhoneListType phoneListType = new PhoneListType();
        final List<PhoneType> phoneTypes = new ArrayList<>();
        phoneListType.setPhone(phoneTypes);

        final List<Node> telephoneNodes = PojoXmlUtil.getNodesByTagName(locationDetailsNodes.get(num), "Telephone");
        for(final Node telephoneNode : telephoneNodes)
        {
            final PhoneType phoneType = new PhoneType();
            final String telephonestring = telephoneNode.getAttributes().getNamedItem("PhoneNumber").getTextContent().replace("(", "").replace(")", "").replace(" ","");
            phoneType.setPhoneNumber(telephonestring);
            phoneTypes.add(phoneType);
        }
        return phoneListType;
    }

    private AddressType generateAddress(int num, List<Node> locationDetailsNodes) throws DataAccessException {
        final AddressType addressType = new AddressType();
        addressType.setAddressCategoryCode("2");
        addressType.setFirstAddressLine(PojoXmlUtil.getNodeByTagName(locationDetailsNodes.get(num), "StreetNmbr").getTextContent());
        addressType.setSecondAddressLine(PojoXmlUtil.getNodeByTagName(locationDetailsNodes.get(num), "AddressLine").getTextContent());
        addressType.setThirdAddressLine(PojoXmlUtil.getNodeByTagName(locationDetailsNodes.get(num), "CityName").getTextContent());
        addressType.setPostalCode(PojoXmlUtil.getNodeByTagName(locationDetailsNodes.get(num), "PostalCode").getTextContent());

        final List<Country>  countryTypes = scsDataSource.getCountryCodeFromCountryShortCode(PojoXmlUtil.getNodeByTagName(locationDetailsNodes.get(num), "CountryName").getAttributes().getNamedItem("Code").getTextContent());
       if (CollectionUtils.isNotEmpty(countryTypes))
       {
           addressType.setCountryAlpha3Code(countryTypes.get(0).getpCountryCode());

       }
       return addressType;
    }

    private void carInventoryKey(Node response, CarsSCSDataSource scsDataSource, CarProductType carProductType) throws Exception {
        final CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
        carProductType.setCarInventoryKey(carInventoryKey);

        //set PickupTime and dropUptime
        final Node vehRentalCoreNode = PojoXmlUtil.getNodeByTagName(response, "VehRentalCore");
       // readDate(carInventoryKey, vehRentalCoreNode);

        final CarCatalogKeyType carCatalogKeyType = new CarCatalogKeyType();
        carInventoryKey.setCarCatalogKey(carCatalogKeyType);

        //carCatalogKey
        final String pickupLocationCode = PojoXmlUtil.getNodesByTagName(response, "LocationDetails").get(0).
                getAttributes().getNamedItem("Code").getTextContent();

        final String vendorCode = vehRentalCoreNode.getAttributes().getNamedItem("Code").getTextContent();

      //setSupplierId
        carCatalogKeyType.setVendorSupplierID(Long.parseLong(TestDataUtil.getSupplierIDByVendorCode(vendorCode)));

        //get dropoffLoctionCode
        final String dropoffLocationCode = PojoXmlUtil.getNodesByTagName(response, "LocationDetails").get(1).
                getAttributes().getNamedItem("Code").getTextContent();

        final CarLocationKeyType carPickupLocationKey = new CarLocationKeyType();
        final CarLocationKeyType carDropoffLocationKey = new CarLocationKeyType();
        carCatalogKeyType.setCarDropOffLocationKey(carDropoffLocationKey);
        carCatalogKeyType.setCarPickupLocationKey(carPickupLocationKey);

        VehAvailNodeHelper.setLocationCodes(carPickupLocationKey, pickupLocationCode);
        VehAvailNodeHelper.setLocationCodes(carDropoffLocationKey, dropoffLocationCode);

        final String carModel = PojoXmlUtil.getNodeByTagName(response, "VehMakeModel").
                getAttributes().getNamedItem("Code").getTextContent();
        final VehMakeModel vehMakeModel = new VehMakeModel(carModel.substring(0, 1), carModel.substring(1, 2), carModel.substring(2, 3), carModel.substring(3, 4), true);
        GDSMsgReadHelper.readVehMakeModel(carInventoryKey, scsDataSource, vehMakeModel);

        //set CarRate
        final CarRateType carRateType = new CarRateType();
        carInventoryKey.setCarRate(carRateType);
        carRateType.setRatePeriodCode("Trip");
    }



}

