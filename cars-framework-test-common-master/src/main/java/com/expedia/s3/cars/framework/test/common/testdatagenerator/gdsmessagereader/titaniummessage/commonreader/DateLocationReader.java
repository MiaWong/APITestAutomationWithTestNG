package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.commonreader;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneListType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.timetypes.defn.v4.*;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Calendar.*;

/**
 * Created by v-mechen on 12/6/2016.
 */
public class DateLocationReader {
    private DateLocationReader(){

    }
    private static char operationTimeSplit = '~';
    //Read DateLocation for search request
    public static void readSearchReqDateLocation(CarSearchCriteriaType carSearchCriteria, Node vehRentalCoreNode, CarsSCSDataSource scsDataSource,
                                                 Long supplierID) throws DataAccessException {
        //Read date
        final String pickDT = vehRentalCoreNode.getAttributes().getNamedItem("PickUpDateTime").getTextContent();
        final String returnDT = vehRentalCoreNode.getAttributes().getNamedItem("ReturnDateTime").getTextContent();
        if(carSearchCriteria.getCarTransportationSegment() == null){
            carSearchCriteria.setCarTransportationSegment(new CarTransportationSegmentType());
            carSearchCriteria.getCarTransportationSegment().setSegmentDateTimeRange(new SegmentDateTimeRangeType());
            carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().setStartDateTimeRange(new DateTimeRangeType());
            carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().setEndDateTimeRange(new DateTimeRangeType());
        }
        carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMinDateTime(new DateTime(pickDT.length() > 19 ? pickDT.substring(0, 19) : pickDT));
        carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().setMaxDateTime(carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getStartDateTimeRange().getMinDateTime());
        carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().setMinDateTime(new DateTime(returnDT.length() > 19 ? returnDT.substring(0, 19) : returnDT));
        carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().setMaxDateTime(carSearchCriteria.getCarTransportationSegment().getSegmentDateTimeRange().getEndDateTimeRange().getMinDateTime());

        //Read location
        final CarLocationKeyType pickLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "PickUpLocation").getAttributes().getNamedItem("LocationCode").getTextContent(),
                scsDataSource, supplierID, true,  true);
        carSearchCriteria.getCarTransportationSegment().setStartCarLocationKey(pickLocation);
        final CarLocationKeyType returnLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "ReturnLocation").getAttributes().getNamedItem("LocationCode").getTextContent(),
                scsDataSource, supplierID, true,  true);
        carSearchCriteria.getCarTransportationSegment().setEndCarLocationKey(returnLocation);
    }

    //Reaqd DateLocation for CostAvail
    public static void readCostAvailReqDateLocation(CarProductType carproduct, Node vehRentalCoreNode, CarsSCSDataSource scsDataSource, Long supplierID) throws DataAccessException {
        //Read date
        final String pickDT = vehRentalCoreNode.getAttributes().getNamedItem("PickUpDateTime").getTextContent();
        final String returnDT = vehRentalCoreNode.getAttributes().getNamedItem("ReturnDateTime").getTextContent();
        carproduct.getCarInventoryKey().setCarPickUpDateTime(new DateTime(pickDT.length() > 19 ? pickDT.substring(0, 19) : pickDT));
        carproduct.getCarInventoryKey().setCarDropOffDateTime(new DateTime(returnDT.length() > 19 ? returnDT.substring(0, 19) : returnDT));

        //Read location
        final CarLocationKeyType pickLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "PickUpLocation").getAttributes().getNamedItem("LocationCode").getTextContent(),
                scsDataSource, supplierID, false,  false);
        carproduct.getCarInventoryKey().getCarCatalogKey().setCarPickupLocationKey(pickLocation);
        final CarLocationKeyType returnLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehRentalCoreNode, "ReturnLocation").getAttributes().getNamedItem("LocationCode").getTextContent(),
                scsDataSource, supplierID, false,  false);
        carproduct.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(returnLocation);
    }

    /// <summary>
    /// read DateLocation from VehAvail
    /// </summary>
    /// <param name="carInventoryKey"></param>
    /// <param name="VehAvailNode"></param>
    public static void readLocationFromVehAvail(CarInventoryKeyType carInventoryKey, Node vehAvailNode, CarsSCSDataSource scsDataSource, Long supplierID) throws DataAccessException {
        final CarLocationKeyType pickLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehAvailNode, "VendorLocation").getAttributes().getNamedItem("LocationCode").getTextContent(), scsDataSource, supplierID, false, false);

        if(CompareUtil.isObjEmpty(carInventoryKey.getCarCatalogKey()))
        {
            carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
        }
        carInventoryKey.getCarCatalogKey().setCarPickupLocationKey(pickLocation);
        final CarLocationKeyType returnLocation = readLocationKey(PojoXmlUtil.getNodeByTagName(vehAvailNode, "DropOffLocation").getAttributes().getNamedItem("LocationCode").getTextContent(), scsDataSource, supplierID, false, false);
        carInventoryKey.getCarCatalogKey().setCarDropOffLocationKey(returnLocation);
    }


    //Read CarLocationKey from external location
    public static CarLocationKeyType readLocationKey(String locationNodeValue, CarsSCSDataSource scsDataSource,
                                                     Long supplierID, boolean keepKeyValue, boolean isSearchRequest) throws DataAccessException {
        final CarLocationKeyType key = new CarLocationKeyType();

        //Try get CarVendorLocationID
        final List<ExternalSupplyServiceDomainValueMap> locationMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(supplierID, 0L, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, null, locationNodeValue);
        final Long carVendorLocationID = locationMapList.isEmpty() ? 0L: Long.parseLong(locationMapList.get(0).getDomainValue());

        //if CarVendorLocationID exist, return CarVendorLocationID in location key
        if (carVendorLocationID > 0)
        {
            key.setCarVendorLocationID(carVendorLocationID);
        }

        if (isSearchRequest)
        {
            setSearchReqLocationCodes(key, locationNodeValue);
        }
        else
        {
            setLocationCodes(key, locationNodeValue, keepKeyValue);
        }

        return key;
    }

    //Search request location is specific - maybe onAirport, like CDG
    public static void setSearchReqLocationCodes(CarLocationKeyType key, String locationNodeValue) {
        if (!StringUtils.isEmpty(locationNodeValue))
        {
            key.setLocationCode(locationNodeValue.substring(0, 3));
            if (locationNodeValue.length() > 3)
            {
                key.setCarLocationCategoryCode(locationNodeValue.substring(3, 4));
                key.setSupplierRawText(locationNodeValue.substring(4));
                if (key.getSupplierRawText().length() == 2){
                    key.setSupplierRawText("0" + key.getSupplierRawText());
                }
            }
        }
    }

    //Set location codes from GDS node value
    public static void setLocationCodes(CarLocationKeyType key, String locationNodeValue, boolean keepKeyValue) {
        //If CarVendorLocationID not exist or we need to keep key value, read the key values - this case is only for search request
        if ((null == key.getCarVendorLocationID() || key.getCarVendorLocationID() == 0L || keepKeyValue) && Pattern.matches("[a-zA-Z]{4}[0-9]{2,3}$", locationNodeValue))
        {
            key.setLocationCode(locationNodeValue.substring(0, 3));
            key.setCarLocationCategoryCode(locationNodeValue.substring(3, 4));
            key.setSupplierRawText(locationNodeValue.substring(4));
            if (key.getSupplierRawText().length() == 2){
                key.setSupplierRawText("0" + key.getSupplierRawText());
            }
        }
    }

    //Read Date
    public static CarInventoryKeyType readDate(CarInventoryKeyType carInventoryKey, Node vehRentalCoreNode)
    {
        //Read date
        final String pickDT = vehRentalCoreNode.getAttributes().getNamedItem("PickUpDateTime").getTextContent();
        final String returnDT = vehRentalCoreNode.getAttributes().getNamedItem("ReturnDateTime").getTextContent();
        if (!StringUtils.isEmpty(pickDT)) {
            carInventoryKey.setCarPickUpDateTime(new DateTime(pickDT.length() > 19 ? pickDT.substring(0, 19) : pickDT));
        }
        if (!StringUtils.isEmpty(returnDT)) {
            carInventoryKey.setCarDropOffDateTime(new DateTime(returnDT.length() > 19 ? returnDT.substring(0, 19) : returnDT));
        }

        return carInventoryKey;
    }

    //Read Locations from LocationDetails
    public static void readLocationsFromLocationDetails(CarProductType car, List<Node> locationDetails, CarsSCSDataSource scsDataSource, Long supplierID) throws DataAccessException {
        if(CompareUtil.isObjEmpty(car.getCarInventoryKey()))
        {
            car.setCarInventoryKey(new CarInventoryKeyType());
        }
        if(CompareUtil.isObjEmpty(car.getCarInventoryKey().getCarCatalogKey()))
        {
            car.getCarInventoryKey().setCarCatalogKey(new CarCatalogKeyType());
        }
        for (final Node locationDetail : locationDetails)
        {
            //For Search, there is no DropOffIndicator
            final boolean dropOffIndicator = getDropOffIndicator(locationDetail, car);
            if (!dropOffIndicator)
            {
                readPickupLocation(car, locationDetail, scsDataSource, supplierID);
                car.getCarInventoryKey().getCarCatalogKey().setCarPickupLocationKey(car.getCarPickupLocation().getCarLocationKey());
            }
            if (dropOffIndicator)
            {
                readDropOffLocation(car, locationDetail, scsDataSource, supplierID);
                car.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(car.getCarDropOffLocation().getCarLocationKey());
            }
        }
        //Copy the location from pickup if it's null
        copyPickLocationToDropoff(car);
    }


    public static boolean getDropOffIndicator(Node locationDetail, CarProductType car)
    {
        boolean dropOffIndicator = false;
        //Get DropOffIndicator from GDS message
        if(null != locationDetail.getAttributes().getNamedItem("DropOffIndicator")) {
            dropOffIndicator = Boolean.parseBoolean(locationDetail.getAttributes().getNamedItem("DropOffIndicator").getTextContent());
        }
        //If pickupLocation already exist, it maybe dropoff location too
        if (null == locationDetail.getAttributes().getNamedItem("DropOffIndicator") && isPickupLocationExist(car)){
            dropOffIndicator = true;
        }

        return dropOffIndicator;
    }

    public static boolean isPickupLocationExist(CarProductType car){
        boolean pickupLocationExist = false;
        CarLocationKeyType pickupLocationKey = null;
        if(null != car.getCarPickupLocation()){
            pickupLocationKey = car.getCarPickupLocation().getCarLocationKey();
        }
        if(null != pickupLocationKey && null != pickupLocationKey.getCarVendorLocationID() && pickupLocationKey.getCarVendorLocationID() > 0 ){
            pickupLocationExist = true;
        }
        if(null != pickupLocationKey && !StringUtils.isEmpty(car.getCarPickupLocation().getCarLocationKey().getLocationCode())){
            pickupLocationExist = true;
        }
        return pickupLocationExist;

    }

    public static void copyPickLocationToDropoff(CarProductType car){
        if (car.getCarDropOffLocation() == null ||
                car.getCarDropOffLocation().getCarLocationKey() == null
                || (StringUtils.isEmpty(car.getCarDropOffLocation().getCarLocationKey().getLocationCode())
                && car.getCarDropOffLocation().getCarLocationKey().getCarVendorLocationID() == 0))
        {
            car.setCarDropOffLocation(car.getCarPickupLocation());
            // CarDropOffLocation don't hava CarShuttleCategoryCode, CarPickupLocation has
            car.getCarDropOffLocation().setCarShuttleCategoryCode(null);
            car.getCarInventoryKey().getCarCatalogKey().setCarDropOffLocationKey(car.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
        }
    }

    /// <summary>
    /// Read pickup location from LocationCode
    /// </summary>
    /// <param name="locationNode"></param>
    /// <returns></returns>
    public static void readPickupLocation(CarProductType car, Node locationDetail, CarsSCSDataSource scsDataSource, Long supplierID) throws DataAccessException {
        if(CompareUtil.isObjEmpty(car.getCarPickupLocation()))
        {
            car.setCarPickupLocation(new CarLocationType());
        }
        car.getCarPickupLocation().setCarLocationKey(readLocationKey(locationDetail.getAttributes().getNamedItem("ExtendedLocationCode").getTextContent(),
                scsDataSource, supplierID, false, false));

        //CarShuttleCategoryCode - <CounterLocation Location="1"></CounterLocation>
        car.getCarPickupLocation().setCarShuttleCategoryCode("NoShuttle");	 //default value
        final List<Node> counterList = PojoXmlUtil.getNodesByTagName(locationDetail, "CounterLocation");
        if (!counterList.isEmpty())
        {
            if (counterList.get(0).getAttributes().getNamedItem("Location").getTextContent().equals("2")){
                car.getCarPickupLocation().setCarShuttleCategoryCode("ShuttleToCounter");
            }
            if (counterList.get(0).getAttributes().getNamedItem("Location").getTextContent().equals("3")){
                car.getCarPickupLocation().setCarShuttleCategoryCode("ShuttleToCounterAndCarOffAirport");
            }
        }

        car.getCarPickupLocation().setAddress(readAddress(PojoXmlUtil.getNodeByTagName(locationDetail, "Address")));

        car.getCarPickupLocation().setPhoneList(readPhoneList(locationDetail));

        //build Car OpenSchedule
        buildCarOpenShedules(car, PojoXmlUtil.getNodesByTagName(locationDetail, "AdditionalInfo"), false);
    }

    /// <summary>
    /// Read address from it's parent node
    /// </summary>
    /// <param name="address"></param>
    /// <returns></returns>
    public static AddressType readAddress(Node address) throws DataAccessException {
        final AddressType addr = new AddressType();
        addr.setAddressCategoryCode("2");

        addr.setFirstAddressLine(PojoXmlUtil.getNodeByTagName(address, "AddressLine").getTextContent());
        addr.setCityName(PojoXmlUtil.getNodeByTagName(address, "CityName").getTextContent());
        // addr.PostalCode =  XmlDocUtil.getChildNode(address, "PostalCode")[0].Value;

        addr.setCountryAlpha3Code(GDSMsgReadHelper.getCountryAlpha3CodeFromCountryCode(PojoXmlUtil.
                getNodeByTagName(address, "CountryName").getAttributes().getNamedItem("Code").getTextContent()));
        return addr;
    }

    /// <summary>
    /// Read PhoneList from it's parent node
    /// </summary>
    /// <param name="address"></param>
    /// <returns></returns>
    public static PhoneListType readPhoneList(Node locationDetail)
    {
        final PhoneListType phonelist = new PhoneListType();
        phonelist.setPhone(new ArrayList<PhoneType>());
        final List<Node> telnodes = PojoXmlUtil.getNodesByTagName(locationDetail, "Telephone");
        for (int i = 0; i < telnodes.size(); i++)
        {
            final PhoneType phone = new PhoneType();

            //TODO mia PhoneCategoryCode no node
            phone.setPhoneCategoryCode("2");
            readPhoneNumber(phone, telnodes.get(i).getAttributes().getNamedItem("PhoneNumber").getTextContent());
            phonelist.getPhone().add(phone);
        }

        return phonelist;
    }

    /// <summary>
    /// read telephone Node
    /// </summary>
    /// <param name="phone"></param>
    /// <param name="PhoneNumber"></param>
    public static void readPhoneNumber(PhoneType phone, String phoneNumber)
    {
            /*
            int PhoneCountryPrefixNbrLth = 1;
            string contrycode = ConfigurationMaster.GetCountryCodeFromPhoneCountryPrefixNbr(PhoneNumber.Substring(1, PhoneCountryPrefixNbrLth));
            if (String.IsNullOrEmpty(contrycode))
            {
                PhoneCountryPrefixNbrLth = 2;
                contrycode = ConfigurationMaster.GetCountryCodeFromPhoneCountryPrefixNbr(PhoneNumber.Substring(1, PhoneCountryPrefixNbrLth));
                if (String.IsNullOrEmpty(contrycode))
                {
                    PhoneCountryPrefixNbrLth = 3;
                    contrycode = ConfigurationMaster.GetCountryCodeFromPhoneCountryPrefixNbr(PhoneNumber.Substring(1, PhoneCountryPrefixNbrLth));
                }
            }
            phone.PhoneCountryCode = PhoneNumber.Substring(1, PhoneCountryPrefixNbrLth);

            phone.PhoneAreaCode = PhoneNumber.Substring(PhoneCountryPrefixNbrLth + 1, PhoneCountryPrefixNbrLth + 3);

            if (PhoneNumber.Length > PhoneCountryPrefixNbrLth + 4)
                phone.PhoneNumber = PhoneNumber.Substring((PhoneCountryPrefixNbrLth + 4));
             * */
        phone.setPhoneNumber(phoneNumber);
    }

    private static void buildCarOpenShedules(CarProductType car, List<Node> additionalInfoList, boolean dropOff)
    {
        if (!additionalInfoList.isEmpty())
        {
            final List<Node> opttimes = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(additionalInfoList.get(0), "OperationSchedules"), "OperationSchedule"), "OperationTimes"), "OperationTime");

            //OperationTime
            buildCarRecurringPeriod(car, car.getCarInventoryKey().getCarPickUpDateTime(), opttimes, dropOff);
            //OperationTime
            buildCarRecurringPeriod(car, car.getCarInventoryKey().getCarDropOffDateTime(), opttimes, dropOff);
        }
    }

    private static void buildCarRecurringPeriod(CarProductType car, DateTime dateTime, List<Node> operationTimeList, boolean dropOff)
    {
        final RecurringPeriodType recurringPeriod = new RecurringPeriodType();
        if (dropOff){
            if(CompareUtil.isObjEmpty(car.getCarDropOffLocation().getOpenSchedule()))
            {
                car.getCarDropOffLocation().setOpenSchedule(new OpenScheduleType());
                car.getCarDropOffLocation().getOpenSchedule().setNormalRecurringPeriodList(new RecurringPeriodListType());
                car.getCarDropOffLocation().getOpenSchedule().getNormalRecurringPeriodList().setRecurringPeriod(new ArrayList<RecurringPeriodType>());
            }
            car.getCarDropOffLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(recurringPeriod);
        }
        else {
            if(CompareUtil.isObjEmpty(car.getCarPickupLocation().getOpenSchedule()))
            {
                car.getCarPickupLocation().setOpenSchedule(new OpenScheduleType());
                car.getCarPickupLocation().getOpenSchedule().setNormalRecurringPeriodList(new RecurringPeriodListType());
                car.getCarPickupLocation().getOpenSchedule().getNormalRecurringPeriodList().setRecurringPeriod(new ArrayList<RecurringPeriodType>());
            }
            car.getCarPickupLocation().getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod().add(recurringPeriod);
        }

        if(CompareUtil.isObjEmpty(recurringPeriod.getDateRange()))
        {
            recurringPeriod.setDateRange(new DateRangeType());
        }
        recurringPeriod.getDateRange().setMinDate(DateTime.getInstanceByDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
        recurringPeriod.getDateRange().setMaxDate(DateTime.getInstanceByDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));

        if (operationTimeList != null)
        {
            final StringBuilder startTime = new StringBuilder();
            final StringBuilder endTime = new StringBuilder();
            getOperationTime(dateTime, startTime, endTime, operationTimeList);

            if (!StringUtils.isEmpty(startTime.toString()) && !StringUtils.isEmpty(endTime.toString())) {
                buildCarPickAndDropLocationTimeRange(recurringPeriod, dateTime, startTime.toString(), endTime.toString());
            }
        }
    }

    private static void getOperationTime(DateTime dateTime, StringBuilder startTime, StringBuilder endTime, List<Node> operationTimeList)
    {
        if (operationTimeList.size() > 1)
        {
            getOperationTimePerDayOfWeek(dateTime, startTime, endTime, operationTimeList);
        }
        else
        {
            final String optTime = operationTimeList.get(0).getAttributes().getNamedItem("Text").getTextContent().trim();
            if (!StringUtils.isEmpty(optTime))
            {
                startTime.append(optTime.split("-")[1].substring(5, 10));
                endTime.append(optTime.split("-")[2]);
            }
        }
    }

    private static void getOperationTimePerDayOfWeek(DateTime dateTime, StringBuilder startTime, StringBuilder endTime, List<Node> operationTimeList)
    {
        final Calendar cal = dateTime.toCalendar();
        switch (cal.get(DAY_OF_WEEK))
        {
            case SUNDAY:
                getOperationTime(startTime, endTime, "Sun", operationTimeList);
                break;
            case MONDAY:
                getOperationTime(startTime, endTime, "Mon", operationTimeList);
                break;
            case TUESDAY:
                getOperationTime(startTime, endTime, "Tue", operationTimeList);
                break;
            case WEDNESDAY:
                getOperationTime(startTime, endTime, "Weds", operationTimeList);
                break;
            case THURSDAY:
                getOperationTime(startTime, endTime, "Thur", operationTimeList);
                break;
            case FRIDAY:
                getOperationTime(startTime, endTime, "Fri", operationTimeList);
                break;
            case SATURDAY:
                getOperationTime(startTime, endTime, "Sat", operationTimeList);
                break;
            default:
                getOperationTime(startTime, endTime, "Sun", operationTimeList);
                break;
        }
    }

    private static void getOperationTime(StringBuilder startTime, StringBuilder endTime, String dayOfWeek, List<Node> operationTimeList)
    {
        for (final Node operationTime : operationTimeList)
        {
            if (null != operationTime.getAttributes().getNamedItem(dayOfWeek))
            {
                if (startTime.length() > 1) {
                    startTime.append(operationTimeSplit);
                }
                startTime.append(operationTime.getAttributes().getNamedItem("Start").getTextContent());
                if (endTime.length() > 1) {
                    endTime.append(operationTimeSplit);
                }
                endTime.append(operationTime.getAttributes().getNamedItem("End").getTextContent());
            }
        }
    }

    private static void buildCarPickAndDropLocationTimeRange(RecurringPeriodType recurringPeriod, DateTime date, String arrivalTimes, String departureTimes)
    {
        if(CompareUtil.isObjEmpty(recurringPeriod.getTimeRangeList()))
        {
            recurringPeriod.setTimeRangeList(new TimeRangeListType());
            recurringPeriod.getTimeRangeList().setTimeRange(new ArrayList<TimeRangeType>());
        }

        final DateRangeType dateRange = new DateRangeType();
        final DateTime dateMin = DateTime.getInstanceByDate(date.getYear(), date.getMonth(), date.getDay());
        final DateTime dateMax = DateTime.getInstanceByDate(date.getYear(), date.getMonth(), date.getDay());
        dateRange.setMinDate(dateMin);
        dateRange.setMaxDate(dateMax);

        final String[] arrivalTimeArr = arrivalTimes.split("~");
        final String[] departureTimeArr = departureTimes.split("~");


        for (int i = 0; i < arrivalTimeArr.length; i++)
        {
            final String arrivalTime = arrivalTimeArr[i];
            final String[] arrTimes = arrivalTime.split(":");
            final int hours = Integer.parseInt(arrTimes[0]);
            final int minutes = Integer.parseInt(arrTimes[1]);
            int second = 0;
            if (arrTimes.length > 2) {
                second = Integer.parseInt(arrTimes[2]);
            }

            final String departureTime = departureTimeArr[i];
            final String[] depTimes = departureTime.split(":");
            int hours2 = Integer.parseInt(depTimes[0]);
            int minutes2 = Integer.parseInt(depTimes[1]);
            int second2 = 0;
            if (depTimes.length > 2) {
                second2 = Integer.parseInt(depTimes[2]);
            }
            if (hours2 == 24)
            {
                hours2 = 23;
                minutes2 = 59;
                second2 = 59;
            }

            final TimeRangeType timeRange = new TimeRangeType();
            final DateTime minTime = DateTime.getInstanceByTime(hours, minutes, second, 0);
            final DateTime maxTime = DateTime.getInstanceByTime(hours2, minutes2, second2, 0);
            timeRange.setMinTime(minTime);
            timeRange.setMaxTime(maxTime);

            recurringPeriod.getTimeRangeList().getTimeRange().add(timeRange);
        }
    }

    /// <summary>
    /// Read drop off location from LocationCode
    /// </summary>
    /// <param name="locationDetail"></param>
    /// <returns></returns>
    public static void readDropOffLocation(CarProductType car, Node locationDetail, CarsSCSDataSource scsDataSource,  Long supplierID) throws DataAccessException {
        if(CompareUtil.isObjEmpty(car.getCarDropOffLocation()))
        {
            car.setCarDropOffLocation(new CarLocationType());
        }
        car.getCarDropOffLocation().setCarLocationKey(readLocationKey(locationDetail.getAttributes().getNamedItem("ExtendedLocationCode").getTextContent(), scsDataSource, supplierID, false, false));

        car.getCarDropOffLocation().setAddress(readAddress(PojoXmlUtil.getNodeByTagName(locationDetail, "Address")));

        car.getCarDropOffLocation().setPhoneList(readPhoneList(locationDetail));

        //build Car OpenSchedule
        buildCarOpenShedules(car, PojoXmlUtil.getNodesByTagName(locationDetail, "AdditionalInfo"), true);
    }



}
