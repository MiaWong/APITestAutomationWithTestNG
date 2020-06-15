package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyProductTokenSearch;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;

/**
 * Created by yyang4 on 6/21/2018.
 */
public class ProductTokenSearch extends SuiteCommon
{
    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "507426"})
    public void tfsProductTokenSearch507426() throws Exception
    {
        //Verify Utility interface testing - encode and decode works well for  WSPN standalone Agency OnAirport RoundTrip  1 day
        //includeDetails true
        doTest(CommonScenarios.Worldspan_US_Agency_Standalone_LAS_OnAirport.getTestScenario(), "507426", CommonEnumManager.TimeDuration.Daily);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "507427"})
    public void tfsProductTokenSearch507427() throws Exception
    {
        //Verify Utility interface testing - encode and decode works well for  WSPN standalone Agency OnAirport RoundTrip  1 day
        //includeDetails true
        doTest(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(), "507427", CommonEnumManager.TimeDuration.Days3);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION , "507430"})
    public void tfsProductTokenSearch507430() throws Exception
    {
        //Verify Utility interface testing - encode and decode works well for  WSPN standalone Agency OnAirport RoundTrip  1 day
        //includeDetails true
        doTest(CommonScenarios.MicronNexus_FR_GDSP_Package_nonFRLocation_OnAirport.getTestScenario(), "507430", CommonEnumManager.TimeDuration.Days3);
    }

    public void doTest(TestScenario scenarios, String tuid, CommonEnumManager.TimeDuration timeDuration) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomGuid);
        //send search Request
        final CarbsRequestGenerator requestGernerator = ExecutionHelper.executeSearch(testData, spooferTransport,
                DatasourceHelper.getCarInventoryDatasource(), logger);
        final CarProductType selectCar = requestGernerator.getCarProduct(requestGernerator.getSearchResponseType(), testData);
        VerifyProductTokenSearch.carInventoryKeyCompareVerifier(randomGuid, selectCar, httpClient);
    }
}
