package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressListType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.timetypes.defn.v4.*;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miawang on 12/5/2016.
 */
@SuppressWarnings("PMD")
public class ACAQRsp {
    private final List<CarProductType> gdsCarProductList;

    // Get info from companyLocationInfo node
    private final Map MAPOPENTIMETOVENDORANDLOCATION = new HashMap();
    private final Map MAPADDRESSTOVENDORANDLOCATION = new HashMap();
    private final Map MAPSHUTTLEINFOTOVENDORANDLOCATION = new HashMap();

    public final String GUARANTEE_REQUIRED_AT_BOOKING = "FGR";
    public final String CREDIT_CARD_GUARANTEE = "907";

    public List<CarProductType> getGdsCarProductList() {
        return gdsCarProductList;
    }

    public ACAQRsp(Node response, CarSearchCriteriaListType searchCriteriaList, CarsSCSDataSource scsDataSource) throws DataAccessException
    {
        buildCompanyData(response);
        final List<Node> ratesNodeList = PojoXmlUtil.getNodesByTagName(response, "rates");
        gdsCarProductList = new ArrayList<>();

        for (final Node rateTmp : ratesNodeList)
        {
            final CarProductType car = new CarProductType();
            new ReadCarProductSearch(car, rateTmp, scsDataSource);

            if (null != searchCriteriaList && !isCarNeedFilter(searchCriteriaList, car))
            {
                gdsCarProductList.add(car);
            } else if (null == searchCriteriaList)
            {
                gdsCarProductList.add(car);
            }
        }
    }

