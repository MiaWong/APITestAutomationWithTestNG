package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification.VerifyMediaDataGetDetails;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper.checkConfigRetrieveDataFromGDS;

/**
 * Created by yyang4 on 5/8/2018.
 */

public class MediaDataGetDetails extends SuiteCommon {

    //CASSS-9720 Remove"or similar" for car models when the model is guaranteed
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetDetailsFeatureOn9720101() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //CarModelGuaranteedBoolean=true
        final TestScenario testScenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doGetDetails(testScenario, "TSCS_CarModelGuaranteed", "9720101", "0", "1", "1", "1", false, false, "S7JWZD");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetDetailsFeatureOn9720102() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //CarModelGuaranteedBoolean=false
        //BS clientConfig Search.returnACRISSCode/enable is just search feature, don't need verify for details
        final TestScenario testScenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doGetDetails(testScenario, "", "9720102", "0", "1", "0","1", false, false,"ZCS52L");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataGetDetailsFeatureOn9720103() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature off
        //BS clientConfig Search.returnACRISSCode/enable is just search feature, don't need verify for details
        final TestScenario scenario = CommonScenarios.TiSCS_GBR_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doGetDetails(scenario, "TSCS_CarModelGuaranteed", "9720103", "0", "0",  "1","1", false, false,"0Q7XRN");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfsAmadeusMediaFromGDSFeatureOnForOneway491761() throws Exception {
        //BS clientConfig PopulateMediaInfoFromGDS/enable feature on, OneWay
        //bsClientDynamicConfigValue is for titatium feature, dont need set
        //BS clientConfig Search.returnACRISSCode/enable is just search feature, don't need verify for details
        final TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS.getTestScenario();
        doGetDetails(scenario, "Amadeus_FRA_Standalone_OneWay_OnAirport_NCELYS_SpecialEquipment_TUID_207164", "491761", "1", "0",  "1","", true, false,"W0DFCJ");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfsAmadeusMediaFromGDSFeatureOnRoundTripNoImageFromGDS490678() throws Exception {
        //BS clientConfig PopulateMediaInfoFromGDS/enable feature on
        //bsClientDynamicConfigValue is for titatium feature, dont need set
        //BS clientConfig Search.returnACRISSCode/enable is just search feature, don't need verify for details
        final TestScenario scenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        doGetDetails(scenario, "Amadeus_FR_Agency_RoundTrip_NoPictureFromGDS", "490678", "1", "0",  "1","", true, true, "W0DFCJ");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfsAmadeusMediaFromGDSFeatureOffRoundTrip489705() throws Exception {
        //BS clientConfig PopulateMediaInfoFromGDS/enable feature off
        //bsClientDynamicConfigValue is for titatium feature, dont need set
        //BS clientConfig Search.returnACRISSCode/enable is just search feature, don't need verify for details
        final TestScenario scenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        doGetDetails(scenario, "Amadeus_FR_Agency_RoundTrip", "489705", "0", "0",  "1","", true, false, "0Q7XRN");
    }

    //CarCatalogMarkModel node verify
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssAmadeusMediaDataGetDetailsFeatureOn9720114() throws Exception {
        //BS clientConfig PopulateMediaInfoFromGDS/enable always keep feature on for Egencia
        final TestScenario testScenario = CommonScenarios.Amadeus_FR_Agency_Standalone_FRLocation_onAirport_roundTrip_LYS.getTestScenario();
        mediaDataForGetDetails(testScenario,  "9720102", "W0DFCJ");
    }

    private void mediaDataForGetDetails(TestScenario scenarios, String  tuid, String clientCode) throws Exception {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.setClientCode(clientCode);
        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = requestGenerator.createCarbsDetailsRequest();
        final CarCatalogMakeModelType expectCarCatalogMakeModelType = getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarCatalogMakeModel();
        //just send a CarproductToken down stream.
        buildgetDetailsRequest(getDetailsRequestType);
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
        requestGenerator.setGetDetailsResponseType(getDetailsResponseType);
        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);
        final StringBuilder errorMsg = new StringBuilder();
        if(!CompareUtil.compareObject(expectCarCatalogMakeModelType, getDetailsResponseType.getCarProductList().getCarProduct().get(0).getCarCatalogMakeModel(), null, errorMsg, true))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    private void buildgetDetailsRequest(CarECommerceGetDetailsRequestType getDetailsRequestType)
    {
        final String carProductToken = getDetailsRequestType.getCarProductList().getCarProduct().get(0).getCarProductToken();
        final CarProductType carProductType = new CarProductType();
        carProductType.setCarProductToken(carProductToken);
        final List<CarProductType> carProductTypeList = new ArrayList<>();
        carProductTypeList.add(carProductType);
        getDetailsRequestType.getCarProductList().setCarProduct(carProductTypeList);
    }


    private void doGetDetails(TestScenario scenarios, String scenarioName, String tuid, String bsClientMediaConfigValue, String bsClientGDSConfigValue,String bsClientACRISSConfigValue,  String bsClientDynamicConfigValue, boolean isMediaInfoScenario, boolean noImageReturned, String clientCode) throws Exception {
        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //if scenario name is not null, override the scenario name
        if (scenarioName != null) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);

        testData.setClientCode(clientCode);
        if (isMediaInfoScenario) {
            testData.setForceDownstream(true);
        }
        //set ClientConfig
        checkConfigRetrieveDataFromGDS(testData, bsClientMediaConfigValue, bsClientGDSConfigValue, bsClientACRISSConfigValue, bsClientDynamicConfigValue, clientCode);

        final CarbsRequestGenerator requestGenerator = ExecutionHelper.executeSearch(testData, spooferTransport, DatasourceHelper.getCarInventoryDatasource(), logger);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = requestGenerator.createCarbsDetailsRequest();
        if (isMediaInfoScenario) {
            VerifyMediaDataGetDetails.verifyMediaInfoForSearch(requestGenerator.getSearchResponseType(), noImageReturned, bsClientMediaConfigValue);
            getDetailsRequestType.getCarProductList().getCarProduct().get(0).setCarCatalogMakeModel(null);
        }
        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(testData.getGuid(), testData.getHttpClient(), getDetailsRequestType);
        final GetDetailsVerificationInput getDetailsVerificationInput = new GetDetailsVerificationInput(getDetailsRequestType, getDetailsResponseType);
        requestGenerator.setGetDetailsResponseType(getDetailsResponseType);
        VerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, spooferTransport, testData.getScenarios(), testData.getGuid(), false, logger);
        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), scenarios);

        //Verification for media info or mediaData depends on scenario
        if (isMediaInfoScenario) {
            VerifyMediaDataGetDetails.verifyMediaInfoForGetDetails(requestGenerator, basicVerificationContext, bsClientMediaConfigValue);
        } else {
            VerifyMediaDataGetDetails.verifyMediaData(getDetailsVerificationInput, basicVerificationContext, bsClientGDSConfigValue);
        }
    }
}
