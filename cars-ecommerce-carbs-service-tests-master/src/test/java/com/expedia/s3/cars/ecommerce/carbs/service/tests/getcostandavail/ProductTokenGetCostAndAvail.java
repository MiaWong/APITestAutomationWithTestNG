package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMServiceSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.OmReserveVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarsInventoryKeyComparator;
import expedia.om.supply.messages.defn.v1.GetOrderProcessRequest;
import expedia.om.supply.messages.defn.v1.GetOrderProcessResponseType;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductTokenGetCostAndAvail extends SuiteCommon {
    Logger logger = Logger.getLogger(getClass());
    private DataSource carsInvDatasource;

    //test: CostAvail/GetOrderProcess with token and CarInventoryKey - Agency, one way
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs507319CarProductTokenAndInventoryKeyForAgencyAndOneWay() throws Exception {
        validateCarInventoryKeyForCostAndAvailAndGetOrderProcess(CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OffAirport_oneway.getTestScenario(), "ExtraHourPrice", "507319", CommonEnumManager.TimeDuration.Days3, false, false);
    }

    //test: CostAvail/GetOrderProcess with token and CarInventoryKey - GDSP
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs507322CarProductTokenAndInventoryKeyForGDSP() throws Exception {
        validateCarInventoryKeyForCostAndAvailAndGetOrderProcess(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(), "WSCS_EUR_Daily", "507322", CommonEnumManager.TimeDuration.Days3, false, false);
    }

    //test: CostAvail/GetOrderProcess with token and CarInventoryKey - loyalty program
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs989827CarProductTokenAndInventoryKeyWithLoyalty() throws Exception {
        validateCarInventoryKeyForCostAndAvailAndGetOrderProcess(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "PriceListAgencyDailyPrice", "989827", CommonEnumManager.TimeDuration.Weekend3dayextraHours, true, true);
    }

    public void validateCarInventoryKeyForCostAndAvailAndGetOrderProcess(TestScenario testScenario, String spooferScenario, String tuid, CommonEnumManager.TimeDuration timeDuration, boolean extraHours, boolean ifLoyaltyAndVendorCode) throws IOException, DataAccessException {
        boolean result;
        final StringBuilder errorMsg = new StringBuilder();

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenario).build(), guid);
        final TestData testData = new TestData(httpClient, timeDuration, testScenario, tuid, guid, extraHours);

        if (ifLoyaltyAndVendorCode) {
            carsInvDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_INVENTORY_DATABASE_SERVER, SettingsProvider.DB_CARS_INVENTORY_DATABASE_NAME,
                    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
            ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, carsInvDatasource, true, RequestDefaultValues.VENDOR_CODE_AL);
            ExecutionHelper.setCarRateOfTestData(testData, true, RequestDefaultValues.LOYALTYNUMBER_ALAMO, "");
        }
        final CarbsRequestGenerator carbsRequestGenerator = ExecutionHelper.getCostAndAvailabilityByBusinessModelIDAndServiceProviderID(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        result = CarsInventoryKeyComparator.isCarInventoryKeyEqual(carbsRequestGenerator.getSelectedCarProduct().getCarInventoryKey(), carbsRequestGenerator.getGetCostAndAvailabilityResponseType()
                .getCarProductList().getCarProduct().get(0).getCarInventoryKey(), errorMsg, new ArrayList<>());
        if (!result) {
            Assert.fail("CarInventoryKey of selected carProduct and CostAndAvail are not equal");
        }

        final CarbsOMReserveReqAndRespGenerator carOMSReqAndRespObj = new CarbsOMReserveReqAndRespGenerator(carbsRequestGenerator.getSearchRequestType(), carbsRequestGenerator.getSearchResponseType());
        carOMSReqAndRespObj.setGetCostAndAvailabilityResponseType(carbsRequestGenerator.getGetCostAndAvailabilityResponseType());
        carOMSReqAndRespObj.setSelectCarProduct(carbsRequestGenerator.getSelectedCarProduct());
        final GetOrderProcessRequest getOrderProcessRequest = carOMSReqAndRespObj.createGetOrderProcessRequest(testData);
        final GetOrderProcessResponseType getOrderProcessResponse = CarbsOMServiceSender.sendGetOrderProcessResponse(guid, httpClient, getOrderProcessRequest);
        OmReserveVerifier.isGetOrderProcessWorksVerifier(testData.getGuid(), testData.getScenarios(), getOrderProcessRequest, getOrderProcessResponse);

        result = CarsInventoryKeyComparator.isCarInventoryKeyEqual(carOMSReqAndRespObj.getSelectCarProduct().getCarInventoryKey(), getOrderProcessResponse.getOrderProductList()
                .getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct().getCarInventoryKey(), errorMsg, new ArrayList<>());

        if (!result) {
            Assert.fail("CarInventoryKey of selected carProduct and OMS reserve call are not equal");
        }
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs81554CarBSGetCostAvailRequestSendSuccessfullyByUsingOnlyPIID() throws Exception {
        final TestData testData = new TestData(httpClient, CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "81554", PojoXmlUtil.getRandomGuid());

        testCarBSGetCostAvailOnlyPIID(testData);
    }

    public void testCarBSGetCostAvailOnlyPIID(TestData testData) throws IOException, DataAccessException {

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData);
        final CarECommerceGetCostAndAvailabilityRequestType costAndAvailabilityRequestType = requestGenerator.createCarbsCostAndAvailRequest();
        //just send a CarproductToken down stream.
        final String carProductToken = costAndAvailabilityRequestType.getCarProductList().getCarProduct().get(0).getCarProductToken();
        final CarProductType carProductType = new CarProductType();
        carProductType.setCarProductToken(carProductToken);
        final List<CarProductType> carProductTypeList = new ArrayList<>();
        carProductTypeList.add(carProductType);
        costAndAvailabilityRequestType.getCarProductList().setCarProduct(carProductTypeList);

        final CarECommerceGetCostAndAvailabilityResponseType costAndAvailabilityResponseType = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(testData.getGuid(), testData.getHttpClient(), costAndAvailabilityRequestType);
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(costAndAvailabilityRequestType, costAndAvailabilityResponseType);
        VerificationHelper.getCostAndAvailabilityBasicVerification(getCostAndAvailabilityVerificationInput, null, testData.getScenarios(), testData.getGuid(), false, logger);

    }



}
