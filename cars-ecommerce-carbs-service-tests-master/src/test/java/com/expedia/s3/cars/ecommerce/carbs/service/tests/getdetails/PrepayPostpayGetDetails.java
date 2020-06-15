package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
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

public class PrepayPostpayGetDetails extends SuiteCommon {
    final Logger logger = Logger.getLogger(getClass());

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161755CarBSAgencyCCGuarantee() throws Exception
    {
        this.carPrepayPostpayInGetDetailsVerification(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario(),"2125248504","USAgencyStandaloneLatLong",  true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs162269CarBSAgencyNoCCGuarantee() throws Exception
    {
        this.carPrepayPostpayInGetDetailsVerification(CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OffAirport.getTestScenario(), "2125248555", "USAgencyStandaloneLatLong", false);
    }



    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161754CarBSMerchantPrePay() throws Exception
    {
        this.carPrepayPostpayInGetDetailsVerification(CommonScenarios.Worldspan_UK_Merchant_Standalone_nonUKLocation_OnAirport.getTestScenario(), "2125248506", "MerchantWeekly", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs161752CarBSGDSPPrePay() throws Exception
    {
        this.carPrepayPostpayInGetDetailsVerification(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport_Oneway.getTestScenario(), "2125248507", "ExtraHourPrice", false);

    }

    private void carPrepayPostpayInGetDetailsVerification(TestScenario scenarios, String tuid, String spooferScenario, boolean needSupplierID) throws IOException, DataAccessException
    {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomguid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferScenario).build(), randomguid);
        final TestData testData = new TestData(httpClient, scenarios, tuid, randomguid);
        // set TestScenarioSpecialHandleParam of test data
        ExecutionHelper.setTestScenarioSpecialHandleParamOfTestData(testData, DatasourceHelper.getCarInventoryDatasource(), needSupplierID, RequestDefaultValues.VENDOR_CODE_ZT);

        //send search + getDetail Request
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearchAndGetDetailByBusinessModelIDAndServiceProviderID(testData,
                spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);

        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput
                (requestGenerator.getGetDetailsRequestType(), requestGenerator.getGetDetailsResponseType());
        VerificationHelper.prepayPostpayInResponseVerification(getDetailsVerificationInput.getRequest().getCarProductList().getCarProduct(),
                scenarios, randomguid, logger, DatasourceHelper.getCarInventoryDatasource());
    }
}