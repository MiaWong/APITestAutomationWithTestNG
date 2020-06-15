package com.expedia.s3.cars.ecommerce.carbs.service.tests.search;

import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification.VerifyMediaDataSearch;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper.checkConfigRetrieveDataFromGDS;


/**
 * Created by yyang4 on 5/8/2018.
 */
@SuppressWarnings("PMD")
public class MediaDataSearch extends SuiteCommon{
    private SpooferTransport spooferTransport;

    //CASSS-9720 Remove"or similar" for car models when the model is guaranteed
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataSearchFeatureOn9720001() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature on
        //CarModelGuaranteedBoolean=true
        final TestScenario scenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doSearch(scenario,"9720001","0","1","1","1",true, "S7JWZD");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataSearchFeatureOn9720002() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature off
        //CarModelGuaranteedBoolean=false
        final TestScenario scenario = CommonScenarios.TiSCS_FRA_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doSearch(scenario,"9720002", "0","1","0","1", false, "ZCS52L");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataSearchFeatureOn9720003() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature off
        final TestScenario scenario = CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario();
        doSearch(scenario,"9720003","0","0","1", "1",true ,"0Q7XRN");
    }


    private void doSearch(TestScenario scenarios, String tuid, String bsClientMediaConfigValue, String bsClientGDSConfigValue,String bsClientACRISSConfigValue, String bsClientDynamicConfigValue, boolean guaranteeFlag, String clientCode) throws Exception{
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //if guarantee is true, then set scenarioname, it will set guarantee= true, other scenario default guarantee = false
        if(guaranteeFlag) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.setClientCode(clientCode);
        //set ClientConfig
        checkConfigRetrieveDataFromGDS(testData, bsClientMediaConfigValue, bsClientGDSConfigValue, bsClientACRISSConfigValue, bsClientDynamicConfigValue, clientCode);

        //send search Request
        final CarbsRequestGenerator requestGernerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);

        //search Verification
        final SearchVerificationInput searchVerificationInput = new SearchVerificationInput(requestGernerator.getSearchRequestType(),
                requestGernerator.getSearchResponseType());
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), scenarios);
        VerifyMediaDataSearch.verifyMediaData(searchVerificationInput,basicVerificationContext,bsClientGDSConfigValue,bsClientACRISSConfigValue);
    }

}
