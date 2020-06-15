package com.expedia.www.cars.bugatti.service.tests.requestgenerators;

import com.expedia.www.cars.bugatti.service.tests.verification.SearchResponseVerifier;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.cars.schema.common.v1.*;
import com.expedia.cars.schema.ecommerce.shopping.v1.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by miawang on 3/21/2017.
 */
@SuppressWarnings("PMD")
public class SearchRequestGenerator {

    private static final String MESSAGE_NAME = "CarSearchRequest";

    public CarSearchRequest createSearchRequest(TestScenario scenarios, String guid) {
        final CarSearchRequest carSearchRequest = new CarSearchRequest();

        ////MessageInfo
        carSearchRequest.setMessageInfo(new MessageInfo());
        carSearchRequest.getMessageInfo().setMessageGUID(guid);
        carSearchRequest.getMessageInfo().setTransactionGUID(UUID.randomUUID().toString());
        carSearchRequest.getMessageInfo().setUserGUID(UUID.randomUUID().toString());
        carSearchRequest.getMessageInfo().setMessageName(MESSAGE_NAME);

        //MessageInfo/CreateDateTime
        DatatypeFactory dataTypeFactory;
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        GregorianCalendar gc = new GregorianCalendar();
        final Date date = new Date();
        gc.setTimeInMillis(date.getTime());
        carSearchRequest.getMessageInfo().setCreateDateTime(dataTypeFactory.newXMLGregorianCalendar(gc));

        //MessageInfo/ClientIdentityInfo
        carSearchRequest.getMessageInfo().setClientIdentityInfo(new ClientIdentityInfo());
        carSearchRequest.getMessageInfo().getClientIdentityInfo().setClientCode("1FX936");
        carSearchRequest.getMessageInfo().getClientIdentityInfo().setPreferredLocale("en_US");

        //MessageInfo/ClientIdentityInfo/PointOfSaleKey
        carSearchRequest.getMessageInfo().getClientIdentityInfo().setPointOfSaleKey(new PointOfSaleKey());
        carSearchRequest.getMessageInfo().getClientIdentityInfo().getPointOfSaleKey().setJurisdictionCountryCode(scenarios.getJurisdictionCountryCode());
        carSearchRequest.getMessageInfo().getClientIdentityInfo().getPointOfSaleKey().setCompanyCode(scenarios.getCompanyCode());
        carSearchRequest.getMessageInfo().getClientIdentityInfo().getPointOfSaleKey().setManagementUnitCode(scenarios.getManagementUnitCode());


        ////TraceInfo
        carSearchRequest.setTraceInfo(new TraceInfo());
        carSearchRequest.getTraceInfo().setSessionGUID(UUID.randomUUID().toString());
        carSearchRequest.getTraceInfo().setSendingHostname("BugattiAutomationBVTTestTestNG");
        carSearchRequest.getTraceInfo().setEnableDebugTrace(false);
        carSearchRequest.getTraceInfo().setForceLogging(true);
        carSearchRequest.getTraceInfo().setForceDownstreamTransactions(true);


        ////SearchStrategy
        carSearchRequest.setSearchStrategy(new SearchStrategy());
        carSearchRequest.getSearchStrategy().setPopulateReferencePrices(true);
        carSearchRequest.getSearchStrategy().setPopulateUpgradeMap(true);
        carSearchRequest.getSearchStrategy().setResultProductTrimStrategy("1");
        carSearchRequest.getSearchStrategy().setRequestedCurrencyCode("USD");


        ////SearchCriteriaList
        carSearchRequest.setSearchCriteriaList(new SearchCriteriaList());
        //SearchCriteriaList/SearchCriteria
        final SearchCriteria searchCriteria = new SearchCriteria();
        carSearchRequest.getSearchCriteriaList().getSearchCriteria().add(searchCriteria);
        searchCriteria.setSequence(1);

        //SearchCriteria/CarOfferContext
        searchCriteria.setCarOfferContext(new CarOfferContext());
        searchCriteria.getCarOfferContext()
                .setPointOfSaleKey(carSearchRequest.getMessageInfo().getClientIdentityInfo().getPointOfSaleKey());
        //SearchCriteria/CarOfferContext/BundlingInfo
        searchCriteria.getCarOfferContext().setBundlingInfo(new CarOfferContext.BundlingInfo());

        searchCriteria.getCarOfferContext().getBundlingInfo().setCrossSell(false);
        searchCriteria.getCarOfferContext().getBundlingInfo().setHotelPurchaseOption("1");
        searchCriteria.getCarOfferContext().getBundlingInfo().setHotelStarRating(0L);
        searchCriteria.getCarOfferContext().getBundlingInfo().setAirClassRating("0");


        //SearchCriteria/CarOfferContext/BundlingInfo/Package info
        searchCriteria.getCarOfferContext().getBundlingInfo().setPackage(!scenarios.isStandalone());
        final SearchResponseVerifier searchResponseVerifier = new SearchResponseVerifier(scenarios);
        searchCriteria.getCarOfferContext().getBundlingInfo().setProductCategoryCodeList(searchResponseVerifier.buildProductCategoryCodeListWithTestScenarios(scenarios));

        //SearchCriteria/CarOfferContext/UserEntryPointQualifiers
        searchCriteria.getCarOfferContext().setUserEntryPointQualifiers(new CarOfferContext.UserEntryPointQualifiers());
        searchCriteria.getCarOfferContext().getUserEntryPointQualifiers().setDevice("Web");
        searchCriteria.getCarOfferContext().getUserEntryPointQualifiers().setKnownUser(true);

        //SearchCriteria/CarOfferContext/UserGroupQualifiers
        searchCriteria.getCarOfferContext().setUserGroupQualifiers(new CarOfferContext.UserGroupQualifiers());
        searchCriteria.getCarOfferContext().getUserGroupQualifiers().setLoyaltyProgramList(new CarOfferContext.UserGroupQualifiers.LoyaltyProgramList());
        final LoyaltyProgram loyaltyProgram = new LoyaltyProgram();
        searchCriteria.getCarOfferContext().getUserGroupQualifiers().getLoyaltyProgramList().getLoyaltyProgram().add(loyaltyProgram);
        loyaltyProgram.setLoyaltyProviderCode("Expedia");
        loyaltyProgram.setLoyaltyProgramCode("Gold");
        loyaltyProgram.setLoyaltyProgramMembershipCode("12345");

        //SearchCriteria/CarTransportationSegment
        searchCriteria.setCarTransportationSegment(new CarTransportationSegment());

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 130);
        calendar.set(Calendar.HOUR, 12);
        gc = new GregorianCalendar();
        gc.setTime(calendar.getTime());
        searchCriteria.getCarTransportationSegment().setStartDateTime(dataTypeFactory.newXMLGregorianCalendar(gc));

