package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;

import com.expedia.e3.data.basetypes.defn.v4.UserKeyType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramListType;
import com.expedia.e3.data.financetypes.defn.v4.LoyaltyProgramType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.CarbsDB;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.OmReserveVerifier;
import com.expedia.s3.cars.framework.core.appconfig.AppConfig;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.ClientConfigSettigName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestDataErrHandle;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by v-mechen on 10/25/2018.
 *
 * TODO: fix the PMD errors
 */
@SuppressWarnings("PMD")
public class SoftErrorHandling extends SuiteCommon
{
    private static final String setPosConfigUrl = AppConfig.resolveStringValue("${setPosConfig.server.address}");
    final CarbsDB carbsDB = new CarbsDB(DatasourceHelper.getCarBSDatasource());

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333627SoftErrorAmadeusInvalidSpecailRequestFeatureOff() throws Exception
    {
        final TestScenario testScenario =
                CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        testSoftError(testScenario, "333627", "", CarCommonEnumManager.ClientID.ClientID_3,
                new TestDataErrHandle(String.valueOf(
                        CarCommonEnumManager.InValidFildType.InvalidSpecialRequest), "", ""));
    }

    private void testSoftError(TestScenario scenario, String tuid, String spooferScenarioName,
                               CarCommonEnumManager.ClientID clientID, TestDataErrHandle testDataErrHandle)
            throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        if(StringUtils.isNotBlank(spooferScenarioName))
        {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride(
                    "ScenarioName", spooferScenarioName).build(), guid);
        }

        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        testData.setClientCode(CommonTestHelper.getClientCode(clientID));
        testData.setErrHandle(testDataErrHandle);

        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj =
                CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(testData, false);

        //OMS book
        try
        {
            CarbsOMReserveRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData);
            //Cancel if car booking is successful
            if (null != carOMSReqAndRespObj.getCommitPreparePurchaseResponseType())
            {
                final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj =
                        new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
                CarbsOMCancelRequestSender.omsCancelSend(
                        testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient,
                        CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            }
        }
        catch (Exception exception)
        {
            //NOP
        }

        //Verify soft error
        verifySoftError(carOMSReqAndRespObj.getPreparePurchaseResponseType(), clientID);
    }

    private void verifySoftError(PreparePurchaseResponseType response, CarCommonEnumManager.ClientID clientID)
            throws DataAccessException
    {
        //Verify XPath returned under error list
        if (CollectionUtils.isEmpty(PojoXmlUtil.getXmlFieldValue(response, "XPath")))
        {
            Assert.fail("XPath should be returned under error list!");
        }

        //if Booking.softErrorHandling.retry/enable is ON, we should have car returned
        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());

        boolean softErrorClientConfig;
        if (clientID.equals(CarCommonEnumManager.ClientID.ClientID_empty))
        {
            softErrorClientConfig = carbsDB.getValueByName(
                    ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString()) == 1 ? true : false;
        }
        else
        {
            softErrorClientConfig = clientConfigHelper.checkClientConfig(
                    PojoXmlUtil.getEnvironment(), ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.stringValue(),
                    Integer.parseInt(clientID.getValue()), "1");
        }

        if (softErrorClientConfig)
        {
            if (null == response.getPreparedItems() || null == response.getPreparedItems().getBookedItemList() ||
                    CollectionUtils.isEmpty(response.getPreparedItems().getBookedItemList().getBookedItem()))
            {
                Assert.fail("When Booking.softErrorHandling.retry/enable is ON, "
                        + "car should be booked successully with error!");
            }
        }
    }

    //Test case 333681: CarBS OMS Soft Error Handling - Verify CarBS booking failed with invalid CarClubNumber and returns a
    // FieldInvalidError in PreparePurchase response back without retry PreparePurchase request
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333681CarBSSoftError_InvalidCarClubNumber_nonEgencia() throws DataAccessException
    {
        final int returnSoftErrors = carbsDB.getValueByName(
                ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString());
        final int retry = carbsDB.getValueByName(
                ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString());

        if (returnSoftErrors == 1 && retry == 1)
        {
            testCarBSSoftError(
                    CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "333681",
                    CarCommonEnumManager.InValidFildType.InvalidCarClubNumber.toString(),
                    new TestDataErrHandle( "FieldInvalidError", "EMAAAaaaaaaaaaaaaaaaaaaaaa", "Invalid"),
                    "0Q7XRN", 40);
        }
        else
        {
            Assert.fail("The "+ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString()+
                    " and "+ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString()+
                    " for clienID= null and environmentName=null are not equal the expected value=1 in Client config.");
        }
    }

    //Test case 333683: CarBS OMS Soft Error Handling - Verify CarBS booking failed with invalid FrequentTravelerNumber
    // and returns a FieldInvalidError in PreparePurchase response back without retry PreparePurchase request
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333683CarBSSoftError_InvalidFrequentTravelerNumber_nonEgencia() throws DataAccessException
    {
        final int returnSoftErrors = carbsDB.getValueByName(
                ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString());
        final int retry = carbsDB.getValueByName(ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString());

        if (returnSoftErrors == 1 && retry == 1)
        {
            testCarBSSoftError(
                    CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "333683",
                    CarCommonEnumManager.InValidFildType.InvalidFrequentTravelerNumber.toString(),
                    new TestDataErrHandle("FieldInvalidError", "InvalidFrequentTravelerNumber", "Invalid"),
                    "0Q7XRN", 40);
        }
        else
        {
            Assert.fail("The "+ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString()+
                    " and "+ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString()+
                    " for clienID= null and environmentName=null are not equal the expected value=1 in Client config.");
        }
    }

    //Test case 333685: CarBS OMS Soft Error Handling - Verify CarBS booking failed with invalid CouponCode and returns
    // a FieldInvalidError in PreparePurchase response back without retry PreparePurchase request
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs333685CarBSSoftError_InvalidCouponCode_nonEgencia() throws DataAccessException
    {
        final int returnSoftErrors = carbsDB.getValueByName(
                ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString());
        final int retry = carbsDB.getValueByName(ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString());
        if (returnSoftErrors == 1 && retry == 1)
        {
            testCarBSSoftError(
                    CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "333685",
                    CarCommonEnumManager.InValidFildType.InvalidCouponCode.toString(),
                    new TestDataErrHandle("FieldInvalidError", "EMInvalidCouponCodeaaaaa","Invalid"),
                    "0Q7XRN", 40);
        }
        else
        {
            Assert.fail("The "
                    + ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETURNSOFTERRORS.toString()
                    + " and "+ClientConfigSettigName.BOOKING_SOFTERRORHANDLING_RETRY.toString()
                    + " for clienID= null and environmentName=null are not equal the expected value=1"
                    + " in Client config.");
        }
    }

    public void testCarBSSoftError(TestScenario scenario, String tuid, String errorScenarioName,
                                   TestDataErrHandle testDataErrHandle,
                                   String clientCode, int preferedVendor)
    {
        try
        {
            PreparePurchaseResponseType preparePurchaseResponse = carbsBookingAndShoppingSoftError(scenario, tuid,
                    errorScenarioName, testDataErrHandle.getInvalidValue(), clientCode, preferedVendor);

            verifySoftError(preparePurchaseResponse, CarCommonEnumManager.ClientID.ClientID_empty);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     *
     *
     * @param scenario
     * @param tuid
     * @param errorScenarioName
     * @param invalidValue
     * @param clientCode
     * @param preferedVendor
     * @return
     * @throws Exception
     */
    private PreparePurchaseResponseType carbsBookingAndShoppingSoftError(
            TestScenario scenario, String tuid, String errorScenarioName, String invalidValue,
            String clientCode, int preferedVendor) throws Exception
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride(
                "ScenarioName", "ErrorMap").build(), guid);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        if (clientCode != null)
        {
            testData.setClientCode(clientCode);
        }

        if (preferedVendor > 0)
        {
            testData.setTestScenarioSpecialHandleParam(new TestScenarioSpecialHandleParam());
            testData.getTestScenarioSpecialHandleParam().setVendorSupplierID(preferedVendor);
        }

        //Shopping
        //search and costAvail for OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj =
                CarbsOMReserveRequestSender.sendShopMsgForOMSReserve(testData, true);

        //Booking
        CarbsOMReserveRequestSender.CarBSGetOrderProcessSend(testData, carOMSReqAndRespObj);
        CarbsOMReserveRequestSender.CarBSCreateRecordSend(testData, carOMSReqAndRespObj);

        final PreparePurchaseRequest prePurchaseRequest = carOMSReqAndRespObj.createPreparePurchaseRequest(testData);

        // upddate the request with the value get from config, like the specailequipment, CD code etc
        String loyaltyProgramCode;
        String loyaltyProgramMembershipCode;
        final String loyaltyProgramCategoryCode = null;

        //invalid CarClubNumber
        if (CarCommonEnumManager.InValidFildType.InvalidCarClubNumber.toString().equals(errorScenarioName))
        {
            loyaltyProgramMembershipCode = invalidValue;
            loyaltyProgramCode = "CarClubNumber";

            if(null == prePurchaseRequest.getConfiguredProductData().getCarOfferData()
                    .getCarReservation().getCarProduct().getCarInventoryKey()
                    .getCarRate().getLoyaltyProgram())
            {
                prePurchaseRequest.getConfiguredProductData().getCarOfferData()
                        .getCarReservation().getCarProduct().getCarInventoryKey()
                        .getCarRate().setLoyaltyProgram(new LoyaltyProgramType());
            }
            prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey()
                    .getCarRate().getLoyaltyProgram().setLoyaltyProgramMembershipCode(loyaltyProgramMembershipCode);

            prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey()
                    .getCarRate().getLoyaltyProgram().setLoyaltyProgramCode(loyaltyProgramCode);

            setPreparePurchaseTravler(prePurchaseRequest);
        }

        //invalid CouponCode
        if (CarCommonEnumManager.InValidFildType.InvalidCouponCode.toString().equals(errorScenarioName))
        {
            prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct()
                    .getCarInventoryKey().getCarRate().setPromoCode(invalidValue);
        }

        //invalid FrequentTravelerNumber
        if (CarCommonEnumManager.InValidFildType.InvalidFrequentTravelerNumber.toString().equals(errorScenarioName))
        {
            loyaltyProgramMembershipCode = invalidValue;
            loyaltyProgramCode = "EM";
            final LoyaltyProgram loyaltyProgram = new LoyaltyProgram();

            if(null == prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                    .getTraveler().get(0).getLoyaltyProgramList())
            {
                prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                        .getTraveler().get(0).setLoyaltyProgramList(new LoyaltyProgramListType());
            }

            if(null == prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                    .getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram())
            {
                prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                        .getTraveler().get(0).getLoyaltyProgramList().setLoyaltyProgram(new ArrayList<>());
            }

            prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                    .getTraveler().get(0).getLoyaltyProgramList().getLoyaltyProgram().
                    add(loyaltyProgram.buildLoyaltyProgram(
                            loyaltyProgramCode, loyaltyProgramMembershipCode, loyaltyProgramCategoryCode));

            setPreparePurchaseTravler(prePurchaseRequest);
        }

        final PreparePurchaseResponseType preparePurchaseResponse =
                CarbsOMServiceSender.sendPreparePurchaseResponse(
                        testData.getGuid(), testData.getHttpClient(), prePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseRequestType(prePurchaseRequest);
        carOMSReqAndRespObj.setPreparePurchaseResponseType(preparePurchaseResponse);
        OmReserveVerifier.isPreparePurchaseWorksVerifier(
                testData.getGuid(),testData.getScenarios(),prePurchaseRequest,preparePurchaseResponse);
        if (testData.isRegression())
        {
            OmReserveVerifier.preparePurchaseRegressionVerifier(testData, carOMSReqAndRespObj);
        }

        CarbsOMReserveRequestSender.CarBSRollbackPreparePurchaseSend(testData, carOMSReqAndRespObj);

        return preparePurchaseResponse;
    }

    private void setPreparePurchaseTravler(PreparePurchaseRequest prePurchaseRequest)
    {
        if(null == prePurchaseRequest.getConfiguredProductData().getCarOfferData()
                .getCarReservation().getTravelerList().getTraveler().get(0).getUserKey())
        {
            prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation()
                    .getTravelerList().getTraveler().get(0).setUserKey(new UserKeyType());
        }

        prePurchaseRequest.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList()
                .getTraveler().get(0).getUserKey().setUserID(prePurchaseRequest.getConfiguredProductData()
                .getCarOfferData().getCarLegacyBookingData().getLogonUserKey().getUserID());
    }

    //https://confluence.expedia.biz/display/SSG/Test+Plan+For+User+Story+532149%3A+MicronNexus+SCS+not+properly+handling+warnings+for+special+equipment
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs727582MNSpecialEquipmentNotAvailableError() throws Exception
    {
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        testSpecialEquipmentNotAvailableError(testScenario, "727582", "SpecialEquipment_OneUnavailable");
    }

    private void testSpecialEquipmentNotAvailableError(TestScenario scenario, String tuid, String spooferScenarioName)
            throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);

        //OMS reserve with shop messages
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //OMS cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //Verify SpecialEquipmentNotAvailableError
        verifySpecialEquipmentNotAvailableError(carOMSReqAndRespObj.getPreparePurchaseResponseType());
    }

    private void verifySpecialEquipmentNotAvailableError(PreparePurchaseResponseType response)
    {
        //Verify SpecialEquipmentNotAvailableError
        if(CollectionUtils.isEmpty(PojoXmlUtil.getXmlFieldValue(response, "SpecialEquipmentNotAvailableError"))){
            Assert.fail("SpecialEquipmentNotAvailableError should be returned!");
        }

        //Car should be booked successfully
        if(null == response.getPreparedItems() || null == response.getPreparedItems().getBookedItemList() ||
                CollectionUtils.isEmpty(response.getPreparedItems().getBookedItemList().getBookedItem()))
        {
            Assert.fail("Car should be booked successully with SpecialEquipmentNotAvailableError!");
        }

    }
}
