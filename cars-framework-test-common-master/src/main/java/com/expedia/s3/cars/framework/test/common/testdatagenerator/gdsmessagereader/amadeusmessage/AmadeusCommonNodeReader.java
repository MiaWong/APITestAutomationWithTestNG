package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateDetailType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.CostPriceListType;
import com.expedia.e3.data.financetypes.defn.v4.CostPriceType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.CurrencyAmountType;
import com.expedia.e3.data.financetypes.defn.v4.LegacyFinanceKeyType;
import com.expedia.e3.data.financetypes.defn.v4.MultiplierOrAmountType;
import com.expedia.e3.data.persontypes.defn.v4.AgeType;
import com.expedia.e3.data.persontypes.defn.v4.ContactInformationType;
import com.expedia.e3.data.persontypes.defn.v4.PersonNameType;
import com.expedia.e3.data.persontypes.defn.v4.PersonType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.timetypes.defn.v4.DateRangeType;
import com.expedia.e3.data.timetypes.defn.v4.OpenScheduleType;
import com.expedia.e3.data.timetypes.defn.v4.RecurringPeriodListType;
import com.expedia.e3.data.timetypes.defn.v4.RecurringPeriodType;
import com.expedia.e3.data.timetypes.defn.v4.TimeRangeListType;
import com.expedia.e3.data.timetypes.defn.v4.TimeRangeType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.VehMakeModel;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miawang on 1/16/2017.
 */
@SuppressWarnings("PMD")
public class AmadeusCommonNodeReader {
    //build location key in CarPickupAndDropOffLocation
    public void buildCarPickupAndDropOffLocationKey(CarProductType carproduct, boolean isDropOffLocation) {
        if (null == carproduct.getCarPickupLocation()) {
            carproduct.setCarPickupLocation(new CarLocationType());
        }
        if (null == carproduct.getCarDropOffLocation()) {
            carproduct.setCarDropOffLocation(new CarLocationType());
        }

        CarLocationType carLocation = carproduct.getCarPickupLocation();
        if (isDropOffLocation) {
            carLocation = carproduct.getCarDropOffLocation();
        }

        if(null != carproduct.getCarInventoryKey() && null != carproduct.getCarInventoryKey().getCarCatalogKey())
        {
            CarLocationKeyType carPickUpDropOffLocationKey = carproduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
            if (isDropOffLocation)
            {
                carPickUpDropOffLocationKey = carproduct.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
            }

            carLocation.setCarLocationKey(carPickUpDropOffLocationKey);
        }
        carLocation.setCarShuttleCategoryCode("");
    }

    public void buildPickUpDropOffAddress(CarLocationType pickUpDropOffLocation, Node pickUpDropOffLocationNode)
    {
        final Node addressNode = PojoXmlUtil.getNodeByTagName(pickUpDropOffLocationNode, "address");

        if (null != addressNode)
        {
            if (null == pickUpDropOffLocation.getAddress())
            {
                pickUpDropOffLocation.setAddress(new AddressType());
            }

            pickUpDropOffLocation.getAddress().setCityName(PojoXmlUtil.getNodeByTagName(addressNode, "city").getTextContent());
            pickUpDropOffLocation.getAddress().setCountryAlpha3Code(PojoXmlUtil.getNodeByTagName(addressNode, "countryCode").getTextContent());
            pickUpDropOffLocation.getAddress().setFirstAddressLine(PojoXmlUtil.getNodeByTagName(addressNode, "line1").getTextContent());

            final Node zipCodeNode = PojoXmlUtil.getNodeByTagName(addressNode, "zipCode");
            if (null != zipCodeNode)
            {
                pickUpDropOffLocation.getAddress().setPostalCode(zipCodeNode.getTextContent());
            }


            final Node line2Node = PojoXmlUtil.getNodeByTagName(addressNode, "line2");
            if (null != line2Node)
            {
                pickUpDropOffLocation.getAddress().setSecondAddressLine(line2Node.getTextContent());
            }
        }
    }

    public void buildPickUpDropOffRecurringPeriod(CarLocationType pickUpDropOffLocation,
                                                  CarInventoryKeyType inventoryKey, Node pickUpDropOffLocationNode, boolean isDropOffLocation) {
        if (null == pickUpDropOffLocation.getOpenSchedule()) {
            pickUpDropOffLocation.setOpenSchedule(new OpenScheduleType());
        }
        if (null == pickUpDropOffLocation.getOpenSchedule().getNormalRecurringPeriodList()) {
            pickUpDropOffLocation.getOpenSchedule().setNormalRecurringPeriodList(new RecurringPeriodListType());
        }
        if (null == pickUpDropOffLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod()) {
            pickUpDropOffLocation.getOpenSchedule().getNormalRecurringPeriodList().setRecurringPeriod(new ArrayList<>());
        }

        final List<Node> openHoursList = PojoXmlUtil.getNodesByTagName(pickUpDropOffLocationNode, "openingHours");
        for (final Node openHours : openHoursList) {
            if (openHours.getChildNodes().getLength() == 3) {
                final RecurringPeriodType recurringPeriod = new RecurringPeriodType();

                DateTime date = inventoryKey.getCarPickUpDateTime();
                if (isDropOffLocation) {
                    date = inventoryKey.getCarDropOffDateTime();
                }

                final DateRangeType dateRange = new DateRangeType();
                dateRange.setMinDate(DateTime.getInstanceByDate(date.getYear(), date.getMonth(), date.getDay()));
                dateRange.setMaxDate(DateTime.getInstanceByDate(date.getYear(), date.getMonth(), date.getDay()));


                final String hours = PojoXmlUtil.getNodeByTagName(openHours.getChildNodes().item(1), "hour").getTextContent();
                final String minutes = PojoXmlUtil.getNodeByTagName(openHours.getChildNodes().item(1), "minutes").getTextContent();
                String hoursMax = PojoXmlUtil.getNodeByTagName(openHours.getChildNodes().item(2), "hour").getTextContent();
                String minutesMax = PojoXmlUtil.getNodeByTagName(openHours.getChildNodes().item(2), "minutes").getTextContent();
                if (null != hoursMax && hoursMax.equals("24")) {
                    hoursMax = "23";
                    minutesMax = "59";
                }

                final DateTime dt = DateTime.getInstanceByTime(Integer.parseInt(hours), Integer.parseInt(minutes), 0, 0);
                final DateTime dtMax = DateTime.getInstanceByTime(Integer.parseInt(hoursMax), Integer.parseInt(minutesMax), 0, 0);
                final TimeRangeType timeRange = new TimeRangeType();
                timeRange.setMinTime(dt);
                timeRange.setMaxTime(dtMax);
                recurringPeriod.setDateRange(dateRange);
                recurringPeriod.setTimeRangeList(new TimeRangeListType());
                recurringPeriod.getTimeRangeList().setTimeRange(new ArrayList<>());
                recurringPeriod.getTimeRangeList().getTimeRange().add(timeRange);

                pickUpDropOffLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(recurringPeriod);
                break;
            }
        }
    }

    // CarPickupLocation
    public void buildPickupAndDropOffLocationPhoneList(CarProductType car, Node rateDetails) {
        //Get pickupDropoffLocation
        final List<Node> locationList = PojoXmlUtil.getNodesByTagName(rateDetails, "pickupDropoffLocation");
        //Get PickupLocation
        Node pickLocationNode = null;
        Node dropLocationNode = null;
        for (final Node location : locationList) {
            //get locationType
            final String locationType = PojoXmlUtil.getNodeByTagName(location, "locationType").getTextContent();
            if (locationType.equals(CommonConstantManager.PickupOrDropoffType.PICKUP_TYPE)) {
                pickLocationNode = location;
                if (null == car.getCarPickupLocation()) {
                    car.setCarPickupLocation(new CarLocationType());
                }
                //Read the phone info from nodeList to carpickupLocation
                car.getCarPickupLocation().setPhoneList(getPhoneList(pickLocationNode));
            } else if (locationType.equals(CommonConstantManager.PickupOrDropoffType.DROPOFF_TYPE)) {
                dropLocationNode = location;
                if (null == car.getCarDropOffLocation()) {
                    car.setCarDropOffLocation(new CarLocationType());
                }
                //Read the phone info from nodeList to carpickupLocation
                car.getCarDropOffLocation().setPhoneList(getPhoneList(dropLocationNode));
            }
        }
    }

    //Phone
    private PhoneListType getPhoneList(Node locationNode) {
        //Get the phone nodes from pickLocationNode
        List<Node> phoneNodeList = null;
        if (locationNode != null) {
            phoneNodeList = PojoXmlUtil.getNodesByTagName(locationNode, "phone");
        }

        //Read the phone info from nodeList to carpickupLocation
        final PhoneListType result = new PhoneListType();
        if (phoneNodeList == null) {
            return result;
        }

       /*  when config mapPhoneCategoryCodeFromGDS/enable enable
       MaseratiPhoneCategoryCode
        Expedia domain values for Phone category:
        0  unknown
        1	Home
        2	Business
        6	Fax
        10 Mobile*/
        buildPhoneListIfPopulatePhoneCategoryCode(result, phoneNodeList);

        //this is for mapPhoneCategoryCodeFromGDS/enable disable
        // buildPhoneList(result, phoneNodeList);
        return result;
    }

    private void buildPhoneList(PhoneListType result, List<Node> phoneNodeList) {
        for (final Node phoneNode : phoneNodeList) {
            //Get the phoneOrEmailType because we only map the PHO - phone number
            final String phoneOrEmailType = PojoXmlUtil.getNodeByTagName(phoneNode, "phoneOrEmailType").getTextContent().toString();
            //Only read the PHO which has phone number
            if (phoneOrEmailType.equals("PHO") && PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent() != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent().length() > 0) {
                final PhoneType phone = new PhoneType();
                phone.setPhoneNumber(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent());
                //Only get one phone
                if (null == result.getPhone()) {
                    result.setPhone(new ArrayList<>());
                }
                if (result.getPhone().size() == 0) {
                    result.getPhone().add(phone);
                    break;
                }
            }
        }
    }

