package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification.VerifyMediaDataRetrieve;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import static com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper.checkConfigRetrieveDataFromGDS;

/**
 * Created by yyang4 on 5/8/2018.
 */
@SuppressWarnings("PMD")
public class MediaDataRetrieve extends SuiteCommon{

    private SpooferTransport spooferTransport;

    //CASSS-9720 Remove"or similar" for car models when the model is guaranteed
    //there is no ACRISS info return for retrieve response no matter the feature is on or off
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataRetrieveFeatureOn9720201() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature off
        //CarModelGuaranteedBoolean=true
        final TestScenario scenario = CommonScenarios.TiSCS_FRA_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doRetrieve(scenario,"9720201","0","1","0","1", true, "ZCS52L");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataRetrieveFeatureOn9720202() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature on
        //BS clientConfig Search.returnACRISSCode/enable feature off
        //CarModelGuaranteedBoolean=false
        final TestScenario scenario = CommonScenarios.TiSCS_FRA_Standalone_OneWay_OnAirport_CDG.getTestScenario();
        doRetrieve(scenario,"9720202","0","1","0","1", false,"ZCS52L");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void casssMediaDataRetrieveFeatureOn9720203() throws Exception {
        //BS clientConfig PopulateDynamicMediaDataFromGDS/enable feature off
        final TestScenario scenario = CommonScenarios.TisSCS_FRA_Package_Roundtrip_OnAirport_CDG.getTestScenario();
        doRetrieve(scenario,"9720203","0","0","1","1", true, "0Q7XRN");
    }

    @SuppressWarnings("CPD-START")
    private void doRetrieve(TestScenario scenarios, String tuid, String bsClientMediaConfigValue, String bsClientGDSConfigValue,String bsClientACRISSConfigValue, String bsClientDynamicConfigValue, boolean guaranteeFlag, String clientCode) throws Exception{
        spooferTransport = new SpooferTransport(httpClient, SettingsProvider.SPOOFER_SERVER, SettingsProvider.SPOOFER_PORT, 30000);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //if guarantee is true, then set scenarioname, it will set guarantee= true, other scenario default guarantee = false
        if(guaranteeFlag) {
            spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), guid);
        }
        final TestData testData = new TestData(httpClient, scenarios, tuid, guid);
        testData.setClientCode(clientCode);
        //set clientconfig and scs posconfig
        checkConfigRetrieveDataFromGDS(testData, bsClientMediaConfigValue, bsClientGDSConfigValue, bsClientACRISSConfigValue, bsClientDynamicConfigValue, clientCode);

        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (testData);
        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator requestGernerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(scenarios, guid, httpClient, requestGernerator, false);


        final Document spooferDoc = spooferTransport.retrieveRecords(testData.getGuid());
        final RetrieveVerificationInput retrieveVerificationInput = new RetrieveVerificationInput(requestGernerator.getRetrieveRequestType(),requestGernerator.getRetrieveResponseType());
        final BasicVerificationContext basicVerificationContext = new BasicVerificationContext(spooferDoc, testData.getGuid(), scenarios);
        System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));
        VerifyMediaDataRetrieve.verifyMediaData(retrieveVerificationInput,basicVerificationContext,bsClientGDSConfigValue,bsClientACRISSConfigValue);
        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, guid, httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());



    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs208107MediaDataFromDBInRetrieveWorldspanCar() throws Exception {
        final TestScenario scenario = CommonScenarios.Worldspan_US_Agency_Standalone_nonUSLocation_OnAirport.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "208107", PojoXmlUtil.getRandomGuid());
        testRetrieveMediaFromDB(testData);
    }

    //208120
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs208120MediaDataFromDBInRetrieveMNCar() throws Exception {
        final TestScenario scenario = CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario();
        final TestData testData = new TestData(httpClient, scenario, "208120", PojoXmlUtil.getRandomGuid());
        testRetrieveMediaFromDB(testData);
    }

    @SuppressWarnings("CPD-START")
    private void testRetrieveMediaFromDB(TestData testData) throws Exception{
        //booking with shop
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);

        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(testData.getScenarios(), testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, false);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(testData.getScenarios(), omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        verifyMediaFromDB(carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType());
    }

    public static void verifyMediaFromDB(RetrieveResponseType retrieveResponse) throws DataAccessException {
        final CarProductType rspCar =  retrieveResponse.getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation()
                .getCarProduct();
        final CarInventoryKeyType rspCarInventoryKey = rspCar.getCarInventoryKey();
        final long carCategoryID = rspCarInventoryKey.getCarCatalogKey().getCarVehicle().getCarCategoryCode();
        final long carTypeID = rspCarInventoryKey.getCarCatalogKey().getCarVehicle().getCarTypeCode();
        final long supplierID = rspCarInventoryKey.getCarCatalogKey().getVendorSupplierID();
        final String locationCode = rspCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey().getLocationCode();

        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        if ((inventoryHelper.existMediaInfoForRequestedCar(supplierID, carCategoryID, carTypeID, locationCode) && null == rspCar.getCarCatalogMakeModel())
                || (!inventoryHelper.existMediaInfoForRequestedCar(supplierID, carCategoryID, carTypeID, locationCode) && (
                null !=  rspCar.getCarCatalogMakeModel() || rspCar.getCarCatalogMakeModel().getMediaID() !=0)))
        {
            Assert.fail("Media information return error, DB record was not matched with response returned.");
        }
        else
        {
            CarCatalogMakeModelType makeModelDB = null;
            if (rspCar.getCarCatalogMakeModel().getMediaID() > 0) {
                makeModelDB = inventoryHelper.getMakeModelInfo(rspCar.getCarCatalogMakeModel().getMediaID());
            }

            if (null != makeModelDB  && (!makeModelDB.getImageFilenameString().equals(rspCar.getCarCatalogMakeModel().getImageFilenameString()) ||
                    !makeModelDB.getImageThumbnailFilenameString().equals(rspCar.getCarCatalogMakeModel().getImageThumbnailFilenameString())))
            {
                Assert.fail("Media information was wrong, what response returned was not matched with DB.");
            }
        }
    }




}
