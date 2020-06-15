package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper.getCarInventoryDatasource;

@SuppressWarnings("PMD")
public class LoyaltyProgram extends SuiteCommon {


    private static final String LOYALTY_NUMBER_HERTZ = "Car-43825675";
    private static final String LOYALTY_NUMBER_AIR = "Air-987654321,Air-987654324";
    private static final String LOYALTY_NUMBER_CDCODE_HERTZ = "Car-51442848";
    private static final String LOYALTY_NUMBER_HERTZ2 = "Car-61384267";

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs302034AmadeusStandaloneRoundTripOnAirportLoyaltyNumber() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        validateLoyaltyInPreparePurchase(testScenario, "302034", "Standalone_GBR_RoundTrip_OnAirport_LoyaltyCardNumber", "S7JWZD", LOYALTY_NUMBER_HERTZ, true, false, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs484840tfs455549AmadeusStandaloneRoundTripOnAirportMultiTravelerWithLoyalty() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_NCL.getTestScenario();
        validateLoyaltyInPreparePurchase(testScenario, "484840", "Standalone_FRA_RoundTrip_OffAirport_MultiTraveler_Loyalty", "0Q7XRN", LOYALTY_NUMBER_AIR, false, true, true);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs484834AmadeusStandaloneRoundTripOnAirportSingleTravelerWithLoyalty() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_NCL.getTestScenario();
        validateLoyaltyInPreparePurchase(testScenario, "484834", "Standalone_FRA_RoundTrip_OffAirport_MultiTraveler_Loyalty", "0Q7XRN", LOYALTY_NUMBER_AIR, false, false, true);
    }