    private void buildPhoneListIfPopulatePhoneCategoryCode(PhoneListType result, List<Node> phoneNodeList) {
        String phoneNumber = "";
        String faxNumber = "";
        for (final Node phoneNode : phoneNodeList) {

            //Get the phoneOrEmailType
            final String phoneOrEmail = PojoXmlUtil.getNodeByTagName(phoneNode, "phoneOrEmailType").getTextContent().toString();
            if (phoneOrEmail.equals("PHO") && PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent() != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent().length() > 0)
            {

                 phoneNumber = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent();
            }

            if (phoneOrEmail.equals("FAX") && PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber") != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent() != null
                    && PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent().length() > 0)
            {

                faxNumber = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(phoneNode, "telephoneNumber"), "telephoneNumber").getTextContent();
            }

        }

        final List<PhoneType> phoneTypes = new ArrayList<>();
        result.setPhone(phoneTypes);

        if (StringUtil.isNotBlank(phoneNumber))
        {
            final PhoneType phone = new PhoneType();
            phoneTypes.add(phone);
            phone.setPhoneCategoryCode("0");
            phone.setPhoneNumber(phoneNumber);
        }
        if (StringUtil.isNotBlank(faxNumber))
        {
            final PhoneType phone = new PhoneType();
            phoneTypes.add(phone);
            phone.setPhoneCategoryCode("6");
            phone.setPhoneNumber(faxNumber);
        }

    }
    /// for sepecail equipment
    public void buildCarVehicleOption(CarProductType carProduct, Node rateDetailsNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final List<Node> specialEqGroupNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        if (null == carProduct.getCarVehicleOptionList()) {
            carProduct.setCarVehicleOptionList(new CarVehicleOptionListType());
        }
        if (null == carProduct.getCarVehicleOptionList().getCarVehicleOption()) {
            carProduct.getCarVehicleOptionList().setCarVehicleOption(new ArrayList<>());
        }
        for (final Node taxCovSurchargeGroup : specialEqGroupNodeList) {
            final Node chargeDetails = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");
            final String type = PojoXmlUtil.getNodeByTagName(chargeDetails, "type").getTextContent();

            if (null != type && (type.equals("013") || type.equals("13"))) {
                //TODO Mia reserve have this logic, not sure if this is useful or not.
//                String description = chargeDetails["description"].InnerText;
//                double amount_decimal = 0.00;
//                if (description.Equals("IES") || description.Equals("IBR"))
//                {
                final CarVehicleOptionType vehicle = new CarVehicleOptionType();
                vehicle.setCarVehicleOptionCategoryCode("special equipment");
                final String externalCarSpecialEquipmentCode = PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent().substring(0,3);
                vehicle.setCarSpecialEquipmentCode(scsDataSource.getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_SPECIAL_EQUIPMENT, null, externalCarSpecialEquipmentCode).get(0).getDomainValue());

                vehicle.setAvailStatusCode("A");
                vehicle.setDescriptionRawText(PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent());
                vehicle.setCost(new CostType());

                final List<Node> chargeAmountNodes = PojoXmlUtil.getNodesByTagName(taxCovSurchargeGroup, "chargeDetails");
                Node chargeAmountNode = null;
                if(CollectionUtils.isNotEmpty(chargeAmountNodes) && chargeAmountNodes.size()>1)
                {
                    chargeAmountNode = chargeAmountNodes.get(1);
                }

                if(null != chargeAmountNode)
                {
                    buildVehicle(chargeDetails, vehicle, chargeAmountNode, false);

                    carProduct.getCarVehicleOptionList().getCarVehicleOption().add(vehicle);
                }
            }
        }
    }

    public void buildSpecialEquipmentForReserve(CarReservationType carReservationType, Node rateDetailsNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final List<Node> specialEqGroupNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        final CarSpecialEquipmentListType carSpecialEquipmentList = new CarSpecialEquipmentListType();
        List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
        carSpecialEquipmentList.setCarSpecialEquipment(carSpecialEquipmentTypes);
        carReservationType.setCarSpecialEquipmentList(carSpecialEquipmentList);

        for (final Node taxCovSurchargeGroup : specialEqGroupNodeList)
        {
            final Node chargeDetails = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");
            final String type = null == PojoXmlUtil.getNodeByTagName(chargeDetails, "type") ? null :PojoXmlUtil.getNodeByTagName(chargeDetails, "type").getTextContent();
            final String description = null == PojoXmlUtil.getNodeByTagName(chargeDetails, "description") ? null :PojoXmlUtil.getNodeByTagName(chargeDetails, "description").getTextContent();

            if (null != type && type.equals("013") && null != description
                    && (description.equals("IES") || description.equals("IBR") || description.equals("NBR")
                    || description.equals("CNF") || description.equals("REQ") || description.equals("ONR") || description.equals("NAV")))
            {
                buildSpeciaEquip(scsDataSource, carSpecialEquipmentTypes, chargeDetails, description);
            }
        }
    }

    /*
     * get logic from Xslt(it is strange why the logic is different from reserve)
     */
    public void buildSpecialEquipmentForGetReservation(CarReservationType carReservationType, Node rateDetailsNode, CarsSCSDataSource scsDataSource) throws DataAccessException
    {
        final List<Node> specialEqGroupNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        final CarSpecialEquipmentListType carSpecialEquipmentListType = new CarSpecialEquipmentListType();
        List<CarSpecialEquipmentType> carSpecialEquipmentTypes = new ArrayList<>();
        carSpecialEquipmentListType.setCarSpecialEquipment(carSpecialEquipmentTypes);
        carReservationType.setCarSpecialEquipmentList(carSpecialEquipmentListType);

        for (final Node taxCovSurchargeGroup : specialEqGroupNodeList)
        {
            final Node chargeDetails = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");
            final String type = PojoXmlUtil.getNodeByTagName(chargeDetails, "type").getTextContent();
            final String description = PojoXmlUtil.getNodeByTagName(chargeDetails, "description").getTextContent();

            if (null != type && type.equals("013"))
            {
                buildSpeciaEquip(scsDataSource, carSpecialEquipmentTypes, chargeDetails, description);
            }
        }
    }

    private void buildSpeciaEquip(CarsSCSDataSource scsDataSource, List<CarSpecialEquipmentType> carSpecialEquipmentTypes, Node chargeDetails, String description) throws DataAccessException
    {
        final CarSpecialEquipmentType carSpecialEquipmentType = new CarSpecialEquipmentType();
        carSpecialEquipmentTypes.add(carSpecialEquipmentType);

        String externalCarSpecialEquipmentCode = "";
        final String gdsCarSpecialEquipmentCode = PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent().substring(0, 3);
        List<ExternalSupplyServiceDomainValueMap> externalSupplyServiceDomainValueMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(0, 0, "CarSpecialEquipment", null, gdsCarSpecialEquipmentCode);
        if (CollectionUtils.isNotEmpty(externalSupplyServiceDomainValueMapList))
        {
            externalCarSpecialEquipmentCode = externalSupplyServiceDomainValueMapList.get(0).getDomainValue();
        }

        carSpecialEquipmentType.setCarSpecialEquipmentCode(externalCarSpecialEquipmentCode);

        if (description.equals("IES") || description.equals("IBR") || description.equals("NBR") || description.equals("CNF"))
        {
            carSpecialEquipmentType.setBookingStateCode("Booked");
        }
        else
        {
            carSpecialEquipmentType.setBookingStateCode("Unconfirmed");
        }
    }


    public void buildCarVehicleOptionForReserve(CarProductType carProduct, Node rateDetailsNode) throws DataAccessException {
        final List<Node> specialEqGroupNodeList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        if (null == carProduct.getCarVehicleOptionList())
        {
            carProduct.setCarVehicleOptionList(new CarVehicleOptionListType());
        }
        for (final Node taxCovSurchargeGroup : specialEqGroupNodeList) {
            final Node chargeDetails = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroup, "taxSurchargeCoverageInfo"), "chargeDetails");
            final String typeForChargeDetails = null == PojoXmlUtil.getNodeByTagName(chargeDetails, "type") ? null :PojoXmlUtil.getNodeByTagName(chargeDetails, "type").getTextContent();
            final String descriptionForChargeDetails = null == PojoXmlUtil.getNodeByTagName(chargeDetails, "description") ? null :PojoXmlUtil.getNodeByTagName(chargeDetails, "description").getTextContent();

            if (null != typeForChargeDetails && typeForChargeDetails.equals("013") && null != descriptionForChargeDetails
                    && (descriptionForChargeDetails.equals("IES") || descriptionForChargeDetails.equals("IBR")))
            {
                final CarVehicleOptionType vehicle = new CarVehicleOptionType();
                vehicle.setCarVehicleOptionCategoryCode("special equipment");
                final String externalCarSpecialEquipmentCode = PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent().substring(0,3);
                vehicle.setCarSpecialEquipmentCode(externalCarSpecialEquipmentCode);

                vehicle.setAvailStatusCode("A");
                vehicle.setDescriptionRawText(PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent());
                vehicle.setCost(new CostType());

                final List<Node> chargeAmountNodes = PojoXmlUtil.getNodesByTagName(taxCovSurchargeGroup, "chargeDetails");
                Node chargeAmountNode = null;
                if(CollectionUtils.isNotEmpty(chargeAmountNodes) && chargeAmountNodes.size()>1)
                {
                    chargeAmountNode = chargeAmountNodes.get(1);
                }

                if(null != chargeAmountNode)
                {
                    buildVehicle(chargeDetails, vehicle, chargeAmountNode, true);

                    if (null == carProduct.getCarVehicleOptionList().getCarVehicleOption())
                    {
                        carProduct.getCarVehicleOptionList().setCarVehicleOption(new ArrayList<>());
                    }
                    carProduct.getCarVehicleOptionList().getCarVehicleOption().add(vehicle);
                }
            }
        }
    }

    private void buildVehicle(Node chargeDetails, CarVehicleOptionType vehicle, Node chargeAmountNode, boolean isbooking)
    {
        vehicle.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
        vehicle.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
        vehicle.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());

        //final double amountDecimal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeAmountNode, "amount").getTextContent().replace(".", ""));
        vehicle.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(2);
        vehicle.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(Integer.parseInt(PojoXmlUtil.getNodeByTagName(chargeAmountNode, "amount").getTextContent().replace(".", "")));

        vehicle.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(PojoXmlUtil.getNodeByTagName(chargeAmountNode, "currency").getTextContent());

        final String periodType = PojoXmlUtil.getNodeByTagName(chargeAmountNode, "periodType").getTextContent();
        vehicle.getCost().setFinanceApplicationCode(ASCSGDSReaderUtil.getChargeDetailsPeriodType(Integer.parseInt(periodType)));
        vehicle.getCost().setFinanceApplicationUnitCount(1L);

        if(isbooking)
        {
            vehicle.getCost().setFinanceCategoryCode("Optional");
        }
        else
        {
            vehicle.getCost().setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Fee.getFinanceCategoryCode());
        }

        vehicle.getCost().setDescriptionRawText(PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent());

        vehicle.getCost().setLegacyFinanceKey(new LegacyFinanceKeyType());
        vehicle.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(0);
        vehicle.getCost().getLegacyFinanceKey().setLegacyMonetaryClassID(0);
        vehicle.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(0);
    }

    //--------------------------------------------------------------start---CostList-------------------------------------------------------------
    /**
     * CostList
     * @param car
     * @param ratesNode
     * @param ignoreZeroCost
     */
    public void buildCostList(CarProductType car, Node ratesNode, boolean ignoreZeroCost, boolean forDetails) {
        if (null == car.getCostList()) {
            car.setCostList(new CostListType());
        }

        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:chargeDetails[4]/:type
        final List<Node> rateDetailNodeList = PojoXmlUtil.getNodesByTagName(ratesNode, "rateDetail");
        if (rateDetailNodeList != null) {
            for (final Node rateDetail : rateDetailNodeList) {
                if(forDetails)
                {
                    buildCostListFromChargeDetailsNode(car.getCostList(), rateDetail, ignoreZeroCost);
                }

                buildCostListFromTariffInfoNode(car.getCostList(), car.getCarInventoryKey(), rateDetail, ignoreZeroCost);

            }
        }
        //TODO Mia this calculation is from intg framework, not work for 401448, so did some change by myself from 2018/4/2,
        // need to find out if it is right or wrong
       if(forDetails)
       {
           buildMiscCosts(car.getCostList().getCost());
       }
       else
       {

           //build MiscCost and MiscChargeDetailsCost for reserve
           buildMiscCostForReserve(car, rateDetailNodeList);
       }

        buildCostListLegacyFinanceKey(car.getCostList());
    }

    private void buildMiscCostForReserve(CarProductType car, List<Node> rateDetailNodeList)
    {
        BigDecimal accNonIncludedFeesAndTaxes = new BigDecimal("0");
        String baseRateTotal = "0";
        String totalRate = "0";
        String currency = "";
        for (final Node rateDetail : rateDetailNodeList)
        {
            //build MiscChargeDetailsCost for reserve
            if ("RP".equals(PojoXmlUtil.getNodeByTagName(rateDetail, "amountType").getTextContent()))
            {
                accNonIncludedFeesAndTaxes =  buildMiscChargeDetailsCostForReserveAndGetAccNonIncludedFeesAndTaxes(car, rateDetail);
                final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo");
                currency = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "currency").getTextContent();
            }

            if ("RB".equals(PojoXmlUtil.getNodeByTagName(rateDetail, "amountType").getTextContent()))
            {
                final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo");
                baseRateTotal = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "amount").getTextContent();

            }
            if ("904".equals(PojoXmlUtil.getNodeByTagName(rateDetail, "amountType").getTextContent()))
            {
                final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo");
                totalRate = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "amount").getTextContent();

            }
        }
            BigDecimal misc = new BigDecimal(new BigDecimal(totalRate).subtract(new BigDecimal(baseRateTotal)).toPlainString()).subtract(accNonIncludedFeesAndTaxes);
            if(misc.compareTo(new BigDecimal(0)) !=0)
            {
                final CostType cost = getMultiplierOrAmount(String.valueOf(misc), currency);
                LegacyFinanceKeyType legacyFinanceKeyType = getLegacyFinanceKeyType(6l
                        , 8l, 1l);
                cost.setDescriptionRawText("Misc Charges");
                cost.setFinanceCategoryCode("Misc");
                cost.setFinanceApplicationUnitCount(1l);
                cost.setFinanceApplicationCode("Trip");
                cost.setLegacyFinanceKey(legacyFinanceKeyType);
                car.getCostList().getCost().add(cost);
            }
    }

    private BigDecimal buildMiscChargeDetailsCostForReserveAndGetAccNonIncludedFeesAndTaxes(CarProductType car, Node rateDetail)
    {
        final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(rateDetail, "chargeDetails");
        BigDecimal accNonIncludedFeesAndTaxes = new BigDecimal("0");
        if (CollectionUtils.isNotEmpty(chargeDetails))
        {
            for (final Node chargeDetail : chargeDetails)
            {
                String financeCategoryCode = getFinanceCategoryCode(chargeDetail);
                if(StringUtil.isBlank(financeCategoryCode))
                {
                    continue;
                }

                String amount = getAmount(chargeDetail);
                if(StringUtil.isBlank(amount))
                {
                    continue;
                }
                String currency = getCurrency(rateDetail, chargeDetail);
                String descriptionRawText = getDescription(chargeDetail);
                String applicationCode = "Trip";
                Long legacyMonetaryCalculationID = 10l;
                Long legacyMonetaryClassID = 3l;
                Long legacyMonetaryCalculationSystemID = 1l;
                Long financeApplicationUnitCount = 1l;
                final CostType cost = getMultiplierOrAmount(amount, currency);
                LegacyFinanceKeyType legacyFinanceKeyType = getLegacyFinanceKeyType(legacyMonetaryCalculationID
                        , legacyMonetaryClassID, legacyMonetaryCalculationSystemID);
                cost.setDescriptionRawText(descriptionRawText);
                cost.setFinanceCategoryCode(financeCategoryCode);
                cost.setFinanceApplicationUnitCount(financeApplicationUnitCount);
                cost.setFinanceApplicationCode(applicationCode);
                cost.setLegacyFinanceKey(legacyFinanceKeyType);


                car.getCostList().getCost().add(cost);
                accNonIncludedFeesAndTaxes = accNonIncludedFeesAndTaxes.add(new BigDecimal(amount));
            }
        }
        return accNonIncludedFeesAndTaxes;
    }

    private LegacyFinanceKeyType getLegacyFinanceKeyType(Long legacyMonetaryCalculationID, Long legacyMonetaryClassID, Long legacyMonetaryCalculationSystemID)
    {
        LegacyFinanceKeyType legacyFinanceKeyType = new LegacyFinanceKeyType();
        legacyFinanceKeyType.setLegacyMonetaryCalculationID(legacyMonetaryCalculationID);
        legacyFinanceKeyType.setLegacyMonetaryCalculationSystemID(legacyMonetaryCalculationSystemID);
        legacyFinanceKeyType.setLegacyMonetaryClassID(legacyMonetaryClassID);
        return legacyFinanceKeyType;
    }

    private CostType getMultiplierOrAmount(String amount, String currency)
    {
        final CostType cost = new CostType();
        cost.setMultiplierOrAmount(new MultiplierOrAmountType());
        cost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
        buildCurrencyAmount(cost.getMultiplierOrAmount().getCurrencyAmount(), amount, currency);
        return cost;
    }

    private String getFinanceCategoryCode(Node chargeDetail)
    {
        String excludeFromCost = "XHK|XDM|206|XHM|216|207|218|217|81|081|80|080|9|009|82|082|NBD|XDK|NBH|219|32|032|31|031|79|079|8|008|34|034|33|033";
        String[] excludeFromCostArray = excludeFromCost.split("\\|");
        List<String> excludeFromCostList = Arrays.asList(excludeFromCostArray);
        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();
        if(excludeFromCostList.contains(type))
        {
            return "";
        }
        String financeCategoryCode = "";
        if ("108".equals(type))
        {
            financeCategoryCode = "Surcharge";
        }
        else if ("COV".equals(type))
        {
            financeCategoryCode = "Fee";
        }
        else if ("013".equals(type))
        {
            financeCategoryCode = "Fee";
        }
        else
        {
            financeCategoryCode = "Taxes";
        }
        return financeCategoryCode;
    }

    private String getDescription(Node chargeDetail)
    {
        final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "comment");
        String descriptionRawText = "";
        if (commentNode != null)
        {
            descriptionRawText = commentNode.getTextContent().trim();
        }
        return descriptionRawText;
    }

    private String getCurrency(Node rateDetail, Node chargeDetail)
    {
        Node currencyCodeNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "currency");
        String currency = "";
        if (currencyCodeNode == null)
        {
            currencyCodeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "currency");
            currency = currencyCodeNode.getTextContent().trim();
        }
        return currency;
    }

    private String getAmount(Node chargeDetail)
    {
        final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");
        if (null == amountNode)
        {
            return null;
        }
        return  amountNode.getTextContent().trim();
    }



    private void buildCostListFromChargeDetailsNode(CostListType costList, Node rateDetail, boolean ignoreZeroCost) {
        final List<Node> chargeDetails = PojoXmlUtil.getNodesByTagName(rateDetail, "chargeDetails");
        // ChargeDetails = 008 or 009, Extral hour or extral day.
        if (chargeDetails != null) {
            for (final Node chargeDetail : chargeDetails) {
                buildCostFromChargeDetailNode(costList, chargeDetail, rateDetail, ignoreZeroCost);
            }
        }
    }

    private void buildCostFromChargeDetailNode(CostListType costList, Node chargeDetail, Node rateDetail, boolean ignoreZeroCost)
    {
        final String type = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();
        final Node amountNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "amount");
        String amount = "0.00";

        //if (type != "008" && type != "009")
        //Include special equipment fee - type 013
        if (((null != amountNode) && (null != type) && (type.equals("045") || type.equals("108") || type.equals("113") || type.equals("013")))
                || (type.equals("COV")))
        {
            if (null != amountNode)
            {
                amount = amountNode.getTextContent().trim();
            }
            Node currencyCodeNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "currency");
            final Node commentNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "comment");

            ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[1]/:tariffInfo/:currency
            if (currencyCodeNode == null)
            {
                currencyCodeNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo"), "currency");
            }
            final CostType cost = new CostType();
            cost.setFinanceApplicationUnitCount(0L);
            //cost.FinanceApplicationUnitCount = 1;
            buildCostFormChargeDetailsChildNodeType(cost, type);
            if (null == cost.getMultiplierOrAmount())
            {
                cost.setMultiplierOrAmount(new MultiplierOrAmountType());
            }
            if (null == cost.getMultiplierOrAmount().getCurrencyAmount())
            {
                cost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
            }

            buildCurrencyAmount(cost.getMultiplierOrAmount().getCurrencyAmount(), amount, currencyCodeNode.getTextContent().trim());

            if (commentNode != null)
            {
                cost.setDescriptionRawText(commentNode.getTextContent().trim());
            }

            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(type));
            if(cost.getDescriptionRawText().equals(CommonEnumManager.CostDescriptionRawText.OneWayCharge.getDescriptionRawText()))
            {
                cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Fee.getFinanceCategoryCode());
            }

            if (null == costList.getCost())
            {
                costList.setCost(new ArrayList<>());
            }

            if (cost != null && (!ignoreZeroCost || (Double.parseDouble(amount) > 0 && ignoreZeroCost)) && cost.getFinanceApplicationUnitCount() > 0)
            {
                costList.getCost().add(cost);
            }
        }

    }

    private void buildCostFormChargeDetailsChildNodeType(CostType cost, String type) {
        if (null != type && type.equals("008")) {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.ExtraDaily.getFinanceApplicationCode());
            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(type));
            cost.setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.ExtraDayCharge.getDescriptionRawText());
        } else if (null != type && type.equals("009")) {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.ExtraHourly.getFinanceApplicationCode());
            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(type));
            cost.setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.ExtraHourCharge.getDescriptionRawText());
        } else if (null != type) {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode());
            ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(type);
        }
    }

    private void buildCostListFromTariffInfoNode(CostListType costList, CarInventoryKeyType inventory, Node rateDetail, boolean ignoreZeroCost)
    {
        //TariffInfo.
        ///:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[2]/:tariffInfo
        ////:AmadeusSessionManagerResponse/:RawAmadeusXml/:Car_RateInformationFromCarSegmentReply/:rateDetails/:rateDetail[2]/:tariffInfo/:amount


        final Node tariffInfoNode = PojoXmlUtil.getNodeByTagName(rateDetail, "tariffInfo");

        String amountTypeTar = "";
        final Node amountTypeTarNode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "amountType");
        if (amountTypeTarNode != null)
        {
            amountTypeTar = amountTypeTarNode.getTextContent().trim();
        }

        String amountTar = "";
        String currencyTar = "";
        String ratePlanIndicator = "";
        final Node amountTarNode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "amount");
        final Node currencyTarNode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "currency");
        final Node ratePlanIndicatorNode = PojoXmlUtil.getNodeByTagName(tariffInfoNode, "ratePlanIndicator");
        if (amountTarNode != null)
        {
            amountTar = amountTarNode.getTextContent().trim();
        }
        if (currencyTarNode != null)
        {
            currencyTar = currencyTarNode.getTextContent().trim();
        }
        if (ratePlanIndicatorNode != null)
        {
            ratePlanIndicator = ratePlanIndicatorNode.getTextContent().trim();
        }

        if ((null != amountTypeTar) && (amountTypeTar.equals("904") || amountTypeTar.equals("RB")))
        {
            final CostType costTar = new CostType();
            costTar.setFinanceApplicationUnitCount(0L);
            setFinanceOfCostForTariffInfo(costTar, amountTypeTar, ratePlanIndicator);

            costTar.setMultiplierOrAmount(new MultiplierOrAmountType());
            costTar.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());

            buildCurrencyAmount(costTar.getMultiplierOrAmount().getCurrencyAmount(), amountTar, currencyTar);

            if (null == costList.getCost())
            {
                costList.setCost(new ArrayList<>());
            }

            if ((costTar != null) && (!ignoreZeroCost || (Double.parseDouble(amountTar) > 0 && ignoreZeroCost)))
            {
                costList.getCost().add(costTar);
            }
        } else if (null != amountTypeTar && amountTypeTar.equals("RP"))
        {

            //TODO Mia this is not from old framework, so need to find out if it work or not
            inventory.getCarRate().setRatePeriodCode(ASCSGDSReaderUtil.getFinanceApplicationCodeByRatePlanIndicator(ratePlanIndicator));
        }
    }

    private void setFinanceOfCostForTariffInfo(CostType cost, String amountType, String ratePlanIndicator)
    {
        if (amountType != null && amountType.equals("RB"))
        {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(amountType));
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode());
            cost.setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.BaseRateTotal.getDescriptionRawText());
        } else if (amountType != null && amountType.equals("904"))
        {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(amountType));
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Total.getFinanceApplicationCode());
            cost.setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.EstimatedTotalAmount.getDescriptionRawText());
        } else if (amountType != null && amountType.equals("RP"))
        {
            cost.setFinanceApplicationUnitCount(1L);
            cost.setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(amountType));
            cost.setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.Base.getDescriptionRawText());
            if (null != ratePlanIndicator)
            {
                cost.setFinanceApplicationCode(ASCSGDSReaderUtil.getFinanceApplicationCodeByRatePlanIndicator(ratePlanIndicator));
            }
        }
    }
    //--------------------------------------------------------------end---CostList-------------------------------------------------------------

    /**
     * @param carInventoryKey
     * @param ratesNode       detail is rateDetails Node, search is ratesNode
     * @param scsDataSource
     * @param isSearch
     * @throws DataAccessException
     */
    public void readCarVehicle(CarInventoryKeyType carInventoryKey, Node ratesNode, CarsSCSDataSource scsDataSource, boolean isSearch) throws DataAccessException {
        Node vehicleRentalPrefTypeNode = PojoXmlUtil.getNodeByTagName(ratesNode, "vehicleRentalPrefType");
        if (isSearch) {
            final Node vehicleTypeInfoNodeList = PojoXmlUtil.getNodeByTagName(ratesNode, "vehicleTypeInfo");
            if(null != vehicleTypeInfoNodeList)
            {
                vehicleRentalPrefTypeNode = PojoXmlUtil.getNodeByTagName(vehicleTypeInfoNodeList, "vehicleRentalPrefType");
            }
        }

        if (vehicleRentalPrefTypeNode != null) {
            final String externalSIPP = vehicleRentalPrefTypeNode.getTextContent().trim();

            if (externalSIPP.length() == 4) {
                final VehMakeModel sippObj = new VehMakeModel(externalSIPP.substring(0, 1), externalSIPP.substring(1, 2), externalSIPP.substring(2, 3),
                        externalSIPP.substring(3), true);
                GDSMsgReadHelper.readVehMakeModel(carInventoryKey, scsDataSource, sippObj);
            }
        }
    }

    public String readCurrencyCode(Node requestNode) throws DataAccessException {
        String currencyCode = "";
        final Node currencyNode = PojoXmlUtil.getNodeByTagName(requestNode, "currency");
        final Node currencyIsoCodeNode = PojoXmlUtil.getNodeByTagName(requestNode, "currencyIsoCode");

        if (currencyIsoCodeNode != null) {
            currencyCode = currencyIsoCodeNode.getTextContent().trim();
        }
        else if (currencyNode != null) {
            currencyCode = currencyNode.getTextContent().trim();
        }

        return currencyCode;
    }

    /**
     * @param carInventoryKey
     * @param ratesNode
     * @param nodeTagName
     */
    public void readCarPickupAndDropOffLocationKey(CarInventoryKeyType carInventoryKey, Node ratesNode, String nodeTagName) {
        final List<Node> pickupDropoffLocationsNodeList = PojoXmlUtil.getNodesByTagName(ratesNode, nodeTagName);
        if (!pickupDropoffLocationsNodeList.isEmpty()) {
            final Node pickupLocationNameNode = PojoXmlUtil.getNodeByTagName(pickupDropoffLocationsNodeList.get(0), "name");

            if(null == carInventoryKey.getCarCatalogKey())
            {
                carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
            }

            if (null == carInventoryKey.getCarCatalogKey().getCarPickupLocationKey()) {
                carInventoryKey.getCarCatalogKey().setCarPickupLocationKey(new CarLocationKeyType());
            }

            buildCarLocationKey(pickupLocationNameNode, carInventoryKey.getCarCatalogKey().getCarPickupLocationKey());

            Node dropOffLocationNameNode = pickupLocationNameNode;
            if (pickupDropoffLocationsNodeList.size() > 1) {
                dropOffLocationNameNode = PojoXmlUtil.getNodeByTagName(pickupDropoffLocationsNodeList.get(1), "name");
            }

            if (null == carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey()) {
                carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(new CarLocationKeyType());
            }

            buildCarLocationKey(dropOffLocationNameNode, carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey());
        }
    }

    private void buildCarLocationKey(Node locationNameNode, CarLocationKeyType locationKey) {
        if (null != locationNameNode && !StringUtils.isEmpty(locationNameNode.getTextContent())) {
            locationKey.setLocationCode(locationNameNode.getTextContent().substring(0, 3));
            if (locationNameNode.getTextContent().length() > 3) {
                locationKey.setCarLocationCategoryCode(locationNameNode.getTextContent().substring(3, 4));
                buildLocationSupplierRawText(locationNameNode, locationKey);
            }
        }
    }

    private void buildLocationSupplierRawText(Node locationNameNode, CarLocationKeyType locationKey) {
        locationKey.setSupplierRawText(locationNameNode.getTextContent().substring(4));
        if (locationKey.getSupplierRawText().length() == 2) {
            locationKey.setSupplierRawText("0" + locationKey.getSupplierRawText());
        }
    }

    public void readCarPickUpAndDropOffDateTime(CarInventoryKeyType carInventoryKey, Node pickupDropoffTimeNode) {
        // 5.Get CarPickUpDateTime (For example: 2012-11-24T12:00:00)
        readCarPickUpDateTime(carInventoryKey, pickupDropoffTimeNode);

        // 6.Get CarDropOffDateTime
        readCarDropOffDateTime(carInventoryKey, pickupDropoffTimeNode);
    }

    private void readCarPickUpDateTime(CarInventoryKeyType carInventoryKey, Node timeNode) {
        carInventoryKey.setCarPickUpDateTime(buildPickUpDropOffTime(timeNode, "beginDateTime"));
    }

    private void readCarDropOffDateTime(CarInventoryKeyType carInventoryKey, Node timeNode) {
        carInventoryKey.setCarDropOffDateTime(buildPickUpDropOffTime(timeNode, "endDateTime"));
    }

    private DateTime buildPickUpDropOffTime(Node timeNode, String timeNodeTagName)
    {
        final Node dateTimeNode = PojoXmlUtil.getNodeByTagName(timeNode, timeNodeTagName);
        if (null != dateTimeNode)
        {
            final String yearStr = PojoXmlUtil.getNodeByTagName(dateTimeNode, "year").getTextContent();
            final String monthStr = PojoXmlUtil.getNodeByTagName(dateTimeNode, "month").getTextContent();
            final String dayStr = PojoXmlUtil.getNodeByTagName(dateTimeNode, "day").getTextContent();
            final String hourStr = PojoXmlUtil.getNodeByTagName(dateTimeNode, "hour").getTextContent();
            final String minuteStr = PojoXmlUtil.getNodeByTagName(dateTimeNode, "minutes").getTextContent();

            return DateTime.getInstanceByDateTime(
                    Integer.parseInt(yearStr),
                    Integer.parseInt(monthStr),
                    Integer.parseInt(dayStr),
                    Integer.parseInt(hourStr),
                    Integer.parseInt(minuteStr), 0, 0);
        }
        return null;
    }

    public void readRateCodeAndCorporateDiscountCode(CarInventoryKeyType carInventoryKey, Node ratesNode, String rcCdCodeParentNodeTagName) {
        if(null != ratesNode)
        {
            final Node loyaltyNumbersNodeList = PojoXmlUtil.getNodeByTagName(ratesNode, rcCdCodeParentNodeTagName);
            if(null != loyaltyNumbersNodeList)
            {
                final List<Node> referenceQualifierNodeList = PojoXmlUtil.getNodesByTagName(loyaltyNumbersNodeList, "referenceQualifier");

                if (null != referenceQualifierNodeList)
                {
                    final List<Node> referenceNumberNodes = PojoXmlUtil.getNodesByTagName(loyaltyNumbersNodeList, "referenceNumber");

                    if(null != referenceNumberNodes && referenceQualifierNodeList.size() == referenceNumberNodes.size())
                    {
                        if (null == carInventoryKey.getCarRate())
                        {
                            carInventoryKey.setCarRate(new CarRateType());
                        }

                        for (int count = 0; count < referenceQualifierNodeList.size(); count++)
                        {
                            if (referenceQualifierNodeList.get(count).getTextContent().equals("RC"))
                            {
                                carInventoryKey.getCarRate().setRateCode(referenceNumberNodes.get(count).getTextContent());

                            }

                            if (referenceQualifierNodeList.get(count).getTextContent().equals("CD"))
                            {
                                carInventoryKey.getCarRate().setCorporateDiscountCode(referenceNumberNodes.get(count)
                                        .getTextContent());
                            }
                        }
                    }
                }
            }
        }
    }

    public void readRateCategoryCode(CarInventoryKeyType carInventoryKey, Node ratesNode, String rateCategoryParentNodeTagName, String rateCategoryNodeTagName) {
        final Node rateDetailsInfoNodeList = PojoXmlUtil.getNodeByTagName(ratesNode, rateCategoryParentNodeTagName);
        final Node rateCategory = PojoXmlUtil.getNodeByTagName(rateDetailsInfoNodeList, rateCategoryNodeTagName);
        final String rateCatagoryCode = rateCategory.getTextContent();

        if (null == carInventoryKey.getCarRate()) {
            carInventoryKey.setCarRate(new CarRateType());
        }
        if (!CompareUtil.isObjEmpty(ASCSGDSReaderUtil.getRateCategoryCode(rateCatagoryCode))) {
            carInventoryKey.getCarRate().setRateCategoryCode(ASCSGDSReaderUtil.getRateCategoryCode(rateCatagoryCode));
        }
    }

    public void readCarDoorCount(CarCatalogMakeModelType carMakeModel, Node vehicleInformationNode) {
        final List<Node> vehicleInfoNodeList = PojoXmlUtil.getNodesByTagName(vehicleInformationNode, "vehicleInfo");
        for (Node vehicleInfoNode : vehicleInfoNodeList) {
            final Node qualifierNode = PojoXmlUtil.getNodeByTagName(vehicleInfoNode, "qualifier");
            final String value = PojoXmlUtil.getNodeByTagName(vehicleInfoNode, "value").getTextContent();

            if (qualifierNode.getTextContent().equals("NOD")) {
                final int carDoorCount = Integer.parseInt(value);
                carMakeModel.setCarMaxDoorCount(carDoorCount);
                carMakeModel.setCarMinDoorCount(carDoorCount);
            } else if ((qualifierNode.getTextContent().equals("PAX")) || (qualifierNode.getTextContent().equals("NOS"))) {
                carMakeModel.setCarCapacityAdultCount(Integer.parseInt(value));
            } else if (qualifierNode.getTextContent().equals("MOD")) {
                carMakeModel.setCarMaxDoorCount(Integer.parseInt(value));
            } else if (qualifierNode.getTextContent().equals("NOB")) {
                carMakeModel.setCarCapacityLargeLuggageCount(Integer.parseInt(value));
            }
        }
    }

    public void buildCostListLegacyFinanceKey(CostListType costList) {
        if (null != costList && CollectionUtils.isNotEmpty(costList.getCost()))
        {
            for (final CostType cost : costList.getCost())
            {
                if (null != cost)
                {
                    buildLegacyFinanceKeyByFinanceCategoryCode(cost);
                }
            }
        }
    }

    public void buildLegacyFinanceKeyByFinanceCategoryCode(CostType cost) {
        if (null != cost.getFinanceCategoryCode() && !StringUtils.isEmpty(cost.getFinanceCategoryCode())) {
            if (null == cost.getLegacyFinanceKey()) {
                cost.setLegacyFinanceKey(new LegacyFinanceKeyType());
            }

            //set default value.
            cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
            cost.getLegacyFinanceKey().setLegacyMonetaryClassID(8);
            cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(0);

            switch (cost.getFinanceCategoryCode()) {
                case "Base":
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(7);
                    if(cost.getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.Weekly.getFinanceApplicationCode()))
                    {
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(8);
                    }
                    else if(cost.getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.Weekend.getFinanceApplicationCode()))
                    {
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(9);
                    }
                    if(null != cost.getDescriptionRawText() && cost.getDescriptionRawText()
                            .equals(CommonEnumManager.CostDescriptionRawText.BaseRateTotal.getDescriptionRawText()))
                    {
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(14);
                    }
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
                    cost.getLegacyFinanceKey().setLegacyMonetaryClassID(1);
                    break;
                case "Total":
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(0);
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(0);
                    cost.getLegacyFinanceKey().setLegacyMonetaryClassID(0);
                    break;
                case "Surcharge":
                case "Taxes":
                case "Fee":
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(10);
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
                    cost.getLegacyFinanceKey().setLegacyMonetaryClassID(3);
                    //for one way dropoff charge, LegacyFinanceKey should be 18, 1, 27
                    if (null != cost.getDescriptionRawText() && cost.getDescriptionRawText().contains(CommonEnumManager.CostDescriptionRawText.OneWayCharge.getDescriptionRawText())) {
                        cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(27);
                        cost.getLegacyFinanceKey().setLegacyMonetaryClassID(18);
                        //for one way dropoff charge, FinanceCategoryCode should be "Fee"
                       cost.setFinanceCategoryCode("Fee");
                    }
                    break;
                case "Misc":
                case "MiscBase":
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(6);
                    break;
                case "Coverage":
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(21);
                    break;
                default:
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(21);
                    break;
            }

            //------ TODO Mia
            //getExcessAndLiabilityOfCarRateDetail legancyfinance method logic, not sure merge with cost list legacy financy key is ok or not,
            // if it is not ok, should not make it public common method
            if (StringUtils.isEmpty(cost.getFinanceApplicationCode())) {
                cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(21);
            } else {
                if (cost.getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.ExtraDaily.getFinanceApplicationCode())) {
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(7);
                } else if (cost.getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.ExtraHourly.getFinanceApplicationCode())) {
                    cost.getLegacyFinanceKey().setLegacyMonetaryCalculationID(25);
                }
            }
            //------END TODO Mia
        }
    }

    /**
     * build Misc Cost base on total and base cost
     */
    public void buildMiscCosts(List<CostType> costList)
    {
        if (null != costList && !costList.isEmpty())
        {
            String currency1 = null;
            CostType totalCostcurrency1 = null;
            CostType baseCostcurrency1 = null;
            CostType taxesCostcurrency1 = null;
            double feeCostcurrency1 = 0;

            CostType totalCostcurrency2 = null;
            CostType baseCostcurrency2 = null;
            CostType taxesCostcurrency2 = null;
            double feeCostcurrency2 = 0;

            for (final CostType cost : costList)
            {
                final String costCurrency = cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                if (null == currency1)
                {
                    currency1 = costCurrency;
                }

                if (null != cost.getFinanceCategoryCode() && cost.getFinanceCategoryCode().equals(CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode()))
                {
                    if (costCurrency.equals(currency1))
                    {
                        totalCostcurrency1 = cost;
                    } else
                    {
                        totalCostcurrency2 = cost;
                    }
                } else if (null != cost.getFinanceCategoryCode() && cost.getFinanceCategoryCode().equals(CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode()))
                {
                    if (costCurrency.equals(currency1))
                    {
                        baseCostcurrency1 = cost;
                    } else
                    {
                        baseCostcurrency2 = cost;
                    }
                } else if (null != cost.getFinanceCategoryCode() && cost.getFinanceCategoryCode().equals(CommonEnumManager.FinanceCategoryCode.Taxes.getFinanceCategoryCode()))
                {
                    if (costCurrency.equals(currency1))
                    {
                        taxesCostcurrency1 = cost;
                    } else
                    {
                        taxesCostcurrency2 = cost;
                    }
                } else if (null != cost.getFinanceCategoryCode() && cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() > 0)
                {
                    double feeAmout = CostPriceCalculator.calculateCostAmount(cost, 0, true);
                    if (costCurrency.equals(currency1))
                    {
                        feeCostcurrency1 = feeCostcurrency1 + feeAmout;
                    } else
                    {
                        feeCostcurrency2 = feeCostcurrency2 + feeAmout;
                    }
                }
            }

            CostType miscCost = buildMiscCost(totalCostcurrency1, baseCostcurrency1, taxesCostcurrency1, feeCostcurrency1);
            if (miscCost != null)
            {
                costList.add(miscCost);
            }

            if (null != totalCostcurrency2 && null != baseCostcurrency2)
            {
                CostType miscCostCurrency2 = buildMiscCost(totalCostcurrency2, baseCostcurrency2, taxesCostcurrency2, feeCostcurrency2);
                if (miscCostCurrency2 != null)
                {
                    costList.add(miscCostCurrency2);
                }
            }
        }
    }


    private CostType buildMiscCost(CostType totalCost, CostType baseCost, CostType taxesCost, double feeCost)
    {
        double miscAmountDecimal = CostPriceCalculator.calculateCostAmount(totalCost, 0, true)
                - CostPriceCalculator.calculateCostAmount(baseCost, 0, true);

        if (null != taxesCost)
        {
            miscAmountDecimal = miscAmountDecimal - CostPriceCalculator.calculateCostAmount(taxesCost, 0, true);
        }

        miscAmountDecimal = miscAmountDecimal - feeCost;

        if (Math.abs(miscAmountDecimal) > 0.1)
        {
            //Misc
            final CostType cost = new CostType();
            cost.setMultiplierOrAmount(new MultiplierOrAmountType());
            cost.getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
            cost.getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(totalCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());

            cost.getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
            final String miscAmountDecimalStr = (miscAmountDecimal + "");
            int decimalPC = miscAmountDecimalStr.contains(".") ? miscAmountDecimalStr.length() - miscAmountDecimalStr.indexOf(".") - 1 : 0;
            if (decimalPC > 5)
            {
                decimalPC = 5;
            }
            cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(decimalPC);
            cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(new BigDecimal(miscAmountDecimal * Math.pow(10, decimalPC)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue());

            cost.setFinanceCategoryCode(CommonEnumManager.FinanceCategoryCode.Misc.getFinanceCategoryCode());
            cost.setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode());
            cost.setFinanceApplicationUnitCount(1L);
            cost.setDescriptionRawText("Misc base rate (may include Extra days,Taxes etc) (requested currency)");

            return cost;
        }

        return null;
    }

    public void buildAmount(AmountType amount, String amountStr) {
        if(null != amountStr && !amountStr.isEmpty())
        {
            final int len = amountStr.length() - amountStr.indexOf('.') - 1;
            amount.setDecimalPlaceCount(len);
            amount.setDecimal(new Double(Double.parseDouble(amountStr) * Math.pow(10, len)).intValue());
        }
    }

    public void buildCurrencyAmount(CurrencyAmountType currencyAmount, String amountStr, String currencyCode) {
        if (null == currencyAmount.getAmount()) {
            currencyAmount.setAmount(new AmountType());
        }
        buildAmount(currencyAmount.getAmount(), amountStr);
        currencyAmount.setCurrencyCode(currencyCode);
    }

    //----------------------------------------start conditional price list ----------------------------------------------


    public void buildCarRateDetail(CarProductType carproduct, Node rateDetailsNode) {
        buildCarConditionalCostPriceList(carproduct, rateDetailsNode);
    }

    public void buildCarConditionalCostPriceList(CarProductType carproduct, Node rateDetailsNode) {
        if (null == carproduct.getCarRateDetail()) {
            carproduct.setCarRateDetail(new CarRateDetailType());
        }
        if (null == carproduct.getCarRateDetail().getConditionalCostPriceList()) {
            carproduct.getCarRateDetail().setConditionalCostPriceList(new CostPriceListType());
        }
        if (null == carproduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice()) {
            carproduct.getCarRateDetail().getConditionalCostPriceList().setCostPrice(new ArrayList<>());
        }

        buildConditionalPriceFromTaxCovSurchargeGroupNode(carproduct.getCarRateDetail(), rateDetailsNode);

        buildConditionalPriceFromRateDetailNode(carproduct.getCarRateDetail(), rateDetailsNode);

        getExcessAndLiabilityOfCarRateDetail(carproduct, rateDetailsNode);

        if (!carproduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice().isEmpty()) {
            setLegacyFinanceKey(carproduct.getCarRateDetail().getConditionalCostPriceList().getCostPrice());
        }
    }

    private void buildConditionalPriceFromTaxCovSurchargeGroupNode(CarRateDetailType rateDetails, Node rateDetailsNode) {
        /// Not all of the rateDetails/taxCovSurchargeGroup/taxSurchargeCoverageInfo[chargeDetails] mappinf in Maserati response
        final List<Node> taxCovGroup = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "taxCovSurchargeGroup");
        for (final Node covGroup : taxCovGroup) {
            if(!isOnlyExcessOrLiabilityCov(covGroup)) {
                final Node chargeDetails = PojoXmlUtil.getNodeByTagName(covGroup, "chargeDetails");
                // base period type mached
                final String chargeType = PojoXmlUtil.getNodeByTagName(chargeDetails, "type").getTextContent();

                final Node descriptionNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "description");
                final String description = null == descriptionNode ? null : descriptionNode.getTextContent();
                if (null != description && (description.equals("OPT") || description.equals("NBR"))) {
                    if (null != chargeType && (chargeType.equals("108") || chargeType.equals("COV"))) {
                        buildConditionalPrice(rateDetails.getConditionalCostPriceList(), chargeDetails, chargeType);
                    }
                }

                buildConditionalPriceFromTaxCovSurchargeGroupChildNodeChargeDetails(rateDetails, covGroup);
            }
        }
    }

    private boolean isOnlyExcessOrLiabilityCov(Node taxCovGroupNode)
    {
        boolean firstIsNormalCov = false;
        boolean hasExcessOrLiablilityCov = false;
        final List<Node> chargeDetailsNodeList = PojoXmlUtil.getNodesByTagName(taxCovGroupNode, "chargeDetails");
        if(!CollectionUtils.isEmpty(chargeDetailsNodeList))
        {
            final Node firstTypetype = PojoXmlUtil.getNodeByTagName(chargeDetailsNodeList.get(0), "type");
            final Node firstDescription = PojoXmlUtil.getNodeByTagName(chargeDetailsNodeList.get(0), "description");
            if(firstTypetype != null && firstTypetype.getTextContent().trim().equals("COV") && !(firstDescription != null
                    && (firstDescription.getTextContent().trim().equals("E") || firstDescription.getTextContent().trim().equals("LIA"))))
            {
                firstIsNormalCov = true;
            }
        }
        for(final Node chargeDetailsNode : chargeDetailsNodeList)
        {
            final Node type = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "type");
            final Node description = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "description");

            if (type != null && type.getTextContent().trim().equals("COV") && description != null
                    && (description.getTextContent().trim().equals("E") || description.getTextContent().trim().equals("LIA"))) {
                hasExcessOrLiablilityCov = true;
                break;
            }
        }
        if(!firstIsNormalCov && hasExcessOrLiablilityCov){
            return  true;
        }
        return false;
    }



    // 'Excess' and 'Liability' charges: <description>E</description> or <description>LIA</description> will be mapped in the ConditionalCostPriceList.
    private void buildConditionalPriceFromTaxCovSurchargeGroupChildNodeChargeDetails(CarRateDetailType rateDetails, Node covGroup) {
        final Node taxCovInfoNode = covGroup.getFirstChild();

        final Node chargeDetail = PojoXmlUtil.getNodeByTagName(taxCovInfoNode, "chargeDetails");
        final String chargeType = PojoXmlUtil.getNodeByTagName(chargeDetail, "type").getTextContent();

        final Node descNode = PojoXmlUtil.getNodeByTagName(chargeDetail, "description");
        if (null != descNode) {
            final String description = descNode.getTextContent();
            if ((null != chargeType && null != description) && (chargeType.equals("COV") && (description.equals("E") || description.equals("LIA")))) {
                buildConditionalPrice(rateDetails.getConditionalCostPriceList(), chargeDetail, "Trip");
            }
        }
    }

    private void buildConditionalPriceFromRateDetailNode(CarRateDetailType rateDetails, Node rateDetailsNode) {
        final List<Node> rateDetailList = PojoXmlUtil.getNodesByTagName(rateDetailsNode, "rateDetail");
        if (CollectionUtils.isNotEmpty(rateDetailList))
        {
            final List<Node> chargeDetailsList = PojoXmlUtil.getNodesByTagName(rateDetailList.get(0), "chargeDetails");
            final String currencyCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(rateDetailList.get(0), "tariffInfo"), "currency").getTextContent();
            for (final Node chargeNode : chargeDetailsList)
            {
                final String chargeType = PojoXmlUtil.getNodeByTagName(chargeNode, "type").getTextContent();
                if ((null != chargeType) && (chargeType.equals("008") || chargeType.equals("009")))
                {
                    final CostPriceType cp = new CostPriceType();
                    cp.setCost(new CostType());

                    cp.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
                    cp.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());

                    cp.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currencyCode);
                    cp.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                    cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(2L);

                    final double amountDecimal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeNode, "amount").getTextContent()) * Math.pow(10, 2);
                    cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(new Double(amountDecimal).intValue());
                    cp.getCost().setFinanceCategoryCode(ASCSGDSReaderUtil.getFinanceCategoryCodeByChargeDetailsType(chargeType));
                    if ((null != chargeType) && chargeType.equals("008"))
                    {
                        cp.getCost().setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.ExtraDaily.getFinanceApplicationCode());
                        cp.getCost().setFinanceApplicationUnitCount(1L);
                        cp.getCost().setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.ExtraDayCharge.getDescriptionRawText());
                    }
                    else if ((null != chargeType) && chargeType.equals("009"))
                    {
                        cp.getCost().setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.ExtraHourly.getFinanceApplicationCode());
                        cp.getCost().setFinanceApplicationUnitCount(1L);
                        cp.getCost().setDescriptionRawText(CommonEnumManager.CostDescriptionRawText.ExtraHourCharge.getDescriptionRawText());
                    }

                    rateDetails.getConditionalCostPriceList().getCostPrice().add(cp);
                }
            }
        }
    }

    private void buildConditionalPrice(CostPriceListType conditionalPriceList, Node chargeDetails, String chargeType) {
        final CostPriceType cp = new CostPriceType();
        cp.setCost(new CostType());
        cp.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
        cp.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());

        if(null == PojoXmlUtil.getNodeByTagName(chargeDetails, "currency")) {
            cp.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode("");
        }
        else
        {
            cp.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(PojoXmlUtil.getNodeByTagName(chargeDetails, "currency").getTextContent());
        }

        cp.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
        cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(2L);

        if(null == PojoXmlUtil.getNodeByTagName(chargeDetails, "amount")) {
            cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(0);
            cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(0);
            cp.getCost().setFinanceCategoryCode(ASCSGDSReaderUtil.getConditionalCostPriceFinanceCategoryCode(chargeType));
        }
        else {
            final double amountDecimal = Double.parseDouble(PojoXmlUtil.getNodeByTagName(chargeDetails, "amount").getTextContent()) * Math.pow(10, 2);
            cp.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(new Double(amountDecimal).intValue());
            cp.getCost().setFinanceCategoryCode(ASCSGDSReaderUtil.getConditionalCostPriceFinanceCategoryCode(chargeType));
        }
