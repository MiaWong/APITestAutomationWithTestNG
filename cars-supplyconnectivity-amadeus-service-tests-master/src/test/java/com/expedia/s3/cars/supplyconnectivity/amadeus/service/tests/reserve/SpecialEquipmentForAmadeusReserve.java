package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.constant.reservedefaultvalue.ReserveDefaultValue;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.customized
        .SpecialEquipForReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by fehu on 10/17/2017.
 */
public class SpecialEquipmentForAmadeusReserve extends SuiteContext {
    final public static TestScenario scenario = CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
    final public  static PosConfigHelper posConfigHelper = new PosConfigHelper(SettingsProvider.CARAMADEUSSCSDATASOURCE, SettingsProvider.UPDATE_POSCONFIG_URL_AMADEUS);

    /*
     *Reserve.suppressSpecialEquipment/enable 1
     *Reserve.specialEquipmentWhitelist/enable 1
     *Reserve.specialEquipmentWhitelist/list  ""
     * Result : All suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void  reserveSpeEquipBothEnableWhitelistNUlLVerify() throws Exception {
        try {
            setPosconfig(posConfigHelper,"1", "1" ,"");
            specialEquipmentSuppressVerify(scenario, "10021", ReserveDefaultValue.MULTISPECIALEQUIPMENT, true);

        } catch (Exception e) {
            posConfigHelper.rollbackPosConfigList();
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();
        }

    }


    /*
      *Reserve.suppressSpecialEquipment/enable 1
      *Reserve.specialEquipmentWhitelist/enable 1
       *Reserve.specialEquipmentWhitelist/list  LeftHandControl,RightHandControl
       * Result : All suppressed except LeftHandControl,RightHandControl
      */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void  reserveSpeEquipBothEnableWhitelistNOTNULLVerify() throws Exception {

        try {
            setPosconfig(posConfigHelper,"1", "1" ,"LeftHandControl,RightHandControl");
            specialEquipmentSuppressVerify(scenario, "10022", ReserveDefaultValue.CARSPECIALEQUIPMENT_HANDCONTROL, false);

        } catch (Exception e) {
            posConfigHelper.rollbackPosConfigList();
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();
        }

    }

    /*
     *Reserve.suppressSpecialEquipment/enable 1
      *Reserve.specialEquipmentWhitelist/enable 1
      *Reserve.specialEquipmentWhitelist/list  	stR8GarBaGeValuE,tHaTmEan$N0ThinG
      * Result : All suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void  reserveSpeEquipBothEnableWhitelistNOTValidateVerify() throws Exception {
        try {
            setPosconfig(posConfigHelper,"1", "1" ,"stR8GarBaGeValuE,tHaTmEan$N0ThinG");
            specialEquipmentSuppressVerify(scenario, "10023",  ReserveDefaultValue.CARSPECIALEQUIPMENT_UNAVAILABLE, true);

        } catch (Exception e) {
            posConfigHelper.rollbackPosConfigList();
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();
        }
    }

    /*
     *Reserve.suppressSpecialEquipment/enable 0
      *Reserve.specialEquipmentWhitelist/enable 1
      *Reserve.specialEquipmentWhitelist/list  	"./=9"
      * Result : All Not suppressed
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void  reserveSpeEquipSuppressNotEnableWhitelistNOTValidateVerify() throws Exception {
        try {
            setPosconfig(posConfigHelper,"0", "1" ,"./=9");
            specialEquipmentSuppressVerify(scenario, "10024",  ReserveDefaultValue.CARSPECIALEQUIPMENT_SNOWCHAINS_SKIRACK, false);

        } catch (Exception e) {
            posConfigHelper.rollbackPosConfigList();
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();
        }
    }

    /*
    *Reserve.suppressSpecialEquipment/enable 1
     *Reserve.specialEquipmentWhitelist/enable 0
     *Reserve.specialEquipmentWhitelist/list  	""
     * Result : All  suppressed
    */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void  reserveSpeEquipWhiteNotEnableWhitelistNOTValidateVerify() throws Exception {
        try {
            setPosconfig(posConfigHelper,"1", "0" ,"");
            specialEquipmentSuppressVerify(scenario, "10025",  ReserveDefaultValue.MULTISPECIALEQUIPMENT, true);

        } catch (Exception e) {
            posConfigHelper.rollbackPosConfigList();
            throw new Exception(e);
        } finally {
            posConfigHelper.rollbackPosConfigList();
        }
    }


    private void setPosconfig(PosConfigHelper posConfigHelper, String suppressFeature, String whiteEnable, String whiteList) throws Exception {
        posConfigHelper.setFeatureEnable(scenario, suppressFeature, PosConfigSettingName.RESERVE_SUPPRESSSPECIALEQUIPMENT, false);
        posConfigHelper.setFeatureEnable(scenario, whiteEnable, PosConfigSettingName.RESERVE_SPECIALEQUIPMENTWHITELIST_ENABLE,  false);
        posConfigHelper.setFeatureEnable(scenario, whiteList, PosConfigSettingName.RESERVE_SPECIALEQUIPMENTWHITELIST,  false);
    }

    private void specialEquipmentSuppressVerify(TestScenario scenario, String tuid
            , ReserveDefaultValue reserveDefaultValue, boolean isSuppressed) throws Exception {

        final SpooferTransport spooferTransport =  new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);

        //search
        TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), PojoXmlUtil.getRandomGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(testData, SettingsProvider.CARAMADEUSSCSDATASOURCE);


        //reserve
        String reserveGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport) ;
        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
        final CarSupplyConnectivityReserveRequestType reserveRequestType = scsRequestGenerator.createReserveRequest();
        //set SpecialEquipment
        SpecialEquipForReserveVerification.setEquipment(reserveDefaultValue, reserveRequestType);
        ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequestType, reserveGuid);

        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(reserveGuid), reserveGuid, testData.getScenarios());

        reserveVerify(reserveVerificationInput, verificationContext ,isSuppressed);
        //cancel
        cancel(testData, scsRequestGenerator, reserveVerificationInput);

    }

    private void cancel(TestData testData, SCSRequestGenerator scsRequestGenerator, ReserveVerificationInput reserveVerificationInput) throws Exception {
        scsRequestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        final CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(testData, scsRequestGenerator);

        CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, testData.getScenarios(), testData.getGuid(), logger);
        scsRequestGenerator.setCancelResp(cancelVerificationInput.getResponse());
    }


    private void reserveVerify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext, boolean isSuppressed) throws DataAccessException {

        SpecialEquipForReserveVerification verification = new SpecialEquipForReserveVerification();
        final IVerification.VerificationResult verificationResult;
        if (isSuppressed) {
            verificationResult = verification.verifyAllSpeEquipSuppress(verificationContext);
        }
        else
        {
            verificationResult = verification.verify(reserveVerificationInput, verificationContext, new CarsSCSDataSource(SettingsProvider.CARAMADEUSSCSDATASOURCE), CommonEnumManager.ServieProvider.Amadeus);
        }
        if (!verificationResult.isPassed())
        {
            Assert.fail(verificationResult.toString());
        }
    }
}
