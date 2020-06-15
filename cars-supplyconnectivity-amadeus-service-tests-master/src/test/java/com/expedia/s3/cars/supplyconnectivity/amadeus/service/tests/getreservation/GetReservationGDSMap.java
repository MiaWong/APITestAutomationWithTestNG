package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.cancel.utilities.CancelVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.utilities.GetDetailsVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.utilities.GetReservationVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.utilities.ReserveVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.search.utilities.SearchVerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SuiteContext;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by miawang on 12/22/2016.
 */
public class GetReservationGDSMap extends SuiteContext
{

    /**
     * [TestCategory("Regression CarASCS"), TestMethod]
     * public void TFS_207209_testAMDSCS_GetReservation_DeliveryAndCollection_Home_GBR_OneWay()
     * {
     * testCarASCSGetReservation_forNew(TestScenarioName.Amadeus_FRA_Standalone_RoundTrip_OnAirport_HomeDelCol, "207209", deliveryAndCollectinoName: "//Home_Delivery_Collection",
     * verifyType: CarCommonEnumManager.VerifyType.DeliveryAndCollectionLocation, testConfigFileName: TestConfigFileName.AmaduesConfigs);
     * }
     *
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws DataAccessException
     * @throws IllegalAccessException
     */
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs207209tfs297637tfs401732GetReservationDeliveryAndCollectionHomeLoyaltySpecialEquip() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "207209", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Daily);

        amadeusSCSReserveGDSMsgMapping(parameters, "Amadues_DeliveryCollection_homeAddress_loyalty_specialEquip");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs207203tfs458460tfs455527GetReservationDeliveryAndCollectionPlaceIDSecondDriverPhone() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OffAirport_VLC.getTestScenario(),
                "207203", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.WeeklyDays6);

        amadeusSCSReserveGDSMsgMapping(parameters, "Amadues_DeliveryCollection_placeID_secondDriver_multiPhoneNum");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs528742tfs533969GetReservationWECWarn() throws Exception {
        TestData parameters = new TestData(httpClient,
                CommonScenarios.Amadeus_GBR_Agency_Standalone_RoundTrip_OnAirport_LAX.getTestScenario(),
                "528742", ExecutionHelper.generateNewOrigGUID(spooferTransport));

        parameters.setUseDays(CommonEnumManager.TimeDuration.Mounthly);
        amadeusSCSReserveGDSMsgMapping(parameters, "Amadues_WECWarn");
    }

    /**
     * @param parameters
     * @throws IOException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws DataAccessException
     */
    private void amadeusSCSReserveGDSMsgMapping(TestData parameters, String spooferTemplateScenarioName)
            throws Exception
    {
        //Search and basic verify
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final SearchVerificationInput searchVerificationInput = SearchExecutionHelper.search(parameters, SettingsProvider.CARAMADEUSSCSDATASOURCE);

        //logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getRequest())));
        //logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchVerificationInput.getResponse())));

        SearchVerificationHelper.searchNotEmptyVerification(searchVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        SCSRequestGenerator scsRequestGenerator = ExecutionHelper.createSCSRequestGenerator(searchVerificationInput);

       // parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
       // spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        //GetDetails and basic verify
        final GetDetailsVerificationInput getDetailsVerificationInput = GetDetailsExecutionHelper.getDetails(parameters, scsRequestGenerator);

        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getDetailsVerificationInput.getResponse())));

        GetDetailsVerificationHelper.getDetailsBasicVerification(getDetailsVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //Reserve and basic verify
        final ReserveVerificationInput reserveVerificationInput = ReserveExecutionHelper.reserve(parameters, scsRequestGenerator);

        ReserveVerificationHelper.reserveBasicVerification(reserveVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        parameters.setGuid(ExecutionHelper.generateNewOrigGUID(spooferTransport));
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", spooferTemplateScenarioName).build(), parameters.getGuid());
        final GetReservationVerificationInput getReservationVerificationInput = GetReservationExecutionHelper.retrieveReservation(parameters, scsRequestGenerator);


        logger.info("\nrequest xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getRequest())));
        logger.info("\nresponse xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationVerificationInput.getResponse())));

        GetReservationVerificationHelper.getReservationBasicVerification(getReservationVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);

        //cancel and basic verify
        if (null != reserveVerificationInput.getResponse().getCarReservation() && null != reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode())
        {
            if (reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.BOOKED.getStatusCode())
                    || reserveVerificationInput.getResponse().getCarReservation().getBookingStateCode().equals(CommonEnumManager.BookStatusCode.RESERVED.getStatusCode()))
            {
                CancelVerificationInput cancelVerificationInput = CancelExecutionHelper.cancel(parameters, scsRequestGenerator);

                CancelVerificationHelper.cancelBasicVerification(cancelVerificationInput, parameters.getScenarios(), parameters.getGuid(), logger);
            }
        }

        GetReservationVerificationHelper.getReservationRequestGDSMsgMappingVerification(getReservationVerificationInput,
                spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);
        GetReservationVerificationHelper.getReservationResponseGDSMsgMappingVerification(getReservationVerificationInput,
                spooferTransport, parameters.getScenarios(), parameters.getGuid(), logger);
    }
}