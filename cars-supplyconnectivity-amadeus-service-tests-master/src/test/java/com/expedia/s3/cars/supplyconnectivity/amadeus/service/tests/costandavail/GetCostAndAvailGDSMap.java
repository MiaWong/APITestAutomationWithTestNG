package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail;

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
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities.CostAndAvailExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.utilities.CostAndAvailVerificationHelper;
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
 * Created by miawang on 08/17/2017.
 */
public class GetCostAndAvailGDSMap extends SuiteContext
{
    /**
     * ASCS_Standalone_on_roundTrip_FR_EUR_ExtraHours_EP_CDCode
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182821AscsOnRoundTripEgenciaFRA() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "182821", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3extraHours);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174",
                "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG_CD_EP51354174", false);
    }
    /**
     *  [TestMethod]
     public void TFS_182825_ASCS_off_oneway_Egencia_UK()
     {
     Test_ASCS_getCostAndAvail("//ASCS_off_oneWay_LYS_NCE", 182825);
     }
     */

    /**
     * ASCS_Standalone_on_roundTrip_Spain_EUR_weekend
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182822AscsOnRoundTripEgenciaSpain() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_NCL.getTestScenario(),
                "182822", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Weekend3day);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_FRA_Standalone_RoundTrip_OffAirport_NCL_BillingCode_ZI_871131370003",
                "Amadeus_FRA_Standalone_RoundTrip_OffAirport_NCL_BillingCode_ZI_871131370003", false);
    }

    /**
     * ASCS_off_oneWay_LYS_NCE
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182825AscsOffOnewayEgenciaUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_NCE_LYS.getTestScenario(),
                "182825", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(41);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("ZI-N865556");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "EVoucher_Amadeus_FR_Agency_oneWay",
                "EVoucher_Amadeus_FR_Agency_oneWay", false);
    }

    /**
     * ASCS_Standalone_off_roundTrip_Egencia_UK_GBP_daily
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182826AscsOnRoundTripEgenciaUK() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "182826", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_Daily_249590",
                "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_Daily_249590", false);
    }

    /**
     * ASCS_Standalone_off_oneway_FR_EUR_MultipleLocation_Monthly
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182828AscsOffOnewayEgenciaFRMltiLocation() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OffAirport_NCE_MRS.getTestScenario(),
                "182828", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(39);
        specialHandleParam.setDropOffCarVendorLocationCode("T001");
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("ZI-N865556");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadues_Dynamic_daily_GB_POSU_GBP",
                "Amadues_Dynamic_daily_GB_POSU_GBP", false);
    }

    /**
     * ASCS_Standalone_on_oneWay_Spain_WithWithoutCDcode_EUR
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182829AscsOffOnewayEgenciaSpainMltiVendorWithWithoutCDcode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_OneWay_OnAirport_LHR_EDI.getTestScenario(),
                "182829", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-null");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_OneWay_OnAirport_LHREDI_WithWithoutEPCDCode_190973",
                "Amadeus_GBR_Standalone_OneWay_OnAirport_LHREDI_WithWithoutEPCDCode_190973", false);
    }

    /**
     * ASCS_Standalone_on_roundTrip_FR_OneCDcode_EUR
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182830AscsOffOnewayEgenciaFRVendorOneCDcode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "182830", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays7);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633",
                "Amadeus_GBR_Standalone_RoundTrip_OnAirport_LHR_EPCDCode_TUID_297633", false);
    }

    /**
     * ASCS_Standalone_on_oneWay_UK_MultipleCDcode_GBP
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182831AscsOffOnewayEgenciaUKMltiVendorMltiCDcode() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "182831", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        CarRate cdCode = new CarRate();
        cdCode.setCdCode("EP-51354174,EP-40823215");
        parameters.setCarRate(cdCode);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadues_Dynamic_daily_GB_POSU_GBP",
                "Amadues_Dynamic_daily_GB_POSU_GBP", false);
    }

    /**
     * ASCS_Standalone_on_roundTrip_Egencia_UK_GBP_weekly_currencyConvert
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182832AscsOnRoundTripUKWeeklyCurrencyConvert() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_PAR.getTestScenario(),
                "182832", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays5);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG",
                "Amadeus_GBR_Standalone_Roundtrip_OnAirport_CurrencyConvert_CDG", false);
    }

    /**
     * ASCS_Standalone_on_oneway_Egencia_UK_GBP_3days_currencyConvert
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs182833AscsOnOnewayUK3daysCurrencyConvert() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_OneWay_OnAirport_BCN_VCE.getTestScenario(),
                "182833", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Days3);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(40);
        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters, "Amadeus_FRA_Standalone_OneWay_OnAirport_NCELYS_SpecialEquipment_TUID_207164",
                "Amadeus_FRA_Standalone_OneWay_OnAirport_NCELYS_SpecialEquipment_TUID_207164", false);
    }

    private void amadeusSCSGetCostAndAvailGDSMsgMapping(TestData parameters,String searchTemplateOverrideName,
                                                        String costAndAvailTemplateOverrideName, boolean isSupportHandleWarningCase)
            throws Exception

    {
        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", searchTemplateOverrideName).build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //GetDetails and verifiers
        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName",
                costAndAvailTemplateOverrideName).build(), parameters.getGuid());
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                CostAndAvailExecutionHelper.getCostAndAvailability(parameters, ExecutionHelper.createSCSRequestGenerator(searchVerificationInput));

        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getCostAndAvailabilityVerificationInput.getResponse())));
        if (isSupportHandleWarningCase)
        {
            if (null == getCostAndAvailabilityVerificationInput.getResponse().getErrorCollection())
            {
                Assert.fail("There is no Error return in getCostAndAvailability response.");
            } else
            {
                CommonVerificationHelper.supportHandleWarningVerification(
                        getCostAndAvailabilityVerificationInput.getResponse().getErrorCollection().getUnclassifiedErrorList(),
                        getCostAndAvailabilityVerificationInput.getResponse().getErrorCollection(),
                        getCostAndAvailabilityVerificationInput.getResponse().getCarProductList(), false,
                        CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT, logger);
            }
        } else
        {
            CostAndAvailVerificationHelper.getCostAndAvailBasicVerification(getCostAndAvailabilityVerificationInput,
                    parameters.getScenarios(), parameters.getGuid(), logger);
            CostAndAvailVerificationHelper.getCostAndAvailRequestGDSMsgMappingVerification(getCostAndAvailabilityVerificationInput,
                    spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);
            CostAndAvailVerificationHelper.getCostAndAvailResponseGDSMsgMappingVerification(getCostAndAvailabilityVerificationInput,
                    spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);
        }
    }

    /**
     * Amadeus_FRA_Standalone_RoundTrip_OnAirport
     * user story 499064 [Amadeus SCS] Properly handle warnings returned in all messages
     * 528736	Verify GetCostAndAvaill successfully with error returned in SCS response when errorCategory=WEC returned in ACAQ response
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs528736AirplusAmadeusCarWithDBIData() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(),
                "528736", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        amadeusSCSGetCostAndAvailGDSMsgMapping(parameters,"UserStory499064_ProperlyHandleWarningsInAllMessage_CostAndAvail",
                "UserStory499064_ProperlyHandleWarningsInAllMessage_CostAndAvail", true);
    }
}