package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.reserve;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsconfig.PosConfig;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.ConfigSetUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized.SpecialEquipForReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.utilities.SuiteContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * CASSS-6512
 * WSCS - Add ability to let certain special equipment go through even if suppression is enabled
 * Created by fehu on 6/29/2017.
 */
public class SpecialEquipmentForReserve extends SuiteContext {
    /*
     *Reserve.suppressSpecialEquipment/enable 1
     *Reserve.specialEquipmentWhitelist/enable 1
     *Reserve.specialEquipmentWhitelist/list  ""
     * Result : All suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "10021"})
    public void reserveSpeEquipBothEnableWhitelistNUlLVerify() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario();
        setPosconfig(scenario, "1", "1", "", "10021");
        specialEquipmentSuppressVerify(scenario, "10021", ReserveDefaultValue.MULTISPECIALEQUIPMENT, true);
    }


    /*
      *Reserve.suppressSpecialEquipment/enable 1
      *Reserve.specialEquipmentWhitelist/enable 1
       *Reserve.specialEquipmentWhitelist/list  LeftHandControl,RightHandControl
       * Result : All suppressed except LeftHandControl,RightHandControl
      */
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "10022"})
    public void reserveSpeEquipBothEnableWhitelistNOTNULLVerify() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_MX_Agency_Standalone_OnAirport_oneway.getTestScenario();
        setPosconfig(scenario, "1", "1", "LeftHandControl,RightHandControl", "10022");
        specialEquipmentSuppressVerify(scenario, "10022", ReserveDefaultValue.CARSPECIALEQUIPMENT_HANDCONTROL, false);
    }

    /*
     *Reserve.suppressSpecialEquipment/enable 1
      *Reserve.specialEquipmentWhitelist/enable 1
      *Reserve.specialEquipmentWhitelist/list  	stR8GarBaGeValuE,tHaTmEan$N0ThinG
      * Result : All suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "10023"})
    public void reserveSpeEquipBothEnableWhitelistNOTValidateVerify() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Package_USLocation_OnAirport.getTestScenario();
        setPosconfig(scenario, "1", "1", "stR8GarBaGeValuE,tHaTmEan$N0ThinG", "10023");
        specialEquipmentSuppressVerify(scenario, "10023", ReserveDefaultValue.CARSPECIALEQUIPMENT_UNAVAILABLE, true);
    }

    /*
     *Reserve.suppressSpecialEquipment/enable 0
      *Reserve.specialEquipmentWhitelist/enable 1
      *Reserve.specialEquipmentWhitelist/list  	"./=9"
      * Result : All Not suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "10024"})
    public void reserveSpeEquipSuppressNotEnableWhitelistNOTValidateVerify() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_CA_Agency_Standalone_MEX_OnAirport.getTestScenario();
        setPosconfig(scenario, "0", "1", "./=9", "10024");
        specialEquipmentSuppressVerify(scenario, "10024", ReserveDefaultValue.CARSPECIALEQUIPMENT_SNOWCHAINS_SKIRACK, false);
    }

    /*
    *Reserve.suppressSpecialEquipment/enable 1
     *Reserve.specialEquipmentWhitelist/enable 0
     *Reserve.specialEquipmentWhitelist/list  	""
     * Result : All  suppressed
    */
    @Test(groups = {TestGroup.BOOKING_REGRESSION, "10025"})
    public void reserveSpeEquipWhiteNotEnableWhitelistNOTValidateVerify() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_FR_Merchant_Standalone_nonFRLocation_OnAirport.getTestScenario();
        setPosconfig(scenario, "1", "0", "", "10025");
        specialEquipmentSuppressVerify(scenario, "10025", ReserveDefaultValue.MULTISPECIALEQUIPMENT, true);

    }


    private void setPosconfig(TestScenario scenario, String suppressFeature, String whiteEnable, String whiteList, String tuid) throws Exception {
        final PosConfig posConfig = new PosConfig();
        posConfig.setEnvironmentName(SettingsProvider.ENVIRONMENT_NAME);
        posConfig.setSettingName(PosConfigSettingName.RESERVE_SUPPRESSSPECIALEQUIPMENT);
        posConfig.setSettingValue(suppressFeature);
        ConfigSetUtil.posConfigSet(posConfig, scenario, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);

        posConfig.setSettingName(PosConfigSettingName.RESERVE_SPECIALEQUIPMENTWHITELIST_ENABLE);
        posConfig.setSettingValue(whiteEnable);
        ConfigSetUtil.posConfigSet(posConfig, scenario, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);

        posConfig.setSettingName(PosConfigSettingName.RESERVE_SPECIALEQUIPMENTWHITELIST);
        posConfig.setSettingValue(whiteList);
        ConfigSetUtil.posConfigSet(posConfig, scenario, httpClient, tuid, SettingsProvider.SERVICE_ADDRESS, true);
    }

    private void specialEquipmentSuppressVerify(TestScenario scenario, String tuid
            , ReserveDefaultValue reserveDefaultValue, boolean isSuppressed) throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {

        final SpooferTransport spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);

        //search
        SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARWORLDSPANSCSDATASOURCE);
        TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), PojoXmlUtil.getRandomGuid());
        CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);
        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, testData.getGuid());


        //reserve
        String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequestType = scsRequestGenerator.createReserveRequest();
        //set SpecialEquipment
        SpecialEquipForReserveVerification.setEquipment(reserveDefaultValue, reserveRequestType);
        ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequestType, reserveGuid);

        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(reserveGuid), reserveGuid, testData.getScenarios());

        reserveVerify(reserveVerificationInput, verificationContext, isSuppressed);
        //cancel
        cancel(testData, scsRequestGenerator, reserveVerificationInput);

    }

    private void cancel(TestData testData, SCSRequestGenerator scsRequestGenerator, ReserveVerificationInput reserveVerificationInput) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        final CancelVerificationInput cancelVerificationInput =
                ExecutionHelper.cancel(httpClient, scsRequestGenerator, testData.getGuid());


        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifyCancelBasic verifier = new VerifyCancelBasic();
        final IVerification.VerificationResult result = verifier.verify(cancelVerificationInput, verificationContext);
        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
        scsRequestGenerator.setCancelResp(cancelVerificationInput.getResponse());
    }


    private void reserveVerify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext, boolean isSuppressed) throws DataAccessException {

        SpecialEquipForReserveVerification verification = new SpecialEquipForReserveVerification();
        final IVerification.VerificationResult verificationResult;
        if (isSuppressed) {
            verificationResult = verification.verifyAllSpeEquipSuppress(verificationContext);
        } else {
            verificationResult = verification.verify(reserveVerificationInput, verificationContext, new CarsSCSDataSource(SettingsProvider.CARWORLDSPANSCSDATASOURCE), CommonEnumManager.ServieProvider.worldSpanSCS);
        }
        if (!verificationResult.isPassed()) {
            Assert.fail(verificationResult.toString());
        }
    }


}
