package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationType;
import com.expedia.e3.data.cartypes.defn.v5.CarMileageType;
import com.expedia.e3.data.cartypes.defn.v5.CarPolicyListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRentalLimitsType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationRemarkType;
import com.expedia.e3.data.cartypes.defn.v5.CarReserveFieldOverridesType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionListType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.e3.data.financetypes.defn.v4.CostPriceType;
import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.CurrencyAmountType;
import com.expedia.e3.data.timetypes.defn.v4.OpenScheduleType;
import com.expedia.e3.data.timetypes.defn.v4.RecurringPeriodType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.UAPICommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.DateTimeUtil;
import com.expedia.s3.cars.framework.test.common.utils.HttpMessageSendUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.DataSourceHelper;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager.DomainType.CarSpecialEquipment;
import static com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon.logger;

/**
 * Created by yyang4 on 12/28/2016.
 */
public class UapiMapCommonVerification {

    // TravelerList
    public static void isTravelerListEqual(List<TravelerType> actualTravelerList, List<TravelerType> expectedTravelerList, boolean compareAddress, String header) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> travelerIgnoreList = new ArrayList<String>();
        travelerIgnoreList.add("personalTitle");
        travelerIgnoreList.add("middleName");
        travelerIgnoreList.add("suffixName");
        travelerIgnoreList.add("phoneExtensionNumber");
        travelerIgnoreList.add("databaseServer");
        travelerIgnoreList.add("databaseName");
        travelerIgnoreList.add("databaseTable");
        travelerIgnoreList.add("personalTitle");
        travelerIgnoreList.add("databaseField");
        travelerIgnoreList.add("databaseIDField");
        travelerIgnoreList.add("personalTitle");
        travelerIgnoreList.add("databaseIDValue");
        travelerIgnoreList.add("addressCategoryCode");
        travelerIgnoreList.add("companyNameAddressLine");
        travelerIgnoreList.add("thirdAddressLine");
        travelerIgnoreList.add("fourthAddressLine");
        travelerIgnoreList.add("fifthAddressLine");
        travelerIgnoreList.add("provinceName");
        travelerIgnoreList.add("countryAlpha3Code");
        travelerIgnoreList.add("emailAddressEntryList");
        travelerIgnoreList.add("passportList");
        travelerIgnoreList.add("userKey");
        travelerIgnoreList.add("sequence");
        if (CompareUtil.isObjEmpty(expectedTravelerList.get(0).getPerson().getAge())) {
            travelerIgnoreList.add("age");
        }
        if (CompareUtil.isObjEmpty(expectedTravelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram())) {
            travelerIgnoreList.add("loyaltyProgramList");
        }
        if (!compareAddress) {
            travelerIgnoreList.add("addressList");
        }
        if (CompareUtil.isObjEmpty(actualTravelerList.get(0).getContactInformation()) || CompareUtil.isObjEmpty(expectedTravelerList.get(0).getContactInformation())) {
            travelerIgnoreList.add("contactInformation");
        }

