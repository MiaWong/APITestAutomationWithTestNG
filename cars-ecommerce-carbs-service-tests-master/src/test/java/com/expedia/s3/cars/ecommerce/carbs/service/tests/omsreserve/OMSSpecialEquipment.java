package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.ClientConfigSettigName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by v-mechen on 11/13/2018.
 */
@SuppressWarnings("PMD")
public class OMSSpecialEquipment  extends SuiteCommon {

    //GetOrderProcess.returnRequestedSpecialEquipment/enable is ON for clientID 1 on stt05
    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs480555GetOrderReturnRequestSpecialEquipOn() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL_CSI");
        final TestData testData = new TestData(httpClient, testScenario, "480555", guid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        testData.setCarSpecialEquipmentCode("InfantChildSeat,LeftHandControl");
        testOMSSpecialEquipment(testData, CarCommonEnumManager.ClientID.ClientID_1);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs480558GetOrderReturnRequestSpecialEquipOnCarVehicleOptionList() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL_CSI");
        final TestData testData = new TestData(httpClient, testScenario, "480558", guid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        testData.setSpecialEquipmentEnumType("CSI,HCL"); //SpecialEquipmentEnumType will set specail eqipment to CarVehicleOptionList
        testOMSSpecialEquipment(testData, CarCommonEnumManager.ClientID.ClientID_1);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs480558GetOrderReturnRequestSpecialEquipOff() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "Amadeus_FRA_Standalone_RoundTrip_OnAirport_CDG_SpecialEquipment_HCL_CSI");
        final TestData testData = new TestData(httpClient, testScenario, "4805581", guid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_3));
        testData.setSpecialEquipmentEnumType("CSI,HCL"); //SpecialEquipmentEnumType will set specail eqipment to CarVehicleOptionList
        testOMSSpecialEquipment(testData, CarCommonEnumManager.ClientID.ClientID_3);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION_AMADEUS, TestGroup.BOOKING_REGRESSION})
    public void tfs489654MNSpecialEquipListAndCarVehicleOptionList() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "");
        final TestData testData = new TestData(httpClient, testScenario, "489654", guid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        testData.setSpecialEquipmentEnumType("CSI,HCL"); //SpecialEquipmentEnumType will set specail eqipment to CarVehicleOptionList
        testData.setCarSpecialEquipmentCode("InfantChildSeat,LeftHandControl");
        testOMSSpecialEquipment(testData, CarCommonEnumManager.ClientID.ClientID_1);

    }

    private void testOMSSpecialEquipment(TestData testData, CarCommonEnumManager.ClientID clientID) throws Exception {
        //OMS reserve
        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carOMSReqAndRespObj);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        //Do special equpment verification
        verifySpecailEquipment(carOMSReqAndRespObj, clientID);
    }

    private void verifySpecailEquipment(CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj, CarCommonEnumManager.ClientID clientID) throws DataAccessException {
        //if GetOrderProcess.returnRequestedSpecialEquipment/enable is ON, specialequipment should be returned in GetOrderProcess response
        //If CarSpecialEquipmentList, then use it; if it's null and CarVehicleOptionList is not null, use it
        final CarReservationType getOrderReqCarReservation = carOMSReqAndRespObj.getGetOrderProcessRequest().getConfiguredOfferData().getCarOfferData().getCarReservation();
        if(null == getOrderReqCarReservation.getCarSpecialEquipmentList() && null != getOrderReqCarReservation.getCarProduct().getCarVehicleOptionList() &&
                CollectionUtils.isNotEmpty(getOrderReqCarReservation.getCarProduct().getCarVehicleOptionList().getCarVehicleOption()))
        {
            final CarSpecialEquipmentListType specialEquipListFromVehicleOptionList = new CarSpecialEquipmentListType();
            getOrderReqCarReservation.setCarSpecialEquipmentList(specialEquipListFromVehicleOptionList);
            specialEquipListFromVehicleOptionList.setCarSpecialEquipment(new ArrayList<>());
            for(final CarVehicleOptionType vehicleOption : getOrderReqCarReservation.getCarProduct().getCarVehicleOptionList().getCarVehicleOption()){
                final CarSpecialEquipmentType specialEquipment = new CarSpecialEquipmentType();
                specialEquipment.setCarSpecialEquipmentCode(vehicleOption.getCarSpecialEquipmentCode());
                specialEquipListFromVehicleOptionList.getCarSpecialEquipment().add(specialEquipment);
            }
        }
        final StringBuilder erroMsg = new StringBuilder();
        boolean compared = CompareUtil.compareObject(getOrderReqCarReservation.getCarSpecialEquipmentList(),
                carOMSReqAndRespObj.getGetOrderProcessResponseType().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarSpecialEquipmentList(),
                new ArrayList<>(), erroMsg);
        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());
        if (clientConfigHelper.checkClientConfig(PojoXmlUtil.getEnvironment(), ClientConfigSettigName.GETORDERPROCESS_RETURNREQUESTEDSPECIALEQUIPMENT.stringValue(),
                Integer.parseInt(clientID.getValue()), "1") && !compared) {
            Assert.fail("When client config GetOrderProcess.returnRequestedSpecialEquipment/enable is on, we should return SpecialEquipment from GetOrderProcess request!");
        }

        if (!clientConfigHelper.checkClientConfig(PojoXmlUtil.getEnvironment(), ClientConfigSettigName.GETORDERPROCESS_RETURNREQUESTEDSPECIALEQUIPMENT.stringValue(),
                Integer.parseInt(clientID.getValue()), "1") && compared) {
            Assert.fail("When client config GetOrderProcess.returnRequestedSpecialEquipment/enable is off, we should not return SpecialEquipment from GetOrderProcess request!");
        }

        //For preparePurchase, it should return the specialEquipmentList from CarSS reserve response and when they exist in getDetails response
        //In testNG framework, since we don't get downstream request/response from carlog, so use the request for verification - consider that spoofer return requested specail equpment
        CarSpecialEquipmentListType expPreparedSpecialEquipList = new CarSpecialEquipmentListType();
        expPreparedSpecialEquipList.setCarSpecialEquipment(new ArrayList<>());
        for(final CarSpecialEquipmentType reqSpecialEquip : getOrderReqCarReservation.getCarSpecialEquipmentList().getCarSpecialEquipment())
        {
            for(final CarVehicleOptionType detailsVehicle : carOMSReqAndRespObj.getGetDetailsResponseType().getCarProductList().getCarProduct().get(0).getCarVehicleOptionList().getCarVehicleOption())
            {
                if(reqSpecialEquip.getCarSpecialEquipmentCode().equals(detailsVehicle.getCarSpecialEquipmentCode()))
                {
                    final CarSpecialEquipmentType expPreparedSpecialEquip = new CarSpecialEquipmentType();
                    expPreparedSpecialEquip.setCarSpecialEquipmentCode(reqSpecialEquip.getCarSpecialEquipmentCode());
                    expPreparedSpecialEquip.setBookingStateCode("Unconfirmed");
                    expPreparedSpecialEquipList.getCarSpecialEquipment().add(expPreparedSpecialEquip);
                    break;
                }
            }
        }
        if(expPreparedSpecialEquipList.getCarSpecialEquipment().isEmpty())
        {
            expPreparedSpecialEquipList = null;
        }
        CarSpecialEquipmentListType actPreparedSpecialEquipList = carOMSReqAndRespObj.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData()
                .getCarOfferData().getCarReservation().getCarSpecialEquipmentList();
        if(null != actPreparedSpecialEquipList && (null == actPreparedSpecialEquipList.getCarSpecialEquipment() ||
                actPreparedSpecialEquipList.getCarSpecialEquipment().isEmpty()))
        {
            actPreparedSpecialEquipList = null;
        }
        compared = CompareUtil.compareObject(expPreparedSpecialEquipList, actPreparedSpecialEquipList,new ArrayList<>(), erroMsg);
        if (!compared) {
            Assert.fail(String.format("CarSpecialEquipmentList in PreparePurchase response is not existed, compare error: %s!", erroMsg.toString()));
        }

    }
}
