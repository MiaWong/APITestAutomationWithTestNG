package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.CarRate;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestScenarioSpecialHandleParam;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.CommonVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import org.apache.log4j.Priority;
import org.junit.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 12/22/2016.
 */
public class GetDetailsGDSMap extends SuiteContext
{

    /**
     * Standalone_on_roundTrip_FR_EUR_ExtraHours_EP_CDCode
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176453OnRoundTripExtraHoursEgenciaFRA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "176453", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3extraHours);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174",
                "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174", false);
    }


    /**
     * ASCS_Standalone_on_roundTrip_Spain_EUR_weekend
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176458ASCSOnRoundTripEgenciaSpain() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_NCL.getTestScenario(),
                "176458", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174",
                "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174", false);
    }

    /**
     * ASCS_Standalone_off_roundTrip_Egencia_UK_GBP_daily
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176462AscsOffRoundTripEgenciaUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN.getTestScenario(),
                "176462", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP",
                "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_Daily_249590", false);
    }

    /**
     * ASCS_Standalone_on_roundTrip_FR_OneCDcode_EUR
     *
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176466AscsOffOnewayEgenciaFRVendorOneCDcode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_MAN.getTestScenario(),
                "176466", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP",
                "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs176467AscsOffOnewayEgenciaUKMltiVendorMltiCDcode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "176467", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-40823215");
        parameters.setCarRate(cdCode);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_Dynamic_daily_GB_POSU_GBP",
                "Amadues_Dynamic_daily_GB_POSU_GBP", false);
    }

    /**
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs185113AscsOnRoundTripCarPolicyGBR() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS.getTestScenario(),
                "185113", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadeus_FRA_Standalone_OneWay_OffAirport_NCE_LYS_3days",
                "Amadeus_FRA_Standalone_OneWay_OffAirport_NCE_LYS_3days", false);
    }

    /**
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs185112AscsOnOnewayCarPolicyGBR() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "185112", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3);

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_Dynamic_daily_GB_POSU_GBP",
                "Amadues_Dynamic_daily_GB_POSU_GBP", false);
    }

    /**
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs401448AscsGetDetailCostListSpecialEquipFee() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS.getTestScenario(),
                "401448", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_GB_POSU_GBP",
                "Amadeus_FRA_Standalone_OneWay_OnAirport_LoyatalNumber_SpecialEquipment_TUID_308985", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs435035tfs455516GetDetailExcessLiabilityAgencyPhoneNumber() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "435035", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_GetDetails_Excess_Liability",
                "Amadues_GetDetails_Excess_Liability", false);
    }

    private void amadeusSCSGetDetailsGDSMsgMapping(TestData parameters, String searchTemplateOverrideName,
                                                   String getDetailsTemplateOverrideName, boolean isSupportHandleWarningCase)
            throws Exception
    {
        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", searchTemplateOverrideName).build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        //logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //GetDetails and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                getDetailsTemplateOverrideName).build(), parameters.getGuid());
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsExecutionHelper.getDetails(parameters,
                ExecutionHelper.createSCSRequestGenerator(searchVerificationInput));

        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));
        if (isSupportHandleWarningCase)
        {
            if (null == getDetailsVerificationInput.getResponse().getErrorCollection())
            {
                Assert.fail("There is no Error return in GetDetails response.");
            } else
            {
                CommonVerificationHelper.supportHandleWarningVerification(
                        getDetailsVerificationInput.getResponse().getErrorCollection().getUnclassifiedErrorList(),
                        getDetailsVerificationInput.getResponse().getErrorCollection(),
                        getDetailsVerificationInput.getResponse().getCarProductList(), true,
                        CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT, logger);
            }
        } else
        {
            GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
            GetDetailsVerificationHelper.getDetailsRequestGDSMsgMappingVerification(getDetailsVerificationInput, spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);
            GetDetailsVerificationHelper.getDetailsResponseGDSMsgMappingVerification(getDetailsVerificationInput, spooferTransport, parameters.getScenarios(),
                    parameters.getGuid(), logger);
            CommonVerificationHelper.specialEquipmentCostVerification(
                    getDetailsVerificationInput.getResponse().getCarProductList().getCarProduct().get(0),
                    CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT, logger);
        }
    }

    /**
     * #region user story 499064 [Amadeus SCS] Properly handle warnings returned in all messages - created by Monica
     * //528731	Verify Getdetail successfully with error returned in SCS response when errorCategory=WEC returned in ARIA respons
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs528731AirplusAmadeusCarWithDBIData() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "528731", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        amadeusSCSGetDetailsGDSMsgMapping(parameters, "Amadues_DynamicSearch_daily_FR_POSU_EUR",
                "UserStory499064_ProperlyHandleWarningsInAllMessage_GetDetail", true);
    }
}