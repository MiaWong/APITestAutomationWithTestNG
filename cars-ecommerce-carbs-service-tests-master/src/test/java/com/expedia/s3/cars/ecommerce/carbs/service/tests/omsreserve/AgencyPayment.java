package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;

import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;


import java.util.List;


@SuppressWarnings("PMD")
public class AgencyPayment  extends SuiteCommon {

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
   public void tfs383472AgencyPaymentCCGuaranteeSingleVendor() throws Exception {

        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_WithCC_OnAirport.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "CCGuaranteeRequired").build(), randomGuid);

        final TestData testData = new TestData(httpClient, scenario, "512340", randomGuid);

        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final List<CarVendor> carVendors = carsInventoryHelper.getCarVendorList("AD");

        final TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorCode("AD");
        specialHandleParam.setVendorSupplierID(Long.valueOf(carVendors.get(0).getSupplierID()));
        testData.setTestScenarioSpecialHandleParam(specialHandleParam);

        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs383522AgencyPaymentCCGuaranteeNoVendor() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_WithCC_OnAirport.getTestScenario();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "CCGuaranteeRequired").build(), randomGuid);

        final TestData testData = new TestData(httpClient, scenario, "383522", randomGuid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        testData.setNeedPaymentInstrumentToken(true);

        ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs399092NonCCRequiredCarPaymentInstrumentIDAndCCInfo() throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final TestScenario scenario = CommonScenarios.Worldspan_CA_Agency_Standalone_CALocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "399092", randomGuid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_1));
        testData.setSpecialTest("CCCard");
        testData.setNeedPaymentInstrumentToken(true);

        ExecutionHelper.carBSOMReserveAndCancelByBusinessModelIDAndServiceProviderID(testData);
    }
}
