package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMCancelReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsSearchRequestGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMCancelRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMReserveRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsOMRetrieveSender;
import com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade.CarbsRequestSender;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.CommonTestHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.ExecutionHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.CarBSGetCostAndAvailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.CarBSGetDetailVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.CarBSSearchVerifier;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.ecommerce.messages.getcostandavailability.defn.v4.CarECommerceGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsRequestType;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchRequestType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingAmount;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import expedia.om.supply.messages.defn.v1.BookingAmountType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 11/23/2016.
 */
public class Retrieve extends SuiteCommon
{
    Logger logger = Logger.getLogger(getClass());
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2125And2485VerifyRateDetailInRetrieve() throws Exception {
        final String randomGuid = ExecutionHelper.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient));
        this.carRateDetailInRetrieveVerification(randomGuid, CommonScenarios.TiSCS_GBR_Standalone_RoundTrip_OnAirport_LHR.
                getTestScenario(), "2125248509");
    }

    //    CASSS-2125    ConditionalCostList to be returned in xml format
    //    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
    private void carRateDetailInRetrieveVerification(String guid, TestScenario scenarios, String tuid) throws Exception {
        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg
                (scenarios, DatasourceHelper.getCarInventoryDatasource(), tuid, guid, httpClient, DatasourceHelper.getSpooferTransport(httpClient));
        //retrieve
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMRetrieveSender.carBSOMRetrieveSend(scenarios, guid, httpClient, carbsOMRetrieveReqAndRespGenerator, false);

        //retrieve Verification
        final RetrieveVerificationInput retrieveVerificationInput = new RetrieveVerificationInput
                (carbsOMRetrieveReqAndRespGenerator.getRetrieveRequestType(), carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType());
        retrieveVerificationInput.setCarProductType(carbsOMReserveReqAndRespGenerator.getSelectCarProduct());

        VerificationHelper.carRateDetailInRetrieveVerification(retrieveVerificationInput, DatasourceHelper.getSpooferTransport(httpClient),
                DatasourceHelper.getCarInventoryDatasource(), scenarios, guid, true, logger);

        //Cancel
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        CarbsOMCancelRequestSender.omsCancelSend(scenarios, omsCancelReqAndRespObj, guid, httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());

        // final  Document spooferDoc = spooferTransport.retrieveRecords(guid);
        // System.out.println("spooferxml" + PojoXmlUtil.toString(spooferDoc));

    }
    //US/UK/Egencia  (UK for worldapn GDSP/Merchant car, micronnexus car)     (Standalone, package) (cancel fee, share shift)

    //UK worldapn GDSP car standalone.
    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casss2075VerifyTPIDIndependentWSCSGDSPStand() throws Exception {
        final TestData param = new TestData(httpClient, CommonScenarios.Worldspan_UK_GDSP_Standalone_UKLocation_OnAirport.getTestScenario(),
                "207501", ExecutionHelper.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient)));
        this.tpidIndependence4Shopping(param, false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void casssVerifyTPIDIndependentMNSCSPkg() throws Exception {
        final TestData param = new TestData(httpClient, CommonScenarios.MN_GBR_Package_RoundTrip_OnAirport_CDG.getTestScenario(),
                "207502", ExecutionHelper.generateNewOrigGUID(DatasourceHelper.getSpooferTransport(httpClient)));
        this.tpidIndependence4Shopping(param, false);
    }

    private void tpidIndependence4Shopping(TestData param, boolean retrieveAfterCancel) throws Exception {
        //-----------------------Shopping-------------------------
        //----------------search-------------
        final CarECommerceSearchRequestType searchRequest = CarbsSearchRequestGenerator.createCarbsSearchRequest(param);

        //remove tpid from search request.
        searchRequest.getAuditLogTrackingData().setAuditLogTPID(null);

        final CarECommerceSearchResponseType searchResponse = CarbsRequestSender.getCarbsSearchResponse(param.getGuid(), httpClient, searchRequest);
        CarBSSearchVerifier.isCarbsSearchWorksVerifier(param, searchRequest, searchResponse);

        logger.warn("request xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchRequest)));
        logger.warn("response xml ==================>\n" + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(searchResponse)));


        //verify upgrade Car
        VerificationHelper.isUpgradeCarReturnVerification(param.getGuid(), param.getScenarios(), searchRequest, searchResponse, logger);

        //----------------getdetails----------------
        final CarbsRequestGenerator carbsSearchRequestGenerator = new CarbsRequestGenerator(searchRequest, searchResponse, param);
        final CarECommerceGetDetailsRequestType getDetailsRequestType = carbsSearchRequestGenerator.createCarbsDetailsRequest();

        //remove tpid from get detail request.
        getDetailsRequestType.getAuditLogTrackingData().setAuditLogTPID(null);

        final CarECommerceGetDetailsResponseType getDetailsResponseType = CarbsRequestSender.getCarbsDetailsResponse(param.getGuid(), httpClient, getDetailsRequestType);
        CarBSGetDetailVerifier.isCarbsGetDetailWorksVerifier(param.getGuid(), param.getScenarios(), getDetailsRequestType, getDetailsResponseType);

        //----------------Cost&Avail----------------
        final CarECommerceGetCostAndAvailabilityRequestType getCostAndAvailabilityRequestType = carbsSearchRequestGenerator.createCarbsCostAndAvailRequest();

        //remove tpid from get Cost&Avail request.
        getCostAndAvailabilityRequestType.getAuditLogTrackingData().setAuditLogTPID(null);

        final CarECommerceGetCostAndAvailabilityResponseType getCostAndAvailabilityResponse = CarbsRequestSender.getCarbsGetCostAndAvailabilityResponse(param.getGuid(),
                httpClient, getCostAndAvailabilityRequestType);
        CarBSGetCostAndAvailVerifier.isCarbsGetCostAndAvailWorksVerifier(param.getGuid(), param.getScenarios(),
                getCostAndAvailabilityRequestType, getCostAndAvailabilityResponse);

        //-----------------------------------Booking logic---------------------------
        //---------------------------------------------------------------------------
        //  Send a reserve(1.GetOrderProcess -> 2.Create record -> 3.Prepare purchase -> 4.Commit purchase or Rollback purchase) request and verify if no error exist in response.
        final CarbsOMReserveReqAndRespGenerator omReserveReqAndRespGenerator = new CarbsOMReserveReqAndRespGenerator(searchRequest, searchResponse);
        //for get pointOfSaleToPointOfSupplyExchangeRate from getCostAndAvailabilityResponse later
        omReserveReqAndRespGenerator.setGetCostAndAvailabilityResponseType(getCostAndAvailabilityResponse);
        omReserveReqAndRespGenerator.setSelectCarProduct(carbsSearchRequestGenerator.getSelectedCarProduct());

        CarbsOMReserveRequestSender.oMSReserveSend(omReserveReqAndRespGenerator, param);

        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(omReserveReqAndRespGenerator);
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(omReserveReqAndRespGenerator);

        if(retrieveAfterCancel){
            //retrieve call after cancel call
            CarbsOMCancelRequestSender.omsCancelSend(param.getScenarios(), omsCancelReqAndRespObj, param.getGuid(),
                    httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            CarbsOMRetrieveSender.carBSOMRetrieveSend(param.getScenarios(), param.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
        }else{
            CarbsOMRetrieveSender.carBSOMRetrieveSend(param.getScenarios(), param.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
            CarbsOMCancelRequestSender.omsCancelSend(param.getScenarios(), omsCancelReqAndRespObj, param.getGuid(),
                    httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
        }
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs895148CancelAtPrepareChangRetrieveBookingAmountListBeforeCancelEgenica() throws Exception
    {
        testBookingAmountList(CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG.getTestScenario(), "895148", false);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs895151CancelAtPrepareChangeRetrieveBookingAmountListAfterCancelEgenica() throws Exception
    {
        testBookingAmountList(CommonScenarios.Amadeus_FRA_Agency_Standalone_RoundTrip_OffAirport_CDG.getTestScenario(), "895151", true);
    }

    private static void testBookingAmountList(TestScenario scenarioName, String tuid, Boolean retrieveAfterCancel) throws Exception
    {
        final TestData testData = new TestData(httpClient, scenarioName, tuid, PojoXmlUtil.getRandomGuid());
        testData.setNeedMultiTraveler(true);
        //booking
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        final  CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        final List<String> errorMsg = new ArrayList<>();

        if(retrieveAfterCancel)
        {
            //Cancel
            CarbsOMCancelRequestSender.omsCancelSend(scenarioName, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            //retrieve
            CarbsOMRetrieveSender.carBSOMRetrieveSend(scenarioName, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
            bookingAmountListVerifier(errorMsg, carbsOMReserveReqAndRespGenerator, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
        }
        else
        {
            //retrieve
            CarbsOMRetrieveSender.carBSOMRetrieveSend(scenarioName, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
            bookingAmountListVerifier(errorMsg, carbsOMReserveReqAndRespGenerator, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
            //Cancel
            CarbsOMCancelRequestSender.omsCancelSend(scenarioName, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
        }
        //total Price verify
        totalPriceForOMSGetReservationVerifier(errorMsg, testData, carbsOMReserveReqAndRespGenerator, carbsOMRetrieveReqAndRespGenerator);

        // Verify if the secondary driver in retrieve respone is equal to the same node in reserve request.
        compareTravellerList(carbsOMReserveReqAndRespGenerator, carbsOMRetrieveReqAndRespGenerator, errorMsg);

        if(CollectionUtils.isNotEmpty(errorMsg))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    private static void compareTravellerList(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, List<String> errorMsg)
    {
        final List<TravelerType> retrieveTravelerTypeList = carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getTravelerList().getTraveler();

        final List<TravelerType> preparePurchaseTravelerTypeList = carbsOMReserveReqAndRespGenerator.getPreparePurchaseRequestType().getConfiguredProductData().getCarOfferData().getCarReservation()
                .getTravelerList().getTraveler();
        final StringBuilder errMsg = new StringBuilder("");
        CompareUtil.compareObject(preparePurchaseTravelerTypeList, retrieveTravelerTypeList, null, errMsg, true);
        if(StringUtil.isNotBlank(errMsg.toString()))
        {
            errorMsg.add(errMsg.toString() + "\n");
        }
    }

    private static void  totalPriceForOMSGetReservationVerifier(List<String> errorMsg, TestData testData, CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator)
    {
        final CarProductType retrieveCarProduct = carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList().getBookedItem().get(0).getItemData()
                .getCarOfferData().getCarReservation().getCarProduct();

        final CarProductType selectCarProduct = carbsOMReserveReqAndRespGenerator.getSelectCarProduct();

        final CarProductType costAndAvailCarProduct = carbsOMReserveReqAndRespGenerator.getGetCostAndAvailabilityResponseType().getCarProductList().getCarProduct().get(0);

        final CarProductType preparePurchaseCarProduct = carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList()
                .getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        TotalPriceVerifier.verifyTotalPriceEqual(selectCarProduct,retrieveCarProduct, testData.getScenarios().getSupplierCurrencyCode(), errorMsg, false);
        TotalPriceVerifier.verifyTotalPriceEqual(costAndAvailCarProduct,retrieveCarProduct, testData.getScenarios().getSupplierCurrencyCode(), errorMsg, false);
        TotalPriceVerifier.verifyTotalPriceEqual(preparePurchaseCarProduct,retrieveCarProduct, testData.getScenarios().getSupplierCurrencyCode(), errorMsg, false);
    }

    private static void bookingAmountListVerifier(List<String> errorMsg, CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, boolean cancelBollean) throws DataAccessException
    {
        //Get actual BookingAmount from DB
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        final List<BookingAmount> bookingAmountFromDB = carsBookingHelper.getBookingAmountList(BookingVerificationUtils.
                getBookingItemID(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType()), cancelBollean);
        final BookingAmountType bookingAmount = carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList().getBookedItem().get(0)
                .getOrderBookedItemInformation().getBookingAmountList().getBookingAmount().get(0);

        if(CollectionUtils.isEmpty(bookingAmountFromDB))
        {
            errorMsg.add("BookingAmountList from DB  is empty.\n");
        }

        compareAmountList(errorMsg, bookingAmountFromDB, bookingAmount);
    }

    private static void compareAmountList(List<String> errorMsg, List<BookingAmount> bookingAmountFromDB, BookingAmountType bookingAmount)
    {

            boolean matched = false;
            for (final BookingAmount dbBookAmount : bookingAmountFromDB)
            {
                if (bookingAmount.getBookingAmountID().equals(dbBookAmount.getBookingAmountRowGUID()) && bookingAmount.isIsCancel() == (dbBookAmount.getCancelBool().equals("1"))
                        && new BigDecimal(bookingAmount.getCost().getAmount().getSimpleAmount()).equals(dbBookAmount.getTransactionAmtCost())
                        && new BigDecimal(bookingAmount.getPrice().getAmount().getSimpleAmount()).equals(dbBookAmount.getTransactionAmtPrice()))
                {
                    matched = true;
                    break;
                }
            }
            if (!matched)
            {
                errorMsg.add("BookingAmount with BookingAmountID "+ bookingAmount.getBookingAmountID() +" is not from BookingAmount table\n");
            }

    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs188683OMSGetReservationMNStandaloneGDSPRetrieveAfterCancel() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario();
        retrieveResponseVerify(testScenario, "188683", "Standalone_OnAirport_NonUK_NoBaseRate", true);
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    public void tfs188672OMSGetReservationMNStandaloneGDSPRetrieveBeforeCancel() throws Exception {
        final TestScenario testScenario = CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario();
        retrieveResponseVerify(testScenario, "188672", "Standalone_OnAirport_NonUK", false);
    }

    private static void retrieveResponseVerify(TestScenario testScenario, String tuid, String scenarioName, boolean retrieveAfterCancel) throws Exception {
        final SpooferTransport spooferTransport = DatasourceHelper.getSpooferTransport(httpClient);
        final String guid = ExecutionHelper.generateNewOrigGUID(spooferTransport);
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().withOverride("ScenarioName", scenarioName).build(), guid);
        final TestData testData = new TestData(httpClient, testScenario, tuid, guid);
        testData.setClientCode(CommonTestHelper.getClientCode(CarCommonEnumManager.ClientID.ClientID_7));
        //shopping and booking flow
        final CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator = CarbsOMReserveRequestSender.oMSReserveSendWithShopMsg(testData);
        final CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator = new CarbsOMRetrieveReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);
        final CarbsOMCancelReqAndRespGenerator omsCancelReqAndRespObj = new CarbsOMCancelReqAndRespGenerator(carbsOMReserveReqAndRespGenerator);

        if (retrieveAfterCancel) {
            CarbsOMCancelRequestSender.omsCancelSend(testScenario, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
            CarbsOMRetrieveSender.carBSOMRetrieveSend(testScenario, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
        } else {
            CarbsOMRetrieveSender.carBSOMRetrieveSend(testScenario, testData.getGuid(), httpClient, carbsOMRetrieveReqAndRespGenerator, retrieveAfterCancel);
            CarbsOMCancelRequestSender.omsCancelSend(testScenario, omsCancelReqAndRespObj, testData.getGuid(), httpClient, CarCommonEnumManager.OMCancelMessageType.CommitPrepareChange.toString());
        }
    }

}