//        cp.getCost().setFinanceApplicationCode(CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode());
        final Node periodTypeNode = PojoXmlUtil.getNodeByTagName(chargeDetails, "periodType");
        if (periodTypeNode != null) {
            final String chargeBasePeriodType = ASCSGDSReaderUtil.getChargeDetailsPeriodType(Integer.parseInt(periodTypeNode.getTextContent()));
            cp.getCost().setFinanceApplicationCode(chargeBasePeriodType);
        }
        cp.getCost().setFinanceApplicationUnitCount(1L);

        if (null != PojoXmlUtil.getNodeByTagName(chargeDetails, "comment")) {
            cp.getCost().setDescriptionRawText(PojoXmlUtil.getNodeByTagName(chargeDetails, "comment").getTextContent());
        }

        conditionalPriceList.getCostPrice().add(cp);
    }

    private void getExcessAndLiabilityOfCarRateDetail(CarProductType car, Node rateDetails) {
        if (null == car.getCarRateDetail()) {
            car.setCarRateDetail(new CarRateDetailType());
        }
        ///AmadeusSessionManagerResponse/RawAmadeusXml/Car_RateInformationFromCarSegmentReply/rateDetails/taxCovSurchargeGroup[4]/taxSurchargeCoverageInfo/chargeDetails/type
        ////AmadeusSessionManagerResponse/RawAmadeusXml/Car_RateInformationFromCarSegmentReply/rateDetails/taxCovSurchargeGroup[4]/taxSurchargeCoverageInfo/chargeDetails/description
        final List<Node> taxCovSurchargeGroupList = PojoXmlUtil.getNodesByTagName(rateDetails, "taxCovSurchargeGroup");
        if (taxCovSurchargeGroupList != null) {
            for (final Node taxCovSurchargeGroupNode : taxCovSurchargeGroupList) {
                ///AmadeusSessionManagerResponse/RawAmadeusXml/Car_RateInformationFromAvailabilityReply/rateDetails/taxCovSurchargeGroup[3]/taxSurchargeCoverageInfo/chargeDetails[3]/description
                ////AmadeusSessionManagerResponse/RawAmadeusXml/Car_RateInformationFromAvailabilityReply/rateDetails/taxCovSurchargeGroup[3]/taxSurchargeCoverageInfo/tariffInfo/currency
                final Node taxSurchargeCoverageInfoNode = PojoXmlUtil.getNodeByTagName(taxCovSurchargeGroupNode, "taxSurchargeCoverageInfo");
                final List<Node> chargeDetailsNodeList = PojoXmlUtil.getNodesByTagName(taxSurchargeCoverageInfoNode, "chargeDetails");
                String descriptionRawText = "";

                if (chargeDetailsNodeList != null) {
                    for (final Node chargeDetailsNode : chargeDetailsNodeList) {
                        final Node type = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "type");
                        final Node description = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "description");
                        final Node comment = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "comment");

                        //https://confluence.expedia.biz/display/SSG/TFS+375139+-+Egencia+Insurance+Rate+Details+Design+Doc
                        //descriptionRawText should be ChargeDescriptionRawText
                        if (comment != null && !(description != null
                                && (description.getTextContent().trim().equals("E") || description.getTextContent().trim().equals("LIA")))) {
                            descriptionRawText = comment.getTextContent().trim();
                        }

                        String financeApplicationCode = CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode();

                        final Node periodType = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "periodType");

                        if (periodType != null) {
                            financeApplicationCode = ASCSGDSReaderUtil.getChargeDetailsPeriodType(Integer.parseInt(periodType.getTextContent().trim()));
                        }

                        if (type != null && type.getTextContent().trim().equals("COV") && description != null
                                && (description.getTextContent().trim().equals("E") || description.getTextContent().trim().equals("LIA"))) {
                            final CostPriceType costPrice = new CostPriceType();
                            //type -> FinanceCategoryCode
                            //comment - > DescriptionRawText
                            //periodType -> FinanceApplicationCode
                            // currency -> CurrencyCode
                            //amount -> Multiplier
                            costPrice.setCost(new CostType());
                            costPrice.getCost().setFinanceCategoryCode(ASCSGDSReaderUtil.getConditionalCostPriceFinanceCategoryCode(type.getTextContent().trim()));
                            costPrice.getCost().setFinanceApplicationUnitCount(1L);

                            if (description.getTextContent().trim().equals("E")) {
                                costPrice.getCost().setDescriptionRawText(descriptionRawText + " - Excess");
                            } else if (description.getTextContent().trim().equals("LIA")) {
                                costPrice.getCost().setDescriptionRawText(descriptionRawText + " - Liability");
                            }

                            costPrice.getCost().setFinanceApplicationCode(financeApplicationCode);

                            costPrice.getCost().setMultiplierOrAmount(new MultiplierOrAmountType());
                            costPrice.getCost().getMultiplierOrAmount().setCurrencyAmount(new CurrencyAmountType());
                            costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setAmount(new AmountType());
                            final Node currency = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "currency");
                            if (currency == null) {
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode("");
                            }
                            else
                            {
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode(currency.getTextContent().trim());
                            }

                            final Node amount = PojoXmlUtil.getNodeByTagName(chargeDetailsNode, "amount");

                            if (amount == null) {
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimal(0);
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount().setDecimalPlaceCount(0);
                                costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().setCurrencyCode("");
                            } else {
                                buildAmount(costPrice.getCost().getMultiplierOrAmount().getCurrencyAmount().getAmount(), amount.getTextContent().trim());
                            }

                            car.getCarRateDetail().getConditionalCostPriceList().getCostPrice().add(costPrice);
                        }
                    }
                }
            }
        }
