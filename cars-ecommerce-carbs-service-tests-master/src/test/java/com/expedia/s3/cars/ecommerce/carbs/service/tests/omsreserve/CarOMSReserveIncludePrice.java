package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveErrorHandlingRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.VerifyReserveIncludePrice;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import static com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport;
import static com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios.Worldspan_US_Agency_Standalone_USLocation_OnAirport;
import static com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios.Worldspan_US_Agency_Standalone_WithCC_OnAirport;

/**
 * Created by yyang4 on 1/22/2018.
 */
public class CarOMSReserveIncludePrice extends SuiteCommon {
    final public Logger logger = Logger.getLogger(OMSReserveForPriceChangeLoop.class);


    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void cassIncludePriceTestFeatureOff8346001() throws Exception {

        logger.info("Scenario 1 running begain:");
        //1.Feature off agency car
        //Verify TotalPriceWithTaxAmount is returned as Zero in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is not returned in GetOrderProcess respons
        final String tuid = "8346001";
        final TestScenario scenario = Worldspan_US_Agency_Standalone_USLocation_OnAirport.getTestScenario();
        setFeatureValue(tuid,"0");
        doTest(tuid,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario,0,null,"");

        //2.Feature on agency car
        //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is false in GetOrderProcess response
        setFeatureValue(tuid,"1");
        doTest(tuid,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario,1,false,"");


        logger.info("Scenario 2 running begain:");
        //1.Feature off CC required agency car
        //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is not returned in GetOrderProcess respons
        final String tuid2 = "8346002";
        final TestScenario scenario2 = Worldspan_US_Agency_Standalone_WithCC_OnAirport.getTestScenario();
        setFeatureValue(tuid2,"0");
        doTest(tuid2,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario2,1,null,"CCGuaranteeRequired");

        //2.Feature on CC required agency car
        //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is true in GetOrderProcess response
        setFeatureValue(tuid2,"1");
        doTest(tuid2,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario2,1,true,"CCGuaranteeRequired");


        logger.info("Scenario 3 running begain:");
        //1.Feature off GDSP car
        //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is not returned in GetOrderProcess respons
        final String tuid3 = "8346003";
        final TestScenario scenario3 = Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        setFeatureValue(tuid3,"0");
        doTest(tuid3,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario3,1,null,null);

        //2.Feature on GDSP car
        //Verify TotalPriceWithTaxAmount is returned as actual total price in GetOrderProcess response
        //Verify customerPaymentInstrumentRequiredBySupplier is true in GetOrderProcess response
        setFeatureValue(tuid3,"1");
        doTest(tuid3,CarCommonEnumManager.ClientID.ClientID_1.getValue(),scenario3,1,false,null);

    }



    public void doTest(String tuid, String clientId, TestScenario testScenario,double expectAmout,Boolean expectCustomerPaymentRequired,String overrideScenarioName) throws Exception
    {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String clientCode = carBSHelper.getClientListById(clientId) == null ? "" : carBSHelper.getClientListById(clientId).get(0).getClientCode();
        //booking
        final TestData testData = new TestData(httpClient, testScenario, tuid, randomGuid);

        if(!CompareUtil.isObjEmpty(overrideScenarioName)){
            testData.setSpecialTest("CCCard");
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", overrideScenarioName).build(), randomGuid);
        }
        final CarbsOMReserveReqAndRespGenerator generator = CarbsOMReserveErrorHandlingRequestSender.oMSReserveSendWithShopMsg(testScenario, DatasourceHelper.getCarInventoryDatasource(), randomGuid, httpClient, testData, clientCode, spooferTransport);
         //verify
        VerifyReserveIncludePrice.includePriceVerify(generator,expectAmout,expectCustomerPaymentRequired);
    }


    public void setFeatureValue(String tuid, String customerPaymentInstrumentRequiredBySupplier) throws Exception {
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.GETORDERPROCES_CUSTOMERPAYMENTINSTRUMENTREQUIREDBYSUPPLIER);
        posConfig.setSettingValue(customerPaymentInstrumentRequiredBySupplier);
        ConfigSetUtil.posConfigSet(posConfig, null, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
    }
}
