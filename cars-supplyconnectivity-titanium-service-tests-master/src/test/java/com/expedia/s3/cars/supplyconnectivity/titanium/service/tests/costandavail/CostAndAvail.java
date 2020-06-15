package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;

/**
 * Created by jiyu on 8/22/16.
 */
public class CostAndAvail extends SuiteContext {
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void casss1896GetCostAndAvailabilityValidDriverAge() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testGetCostAndAvailability(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                "1896",
                randomGuid,
                -1L);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896GetCostAndAvailabilityValidDriverAgeTestHook(@Optional("15") long vendorSupplierID,
                                                                      @Optional("10") long carCategoryCode,
                                                                      @Optional("4") long carTypeCode,
                                                                      @Optional("1") long carTransmissionDriveCode,
                                                                      @Optional("1") long carFuelACCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, DataAccessException, Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);

        testGetCostAndAvailability(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(),
                "1896",
                randomGuid,
                -1L,
                false,
                vendorSupplierID,
                carCategoryCode,
                carTypeCode,
                carTransmissionDriveCode,
                carFuelACCode);
    }

    //  common flow for GetCostAndAvail
    private void testGetCostAndAvailability(TestScenario scenarios,
                                            String tuid,
                                            String guid,
                                            long driverAge) throws  Exception {
        testGetCostAndAvailability(scenarios, tuid, guid, driverAge, true, 0L, 0L, 0L, 0L, 0L);
    }

    private void testGetCostAndAvailability(TestScenario scenarios,
                                            String tuid,
                                            String guid,
                                            long driverAge,
                                            boolean isRandomPick,
                                            long vendorSupplierID,
                                            long carCategoryCode,
                                            long carTypeCode,
                                            long carTransmissionDriveCode,
                                            long carFuelACCode) throws  Exception {

        final SearchVerificationInput searchVerificationInput = SearchHelper.search(httpClient,
                scenarios,
                tuid,
                guid,
                driverAge, carSCSDatasource);


        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final GetCostAndAvailabilityVerificationInput costandavailVerificationInput =
                CostAndAvailHelper.getCostAndAvailability(httpClient,
                        requestGenerator,
                        newGuid,
                        isRandomPick,
                        vendorSupplierID,
                        carCategoryCode,
                        carTypeCode,
                        carTransmissionDriveCode,
                        carFuelACCode);

        CostAndAvailHelper.getCostAndAvailabilityVerification(costandavailVerificationInput,
                spooferTransport,
                scenarios,
                newGuid,
                logger,
                true);
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    //OnAirport, roundtrip, no currency conversation, weekly
    public void tfs1077271CostAvailTIMapOnairportRoundtrip() throws  Exception {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_CarModelGuaranteed").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1077271", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.WeeklyExtDays);
        testTICostAvailMap(testData, "TSCS_CarModelGuaranteed", false);
    }

    //TiSCS_GBR_Standalone_OneWay_OffAirport_CDG OffAirport, oneway, currency conversation, daily
    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077265CostAvailTIMapOffairportOneway() throws  Exception {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_EUR").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_OneWay_OffAirport_CDG.getTestScenario(),
                "1077265", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTICostAvailMap(testData, "TSCS_EUR", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1077283CostAvailTIMapMileage() throws Exception {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Set ScenarioName override
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", "TSCS_LimitMileage").build(), randomGuid);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1077283", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTICostAvailMap(testData, "TSCS_LimitMileage", false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1078187CostAvailTIErrorMap2() throws  Exception {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1078187", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTICostAvailMap(testData, "TSCS_CostAvailError2", true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION, TestGroup.REGRESSION})
    public void tfs1078203CostAvailTIErrorMap12() throws  Exception {
        //Create a GUID
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        //Create test data
        final TestData testData = new TestData(httpClient, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(),
                "1078203", randomGuid);
        testData.setUseDays(CommonEnumManager.TimeDuration.Daily);
        testTICostAvailMap(testData, "TSCS_CostAvailError12", true);
    }

    private void testTICostAvailMap(TestData testData, String spooferOverrideName, boolean errorHandling) throws  Exception {
        //Create request based on testata
        final CarSupplyConnectivitySearchRequestType request = createSearchRequest(testData);

        //Send search request
        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION, request, testData.getGuid());

        //GetCostAvail with new GUID
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        final String newGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferOverrideName).build(), newGuid);
        final GetCostAndAvailabilityVerificationInput input = CostAndAvailHelper.getCostAndAvailability(httpClient, requestGenerator, newGuid);

        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getRequest())));
        logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(input.getResponse())));

        //Verify GDS map
        if (errorHandling) {
            CostAndAvailHelper.costAvailErrorMapVerification(input, spooferTransport,
                    testData.getScenarios(), newGuid, logger);
        } else {
            //Do BVT verification
            CostAndAvailHelper.getCostAndAvailabilityVerification(input, spooferTransport, testData.getScenarios(), newGuid, logger, false);
            CostAndAvailHelper.costAvailGDSMapVerification(input, spooferTransport,
                    testData.getScenarios(), newGuid, logger);
        }

    }


}
