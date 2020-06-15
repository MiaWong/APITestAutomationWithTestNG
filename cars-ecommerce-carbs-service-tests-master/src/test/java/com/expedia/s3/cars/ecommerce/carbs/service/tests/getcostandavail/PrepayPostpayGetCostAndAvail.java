package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.RequestDefaultValues;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import java.io.IOException;


public class PrepayPostpayGetCostAndAvail extends SuiteCommon {
    final Logger logger = Logger.getLogger(getClass());


    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161743CarBSGDSPPrePay() throws Exception
    {
        this.carPrepayPostpayInCostAndAvailVerification(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway.getTestScenario(), "2125248509", "ExtraHourPrice", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161745CarBSMerchantPrePay() throws Exception
    {
        this.carPrepayPostpayInCostAndAvailVerification(CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario(), "2125248510", "MerchantWeekly", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161748CarBSAgencyCCGuarantee() throws Exception
    {
        this.carPrepayPostpayInCostAndAvailVerification(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(), "2125248511", "USAgencyStandaloneLatLong", true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs162268CarBSAgencyNoCCGuarantee() throws Exception
    {
        this.carPrepayPostpayInCostAndAvailVerification(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario(), "2125248512", "USAgencyStandaloneLatLong", false);
    }

    private void carPrepayPostpayInCostAndAvailVerification(TestScenario scenarios, String tuid, String spooferScenario, boolean needSupplierID) throws IOException, DataAccessException
    {

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenario).build(), guid);
        // create test data and set TestScenarioSpecialHandleParam
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, DatasourceHelper.getCarInventoryDatasource(), needSupplierID, RequestDefaultValues.VENDOR_CODE_ZT);
        //send search + GetCostAndAvailability Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.getCostAndAvailabilityByBusinessModelIDAndServiceProviderID(testData,
                spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);

        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput
                (requestGenerator.getGetCostAndAvailabilityRequestType(), requestGenerator.getGetCostAndAvailabilityResponseType());
        VerificationHelper.prepayPostpayInResponseVerification(getCostAndAvailabilityVerificationInput.getRequest().getCarProductList().getCarProduct(),
                scenarios, guid, logger, DatasourceHelper.getCarInventoryDatasource());
    }


}