        if (!CompareUtil.compareObject(expectedTravelerList, actualTravelerList, travelerIgnoreList, errorMsg.append("Traveler: "))) {
            Assert.fail(header + errorMsg.toString());
        }


    }

    public static boolean isSameCarInventoryKey(CarInventoryKeyType actualCarInventoryKey, CarInventoryKeyType expectedCarInventoryKey) {
        final List<String> invertoryNeedFieldList = new ArrayList<String>();
        invertoryNeedFieldList.add("supplySubsetID");
        invertoryNeedFieldList.add("carCatalogKey");
        invertoryNeedFieldList.add("vendorSupplierID");
        invertoryNeedFieldList.add("carVehicle");
        invertoryNeedFieldList.add("carCategoryCode");
        invertoryNeedFieldList.add("carTypeCode");
        invertoryNeedFieldList.add("carTransmissionDriveCode");
        invertoryNeedFieldList.add("carFuelACCode");
        return CompareUtil.compareObjectOnlyForNeedField(expectedCarInventoryKey, actualCarInventoryKey, invertoryNeedFieldList, null);

    }

    // CarInventoryKey
    public static void isCarInventoryKeyEqual(CarInventoryKeyType actualCarInventoryKey, CarInventoryKeyType expectedCarInventoryKey, String headerMsg, boolean ignoreCarRate, boolean ignoreCarRateQualifierCode, boolean ignoreLoyaltyProgramMembershipCode, boolean ignoreSupplysubsetID) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> invertoryIgnoreList = new ArrayList<String>();

        invertoryIgnoreList.add("packageBoolean");
        invertoryIgnoreList.add("postPurchaseBoolean");
        invertoryIgnoreList.add("productCategoryCodeList");
        invertoryIgnoreList.add("driverAgeYearCount");
        invertoryIgnoreList.add("collectionBoolean");
        invertoryIgnoreList.add("deliveryBoolean");
        invertoryIgnoreList.add("outOfOfficeHourBoolean");
        invertoryIgnoreList.add("carAgreementID");
        invertoryIgnoreList.add("carVendorAgreementCode");
        invertoryIgnoreList.add("loyaltyProgramCategoryCode");
        invertoryIgnoreList.add("loyaltyProgramCode");
        if (CompareUtil.isObjEmpty(actualCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID())
                || CompareUtil.isObjEmpty(actualCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID())
                || CompareUtil.isObjEmpty(expectedCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID())
                || CompareUtil.isObjEmpty(expectedCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey().getCarVendorLocationID())) {
            invertoryIgnoreList.add("carVendorLocationID");
        }
        if (ignoreCarRate) {
            invertoryIgnoreList.add("carRate");
        }
        if (ignoreCarRateQualifierCode) {
            invertoryIgnoreList.add("carRateQualifierCode");
        }
        if (ignoreLoyaltyProgramMembershipCode) {
            invertoryIgnoreList.add("loyaltyProgramMembershipCode");
        }
        if (ignoreSupplysubsetID) {
            invertoryIgnoreList.add("supplySubsetID");
        }

        if (!CompareUtil.compareObject(expectedCarInventoryKey, actualCarInventoryKey, invertoryIgnoreList, errorMsg.append("CarInventory: "))) {
            Assert.fail(headerMsg + errorMsg.toString());
        }
    }

    // <summary>
    // verify if CarLocationKey is equal(can be used for StartCarLocationKey, EndCarLocationKey)
    // </summary>
    // <param name="expectedCarLocation"></param>
    // <param name="actualCarLocation"></param>
    // <param name="errorHeader"></param>
    public static void isCarLocationTypeEqual(CarLocationType expectedCarLocation, CarLocationType actualCarLocation, String errorHeader) {
        //verify OpenSchedule
        isOpenScheduleEqual(actualCarLocation.getOpenSchedule(), expectedCarLocation.getOpenSchedule(), errorHeader + "OpenSchedule: ");
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> locationIgnoreList = new ArrayList<String>();
        locationIgnoreList.add("carVendorLocationID");
        locationIgnoreList.add("supplierID");
        locationIgnoreList.add("regionID");
        locationIgnoreList.add("latLong");
        locationIgnoreList.add("providerLocationCode");
        locationIgnoreList.add("providerLocationType");
        locationIgnoreList.add("providerLocationSupplierCode");
        locationIgnoreList.add("providerLocationCountryCode");
        locationIgnoreList.add("providerLocationIataCode");
        locationIgnoreList.add("providerLocationShuttleCategoryCode");
        locationIgnoreList.add("addressCategoryCode");
        locationIgnoreList.add("companyNameAddressLine");
        locationIgnoreList.add("postalCode");
        locationIgnoreList.add("phoneExtensionNumber");
        locationIgnoreList.add("databaseServer");
        locationIgnoreList.add("databaseName");
        locationIgnoreList.add("databaseTable");
        locationIgnoreList.add("databaseField");
        locationIgnoreList.add("databaseIDField");
        locationIgnoreList.add("databaseIDValue");
        locationIgnoreList.add("openSchedule");
        if (!CompareUtil.compareObject(expectedCarLocation, actualCarLocation, locationIgnoreList, errorMsg)) {
            Assert.fail(errorHeader + errorMsg.toString());
        }
    }

    // <summary>
    // verify if CarLocationKeyType is equal(can be used for StartCarLocationKey, EndCarLocationKey)
    // </summary>
    // <param name="expectedCarLocation"></param>
    // <param name="actualCarLocation"></param>
    // <param name="errorHeader"></param>
    public static boolean isCarLocationKeyTypeEqual(CarLocationKeyType expectedCarLocation, CarLocationKeyType actualCarLocation) {
        //verify OpenSchedule
        final List<String> locationNeedFieldList = new ArrayList<String>();
        locationNeedFieldList.add("locationCode");
        if (!CompareUtil.isObjEmpty(actualCarLocation.getCarLocationCategoryCode()) && !CompareUtil.isObjEmpty(expectedCarLocation.getCarLocationCategoryCode())) {
            locationNeedFieldList.add("carLocationCategoryCode");
        }
        if (!CompareUtil.isObjEmpty(actualCarLocation.getSupplierRawText()) && !CompareUtil.isObjEmpty(expectedCarLocation.getSupplierRawText())) {
            locationNeedFieldList.add("supplierRawText");
        }
        return CompareUtil.compareObjectOnlyForNeedField(expectedCarLocation, actualCarLocation, locationNeedFieldList, null);
    }

    // OpenSchedule
    public static void isOpenScheduleEqual(OpenScheduleType actualOpenSchedule, OpenScheduleType expectedOpenSchedule, String errorMsgHead) {
        final StringBuilder errorMsg = new StringBuilder();
        boolean isEqual = false;
        for (RecurringPeriodType expectedRecurringPeriod : expectedOpenSchedule.getNormalRecurringPeriodList().getRecurringPeriod()) {
            for (RecurringPeriodType actualRecurringPeriod : actualOpenSchedule.getNormalRecurringPeriodList().getRecurringPeriod()) {
                boolean isTimeRangeEqual = false;
                if ((CompareUtil.isObjEmpty(expectedRecurringPeriod.getTimeRangeList()) || CompareUtil.isObjEmpty(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0)))
                        && (CompareUtil.isObjEmpty(actualRecurringPeriod.getTimeRangeList()) || CompareUtil.isObjEmpty(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0)))) {
                    isTimeRangeEqual = true;
                } else if (CompareUtil.compareObject(DateTimeUtil.getFormatString(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), DateTimeUtil.FORMAT3), DateTimeUtil.getFormatString(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), DateTimeUtil.FORMAT3), null, null)
                        && CompareUtil.compareObject(DateTimeUtil.getFormatString(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime(), DateTimeUtil.FORMAT3), DateTimeUtil.getFormatString(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime(), DateTimeUtil.FORMAT3), null, null)) {
                    isTimeRangeEqual = true;
                }
                if (CompareUtil.compareObject(DateTimeUtil.getFormatString(expectedRecurringPeriod.getDateRange().getMinDate(), DateTimeUtil.FORMAT1), DateTimeUtil.getFormatString(actualRecurringPeriod.getDateRange().getMinDate(), DateTimeUtil.FORMAT1), null, null)
                        && CompareUtil.compareObject(DateTimeUtil.getFormatString(expectedRecurringPeriod.getDateRange().getMaxDate(), DateTimeUtil.FORMAT1), DateTimeUtil.getFormatString(actualRecurringPeriod.getDateRange().getMaxDate(), DateTimeUtil.FORMAT1), null, null)
                        && isTimeRangeEqual) {
                    isEqual = true;
                    break;
                } else {
                    errorMsg.append(String.format("\nThe acutal %s/OpenSchedule DateRange/MinDate is %s\n", errorMsgHead, DateTimeUtil.getFormatString(actualRecurringPeriod.getDateRange().getMinDate(), DateTimeUtil.FORMAT1)));
                    errorMsg.append(String.format("\nThe acutal %s/OpenSchedule DateRange/MaxDate is %s\n", errorMsgHead, DateTimeUtil.getFormatString(actualRecurringPeriod.getDateRange().getMaxDate(), DateTimeUtil.FORMAT1)));
                    errorMsg.append(String.format("\nThe acutal %s/OpenSchedule TimeRange/MinDate is %s\n", errorMsgHead, DateTimeUtil.getFormatString(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), DateTimeUtil.FORMAT3)));
                    errorMsg.append(String.format("\nThe acutal %s/OpenSchedule TimeRange/MinDate is %s\n", errorMsgHead, DateTimeUtil.getFormatString(actualRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime(), DateTimeUtil.FORMAT3)));
                }
            }
            if (isEqual) {
                errorMsg.setLength(0);
                isEqual = false;
            } else {
                errorMsg.append(String.format("\nThe expected %s/OpenSchedule DateRange/MinDate is %s", errorMsgHead, DateTimeUtil.getFormatString(expectedRecurringPeriod.getDateRange().getMinDate(), DateTimeUtil.FORMAT1)));
                errorMsg.append(String.format("\nThe expected %s/OpenSchedule DateRange/MaxDate is %s", errorMsgHead, DateTimeUtil.getFormatString(expectedRecurringPeriod.getDateRange().getMaxDate(), DateTimeUtil.FORMAT1)));
                errorMsg.append(String.format("\nThe expected %s/OpenSchedule TimeRange/MinDate is %s", errorMsgHead, DateTimeUtil.getFormatString(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMinTime(), DateTimeUtil.FORMAT3)));
                errorMsg.append(String.format("\nThe expected %s/OpenSchedule TimeRange/MinDate is %s\r\n", errorMsgHead, DateTimeUtil.getFormatString(expectedRecurringPeriod.getTimeRangeList().getTimeRange().get(0).getMaxTime(), DateTimeUtil.FORMAT3)));
                errorMsg.append("");
            }
        }
        if (errorMsg.length() > 0) {
            Assert.fail(errorMsgHead + errorMsg);
        }
    }

    public static void policeListMatch(CarPolicyListType policeMase, CarPolicyListType policeExpected, String errorMsgHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> policyIgnoreList = new ArrayList<String>();
        policyIgnoreList.add("sequence");
        if (!CompareUtil.compareObject(policeExpected, policeMase, policyIgnoreList, errorMsg)) {
            Assert.fail(errorMsgHeader + errorMsg.toString());
        }
    }

    public static void conditionalCostPriceListMatch(List<CostPriceType> costPriceListMasr, List<CostPriceType> costPriceListExpected, String errorMsgHeader) {
        if (costPriceListMasr.size() != costPriceListExpected.size()) {
            Assert.fail(errorMsgHeader + "The number of ConditionalCostPriceList in Maserati response " + costPriceListMasr.size() +
                    "doesn't match in expected count " + costPriceListExpected.size());
            return;
        }
        for (int count = 0; count < costPriceListMasr.size(); count++) {
            CurrencyAmountType amountMaserati = costPriceListMasr.get(count).getCost().getMultiplierOrAmount().getCurrencyAmount();
            CurrencyAmountType amountExpected = costPriceListExpected.get(count).getCost().getMultiplierOrAmount().getCurrencyAmount();
            double expectedAmount = (double) (amountExpected.getAmount().getDecimal() / Math.pow(10, amountExpected.getAmount().getDecimalPlaceCount()));
            double actualAmount = (double) (amountMaserati.getAmount().getDecimal() / Math.pow(10, amountMaserati.getAmount().getDecimalPlaceCount()));

            if (!amountMaserati.getCurrencyCode().equals(amountExpected.getCurrencyCode())) {
                Assert.fail(errorMsgHeader + String.format("ConditionalCostPriceList row [%s] CurrencyCode mach failed."
                                + "expected CurrencyCode=%s, actual value =%s", count,
                        amountExpected.getCurrencyCode(), amountMaserati.getCurrencyCode()));
            }
            if (!(Math.abs(actualAmount - expectedAmount) <= 0.01)) {
                Assert.fail(errorMsgHeader + String.format("ConditionalCostPriceList row [%s] Amount mach failed "
                                + "expected Amount=%s, actual value=%s", count,
                        expectedAmount, actualAmount));
            }
            if (!CompareUtil.compareObject(costPriceListMasr.get(count).getCost().getFinanceCategoryCode(), costPriceListExpected.get(count).getCost().getFinanceCategoryCode(), null, null)) {
                Assert.fail(errorMsgHeader + String.format("ConditionalCostPriceList row [%s] expected cost.FinanceCategoryCode={%s}, actual value={%s}", count,
                        costPriceListExpected.get(count).getCost().getFinanceCategoryCode(), costPriceListMasr.get(count).getCost().getFinanceCategoryCode()));
            }
            if (!CompareUtil.compareObject(costPriceListMasr.get(count).getCost().getFinanceApplicationCode(), costPriceListExpected.get(count).getCost().getFinanceApplicationCode(), null, null)) {
                Assert.fail(errorMsgHeader + String.format("ConditionalCostPriceList row [%s] expected cost.FinanceApplicationCode=%s, actual value =%s", count,
                        costPriceListExpected.get(count).getCost().getFinanceApplicationCode(), costPriceListMasr.get(count).getCost().getFinanceApplicationCode()));
            }
            if (!CompareUtil.compareObject(costPriceListMasr.get(count).getCost().getFinanceApplicationUnitCount(), costPriceListExpected.get(count).getCost().getFinanceApplicationUnitCount(), null, null)) {
                Assert.fail(errorMsgHeader + String.format("ConditionalCostPriceList row[%s] expected  cost.FinanceApplicationUnitCount=%s, actual value =%s", count,
                        costPriceListExpected.get(count).getCost().getFinanceApplicationUnitCount(), costPriceListMasr.get(count).getCost().getFinanceApplicationUnitCount()));
            }
        }
    }

    public static void carVehicleOptionMatch(CarVehicleOptionListType expCarvehicle, CarVehicleOptionListType maseCarvehicle, String errorMsgHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> vehicleIgnoreList = new ArrayList<String>();
        vehicleIgnoreList.add("price");
        vehicleIgnoreList.add("carVehicleOptionMaxCount");
        vehicleIgnoreList.add("carVehicleOptionMaxCharge");
        vehicleIgnoreList.add("legacyFinanceKey");
        vehicleIgnoreList.add("referenceList");
        vehicleIgnoreList.add("descriptionRawText");
        vehicleIgnoreList.add("multiplier");
        if (!CompareUtil.compareObject(expCarvehicle.getCarVehicleOption(), maseCarvehicle.getCarVehicleOption(), vehicleIgnoreList, errorMsg)) {
            Assert.fail(errorMsgHeader + errorMsg.toString());
        }
    }

    public static void carRentalLimitsMatch(CarRentalLimitsType actualRentalLimits, CarRentalLimitsType expectedCarRentalLimits, String errorMsgHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> rentalLimitsIgnoreList = new ArrayList<String>();
        rentalLimitsIgnoreList.add("latestReturnTime");
        rentalLimitsIgnoreList.add("minDurationCount");
        if (!CompareUtil.compareObject(expectedCarRentalLimits, actualRentalLimits, rentalLimitsIgnoreList, errorMsg)) {
            Assert.fail(errorMsgHeader + errorMsg.toString());
        }
    }

    //Compare the price with financeCategoryCode or currencyCode for 2 PriceLists.
    public static void verifyCertainCostInCostList(List<CostType> expCostList, List<CostType> actualCostList, String financeCategoryCode, String currencyCode) {
        final StringBuilder strBuilder = new StringBuilder();
        boolean exist = false;
        for (final CostType expCost : expCostList) {
            if (financeCategoryCode.equals(expCost.getFinanceCategoryCode()) && (CompareUtil.isObjEmpty(currencyCode) || CompareUtil.compareObject(expCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), currencyCode, null, null))) {
                final long tempExp = expCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
                final long decimalCountExp = expCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                final BigDecimal expectedTotalPrice = new BigDecimal(tempExp / Math.pow(10, decimalCountExp)).setScale(4, RoundingMode.HALF_UP);
                for (final CostType actualCost : actualCostList) {
                    if (CompareUtil.compareObject(expCost.getFinanceCategoryCode(), actualCost.getFinanceCategoryCode(), null, null)
                            && CompareUtil.compareObject(expCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), actualCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), null, null)
                            && CompareUtil.compareObject(expCost.getLegacyFinanceKey(), actualCost.getLegacyFinanceKey(), null, null)) {
                        exist = true;
                        final long tempActual = actualCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal();
                        final long decimalCountActual = actualCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount();
                        final BigDecimal actualTotalPrice = new BigDecimal(tempActual / Math.pow(10, decimalCountActual)).setScale(4, RoundingMode.HALF_UP);
                        if (Math.abs(expectedTotalPrice.doubleValue() - actualTotalPrice.doubleValue()) > 0.01) {
                            Assert.fail(String.format("Mapping cost failed - actual value is [%s] but expected value [%s] for CurrencyCode={%s} FinaceCatetgoryCode={%s}",
                                    actualTotalPrice, expectedTotalPrice, actualCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), financeCategoryCode));

                        }
                        break;
                    }
                }
                if (exist == false) {
                    Assert.fail(String.format("can't find in actual the expected cost =%s for CurrencyCode=%s for FinaceCatetgoryCode=%s",
                            expectedTotalPrice, expCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), financeCategoryCode));
                } else {
                    exist = false;
                }
            }
        }

    }

    // ReferenceList
    public static void isReferenceListEqual(List<ReferenceType> actualReferenceList, List<ReferenceType> expectedReferenceList, String errorHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> referenceIgnoreList = new ArrayList<String>(Arrays.asList("entityKey"));
        if (!CompareUtil.compareObject(expectedReferenceList, actualReferenceList, referenceIgnoreList, errorMsg.append("ReferenceList: "))) {
            Assert.fail(errorHeader + errorMsg.toString());
        }
    }

    // <summary>
    // Verify CarReserveFieldOverrides  is equal
    // </summary>
    // <param name="errorMsg"></param>
    // <param name="actualCarReservationRemarkList"></param>
    // <param name="expectedCarReservationRemarkList"></param>
    // <param name="errorHeader"></param>
    public static void isCarReserveFieldOverridesEqual(CarReserveFieldOverridesType actualCarReserveFieldOverrides, CarReserveFieldOverridesType expectedCarReserveFieldOverrides, String errorHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> rateIgnoreList = new ArrayList<String>();
        rateIgnoreList.add("loyaltyProgramCategoryCode");
        rateIgnoreList.add("loyaltyProgramCode");
        rateIgnoreList.add("sequence");
        rateIgnoreList.add("carRateQualifierCode");
        rateIgnoreList.add("carAgreementID");
        rateIgnoreList.add("carVendorAgreementCode");
        if (!CompareUtil.compareObject(expectedCarReserveFieldOverrides.getCarRate(), actualCarReserveFieldOverrides.getCarRate(), rateIgnoreList, errorMsg.append("CarRate: "))) {
            Assert.fail(errorHeader + errorMsg.toString());
        }

        final List<String> overridesIgnoreList = new ArrayList<String>();
        overridesIgnoreList.add("entityKey");
        overridesIgnoreList.add("loyaltyProgramCategoryCode;");
        overridesIgnoreList.add("sequence");
        overridesIgnoreList.add("bookingSource");
        overridesIgnoreList.add("carRate");
        if (!CompareUtil.compareObject(expectedCarReserveFieldOverrides, actualCarReserveFieldOverrides, overridesIgnoreList, errorMsg.append("CarReserveFieldOverrides: "))) {
            Assert.fail(errorHeader + errorMsg.toString());
        }
    }

    // <summary>
    // Compare CarSpecialEquipmentList by CarSpecialEquipmentCode directly, don't convert Domain value as above isCarSpecialEquipmentListEqual method
    // </summary>
    // <param name="errorMsg"></param>
    // <param name="actualCarSpecialEquipmentList"></param>
    // <param name="expectedCarSpecialEquipmentList"></param>
    // <param name="errorHeader"></param>
    public static void isCarSpecialEquipmentListEqualDirectCompare(List<CarSpecialEquipmentType> actualCarSpecialEquipmentList, List<CarSpecialEquipmentType> expectedCarSpecialEquipmentList, String errorHeader) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> specialEquipIgnoreList = new ArrayList<String>();
        specialEquipIgnoreList.add("bookingStateCode");
        specialEquipIgnoreList.add("carSpecialEquipmentCount");
        //special equipment
        if (!CompareUtil.compareObject(expectedCarSpecialEquipmentList, actualCarSpecialEquipmentList, specialEquipIgnoreList, errorMsg.append("CarSpecialEquipment:CarSpecialEquipmentCode"))) {
            Assert.fail(errorHeader + errorMsg.toString());
        }
    }


    // <summary>
    // Verify CarReservationRemarkList is equal
    // </summary>
    // <param name="errorMsg"></param>
    // <param name="actualCarReservationRemarkList"></param>
    // <param name="expectedCarReservationRemarkList"></param>
    // <param name="errorHeader"></param>
    public static void isCarReservationRemarkListEqual(List<CarReservationRemarkType> actualCarReservationRemarkList, List<CarReservationRemarkType> expectedCarReservationRemarkList) {
        boolean isEqual = false;
        for (int i = 0; i < expectedCarReservationRemarkList.size(); i++) {
            //Default CarReservationRemarkCategoryCode is "5"
            if (CompareUtil.isObjEmpty(expectedCarReservationRemarkList.get(i).getCarReservationRemarkCategoryCode())) {
                expectedCarReservationRemarkList.get(i).setCarReservationRemarkCategoryCode("5");
            }
            for (int j = 0; j < actualCarReservationRemarkList.size(); j++) {
                if (CompareUtil.compareObject(expectedCarReservationRemarkList.get(i), actualCarReservationRemarkList.get(j), null, null)) {
                    isEqual = true;
                    break;
                }
            }

            if (isEqual) {
                isEqual = false;
            } else {
                Assert.fail(String.format("The expected CarReservationRemark: CarReservationRemarkText/CarReservationRemarkCategoryCode=%s/%s can't find in the actual CarReservationRemark Node.",
                        expectedCarReservationRemarkList.get(i).getCarReservationRemarkText(), expectedCarReservationRemarkList.get(i).getCarReservationRemarkCategoryCode()));
            }
        }
    }

    //AdvisoryTextList
    public static void isAdvisoryTextListEqual(List<String> actualAdvisoryList, List<String> expctedAdvisoryList) {
        StringBuilder errorMsg = new StringBuilder("The actual AdvisoryTextList is not equal the expected values:");
        if (actualAdvisoryList.size() != expctedAdvisoryList.size()) {
            Assert.fail(String.format("The actual counts of AdrisoryTextList=%s is not equal the expcted value=%s ", actualAdvisoryList.size(), expctedAdvisoryList.size()));
        }
        int textCount = 0;
        for (String actualValue : actualAdvisoryList) {
            for (String expctedValue : expctedAdvisoryList) {
                errorMsg.append(expctedValue + "\r\n");
                if (actualValue.equals(expctedValue)) {
                    textCount++;
                    break;
                }
            }
        }

        if (textCount != actualAdvisoryList.size()) {
            Assert.fail(errorMsg.toString());
        }
    }

    // CarMileage
    public static void isCarMileageEqual(CarMileageType actualCarMileage, CarMileageType expectedCarMileage, String errorMsgHead) {
        final StringBuilder errorMsg = new StringBuilder();
        //if both null, return
        if ((CompareUtil.isObjEmpty(actualCarMileage) || CompareUtil.isObjEmpty(actualCarMileage.getFreeDistance()) || (CompareUtil.isObjEmpty(actualCarMileage.getFreeDistance().getDistanceUnit()) &&
                actualCarMileage.getFreeDistance().getDistanceUnitCount().intValue() == 0)) && (CompareUtil.isObjEmpty(expectedCarMileage) || CompareUtil.isObjEmpty(expectedCarMileage.getFreeDistance()) ||
                (CompareUtil.isObjEmpty(expectedCarMileage.getFreeDistance().getDistanceUnit()) && expectedCarMileage.getFreeDistance().getDistanceUnitCount().intValue() == 0))) {
            return;
        }
        //bool req = string.Equals(expectedCarMileage.ExtraCostPerDistance.Distance.DistanceUnit, actualCarMileage.ExtraCostPerDistance.Distance.DistanceUnit, StringComparison.OrdinalIgnoreCase);
        boolean req1 = false;
        if (!CompareUtil.isObjEmpty(expectedCarMileage) && !CompareUtil.isObjEmpty(actualCarMileage.getFreeDistance())) {
            req1 = CompareUtil.compareObject(expectedCarMileage.getFreeDistance().getDistanceUnitCount(), actualCarMileage.getFreeDistance().getDistanceUnitCount(), null, errorMsg.append("DistanceUnitCount: "));
            if (actualCarMileage.getFreeDistance().getDistanceUnitCount().intValue() == 0 && (expectedCarMileage.getFreeDistance().getDistanceUnitCount().intValue() == 0 ||
                    expectedCarMileage.getFreeDistance().getDistanceUnitCount().intValue() == -1)) {
                req1 = true;
            }
        }
        final List<String> ignoreList = new ArrayList<>(Arrays.asList("distanceUnitCount", "currencyCode"));
        if (!(req1 && CompareUtil.compareObject(actualCarMileage.getExtraCostPerDistance(), expectedCarMileage.getExtraCostPerDistance(), ignoreList, errorMsg.append("ExtraCostPerDistance: ")))) {
            Assert.fail(errorMsgHead + String.format("The actual CarMileage: FreeDistance/DistanceUnitCount:ExtraCostPerDistance/DistanceUnit:ExtraCostPerDistance/DecimalPlaceCount:ExtraCostPerDistance/Decimal-%s:%s:%s:%s is not equal the value-%s:%s:%s:%s\r\n",
                    actualCarMileage.getFreeDistance().getDistanceUnitCount(), actualCarMileage.getExtraCostPerDistance().getDistance().getDistanceUnit(), actualCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimalPlaceCount(), actualCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimal(),
                    expectedCarMileage.getFreeDistance().getDistanceUnitCount(), expectedCarMileage.getExtraCostPerDistance().getDistance().getDistanceUnit(), expectedCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimalPlaceCount(), expectedCarMileage.getExtraCostPerDistance().getCostCurrencyAmount().getAmount().getDecimal()));
        }
    }

    //Compare the 2 Costlists are equal
    public static void compareCostList(List<CostType> actualCostList, List<CostType> expectedCostList, String curencyCode, String guid, boolean compareCount, HttpClient httpClient) throws DataAccessException, ParserConfigurationException {
        final Map<String, Long> actualCount = new HashMap<String, Long>();
        final Map<String, Long> expectedCount = new HashMap<String, Long>();
        final String posuCurrency = getPOSuCurrencyCode(actualCostList, curencyCode);
        double exchangeRate = getCurrencyConversionRate(posuCurrency, curencyCode, guid, httpClient);
        String ignoreDescription = "Merchant Car Expedia Cancellation Fee";
        final List<String> ignoreList = new ArrayList<>(Arrays.asList("multiplierOrAmount", "referenceList", "descriptionRawText", "multiplier", "amount"));
        final StringBuilder errorMsg = new StringBuilder();
        boolean exist;
        double actualAmount;
        double expectedAmount;
        CostType posuCost;

        for (CostType expectedCost : expectedCostList) {
            exist = false;
            //don't validate drop charge
            if (!CompareUtil.isObjEmpty(expectedCost.getDescriptionRawText()) && "DROP CHARGE".equals(expectedCost.getDescriptionRawText().toUpperCase())) {
                continue;
            }
            expectedAmount = calculateCostAmount(expectedCost, 0, false);
            if (!CompareUtil.compareObject(expectedCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), posuCurrency, null, errorMsg) && "USD".equals(posuCurrency)) {
                //get a posucurrency cost and rate to calculate, because SCS poscurrency Amount and BS poscurrency Amount are quite different.
                posuCost = posUCostGetByPosExpectedCost(expectedCostList, expectedCost, posuCurrency);
                expectedAmount = exchangeRate * calculateCostAmount(posuCost, 0, false);
            }
            for (CostType actualCost : actualCostList) {
                actualAmount = calculateCostAmount(actualCost, 0, false);
                if (CompareUtil.compareObject(actualCost, expectedCost, ignoreList, errorMsg) && Math.abs(actualAmount - expectedAmount) <= 0.01) {
                    exist = true;
                    break;
                }
            }
            if (exist == false) {
                StringBuilder errStr = new StringBuilder();
                errStr.append(String.format("The expected cost - %s with currencyCode=%s can't find in actual costlist, the expected cost detail is: \n",
                        expectedCost.getDescriptionRawText(), expectedCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()));
                errStr.append(String.format("CurrencyCode: %s \n", expectedCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()));
                errStr.append(String.format("DecimalPlaceCount: %s \n", expectedCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount()));
                errStr.append(String.format("Decimal: %s \n", expectedCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal()));
                errStr.append(String.format("FinanceCategoryCode: %s \n", expectedCost.getFinanceCategoryCode()));
                errStr.append(String.format("FinanceApplicationCode: %s \n", expectedCost.getFinanceApplicationCode()));
                errStr.append(String.format("FinanceApplicationUnitCount: %s \n", expectedCost.getFinanceApplicationUnitCount()));
                errStr.append(String.format("LegacyMonetaryClassID: %s \n", expectedCost.getLegacyFinanceKey().getLegacyMonetaryClassID()));
                errStr.append(String.format("LegacyMonetaryCalculationSystemID: %s \n", expectedCost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID()));
                errStr.append(String.format("LegacyMonetaryCalculationID:%s \n", expectedCost.getLegacyFinanceKey().getLegacyMonetaryCalculationID()));
                errStr.append(String.format("DescriptionRawText: %s \n", expectedCost.getDescriptionRawText()));
                Assert.fail(errStr.toString());
            }

            // if no error, then count currencycode
            if (!CompareUtil.compareObject(ignoreDescription, expectedCost.getDescriptionRawText(), null, errorMsg)) {
                final String key = expectedCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                if (expectedCount.containsKey(key)) {
                    expectedCount.put(key, expectedCount.get(key) + 1);
                } else {
                    expectedCount.put(key, 1L);
                }
            }
        }

        for (CostType actualCost : actualCostList) {
            //don't validate drop charge
            if (!CompareUtil.isObjEmpty(actualCost.getDescriptionRawText()) && "DROP CHARGE".equals(actualCost.getDescriptionRawText().toUpperCase())) {
                continue;
            }
            if (!CompareUtil.compareObject(ignoreDescription, actualCost.getDescriptionRawText(), null, errorMsg)) {
                final String key = actualCost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                if (actualCount.containsKey(key)) {
                    actualCount.put(key, actualCount.get(key) + 1);
                } else {
                    actualCount.put(key, 1L);
                }
            }
        }
        //compare count
        if (compareCount) {
            countStatisCompare(expectedCount, actualCount, false);
        }
    }

    /// <summary>
    /// Verify costs addup to total, note: not every CostList can do this verification, E.g: MN car may have dynamic commission and transaction fee
    /// </summary>
    /// <param name="costList"></param>
    /// <returns></returns>
    public static void verifyCostAddupToTotal(List<CostType> costList) {
        final StringBuilder strBuilder = new StringBuilder();
        double addUpAmount = 0;
        double totalAmount = 0;
        for (CostType cost : costList) {
            if ("Total".equals(cost.getFinanceCategoryCode())) {
                totalAmount += calculateCostAmount(cost, 0, true);
            } else {
                addUpAmount += calculateCostAmount(cost, 0, true);
            }
        }

        if (Math.abs(addUpAmount - totalAmount) > 0.01) {
            Assert.fail(String.format("Costs don't add up to Total Cost, addUp amount: %s, total amount: %s!\r\n", addUpAmount, totalAmount));
        }
    }

    public static void countStatisCompare(Map<String, Long> ccountDic, Map<String, Long> pcountDic, boolean isCompareCount) {
        final StringBuilder errorMsg = new StringBuilder();
        if (isCompareCount && ccountDic.size() != pcountDic.size()) {
            Assert.fail(String.format("The total of number are not the same, the first is %s, the second is %s\r\n", ccountDic.size(), pcountDic.size()));
        }
        for (String key : ccountDic.keySet()) {
            if (pcountDic.containsKey(key)) {
                if (!CompareUtil.compareObject(ccountDic.get(key), pcountDic.get(key), null, errorMsg.append(""))) {
                    Assert.fail(String.format("The count of %s are not the same, the first is %s, the second is %s\r\n", key, ccountDic.get(key), pcountDic.get(key)));
                }
            } else {
                Assert.fail(String.format("The second is not contains %s\r\n", key));
            }
        }
    }

    public static CostType posUCostGetByPosExpectedCost(List<CostType> costs, CostType posCost, String posuCurrency) {
        final StringBuilder errorMsg = new StringBuilder();
        final List<String> ignoreList = new ArrayList<>(Arrays.asList("multiplierOrAmount", "referenceList", "descriptionRawText"));
        CostType rc = null;
        for (CostType c : costs) {
            if (CompareUtil.compareObject(c.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode(), posuCurrency, null, errorMsg) && CompareUtil.compareObject(c, posCost, ignoreList, errorMsg)) {
                rc = c;
                break;
            }
        }
        if (CompareUtil.isObjEmpty(rc)) {
            StringBuilder errStr = new StringBuilder();
            errStr.append(String.format(" No find the expected posucurrencycode cost - %s with currencyCode=%s can't find in costlist, the expected cost detail is: \n",
                    posCost.getDescriptionRawText(), posuCurrency));
            errStr.append(String.format("DecimalPlaceCount: %s \n", posCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount()));
            errStr.append(String.format("Decimal: %s \n", posCost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal()));
            errStr.append(String.format("FinanceCategoryCode: %s \n", posCost.getFinanceCategoryCode()));
            errStr.append(String.format("FinanceApplicationCode: %s \n", posCost.getFinanceApplicationCode()));
            errStr.append(String.format("FinanceApplicationUnitCount: %s \n", posCost.getFinanceApplicationUnitCount()));
            errStr.append(String.format("LegacyMonetaryClassID: %s \n", posCost.getLegacyFinanceKey().getLegacyMonetaryClassID()));
            errStr.append(String.format("LegacyMonetaryCalculationSystemID: %s \n", posCost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID()));
            errStr.append(String.format("LegacyMonetaryCalculationID: %s \n", posCost.getLegacyFinanceKey().getLegacyMonetaryCalculationID()));
            errStr.append(String.format("DescriptionRawText: %s \n", posCost.getDescriptionRawText()));

            Assert.fail(errStr.toString());
        }
        return rc;
    }

    public static String getPOSuCurrencyCode(List<CostType> costList, String posCurrencyCode) {
        String posuCurrencyCode = posCurrencyCode;
        for (CostType cost : costList) {
            if (!posCurrencyCode.equals(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
                posuCurrencyCode = cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                break;
            }
        }
        return posuCurrencyCode;
    }

    public static double calculateCostAmount(CostType cost, long roundDigits, boolean applyFinanceApplicationUnitCount) {
        double amount = 0;
        boolean multiplierBoolean = true;
        if (!CompareUtil.isObjEmpty(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())) {
            multiplierBoolean = false;
        }
        amount = multiplierBoolean ? calculateAmountFromCurrencyAmountOrMultiplier(null, cost.getMultiplierOrAmount().getMultiplier(), 0) : calculateAmountFromCurrencyAmountOrMultiplier(cost.getMultiplierOrAmount().getCurrencyAmount(), null, 0);
        //In most cases, we need to multiple FinanceApplicationUnitCount, but for some cost, like Commission, we just need the amount, FinanceApplicationUnitCount is 0
        if (applyFinanceApplicationUnitCount) {
            amount = amount * cost.getFinanceApplicationUnitCount();
        }
        if (roundDigits > 0) {
            amount = new BigDecimal(amount).setScale((int) roundDigits, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }

    public static double calculateAmountFromCurrencyAmountOrMultiplier(CurrencyAmountType currencyAmount, MultiplierType multiplier, long roundDigits) {
        double amount = 0;
        int amountActual = 0;
        int decimaiCountActual = 0;
        if (!CompareUtil.isObjEmpty(currencyAmount)) {
            amountActual = currencyAmount.getAmount().getDecimal();
            decimaiCountActual = (int) currencyAmount.getAmount().getDecimalPlaceCount();
        } else if (!CompareUtil.isObjEmpty(multiplier)) {
            amountActual = multiplier.getDecimal();
            decimaiCountActual = (int) multiplier.getDecimalPlaceCount();
        } else {
            Assert.fail("Automation exception: you must input CurrencyAmount or Multiplier or MultiplierType for method: calculateAmountFromCurrencyAmountOrMultiplier!");
        }
        amount = amountActual / Math.pow(10, decimaiCountActual);
        if (roundDigits > 0) {
            amount = new BigDecimal(amount).setScale((int) roundDigits, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }

    public static Double getCurrencyConversionRate(String baseCurCode, String targetCurCode, String guid, HttpClient httpClient) throws ParserConfigurationException {
        Double exchangeRate = -1.00; // error value
        int iCount = 1;
        while (iCount <= 5) {
            exchangeRate = getCurrencyConversionRateSend(baseCurCode, targetCurCode, guid, httpClient);
            if (exchangeRate != -1.00) {
                break;
            }
            iCount++;
        }

        if (exchangeRate == -1.00) {
            Assert.fail(String.format("Can't get rate from FXRS for baseCurCode: %s, targetCurCode: %s. ", baseCurCode, targetCurCode));
        }
        return exchangeRate;
    }

    public static Double getCurrencyConversionRateSend(String baseCurCode, String targetCurCode, String guid, HttpClient httpClient) throws ParserConfigurationException {
        Assert.assertNotNull(baseCurCode, "baseCurCode");
        Assert.assertNotNull(targetCurCode, "targetCurCode");
        Double exchangeRate = -1.00; // error value
        final Document docRequest = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        try {
            final Element envelope = docRequest.createElement("s:Envelope");
            docRequest.appendChild(envelope);
            //Get root element
            envelope.setAttribute("xmlns:s", "http://www.w3.org/2003/05/soap-envelope");
            envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

            // Create body
            final Element body = docRequest.createElementNS("http://www.w3.org/2003/05/soap-envelope", "s:Body");
            envelope.appendChild(body);
            final Element fxRatesRequest = docRequest.createElement("urn:FXRatesRequest");
            body.appendChild(fxRatesRequest);
            fxRatesRequest.setAttribute("xmlns:urn", "urn:expedia:xmlapi:fxrs:v1");
            final Element messageInfo = docRequest.createElement("urn:MessageInfo");
            fxRatesRequest.appendChild(messageInfo);
            final Element createDateTime = docRequest.createElement("urn:CreateDateTime");
            messageInfo.appendChild(createDateTime);
            createDateTime.setTextContent("2010-05-06T23:52:04.446-08:00");
            final Element fxRateQueryList = docRequest.createElement("urn:FXRateQueryList");
            fxRatesRequest.appendChild(fxRateQueryList);
            final Element fxRateQuery = docRequest.createElement("urn:FXRateQuery");
            fxRateQueryList.appendChild(fxRateQuery);
            final Element baseCurrencyCode = docRequest.createElement("urn:BaseCurrencyCode");
            fxRateQuery.appendChild(baseCurrencyCode);
            baseCurrencyCode.setTextContent("USD");
            final Element targetCurrencyCode = docRequest.createElement("urn:TargetCurrencyCode");
            fxRateQuery.appendChild(targetCurrencyCode);
            targetCurrencyCode.setTextContent("CNY");

            // Set DateTime
            final String currentDate = DateTime.now().toString();
            createDateTime.setTextContent(currentDate);

            // Set BaseCurrencyCode
            baseCurrencyCode.setTextContent(baseCurCode);

            // Set TargetCurrencyCode
            targetCurrencyCode.setTextContent(targetCurCode);

            System.out.println("document: " + PojoXmlUtil.toString(docRequest));
            // Send/Recv
            final String fxRsUri = SettingsProvider.FXRS_URI;
            final HttpMessageSendUtil httpMessageSendUtil = new HttpMessageSendUtil();
            httpMessageSendUtil.setServiceUrl(fxRsUri);
            httpMessageSendUtil.setHttpClient(httpClient);
            httpMessageSendUtil.setMethod(HttpMethod.POST.asString());
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put("Accept-Encoding", "gzip");
            headers.put("Content-Type", "application/soap+xml");
            headers.put("Accept", "application/soap+xml");
            // E3JMS-L-activityId
            // Note: If need Spoofer,we all need to add OriginalGUID in request header.
            if (SettingsProvider.USE_SPOOFER) {
                headers.put("e3jms-l-activityId-propname", "activityId");
                headers.put("E3JMS-L-activityId", guid);
            }
            httpMessageSendUtil.setRequestHeaders(headers);
            httpMessageSendUtil.setContent(new BytesContentProvider((PojoXmlUtil.toString(docRequest)).getBytes()));
            final ContentResponse response = httpMessageSendUtil.sendHttpMessage();
            // Load Response in doc
            final Document docResponse = PojoXmlUtil.stringToXml(response.getContentAsString());
            // Extract exchange rate
            if (CompareUtil.isObjEmpty(PojoXmlUtil.getNodesByTagName(docResponse.getFirstChild(), "Rate"))) {
                return exchangeRate;
            }

            final Node rate = PojoXmlUtil.getNodeByTagName(docResponse.getFirstChild(), "Rate");
            exchangeRate = Double.parseDouble(rate.getTextContent());
        } catch (Exception e) {
            logger.error(String.format("Error when sending FXRS for baseCurCode:%s, targetCurCode: %s. ", baseCurCode, targetCurCode));
        }

        return exchangeRate;
    }

    public static Map<String, String> getSPCodesFromCarSpecialEquipmentListAndCarVehicleOptionList(CarSpecialEquipmentListType carSpecialEquipmentList, CarVehicleOptionListType carVehicleOptionList, ReserveDefaultValue reserveDefaultValue) throws DataAccessException {
        Map<String, String> spCodes = new HashMap<String, String>();
        String externalDomainValue = "";
        if (CompareUtil.isObjEmpty(reserveDefaultValue.getReserveConfigValue().getCarSpecialEquipmentCode())) {
            if (!CompareUtil.isObjEmpty(carVehicleOptionList)) {
                for (CarVehicleOptionType carVehicleOption : carVehicleOptionList.getCarVehicleOption()) {
                    if (!CompareUtil.isObjEmpty(carVehicleOption.getCarVehicleOptionCategoryCode()) && "special equipment".equals(carVehicleOption.getCarVehicleOptionCategoryCode().toLowerCase())) {
                        if (!CompareUtil.isObjEmpty(carVehicleOption.getCarSpecialEquipmentCode())) {
                            externalDomainValue = UAPICommonNodeReader.readDomainValue(DataSourceHelper.getWSCSDataSourse(), 0, 0, CarSpecialEquipment.getDomainType(), "", carVehicleOption.getCarSpecialEquipmentCode());
                            spCodes.put(carVehicleOption.getCarSpecialEquipmentCode(), externalDomainValue);
                        }
                    }
                }
            }
        } else {
            if(!CompareUtil.isObjEmpty(carSpecialEquipmentList) && !CompareUtil.isObjEmpty(carSpecialEquipmentList.getCarSpecialEquipment())) {
                for (CarSpecialEquipmentType carSpecialEquipment : carSpecialEquipmentList.getCarSpecialEquipment()) {
                    if (!CompareUtil.isObjEmpty(carSpecialEquipment.getCarSpecialEquipmentCode())) {
                        externalDomainValue = UAPICommonNodeReader.readDomainValue(DataSourceHelper.getWSCSDataSourse(), 0, 0, CarSpecialEquipment.getDomainType(), "", carSpecialEquipment.getCarSpecialEquipmentCode());
                        spCodes.put(carSpecialEquipment.getCarSpecialEquipmentCode(), externalDomainValue);
                    }
                }
            }
        }

        return spCodes;
    }

    public void verifyIfPrePayBooleanReturnInProductForHertz(CarProductType carProduct,
                                                             CarsInventoryHelper inventoryHelper) throws DataAccessException
    {
        final SupplySubSetToWorldSpanSupplierItemMap supplySubsetMap = inventoryHelper
                .getWorldSpanSupplierItemMap(carProduct.getCarInventoryKey().getSupplySubsetID());

        final StringBuilder errorMsg = new StringBuilder();

        if(null != supplySubsetMap.getPrepaidBool() && supplySubsetMap.getPrepaidBool().equals("1"))
        {
            if(!carProduct.getPrePayBoolean())
            {
                errorMsg.append("PrePayBoolean Should be true but get false for Hertz, SupplySubsetID = " + carProduct.getCarInventoryKey().getSupplySubsetID());
            }
        }
        else
        {
            if(carProduct.getPrePayBoolean())
            {
                errorMsg.append("PrePayBoolean Should be false but get true for Hertz, SupplySubsetID = " + carProduct.getCarInventoryKey().getSupplySubsetID());
            }
        }

        if (!errorMsg.toString().trim().isEmpty())
        {
            Assert.fail(errorMsg.toString());
        }
    }
}
