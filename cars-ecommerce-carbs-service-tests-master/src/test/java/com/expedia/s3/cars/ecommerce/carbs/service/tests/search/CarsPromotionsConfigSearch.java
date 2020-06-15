package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

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
 * Created by ankimaheshwar on 10/30/2018.
 */

public class CarsPromotionsConfigSearch extends SuiteCommon {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void carsInclusionAndExclusionPromotionsUSLocationAgencyOnAirport() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        carsPromotionsConfigurableTest(testScenario, "1053801", "ZD", "0", "1", "1", "0");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void carsInclusionPromotionsUSLocationAgencyOnAirport() throws Exception {
        final TestScenario testScenario = CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        carsPromotionsConfigurableTest(testScenario, "1053802", "ZI", "0", "1", "0", "0");
    }

    private void carsPromotionsConfigurableTest(TestScenario testScenario, String tuid, String vendorCode, String shoppingCarVendorPromotions, String shoppingCarInclusionDetails, String shoppingCarExclusionDetails, String shoppingCarMiscellaneousInfo) throws Exception {
        final SpooferTransport spoofer = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final TestData testData = new TestData(httpClient, testScenario, tuid, ExecutionHelper.generateNewOrigGUID(spoofer));
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, DatasourceHelper.getCarInventoryDatasource(), true, vendorCode);
        //set CarPromotion POS config values
        final CarPromotionsCommonVerifier carPromotionsCommonVerifier = new CarPromotionsCommonVerifier();
        carPromotionsCommonVerifier.setCarsPromotionsPosConfigs(testData, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);

        //send search request and verify the response
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spoofer, DatasourceHelper.getCarInventoryDatasource(), logger);
        carPromotionsCommonVerifier.verifyCarPromotionsInSearchResponse(requestGenerator.getSearchResponseType(), testData, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);
    }
}
