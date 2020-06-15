package com.expedia.s3.cars.framework.test.common.verification;

import com.expedia.e3.data.basetypes.defn.v4.DistanceType;
import com.expedia.e3.data.basetypes.defn.v4.VendorSupplierIDListType;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CostListType;
import com.expedia.e3.data.financetypes.defn.v4.CostPerDistanceType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.timetypes.defn.v4.RecurringPeriodType;
import com.expedia.e3.data.timetypes.defn.v4.TimeRangeListType;
import com.expedia.e3.data.traveltypes.defn.v4.SegmentDateTimeRangeType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 12/5/2016.
 *
 * if there is any logic error please refer old framwork class Expedia.CarInterface.CarServiceTest.Verification.Common.CarNodesMappingVerifier
 */
public class CarNodeComparator {
    private CarNodeComparator() {

    }

    public static boolean isVendorIdAndSIPPEqual(CarCatalogKeyType expectedCarCatalogKeyType, CarCatalogKeyType actualCarCatalogKeyType, StringBuilder eMsg) {
        final StringBuilder errorMsg = new StringBuilder();
        if (expectedCarCatalogKeyType.getVendorSupplierID() != actualCarCatalogKeyType.getVendorSupplierID()) {
            errorMsg.append("The actual CarInventoryKey.CarCatalogKey- VendorSupplierID : ").append(actualCarCatalogKeyType.getVendorSupplierID())
                    .append("is not equal the expected value : ").append(expectedCarCatalogKeyType.getVendorSupplierID());
            return false;
        }

        boolean isEqual = true;
        if (isExpActObjBothNullCheck(expectedCarCatalogKeyType.getCarVehicle(), actualCarCatalogKeyType.getCarVehicle(),
                errorMsg, "Car CarInventoryKey/CarCatalogKey/CarVehicle is null in Expect or Actual; ")) {
            return isEqual;
        } else if (StringUtils.isEmpty(errorMsg.toString().trim())) {
            boolean isVehicleEqual = expectedCarCatalogKeyType.getCarVehicle().getCarCategoryCode() == actualCarCatalogKeyType.getCarVehicle().getCarCategoryCode();
            isVehicleEqual = isVehicleEqual && expectedCarCatalogKeyType.getCarVehicle().getCarTypeCode() == actualCarCatalogKeyType.getCarVehicle().getCarTypeCode();
            isVehicleEqual = isVehicleEqual&&  expectedCarCatalogKeyType.getCarVehicle().getCarTransmissionDriveCode() == actualCarCatalogKeyType.getCarVehicle().getCarTransmissionDriveCode();
            isVehicleEqual = isVehicleEqual && expectedCarCatalogKeyType.getCarVehicle().getCarFuelACCode() == actualCarCatalogKeyType.getCarVehicle().getCarFuelACCode();

            if (!isVehicleEqual) {
                errorMsg.append("The actual CarInventoryKey.CarCatalogKey- VendorSupplierID:CarCategoryCode:CarTypeCode:CarTransmissionDriveCode:CarFuelACCode=" +
                        actualCarCatalogKeyType.getVendorSupplierID() + ":" +
                        actualCarCatalogKeyType.getCarVehicle().getCarCategoryCode() + "/" +
                        actualCarCatalogKeyType.getCarVehicle().getCarTypeCode() + "/" +
                        actualCarCatalogKeyType.getCarVehicle().getCarTransmissionDriveCode() + "/" +
                        actualCarCatalogKeyType.getCarVehicle().getCarFuelACCode() + " is not equal the expected value=" +
                        expectedCarCatalogKeyType.getVendorSupplierID() + ":" +
                        expectedCarCatalogKeyType.getCarVehicle().getCarCategoryCode() + "/" +
                        expectedCarCatalogKeyType.getCarVehicle().getCarTypeCode() + "/" +
                        expectedCarCatalogKeyType.getCarVehicle().getCarTransmissionDriveCode() + "/" +
                        expectedCarCatalogKeyType.getCarVehicle().getCarFuelACCode() + '\n');
            }
        }

        if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
            isEqual = false;
            if (eMsg != null) {
                eMsg.append(errorMsg.toString());
            }
        }
        return isEqual;
    }

    public static boolean isCarShuttleCategoryCodeEqual(CarLocationType expectedLocation, CarLocationType actualLocation, StringBuilder errorMsg)
    {
        boolean isEqual = true;
        final StringBuilder eMsg = new StringBuilder();
        if (isExpActObjBothNullCheck(expectedLocation.getCarShuttleCategoryCode(), actualLocation.getCarShuttleCategoryCode(),
                eMsg, null)) {
            return isEqual;
        } else if (!StringUtils.isEmpty(eMsg.toString())) {
            return false;
        }

        if (!actualLocation.getCarShuttleCategoryCode().equals(expectedLocation.getCarShuttleCategoryCode())) {
            isEqual = false;
            errorMsg.append(" getCarShuttleCategoryCode() is not expected, expected: ")
                    .append(expectedLocation.getCarShuttleCategoryCode())
                    .append(", actual: ").append(actualLocation.getCarShuttleCategoryCode());
        }
        return isEqual;
    }

    //Comare if CarLocationKey is equal - we may just need to compare locationID or location nodes
    @SuppressWarnings("PMD")
    public static boolean isCarLocationKeyEqual(CarLocationKeyType expLocationKey, CarLocationKeyType actLocationKey,
                                                StringBuilder errorMsg, List<String> ignoreNodeList) {
        boolean isEqual = true;
        if(expLocationKey == null && actLocationKey == null) {
            return isEqual;
        }
        else if((expLocationKey != null && actLocationKey == null)||(expLocationKey == null && actLocationKey != null)) {
            return false;
        }
        //Create a temp error mesasge builder to see if CarRate is equal - if use errorMsg directly, it will be impacted by incomping value
        final StringBuilder pError = new StringBuilder();
        //Compare locationID
        if (!ignoreNodeList.contains(CarTags.CAR_VENDOR_LOCATION_ID)) {
            if (null != expLocationKey.getCarVendorLocationID() && null != actLocationKey.getCarVendorLocationID() &&
                    expLocationKey.getCarVendorLocationID().longValue() != actLocationKey.getCarVendorLocationID().longValue()) {
                pError.append("CarVendorLocationID ").append(actLocationKey.getCarVendorLocationID()).
                        append(" is not equal to expected:").append(expLocationKey.getCarVendorLocationID()).append("!\n");
            }
        }

        //Compare location codes
        isStringNodeEqual(CarTags.LOCATION_CODE, expLocationKey.getCarLocationCategoryCode(), actLocationKey.getCarLocationCategoryCode(), pError, ignoreNodeList);
        isStringNodeEqual(CarTags.CAR_LOCATION_CATEGORY_CODE, expLocationKey.getLocationCode(), actLocationKey.getLocationCode(), pError, ignoreNodeList);
        isStringNodeEqual(CarTags.SUPPLIER_RAW_TEXT, expLocationKey.getSupplierRawText(), actLocationKey.getSupplierRawText(), pError, ignoreNodeList);

        if (pError.toString().trim().length() > 0) {
            isEqual = false;
            if (errorMsg != null) {
                errorMsg.append("CarLocationKey In is not as expected , below is the Details : ").append(pError.toString());
            }
        }
        return isEqual;
    }

    //Compare CarLocationKey - if CarVendorLocationID is there, ignore location codes
    public static boolean isCarLocationKeyEqual(CarLocationKeyType expLocationKey, CarLocationKeyType actLocationKey) {
        final List<String> ignoreNodeList = new ArrayList<String>();
        if(null != expLocationKey.getCarVendorLocationID() && expLocationKey.getCarVendorLocationID() > 0 &&
                null != actLocationKey.getCarVendorLocationID() && actLocationKey.getCarVendorLocationID() > 0 )
        {
            ignoreNodeList.add(CarTags.LOCATION_CODE);
            ignoreNodeList.add(CarTags.SUPPLIER_RAW_TEXT);
            ignoreNodeList.add(CarTags.CAR_LOCATION_CATEGORY_CODE);
        }
        return isCarLocationKeyEqual(expLocationKey, actLocationKey, new StringBuilder(), ignoreNodeList);
    }

    //Comare if CarLocationKey is equal - we may just need to compare locationID or location nodes
    public static boolean isCarLocationEqual(CarLocationType expectedLocation, CarLocationType actualLocation, boolean isPickUpLocation,
                                             StringBuilder eMsg, List<String> ignoreNodeList) {
        boolean isEqual = true;
        if ((isPickUpLocation && ignoreNodeList.contains(CarTags.CAR_PICK_UP_LOCATION)) ||
                (!isPickUpLocation && ignoreNodeList.contains(CarTags.CAR_DROP_OFF_LOCATION))) {
            return isEqual;
        }

        final StringBuilder errorMsg = new StringBuilder();
        if (isExpActObjBothNullCheck(expectedLocation, actualLocation,
                errorMsg, " Car Location is null in Expect or Actual; Expect : ")) {
            return isEqual;
        } else if (StringUtils.isEmpty(errorMsg.toString().trim())) {

            if (!ignoreNodeList.contains(CarTags.SHUTTLE_CATEGORY_CODE))
            {
                isCarShuttleCategoryCodeEqual(expectedLocation, actualLocation, errorMsg);
            }

            isCarLocationKeyEqual(expectedLocation.getCarLocationKey(), actualLocation.getCarLocationKey(), errorMsg, ignoreNodeList);

            isAddressEqual(expectedLocation.getAddress(), actualLocation.getAddress(), errorMsg);

            isRecurringPeriodInLocationEqual(expectedLocation, actualLocation, errorMsg);
        }

        if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
            isEqual = false;
            eMsg.append(errorMsg.append('\n').toString());
        }

        return isEqual;
    }

    private static void formatAddresses(AddressType expectedLocation, AddressType actualLocation) {
        if (null != expectedLocation.getFirstAddressLine() && !StringUtils.isEmpty(expectedLocation.getFirstAddressLine())) {
            expectedLocation.setFirstAddressLine(expectedLocation.getFirstAddressLine().replace("\r\n", "\n"));
        }
        if (null != actualLocation.getFirstAddressLine() && !StringUtils.isEmpty(actualLocation.getFirstAddressLine())) {
            actualLocation.setFirstAddressLine(actualLocation.getFirstAddressLine().replace("\r\n", "\n"));
        }
        if (null != expectedLocation.getSecondAddressLine() &&!StringUtils.isEmpty(expectedLocation.getSecondAddressLine())) {
            expectedLocation.setSecondAddressLine(expectedLocation.getSecondAddressLine().replace("\r\n", "\n"));
        }
        if (null != actualLocation.getSecondAddressLine() &&!StringUtils.isEmpty(actualLocation.getSecondAddressLine())) {
            actualLocation.setSecondAddressLine(actualLocation.getSecondAddressLine().replace("\r\n", "\n"));
        }
    }

    private static boolean isAddressEqual(AddressType expectedLocationAddress, AddressType actualLocationAddress, StringBuilder errorMsg) {
        boolean isEqual = true;
        final StringBuilder eMsg = new StringBuilder();
        if (isExpActObjBothNullCheck(expectedLocationAddress, actualLocationAddress, eMsg, null)) {
            return isEqual;
        } else if (StringUtils.isEmpty(eMsg.toString())) {
            formatAddresses(expectedLocationAddress, actualLocationAddress);
            final boolean req = isAddressEqualPre(expectedLocationAddress, actualLocationAddress);

            isEqual = req && StringUtils.equalsIgnoreCase(expectedLocationAddress.getPostalCode(), actualLocationAddress.getPostalCode());
            isEqual = isEqual && StringUtils.equalsIgnoreCase(expectedLocationAddress.getCityName(), actualLocationAddress.getCityName());
            isEqual = isEqual && StringUtils.equalsIgnoreCase(null == expectedLocationAddress.getFirstAddressLine() ? null : expectedLocationAddress.getFirstAddressLine().trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", ""),
                    null == actualLocationAddress.getFirstAddressLine()? null : actualLocationAddress.getFirstAddressLine().trim().replaceAll("\\r", "").replaceAll("\\n", "").replaceAll("\\r\\n", "").replaceAll(" ", ""));
            isEqual = isEqual && StringUtils.equalsIgnoreCase(expectedLocationAddress.getSecondAddressLine(), actualLocationAddress.getSecondAddressLine());
            if (!isEqual) {
                errorMsg.append("The Actual CarPickup/DropoffLocation Address->PostalCode:CityName:CountryAlpha3Code:FirstAddressLine:SecondAddressLine=")
                        .append(actualLocationAddress.getPostalCode()).append(':').append(actualLocationAddress.getCityName()).append(':')
                        .append(actualLocationAddress.getCountryAlpha3Code()).append(':').append(actualLocationAddress.getFirstAddressLine()).append(':')
                        .append(actualLocationAddress.getSecondAddressLine()).append(" is not equal the expected value=")
                        .append(expectedLocationAddress.getPostalCode()).append(':').append(expectedLocationAddress.getCityName()).append(':')
                        .append(expectedLocationAddress.getCountryAlpha3Code()).append(':').append(expectedLocationAddress.getFirstAddressLine()).append(':')
                        .append(expectedLocationAddress.getSecondAddressLine()).append('\n');
            }
        } else {
            isEqual = false;
        }
        return isEqual;
    }

    private static boolean isAddressEqualPre(AddressType expectedLocation, AddressType actualLocation) {
        boolean req = false;
        if (actualLocation == null && expectedLocation == null) {
            req = true;
        } else {
            boolean countryNullAssert = (actualLocation.getCountryAlpha3Code() == null && expectedLocation.getCountryAlpha3Code() != null);
            countryNullAssert = countryNullAssert || (expectedLocation.getCountryAlpha3Code() == null && actualLocation.getCountryAlpha3Code() != null);
            if (countryNullAssert) {
                req = false;
            } else if (StringUtils.equalsIgnoreCase(actualLocation.getCountryAlpha3Code(), expectedLocation.getCountryAlpha3Code()) ||
                    actualLocation.getCountryAlpha3Code().toLowerCase().contains(expectedLocation.getCountryAlpha3Code().toLowerCase())) {
                req = true;
            }
        }

        return req;
    }

    @SuppressWarnings("PMD")
    private static void isRecurringPeriodInLocationEqual(CarLocationType expectedLocation, CarLocationType actualLocation, StringBuilder errorMsg) {
        boolean isEqual;

        List<RecurringPeriodType> expectedRecurringPeriodList = null;
        if(null != expectedLocation && null != expectedLocation.getOpenSchedule() &&null != expectedLocation.getOpenSchedule().getNormalRecurringPeriodList()) {
            expectedRecurringPeriodList = expectedLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod();
        }

        List<RecurringPeriodType> actualRecurringPeriodList = null;
        if(null != actualLocation && null != actualLocation.getOpenSchedule() &&null != actualLocation.getOpenSchedule().getNormalRecurringPeriodList()) {
            actualRecurringPeriodList = actualLocation.getOpenSchedule().getNormalRecurringPeriodList().getRecurringPeriod();
        }

        final StringBuilder errorMsgTemp = new StringBuilder();
        final boolean isBothNull = isExpActObjBothNullCheck(expectedRecurringPeriodList, actualRecurringPeriodList, errorMsgTemp,
                "Car Pickup or DropOff Location/OpenSchedule is null in Expect or Actual; ");
        if (!isBothNull && StringUtils.isEmpty(errorMsgTemp.toString())) {
            for (final RecurringPeriodType expectedRecurringPeriod : expectedRecurringPeriodList) {
                isEqual = isRecurringPeriodInLocationEqualSmallLoop(expectedRecurringPeriod, actualRecurringPeriodList);
                if (!isEqual) {
                    errorMsg.append("\n\nThe Expected Pickup or DropOff Location/OpenSchedule can not find in actual values.\nThe Expected Pickup or DropOff Location/OpenSchedule DateRange/MinDate is ")
                            .append(expectedRecurringPeriod.getDateRange().getMinDate())
                            .append("\nThe Expected Pickup or DropOff Location/OpenSchedule DateRange/MaxDate is ")
                            .append(expectedRecurringPeriod.getDateRange().getMaxDate())
                            .append("\nThe Expected Pickup or DropOff Location/OpenSchedule TimeRange/MinDate is ")
                            .append(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime().getDateTimeString())
                            .append("\nThe Expected Pickup or DropOff Location/OpenSchedule TimeRange/MinDate is ")
                            .append(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime().getDateTimeString());
                }
            }
        }
        if (!StringUtils.isEmpty(errorMsgTemp.toString().trim())) {
            if (errorMsg != null) {
                errorMsg.append(errorMsg.toString());
            }
        }
    }

    private static boolean isRecurringPeriodInLocationEqualSmallLoop(RecurringPeriodType expectedRecurringPeriod,
                                                                     List<RecurringPeriodType> actualRecurringPeriodList) {
        boolean isEqual = true;
        for (final RecurringPeriodType actualRecurringPeriod : actualRecurringPeriodList) {
            isEqual = isRecurringPeriodEqual(expectedRecurringPeriod, actualRecurringPeriod, null, null);
            if (isEqual) {
                break;
            }
        }

        return isEqual;
    }

    //Comare if VendorSupplierIDList is equal
    public static boolean isVendorSupplierIDListEqual(VendorSupplierIDListType expVendorSupplierIDList, VendorSupplierIDListType actVendorSupplierIDList, StringBuilder eMsg) {
        //if both null, return true
        if (expVendorSupplierIDList == null && actVendorSupplierIDList == null) {
            return true;
        }

        final StringBuilder errorMsg = new StringBuilder();
        //Verify VendorSupplierIDList size
        boolean isEqual = true;
        if (expVendorSupplierIDList.getVendorSupplierID().size() != actVendorSupplierIDList.getVendorSupplierID().size()) {
            errorMsg.append("The actual VendoerSupplierList count=").append(actVendorSupplierIDList.getVendorSupplierID().size())
                    .append(" is not equal the expected value=").append(expVendorSupplierIDList.getVendorSupplierID().size()).append("!\n");
        }

        //Verify VendorSupplierIDList values
        for (int i = 0; i < expVendorSupplierIDList.getVendorSupplierID().size(); i++) {
            if (actVendorSupplierIDList.getVendorSupplierID().indexOf(expVendorSupplierIDList.getVendorSupplierID().get(i).longValue()) == -1) {
                errorMsg.append("The expected VendorSupplierID:").append(expVendorSupplierIDList.getVendorSupplierID().get(i))
                        .append(" can't be found in actual VendorSupplierIDList:").append(StringUtils.join(actVendorSupplierIDList.getVendorSupplierID(), ",")).append('\n');
            }
        }

        if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
            isEqual = false;
            if (eMsg != null) {
                eMsg.append(errorMsg.toString());
            }
        }
        return isEqual;
    }

    @SuppressWarnings("PMD")
    public static boolean isCarRateEqual(CarRateType expCarRate, CarRateType actCarRate, StringBuilder errorMsg,
                                         List<String> ignoreNodeList) {
        boolean isEqual = true;

        if (!ignoreNodeList.contains(CarTags.CAR_RATE)) {
            if (expCarRate == null && actCarRate == null) {
                return isEqual;
            }
            if((expCarRate == null && actCarRate != null)||(expCarRate != null && actCarRate == null)) {
                return false;
            }
            //Create a temp error mesasge builder to see if CarRate is equal - if use errorMsg directly, it will be impacted by incomping value
            final StringBuilder pError = new StringBuilder();
            //Verify RateCode
            isStringNodeEqual(CarTags.RATE_CODE, expCarRate.getRateCode(), actCarRate.getRateCode(), pError, ignoreNodeList);

            //Verify CarRateQualifierCode
            isStringNodeEqual(CarTags.CAR_RATE_QUALIFIER_CODE, expCarRate.getCarRateQualifierCode(), actCarRate.getCarRateQualifierCode(),
                    pError, ignoreNodeList);

            //Verify LoyaltyProgramMembershipCode
            final String expLoyaltyNum = null == expCarRate.getLoyaltyProgram()?
                    "null" : (null == expCarRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode()?
                    "null" : expCarRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode());
            final String actLoyaltyNum = null == actCarRate.getLoyaltyProgram()?
                    "null" : (null == actCarRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode()?
                    "null" : expCarRate.getLoyaltyProgram().getLoyaltyProgramMembershipCode());
            isStringNodeEqual(CarTags.LOYALTY_PROGRAM_MEMBERSHIP_CODE, expLoyaltyNum, actLoyaltyNum, pError, ignoreNodeList);

            //Verify LoyaltyProgramMembershipCode
            //TSCS CostAvail reqeust & TVAR request ignore RatePeriodCode compare
            isStringNodeEqual(CarTags.RATE_PERIOD_CODE, expCarRate.getRatePeriodCode(), actCarRate.getRatePeriodCode(), pError, ignoreNodeList);

            //Verify CorporateDiscountCode
            isStringNodeEqual(CarTags.CORPORATE_DISCOUNT_CODE, expCarRate.getCorporateDiscountCode(), actCarRate.getCorporateDiscountCode(), pError, ignoreNodeList);

            //Verify RateCategoryCode
            isStringNodeEqual(CarTags.RATE_CATEGORY_CODE, expCarRate.getRateCategoryCode(), actCarRate.getRateCategoryCode(), pError, ignoreNodeList);

            //Verify PromoCode
            isStringNodeEqual(CarTags.PROMO_CODE, expCarRate.getPromoCode(), actCarRate.getPromoCode(), pError, ignoreNodeList);

            if (!StringUtils.isEmpty(pError.toString().trim())) {
                isEqual = false;
                if (errorMsg != null) {
                    errorMsg.append(pError.toString());
                }
            }
        }
        return isEqual;
    }

    public static boolean isRecurringPeriodEqual(RecurringPeriodType expectedRecurringPeriod, RecurringPeriodType actualRecurringPeriod,
                                                 StringBuilder eMsg, List<String> ignoreNodeList)
    {
        boolean isEqual = true;
        //if both null, return
        if (actualRecurringPeriod.getDateRange() == null && expectedRecurringPeriod.getDateRange() == null && expectedRecurringPeriod.getTimeRangeList() == null && actualRecurringPeriod.getTimeRangeList() == null)
        {
            return isEqual;
        }

        final boolean dateRangeBothNotNull = actualRecurringPeriod.getDateRange() != null && expectedRecurringPeriod.getDateRange() != null;
        final StringBuilder errorMsg = new StringBuilder();
        isTimeRangeListSizeEqual(expectedRecurringPeriod, actualRecurringPeriod, errorMsg);
        if (StringUtils.isEmpty(errorMsg.toString().trim()) && dateRangeBothNotNull)
        {
            isDateRangeTimeRangeEqual(expectedRecurringPeriod, actualRecurringPeriod, errorMsg);
        }

        if (!StringUtils.isEmpty(errorMsg.toString().trim()))
        {
            isEqual = false;
            if (eMsg != null)
            {
                eMsg.append(errorMsg.toString());
            }
        }
        return isEqual;
    }

    private static void isTimeRangeListSizeEqual(RecurringPeriodType expectedRecurringPeriod, RecurringPeriodType actualRecurringPeriod,
                                                 StringBuilder errorMsg) {
        final StringBuilder nullCheckError = new StringBuilder();
        if(isExpActObjBothNullCheck(expectedRecurringPeriod, actualRecurringPeriod, nullCheckError, "RecurringPeriod"))
        {
            return;
        }
        else if(nullCheckError.toString().trim().length() >0 )
        {
            errorMsg.append(nullCheckError.toString().trim());
            return;
        }

        if(isExpActTimeRangeListBothNullCheck(expectedRecurringPeriod.getTimeRangeList(), actualRecurringPeriod.getTimeRangeList(), nullCheckError)){
            return;
        }
        else if(nullCheckError.toString().trim().length() >0 )
        {
            errorMsg.append(nullCheckError.toString().trim());
            return;
        }

        if (expectedRecurringPeriod.getTimeRangeList().getTimeRange().size() != actualRecurringPeriod.getTimeRangeList().getTimeRange().size()) {
            errorMsg.append("PickUpLocation OperationTime's TimeRangeList count is not as expected, expected: ")
                    .append(expectedRecurringPeriod.getTimeRangeList().getTimeRange().size()).append(", actual: ")
                    .append(actualRecurringPeriod.getTimeRangeList().getTimeRange().size())
                    /*.append("!\n Actual TimeRangeList: \n")
                    .append(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actualRecurringPeriod.getTimeRangeList())))*/;

            /*errorMsg.append("Expected TimeRangeList: \n")
                    .append(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expectedRecurringPeriod.getTimeRangeList())));*/
        }
    }

    public static boolean isExpActTimeRangeListBothNullCheck(TimeRangeListType expTimeRageList,
                                                             TimeRangeListType actTimeRageList, StringBuilder errorMsg) {
        boolean isBothNull = false;
        if (isTimeRangeListNull(expTimeRageList) && isTimeRangeListNull(actTimeRageList)) {
            isBothNull = true;
        } else if (isTimeRangeListNull(expTimeRageList) || isTimeRangeListNull(actTimeRageList)) {
            errorMsg.append(" Expect TimeRangeList ").append(isTimeRangeListNull(expTimeRageList) ? "Null" : "Not Null")
                    .append(" Actual TimeRangeList ").append(isTimeRangeListNull(actTimeRageList) ? "Null" : "Not Null").append('\n');
        }
        return isBothNull;
    }

    private  static boolean isTimeRangeListNull(TimeRangeListType timeRangeList)
    {
        boolean isNull = false;
        if(null == timeRangeList || null == timeRangeList.getTimeRange() || timeRangeList.getTimeRange().isEmpty() )
        {
            isNull = true;
        }
        return isNull;
    }

    private static void isDateRangeTimeRangeEqual(RecurringPeriodType expectedRecurringPeriod, RecurringPeriodType actualRecurringPeriod,
                                                  StringBuilder errorMsg) {
        final StringBuilder nullCheckError = new StringBuilder();
        if(isExpActTimeRangeListBothNullCheck(expectedRecurringPeriod.getTimeRangeList(), actualRecurringPeriod.getTimeRangeList(), nullCheckError)){
            return;
        }
        else if(nullCheckError.toString().trim().length() >0 )
        {
            errorMsg.append(nullCheckError.toString().trim());
            return;
        }

        if (!(isDateTimeEqual(expectedRecurringPeriod.getDateRange().getMinDate(), actualRecurringPeriod.getDateRange().getMinDate(), null)
                && isDateTimeEqual(expectedRecurringPeriod.getDateRange().getMaxDate(), actualRecurringPeriod.getDateRange().getMaxDate(), null)
                && isDateTimeEqual(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(),
                actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), null)
                && isDateTimeEqual(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(),
                actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), null)
        )) {
            errorMsg.append("\nThe acutal CarPickupLocation/OpenSchedule DateRange/MinDate is ").append(actualRecurringPeriod.getDateRange().getMinDate())
                    .append(", but the expected value is ").append(expectedRecurringPeriod.getDateRange().getMinDate())
                    .append("\nThe acutal CarPickupLocation/OpenSchedule DateRange/MaxDate is ").append(actualRecurringPeriod.getDateRange().getMaxDate())
                    .append(", but the expected value is ").append(expectedRecurringPeriod.getDateRange().getMaxDate())
                    .append("\nThe acutal CarPickupLocation/OpenSchedule TimeRange/MinDate is ")
                    .append(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime().getDateTimeString())
                    .append(", but the expected value is ")
                    .append(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime().getDateTimeString())
                    .append("\nThe acutal CarPickupLocation/OpenSchedule TimeRange/MaxDate is ")
                    .append(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime().getDateTimeString())
                    .append(", but the expected value is ")
                    .append(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime().getDateTimeString()).append('\n');
        }
    }

    /*
    compare SegmentDateTimeRange
     */
    public static boolean isSegmentDateTimeRangeEqual(SegmentDateTimeRangeType expSegmentDateTime, SegmentDateTimeRangeType actSegmentDateTime,
                                                      StringBuilder errorMsg) {
        boolean isEqual = true;
        //result for compareTo is millisecond, we ashould can allow one minute difference
        final DateTime expStartDT = expSegmentDateTime.getStartDateTimeRange().getMinDateTime();
        final DateTime actStartDT = actSegmentDateTime.getStartDateTimeRange().getMinDateTime();
        final DateTime expEndDT = expSegmentDateTime.getEndDateTimeRange().getMinDateTime();
        final DateTime actEndDT = actSegmentDateTime.getEndDateTimeRange().getMinDateTime();

        if (DateTimeUtil.getDiffMinutes(expStartDT, actStartDT) > 1 || DateTimeUtil.getDiffMinutes(expEndDT, actEndDT) > 1) {
            isEqual = false;
            errorMsg.append("SegmentDateTimeRange Start/End DateTime is not expected, expected:").append(expSegmentDateTime.getStartDateTimeRange().getMinDateTime().toString()).
                    append('/').append(expSegmentDateTime.getEndDateTimeRange().getMinDateTime().toString()).append("actual:").
                    append(actSegmentDateTime.getStartDateTimeRange().getMinDateTime().toString()).
                    append('/').append(actSegmentDateTime.getEndDateTimeRange().getMinDateTime().toString()).append("!\n");
        }

        return isEqual;
    }

    public static boolean isCostListEqual(CostListType expectedCostList, CostListType actualCostList, StringBuilder eMsg, List<String> ignoreNodeList) {
        boolean isEqual = false;
        final StringBuilder errorMsg = new StringBuilder();
        if (!ignoreNodeList.contains(CarTags.CAR_COST_LIST)) {
            if (isExpActObjBothNullCheck(expectedCostList, actualCostList,
                    errorMsg, "Car CostList is null in Expect or Actual; ")) {
                return isEqual;
            } else if (StringUtils.isEmpty(errorMsg.toString().trim())) {
                if (expectedCostList.getCost().size() == actualCostList.getCost().size()) {
                    isCostListEqualBigLoop(expectedCostList, actualCostList, errorMsg, ignoreNodeList);
                } else {
                    errorMsg.append("CostList count is not expected, expected: ").append(expectedCostList.getCost().size())
                            .append(", actual: ").append(actualCostList.getCost().size())
                            /*.append("!\nActual CostList: ").append(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actualCostList)))
                            .append("\nExpected CostList: ").append(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expectedCostList)))*/;
                }
            }

            if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
                isEqual = false;
                if (eMsg != null) {
                    eMsg.append(errorMsg.append('\n').toString());
                }
            }
        }
        return isEqual;
    }

    private static void isCostListEqualBigLoop(CostListType expectedCostList, CostListType actualCostList, StringBuilder errorMsg, List<String> ignoreNodeList) {
        for (final CostType expectedCost : expectedCostList.getCost()) {
            if (!isCostListEqualSmallLoop(expectedCost, actualCostList, ignoreNodeList)) {
                errorMsg.append("The expected CostList/Cost ").append(expectedCost.getFinanceCategoryCode()).append(expectedCost.getDescriptionRawText())
                        .append(" can't be found in actual CostList/Cost");
            }
        }
    }

    public static boolean isCostListEqualSmallLoop(CostType expectedCost, CostListType actualCostList, List<String> ignoreNodeList) {
        boolean isEqual = false;
        for (final CostType actualCost : actualCostList.getCost()) {
            isEqual = isCostEqual(expectedCost, actualCost, ignoreNodeList);
            if (isEqual) {
                break;
            }
        }
        return isEqual;
    }

    public static boolean isCostEqual(CostType expectedCost, CostType actualCost, List<String> ignoreNodeList) {
        boolean isEqual = true;
//        boolean b1 = (StringUtils.isEmpty(expectedCost.getDescriptionRawText()) && StringUtils.isEmpty(actualCost.getDescriptionRawText()) ||
//                expectedCost.getDescriptionRawText().equals(actualCost.getDescriptionRawText()));

        boolean condIsEqual = expectedCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()
                .equals(actualCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());
        condIsEqual = condIsEqual&& expectedCost.getFinanceApplicationCode().equals(actualCost.getFinanceApplicationCode());
        condIsEqual = condIsEqual && expectedCost.getFinanceApplicationUnitCount() == actualCost.getFinanceApplicationUnitCount();
        condIsEqual = condIsEqual && expectedCost.getFinanceCategoryCode().equals(actualCost.getFinanceCategoryCode());

        if(!ignoreNodeList.contains(CarTags.LEGACY_FINANCE_KEY)) {
            condIsEqual = condIsEqual && expectedCost.getLegacyFinanceKey().getLegacyMonetaryClassID() == actualCost.getLegacyFinanceKey().getLegacyMonetaryClassID();
            condIsEqual = condIsEqual && expectedCost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID() == actualCost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID();
            condIsEqual = condIsEqual && expectedCost.getLegacyFinanceKey().getLegacyMonetaryCalculationID() == actualCost.getLegacyFinanceKey().getLegacyMonetaryCalculationID();
        }

        final double expAmount = CostPriceCalculator.calculateCostAmount(expectedCost, 0, false);
        final double actAmount = CostPriceCalculator.calculateCostAmount(actualCost, 0, false);
        if (!(//b1  && //Ignore description
                Math.abs(expAmount - actAmount) <= 0.01 && condIsEqual)) {
            isEqual = false;
        }

        return isEqual;
    }

    // CarMileage
    @SuppressWarnings("PMD")
    public static boolean isCarMileageEqual(CarMileageType expectedCarMileage, CarMileageType actualCarMileage, StringBuilder eMsg, List<String> ignoreNodeList) {
        boolean isEqual = true;

        if (!ignoreNodeList.contains(CarTags.CAR_MILEAGE)) {
            //if both null, return
            if (isBothNullMileage(expectedCarMileage, actualCarMileage)) {
                return isEqual;
            } else if ((expectedCarMileage == null && actualCarMileage != null) || (expectedCarMileage != null && actualCarMileage == null)) {
                return false;
            }
            final StringBuffer errorMsg = new StringBuffer();
            isEqual = isFreeDistanceEqual(expectedCarMileage.getFreeDistance(), actualCarMileage.getFreeDistance(), ignoreNodeList);
            isEqual = isEqual && isExtraCostPerDistanceEqual(expectedCarMileage.getExtraCostPerDistance(), actualCarMileage.getExtraCostPerDistance(), errorMsg);

            if (!isEqual) {
                errorMsg.append("The actual CarMileage: getFreeDistance()/getDistanceUnitCount():getExtraCostPerDistance()/ DistanceUnit:getExtraCostPerDistance()/DecimalPlaceCount:getExtraCostPerDistance()/Decimal : ==> ")
                        .append(actualCarMileage.getFreeDistance().getDistanceUnitCount()).append(':')
                        .append(actualCarMileage.getExtraCostPerDistance().getDistance().getDistanceUnit()).append(':')
                        .append(actualCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimalPlaceCount()).append(':')
                        .append(actualCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimal())

                        .append(" is not equal the Expected value : ==> ")
                        .append(expectedCarMileage.getFreeDistance().getDistanceUnitCount()).append(':')
                        .append(expectedCarMileage.getExtraCostPerDistance().getDistance().getDistanceUnit()).append(':')
                        .append(expectedCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimalPlaceCount()).append(':')
                        .append(expectedCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimal()).append('\n');
            }

            if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
                isEqual = false;
                if (eMsg != null) {
                    eMsg.append(errorMsg.toString());
                }
            }
        }
        return isEqual;
    }

    private static boolean isBothNullMileage(CarMileageType expectedCarMileage, CarMileageType actualCarMileage) {
        boolean isBothNullMileage = actualCarMileage == null || actualCarMileage.getFreeDistance() == null || (actualCarMileage.getFreeDistance().getDistanceUnit() == null &&
                actualCarMileage.getFreeDistance().getDistanceUnitCount() == 0);
        isBothNullMileage = isBothNullMileage && (expectedCarMileage == null || expectedCarMileage.getFreeDistance() == null ||
                (expectedCarMileage.getFreeDistance().getDistanceUnit() == null && expectedCarMileage.getFreeDistance().getDistanceUnitCount() == 0));
        return isBothNullMileage;
    }

    private static boolean isFreeDistanceEqual(DistanceType expectedDistance, DistanceType actualDistance, List<String> ignoreNodeList) {
        boolean isFreeDistanceEqual = true;

        if (!ignoreNodeList.contains(CarTags.CAR_MILEAGE_DISTANCEUNITCOUNT))
        {
            isFreeDistanceEqual = false;
            if (expectedDistance != null && actualDistance != null)
            {
                isFreeDistanceEqual = expectedDistance.getDistanceUnitCount().intValue() == actualDistance.getDistanceUnitCount().intValue();
                if (actualDistance.getDistanceUnitCount() == 0 && (expectedDistance.getDistanceUnitCount() == 0 || expectedDistance.getDistanceUnitCount() == -1))
                {
                    isFreeDistanceEqual = true;
                }
            }
        }
        return isFreeDistanceEqual;
    }

    private static boolean isExtraCostPerDistanceEqual(CostPerDistanceType expectedExtraCostPerDistance, CostPerDistanceType actualExtraCostPerDistance, StringBuffer errorMsg) {
        boolean isExtraCostPerDistanceEqual = true;
        StringBuilder eMsg = new StringBuilder();
        if (isExpActObjBothNullCheck(expectedExtraCostPerDistance, actualExtraCostPerDistance, eMsg, "CarMileage/ExtraCostPerDistance")) {
            return isExtraCostPerDistanceEqual;
        } else if (StringUtils.isEmpty(eMsg.toString().trim())) {
            if (isExpActObjBothNullCheck(expectedExtraCostPerDistance.getDistance(), actualExtraCostPerDistance.getDistance(), eMsg, "CarMileage/ExtraCostPerDistance/Distance")) {
                isExtraCostPerDistanceEqual = isExtraCostPerDistanceEqual && true;
            } else if (StringUtils.isEmpty(eMsg.toString())) {
                isExtraCostPerDistanceEqual = isExtraCostPerDistanceEqual && StringUtils.equalsIgnoreCase(expectedExtraCostPerDistance.getDistance().getDistanceUnit(),
                        actualExtraCostPerDistance.getDistance().getDistanceUnit());
            }else {
                isExtraCostPerDistanceEqual = false;
                errorMsg.append(eMsg.toString());
                eMsg = new StringBuilder();
            }

            if (isExpActObjBothNullCheck(expectedExtraCostPerDistance.getCostCurrencyAmount(), actualExtraCostPerDistance.getCostCurrencyAmount(), eMsg, "CarMileage/CostCurrencyAmount")
                    && isExpActObjBothNullCheck(expectedExtraCostPerDistance.getCostCurrencyAmount().getAmount(), actualExtraCostPerDistance.getCostCurrencyAmount().getAmount(), eMsg, "CarMileage/CostCurrencyAmount/Amount")) {
                isExtraCostPerDistanceEqual = isExtraCostPerDistanceEqual && true;
            } else if (StringUtils.isEmpty(eMsg.toString())) {
                isExtraCostPerDistanceEqual = isExtraCostPerDistanceEqual && expectedExtraCostPerDistance.getCostCurrencyAmount().getAmount().getDecimalPlaceCount() ==
                        actualExtraCostPerDistance.getCostCurrencyAmount().getAmount().getDecimalPlaceCount();
                isExtraCostPerDistanceEqual = isExtraCostPerDistanceEqual && expectedExtraCostPerDistance.getCostCurrencyAmount().getAmount().getDecimal() ==
                        actualExtraCostPerDistance.getCostCurrencyAmount().getAmount().getDecimal();
            }else
            {
                isExtraCostPerDistanceEqual = false;
            }
        }

        errorMsg.append(eMsg.toString());
        return isExtraCostPerDistanceEqual;
    }

    public static boolean isDateTimeEqual(DateTime expTime, DateTime actTime, StringBuilder eMsg) {
        boolean isEqual = true;

        final StringBuilder errorMsg = new StringBuilder();
        if (DateTimeUtil.getDiffMillis(expTime, actTime) > 1) {
            errorMsg.append(" DateTime is not expected, expected: ").append(expTime == null ? "Null" : expTime.toString())
                    .append(" actual: ").append(actTime == null ? "Null" : actTime.toString()).append("!\n");
        }

        if (errorMsg.toString().trim().length() > 0) {
            isEqual = false;
            if (eMsg != null) {
                eMsg.append(errorMsg.toString());
            }
        }
        return isEqual;
    }

    public static boolean isStringNodeEqual(String nodeName, String expValue, String actValue, StringBuilder errorMsg, List<String> ignoreNodeList) {
        //return true if node should be ignored
        boolean isEqual = true;
        if(!ignoreNodeList.contains(nodeName))
        {
            final StringBuilder eMsg = new StringBuilder();
            //is both null equal, if one is null the other is not null return false
            if (isExpActObjBothNullCheck(expValue, actValue, eMsg, ""))
            {
                return true;
            }else if (!StringUtils.isEmpty(eMsg.toString().trim()))
            {
                errorMsg.append(nodeName).append(eMsg);
                return false;
            }

            //value
            if (!(StringUtils.isEmpty(expValue) && StringUtils.isEmpty(actValue)) && !expValue.equals(actValue))
            {
                isEqual = false;
                errorMsg.append(nodeName).append(" value ").append(actValue).
                        append(" is not equal to expected:").append(expValue).append("!\n");
            }
        }

        return isEqual;
    }

    public static boolean isStringNodeEqual(String nodeName, String expValue, String actValue, List remarks , List<String> ignoreNodeList) {
        //return true if node should be ignored
        final StringBuilder tempError = new StringBuilder();
        CarNodeComparator.isStringNodeEqual(nodeName, expValue, actValue, tempError, new ArrayList<String>());
        if(tempError.toString().trim().length() > 0)
        {
            remarks.add(tempError.toString().trim());
            return false;
        }

        return true;
    }


    /**
     * @param expObj
     * @param actObj
     * @param errorMsg
     * @param eHeader
     * @return both null return true, one of them null other is not null will add errorMsg.
     */
    @SuppressWarnings("PMD")
    public static boolean isExpActObjBothNullCheck(Object expObj, Object actObj, StringBuilder errorMsg, String eHeader) {
        boolean isBothNull = false;
        if (expObj == null && actObj == null) {
            isBothNull = true;
        } else if ((expObj == null && actObj != null) || (expObj != null && actObj == null)) {
            errorMsg.append(eHeader)
                    .append(" Expect : ").append(null == expObj ? "Null" : "Not Null")
                    .append(" Actual : ").append(null == actObj ? "Null" : "Not Null").append('\n');
        }
        return isBothNull;
    }
}