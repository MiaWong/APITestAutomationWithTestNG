package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.bvt;


import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.CancelHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.CostAndAvailHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.GetDetailsHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.ReserveHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.SearchHelper;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.suitecommon.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.ExecutionHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Created by jiyu on 8/11/16.
 */
public class EndToEnd extends SuiteContext
{
    //==========================================================================================================
    //  coommon cert test
    //==========================================================================================================
    private void testE2ECertTest(TestScenario scenarios,
                                 String tuid,
                                 String guid,
                                 boolean isRequiredSpecialEquipment,
                                 long vendorSupplierID,
                                 long carCategoryCode,
                                 long carTypeCode,
                                 long carTransmissionDriveCode,
                                 long carFuelACCode) throws  Exception
    {
        final SearchVerificationInput searchVerificationInput = SearchHelper.search(httpClient, scenarios, tuid, guid, -1L, vendorSupplierID, carCategoryCode, carTypeCode, carTransmissionDriveCode, carFuelACCode,carSCSDatasource);
        final SCSRequestGenerator requestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);
        String newguid = null;
        newguid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CostAndAvailHelper.getCostAndAvailability(httpClient, requestGenerator, newguid);
        newguid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        GetDetailsHelper.getDetails(httpClient, requestGenerator, newguid);
        newguid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        final ReserveVerificationInput reserveVerificationInput = ReserveHelper.reserve(httpClient, requestGenerator, newguid, isRequiredSpecialEquipment);
        ReserveHelper.reserveVerify(reserveVerificationInput, spooferTransport, scenarios, newguid, isRequiredSpecialEquipment, null, true);
        newguid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        CancelHelper.cancel(httpClient, requestGenerator, newguid, isRequiredSpecialEquipment);
    }

    private void testE2ECertTest(TestScenario scenarios,
                                 String tuid,
                                 String guid,
                                 boolean isRequiredSpecialEquipment) throws  Exception
    {
        testE2ECertTest(scenarios, tuid, guid, isRequiredSpecialEquipment, 0L, 0L, 0L, 0L, 0L);
    }


    //==========================================================================================================
    //  E2E cert test cases
    //==========================================================================================================
    @Test(groups = {TestGroup.SHOPPING_REGRESSION_LIVE}, priority = 1, description = "Spoofer : Cert Test 2", enabled = true)
    public void casss1896E2ECertTest2() throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
    /*  final String randomGuid = UUID.randomUUID().toString();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put(TAG_SCENARIO_NAME, CommonScenarios.FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP.getTestScenario().getScenarionName());
        overrides.put(TAG_STORE_TRABSACTION, STATE_BOOLEAN_TRUE);
        // you can put any variable here like pickupdate, drop off date and have logic in xslt accordingly
        spooferTransport.setOverrides(overrides, randomGuid);
    */  testE2ECertTest(CommonScenarios.FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP.getTestScenario(), "1083451", randomGuid, true);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION_LIVE}, priority = 1, description = "Spoofer : Cert Test 3", enabled = true)
    public void casss1896E2ECertTest3() throws Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
    /*  final String randomGuid = UUID.randomUUID().toString();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put(TAG_SCENARIO_NAME, CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario().getScenarionName());
        overrides.put(TAG_STORE_TRABSACTION, STATE_BOOLEAN_TRUE);
        // you can put any variable here like pickupdate, drop off date and have logic in xslt accordingly
        spooferTransport.setOverrides(overrides, randomGuid);
    */
        testE2ECertTest(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(), "1083452", randomGuid, false);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION_LIVE}, priority = 1, description = "OnLive : Cert Test 2 ", enabled = true)
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896E2ECertTest2Param(@Optional("15") long vendorSupplierID,
                                      @Optional("10") long carCategoryCode,
                                      @Optional("4") long carTypeCode,
                                      @Optional("1") long carTransmissionDriveCode,
                                      @Optional("1") long carFuelACCode) throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
    /*  final Map<String, String> overrides = new HashMap<>();
        overrides.put(TAG_SCENARIO_NAME, CommonScenarios.FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP.getTestScenario().getScenarionName());
        overrides.put(TAG_STORE_TRABSACTION, STATE_BOOLEAN_TRUE);
        // you can put any variable here like pickupdate, drop off date and have logic in xslt accordingly
        spooferTransport.setOverrides(overrides, randomGuid);
    */
        testE2ECertTest(CommonScenarios.FRA_Standalone_OneWay_OnAirport_CERTTEST2_SPECIALEQUIP.getTestScenario(), "1083451", randomGuid, true,
                vendorSupplierID, carCategoryCode, carTypeCode, carTransmissionDriveCode, carFuelACCode);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION_LIVE}, priority = 1, description = "OnLive : Cert Test 3", enabled = true)
    @Parameters({"vendorSupplierID", "carCategoryCode", "carTypeCode", "carTransmissionDriveCode", "carFuelACCode"})
    public void casss1896E2ECertTest3Param(@Optional("15") long vendorSupplierID,
                                      @Optional("10") long carCategoryCode,
                                      @Optional("4") long carTypeCode,
                                      @Optional("1") long carTransmissionDriveCode,
                                      @Optional("1") long carFuelACCode) throws  Exception
    {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
    /*  final String randomGuid = UUID.randomUUID().toString();
        final Map<String, String> overrides = new HashMap<>();
        overrides.put(TAG_SCENARIO_NAME, CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario().getScenarionName());
        overrides.put(TAG_STORE_TRABSACTION, STATE_BOOLEAN_TRUE);
        // you can put any variable here like pickupdate, drop off date and have logic in xslt accordingly
        spooferTransport.setOverrides(overrides, randomGuid);
    */
        testE2ECertTest(CommonScenarios.GBR_Standalone_OneWay_OnAirport_CERTTEST3.getTestScenario(), "1083452", randomGuid, false,
                vendorSupplierID, carCategoryCode, carTypeCode, carTransmissionDriveCode, carFuelACCode);
    }

}

