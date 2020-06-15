package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardFormOfPaymentType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.e3.data.placetypes.defn.v4.PhoneType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 12/22/2016.
 */
public class ReserveGDSMap extends SuiteContext {
    /**
     * Amadues SCS Reserve - Verify CarInventoryKey - Multiple CD code per vendor - Egencia site/GBR
     * <p>
     * ASCS_Standalone_on_oneWay_UK_MultipleCDcode_GBP
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190976OffRoundMultipleCDcodePerVendorUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OffAirport_MAN_EDI.getTestScenario(),
                "190976", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_Dynamic_daily_GB_POSU_GBP"));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-40823215");
        parameters.setCarRate(cdCode);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190973MultipleVendorOneCDcodeEachASCSoffRoundTripEgenciaFRA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "190973", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_GBR_Standalone_OneWay_OnAirport_LHREDI_WithWithoutEPCDCode_190973"));

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190965ASCSonRoundTripEgenciaExtraHours() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "190965", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_FR_Standalone_RoundTrip_OnAirport_3DaysExtraHours"));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3extraHours);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190962ASCSonRoundTripEgenciaWeekend() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ESP_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(),
                "190962",PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_Spain_Standalone_RoundTrip_OnAirport_Weekend"));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Weekend1day);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190968ASCSonRoundTripEgenciaCurrencyDaily() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "190968", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_Daily_249590"));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs190966ASCSonRoundTripEgenciaCurrencyWeeklyExtDays() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(),
                "190966", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_ITA_Standalone_OneWay_OnAirport_VCEBLQ_WeeklyExtradays_190966"));

        parameters.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    //TFS 437657 Egencia - Return Agency/Location phone number
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs455511tfs455509tfs455516ASCSAgencyPhoneNumber() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OnAirport_BCN.getTestScenario(),
                "455511", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadues_DeliveryCollection_placeID_secondDriver_multiPhoneNum"));

        amadeusSCSReserveGDSMsgMapping(parameters, null);
    }

    //verify VehicleOptionList/customer/refrenceList/TravelerList(DigitNumberInLastName)/CarReservationRemarkList/bookingStatesCode
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void u2AndspecialEquipAndDigitNumberInLastName803396Verify() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "803396", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633"));
        parameters.setNeedMultiTraveler(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "ReservationRemarkU2");
    }

    //verify VehicleOptionList/customer/refrenceList/TravelerList(DigitNumberInFirstName)/CarReservationRemarkList/bookingStatesCode
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void nonU2AndspecialEquipDigitNumberInFirstName803397Verify() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "803397", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633"));
        parameters.setNeedMultiTraveler(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "ReservationRemarkNonU2");
    }
    
    //verify VehicleOptionList/customer/refrenceList/TravelerList/CarReservationRemarkList/bookingStatesCode
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void multiAndspecialEquip803398Verify() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "803398", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633"));
        amadeusSCSReserveGDSMsgMapping(parameters, "ReservationRemarkMulti");
    }

    //477362 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains both CarSpecialEquipmentList and CarProduct.CarVehicleOptionList  - Amadeus Value Speicail Equipment Code
     @Test(groups = {TestGroup.BOOKING_REGRESSION})
     public void tfs477362ReservationSpecialEquipmentAmadeusHCLCVOSEP() throws Exception
     {
       TestData parameters = new TestData(httpClient,
                       CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                       "477362", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL"));
       parameters.setCarSpecialEquipmentCode("HCL|CVO|SEP");
         amadeusSCSReserveMapping(parameters);

     }

    //477367 -ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains both CarSpecialEquipmentList and CarProduct.CarVehicleOptionList  -  Multiple Speicail Equipment Code
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477367ReservationSpecialEquipmentAmadeusCSIHCLSEP() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477367", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL_CSI"));
        parameters.setCarSpecialEquipmentCode("CSI,HCL|SEP");
        amadeusSCSReserveMapping(parameters);

    }

    /*//477363 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains both CarSpecialEquipmentList and CarProduct.CarVehicleOptionList  - Expedia Value Speicail Equipment Code
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477363ReservationSpecialEquipmentAmadeusLeftHandControlCVOSEP() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477363", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL"));
        parameters.setCarSpecialEquipmentCode("LeftHandControl|CVO|SEP");
        amadeusSCSReserveMapping(parameters);

    }

    //477372 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains only CarSpecialEquipmentList
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477372ReservationSpecialEquipmentAmadeusLeftHandControlSEP() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477372", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL"));
        parameters.setCarSpecialEquipmentCode("LeftHandControl|SEP");
        amadeusSCSReserveMapping(parameters);

    }

    //477377 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains both CarSpecialEquipmentList and CarProduct.CarVehicleOptionList  - Invalid Speicail Equipment Code
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477377ReservationSpecialEquipmentAmadeusAAA() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477377", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_InvalidSpecialEquipmentCode"));

        parameters.setCarSpecialEquipmentCode("AAA");
        amadeusSCSReserveMapping(parameters);
    }

    //477373 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains only CarVehicleOptionList
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477373ReservationSpecialEquipmentAmadeusCSICVO() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477373", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_CSI"));

        parameters.setCarSpecialEquipmentCode("CSI|CVO");
        amadeusSCSReserveMapping(parameters);

    }*/

    /*//477375 - ASCS Reserve - Verify CarSpecialEquipmentList works well, request contains CarVehicleOptionList and CarSpecialEquipmentList is empty
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs477375ReservationSpecialEquipmentAmadeusSEP() throws Exception
    {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "477375", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_3Days_TUID_207144"));

        parameters.setCarSpecialEquipmentCode(" |SEP");
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode("SX");
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        amadeusSCSReserveMapping(parameters);

    }*/

    //Amadues SCS e-Voucher Reserve - Verify DeliveryLocation and CollectionLocation - PlaceID - RoundTrip - Egencia site/GBR
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs208952EvoucherDeliveryAndCollectionPlaceIDRoundTripOnAirASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "208952", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"EVoucher_DeliveryAndDropOffLocation_PlaceID_ASCS_on_roundTrip"));
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("ZI-N865556");
        parameters.setCarRate(cdCode);
        parameters.setBillingNumber("871131370003");
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "DeliveryAndCollection");
    }

    //Amadues SCS e-Voucher Reserve - Verify DeliveryLocation(at location A) and CollectionLocation(at location B) - PlaceID - OneWay- Egencia site/GBR
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs208994EvoucherDeliveryAndCollectionPlaceIDOneWayOnAirPortASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "208994", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"EVoucher_DeliveryAndCollection_PlaceID_ASCS_on_oneway_Egencia"));
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("ZI-N865556");
        parameters.setCarRate(cdCode);
        parameters.setBillingNumber("871131370003");
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "DeliveryAndCollection");
    }

    //Amadues SCS Reserve - Verify DeliveryLocation(at location A) and Drop-Off Location(at Agency) - PlaceID - OneWay- Egencia site/FRA
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs191467DeliveryPlaceIDASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "191467", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"DeliveryAndDropOffLocation_PlaceID_ASCS_on_OneWay_Egencia_FRA_191467"));
        parameters.setNeedCC(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "Delivery");
    }

    //Amadues SCS Reserve - Verify Pickup Location(at Agency) and CollectionLocation(at location A) - PlaceID  Egencia site/ESP
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs191469CollectionPlaceIDASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "191469", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"PickupLocationAndCollection_PlaceID_Collection_ASCS_FR_round_191469"));
        parameters.setNeedCC(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "Collection");
    }

    //Amadues SCS e-Voucher Reserve - Verify BookingStateCode/Cost - Booked 'HK' - Egencia site/FRA
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs208949EvoucherRoundTripOFFASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_NCL.getTestScenario(),
                "208949", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OffAirport_NCL_BillingCode_ZI_871131370003"));
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        parameters.setBillingNumber("871131370003");
        amadeusSCSReserveGDSMsgMapping(parameters, "E_Voucher");
    }

    //Amadues SCS e-Voucher Reserve - Verify CarVehicleOption - Special Equipment - Egencia site/FRA
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs208945EvoucherCarVehicleOptionListRoundTripOnAirportASCS() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "208945", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_FRA_Standalone_RoundTrip_OnAirport_BillingCode_ZI871131370003_SpecialEquip_CBS"));
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        parameters.setBillingNumber("871131370003");
        parameters.setSpecialEquipmentEnumType("CBS");
        amadeusSCSReserveGDSMsgMapping(parameters, "EVoucher_SpecialEquip");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs302034ReservationLoyaltyNumber() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "302034", PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Standalone_GBR_RoundTrip_OnAirport_LoyaltyCard"));
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        amadeusSCSReserveGDSMsgMapping(parameters, "LoyaltyNumber");
    }

    //Amadues SCS CryptoSupport - Airplus - migrate SCS part verification from CarBS for test case 333511
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs333511AmadeusCryptoSupportAirplus() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "333511", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"PickupLocationAndCollection_PlaceID_Collection_ASCS_FR_round_191469"));

        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "AirPlus");
    }

    //verify VehicleOptionList/customer/refrenceList/TravelerList(DigitNumberInLastName)/CarReservationRemarkList/bookingStatesCode
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs484840AmadeusAirLoyaltyUnderTravelerList() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "484840", PojoXmlUtil.generateNewOrigGUID(spooferTransport,"Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633"));
        parameters.setNeedMultiTraveler(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "FrequentFlyerNumber");
    }

    //1069045 is for non-ascii: https://confluence.expedia.biz/display/SSG/Test+Plan+for+User+Story+1068731%3A+Orphan+booking+because+of+ADBI+failure
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs283422tfs321847tfs1069045AmadeusBillingNumberAndAnalyticalCodeNonAsciiEVoucherFail() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "283422", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setBillingNumber("871131370003");

        amadeusSCSReserveGDSMsgMapping(parameters, "BillingNumberAndAnalyticalCode");
    }

    //1069046 is for non-ascii https://confluence.expedia.biz/display/SSG/Test+Plan+for+User+Story+1068731%3A+Orphan+booking+because+of+ADBI+failure
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs319075tfs1069046AmadeusCryptoSupportAirplusAndAnalyticalCodeNonAscii() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "319075", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "AirPlusAndAnalyticalCode");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs373563AmadeusDeliveryCollectionHomeAddressPhone() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "373563", PojoXmlUtil.generateNewOrigGUID(spooferTransport));

        amadeusSCSReserveGDSMsgMapping(parameters, "DeliveryAndCollectionHomeAddress");
    }

    //Currently EDI data is only passed downstream from Maserati services to Amadeus GDS if it is sent with BillingNumber.  This user story is to make sure EDI data is always sent to Amadeus GDS if it is received by Maserati.
    //https://confluence.expedia.biz/display/SSG/TFS+439223+-+Egencia+-+Pass+EDI+Data+to+Amadeus+if+received+in+request
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs442662AmadeusCDAndEDIDataWithoutBillingNumber() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "442662", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        CarRate cdCode = new CarRate();
        cdCode.setCdCode("ZI-N865556");
        parameters.setCarRate(cdCode);

        amadeusSCSReserveGDSMsgMapping(parameters, "EDIData");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs442669AmadeusEDIDataWithCC() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "442669", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "EDIDataWithCC");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs456139AmadeusEDIDataWithAirPlus() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "456139", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "EDIDataWithAirPlus");
    }

    //https://confluence.expedia.biz/display/SSG/TFS+505500+-+Support+Egencia+sending+%27PNR%27+in+DBI+data
    //For PNR send in DBI data - we send 	<ns4:DescriptiveBillingInfo ns4:Key="PNR" ns4:Value="pnr_value"></ns4:DescriptiveBillingInfo>
    //in ASCS reserve request, ASCS will retrieve PNR from GDS response then send actual PNR value to ADBI request
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs518660AmadeusPNRWithDBIDataForirPlus() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "518660", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "PNRWithDBIData");
    }

    //DBIWithOnlyPNR
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs518661AmadeusWithOnlyPNRInDBIDataForAirPlus() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "518661", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);

        amadeusSCSReserveGDSMsgMapping(parameters, "DBIWithOnlyPNR");
    }

    //For hertz delivery/collection booking with supplier config Reserve.deliveryCollectionBillingGuarantee/enable ON, billing number should be sent in payment node to ACSQ
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs443508HertzDeliveryCollectionHomeAddressPayment() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "443508", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setBillingNumber("871131370003");
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        amadeusSCSReserveGDSMsgMapping(parameters, "DeliveryAndCollectionHomeAddress");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs443524HertzDeliveryCollectionPlaceIDPayment() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "443524", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setBillingNumber("871131370003");
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        amadeusSCSReserveGDSMsgMapping(parameters, "DeliveryCollectionPlaceID");
    }

    //Meichun: add one test case- when we book hertz car without delivery/collection, then billing code should not be sent in Payment to GDS based on design:
    //https://confluence.expedia.biz/display/SSG/TFS+439215+-+Billing+number+guarantee+of+payment+design
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs443524XHertzNonDeliveryCollectionWithBillingCode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "4435241", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setBillingNumber("871131370003");
        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);
        amadeusSCSReserveGDSMsgMapping(parameters, "BillingCode");
    }

    //https://confluence.expedia.biz/display/SSG/Test+Plan+For+User+Story+693903%3A+Egencia+-+Support+Accredetive+Card+with+Airplus+Lodged+Card
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs746065AccredetiveLoyaltyAirPlusAndAnalyticalCode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "746065", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "AccredetiveLoyaltyAirPlusAndAnalyticalCode");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs746067OnlyAccredetiveLoyalty() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "746067", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        amadeusSCSReserveGDSMsgMapping(parameters, "AccredetiveLoyalty");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs746070VendorLoyaltyAirPlusAndAnalyticalCode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "746070", PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        parameters.setNeedCC(true);
        amadeusSCSReserveGDSMsgMapping(parameters, "CarTravelerLoyaltyAirPlus");
    }
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void callSellWECError() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "5287433", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        amadeusReserveErrorWarning(parameters, "Amadues_CarSell_WECError", true);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void callSellWECWarn() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "5287434", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        amadeusReserveErrorWarning(parameters, "Amadues_CarSell_WECWarn", false);
    }

    private void amadeusReserveErrorWarning(TestData parameters, String spooferTemplateScenarioName, boolean isError) throws Exception
    {
        //Search and basic verify
        spooferTransport.setOverrides(
                SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        //GetDetails and basic verify
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsExecutionHelper
                .getDetails(parameters, scsRequestGenerator);

        GetDetailsVerificationHelper
                .getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //Reserve and basic verify
        final ReserveVerificationInput reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);

        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        if(isError)
        {
            Assert.assertTrue(null == reserveVerificationInput.getResponse().getCarReservation());
        }
        else
        {
            Assert.assertTrue(null != reserveVerificationInput.getResponse().getCarReservation()
                    && null != reserveVerificationInput.getResponse().getCarReservation().getCarProduct());
        }
    }


    private void amadeusSCSReserveMapping(TestData parameters)
            throws Exception
    {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);
        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));
        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

       //reserve
        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse(), parameters);
        ReserveVerificationInput reserveVerificationInput  = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);
        logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
         logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

         if("AAA" .equals(parameters.getCarSpecialEquipmentCode()))
        {
            if(!"Invalid special equipment code" .equals(reserveVerificationInput.getResponse().getErrorCollection().getFieldInvalidErrorList()
            .getFieldInvalidError().get(0).getDescriptionRawText()))
            {
                Assert.fail("Invalid special equipment code error should exist in reserve response");
            }
           return;
        }
        else
        {
            ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
        }

        //getReservation
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationExecutionHelper.retrieveReservation(parameters, scsRequestGenerator);
        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));
        GetReservationVerificationHelper.getReservationBasicVerification(getReservationVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);


        //cancel and basic verify
        if (null != reserveVerificationInput.getResponse().getCarReservation() && null != reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode())
        {
            if (reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode())
                    || reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode()))
            {
                CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);

                CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
            }
        }
        // Verify SpecialEquipment send in ACSQ request correct
        // Verify returned SpecialEquipMent in ARIS correctly mapped to CCBR response
        // Verify returned SpecialEquipMent in  APRQ correctly mapped to CCRR response
        StringBuffer errorMsg = new StringBuffer();
        CarSpecialEquipmentListType actualCarSpecialEquipmentListInReserve = reserveVerificationInput.getResponse().getCarReservation().getCarSpecialEquipmentList();
        CarVehicleOptionListType actualCarVehicleOptionListInReserve = reserveVerificationInput.getResponse().getCarReservation().getCarProduct().getCarVehicleOptionList();

        CarSpecialEquipmentListType actualCarSpecialEquipmentListInGetReservation = getReservationVerificationInput.getResponse().getCarReservationList().getCarReservation().get(0).getCarSpecialEquipmentList();
        CarVehicleOptionListType actualCarVehicleOptionListInGetReservation = getReservationVerificationInput.getResponse().getCarReservationList().getCarReservation().get(0).getCarProduct().getCarVehicleOptionList();


        final Document spooferTransactions = spooferTransport.retrieveRecords(parameters.getGuid());
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, parameters.getGuid(), parameters.getScenarios());


            errorMsg.append(ReserveVerificationHelper.verifyspecialequipmentinacsq(reserveVerificationInput.getResponse().getCarReservation().getCarSpecialEquipmentList(), reserveVerificationInput.getResponse().getCarReservation().getCarProduct().getCarVehicleOptionList(), reserveVerificationContext))
                    .append(ReserveVerificationHelper.verifySpecialEuipmentListInSCSResponse(reserveVerificationContext, actualCarSpecialEquipmentListInReserve, actualCarVehicleOptionListInReserve, "Reserve", reserveVerificationInput.getRequest()))
                    .append(ReserveVerificationHelper.verifySpecialEuipmentListInSCSResponse(reserveVerificationContext, actualCarSpecialEquipmentListInGetReservation, actualCarVehicleOptionListInGetReservation, "GetReservation", reserveVerificationInput.getRequest()));

        if(StringUtil.isNotBlank(errorMsg.toString()))
        {
            Assert.fail(errorMsg.toString());
        }



    }
     /**
     * @param parameters
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    private void amadeusSCSReserveGDSMsgMapping(TestData parameters, String specialTest)
            throws Exception
    {
        //Search and basic verify
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);
        //logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        //logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse(), parameters);
        ReserveVerificationInput reserveVerificationInput = null;

        List<CommonEnumManager.VerifyType> verifyTypes = new ArrayList<>();
        if (StringUtil.isNotBlank(specialTest))
        {
            CarSupplyConnectivityReserveRequestType reserveRequest = scsRequestGenerator.createReserveRequest();

            //set DigitNumberInLastName and ReservationRemark and VehicleOptionLis
            if("ReservationRemarkU2".equals(specialTest))
            {
                reserveRequest.getTravelerList().getTraveler().get(0).getPerson().getPersonName().setLastName("STTWO11");
                reserveRequest.getTravelerList().getTraveler().get(1).getPerson().getPersonName().setLastName("STTWO11");

                buildCarReservationRemarkList(reserveRequest,"U2 - 313516", null);

                buildCarVehicleOptionList(reserveRequest);
               //verify BookingStateCode/CarReservationRemark/Customer/TravelerList/SpecialEquipment/ReferenceList
               buildVerifyTypes(verifyTypes);


                }
            //set DigitNumberInFirstName and ReservationRemark and VehicleOptionLis
            if("ReservationRemarkNonU2".equals(specialTest))
            {
                reserveRequest.getTravelerList().getTraveler().get(0).getPerson().getPersonName().setFirstName("CARBSRESERVE11");
                reserveRequest.getTravelerList().getTraveler().get(1).getPerson().getPersonName().setFirstName("CARBSRESERVE11");

                buildCarReservationRemarkList(reserveRequest,"U3 - 313517", "AB");

                buildCarVehicleOptionList(reserveRequest);

                //verify BookingStateCode/CarReservationRemark/Customer/TravelerList/SpecialEquipment/ReferenceList
                buildVerifyTypes(verifyTypes);
            }
            //set ReservationRemark and VehicleOptionLis
            if("ReservationRemarkMulti".equals(specialTest))
            {
                CarReservationRemarkListType carReservationRemarkListType = new CarReservationRemarkListType();
                List<CarReservationRemarkType> carReservationRemarkTypeList = new ArrayList<>();
                buildCarReservationRemark("U2 - 313515", "BC", carReservationRemarkTypeList);
                buildCarReservationRemark("U3 - 313515", null, carReservationRemarkTypeList);
                carReservationRemarkListType.setCarReservationRemark(carReservationRemarkTypeList);
                reserveRequest.setCarReservationRemarkList(carReservationRemarkListType);

                buildCarVehicleOptionList(reserveRequest);

                //verify BookingStateCode/CarReservationRemark/Customer/TravelerList/SpecialEquipment/ReferenceList
                buildVerifyTypes(verifyTypes);
            }

            //set DeliveryLocation and
            if("Delivery".equals(specialTest))
            {
                reserveRequest.setDeliveryLocation(buildPlaceIDCustomerLocation());

                //verify DCLocation/CCCard
                verifyTypes.add(CommonEnumManager.VerifyType.DCLocation);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);

            }
            //set CollectionLocation
            if("Collection".equals(specialTest))
            {
                reserveRequest.setCollectionLocation(buildPlaceIDCustomerLocation());

                //verify DCLocation/CCCard
                verifyTypes.add(CommonEnumManager.VerifyType.DCLocation);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);

            }

            if("DeliveryCollectionPlaceID".equals(specialTest))
            {
                reserveRequest.setDeliveryLocation(buildPlaceIDCustomerLocation());
                reserveRequest.setCollectionLocation(buildPlaceIDCustomerLocation());

                //verify DCLocation
                verifyTypes.add(CommonEnumManager.VerifyType.DCLocation);

            }

            //set DeliveryLocation and CollectionLocation
            if("DeliveryAndCollection".equals(specialTest))
            {
                reserveRequest.setDeliveryLocation(buildPlaceIDCustomerLocation());
                reserveRequest.setCollectionLocation(buildPlaceIDCustomerLocation());

                //verify DCLocation/CCCard
                verifyTypes.add(CommonEnumManager.VerifyType.DCLocation);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);

            }

            if("DeliveryAndCollectionHomeAddress".equals(specialTest))
            {
                reserveRequest.setDeliveryLocation(buildCustomerLocationWithHomeAddress());
                reserveRequest.setCollectionLocation(buildCustomerLocationWithHomeAddress());

                //verify DCLocation/CCCard
                verifyTypes.add(CommonEnumManager.VerifyType.DCLocation);

            }

            if("E_Voucher".equals(specialTest))
            {
                //verify BookingStateCode/CostAndCalculate/billingNumber
                verifyTypes.add(CommonEnumManager.VerifyType.BookingStateCode);
                verifyTypes.add(CommonEnumManager.VerifyType.CostAndCalculate);
            }

            if(specialTest.contains("AirPlus"))
            {
                buildAirPlus(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);
            }

            if("EVoucher_SpecialEquip".equals(specialTest))
            {
                //verify SpecialEquipment/billingNumber
                verifyTypes.add(CommonEnumManager.VerifyType.SpecialEquipment);
            }

            if("LoyaltyNumber" .equals(specialTest))
            {
                //Loyalty Number
                CarRateType carRate = reserveRequest.getCarProduct().getCarInventoryKey().getCarRate();
                LoyaltyProgramType loPro = new LoyaltyProgramType();
                loPro.setLoyaltyProgramCode("LoyaltyCardNumber");
                loPro.setLoyaltyProgramMembershipCode("43825675");
                carRate.setLoyaltyProgram(loPro);

                verifyTypes.add(CommonEnumManager.VerifyType.LoyaltyNumber);
            }

            if(specialTest.contains("CarTravelerLoyalty"))
            {
                buildTravelerCarLoyalty(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.LoyaltyNumber);
            }

            if(specialTest.contains("AccredetiveLoyalty"))
            {
                buildAccreditiveLoyalty(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.LoyaltyNumber);
            }

            if("FrequentFlyerNumber" .equals(specialTest))
            {
                buildFrequentFlyerNumber(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.FrequentFlyerNumber);
            }

            if("BillingNumberAndAnalyticalCode".equals(specialTest))
            {
                buildDescBillingInfoListForBillingCode(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
                verifyTypes.add(CommonEnumManager.VerifyType.EVoucherFail);
            }
            if("EDIData".equals(specialTest))
            {
                buildDescBillingInfoListForBillingCode(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
            }
            if("EDIDataWithCC".equals(specialTest))
            {
                buildDescBillingInfoListForBillingCode(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);
            }

            if("EDIDataWithAirPlus".equals(specialTest))
            {
                buildEDIAndDescBillingInfoListForAirPlus(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
            }

            if("PNRWithDBIData".equals(specialTest))
            {
                buildEDIAndDescBillingInfoListForAirPlus(reserveRequest);
                addDescBillingInfo(reserveRequest.getDescriptiveBillingInfoList(), "PNR", "pnr_value");
                buildAirPlus(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);
            }

            if("DBIWithOnlyPNR".equals(specialTest))
            {
                buildDescBillingInfoListForOnlyPNR(reserveRequest);
                buildAirPlus(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
                verifyTypes.add(CommonEnumManager.VerifyType.CCCard);
            }


            if(specialTest.contains("AirPlusAndAnalyticalCode"))
            {
                buildDescBillingInfoListForAirPlus(reserveRequest);
                verifyTypes.add(CommonEnumManager.VerifyType.AnalyticalCode);
            }


            reserveVerificationInput = TransportHelper.sendReceive(parameters.getHttpClient(), SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, parameters.getGuid());
            scsRequestGenerator.setReserveReq(reserveVerificationInput.getRequest());
            scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        }
        else
        {
            //Reserve and verifiers
            reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);
            logger.info("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getRequest())));
            logger.info("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(reserveVerificationInput.getResponse())));

        }
        ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //cancel and basic verify
        if (null != reserveVerificationInput.getResponse().getCarReservation() && null != reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode())
        {
            if (reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode())
                    || reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode()))
            {
                //Create new GUID for cancel because we want to verify there is no PNR_Retrieve sent for reserve - billing number with evoucher faled
                String oldGUID = parameters.getGuid();
                parameters.setGuid(PojoXmlUtil.generateNewOrigGUID(spooferTransport) );
                CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);
                CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
                parameters.setGuid(oldGUID);
            }
        }


        if(StringUtil.isNotBlank(specialTest))
        {
           ReserveVerificationHelper.verifyASCSReserve(reserveVerificationInput, spooferTransport, parameters, verifyTypes);
        }
        else
        {
            ReserveVerificationHelper.reserveRequestGDSMsgMappingVerification(reserveVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(),logger);
            ReserveVerificationHelper.reserveResponseGDSMsgMappingVerification(reserveVerificationInput, spooferTransport, parameters, logger);
        }


    }

    //	<ns4:DescriptiveBillingInfoList>
    //<ns4:DescriptiveBillingInfo ns4:Key="EDIDATA" ns4:Value="lolo,GRETER Laurent,ft1,"></ns4:DescriptiveBillingInfo>
    //</ns4:DescriptiveBillingInfoList>
    private void buildDescBillingInfoListForBillingCode(CarSupplyConnectivityReserveRequestType reserveRequest)
    {
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        addDescBillingInfo(descBillingInfoList, "EDIDATA", "lolo,Einführung,ft1,"); //ü is non-ascii
        reserveRequest.setDescriptiveBillingInfoList(descBillingInfoList);
    }

    private void buildDescBillingInfoListForOnlyPNR(CarSupplyConnectivityReserveRequestType reserveRequest)
    {
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        addDescBillingInfo(descBillingInfoList, "PNR", "pnr_value");
        reserveRequest.setDescriptiveBillingInfoList(descBillingInfoList);
    }

    private void buildEDIAndDescBillingInfoListForAirPlus(CarSupplyConnectivityReserveRequestType reserveRequest)
    {
        buildDescBillingInfoListForBillingCode(reserveRequest);
        reserveRequest.getDescriptiveBillingInfoList().getDescriptiveBillingInfo().addAll(getDescBillingInfoListForAirPlus().getDescriptiveBillingInfo());
    }

    private void buildDescBillingInfoListForAirPlus(CarSupplyConnectivityReserveRequestType reserveRequest)
    {
        reserveRequest.setDescriptiveBillingInfoList(getDescBillingInfoListForAirPlus());
    }

    private DescriptiveBillingInfoListType getDescBillingInfoListForAirPlus()
    {
        DescriptiveBillingInfoListType descBillingInfoList = new DescriptiveBillingInfoListType();
        descBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        addDescBillingInfo(descBillingInfoList, "CLS", "CAR");
        addDescBillingInfo(descBillingInfoList, "PK", "val1ü"); //ü is non-ascii, it should be converted to u to GDS request
        addDescBillingInfo(descBillingInfoList, "DS", "val2");
        addDescBillingInfo(descBillingInfoList, "KS", "val3");
        addDescBillingInfo(descBillingInfoList, "AE", "val4");
        addDescBillingInfo(descBillingInfoList, "IK", "val5");
        addDescBillingInfo(descBillingInfoList, "BD", "val6");
        addDescBillingInfo(descBillingInfoList, "PR", "val7");
        addDescBillingInfo(descBillingInfoList, "AU","val8");
        addDescBillingInfo(descBillingInfoList, "AK","val9");
        addDescBillingInfo(descBillingInfoList, "RZ","val10");
        return descBillingInfoList;
    }

    private void addDescBillingInfo(DescriptiveBillingInfoListType descBillingInfoList, String key, String value)
    {
        DescriptiveBillingInfoType descBillingInfo = new DescriptiveBillingInfoType();
        descBillingInfo.setKey(key);
        descBillingInfo.setValue(value);
        descBillingInfoList.getDescriptiveBillingInfo().add(descBillingInfo);
    }

    /*<Address xmlns="urn:expedia:e3:data:placetypes:defn:v4">
    <FirstAddressLine>QUAI SOUTHAMPTON</FirstAddressLine>
    <CityName>LA HAVRE</CityName>
    <PostalCode>76600</PostalCode>
    <CountryAlpha3Code>FRA</CountryAlpha3Code>
    </Address>*/
    private AddressType getAddressType()
    {
        AddressType address = new AddressType();
        address.setFirstAddressLine("QUAI SOUTHAMPTON");
        address.setCityName("LA HAVRE");
        address.setPostalCode("76600");
        address.setCountryAlpha3Code("FRA");
        return address;
    }

    private CustomerLocationType buildCustomerLocationWithHomeAddress()
    {
        CustomerLocationType customerLocationType = new CustomerLocationType();
        customerLocationType.setPhone(getPhoneType());
        customerLocationType.setAddress(getAddressType());
        return customerLocationType;
    }

    private CustomerLocationType buildPlaceIDCustomerLocation()
    {
        CustomerLocationType customerLocationType = new CustomerLocationType();
        customerLocationType.setPhone(getPhoneType());
        customerLocationType.setCustomerLocationCode("7278");
        return customerLocationType;
    }

    private PhoneType getPhoneType()
    {
        PhoneType phone = new PhoneType();
        phone.setPhoneAreaCode("034");
        phone.setPhoneCountryCode("222");
        phone.setPhoneNumber("1234567890");
        return phone;
    }

    private void buildVerifyTypes(List<CommonEnumManager.VerifyType> verifyTypes)
    {
        verifyTypes.add(CommonEnumManager.VerifyType.BookingStateCode);
        verifyTypes.add(CommonEnumManager.VerifyType.CarReservationRemark);
        verifyTypes.add(CommonEnumManager.VerifyType.Customer);
        verifyTypes.add(CommonEnumManager.VerifyType.TravelerList);
        verifyTypes.add(CommonEnumManager.VerifyType.SpecialEquipment);
        verifyTypes.add(CommonEnumManager.VerifyType.ReferenceList);
    }

    private void buildCarVehicleOptionList(CarSupplyConnectivityReserveRequestType reserveRequest)
    {
        CarVehicleOptionListType carVehicleOptionListType = new CarVehicleOptionListType();
        List<CarVehicleOptionType> carVehicleOptionTypes = new ArrayList<>();
        CarVehicleOptionType carVehicleOptionType = new CarVehicleOptionType();
        carVehicleOptionType.setCarSpecialEquipmentCode("NVS");
        carVehicleOptionType.setCarVehicleOptionCategoryCode("special equipment");
        carVehicleOptionTypes.add(carVehicleOptionType);
        carVehicleOptionListType.setCarVehicleOption(carVehicleOptionTypes);
        reserveRequest.getCarProduct().setCarVehicleOptionList(carVehicleOptionListType);
    }

    private void buildCarReservationRemarkList(CarSupplyConnectivityReserveRequestType reserveRequest, String carReservationRemarkText
    , String carReservationRemarkCategoryCode)
    {
        CarReservationRemarkListType carReservationRemarkListType = new CarReservationRemarkListType();
        List<CarReservationRemarkType> carReservationRemarkTypeList = new ArrayList<>();
        buildCarReservationRemark(carReservationRemarkText, carReservationRemarkCategoryCode, carReservationRemarkTypeList);
        carReservationRemarkListType.setCarReservationRemark(carReservationRemarkTypeList);
        reserveRequest.setCarReservationRemarkList(carReservationRemarkListType);
    }

    private void buildCarReservationRemark(String carReservationRemarkText, String carReservationRemarkCategoryCode, List<CarReservationRemarkType> carReservationRemarkTypeList)
    {
        CarReservationRemarkType carReservationRemarkType = new CarReservationRemarkType();
        carReservationRemarkType.setCarReservationRemarkText(carReservationRemarkText);
        if(StringUtil.isNotBlank(carReservationRemarkCategoryCode))
        {
            carReservationRemarkType.setCarReservationRemarkCategoryCode(carReservationRemarkCategoryCode);
        }
        carReservationRemarkTypeList.add(carReservationRemarkType);
    }

    private void buildAirPlus(CarSupplyConnectivityReserveRequestType request)
    {
        CreditCardFormOfPaymentType ccCard = request.getCreditCardFormOfPayment();

        // card
        CreditCardType creditCardType = new CreditCardType();
        ccCard.setCreditCard(creditCardType);
        creditCardType.setCreditCardSupplierCode("Airplus");
        creditCardType.setCreditCardNumberEncrypted("AQAQAAEAEAAxMTAxMxJ76+SD6maTX9lMyxA/8vSK6QHWGU/+C86HUtst9k46");

        DateTime dateTime = new DateTime("2017-12-28T23:59:00");
        creditCardType.setExpirationDate(dateTime);
        creditCardType.setCardPresentBoolean(true);
        creditCardType.setMaskedCreditCardNumber("5390");
    }

    private void buildFrequentFlyerNumber(CarSupplyConnectivityReserveRequestType request)
    {
        List<TravelerType> travelerList = request.getTravelerList().getTraveler();
        travelerList.get(0).setLoyaltyProgramList(new LoyaltyProgramListType());
        travelerList.get(0).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
        travelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                "Air", "AA", "987654321"));

        if(travelerList.size() == 2){
            travelerList.get(1).setLoyaltyProgramList(new LoyaltyProgramListType());
            travelerList.get(1).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
            travelerList.get(1).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                    "Air", "AA","987654324"));
        }

    }

    private void buildTravelerCarLoyalty(CarSupplyConnectivityReserveRequestType request)
    {
        List<TravelerType> travelerList = request.getTravelerList().getTraveler();
        travelerList.get(0).setLoyaltyProgramList(new LoyaltyProgramListType());
        travelerList.get(0).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
        travelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                "Car", "", "43825675323"));
    }

    private void buildAccreditiveLoyalty(CarSupplyConnectivityReserveRequestType request)
    {
        List<TravelerType> travelerList = request.getTravelerList().getTraveler();
        travelerList.get(0).setLoyaltyProgramList(new LoyaltyProgramListType());
        travelerList.get(0).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
        travelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                "Accreditive", "", "43825675"));
    }

    private LoyaltyProgramType buildLoyaltyProgram(String categoryCode, String programCode, String loyaltyProgramMembershipCode)
    {
        LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
        loyaltyProgram.setLoyaltyProgramCategoryCode(categoryCode);
        if(StringUtils.isNotBlank(programCode))
        {
            loyaltyProgram.setLoyaltyProgramCode(programCode);
        }
        loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyProgramMembershipCode);

        return loyaltyProgram;

    }
}