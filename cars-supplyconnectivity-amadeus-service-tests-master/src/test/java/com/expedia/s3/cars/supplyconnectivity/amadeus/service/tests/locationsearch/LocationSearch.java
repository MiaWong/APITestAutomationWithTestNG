package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.locationsearch;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonLocationSearchScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.LocationSearchTestScenario;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSLocationSearchRequestGenerator;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.locationsearch.utilities.LocationSearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.locationsearch.verification.VerifyASCSLocationSearch;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification.LocationSearchResponseVerifier;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;

import static com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon.httpClient;

/**
 * Created by v-mechen on 9/29/2018.
 */
public class LocationSearch extends SuiteContext {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs232979LocationSearchIATANullFilterNullNeedLocationDetails() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_ITA_IATA_NullSearchFilter_NoDetail.getTestScenario();
        amadeusSCSSearchGDSMsgMapping(scenario, "232979");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs240035LocationSearchIATADelTColFOhrTVicT() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_IATA_DEL_T_COL_F_OHR_T_VIC_T.getTestScenario();
        amadeusSCSSearchGDSMsgMapping(scenario, "240035");
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs232978LocationSearchIATADelFColFOhrFVicF() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_IATA_DEL_F_COL_F_OHR_F_VIC_F.getTestScenario();
        amadeusSCSSearchGDSMsgMapping(scenario, "232978");
    }

    //DEL-T/COL-F/OHR-F/ILD-F
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs242407LocationSearchIATADelTColFOhrFNoDetail() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_IATA_DEL_F_COL_F_OHR_F_VIC_F.getTestScenario();
        scenario.setDeliveryBoolean("true");
        scenario.setAirportVicinityBoolean(null);
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(false);
        amadeusSCSSearchGDSMsgMapping(scenario, "242407");
    }

    //DEL-T/COL-F/OHR-T/ILD-T
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs242406LocationSearchIATADelTColFOhrTDetail() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_IATA_DEL_F_COL_F_OHR_F_VIC_F.getTestScenario();
        scenario.setDeliveryBoolean("true");
        scenario.setOutOfOfficeHourBoolean("true");
        scenario.setAirportVicinityBoolean(null);
        scenario.setIncludeLocationDetails(true);
        scenario.setNullIncludeLocation(false);
        amadeusSCSSearchGDSMsgMapping(scenario, "242406");
    }


    @Test(groups = {TestGroup.SHOPPING_REGRESSION}) //DEL-T/COL-F/OHR-T, AirportVicinityBoolean=true
    public void tfs232995LocationSearchLatLongKmDelTColFOhrF() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadues_ITA_LatLong_KM_DEL_T_COL_F_OHR_F.getTestScenario();
        amadeusSCSSearchGDSMsgMapping(scenario, "232995");
    }

    //Amadeus_FRA_LatLong_MI_NoFilter
    @Test(groups = {TestGroup.SHOPPING_REGRESSION}) //DEL-T/COL-F/OHR-T, AirportVicinityBoolean=true
    public void tfs242405LocationSearchLatLongMINoFilter() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_LatLong_MI_NoFilter.getTestScenario();
        amadeusSCSSearchGDSMsgMapping(scenario, "242405");
    }

    //232994 DEL-T/COL-T/OHR-F
    @Test(groups = {TestGroup.SHOPPING_REGRESSION}) //DEL-T/COL-F/OHR-T, AirportVicinityBoolean=true
    public void tfs232994LocationSearchLatLongDelTColFOhrF() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_LatLong_MI_NoFilter.getTestScenario();
        scenario.setDeliveryBoolean("true");
        scenario.setCollectionBoolean("false");
        scenario.setOutOfOfficeHourBoolean("false");
        scenario.setNullSearchFilter(false);
        scenario.setIncludeLocationDetails(true);
        scenario.setNullIncludeLocation(false);
        amadeusSCSSearchGDSMsgMapping(scenario, "232994");
    }

    //242409 DEL-T/COL-T/OHR-T/ILD-F
    @Test(groups = {TestGroup.SHOPPING_REGRESSION}) //DEL-T/COL-F/OHR-T, AirportVicinityBoolean=true
    public void tfs242409LocationSearchLatLongDelTColTOhrTNoDetail() throws IOException, DataAccessException {
        final LocationSearchTestScenario scenario = CommonLocationSearchScenarios.Amadeus_FRA_LatLong_MI_NoFilter.getTestScenario();
        scenario.setDeliveryBoolean("true");
        scenario.setCollectionBoolean("true");
        scenario.setOutOfOfficeHourBoolean("true");
        scenario.setNullSearchFilter(false);
        scenario.setIncludeLocationDetails(false);
        scenario.setNullIncludeLocation(false);
        amadeusSCSSearchGDSMsgMapping(scenario, "242409");
    }

    private void amadeusSCSSearchGDSMsgMapping(LocationSearchTestScenario config, String tuid) throws IOException, DataAccessException {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(SettingsProvider.getSpooferTransport(httpClient));
        final SCSLocationSearchRequestGenerator requestGenerator = LocationSearchExecutionHelper.locationSearch(config, tuid, httpClient, randomGuid);

        //BVT verification
        LocationSearchResponseVerifier.verifyCarLocationReturned(requestGenerator.getRequest(), requestGenerator.getResponse());

        //Get GDS message from spoofer
        final Document spooferDoc = spooferTransport.retrieveRecords(randomGuid);
        requestGenerator.setSpooferDoc(spooferDoc);

        //Verify location gds map
        VerifyASCSLocationSearch.verifyASCSLocationSearch(requestGenerator, config);
    }
}