    private boolean isCarNeedFilter(CarSearchCriteriaListType searchCriteriaList, CarProductType car)
    {
        for (CarSearchCriteriaType searchCriteria : searchCriteriaList.getCarSearchCriteria())
        {
            for (Long supplierID : searchCriteria.getVendorSupplierIDList().getVendorSupplierID())
            {
                if (supplierID.longValue() == car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())
                {
                    if (StringUtils.isNotEmpty(searchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarLocationCategoryCode())
                            || StringUtils.isNotEmpty(searchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarLocationCategoryCode()))
                    {
                        if (CarNodeComparator.isCarLocationKeyEqual(car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(),
                                searchCriteria.getCarTransportationSegment().getStartCarLocationKey()) ||
                                CarNodeComparator.isCarLocationKeyEqual(car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey(),
                                        searchCriteria.getCarTransportationSegment().getEndCarLocationKey()))
                        {
                            return false;
                        }
                    } else if (car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getLocationCode()
                            .equals(searchCriteria.getCarTransportationSegment().getStartCarLocationKey().getLocationCode())
                            ||
                            car.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().getLocationCode()
                                    .equals(searchCriteria.getCarTransportationSegment().getEndCarLocationKey().getLocationCode()))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void buildCompanyData(Node varResponse) throws DataAccessException {
        final List<Node> companyList = PojoXmlUtil.getNodesByTagName(varResponse, "companyLocationInfo");
        for (final Node company : companyList) {
            final Node carCompanyData = PojoXmlUtil.getNodeByTagName(company, "carCompanyData");
            final Node vendorCodeNode = PojoXmlUtil.getNodeByTagName(carCompanyData, "companyCode");
            final String vendorCode = vendorCodeNode.getTextContent();
            final List<Node> rentalLocationNodeList = PojoXmlUtil.getNodesByTagName(company, "rentalLocation");

            final AddressListType addressList = new AddressListType();
            TimeRangeType timeRange;
            AddressType address;

            for (int index = 0; index < rentalLocationNodeList.size(); index++) {
                final TimeRangeListType timeRangeList = new TimeRangeListType();
                address = new AddressType();
                final Node addressNode = PojoXmlUtil.getNodeByTagName(rentalLocationNodeList.get(index), "address");
                final Node locationDetails = PojoXmlUtil.getNodeByTagName(addressNode, "locationDetails");
                final String locationName = PojoXmlUtil.getNodeByTagName(locationDetails, "name").getTextContent();
                address.setFirstAddressLine(PojoXmlUtil.getNodeByTagName(addressNode, "line1").getTextContent());
                address.setCityName(PojoXmlUtil.getNodeByTagName(addressNode, "city").getTextContent());
                address.setCountryAlpha3Code(GDSMsgReadHelper.getCountryAlpha3CodeFromCountryCode
                        (PojoXmlUtil.getNodeByTagName(addressNode, "countryCode").getTextContent()));
                if (null == addressList.getAddress()) {
                    addressList.setAddress(new ArrayList<>());
                }
                addressList.getAddress().add(address);
                if (!MAPADDRESSTOVENDORANDLOCATION.containsKey(vendorCode + locationName)) {
                    MAPADDRESSTOVENDORANDLOCATION.put(vendorCode + locationName, address);
                }

                final List<Node> shuttleInfoNodeList = PojoXmlUtil.getNodesByTagName(rentalLocationNodeList.get(index), "shuttleInfo");

                if(null != shuttleInfoNodeList & shuttleInfoNodeList.size()>0)
                {
                    final Node shuttleAttributeDetailsNode = PojoXmlUtil.getNodeByTagName(shuttleInfoNodeList.get(0), "attributeDetails");
                    if (!MAPSHUTTLEINFOTOVENDORANDLOCATION.containsKey(vendorCode + locationName) & null != shuttleAttributeDetailsNode )
                    {
                        if("NON".equalsIgnoreCase(shuttleAttributeDetailsNode.getTextContent()))
                        {
                            MAPSHUTTLEINFOTOVENDORANDLOCATION.put(vendorCode + locationName, "NoShuttle");
                        }

                    }
                }

                /// openingHoursNode list
                final List<Node> openingHoursNodeList = PojoXmlUtil.getNodesByTagName(rentalLocationNodeList.get(index), "openingHours");
                for (final Node openingHours : openingHoursNodeList) {
                    final Node openingHoursNode = openingHours;
                    final List<Node> beginDateTimeNodeList = PojoXmlUtil.getNodesByTagName(openingHoursNode, "beginDateTime");
                    final List<Node> endDateTimeNodeList = PojoXmlUtil.getNodesByTagName(openingHoursNode, "endDateTime");
                    DateTime beginTime;
                    DateTime endTime;
                    for (int count = 0; count < beginDateTimeNodeList.size(); count++) {
                        timeRange = new TimeRangeType();

                        beginTime = DateTime.getInstanceByTime(Integer.parseInt(PojoXmlUtil.getNodeByTagName(beginDateTimeNodeList.get(count), "hour").getTextContent()),
                                Integer.parseInt(PojoXmlUtil.getNodeByTagName(beginDateTimeNodeList.get(count), "minutes").getTextContent()), 0, 0);
                        endTime = DateTime.getInstanceByTime(Integer.parseInt(PojoXmlUtil.getNodeByTagName(endDateTimeNodeList.get(count), "hour").getTextContent()),
                                Integer.parseInt(PojoXmlUtil.getNodeByTagName(endDateTimeNodeList.get(count), "minutes").getTextContent()), 0, 0);

                        timeRange.setMinTime(beginTime);
                        timeRange.setMaxTime(endTime);
                        if (null == timeRangeList.getTimeRange()) {
                            timeRangeList.setTimeRange(new ArrayList<>());
                        }
                        timeRangeList.getTimeRange().add(timeRange);
                    }
                }

                if (MAPOPENTIMETOVENDORANDLOCATION.containsKey(vendorCode + locationName)) {
                    final TimeRangeListType existTimeRangeList = (TimeRangeListType) MAPOPENTIMETOVENDORANDLOCATION.get(vendorCode + locationName);
                    timeRangeList.getTimeRange().addAll(existTimeRangeList.getTimeRange());
                    MAPOPENTIMETOVENDORANDLOCATION.remove(vendorCode + locationName);
                    MAPOPENTIMETOVENDORANDLOCATION.put(vendorCode + locationName, timeRangeList);
                } else {
                    MAPOPENTIMETOVENDORANDLOCATION.put(vendorCode + locationName, timeRangeList);
                }
            }
        }
    }

    //Get RateCode from ACAQ response for GetCost&Avail message.
    private String getRateCode(Node response) {
        String rateCode = null;
        ///asm:AmadeusSessionManagerResponse/asm:RawAmadeusXml/Car_AvailabilityReply/availabilityDetails/rates/loyaltyNumbersList
        final List<Node> loyaltyNumbersList = PojoXmlUtil.getNodesByTagName(response, "loyaltyNumbersList");
        ///availabilityDetails/rates/loyaltyNumbersList[2]/discountNumbers/customerReferenceInfo/referenceQualifier
        if (!loyaltyNumbersList.isEmpty()) {
            rateCode = getRateCodeLoop(loyaltyNumbersList);
        }

        return rateCode;
    }

    //for PMD check
    private String getRateCodeLoop(List<Node> loyaltyNumbersList) {
        String rateCode = null;
        for (final Node loyaltyNum : loyaltyNumbersList) {
            final Node referenceQualifier = PojoXmlUtil.getNodeByTagName(loyaltyNum, "referenceQualifier");
            if (referenceQualifier != null && referenceQualifier.getTextContent().trim().equals("RC")) {
                final Node referenceNumber = PojoXmlUtil.getNodeByTagName(loyaltyNum, "referenceNumber");
                if (referenceNumber != null) {
                    rateCode = referenceNumber.getTextContent().trim();
                }
            }
        }
        return rateCode;
    }


    private class ReadCarProductSearch {
        /**
         * @param car
         * @param rate
         * @param scsDataSource
         * @throws DataAccessException
         */
        public ReadCarProductSearch(CarProductType car, Node rate, CarsSCSDataSource scsDataSource) throws DataAccessException {
            //1.CarInventoryKey
            car.setCarInventoryKey(buildCarProductCarInventoryKey(rate, scsDataSource));

            //2.AvailStatusCode
            car.setAvailStatusCode(readAvailStatusCode(rate, scsDataSource));

            //3.CarCatalogMakeModel
            final CarCatalogMakeModelType makeModel = buildCarCatalogMakeModel(rate);
            if (null != makeModel) {
                car.setCarCatalogMakeModel(makeModel);
                //4.CarDoorCount
                car.setCarDoorCount(makeModel.getCarMaxDoorCount());
            }

            //5.CarPickupLocation 6.CarDropOffLocation
            buildPickupOrDroppOffLocation(car, rate, MAPOPENTIMETOVENDORANDLOCATION, MAPADDRESSTOVENDORANDLOCATION, MAPSHUTTLEINFOTOVENDORANDLOCATION);

            //7.CostList
            buildCarCostList(car, rate);

            // 8.ReservationGuaranteeCategory
            final Node ruleInfo = PojoXmlUtil.getNodeByTagName(rate, "ruleInfo");
            if (ruleInfo != null) {
                final String ruleType = PojoXmlUtil.getNodeByTagName(ruleInfo, "type").getTextContent();
                if (null != ruleType && (ruleType.equals(CREDIT_CARD_GUARANTEE) || ruleType.equals(GUARANTEE_REQUIRED_AT_BOOKING))) {
                    car.setReservationGuaranteeCategory("Required");
                }
            }

            // 9 Build CarMileage
            car.setCarMileage(buildCarmileage(rate));

        }

        /**
         * Build CarInvenstoryKey from GDS Rates node
         *
         * @param ratesNode
         * @return
         */
        public CarInventoryKeyType buildCarProductCarInventoryKey(Node ratesNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
            final CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
            if (null == carInventoryKey.getCarCatalogKey()) {
                carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
            }

            // 1.Get VendorSupplierID
            final Node carCompanyDataNode = PojoXmlUtil.getNodeByTagName(ratesNode, "carCompanyData");
            carInventoryKey.getCarCatalogKey().setVendorSupplierID(
                    GDSMsgReadHelper.readVendorSupplierID(scsDataSource, PojoXmlUtil.getNodeByTagName(carCompanyDataNode, "companyCode").getTextContent()));

            // 2.Get CarVehicle
            AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            commonNodeReader.readCarVehicle(carInventoryKey, ratesNode, scsDataSource, true);

            // 3.Get CarPickupLocationKey // 4.Get CarDropOffLocationKey
            commonNodeReader.readCarPickupAndDropOffLocationKey(carInventoryKey, ratesNode, "pickupDropoffLocations");

            // 5.Get CarPickUpDateTime (For example: 2012-11-24T12:00:00)
            // 6.Get CarDropOffDateTime
            Node pickupDropoffTimeNode = PojoXmlUtil.getNodeByTagName(ratesNode, "pickupDropoffTime");
            commonNodeReader.readCarPickUpAndDropOffDateTime(carInventoryKey, pickupDropoffTimeNode);

            // 7.Get RatePeriodCode
            // 8.CarRateQualifierCode
            readRatePeriodCodeAndCarRateQualifierCode(carInventoryKey, ratesNode, scsDataSource);

            // 9. Get RateCode  and CorporateDiscountCode
            commonNodeReader.readRateCodeAndCorporateDiscountCode(carInventoryKey, ratesNode, "loyaltyNumbersList");

            // 10 RateCategoryCode
            commonNodeReader.readRateCategoryCode(carInventoryKey, ratesNode, "rateDetailsInfo", "rateCategory");

            // 11.Fix value, PackageBoolean
            //carInventoryKey.setPackageBoolean(false);

            return carInventoryKey;
        }

        private void readRatePeriodCodeAndCarRateQualifierCode(CarInventoryKeyType carInventoryKey, Node ratesNode, CarsSCSDataSource scsDataSource)
                throws DataAccessException {
            // 7.Get RatePeriodCode
            final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(ratesNode, "tariffInfo");
            if (null != tariffInfoNode) {
                readRatePeriodCodeAndCarRateQualifierCodeFromNodeList(carInventoryKey, tariffInfoNode, scsDataSource);
            }
        }

        private void readRatePeriodCodeAndCarRateQualifierCodeFromNodeList(CarInventoryKeyType carInventoryKey,
                                                                           Node tariffInfoNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
            String carRateQualifierCode = "";
            if (null != tariffInfoNode) {
                if (null == carInventoryKey.getCarRate()) {
                    carInventoryKey.setCarRate(new CarRateType());
                }
                carInventoryKey.getCarRate().setRatePeriodCode(GDSMsgReadHelper.getDomainValueByDomainTypeAndExternalDomainValue
                        (CommonConstantManager.DomainType.RATE_PERIOD, PojoXmlUtil.getNodeByTagName(tariffInfoNode,
                                "rateType").getTextContent(), scsDataSource));

                // 8.CarRateQualifierCode //
                final Node rateCodeNode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "rateIdentifier");

                if (null != rateCodeNode) {
                    carRateQualifierCode = rateCodeNode.getTextContent();
                }
            }

            if (null == carInventoryKey.getCarRate()) {
                carInventoryKey.setCarRate(new CarRateType());
            }
            carInventoryKey.getCarRate().setCarRateQualifierCode(carRateQualifierCode);
        }

        /**
         * Build CarCatalogMakeModel based on Rates node from ACAQ response
         *
         * @param ratesNode
         * @return
         */
        public CarCatalogMakeModelType buildCarCatalogMakeModel(Node ratesNode) {
            final List<Node> sizedPicturesNodeList = PojoXmlUtil.getNodesByTagName(ratesNode, "sizedPictures");

            String carMakeString = "";

            final CarCatalogMakeModelType carMakeModel = new CarCatalogMakeModelType();
            carMakeModel.setCarMinDoorCount(0);
            carMakeModel.setCarMaxDoorCount(0);
            carMakeModel.setCarCapacityAdultCount(0);
            carMakeModel.setCarCapacityChildCount(0);
            carMakeModel.setCarCapacityLargeLuggageCount(0);
            carMakeModel.setCarCapacitySmallLuggageCount(0);
            carMakeModel.setCarFeatureString("");
            carMakeModel.setMediaID(0);
            carMakeModel.setImageFilenameString("");
            carMakeModel.setImageThumbnailFilenameString("");

            if (sizedPicturesNodeList.isEmpty()) {
                final List<Node> carMakeStringNodeList = PojoXmlUtil.getNodesByTagName(ratesNode, "carModel");
                if (carMakeStringNodeList.isEmpty()) {
                    carMakeString = carMakeStringNodeList.get(0).getTextContent();
                }
            } else {
                final Node vehicleInformationNode = PojoXmlUtil.getNodeByTagName(sizedPicturesNodeList.get(0), "vehicleInformation");
                // 1.carDoorCount and carCapacityAdultCount
                AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                commonNodeReader.readCarDoorCount(carMakeModel, vehicleInformationNode);

                // 2.carCapacityLargeLuggageCount and carFeatureString
                readCarCapacityLargeLuggageCountAndCarFeatureString(carMakeModel, vehicleInformationNode);

                //3. carMakeString
                final List<Node> carMakeStringNodeList = PojoXmlUtil.getNodesByTagName(vehicleInformationNode, "carModel");
                if (!carMakeStringNodeList.isEmpty()) {
                    carMakeString = carMakeStringNodeList.get(0).getTextContent();
                }
                //Get ImageFilenameString and ImageThumbnailFilenameString according to picture size - 7 is ImageFilenameString, 4 is ImageThumbnailFilenameString
                readImageFilenameString(carMakeModel, sizedPicturesNodeList);
            }

            carMakeModel.setCarMakeString(carMakeString);
            return carMakeModel;
        }

        private void readCarCapacityLargeLuggageCountAndCarFeatureString(CarCatalogMakeModelType carMakeModel, Node vehicleInformationNode) {
            final List<Node> nonNumericalAttributesNodeList = PojoXmlUtil.getNodesByTagName(vehicleInformationNode, "nonNumericalAttributes");
            for (int count = 0; count < nonNumericalAttributesNodeList.size(); count++) {
                final List<Node> attributeTypeNodeList = PojoXmlUtil.getNodesByTagName(nonNumericalAttributesNodeList.get(count), "attributeType");
                if (attributeTypeNodeList.get(count).getTextContent().equals("BS")) {
                    carMakeModel.setCarCapacityLargeLuggageCount(Integer.parseInt(PojoXmlUtil.getNodeByTagName(
                            nonNumericalAttributesNodeList.get(count), "attributeDescription").getTextContent()));
                }

                if (attributeTypeNodeList.get(count).getTextContent().equals("CMK")) {
                    carMakeModel.setCarFeatureString(PojoXmlUtil.getNodeByTagName(nonNumericalAttributesNodeList.get(count),
                            "attributeDescription").getTextContent());
                }
            }

        }

        private void readImageFilenameString(CarCatalogMakeModelType carMakeModel, List<Node> sizedPicturesNodeList) {
            for (final Node picNode : sizedPicturesNodeList) {
                //Get picture size
                final Node picSizeNode = PojoXmlUtil.getNodeByTagName(picNode, "pictureSize");
                final String picSize = PojoXmlUtil.getNodeByTagName(picSizeNode, "code").getTextContent();
                final Node imageURl = PojoXmlUtil.getNodeByTagName(picNode, "imageURL");
                if (picSize != null && picSize.equals("7")) {
                    carMakeModel.setImageFilenameString(PojoXmlUtil.getNodeByTagName(imageURl, "identifier").getTextContent());
                } else if (picSize != null && picSize.equals("4")) {
                    carMakeModel.setImageThumbnailFilenameString(PojoXmlUtil.getNodeByTagName(imageURl, "identifier").getTextContent());
                }
            }
        }

        private String readAvailStatusCode(Node rateNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
            String statusCode = "";
            ///AmadeusSessionManagerResponse/RawAmadeusXml/Car_SellReply/carSegment/rateStatus/statusCode
            final String statusCodeNode = PojoXmlUtil.getNodeByTagName(rateNode, "statusCode").getTextContent();
            if (!StringUtils.isEmpty(statusCodeNode.trim())) {
                final List<ExternalSupplyServiceDomainValueMap> statusCodeList = scsDataSource.getExternalSupplyServiceDomainValueMap
                        (CommonConstantManager.DomainType.AVAIL_STATUS, statusCodeNode);
                if (!statusCodeList.isEmpty()) {
                    statusCode = statusCodeList.get(0).getDomainValue();
                }
            }

            return statusCode;
        }

        public void buildPickupOrDroppOffLocation(CarProductType carProduct, Node rateNode, Map mapOpenTimeToLocation, Map mapAddressToLocation, Map mapShuttleToLocation) {
            //5.CarPickupLocation
            readCarLocation(rateNode, carProduct, false, mapOpenTimeToLocation, mapAddressToLocation, mapShuttleToLocation);

            //6.CarDropOffLocation
            readCarLocation(rateNode, carProduct, true, mapOpenTimeToLocation, mapAddressToLocation, mapShuttleToLocation);

        }

        private void readCarLocation(Node rateNode, CarProductType car, boolean isDropOffLocation,
                                     Map mapOpenTimeToLocation, Map mapAddressToLocation, Map mapShuttleToLocation) {
            final List<Node> pickupDropoffLocationsNodeList = PojoXmlUtil.getNodesByTagName(rateNode, "pickupDropoffLocations");
            final Node carCompanyData = PojoXmlUtil.getNodeByTagName(rateNode, "carCompanyData");
            final String vendorCode = PojoXmlUtil.getNodeByTagName(carCompanyData, "companyCode").getTextContent();

            Node locationNameNode = PojoXmlUtil.getNodeByTagName(pickupDropoffLocationsNodeList.get(0), "name");
            if (isDropOffLocation) {
                locationNameNode = PojoXmlUtil.getNodeByTagName(pickupDropoffLocationsNodeList.get(1), "name");
            }

            AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            commonNodeReader.buildCarPickupAndDropOffLocationKey(car, isDropOffLocation);
            CarLocationType carLocation = car.getCarPickupLocation();
            if (isDropOffLocation) {
                carLocation = car.getCarDropOffLocation();
            }

            if (null != mapShuttleToLocation)
            {
                carLocation.setCarShuttleCategoryCode(
                        (String) mapShuttleToLocation.get(vendorCode + locationNameNode.getTextContent()));
            }

            final TimeRangeListType timeRangeList = (TimeRangeListType) mapOpenTimeToLocation.get(vendorCode + locationNameNode.getTextContent());
            if (timeRangeList != null) {
                if (null == carLocation.getOpenSchedule()) {
                    carLocation.setOpenSchedule(new OpenScheduleType());
                }
                if (null == carLocation.getOpenSchedule().getNormalRecurringPeriodList()) {
                    carLocation.getOpenSchedule().setNormalRecurringPeriodList(new RecurringPeriodListType());
                }
                if (null == carLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod()) {
                    carLocation.getOpenSchedule().getNormalRecurringPeriodList().setRecurringPeriod(new ArrayList<>());
                }

                DateTime carPickUpDropOffTime = car.getCarInventoryKey().getCarPickUpDateTime();
                if (isDropOffLocation) {
                    carPickUpDropOffTime = car.getCarInventoryKey().getCarDropOffDateTime();
                }
                for (int count = 0; count < timeRangeList.getTimeRange().size(); count++) {
                    final RecurringPeriodType recurringPeriod = new RecurringPeriodType();
                    recurringPeriod.setTimeRangeList(new TimeRangeListType());
                    recurringPeriod.getTimeRangeList().setTimeRange(new ArrayList<>());
                    recurringPeriod.getTimeRangeList().getTimeRange().add(timeRangeList.getTimeRange().get(count));

                    recurringPeriod.setDateRange(new DateRangeType());
                    recurringPeriod.getDateRange().setMinDate(DateTime.getInstanceByDate(carPickUpDropOffTime.getYear(), carPickUpDropOffTime.getMonth(), carPickUpDropOffTime.getDay()));
                    recurringPeriod.getDateRange().setMaxDate(DateTime.getInstanceByDate(carPickUpDropOffTime.getYear(), carPickUpDropOffTime.getMonth(), carPickUpDropOffTime.getDay()));

                    carLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(recurringPeriod);
                }
            }

            carLocation.setAddress((AddressType) mapAddressToLocation.get(vendorCode + locationNameNode.getTextContent()));

            if (isDropOffLocation) {
                car.setCarDropOffLocation(carLocation);
            } else {
                car.setCarPickupLocation(carLocation);
            }
        }

        public void buildCarCostList(CarProductType carProduct, Node rateNode) {
            final DateTime dtStart = carProduct.getCarInventoryKey().getCarPickUpDateTime();
            final DateTime dtEnd = carProduct.getCarInventoryKey().getCarDropOffDateTime();

            final long daysCountUnit = DateTimeUtil.getDiffDays(dtEnd, dtStart);

            final Node rateDetailsInfoNodeList = PojoXmlUtil.getNodeByTagName(rateNode, "rateDetailsInfo");
            final List<Node> tariffInfoNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsInfoNodeList, "tariffInfo");

            if (null == carProduct.getCostList()) {
                carProduct.setCostList(new CostListType());
            }
            if (null == carProduct.getCostList().getCost()) {
                carProduct.getCostList().setCost(new ArrayList<>());
            }

            double amountDecimal = 0.00;
            //double amount_Base = 0.00;
            //double amount_Total = 0.00;
            ///// first get the total amount to calculate the period unit for weekly and monthly
            //for (int count = 0; count < tariffInfoNodeList.Count; count++)
            //{

            //    String rateType = GetElementsByTagName(tariffInfoNodeList[count].InnerXml, "rateType")[0).getTextContent();
            //    if(rateType.Equals("906"))
            //    {
            //        amount_Total = Convert.ToDouble(GetElementsByTagName(tariffInfoNodeList[count].InnerXml, "rateAmount")[0).getTextContent()) * Math.Pow(10, 2);
            //        break;
            //    }
            //}

            for (int count = 0; count < tariffInfoNodeList.size(); count++) {
                final CostType cost = new CostType();
                cost.setMultiplierOrAmount(new MultiplierOrAmountType());
                cost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                cost.getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateCurrency").getTextContent());

                cost.getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(2);

//                amountDecimal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateAmount").getTextContent());
//                amountDecimal = amountDecimal * 100;
                amountDecimal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateAmount").getTextContent()) * Math.pow(10, 2);
                cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(new BigDecimal(amountDecimal).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());

                cost.setFinanceApplicationUnitCount(0L);

                final String rateType = PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateType").getTextContent();
                switch (rateType) {
                    case "3":
                        cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode());
                        cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Daily.getFinanceApplicationCode());
                        cost.setFinanceApplicationUnitCount(daysCountUnit);
                        break;
                    case "6":
                        cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode());
                        cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Weekly.getFinanceApplicationCode());
                        // Get Total decimal
                        final double amountTotal = getTotalDecimal(tariffInfoNodeList, cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());
                        final int unitCount = ASCSGDSReaderUtil.calculatePeriodUnit(CommonEnumManager.FinanceApplicationCode.Weekly.getFinanceApplicationCode(),
                                Long.valueOf(daysCountUnit).intValue(), amountDecimal, amountTotal);
                        cost.setFinanceApplicationUnitCount((long) unitCount);
                        //amount_Base = amountDecimal * unitCount;
                        break;
                    case "5":
                        cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode());
                        cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Weekend.getFinanceApplicationCode());
                        cost.setFinanceApplicationUnitCount(1L);
                        // amount_Base = amountDecimal * 1;
                        break;
                    case "4":
                        cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode());
                        cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Monthly.getFinanceApplicationCode());
                        final double amountTotalMonth = getTotalDecimal(tariffInfoNodeList, cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());
                        final int unitCountMonthly = ASCSGDSReaderUtil.calculatePeriodUnit(CommonEnumManager.FinanceApplicationCode.Monthly.getFinanceApplicationCode(),
                                Long.valueOf(daysCountUnit).intValue(), amountDecimal, amountTotalMonth);
                        cost.setFinanceApplicationUnitCount((long) unitCountMonthly);
                        // amount_Base = amountDecimal * unitCount_monthly;
                        break;
                    case "906":
                        cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode());
                        cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Total.getFinanceApplicationCode());
                        cost.setFinanceApplicationUnitCount(1L);
                        break;
                    default:
                        break;
                }

                if (null != cost.getFinanceApplicationUnitCount() && cost.getFinanceApplicationUnitCount() > 0) {
                    carProduct.getCostList().getCost().add(cost);
                }
            }

            /// Misc  =  total - base* unit , will check in out (not added here)
            AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            commonNodeReader.buildMiscCosts(carProduct.getCostList().getCost());

            commonNodeReader.buildCostListLegacyFinanceKey(carProduct.getCostList());
        }

        private double getTotalDecimal(List<Node> tariffInfoNodeList, String currencyCodeIN) {
            double amountTotal = 0.00;
            /// first get the total amount to calculate the period unit for weekly and monthly
            for (int count = 0; count < tariffInfoNodeList.size(); count++) {
                final String currencyCode = PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateCurrency").getTextContent();
                final String rateType = PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateType").getTextContent();
                if (null != rateType && rateType.equals("906") && currencyCode.equals(currencyCodeIN)) {
                    amountTotal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(tariffInfoNodeList.get(count), "rateAmount").getTextContent()) * Math.pow(10, 2);
                    break;
                }
            }
            return amountTotal;
        }

        public CarMileageType buildCarmileage(Node rateNode) {
            final CarMileageType carMileage = new CarMileageType();
            final DistanceType freeDistance = new DistanceType();
            final CostPerDistanceType exCostPerDistance = new CostPerDistanceType();
            final DistanceType distance = new DistanceType();
            carMileage.setFreeDistance(freeDistance);
            carMileage.setExtraCostPerDistance(exCostPerDistance);
            exCostPerDistance.setDistance(distance);

            final Node rateDetailsInfoNode = PojoXmlUtil.getNodeByTagName(rateNode, "rateDetailsInfo");

            String amountCharge = buildCarMileageLoop(rateDetailsInfoNode, carMileage, freeDistance, distance);

            if (null == freeDistance.getDistanceUnitCount() || (null != freeDistance.getDistanceUnitCount() && freeDistance.getDistanceUnitCount() == -1)) {
                carMileage.getFreeDistance().setDistanceUnit(null);
                carMileage.setExtraCostPerDistance(null);
            } else {
                final Node currencyCodeNode = PojoXmlUtil.getNodeByTagName(rateDetailsInfoNode, "tariffInfo");
                final CurrencyAmountType amount = new CurrencyAmountType();
                amount.setCurrencyCode(PojoXmlUtil.getNodeByTagName(currencyCodeNode, "rateCurrency").getTextContent());
                if (null != amountCharge && !amountCharge.equals("")) {
                    amount.setAmount(new AmountType());
                    amount.getAmount().setDecimalPlaceCount(2);
                    double amountdouble = 0.00;
                    amountdouble = Double.parseDouble(amountCharge) * Math.pow(10, 2);
                    amount.getAmount().setDecimal(new Double(amountdouble).intValue());
                }
                exCostPerDistance.setCostCurrencyAmount(amount);
            }

            return carMileage;
        }

        private String buildCarMileageLoop(Node rateDetailsInfoNode, CarMileageType carMileage, DistanceType freeDistance, DistanceType distance) {
            String amountCharge = "";
            final List<Node> associatedChargesList = PojoXmlUtil.getNodesByTagName(rateDetailsInfoNode, "associatedCharges");

            for (final Node assChargeNode : associatedChargesList) {
                if (PojoXmlUtil.getNodeByTagName(assChargeNode, "chargeType") != null) {
                    final String charegeType = PojoXmlUtil.getNodeByTagName(assChargeNode, "chargeType").getTextContent();

                    final Node quantity = PojoXmlUtil.getNodeByTagName(assChargeNode, "quantity");
                    final Node amountQualifier = PojoXmlUtil.getNodeByTagName(assChargeNode, "amountQualifier");
                    final Node amount = PojoXmlUtil.getNodeByTagName(assChargeNode, "amount");
                    switch (charegeType) {
                        case "9":
                            if (quantity == null) {
                                if (amountQualifier != null) {
                                    if (amountQualifier.getTextContent().equals(CommonEnumManager.DistanceUnit.UNL.getDistanceUnit())) {
                                        freeDistance.setDistanceUnitCount(-1);
                                    }
                                    break;
                                }
                            } else {
                                freeDistance.setDistanceUnitCount(Integer.parseInt(quantity.getTextContent()));
                            }
                            carMileage.getFreeDistance().setDistanceUnit(CommonEnumManager.DistanceUnit.KM.getDistanceUnit());
                            break;
                        case "8":
                            if (null == quantity) {
                                if (amountQualifier != null) {
                                    if (amountQualifier.getTextContent().equals(CommonEnumManager.DistanceUnit.UNL.getDistanceUnit())) {
                                        freeDistance.setDistanceUnitCount(-1);
                                    }
                                    break;
                                }
                            } else {
                                freeDistance.setDistanceUnitCount(Integer.parseInt(quantity.getTextContent()));
                            }
                            freeDistance.setDistanceUnit(CommonEnumManager.DistanceUnit.MI.getDistanceUnit());
                            break;
                        case "6":
                            distance.setDistanceUnitCount(1);
                            distance.setDistanceUnit(CommonEnumManager.DistanceUnit.MI.getDistanceUnit());
                            amountCharge = amount.getTextContent();
                            break;
                        case "7":
                            distance.setDistanceUnitCount(1);
                            distance.setDistanceUnit(CommonEnumManager.DistanceUnit.KM.getDistanceUnit());
                            amountCharge = amount.getTextContent();
                            break;
                        default:
                            break;
                    }
                }
            }

            return amountCharge;
        }
    }
}