    private void validateLoyaltyInPreparePurchase(TestScenario testScenario, String tuid, String scenarioName, String clienCode, String loyaltyCard, boolean isLoyalty, boolean isMultiTraveler, boolean isTravelerLoyalty) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, getCarInventoryDatasource(), true, "ZE");
        ExecutionHelper.setCarRateOfTestData(testData, true, loyaltyCard, "");
        if (isMultiTraveler) {
            testData.setNeedMultiTraveler(true);
        }
        if (isLoyalty) {
            testData.setNeedLoyaltyCard(true);
        }
        if (isTravelerLoyalty) {
            testData.setNeedTravelerLoyalty(true);
        }
        testData.setClientCode(clienCode);
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);

        // verify Loyalty number in the Prepare purchase response
        if (isLoyalty) {
            Assert.assertTrue(LOYALTY_NUMBER_HERTZ.contains(omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                    .getCarReservation().getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode()), "Loyalty number is not as expected");
        }
        if (isTravelerLoyalty) {
            Assert.assertTrue(LOYALTY_NUMBER_AIR.contains(omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                    .getCarReservation().getTravelerList().getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().get(0).getLoyaltyProgramMembershipCode()));
            if (isMultiTraveler) {
                //Meichun: this verification point has verified multiple traveler returned in response also
                Assert.assertTrue(LOYALTY_NUMBER_AIR.contains(omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                        .getCarReservation().getTravelerList().getTraveler().get(1).getLoyaltyProgramList().getLoyaltyProgram().get(0).getLoyaltyProgramMembershipCode()));
            }
        }
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs845014WorldSpanUSLocationOnAirportSingleTravelerWithLoyalty() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        validateLoyaltyInPreparePurchase(testScenario, "845014", "PriceListAgencyDailyPrice", LOYALTY_NUMBER_HERTZ2, null, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs947548WorldSpanUKLocationGDSPOnAirportLoyaltyAndCDCode() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        validateLoyaltyInPreparePurchase(testScenario, "947548", "POSuGBP_Daily", LOYALTY_NUMBER_CDCODE_HERTZ, "ZE-689349", true);
    }

    private void validateLoyaltyInPreparePurchase(TestScenario testScenario, String tuid, String scenarioName, String loyaltyCard, String cdCode, boolean isLoyaltySurpass) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, getCarInventoryDatasource(), true, "ZE");
        ExecutionHelper.setCarRateOfTestData(testData, true, loyaltyCard, cdCode);

        PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), SettingsProvider.CARBS_POS_SET_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
        if (!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "1", PosConfigSettingName.SHOPPING_PROPAGATELOYALTYINFO_ENABLE_CARBS, SettingsProvider.ENVIRONMENT_NAME)) {
            Assert.fail("The pos config " + PosConfigSettingName.SHOPPING_PROPAGATELOYALTYINFO_ENABLE_CARBS + " set not expect, Please check once");
        }
        if (!posConfigHelper.checkPosConfigFeatureEnable(testData.getScenarios(), "1", PosConfigSettingName.BOOKINGSHOPPING_SUPPRESSLOYALTYINFO_NONAGENCY_ENABLE, SettingsProvider.ENVIRONMENT_NAME) && isLoyaltySurpass) {
            Assert.fail("The pos config " + PosConfigSettingName.BOOKINGSHOPPING_SUPPRESSLOYALTYINFO_NONAGENCY_ENABLE + " set not expect, Please check once");
        }
        testData.setNeedLoyaltyCard(true);
        testData.setNeedTravelerLoyalty(true);
        testData.setClientCode("QGPDJ8");
        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);
        if (cdCode != null) {
            Assert.assertTrue(cdCode.contains(omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                    .getCarReservation().getCarProduct().getCarInventoryKey().getCarRate().getCorporateDiscountCode()), "CDCode is not as expected");
        } else {
            Assert.assertTrue(loyaltyCard.contains(omRequestGenerate.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                    .getCarReservation().getCarProduct().getCarInventoryKey().getCarRate().getLoyaltyProgram().getLoyaltyProgramMembershipCode()), "Loyalty number is not as expected");
        }
    }

    //When both vendor loyalty number and Accredetive  loyalty number are sent, error should be returned
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs746076LoyaltyErrorHandling() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "746076", guid);

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(scenario, "746076", guid, httpClient, false);

        //GetOrder
        CarbsOMReserveRequestSender.CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);
        //Create record
        CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);

        //PreparePurchase with two loyalty - car and Accredetive
        final PreparePurchaseRequest preparePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);
        final List<TravelerType> travelerList = preparePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation()
                .getTravelerList().getTraveler();
        travelerList.get(0).setLoyaltyProgramList(new LoyaltyProgramListType());
        travelerList.get(0).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
        travelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                "Car", "", "43825675323"));
        travelerList.get(0).getLoyaltyProgramList().getLoyaltyProgram().add(buildLoyaltyProgram(
                "Accreditive", "", "43825675"));
        final PreparePurchaseResponseType preparePurchaseResponse = CarbsOMServiceSender.sendPreparePurchaseResponse(testData.getGuid(), testData.getHttpClient(), preparePurchaseRequest);

        //Verify error
        verifyLoyaltyError(preparePurchaseResponse);
    }

    public LoyaltyProgramType buildLoyaltyProgram(String categoryCode, String programCode, String loyaltyProgramMembershipCode)
    {
        final LoyaltyProgramType loyaltyProgram = new LoyaltyProgramType();
        loyaltyProgram.setLoyaltyProgramCategoryCode(categoryCode);
        if(StringUtils.isNotBlank(programCode))
        {
            loyaltyProgram.setLoyaltyProgramCode(programCode);
        }
        loyaltyProgram.setLoyaltyProgramMembershipCode(loyaltyProgramMembershipCode);

        return loyaltyProgram;

    }

    private void verifyLoyaltyError(PreparePurchaseResponseType response) throws DataAccessException {

        //Verify XPath returned under error list
        if(CollectionUtils.isEmpty(PojoXmlUtil.getXmlFieldValue(response, "XPath"))){
            Assert.fail("XPath should be returned under error list!");
        }

        //Verify description as: LoyaltyProgramList must contain only Accreditive LoyaltyProgram or Car LoyaltyProgram, not both
        final List<String> errorDescList = PojoXmlUtil.getXmlFieldValue(response, "DescriptionRawText");
        if(CollectionUtils.isEmpty(errorDescList) || !errorDescList.get(0).contains("LoyaltyProgramList must contain only Accreditive LoyaltyProgram or Car LoyaltyProgram, not both")){
            Assert.fail("DescriptionRawText should be returned:LoyaltyProgramList must contain only Accreditive LoyaltyProgram or Car LoyaltyProgram, not both");
        }

    }
}
