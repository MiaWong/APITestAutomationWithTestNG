package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search;

/**
 * Created by miawang on 1/11/2017.
 */
public class SearchCarAvailabilityOptimization {


    //Todo do it after all GDS mapping case finished.

    /*
        public void TFS_292591_Search_Optimization_Multiple_SLToIATA_Mix()
        {
            Test_ASCS_Search_Optimization_AmadeusSCSLevel("//Search_Optimization_Multiple_SL_IATA_Mix", "SLToIATA", 292591);
        }

    /**
     * The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with 2 search criteria for  one-way/roundtrip locations by SL to  IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292591SearchOptimizationMultipleSLToIATAMix() throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, DataAccessException, IllegalAccessException {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_MAD.getTestScenario(),
                "292591", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        specialHandleParam.setPickUpCarVendorLocationCode("T001");
        specialHandleParam.setSearchCriteriaCount(1);
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters);
    }

    /*
    public void TFS_292592_Search_Optimization_Single_SLToIATA_Roundtrip()
    {
        Test_ASCS_Search_Optimization_AmadeusSCSLevel("//Search_Optimization_Single_SL_IATA_Roundtrip", "SLToIATA", 292592);
    }

    /**
     * The AmadeusSCS search request results in 1 CAQ and the results in CAQ mapped successfully to the search criteria
     * in AmadeusSCS search request with single search criteria for roundtrip locations by SL to IATA
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void tfs292592_Search_Optimization_Single_SLToIATA_Roundtrip() throws IOException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, DataAccessException, IllegalAccessException {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_ITA_Agency_Standalone_RoundTrip_OffAirport_MAD.getTestScenario(),
                "282372", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        TestScenarioSpecialHandleParam specialHandleParam = new TestScenarioSpecialHandleParam();
        specialHandleParam.setVendorSupplierID(14);
        specialHandleParam.setPickUpCarVendorLocationCode("T001");
        specialHandleParam.setSearchCriteriaCount(1);
        specialHandleParam.setDropOffOnAirport(Boolean.TRUE.toString());

        parameters.setTestScenarioSpecialHandleParam(specialHandleParam);

        amadeusSCSSearchGDSMsgMapping(parameters);
    }

    /*
    [TestMethod]
        //The AmadeusSCS search request results in 2 CAQ and the results in CAQ mapped successfully to the search criteria
        //in AmadeusSCS search request with 2 search criteria for differnet roundtrip locations by SL to  IATA
        public void TFS_287844_Search_Optimization_Multiple_SLToIATA_Rountrip()
        {
            Test_ASCS_Search_Optimization_AmadeusSCSLevel("//Search_Optimization_Multiple_SL_IATA_Roundtrip", "SLToIATA", 287844);
        }

    private void amadeusSCSSearchOptimization(TestData parameters) {

    }
    */
}
