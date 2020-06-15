package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.LatLongType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.testng.Assert;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 1/24/2018.
 */
@SuppressWarnings("PMD")
public class ACLQRsp {
    public List<CarLocationType> carLocationList;

    public ACLQRsp(Node response, LocationSearchTestScenario scenario, CarsSCSDataSource scsDataSource) throws DataAccessException {

        List<Node> carLocationDetailsNodes = null;
        if (!CompareUtil.isObjEmpty(response)) {
            carLocationDetailsNodes = PojoXmlUtil.getNodesByTagName(response, "carLocationListDetails");
        }
        carLocationList = new ArrayList<CarLocationType>();
        final Node countryCodeNodeGlobal = PojoXmlUtil.getNodeByTagName(response, "countryAndState");
        if (!CompareUtil.isObjEmpty(carLocationDetailsNodes)) {
            for (final Node locationDetailsNode : carLocationDetailsNodes) {
                String countryCode = null;
                String stateCode = null;
                if (!CompareUtil.isObjEmpty(countryCodeNodeGlobal)) {
                    countryCode = PojoXmlUtil.getNodeByTagName(countryCodeNodeGlobal, "countryCode").getTextContent();
                    final Node stateNode = PojoXmlUtil.getNodeByTagName(countryCodeNodeGlobal, "state");
                    if (!CompareUtil.isObjEmpty(stateNode)) {
                        stateCode = stateNode.getTextContent();
                    }
                }
                final CarLocationType location = buildCarLocation(locationDetailsNode, countryCode, stateCode, scenario,scsDataSource);
                if (null != location) {
                    carLocationList.add(location);
                }
            }
        }
    }

    public CarLocationType buildCarLocation(Node locationDetailsNode, String countryCodeGlobal, String stateCodeGlobal, LocationSearchTestScenario scenario, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final Node geoCodingNode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "geoCoding");

