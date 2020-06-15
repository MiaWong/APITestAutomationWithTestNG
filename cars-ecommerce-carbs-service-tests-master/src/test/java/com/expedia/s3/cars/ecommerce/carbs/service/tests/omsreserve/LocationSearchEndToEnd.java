package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsLocationSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestFromLocationAPI;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveErrorHandlingRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.CarOMSReserveVerify;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyLocationSearchForIATA;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.BusinessModel;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonLocationSearchScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class LocationSearchEndToEnd extends SuiteCommon {

    private static final String EUROPCAR_CD_CODE = "EP-40823215";
    private static final String AVIS_CD_CODE = "ZI-N865555";
    private static final String AVIS_CD_CODE_1 = "ZI-N865556";
    private static final String DEL_OR_COL_PLACEID = "7278";

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs303759EndToEndHomeAddressDeliveryAndCollectionOneWay() throws Exception {
        final SpooferTransport spooferTransport1 = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid1 = ExecutionHelper.generateNewOrigGUID(spooferTransport1);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final LocationSearchTestScenario pickUpScenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP.getTestScenario();
        pickUpScenario.setIataCode("LYS");
        pickUpScenario.setOutOfOfficeHourBoolean("");
        pickUpScenario.setCollectionBoolean("false");
        pickUpScenario.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());
        spooferTransport1.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "HomeAddressLocationSearch").build(), randomGuid1);
        final CarbsLocationSearchRequestGenerator pickUpRequestGenerator = ExecutionHelper.locationIataSearch(pickUpScenario, "123468", httpClient, randomGuid1, null);
        VerifyLocationSearchForIATA.shouldLocationReturned(pickUpRequestGenerator);
        // second location search request
        final LocationSearchTestScenario dropOffScenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP.getTestScenario();
        pickUpScenario.setIataCode("LYS");
        dropOffScenario.setOutOfOfficeHourBoolean("");
        dropOffScenario.setCollectionBoolean("true");
        dropOffScenario.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());

        final SpooferTransport spooferTransport2 = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid2 = ExecutionHelper.generateNewOrigGUID(spooferTransport2);
        spooferTransport2.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "HomeAddressLocationSearchCollection").build(), randomGuid2);
        final CarbsLocationSearchRequestGenerator dropOffRequestGenerator = ExecutionHelper.locationIataSearch(dropOffScenario, "213580", httpClient, randomGuid2, null);
        VerifyLocationSearchForIATA.shouldLocationReturned(dropOffRequestGenerator);

        final TestScenario newTestScenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_LYS_NCE.getTestScenario();
        final TestData testData = new TestData(httpClient, newTestScenario, "666668", randomGuid1);
        testData.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());
        final CarRate carRate = new CarRate();
        carRate.setCdCode(EUROPCAR_CD_CODE);
        testData.setCarRate(carRate);
        executeDeliveryAndCollection(spooferTransport1, randomGuid1, pickUpRequestGenerator, dropOffRequestGenerator, newTestScenario, testData);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs303762EndToEndHomeAddressDeliveryAndDropOffAtAgency() throws Exception {
        final SpooferTransport spooferTransport1 = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid1 = ExecutionHelper.generateNewOrigGUID(spooferTransport1);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final LocationSearchTestScenario pickUpScenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP.getTestScenario();
        pickUpScenario.setIataCode("LYS");
        pickUpScenario.setCollectionBoolean("false");
        pickUpScenario.setOutOfOfficeHourBoolean("");
        pickUpScenario.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());
        spooferTransport1.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "HomeAddressLocationLYS").build(), randomGuid1);
        final CarbsLocationSearchRequestGenerator pickUpRequestGenerator = ExecutionHelper.locationIataSearch(pickUpScenario, "266668", httpClient, randomGuid1, null);
        VerifyLocationSearchForIATA.shouldLocationReturned(pickUpRequestGenerator);

        final LocationSearchTestScenario dropOffScenario = CommonLocationSearchScenarios.CarBS_Location_IATA_Airport_DELT_COLF_OHRF_CP.getTestScenario();
        dropOffScenario.setIataCode("BIQ");
        dropOffScenario.setOutOfOfficeHourBoolean("");
        dropOffScenario.setCollectionBoolean("");
        dropOffScenario.setDeliveryBoolean("");

        dropOffScenario.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());
        final SpooferTransport spooferTransport2 = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid2 = ExecutionHelper.generateNewOrigGUID(spooferTransport2);
        spooferTransport2.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "HomeAddressLocationBIQ").build(), randomGuid2);

        final CarbsLocationSearchRequestGenerator dropOffRequestGenerator = ExecutionHelper.locationIataSearch(dropOffScenario, "266661", httpClient, randomGuid2, null);
        VerifyLocationSearchForIATA.shouldLocationReturned(dropOffRequestGenerator);

        final TestScenario newTestScenario = new TestScenario("Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_LYS_BIQ",
                "FRA","10116","6045","EUR","LYS","BIQ",false, PurchaseType.CarOnly, BusinessModel.Agency.getValue(),6);

        final TestData testData = new TestData(httpClient, newTestScenario, "266668", randomGuid1);
        testData.setClientCode(carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode());
        final CarRate carRate = new CarRate();
        carRate.setCdCode(EUROPCAR_CD_CODE);
        testData.setCarRate(carRate);
        executeDeliveryAndCollection(spooferTransport1, randomGuid1, pickUpRequestGenerator, dropOffRequestGenerator, newTestScenario, testData);

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs303789EndToEndPlaceIDDeliveryAndCollectionRoundTrip() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "AmaduesPlaceID").build(), randomGuid);

        final TestData testData = new TestData(httpClient, scenario, "366668", randomGuid);
        final CarRate carRate = new CarRate();
        carRate.setCdCode(AVIS_CD_CODE);
        testData.setCarRate(carRate);

        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode(RequestDefaultValues.VENDOR_CODE_ZI);
        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final List<CarVendor> carVendors = carsInventoryHelper.getCarVendorList(RequestDefaultValues.VENDOR_CODE_ZI);
        specialHandleParam.setVendorSupplierID(Long.valueOf(carVendors.get(0).getSupplierID()));

        // set deliveryPlaceID
        specialHandleParam.setDeliveryPlaceID(DEL_OR_COL_PLACEID);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        executePlaceIDDeliveryAndCollection(spooferTransport, randomGuid, scenario, clientCode, testData);

    }

    //Verify Location details are correctly returned in PreparePurchaseResponse when send a PreparePurchaseRequest with HomeDelivery to CarBS
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs239133HomeDelivery() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_URO.getTestScenario();

        final String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "AmaduesHomeDelivery").build(), randomGuid);
        final TestData testData = new TestData(httpClient, scenario, "466668", randomGuid);
        final CarRate carRate = new CarRate();
        carRate.setCdCode(RequestDefaultValues.ONE_CD_CODES);
        testData.setCarRate(carRate);

        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();

        // set home Delivery
        specialHandleParam.setDeliveryAvailable(true);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        executePlaceIDDeliveryAndCollection(spooferTransport, randomGuid, scenario, clientCode, testData);

    }

    //239136 - Verify Location details are correctly returned in PreparePurchaseResponse when send a PreparePurchaseRequest with PlaceIDDeliveryAndCollection to CarBS
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs239136PlaceIDDeliveryAndCollection() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();

        final String clientCode = carBSHelper.getClientListById(CarCommonEnumManager.ClientID.ClientID_3.getValue()).get(0).getClientCode();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "AmaduesPlaceIDDelAndCol").build(), randomGuid);
        final TestData testData = new TestData(httpClient, scenario, "566668", randomGuid);
        final CarRate carRate = new CarRate();
        carRate.setCdCode(AVIS_CD_CODE_1);
        testData.setCarRate(carRate);

        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();

        // set home Delivery
        specialHandleParam.setDeliveryPlaceID(DEL_OR_COL_PLACEID);
        specialHandleParam.setCollectionPlaceID(DEL_OR_COL_PLACEID);
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        executePlaceIDDeliveryAndCollection(spooferTransport, randomGuid, scenario, clientCode, testData);

    }

    private void executeDeliveryAndCollection(SpooferTransport spooferTransport, String guid, CarbsLocationSearchRequestGenerator pickUpRequestGenerator, CarbsLocationSearchRequestGenerator dropOffRequestGenerator, TestScenario scenario, TestData testData) throws Exception {
        final CarECommerceSearchRequestType searchRequest = CarbsSearchRequestFromLocationAPI.buildCarBSSearchRequestFromLocationAPI(testData,
                pickUpRequestGenerator.getCarLocationIataSearchResponse(), dropOffRequestGenerator.getCarLocationIataSearchResponse());

        final CarECommerceSearchResponseType searchResponse = CarbsRequestSender.getCarbsSearchResponse(guid, httpClient, searchRequest);
        final CarProductType carProductType = CarBSSearchVerifier.isCarbsSearchWorksVerifier(testData, searchRequest, searchResponse);

        //getdetails
        final CarbsRequestGenerator carbsRequestGenerator = new CarbsRequestGenerator(searchRequest, searchResponse, testData);
        carbsRequestGenerator.setSelectedCarProduct(carProductType);

        //  Get DELIVERY/COLLECTION Address from location search response.
        final Map<String, AddressType> deliveryAndCollectionMap = ExecutionHelper.getAddressFromLocationSearchWithLocationKey(carProductType,
                pickUpRequestGenerator.getCarLocationIataSearchResponse(), dropOffRequestGenerator.getCarLocationIataSearchResponse());
        final TestScenarioSpecialHandleParam param = new TestScenarioSpecialHandleParam();
        param.setDeliveryAndCollectionAddress(deliveryAndCollectionMap);
        param.setDeliveryAvailable(true);
        testData.setTestScenarioSpecialHandleParam(param);


        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsRequestGenerator.createCarbsDetailsRequest();
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(guid, httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(guid, scenario, getDetailsRequestType, getDetailsResponseType);

        //getCostAndAvail
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsRequestGenerator.createCarbsCostAndAvailRequest();
        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(guid, httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(guid, scenario, getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(searchRequest, searchResponse);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        carOMSReqAndRespObj.setSelectCarProduct(carbsRequestGenerator.getSelectedCarProduct());

        final CarbsOMReserveReqAndRespGenerator omRequestGenerate = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSend(carOMSReqAndRespObj, testData, spooferTransport);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(omRequestGenerate);

        CarbsOMCancelRequestSender.omsCancelSend
                (testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), testData.getHttpClient(),
                        CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        // Verify Delivery and Collection

        CarOMSReserveVerify.collectionAndDeliveryVerify(omRequestGenerate.getPreparePurchaseRequestType(), omRequestGenerate.getPreparePurchaseResponseType());
    }

    private void executePlaceIDDeliveryAndCollection(SpooferTransport spooferTransport, String randomGuid, TestScenario scenario, String clientCode, TestData testData) throws Exception {
        final CarbsOMReserveReqAndRespGenerator oMSReserveSendWithShopMsg = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSendWithShopMsg(scenario, DatasourceHelper.getCarBSDatasource(),randomGuid, httpClient, testData, clientCode, spooferTransport);
        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(oMSReserveSendWithShopMsg);

        CarbsOMCancelRequestSender.omsCancelSend
                (testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), testData.getHttpClient(),
                        CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        // Verify Delivery and Collection

        CarOMSReserveVerify.collectionAndDeliveryVerify(oMSReserveSendWithShopMsg.getPreparePurchaseRequestType(), oMSReserveSendWithShopMsg.getPreparePurchaseResponseType());
    }


}
