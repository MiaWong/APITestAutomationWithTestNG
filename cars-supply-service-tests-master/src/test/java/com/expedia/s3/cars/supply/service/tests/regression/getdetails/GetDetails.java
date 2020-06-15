package com.expedia.s3.cars.supply.service.tests.regression.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
//import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
//import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.supply.service.common.SettingsProvider;
//import com.expedia.s3.cars.supply.service.constant.POSConfigSettingName;
import com.expedia.s3.cars.supply.service.requestgenerators.SSRequestGenerator;
import com.expedia.s3.cars.supply.service.requestsender.SSRequestSender;
import com.expedia.s3.cars.supply.service.tests.regression.getdetails.verification.VerificationHelper;
import com.expedia.s3.cars.supply.service.utils.ExecutionHelper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

//import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by v-mechen on 1/16/2017.
 */
public class GetDetails {
    private HttpClient httpClient;
    public SpooferTransport spooferTransport;
    //final private DataSource ssDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_SS_DATABASE_SERVER, SettingsProvider.DB_CAR_SS_DATABASE_NAME,
        //    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @BeforeClass(alwaysRun = true)
    public void suiteSetup() throws Exception {
        final SslContextFactory sslContextFactory = new SslContextFactory();
        httpClient = new HttpClient(sslContextFactory);
        httpClient.start();
        spooferTransport = new SpooferTransport(httpClient,
                SettingsProvider.SPOOFER_SERVER,
                SettingsProvider.SPOOFER_PORT,
                30000);
    }

    @AfterClass
    public void tearDown() throws Exception {
        httpClient.stop();
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void jira3371TitaniumEnable() throws IOException {
        testCarSSGetdetailsDynamiCarPolicy(CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(),
                "33711", true, "TSCS_EUR");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void jira3371TitaniumDisable() throws IOException {
        testCarSSGetdetailsDynamiCarPolicy(CommonScenarios.TisSCS_FRA_Standalone_Roundtrip_OnAirport_CDG.getTestScenario(),
                "33712", false, "TSCS_EUR");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void jira3371WorldspanEnableSCSNotSupport() throws IOException {
        testCarSSGetdetailsDynamiCarPolicy(CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "33713", true, "WSCS_GBP_Daily");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void jira3371TitaniumEnableGDSMissing() throws IOException {
        testCarSSGetdetailsDynamiCarPolicy(CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario(),
                "33714", true, "TSCS_EUR_NoMerchantRules");
    }



    public void testCarSSGetdetailsDynamiCarPolicy(TestScenario scenarios, String tuid, boolean featureFlag, String spoofScenarioName) {
        try {
            //Fail test case if feature flag is not expected
            //final PosConfigHelper configHelper = new PosConfigHelper(ssDatasource,null, "stt05");
            //final boolean posConfigMatched = configHelper.checkPosConfigFeatureEnable(scenarios, featureFlag,
                 //   POSConfigSettingName.GETDETAILS_USEMERCHANTRULESFROMDETAILSRESPONSE_ENABLE);
            //if(!posConfigMatched){
               // Assert.fail(POSConfigSettingName.GETDETAILS_USEMERCHANTRULESFROMDETAILSRESPONSE_ENABLE +" POS config value is not expected!" +
               // "Expected: " + featureFlag);
            //}

            //Generate a random GUID for search
            String guid = UUID.randomUUID().toString();
            //send search request
            final SSRequestGenerator requestGenerator = SSRequestSender.search(scenarios, tuid, httpClient, guid);
            //Create a GUID for GetDetails
            guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
            //Set ScenarioName override
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spoofScenarioName).build(), guid);
            //Do GetDetails
            final SSRequestGenerator ssRequestGenerator = SSRequestSender.getDetail(scenarios, tuid, httpClient, guid, requestGenerator);

            //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(ssRequestGenerator.getDetailsReq())));
            //System.out.println(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(ssRequestGenerator.getDetailsResp())));
            //Verify dynamic carPolicy
            final boolean gdsHasMerchantRules = spoofScenarioName.contains("NoMerchantRules") ? false : true;
            VerificationHelper.dynamicCarPolicyVerification(ssRequestGenerator.getDetailsResp(), spooferTransport, scenarios,
                    guid, Logger.getLogger(getClass()), gdsHasMerchantRules, featureFlag);
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        } catch (DataAccessException e) {
            Assert.fail(e.getMessage());
        }
    }
}