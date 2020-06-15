package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.timetypes.defn.v4.*;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.beans.BeanUtils;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yyang4 on 12/14/2016.
 */
@SuppressWarnings("PMD")
public class VRURRes {
    public CarProductType carProduct;
    public String totalDiscount;
    public String totalMandaryCharge;
    public boolean corporateRate;
    public String discountNumberApplied = "";

    @SuppressWarnings("CPD-START")
    public VRURRes(Node vrurRes, CarInventoryKeyType carsInventorykeyExcepted, DataSource scsDataSource, DataSource carsInventoryDs, boolean needSpoofer) throws DataAccessException {
        final Node vehicleNode = PojoXmlUtil.getNodeByTagName(vrurRes, "Vehicle");
        final Node vehicleRateNode = PojoXmlUtil.getNodeByTagName(vrurRes, "VehicleRate");
        final Node supplierRateNode = PojoXmlUtil.getNodeByTagName(vrurRes, "SupplierRate");
        final List<Node> vehicleRateDescriptionList = PojoXmlUtil.getNodesByTagName(vrurRes, "VehicleRateDescription");
        final Node hourlyLateChargeNode = PojoXmlUtil.getNodeByTagName(vrurRes, "HourlyLateCharge");
        final Node dailyLateChargeNode = PojoXmlUtil.getNodeByTagName(vrurRes, "DailyLateCharge");
        final Node weeklyLateChargeNode = PojoXmlUtil.getNodeByTagName(vrurRes, "WeeklyLateCharge");
        final List<Node> operationTimelist = PojoXmlUtil.getNodesByTagName(vrurRes, "OperationTime");
        final Node startEndTimesNode = PojoXmlUtil.getNodeByTagName(vrurRes, "StartEndTimes");
        final Node rentalPeriodRulesNode = PojoXmlUtil.getNodeByTagName(vrurRes, "RentalPeriodRules");
        final List<Node> vehicleChargeList = PojoXmlUtil.getNodesByTagName(vrurRes, "VehicleCharge");
        final List<Node> vehiclePolicyCategoryInformationNodeList = PojoXmlUtil.getNodesByTagName(vrurRes, "Policy");

        carProduct = new CarProductType();
        final CarInventoryKeyType inventoryKey = new CarInventoryKeyType();
        final CarCatalogKeyType carCatalogKey = new CarCatalogKeyType();
        inventoryKey.setCarCatalogKey(carCatalogKey);
        carProduct.setCarInventoryKey(inventoryKey);
        if (!CompareUtil.isObjEmpty(carsInventorykeyExcepted)) {
            // parts of expected value directly get from request.
            carCatalogKey.setCarPickupLocationKey(carsInventorykeyExcepted.getCarCatalogKey().getCarPickupLocationKey());
            carCatalogKey.setCarDropOffLocationKey(carsInventorykeyExcepted.getCarCatalogKey().getCarDropOffLocationKey());
            inventoryKey.setCarPickUpDateTime(carsInventorykeyExcepted.getCarPickUpDateTime());
            inventoryKey.setCarDropOffDateTime(carsInventorykeyExcepted.getCarDropOffDateTime());
        }
        //region inventory key
        if (!CompareUtil.isObjEmpty(vehicleNode)) {
            carCatalogKey.setCarVehicle(UAPICommonNodeReader.readCarVehicle(scsDataSource, vehicleNode, false));
            carCatalogKey.setVendorSupplierID(UAPICommonNodeReader.readSupplierIDByVendorCode(carsInventoryDs, vehicleNode.getAttributes().getNamedItem("VendorCode").getNodeValue()));
        }

        final CarRateType carRate = new CarRateType();
        inventoryKey.setCarRate(carRate);
        if (!CompareUtil.isObjEmpty(vehicleRateNode)) {
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("CarAgreementID"))) {
                carRate.setCarAgreementID(Long.valueOf(vehicleRateNode.getAttributes().getNamedItem("CarAgreementID").getNodeValue()));
            }
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCategory"))) {
                carRate.setRateCategoryCode(vehicleRateNode.getAttributes().getNamedItem("RateCategory").getNodeValue());
            }
            if (CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RatePeriod"))) {
                carRate.setRatePeriodCode("Trip");
            } else {
                carRate.setRatePeriodCode(vehicleRateNode.getAttributes().getNamedItem("RatePeriod").getNodeValue());
                if (carRate.getRatePeriodCode().contains("Weekend")) {
                    carRate.setRatePeriodCode("Weekend");
                }
                if (carRate.getRatePeriodCode().contains("Total")) {
                    carRate.setRatePeriodCode("Trip");
                }
            }
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("RateCode"))) {
                carRate.setRateCode(vehicleRateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("PromoCode"))) {
                carRate.setPromoCode(vehicleRateNode.getAttributes().getNamedItem("PromoCode").getNodeValue());
            }
            //if (Vehicle_Node.Attributes["LoyaltyProgram"] != null)
            //    inventoryKey.CarRate.LoyaltyProgram = Vehicle_Node.Attributes["LoyaltyProgram"].Value;
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber"))) {
                carRate.setCorporateDiscountCode(vehicleRateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
            }

            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("CorporateRate"))) {
                corporateRate = Boolean.parseBoolean(vehicleRateNode.getAttributes().getNamedItem("CorporateRate").getNodeValue());
            }
            if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("DiscountNumberApplied"))) {
                discountNumberApplied = vehicleRateNode.getAttributes().getNamedItem("DiscountNumberApplied").getNodeValue();
            }
        }

        if (!CompareUtil.isObjEmpty(operationTimelist)) {
            buildCarPickAndDropLocationTimeRange(carProduct, operationTimelist);
        }

        /// carProduct.CarPolicyList
        if (!CompareUtil.isObjEmpty(vehicleRateDescriptionList)) {
            buildCarPolicyList(carProduct, vehicleRateDescriptionList, scsDataSource);
        }
        buildCarPolicyList(carProduct, vehiclePolicyCategoryInformationNodeList, scsDataSource);

        ///CarRentalLimits
        buildCarRentalLimits(carProduct, startEndTimesNode, rentalPeriodRulesNode);

        //CarRateDetail
        buildConditionalCostPriceList(carProduct, vehicleChargeList, hourlyLateChargeNode, dailyLateChargeNode, weeklyLateChargeNode);

        /// cost list --TODO
        if (!CompareUtil.isObjEmpty(supplierRateNode)) {
            buildCarCostList(carProduct, supplierRateNode, vehicleChargeList, vehicleRateNode, needSpoofer);
        }

        if (!CompareUtil.isObjEmpty(vehicleChargeList)) {
            buildCarVehicleOptionList(carProduct, vehicleChargeList,scsDataSource);
        }

        if (!CompareUtil.isObjEmpty(supplierRateNode) && !CompareUtil.isObjEmpty(vehicleRateNode)) {
            buildCarMileage(carProduct, vehicleRateNode, supplierRateNode);
        }
        // Some price related node  -- TODO
        //// Get Total Rate Discount
        //this.totalDiscount = null;

        //// Get Total Mandary Charge
        //this.totalMandaryCharge = null;

        //CCGurantee <vehicle:PaymentRule Purpose="Guarantee" Required="true">
        final Node paymentRuleNode = PojoXmlUtil.getNodeByTagName(vrurRes, "PaymentRule");
        if (!CompareUtil.isObjEmpty(paymentRuleNode) && "Guarantee".equals(paymentRuleNode.getAttributes().getNamedItem("Purpose").getNodeValue()) && !CompareUtil.isObjEmpty(paymentRuleNode.getAttributes().getNamedItem("Required")) && Boolean.parseBoolean(paymentRuleNode.getAttributes().getNamedItem("Required").getNodeValue())) {
            carProduct.setReservationGuaranteeCategory("Required");
        }

        // Get CarPickupLocation from PickupLocationInformation
        final Node pickupLocationInformation = PojoXmlUtil.getNodeByTagName(vrurRes, "PickupLocationInformation");
        buildCarPickupLocation(carProduct.getCarPickupLocation(), carProduct.getCarInventoryKey().getCarPickUpDateTime(), pickupLocationInformation, scsDataSource);

        final Node returnLocationInformation = PojoXmlUtil.getNodeByTagName(vrurRes, "ReturnLocationInformation");
        CarLocationType carPickupLocation = new CarLocationType();
        if (CompareUtil.isObjEmpty(returnLocationInformation)) {
            final List<Node> operationTimeList = PojoXmlUtil.getNodesByTagName(vrurRes, "OperationTime");
            final DateTime dropOffTime = carProduct.getCarInventoryKey().getCarDropOffDateTime();
            final RecurringPeriodType recurringPeriodFromDropOff = new RecurringPeriodType();
            final TimeRangeType timeRange = new TimeRangeType();
            recurringPeriodFromDropOff.setDateRange(new DateRangeType());
            recurringPeriodFromDropOff.getDateRange().setMinDate(DateTime.getInstanceByDateTime(dropOffTime.getYear(), dropOffTime.getMonth(), dropOffTime.getDay(), 0, 0, 0, 0));
            recurringPeriodFromDropOff.getDateRange().setMaxDate(DateTime.getInstanceByDateTime(dropOffTime.getYear(), dropOffTime.getMonth(), dropOffTime.getDay(), 0, 0, 0, 0));
            final TimeRangeListType timeRangeList = new TimeRangeListType();
            final List<TimeRangeType> timeRangeTypes = new ArrayList<TimeRangeType>();
            timeRangeList.setTimeRange(timeRangeTypes);
            recurringPeriodFromDropOff.setTimeRangeList(timeRangeList);
            recurringPeriodFromDropOff.getTimeRangeList().getTimeRange().add(timeRange);
            if (!CompareUtil.isObjEmpty(operationTimeList)) {
                for (final Node operationTime : operationTimeList) {
                    if ("Departure".equals(operationTime.getTextContent().split(" ")[0])) {
                        final String time = operationTime.getTextContent().split(" ")[1];
                        buildCarPickAndDropLocationTimeRange(timeRange, carProduct.getCarInventoryKey().getCarDropOffDateTime(), time.split("-")[0], time.split("-")[1]);
                        break;
                    }
                }
            }
            carProduct.getCarPickupLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(recurringPeriodFromDropOff);
            BeanUtils.copyProperties(carProduct.getCarPickupLocation(), carPickupLocation);
            carPickupLocation.setCarShuttleCategoryCode("");
        } else {
            buildCarPickupLocation(carPickupLocation, carProduct.getCarInventoryKey().getCarDropOffDateTime(), returnLocationInformation, scsDataSource);
        }
        carProduct.setCarDropOffLocation(carPickupLocation);
    }

    public void buildCarPickupLocation(CarLocationType carPickupLocation, DateTime pickupDateTime, Node pickupLocationInformation, DataSource scsDataSource) throws DataAccessException {
        if (CompareUtil.isObjEmpty(carPickupLocation.getCarLocationKey())) {
            carPickupLocation.setCarLocationKey(new CarLocationKeyType());
        }
        //CarLocationKey
        if (!CompareUtil.isObjEmpty(pickupLocationInformation.getAttributes().getNamedItem("LocationCode"))) {
            carPickupLocation.getCarLocationKey().setLocationCode(pickupLocationInformation.getAttributes().getNamedItem("LocationCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(pickupLocationInformation.getAttributes().getNamedItem("LocationType"))) {
            final String carLocationCategoryCode = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.LOCATION_TYPE, "", pickupLocationInformation.getAttributes().getNamedItem("LocationType").getNodeValue());
            carPickupLocation.getCarLocationKey().setCarLocationCategoryCode(carLocationCategoryCode);
        }
        if (!CompareUtil.isObjEmpty(pickupLocationInformation.getAttributes().getNamedItem("LocationNumber"))) {
            carPickupLocation.getCarLocationKey().setSupplierRawText(pickupLocationInformation.getAttributes().getNamedItem("LocationNumber").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(pickupLocationInformation.getAttributes().getNamedItem("CounterLocation"))) {
            final String carShuttleCategoryCoe = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_SHUTTLE_CATEGORY, "", pickupLocationInformation.getAttributes().getNamedItem("CounterLocation").getNodeValue());
            carPickupLocation.setCarShuttleCategoryCode(carShuttleCategoryCoe);
        }

        //Address
        final List<Node> streetList = PojoXmlUtil.getNodesByTagName(pickupLocationInformation, "Street");
        int i = 0;
        final AddressType address = new AddressType();
        address.setAddressCategoryCode("2");
        if (streetList != null) {
            carPickupLocation.setAddress(address);
            for (final Node street : streetList) {
                i++;
                if (Pattern.compile("^[0-9-]*$").matcher(street.getTextContent()).matches()) {
                    address.setPostalCode(street.getTextContent());
                    continue;
                }
                if (Pattern.compile("^[A-Z]{2,3}$").matcher(street.getTextContent()).matches()) {
                    address.setProvinceName(street.getTextContent());
                    continue;
                }
                if (Pattern.compile("^[A-Z]{2,3} [A-Z]{2,3}$").matcher(street.getTextContent()).matches()) {
                    address.setProvinceName(street.getTextContent().split(" ")[0]);
                    address.setCountryAlpha3Code(street.getTextContent().split(" ")[1]);
                    continue;
                }
                if (i == 1) {
                    address.setFirstAddressLine(street.getTextContent());
                } else if (i == 2) {
                    address.setSecondAddressLine(street.getTextContent());
                } else if (i == 3) {
                    address.setThirdAddressLine(street.getTextContent());
                } else if (i == 4) {
                    address.setFourthAddressLine(street.getTextContent());
                } else if (i == 5) {
                    address.setFifthAddressLine(street.getTextContent());
                }

            }
        }
        final Node city = PojoXmlUtil.getNodeByTagName(pickupLocationInformation, "City");
        if (!CompareUtil.isObjEmpty(city)) {
            address.setCityName(city.getTextContent());
        }
        final Node state = PojoXmlUtil.getNodeByTagName(pickupLocationInformation, "State");
        if (state != null) {
            address.setProvinceName(state.getTextContent());
        }
        final Node postalCode = PojoXmlUtil.getNodeByTagName(pickupLocationInformation, "PostalCode");
        if (postalCode != null) {
            address.setPostalCode(postalCode.getTextContent());
        }
        final Node country = PojoXmlUtil.getNodeByTagName(pickupLocationInformation, "Country");
        if (country != null) {
            address.setCountryAlpha3Code(country.getTextContent());
        }

        //PhoneList
        final PhoneListType phoneList = new PhoneListType();
        final List<PhoneType> phoneTypeList = new ArrayList<PhoneType>();
        phoneList.setPhone(phoneTypeList);
        carPickupLocation.setPhoneList(phoneList);
        final List<Node> phoneNumberList = PojoXmlUtil.getNodesByTagName(pickupLocationInformation, "PhoneNumber");
        for (final Node phoneNumber : phoneNumberList) {
            final PhoneType phone = new PhoneType();
            phoneTypeList.add(phone);
            if (CompareUtil.isObjEmpty(phoneNumber.getAttributes().getNamedItem("Type")) || !"Business".equals(phoneNumber.getAttributes().getNamedItem("Type").getNodeValue())) {
                phone.setPhoneCategoryCode("0");
            } else {
                phone.setPhoneCategoryCode("2");
            }

            final String phoneNumberStr = phoneNumber.getAttributes().getNamedItem("Number").getNodeValue();
            if (!CompareUtil.isObjEmpty(phoneNumberStr)) {
                if (Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}.*$").matcher(phoneNumberStr).matches()) {
                    phone.setPhoneCountryCode("1");
                    phone.setPhoneAreaCode(phoneNumberStr.substring(0, phoneNumberStr.indexOf('-')));
                    phone.setPhoneNumber(phoneNumberStr.substring(phoneNumberStr.indexOf('-') + 1));
                } else if (Pattern.compile("^1-[0-9]{3}-[0-9]{3}-[0-9]{4}.*$").matcher(phoneNumberStr).matches()) {
                    phone.setPhoneCountryCode("1");
                    phone.setPhoneAreaCode(phoneNumberStr.split("-")[1]);
                    phone.setPhoneNumber(phoneNumberStr.substring(phone.getPhoneAreaCode().length() + 3));
                } else if (Pattern.compile("^[0-9]{3} [0-9]{3} [0-9]{4}.*$").matcher(phoneNumberStr).matches()) {
                    phone.setPhoneCountryCode("1");
                    phone.setPhoneAreaCode(phoneNumberStr.substring(0, phoneNumberStr.indexOf(' ')));
                    phone.setPhoneNumber(phoneNumberStr.substring(phoneNumberStr.indexOf(' ') + 1));
                } else if (Pattern.compile("^1 [0-9]{3} [0-9]{3} [0-9]{4}.*$").matcher(phoneNumberStr).matches()) {
                    phone.setPhoneCountryCode("1");
                    phone.setPhoneAreaCode(phoneNumberStr.split(" ")[1]);
                    phone.setPhoneNumber(phoneNumberStr.substring(phone.getPhoneAreaCode().length() + 3));
                } else if (Pattern.compile("^\\+[0-9]{2} [0-9]+ .*$").matcher(phoneNumberStr).matches()) {
                    phone.setPhoneCountryCode(phoneNumberStr.substring(0, 2));
                    phone.setPhoneAreaCode(phoneNumberStr.split(" ")[1]);
                    phone.setPhoneNumber(phoneNumberStr.substring(phone.getPhoneAreaCode().length() + 5));
                } else {
                    phone.setPhoneNumber(phoneNumberStr);
                }
            }
        }

        //OperationTime
        // RecurringPeriod recurringPeriod = new RecurringPeriod();
        // carPickupLocation.OpenSchedule.NormalRecurringPeriodList.Add(recurringPeriod);
        if (CompareUtil.isObjEmpty(carPickupLocation.getOpenSchedule())) {
            carPickupLocation.setOpenSchedule(new OpenScheduleType());
        }
        if (CompareUtil.isObjEmpty(carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList())) {
            carPickupLocation.getOpenSchedule().setNormalRecurringPeriodList(new RecurringPeriodListType());
        }
        if (CompareUtil.isObjEmpty(carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod())) {
            final List<RecurringPeriodType> recurringPeriodTypeList = new ArrayList<RecurringPeriodType>();
            final RecurringPeriodType recurringPeriod = new RecurringPeriodType();
            recurringPeriodTypeList.add(recurringPeriod);
            final TimeRangeListType timeRangeListType = new TimeRangeListType();
            final List<TimeRangeType> timeRangeTypes = new ArrayList<TimeRangeType>();
            timeRangeListType.setTimeRange(timeRangeTypes);
            final DateRangeType dateRangeType = new DateRangeType();
            recurringPeriod.setDateRange(dateRangeType);
            recurringPeriod.setTimeRangeList(timeRangeListType);
            carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().setRecurringPeriod(recurringPeriodTypeList);
        }
        if (CompareUtil.isObjEmpty(carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getTimeRangeList().getTimeRange())) {
            carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getTimeRangeList().getTimeRange().add(new TimeRangeType());
        }
        carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getDateRange().setMinDate(DateTime.getInstanceByDateTime(pickupDateTime.getYear(), pickupDateTime.getMonth(), pickupDateTime.getDay(), 0, 0, 0, 0));
        carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getDateRange().setMaxDate(DateTime.getInstanceByDateTime(pickupDateTime.getYear(), pickupDateTime.getMonth(), pickupDateTime.getDay(), 0, 0, 0, 0));
        final List<Node> operationTimeList = PojoXmlUtil.getNodesByTagName(pickupLocationInformation, "OperationTime");
        if (operationTimeList != null) {
            for (final Node operationTime : operationTimeList) {
                if ("vehicle:PickupLocationInformation".equals(pickupLocationInformation.getNodeName()) && "Arrival".equals(operationTime.getTextContent().split(" ")[0])) {
                    final String time = operationTime.getTextContent().split(" ")[1];
                    buildCarPickAndDropLocationTimeRange(carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getTimeRangeList().getTimeRange().get(0), pickupDateTime, time.split("-")[0], time.split("-")[1]);
                    break;
                }
                if ("vehicle:ReturnLocationInformation".equals(pickupLocationInformation.getNodeName()) && "Departure".equals(operationTime.getTextContent().split(" ")[0])) {
                    final String time = operationTime.getTextContent().split(" ")[1];
                    buildCarPickAndDropLocationTimeRange(carPickupLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().get(0).getTimeRangeList().getTimeRange().get(0), pickupDateTime, time.split("-")[0], time.split("-")[1]);
                    break;
                }
            }
        }
    }

    public void buildCarPickAndDropLocationTimeRange(CarProductType carproduct, List<Node> operationTimeList) {

        //CarPickupLocation OpenSchedule
        //CarDropOffLocation OpenSchedule
        for (final Node operNode : operationTimeList) {
            if (operNode.getTextContent().startsWith("ArrivalTimeInformation")) {
                final DateTime date = carproduct.getCarInventoryKey().getCarPickUpDateTime();
                //OpenTime/Check-in time 0000-CloseTime/Check-out time 2400
                final TimeRangeType timeRange = new TimeRangeType();
                final List<String> numberList = regexGetTheNumberListFromString(operNode.getTextContent());
                if (numberList.size() == 2) {
                    if (CompareUtil.isObjEmpty(carproduct.getCarPickupLocation())) {
                        final CarLocationType pickupLocation = new CarLocationType();
                        final OpenScheduleType openSchedule = new OpenScheduleType();
                        final RecurringPeriodListType recurringPeriodList = new RecurringPeriodListType();
                        final List<RecurringPeriodType> recurringPeriods = new ArrayList<RecurringPeriodType>();
                        recurringPeriodList.setRecurringPeriod(recurringPeriods);
                        openSchedule.setNormalRecurringPeriodList(recurringPeriodList);
                        pickupLocation.setOpenSchedule(openSchedule);
                        carproduct.setCarPickupLocation(pickupLocation);
                    }
                    carproduct.getCarPickupLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(buildRecurringPeriod(date, numberList, timeRange));
                }
            }
            if (operNode.getTextContent().startsWith("DepartureTimeInformation")) {
                final DateTime date = carproduct.getCarInventoryKey().getCarDropOffDateTime();
                //OpenTime/Check-in time 0000-CloseTime/Check-out time 2400
                final TimeRangeType timeRange = new TimeRangeType();
                final List<String> numberList = regexGetTheNumberListFromString(operNode.getTextContent());
                if (numberList.size() == 2) {
                    if (CompareUtil.isObjEmpty(carproduct.getCarDropOffLocation())) {
                        final CarLocationType dropoffLocation = new CarLocationType();
                        final OpenScheduleType openSchedule = new OpenScheduleType();
                        final RecurringPeriodListType recurringPeriodList = new RecurringPeriodListType();
                        final List<RecurringPeriodType> recurringPeriods = new ArrayList<RecurringPeriodType>();
                        recurringPeriodList.setRecurringPeriod(recurringPeriods);
                        openSchedule.setNormalRecurringPeriodList(recurringPeriodList);
                        dropoffLocation.setOpenSchedule(openSchedule);
                        carproduct.setCarDropOffLocation(dropoffLocation);
                    }
                    carproduct.getCarDropOffLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(buildRecurringPeriod(date, numberList, timeRange));
                }
            }


        }
    }

    public RecurringPeriodType buildRecurringPeriod(DateTime date, List<String> numberList, TimeRangeType timeRange) {
        final DateRangeType dateRange = new DateRangeType();
        final DateTime dateMin = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0, 0);
        final DateTime dateMax = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), 0, 0, 0, 0);
        dateRange.setMinDate(dateMin);
        dateRange.setMaxDate(dateMax);
        final int hours = Integer.parseInt(numberList.get(0).substring(0, 2));
        final int minutes = Integer.parseInt(numberList.get(0).substring(2, 4));

        int hours2 = Integer.parseInt(numberList.get(1).substring(0, 2));
        int minutes2 = Integer.parseInt(numberList.get(1).substring(2, 4));
        if (hours2 == 24) {
            hours2 = 23;
            minutes2 = 59;
        }
        final DateTime minTime = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), hours, minutes, 0, 0);
        final DateTime maxTime = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), hours2, minutes2, 0, 0);
        timeRange.setMinTime(minTime);
        timeRange.setMaxTime(maxTime);
        final RecurringPeriodType recurring1 = new RecurringPeriodType();
        final TimeRangeListType timeRangeList = new TimeRangeListType();
        final List<TimeRangeType> timeRangeTypes = new ArrayList<TimeRangeType>();
        timeRangeTypes.add(timeRange);
        timeRangeList.setTimeRange(timeRangeTypes);
        recurring1.setTimeRangeList(timeRangeList);
        recurring1.setDateRange(dateRange);
        return recurring1;
    }

    /// <summary>
    /// Build CarPolicyList based on VehiclePolicyCategoryInformation
    /// </summary>
    /// <param name="carProduct"></param>
    /// <param name="VehiclePolicyCategoryInformation_NodeList"></param>
    public void buildCarPolicyList(CarProductType carProduct, List<Node> vehiclePolicyCategoryInformationNodeList, DataSource scsDataSource) throws DataAccessException {
        if (CompareUtil.isObjEmpty(carProduct.getCarPolicyList()) && !CompareUtil.isObjEmpty(vehiclePolicyCategoryInformationNodeList)) {
            final CarPolicyListType carPolicyListType = new CarPolicyListType();
            carPolicyListType.setCarPolicy(new ArrayList<CarPolicyType>());
            carProduct.setCarPolicyList(carPolicyListType);
        }
        for (final Node policyNode : vehiclePolicyCategoryInformationNodeList) {
            if (!CompareUtil.isObjEmpty(policyNode.getChildNodes())) {
                final CarPolicyType policy = new CarPolicyType();
                if ("General".equals(policyNode.getAttributes().getNamedItem("Name").getNodeValue())) {
                    policy.setCarPolicyCategoryCode("GeneralText");
                } else if ("Advisory".equals(policyNode.getAttributes().getNamedItem("Name").getNodeValue())) {
                    policy.setCarPolicyCategoryCode("Advisory");
                } else if ("Guarantee".equals(policyNode.getAttributes().getNamedItem("Name").getNodeValue())) {
                    policy.setCarPolicyCategoryCode("Guarantee");
                } else if ("RentalRestriction".equals(policyNode.getAttributes().getNamedItem("Name").getNodeValue())) {
                    policy.setCarPolicyCategoryCode("RentalRestriction");
                } else {
                    policy.setCarPolicyCategoryCode(UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_POLICY_CATEGORY, "", policyNode.getAttributes().getNamedItem("Name").getNodeValue()));
                }
                final StringBuilder policyText = new StringBuilder();
                for (int i = 0; i < policyNode.getChildNodes().getLength(); i++) {
                    final Node textNode = policyNode.getChildNodes().item(i);
                    // <vehicle:Policy Description="Special Equipment Requests" Name="EQUIP">
                    //    <common_v33_0:SubKey Description="Child Seat/Infant (0-12 month / 0-13kg)" Name="InfantSeat">
                    //        <common_v33_0:Text>INFANT SEATS ARE AVAILABLE FOR AN ADDITIONAL CHARGE   (</common_v33_0:Text>
                    //    </common_v33_0:SubKey>
                    //</vehicle:Policy>
                    if (textNode.getNodeName().equals("SubKey")) {
                        policyText.append(textNode.getAttributes().getNamedItem("Description").getNodeValue());
                        policyText.append(textNode.getAttributes().getNamedItem("Name").getNodeValue());
                        for (int j = 0; j < textNode.getChildNodes().getLength(); j++) {
                            policyText.append(textNode.getChildNodes().item(j).getTextContent());
                        }
                    } else {
                        policyText.append(textNode.getTextContent());
                    }
                }
                policy.setCarPolicyRawText(policyText.toString());
                carProduct.getCarPolicyList().getCarPolicy().add(policy);
            }
        }
    }

    public void buildCarRentalLimits(CarProductType carProduct, Node startEndTimesNode, Node rentalPeriodRulesNode) {
        final CarRentalLimitsType rentalLimits = new CarRentalLimitsType();
        if (CompareUtil.isObjEmpty(carProduct.getCarRateDetail())) {
            final CarRateDetailType carRateDetail = new CarRateDetailType();
            carProduct.setCarRateDetail(carRateDetail);
        }
        carProduct.getCarRateDetail().setCarRentalLimits(rentalLimits);
        if (!CompareUtil.isObjEmpty(startEndTimesNode)) {
            final Calendar calendar = Calendar.getInstance();
            if (!CompareUtil.isObjEmpty(PojoXmlUtil.getNodeByTagName(rentalPeriodRulesNode, "LatestEnd"))) {
                calendar.add(Calendar.HOUR, Integer.parseInt(PojoXmlUtil.getNodeByTagName(rentalPeriodRulesNode, "LatestEnd").getAttributes().getNamedItem("Time").getNodeValue().substring(0, 2)));
                calendar.add(Calendar.MINUTE, Integer.parseInt(PojoXmlUtil.getNodeByTagName(rentalPeriodRulesNode, "LatestEnd").getAttributes().getNamedItem("Time").getNodeValue().substring(2, 2)));
            }
            rentalLimits.setLatestReturnTime(DateTime.getInstanceByDateTime(calendar));
        }
        if (!CompareUtil.isObjEmpty(rentalPeriodRulesNode)) {
            final Node minRenTalNode = PojoXmlUtil.getNodeByTagName(rentalPeriodRulesNode, "MinRental");
            final Node maxRentalNode = PojoXmlUtil.getNodeByTagName(rentalPeriodRulesNode, "MaxRental");
            final DurationType minDuration = new DurationType();
            rentalLimits.setMinDuration(minDuration);
            final DurationType maxDuration = new DurationType();
            rentalLimits.setMaxDuration(maxDuration);
            if (!CompareUtil.isObjEmpty(minRenTalNode)) {
                if ("Days".equals(minRenTalNode.getAttributes().getNamedItem("RentalUnit").getNodeValue())) {
                    rentalLimits.getMinDuration().setTimeUnit("D");
                }
                if ("Hours".equals(minRenTalNode.getAttributes().getNamedItem("RentalUnit").getNodeValue())) {
                    rentalLimits.getMinDuration().setTimeUnit("H");
                }
                rentalLimits.getMinDuration().setMinDurationCount(Long.valueOf(minRenTalNode.getAttributes().getNamedItem("Length").getNodeValue()));
            }
            if (!CompareUtil.isObjEmpty(maxRentalNode)) {
                if ("Days".equals(maxRentalNode.getAttributes().getNamedItem("RentalUnit").getNodeValue())) {
                    rentalLimits.getMaxDuration().setTimeUnit("D");

                }
                if ("Hours".equals(maxRentalNode.getAttributes().getNamedItem("RentalUnit").getNodeValue())) {
                    rentalLimits.getMaxDuration().setTimeUnit("H");
                }
                rentalLimits.getMaxDuration().setMaxDurationCount(Long.valueOf(maxRentalNode.getAttributes().getNamedItem("Length").getNodeValue()));
            }
        }
    }

    public void buildConditionalCostPriceList(CarProductType carproduct, List<Node> vehicleChargeList,
                                              Node hourlyLateChargeNode, Node dailyLateChargeNode, Node weeklyLateChargeNode) {
        final List<CostPriceType> costPriceTypes = new ArrayList<CostPriceType>();
        if (CompareUtil.isObjEmpty(carproduct.getCarRateDetail())) {
            final CarRateDetailType carRateDetail = new CarRateDetailType();
            carproduct.setCarRateDetail(carRateDetail);
        } else if (CompareUtil.isObjEmpty(carproduct.getCarRateDetail().getConditionalCostPriceList())) {
            final CostPriceListType costPriceList = new CostPriceListType();
            costPriceList.setCostPrice(costPriceTypes);
            carproduct.getCarRateDetail().setConditionalCostPriceList(costPriceList);
        }
        carproduct.getCarRateDetail().getConditionalCostPriceList().setCostPrice(costPriceTypes);
        String currencyCode = "";
        final int decimalPC = 2;
        if (!CompareUtil.isObjEmpty(hourlyLateChargeNode)) {
            //Get currencyCode
            currencyCode = hourlyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);
            final double vehicleCharge = Double.parseDouble(hourlyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
            final int amount = (int) (vehicleCharge * Math.pow(10, 2));
            final CostType extraHourlyCost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "Extra", "ExtraHourly", 0, 8, 1, 25, "", false);
            extraHourlyCost.setDescriptionRawText("extra hour charge");
            final CostPriceType costPrice = new CostPriceType();
            costPrice.setCost(extraHourlyCost);
            costPriceTypes.add(costPrice);
        }

        if (!CompareUtil.isObjEmpty(dailyLateChargeNode)) {
            currencyCode = dailyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);
            final double dailyCharge = Double.parseDouble(dailyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
            final int amount = (int) (dailyCharge * Math.pow(10, 2));
            final CostType extraDailyCost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "Extra", "ExtraDaily", 0, 8, 1, 7, "", false);
            extraDailyCost.setDescriptionRawText("extra day charge");
            final CostPriceType costPrice = new CostPriceType();
            costPrice.setCost(extraDailyCost);
            costPriceTypes.add(costPrice);
        }

        if (!CompareUtil.isObjEmpty(weeklyLateChargeNode)) {
            currencyCode = weeklyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(0, 3);
            final double charge = Double.parseDouble(weeklyLateChargeNode.getAttributes().getNamedItem("RateForPeriod").getNodeValue().substring(3));
            final int amount = (int) (charge * Math.pow(10, 2));
            final CostType extraDailyCost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "Extra", "ExtraWeekly", 0, 8, 1, 8, "", false);
            extraDailyCost.setDescriptionRawText("extra week charge");
            final CostPriceType costPrice = new CostPriceType();
            costPrice.setCost(extraDailyCost);
            costPriceTypes.add(costPrice);
        }

        for (final Node chargeNode : vehicleChargeList) {
            final String catagoryCode = chargeNode.getAttributes().getNamedItem("Category").getNodeValue();
            if ("Coverage".equals(catagoryCode) || "Conditional".equals(catagoryCode)) {
                final String chargetype = CompareUtil.isObjEmpty(chargeNode.getAttributes().getNamedItem("Type")) ? "" : chargeNode.getAttributes().getNamedItem("Type").getNodeValue();
                String applicationCode = null;
                switch (chargetype) {
                    case "PerHour":
                        applicationCode = "Hourly";
                        break;
                    case "PerDay":
                        applicationCode = "Daily";
                        break;
                    case "PerWeek":
                        applicationCode = "Weekly";
                        break;
                    case "PerMonth":
                        applicationCode = "Monthly";
                        break;
                    case "Percent":
                        applicationCode = "Percentage";
                        break;
                    case "NoCharge":
                        applicationCode = "Included";
                        break;
                    default:
                        applicationCode = "Trip";
                        break;
                }
                currencyCode = PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent().substring(0, 3);
                final double vehicleCharge = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent().substring(3));
                final String chargeName = chargeNode.getAttributes().getNamedItem("Name").getNodeValue();
                final int amount = (int) (vehicleCharge * Math.pow(10, 2));
                final CostType covCost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "Optional", applicationCode, 1, 8, 1, 21, "", false);
                covCost.setDescriptionRawText(chargetype + "," + chargeName);
                final CostPriceType costPrice = new CostPriceType();
                costPrice.setCost(covCost);
                costPriceTypes.add(costPrice);
            }

        }
    }

    public void buildCarCostList(CarProductType carProduct, Node supplierRateNode, List<Node> vehicleChargeList, Node vehicleRateNode, boolean needSpoofer) {
        int numberOfPeriods = 1;
        if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("NumberOfPeriods"))) {
            numberOfPeriods = Integer.parseInt(vehicleRateNode.getAttributes().getNamedItem("NumberOfPeriods").getNodeValue());
        }

        carProduct.setCostList(UAPICommonNodeReader.buildCostListGetDetails(supplierRateNode, vehicleChargeList, carProduct.getCarInventoryKey().getCarRate().getRatePeriodCode(), numberOfPeriods, needSpoofer));
        // build Surcharge and tax
        for (final Node chargeNode : vehicleChargeList) {
            final String catagoryCode = chargeNode.getAttributes().getNamedItem("Category").getNodeValue();
            if ("Surcharge".equals(catagoryCode) || "Tax".equals(catagoryCode)) {
                final String chargetype = CompareUtil.isObjEmpty(chargeNode.getAttributes().getNamedItem("Type")) ? "" : chargeNode.getAttributes().getNamedItem("Type").getNodeValue();
                String applicationCode = null;
                final int unitCount = 1;
                switch (chargetype) {
                    case "PerHour": {
                        applicationCode = "Hourly";
                        break;
                    }
                    case "PerDay": {
                        applicationCode = "Daily";
                        break;
                    }
                    case "PerWeek": {
                        applicationCode = "Weekly";
                        break;
                    }
                    case "PerMonth": {
                        applicationCode = "Monthly";
                        break;
                    }
                    case "Percent": {
                        applicationCode = "Percentage";
                        break;
                    }
                    case "NoCharge": {
                        applicationCode = "Included";
                        break;
                    }
                    default: {
                        applicationCode = "Trip";
                        break;
                    }
                }
                final String amountText = PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent();
                final String currencyCode = amountText.substring(0, 3);
                final double vehicleCharge = Double.parseDouble(amountText.substring(3));
                final String chargeName = chargeNode.getAttributes().getNamedItem("Name").getNodeValue();
                int amount = 0;
                int decimalPC = 2;
                if (amountText.substring(3).contains(".")) {
                    if ((amountText.substring(3)).split("\\.")[1].length() == 1) {
                        amount = (int) (vehicleCharge * Math.pow(10, 2));
                    } else {
                        amount = Integer.parseInt(amountText.substring(3).replaceAll("\\.", ""));
                    }
                } else {
                    amount = (int) (vehicleCharge * Math.pow(10, 2));
                }
                //int amountDecimal = int.Parse(chargeNode["Amount"].InnerText.Substring(3).Replace(".", ""));
                //decimalPC = amountText.substring(3).contains(".") ? amountText.substring(3).split("\\.")[1].length() : 0;
                final CostType sur_tax_Cost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "Taxes", applicationCode, unitCount, 3, 1, 10, "", false);
                if (CompareUtil.isObjEmpty(chargetype)) {
                    sur_tax_Cost.setDescriptionRawText(chargeName);
                } else {
                    sur_tax_Cost.setDescriptionRawText(chargetype + "," + chargeName);
                }
                //the cost for drop charge has already been added to costList,so don't need add again
                /*if (sur_tax_Cost.getDescriptionRawText().toUpperCase().equals("DROP CHARGE")) {
                    continue;
                }*/
                carProduct.getCostList().getCost().add(sur_tax_Cost);
            }

        }
    }

    public void buildCarVehicleOptionList(CarProductType carproduct, List<Node> vehicleChargeList,DataSource scsDataSource) throws DataAccessException{
        final CarVehicleOptionListType carVehicleOptionList = new CarVehicleOptionListType();
        final List<CarVehicleOptionType> carVehicleOptionTypeList = new ArrayList<CarVehicleOptionType>();
        carVehicleOptionList.setCarVehicleOption(carVehicleOptionTypeList);
        carproduct.setCarVehicleOptionList(carVehicleOptionList);
        for (final Node chargeNode : vehicleChargeList) {
            final String catagoryCode = chargeNode.getAttributes().getNamedItem("Category").getNodeValue();
            if ("Special".equals(catagoryCode)) {
                final String chargetype = chargeNode.getAttributes().getNamedItem("Type").getNodeValue();
                String applicationCode = null;
                int unitCount = 1;
                switch (chargetype) {
                    case "PerHour": {
                        applicationCode = "Hourly";
                        break;
                    }
                    case "PerDay": {
                        applicationCode = "Daily";
                        break;
                    }
                    case "PerWeek": {
                        applicationCode = "Weekly";
                        break;
                    }
                    case "PerMonth": {
                        applicationCode = "Monthly";
                        break;
                    }
                    case "Percent": {
                        applicationCode = "Percentage";
                        break;
                    }
                    case "NoCharge": {
                        applicationCode = "Included";
                        unitCount = 1;
                        break;
                    }
                    default: {
                        applicationCode = "Trip";
                        break;
                    }

                }
                final String currencyCode = PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent().substring(0, 3);
                final double vehicleCharge = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeNode, "Amount").getTextContent().substring(3));
                final String chargeName = chargeNode.getAttributes().getNamedItem("Name").getNodeValue();
                final int amount = (int) (vehicleCharge * Math.pow(10, 2));
                final int decimalPC = 2;
                final CostType equipmentCost = UAPICommonNodeReader.buildCost(amount, decimalPC, currencyCode, "SpecialEquipment", applicationCode, unitCount, 8, 1, 21, "", false);
                equipmentCost.setDescriptionRawText(chargetype + "," + chargeName);
                final CarVehicleOptionType vehicle = new CarVehicleOptionType();
                vehicle.setCarVehicleOptionCategoryCode("special equipment");
                final String sepecialEquipmentCode = UAPICommonNodeReader.readDomainValue(scsDataSource, 0, UAPICommonNodeReader.uapiMessageSystemID, CommonConstantManager.DomainType.CAR_SPECIAL_EQUIPMENT, "", chargeName);
                vehicle.setCarSpecialEquipmentCode(sepecialEquipmentCode);
                vehicle.setDescriptionRawText(chargeName);
                vehicle.setCost(equipmentCost);
                carVehicleOptionTypeList.add(vehicle);
            }
        }
    }

    public void buildCarMileage(CarProductType carproduct, Node vehicleRateNode, Node supplierRateNode) {
        boolean unlimitedBool = false;
        if (!CompareUtil.isObjEmpty(vehicleRateNode.getAttributes().getNamedItem("UnlimitedMileage"))) {
            unlimitedBool = Boolean.parseBoolean(vehicleRateNode.getAttributes().getNamedItem("UnlimitedMileage").getNodeValue());
        }
        if (CompareUtil.isObjEmpty(carproduct.getCarMileage())) {
            final CarMileageType carMileage = new CarMileageType();
            final DistanceType distance = new DistanceType();
            final CostPerDistanceType costPerDistance = new CostPerDistanceType();
            final DistanceType costDistance = new DistanceType();
            final CurrencyAmountType currencyAmount = new CurrencyAmountType();
            final AmountType amount = new AmountType();
            costPerDistance.setDistance(costDistance);
            currencyAmount.setAmount(amount);
            costPerDistance.setCostCurrencyAmount(currencyAmount);
            carMileage.setFreeDistance(distance);
            carMileage.setExtraCostPerDistance(costPerDistance);
            carproduct.setCarMileage(carMileage);

        }
        if (unlimitedBool) {
            carproduct.getCarMileage().getFreeDistance().setDistanceUnitCount(-1);
            carproduct.getCarMileage().setExtraCostPerDistance(null);
        } else {
            final String mileageUnit = vehicleRateNode.getAttributes().getNamedItem("Units").getNodeValue();
            final String muleageAllwoance = vehicleRateNode.getAttributes().getNamedItem("MileageAllowance").getNodeValue();
            carproduct.getCarMileage().getFreeDistance().setDistanceUnitCount(Integer.parseInt(muleageAllwoance));
            carproduct.getCarMileage().getFreeDistance().setDistanceUnit(mileageUnit);
            //
            carproduct.getCarMileage().getExtraCostPerDistance().getDistance().setDistanceUnitCount(-1);
            carproduct.getCarMileage().getExtraCostPerDistance().getDistance().setDistanceUnit(mileageUnit);


            final String currencyCode = supplierRateNode.getAttributes().getNamedItem("ExtraMileageCharge").getNodeValue().substring(0, 3);
            final String mileageCharge = supplierRateNode.getAttributes().getNamedItem("ExtraMileageCharge").getNodeValue().substring(3);
            final int decimalPC = mileageCharge.contains(".") ? mileageCharge.split(".")[1].length() : 0;
            final int amountDecimal = (int) (Double.parseDouble(mileageCharge) * Math.pow(10, decimalPC));
            //int decimalPC = mileageCharge.ToString().Contains(".") ? mileageCharge.ToString().Split(new char[] { '.' })[1].Length : 0;
            carproduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimal(amountDecimal);
            carproduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().getAmount().setDecimalPlaceCount(decimalPC);
            carproduct.getCarMileage().getExtraCostPerDistance().getCostCurrencyAmount().setCurrencyCode(currencyCode);
        }
    }

    public static List<String> regexGetTheNumberListFromString(String xmlString) {
        final List<String> numberList = new ArrayList<String>();
        final Pattern pattern = Pattern.compile("(?<tempname>\\d{4})");
        final Matcher matcher = pattern.matcher(xmlString);
        while (matcher.find()) {
            numberList.add(matcher.group());
        }
        return numberList;
    }

    private void buildCarPickAndDropLocationTimeRange(TimeRangeType timeRange, DateTime date, String arrivalTime, String departureTime) {
        final int hours = Integer.parseInt(arrivalTime.substring(0, 2));
        final int minutes = Integer.parseInt(arrivalTime.substring(2, 4));
        int hours2 = Integer.parseInt(departureTime.substring(0, 2));
        int minutes2 = Integer.parseInt(departureTime.substring(2, 4));
        if (hours2 == 24) {
            hours2 = 23;
            minutes2 = 59;
        }
        final DateTime minTime = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), hours, minutes, 0, 0);
        final DateTime maxTime = DateTime.getInstanceByDateTime(date.getYear(), date.getMonth(), date.getDay(), hours2, minutes2, 0, 0);
        timeRange.setMinTime(minTime);
        timeRange.setMaxTime(maxTime);
    }

    @SuppressWarnings("CPD-END")
    public CarProductType getCarProduct() {
        return carProduct;
    }

    public void setCarProduct(CarProductType carProduct) {
        this.carProduct = carProduct;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getTotalMandaryCharge() {
        return totalMandaryCharge;
    }

    public void setTotalMandaryCharge(String totalMandaryCharge) {
        this.totalMandaryCharge = totalMandaryCharge;
    }

    public boolean isCorporateRate() {
        return corporateRate;
    }

    public void setCorporateRate(boolean corporateRate) {
        this.corporateRate = corporateRate;
    }

    public String getDiscountNumberApplied() {
        return discountNumberApplied;
    }

    public void setDiscountNumberApplied(String discountNumberApplied) {
        this.discountNumberApplied = discountNumberApplied;
    }
}
