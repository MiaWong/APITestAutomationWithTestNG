package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail.verification.VerifyMediaDataCostAndAvail;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper.checkConfigRetrieveDataFromGDS;

/**
 * Created by yyang4 on 5/8/2018.
 */
@SuppressWarnings("PMD")
public class MediaDataGetCostAndAvail extends SuiteCommon{
    private SpooferTransport spooferTransport;

    //CASSS-9720 Remove"or similar" for car models when the model is guaranteed
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetCostAndAvailFeatureOn9720201() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature on
        //CarModelGuaranteedBoolean=true
        final TestScenario scenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doGetCostAndAvail(scenario,"9720201", "0","1","1","1", true, "S7JWZD");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetCostAndAvailFeatureOn9720202() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature off
        //CarModelGuaranteedBoolean=false
        final TestScenario scenario = CommonScenarios.TiSCS_FRA_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doGetCostAndAvail(scenario,"9720202", "0","1","0","1", false, "ZCS52L");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetCostAndAvailFeatureOn9720203() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature off
        final TestScenario scenario = CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario();
        doGetCostAndAvail(scenario,"9720203", "0","0","1", "1", true, "0Q7XRN");
    }

    @SuppressWarnings("CPD-START")
    private void doGetCostAndAvail(TestScenario scenarios, String tuid, String bsClientMediaConfigValue, String bsClientGDSConfigValue,String bsClientACRISSConfigValue, String bsClientDynamicConfigValue , boolean guaranteeFlag, String clientCode) throws Exception{
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //if guarantee is true, then set scenarioname, it will set guarantee= true, other scenario default guarantee = false
        if(guaranteeFlag) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.setClientCode(clientCode);
        testData.setForceDownstream(true);
        //set clientconfig and scs posconfig
        checkConfigRetrieveDataFromGDS(testData, bsClientMediaConfigValue, bsClientGDSConfigValue, bsClientACRISSConfigValue, bsClientDynamicConfigValue, clientCode);
        final CarbsRequestGenerator requestGernerator = ExecutionHelper.getCostAndAvailability(testData,
                spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);


        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = new GetCostAndAvailabilityVerificationInput(
                requestGernerator.getGetCostAndAvailabilityRequestType(), requestGernerator.getGetCostAndAvailabilityResponseType());
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), scenarios);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        VerifyMediaDataCostAndAvail.verifyMediaData(getCostAndAvailabilityVerificationInput,basicVerificationContext,bsClientGDSConfigValue,bsClientACRISSConfigValue);
    }



}