//                Console.WriteLine("Excess and Liability of CarRateDetail from ARIA response:");
//                Print.PrintMessageToConsole(carRateDetail);
    }

    private void setLegacyFinanceKey(List<CostPriceType> costPriceList) {
        for (final CostPriceType costPrice : costPriceList) {
            if (null == costPrice.getCost()) {
                continue;
            }
            buildLegacyFinanceKeyByFinanceCategoryCode(costPrice.getCost());
            /* TODO Mia Make sure if this move to public common is ok or not
            if (null == costPrice.getCost().getLegacyFinanceKey()) {
                costPrice.getCost().setLegacyFinanceKey(new LegacyFinanceKeyType());
            }
            if (StringUtils.isEmpty(costPrice.getCost().getFinanceApplicationCode())) {
                costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(21);
            } else {
                if (costPrice.getCost().getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.ExtraDaily.getFinanceApplicationCode())) {
                    costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(7);
                } else if (costPrice.getCost().getFinanceApplicationCode().equals(CommonEnumManager.FinanceApplicationCode.ExtraHourly.getFinanceApplicationCode())) {
                    costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationID(25);
                }
            }
            costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryCalculationSystemID(1);
            costPrice.getCost().getLegacyFinanceKey().setLegacyMonetaryClassID(8);
            */
        }
    }

    //----------------------------------------end conditional price list ----------------------------------------------
    public void buildTravelerInfo(TravelerListType travelerListType, Node nodeApamPnrAme1Rsp, String travellerInfoNodeTagName) {
        if (null == travelerListType.getTraveler()) {
            travelerListType.setTraveler(new ArrayList<>());
        }

        final TravelerType traveler = new TravelerType();
        final Node travellerInformationNode = PojoXmlUtil.getNodeByTagName(nodeApamPnrAme1Rsp, travellerInfoNodeTagName);
        buildTravelerPerson(traveler, travellerInformationNode, travellerInfoNodeTagName);

        /// ContactInformation
        buildContactInformation(traveler, nodeApamPnrAme1Rsp);

        travelerListType.getTraveler().add(traveler);
        //traverler.UserKey.UserID = 0 ; //???
    }

    public void buildTravelerList(TravelerListType travelerListType, Node nodeApamPnrAme1Rsp, String travellerInfoNodeTagName) {
        if (null == travelerListType.getTraveler()) {
            travelerListType.setTraveler(new ArrayList<>());
        }

        final TravelerType traveler = new TravelerType();
        final List<Node> travellerInformationNodes = PojoXmlUtil.getNodesByTagName(nodeApamPnrAme1Rsp, travellerInfoNodeTagName);
        for(final Node travellerInformationNode : travellerInformationNodes) {
            buildTravelerPerson(traveler, travellerInformationNode, travellerInfoNodeTagName);

            /// ContactInformation
            buildContactInformation(traveler, nodeApamPnrAme1Rsp);

            travelerListType.getTraveler().add(traveler);
        }
        //traverler.UserKey.UserID = 0 ; //???
    }

    private void buildTravelerPerson(TravelerType traverler, Node travellerInformationNode, String travellerInfoNodeTagName) {
        /// person
        traverler.setPerson(new PersonType());
        traverler.getPerson().setPersonName(new PersonNameType());

        Node passenger = null;
        if(null != travellerInformationNode)
        {
            if (travellerInfoNodeTagName.equals("travellerInformation"))
            {
                passenger = PojoXmlUtil.getNodeByTagName(travellerInformationNode, "passenger");
            } else
            {
                passenger = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(travellerInformationNode, "passengerData"), "travellerInformation"), "passenger");
            }

            final String firstName = PojoXmlUtil.getNodeByTagName(passenger, "firstName").getTextContent();
            traverler.getPerson().getPersonName().setFirstName(firstName);

            String surname = "";
            if (null != PojoXmlUtil.getNodeByTagName(travellerInformationNode, "surname"))
            {
                surname = PojoXmlUtil.getNodeByTagName(travellerInformationNode, "surname").getTextContent();
            }

            traverler.getPerson().getPersonName().setLastName(surname);

            if (null == traverler.getPerson().getAge())
            {
                traverler.getPerson().setAge(new AgeType());
            }

            String ageCode = null;
            if (null != PojoXmlUtil.getNodeByTagName(passenger, "quantity"))
            {
                ageCode = PojoXmlUtil.getNodeByTagName(passenger, "quantity").getTextContent();
            }
            if (null == ageCode)
            {
                if (null != PojoXmlUtil.getNodeByTagName(passenger, "type"))
                {
                    ageCode = PojoXmlUtil.getNodeByTagName(passenger, "type").getTextContent();
                    if (null != ageCode && ageCode.equals("ADT"))
                    {
                        ageCode = "Adult";
                    }
                }
            }
            traverler.getPerson().getAge().setAgeCode(null == ageCode ? "0" : ageCode);
        }
    }

    private void buildContactInformation(TravelerType traveler, Node nodeApamPnrAme1Rsp) {
        traveler.setContactInformation(new ContactInformationType());
        traveler.getContactInformation().setPhoneList(new PhoneListType());
        traveler.getContactInformation().getPhoneList().setPhone(new ArrayList<>());

        final List<Node> dataElements = PojoXmlUtil.getNodesByTagName(nodeApamPnrAme1Rsp, "dataElementsIndiv");
        for (final Node dataElement : dataElements) {
            buildPhoneOfContactInformation(dataElement, traveler);
        }
    }

    private void buildPhoneOfContactInformation(Node dataElement, TravelerType traveler) {
        final PhoneType phone = new PhoneType();

        final String segmentName = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(dataElement, "elementManagementData"), "segmentName").getTextContent();
        if (null != PojoXmlUtil.getNodeByTagName(dataElement, "otherDataFreetext")) {
            final String segmentType = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(
                    PojoXmlUtil.getNodeByTagName(dataElement, "otherDataFreetext"), "freetextDetail"), "type").getTextContent();
            buildPhoneOfContactInformation(dataElement, phone, segmentName, segmentType);
        }
        if(!StringUtils.isEmpty(phone.getPhoneNumber()) || !StringUtils.isEmpty(phone.getPhoneCategoryCode())) {
            traveler.getContactInformation().getPhoneList().getPhone().add(phone);
        }
        // user ID ?
    }

    private void buildPhoneOfContactInformation(Node dataElement, PhoneType phone, String segmentName, String segmentType) {
        if (null != segmentName && segmentName.equals("AP")) {
            String phoneInfo = "";
            if (null != segmentType && (segmentType.equals("3") || segmentType.equals("4") || segmentType.equals("7") || segmentType.equals("P01"))) {
                phone.setPhoneCategoryCode(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(dataElement, "otherDataFreetext"),
                        "freetextDetail"), "type").getTextContent());
                phoneInfo = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(dataElement, "otherDataFreetext"), "longFreetext").getTextContent();
            }
            phoneInfo = phoneInfo.replace("(", "").trim();
            final String[] phoneList = phoneInfo.split("\\)");
            phone.setPhoneCategoryCode(phoneList[0]);
            if(phoneList.length ==1){
                phone.setPhoneCategoryCode("Business");
                phone.setPhoneNumber(phoneList[0]);
            }
            // the format not correct now ,need log a new bug
            //phone.PhoneAreaCode = (phoneList.Length == 3 ? phoneList[1] : "");
            //phone.PhoneNumber = (phoneList.Length == 3 ? phoneList[2] : phoneList[1]);

            //Mia APRQ old framework code
            /*
            // CarReservation/TravelerList/Traveler/Person/ContactInformation
        List<Node> emailList = pnrRetrieveResponse.SelectNodes("//n2:dataElementsMaster/n2:dataElementsIndiv", xnmPNR);
        if (null == emailList || emailList.Count == 0) {
            Console.WriteLine("Build TravelerList Error: Can't find the node - dataElementsMaster/dataElementsIndiv in PNR_Retrieve response that mapped Traveler/Person/Contactinformation/EmailAddress.");
        } else {
            ExternalSupplyServiceDomainValueMapHelper essdvm = new ExternalSupplyServiceDomainValueMapHelper(CarCommonEnumManager.ServieProvider.Amadeus);
            for (int i = 1; i <= emailList.Count; i++) {
                Node segmentName = pnrRetrieveResponse.SelectSingleNode("//n2:dataElementsMaster/n2:dataElementsIndiv[" + i + "]/n2:elementManagementData/n2:segmentName", xnmPNR);
                if (segmentName != null && "AP" == segmentName.InnerText) {
                    Node type = pnrRetrieveResponse.SelectSingleNode("//n2:dataElementsMaster/n2:dataElementsIndiv[" + i + "]/n2:otherDataFreetext/n2:freetextDetail/n2:type", xnmPNR);
                    if (null != type) {
                        //if (type.InnerText.Trim() == "3")
                        //{
                        //    phone.PhoneCategoryCode = "Mobile";
                        //    phone.PhoneNumber = pnrRetrieveResponse.SelectSingleNode("//n2:dataElementsMaster/n2:dataElementsIndiv[" + i + "]/n2:otherDataFreetext/n2:longFreetext", xnmPNR).InnerText;
                        //}
                        //else if (type.InnerText.Trim() == "4")
                        //{
                        //    phone.PhoneCategoryCode = "Home";
                        //    phone.PhoneNumber = pnrRetrieveResponse.SelectSingleNode("//n2:dataElementsMaster/n2:dataElementsIndiv[" + i + "]/n2:otherDataFreetext/n2:longFreetext", xnmPNR).InnerText;
                        //}

                        phone.PhoneCategoryCode = essdvm.ESSDVMtblsGetDomainValue(ASCS_DomainType.PhoneCategory, type.InnerText.Trim());
                        if (!String.IsNullOrEmpty(phone.PhoneCategoryCode)) {
                            phone.PhoneNumber = pnrRetrieveResponse.SelectSingleNode("//n2:dataElementsMaster/n2:dataElementsIndiv[" + i + "]/n2:otherDataFreetext/n2:longFreetext", xnmPNR).InnerText;
                        }
                    }
                }
            }
        }
             */
        }
    }

    public String readCompanyCode(Node companyDetails)
    {
        if(null != companyDetails)
        {
            return PojoXmlUtil.getNodeByTagName(companyDetails, "companyCode").getTextContent();
        }else
        {
            return null;
        }
    }

    public long buildVendorSupplierID(CarsSCSDataSource scsDataSource, String vendorCode) throws DataAccessException
    {
        return GDSMsgReadHelper.readVendorSupplierID(scsDataSource, vendorCode);
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj);
    }

    public Map<Long, String> buildDiscountNumList(Node request, CarsSCSDataSource scsDataSource,
                                                  String referenceInfoParentNodeTag, String referenceInfoNodeTag) throws DataAccessException
    {
        Map<Long, String> vendorDiscountNumsMap = new HashMap();
        //Load XML from input String
        //VendorCode
        Long vendorSupplierID = buildVendorSupplierID(scsDataSource, readCompanyCode(request));

        if(null == referenceInfoParentNodeTag)
        {
            referenceInfoParentNodeTag = "customerInfo";
        }
        final Node customerInfoNode = PojoXmlUtil.getNodeByTagName(request, referenceInfoParentNodeTag);

        //ChildNodes of loyaltyNumbersList
        if(null != customerInfoNode)
        {
            if(null == referenceInfoNodeTag)
            {
                referenceInfoNodeTag = "customerReferences";
            }
            final List<Node> customerReferenceInfoNodeList = PojoXmlUtil.getNodesByTagName(customerInfoNode, referenceInfoNodeTag);
            if (null != customerReferenceInfoNodeList && !customerReferenceInfoNodeList.isEmpty())
            {
                for (final Node cstRefInfo : customerReferenceInfoNodeList)
                {
                    final CustomerReferenceInfo customerReferenceInfo = new CustomerReferenceInfo(cstRefInfo);
                    if (customerReferenceInfo.getReferenceQualifier().equals("CD"))
                    {
                        vendorDiscountNumsMap.put(vendorSupplierID, customerReferenceInfo.getReferenceNumber());
                    }
                }
            }
        }

        return vendorDiscountNumsMap;
    }

    public Map<String, List<String>> buildVendorLocMap(Node providerSpecificOption) throws DataAccessException
    {
        Map<String, List<String>> vendorLocMap = new HashMap();

        //Load XML from input String
        //VendorCode
        String vendorCode = readCompanyCode(providerSpecificOption);

        List<Node> pickupDropoffInfos = PojoXmlUtil.getNodesByTagName(providerSpecificOption, "pickupDropoffInfos");
        for(Node pickupDropoffInfo : pickupDropoffInfos)
        {
            CarInventoryKeyType carInventoryKey = new CarInventoryKeyType();
            readCarPickupAndDropOffLocationKey(carInventoryKey, pickupDropoffInfo, "pickupDropoffLocations");
            String locations = buildLocStr(carInventoryKey.getCarCatalogKey().getCarPickupLocationKey())+
                    buildLocStr(carInventoryKey.getCarCatalogKey().getCarDropOffLocationKey());

            addlocToVendorLocMapNoDuplicate(vendorLocMap, vendorCode, locations);
        }

       return vendorLocMap;
    }

    //String locations = commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getStartCarLocationKey())
    // + commonNodeReader.buildLocStr(searchCriteria.getCarTransportationSegment().getEndCarLocationKey());
    public String buildLocStr(CarLocationKeyType locationKey)
    {
        StringBuilder location = new StringBuilder();
        location.append(org.springframework.util.StringUtils.isEmpty(locationKey.getLocationCode()) ? "" : locationKey.getLocationCode());
        location.append(org.springframework.util.StringUtils.isEmpty(locationKey.getCarLocationCategoryCode()) ? "" : locationKey.getCarLocationCategoryCode());
        location.append(org.springframework.util.StringUtils.isEmpty(locationKey.getSupplierRawText()) ? "" : locationKey.getSupplierRawText());

        return location.toString();
    }

    public void addlocToVendorLocMapNoDuplicate(Map<String, List<String>> vendorLocMap, String vendorCode, String locations)
    {
        if (vendorLocMap.containsKey(vendorCode))
        {
            List<String> locationsForVendor = vendorLocMap.get(vendorCode);
            if(!locationsForVendor.contains(locations))
            {
                locationsForVendor.add(locations);
            }
            vendorLocMap.remove(vendorCode);
            vendorLocMap.put(vendorCode, locationsForVendor);
        }
        else
        {
            List<String> locationsForVendor = new ArrayList<>();
            locationsForVendor.add(locations);

            vendorLocMap.put(vendorCode, locationsForVendor);
        }
    }
}