        gc = new GregorianCalendar();
        calendar.add(Calendar.DATE, 3);
        gc = new GregorianCalendar();
        gc.setTime(calendar.getTime());
        searchCriteria.getCarTransportationSegment().setEndDateTime(dataTypeFactory.newXMLGregorianCalendar(gc));

        searchCriteria.getCarTransportationSegment().setPickUpAirportCode(scenarios.getPickupLocationCode());
        searchCriteria.getCarTransportationSegment().setDropOffAirportCode(scenarios.getDropOffLocationCode());

        //SearchCriteria/CarClassificationIDList
        searchCriteria.setCarClassificationIDList(new CarClassificationIDList());
        searchCriteria.getCarClassificationIDList().getCarClassificationID().add(1L);

        //SearchCriteria/RentalOptions
        searchCriteria.setRentalOptions(new com.expedia.cars.schema.ecommerce.shopping.v1.RentalOptions());
        searchCriteria.getRentalOptions().setSmoking(false);
        searchCriteria.getRentalOptions().setPrePaidFuel(false);
        searchCriteria.getRentalOptions().setUnlimitedMileage(false);

        return carSearchRequest;
    }

    public CarSearchRequest createSearchRequest(TestScenario scenarios, String guid, Long driverAgeYearCount)
    {
        final CarSearchRequest request = createSearchRequest(scenarios, guid);
        request.getSearchStrategy().setDriverAgeYearCount(driverAgeYearCount);

        return request;
    }
}