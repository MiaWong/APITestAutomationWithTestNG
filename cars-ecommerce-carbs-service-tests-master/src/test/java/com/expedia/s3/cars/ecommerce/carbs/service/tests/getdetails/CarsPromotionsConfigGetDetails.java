package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarPromotionsCommonVerifier;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

/**
 * Created by miawang on 9/10/2018.
 */

public class CarsPromotionsConfigGetDetails extends SuiteCommon {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void carsInclusionAndVendorPromotionsAmadeusAgencyRoundTrip() throws Exception {
        final TestScenario testScenario = CommonScenarios.Amadeus_US_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        carsPromotionsConfigurableTest(testScenario, "1053803", "SX", "1", "1", "0", "0");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void carsMultipleInclusionPromotionsMNGDSPRoundTrip() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        carsPromotionsConfigurableTest(testScenario, "1053804", "FF", "0", "1", "0", "0");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void carsInclusionPromotionsTitaniumGDSPRoundTrip() throws Exception {
        final TestScenario testScenario = CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario();
        carsPromotionsConfigurableTest(testScenario, "1053805", "ET", "0", "1", "0", "0");
    }

    private void carsPromotionsConfigurableTest(TestScenario testScenario, String tuid, String vendorCode, String shoppingCarVendorPromotions, String shoppingCarInclusionDetails, String shoppingCarExclusionDetails, String shoppingCarMiscellaneousInfo) throws Exception {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final TestData testData = new TestData(httpClient, testScenario, tuid, ExecutionHelper.generateNewOrigGUID(spooferTransport));
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, DatasourceHelper.getCarInventoryDatasource(), true, vendorCode);
        //set CarPromotion POS config values
        final CarPromotionsCommonVerifier carPromotionsCommonVerifier = new CarPromotionsCommonVerifier();
        carPromotionsCommonVerifier.setCarsPromotionsPosConfigs(testData, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);

        //send search + getDetail Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        //search response Verification
        carPromotionsCommonVerifier.verifyCarPromotionsInSearchResponse(requestGenerator.getSearchResponseType(), testData, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);
        //getDetail response Verification
        carPromotionsCommonVerifier.verifyCarPromotionsInGetDetailsResponse(requestGenerator.getGetDetailsResponseType(), testData, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);
    }
}
