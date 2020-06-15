package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyPackageFilterSearch;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.io.IOException;


public class AdditionalPackageFilterSearch extends SuiteCommon {
    final Logger logger = Logger.getLogger(getClass());

    /**
     * Verify Cheapest OptimizationStragetyCode filter works well for US HC package on US location - ResultSetCount=1
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs452726PackageCheapestFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_GDSP_Package_USLocation_OnAirport.getTestScenario(),"2125248601","USAgencyStandaloneLatLong", "tfs452726PackageCheapestFilter");
    }

    /**
     * Verify numeric OptimizationStragetyCode filter works well for US HC package on US location - ResultSetCount=1
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs452891PackageNumericFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_Agency_HCBundle_USLocation_OnAirport.getTestScenario(),"2125248602","USAgencyStandaloneLatLong", "tfs452891PackageNumericFilter");
    }

    /**
     * Verify Capacity OptimizationStragetyCode filter works well for US FHC package on US location - ResultSetCount=3
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs453718PackageCapacityFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_GDSP_FHCPackage_USLocation_OnAirport.getTestScenario(),"2125248603","USAgencyStandaloneLatLong", "tfs453718PackageCapacityFilter");
    }

    /**
     * Verify Margin OptimizationStragetyCode filter works well for UK FC package on non-UK location - ResultSetCount=1
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs453731PackageMarginFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_UK_GDSP_Package_nonUKLocation_OnAirport.getTestScenario(),"2125248604","USAgencyStandaloneLatLong", "tfs453731PackageMarginFilter");
    }

    /**
     * Verify Savings OptimizationStragetyCode filter works well for US HC package on non-US location - ResultSetCount=-1
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs453786PackageSavingFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_GDSP_Package_nonUSLocation_OnAirport.getTestScenario(),"2125248605","USAgencyStandaloneLatLong", "tfs453786PackageSavingFilter");
    }

    /**
     * Verify Cheapest OptimizationStragetyCode filter works well for US FHC package on US location - ResultSetCount=5 and no PassengerCount set
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs452875PackageCheapestFilter() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_UK_GDSP_FHCPackage_nonUKLocation_OnAirport.getTestScenario(),"2125248606","USAgencyStandaloneLatLong", "tfs452875PackageCheapestFilter");
    }


    /**
     *  Verify numeric OptimizationStragetyCode filter works well for US HC package on non-US location - ResultSetCount=-1
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs453696PacFilterOldCheapestNBestUS() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_GDSP_HCPackage_nonUSLocation_OnAirport.getTestScenario(),"453696","POSuCAD_Daily", "tfs453696PackageOldCheapestNbestFilter");
    }


    /**
     *453702 - Verify cheapest car filter works well for US FHC bundle on non-US location - ResultSetCount=-1 and no OptimizationStragetyCode/PassengerCount set
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs453702PacFilterOldCheapestNoOptUS() throws Exception
    {
        this.packageCheapestFilterVerification(CommonScenarios.Worldspan_US_Agency_FHCBundle_nonUSLocation_OnAirport.getTestScenario(),"453702","CheapestPriceCar", "tfs453702PackageOldCheapestNoOptFilter");
    }

    public void packageCheapestFilterVerification(TestScenario scenario, String tuid, String spooferScenario, String testName) throws IOException, DataAccessException {

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenario).build(), guid);
        final TestData testData = new TestData(httpClient, scenario, tuid, guid);
        testData.setSpecialTest(testName);
        //send search Request
        final CarbsRequestGenerator defaultRequestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);

        final SearchVerificationInput defaulSearchInput = new SearchVerificationInput(defaultRequestGenerator.getSearchRequestType(),
                defaultRequestGenerator.getSearchResponseType());

        final CarbsRequestGenerator filterRequestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        final SearchVerificationInput filterSearchInput = new SearchVerificationInput(filterRequestGenerator.getSearchRequestType(),
                filterRequestGenerator.getSearchResponseType());

        VerifyPackageFilterSearch.verifyReturnedCarAccordingFilter(filterSearchInput, defaulSearchInput.getResponse());

    }
}