        //get node info
        String longitude = null;
        String latitude = null;
        final String locationInfomation = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "locationCode").getTextContent();
        final String companyCode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "companyCode").getTextContent();
        if (!CompareUtil.isObjEmpty(geoCodingNode)) {
            longitude = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "porLongitude").getTextContent();
            latitude = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "porLatitude").getTextContent();
        }
        String addressInfo = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "address").getTextContent();
        String numberInfo = null;
        final Node numberNode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "number");
        if (!CompareUtil.isObjEmpty(numberNode)) {
            numberInfo = numberNode.getTextContent();
            addressInfo = numberInfo + " " + addressInfo;
        }

        String cityName = "";
        String postalCode = "";
        String provinceName = "";
        if (!CompareUtil.isObjEmpty(PojoXmlUtil.getNodeByTagName(locationDetailsNode, "cityName"))) {
            cityName = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "cityName").getTextContent();
        }
        if (!CompareUtil.isObjEmpty(PojoXmlUtil.getNodeByTagName(locationDetailsNode, "postalCode"))) {
            postalCode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "postalCode").getTextContent();
        }
        String countryCode = countryCodeGlobal;
        if (CompareUtil.isObjEmpty(countryCodeGlobal)) {
            countryCode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "countryCode").getTextContent();
        }
        if (CompareUtil.isObjEmpty(locationInfomation)) {
            Assert.fail("locationDetailsNode.locationAddress.ationInformation");
        }
        //failed by null value
        if (CompareUtil.isObjEmpty(companyCode)) {
            Assert.fail("locationDetailsNode.companyInfo.companyInformation.companyCode");
        }

        final Node companyAccessType = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "companyAccessType");
        final List<Node> otherOptions = PojoXmlUtil.getNodesByTagName(companyAccessType, "otherSelectionDetails");
        provinceName = stateCodeGlobal;
        if (CompareUtil.isObjEmpty(stateCodeGlobal)) {
            final Node stateNode = PojoXmlUtil.getNodeByTagName(locationDetailsNode, "state");
            if (!CompareUtil.isObjEmpty(stateNode)) {
                provinceName = PojoXmlUtil.getNodeByTagName(stateNode, "stateCode").getTextContent();
            }
        }
        final CarLocationType location = new CarLocationType();
        location.setProviderLocationCode(locationInfomation);
        location.setProviderLocationType(locationInfomation.substring(3, 4));
        location.setProviderLocationIataCode(locationInfomation.substring(0, 3));
        location.setProviderLocationSupplierCode(companyCode);

        //CarLocationKey
        final CarLocationKeyType locationKey = new CarLocationKeyType();
        locationKey.setLocationCode(locationInfomation.substring(0, 3));
        locationKey.setCarLocationCategoryCode(locationInfomation.substring(3, 4));
        locationKey.setSupplierRawText("0" + locationInfomation.substring(4));

        for (final Node otherOption : otherOptions) {
            final String optionString = PojoXmlUtil.getNodeByTagName(otherOption, "option").getTextContent();
            switch (optionString) {
                case "DEL":
                    locationKey.setDeliveryBoolean(true);
                    break;
                case "COL":
                    locationKey.setCollectionBoolean(true);
                    break;
                case "OHR":
                    locationKey.setOutOfOfficeHourBoolean(true);
                    break;
                case "SHT":
                    //mapping shuttle
                    final String shuttleInfo = PojoXmlUtil.getNodeByTagName(otherOption, "optionInformation").getTextContent();
                    location.setCarShuttleCategoryCode(shuttleMappint(shuttleInfo));
                    location.setProviderLocationShuttleCategoryCode(shuttleInfo);
                    break;
            }

        }

        //Filter out the location which is not which has the incorrect DeliveryBoolean/CollectionBoolean/OutOfOfficeHourBoolean with request: just need to filter when the values are true from Amadeus response
        boolean shouldFilteredOutFromResponse = false;
        if (!CompareUtil.isObjEmpty(scenario)) {
            if (locationKey.getDeliveryBoolean() && !(CompareUtil.isObjEmpty(scenario.getDeliveryBoolean()) || Boolean.parseBoolean(scenario.getDeliveryBoolean()))) {
                shouldFilteredOutFromResponse = true;
            }
            if (locationKey.getCollectionBoolean() && !(CompareUtil.isObjEmpty(scenario.getCollectionBoolean()) || Boolean.parseBoolean(scenario.getCollectionBoolean()))) {
                shouldFilteredOutFromResponse = true;
            }
            if (locationKey.getOutOfOfficeHourBoolean() && !(CompareUtil.isObjEmpty(scenario.getOutOfOfficeHourBoolean()) || Boolean.parseBoolean(scenario.getOutOfOfficeHourBoolean()))) {
                shouldFilteredOutFromResponse = true;
            }
        }
        if (shouldFilteredOutFromResponse) {
            return null;
        }

        // suppluerID
        final String supplierID = String.valueOf(GDSMsgReadHelper.readVendorSupplierID(scsDataSource, companyCode));

        if (CompareUtil.isObjEmpty(supplierID))
            //Assert.Fail("There is an unkown car vendor for Amadeus DB : "+companyCode);
            return null;

        //Try get CarVendorLocationID
        final List<ExternalSupplyServiceDomainValueMap> locationMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(
                Long.parseLong(supplierID), 0L, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, null, locationInfomation);
        final Long carVendorLocationID = locationMapList.isEmpty() ? 0L: Long.parseLong(locationMapList.get(0).getDomainValue());

        //if CarVendorLocationID exist, return CarVendorLocationID in location key
        if (carVendorLocationID > 0)
        {
            locationKey.setCarVendorLocationID(carVendorLocationID);
        }

        //LatLong
        if (!CompareUtil.isObjEmpty(geoCodingNode)) {
            final LatLongType latLong = new LatLongType();
            final AmountType latitudeAmount = new AmountType();
            final AmountType longitudeAmount = new AmountType();
            latLong.setLatitudeAmount(latitudeAmount);
            latLong.setLongitudeAmount(longitudeAmount);
            latitudeAmount.setDecimal(Integer.parseInt(latitude));
            latitudeAmount.setDecimalPlaceCount(5);
            longitudeAmount.setDecimal(Integer.parseInt(longitude));
            longitudeAmount.setDecimalPlaceCount(5);
            location.setLatLong(latLong);
        }

        // address
        final AddressType address = new AddressType();
        if (addressInfo.contains(",")) {
            address.setCompanyNameAddressLine(addressInfo.split(",")[0]);
            address.setFirstAddressLine(addressInfo.split(",")[1]);
        } else if (addressInfo.contains("-")) {
            address.setCompanyNameAddressLine(addressInfo.split("-")[0]);
            address.setFirstAddressLine(addressInfo.split("-")[1]);
        } else {
            address.setFirstAddressLine(addressInfo);
        }
        address.setCityName(cityName);
        address.setPostalCode(postalCode);
        address.setCountryAlpha3Code(GDSMsgReadHelper.getCountryAlpha3CodeFromCountryCode(countryCode));
        address.setProvinceName(provinceName);

        location.setSupplierID(Long.parseLong(supplierID));
        location.setCarLocationKey(locationKey);
        location.setAddress(address);

        return location;
    }

    public String shuttleMappint(String shuttleInfo) {
        String shuttledString = "";
        switch (shuttleInfo) {
            case "IN":
                shuttledString = "NoShuttle";
                break;
            case "AT":
            case "MS":
            case "NA":
            case "OF":
            case "RC":
                shuttledString = "ShuttleToCounter";
                break;
            case "SV":
                shuttledString = "ShuttleToCar";
                break;
        }
        return shuttledString;
    }

    public List<CarLocationType> getCarLocationList() {
        return carLocationList;
    }
}
