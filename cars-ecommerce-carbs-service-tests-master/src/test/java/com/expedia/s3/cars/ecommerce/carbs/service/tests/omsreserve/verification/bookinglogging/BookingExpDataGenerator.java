package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.cartypes.defn.v5.CarLegacyBookingDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.Booking;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by v-mechen on 9/11/2018.
 */
public class BookingExpDataGenerator {

    public Booking getExpBooking(PreparePurchaseRequestType request, PreparePurchaseResponseType response
            ) throws DataAccessException, SQLException {
        final Booking booking = new Booking();
        final String bookingID = BookingVerificationUtils.getBookingID(response);
        booking.setBookingID(bookingID);
        fillTuidTpid(booking, request);
        final CarProductType rspCar = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getCarProduct();
        final CarProductType reqCar = request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        booking.setBookingEndDate(new Timestamp(reqCar.getCarInventoryKey().getCarDropOffDateTime().toCalendar().getTimeInMillis()));
        fillFromRequestCarLegacyBookingData(booking, request);
        booking.setWizardID(null != rspCar.getCarInventoryKey().getPackageBoolean() &&
                rspCar.getCarInventoryKey().getPackageBoolean() ? "9" :"3" );
        booking.setTrl("-1");

        return booking;
    }

    protected void fillTuidTpid(Booking booking, PreparePurchaseRequestType request) throws DataAccessException, SQLException {
        // get tuid
        final String tuid = String.valueOf(request.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getUserID());
        booking.setCreateTUID(tuid);
        booking.setUpdateTUID(tuid);
        booking.setTuid(tuid);
        //get TPID
        final String tpid = String.valueOf(request.getSiteMessageInfo().getLegacySiteKey().getTPID());
        booking.setTravelProductID(tpid);
        booking.setPartnerID(String.valueOf(request.getSiteMessageInfo().getLegacySiteKey().getEAPID()));
    }

    protected  void fillFromRequestCarLegacyBookingData(Booking booking, PreparePurchaseRequestType request)
    {
        final CarLegacyBookingDataType legacyData = request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData();
        booking.setTravelPackageTypeID(String.valueOf(legacyData.getTravelPackageID()));
        booking.setMarketingProgramID(String.valueOf(legacyData.getMarketingProgramID()));
        booking.setReferralTrackingServiceID(String.valueOf(legacyData.getReferralTrackingServiceID()));
        booking.setReferralTrackingNbr(legacyData.getReferralTrackingNumer());
        booking.setAbTestGroupID(String.valueOf(legacyData.getAbTestGroupID()));
        booking.setItineraryPurposeMask(String.valueOf(legacyData.getItineraryPurposeMask()));
        booking.setGroupAccountID(String.valueOf(legacyData.getGroupAccountID()));
        booking.setBookingDesc(legacyData.getBookingDescription());
        booking.setLangID(String.valueOf(legacyData.getAuditLogLanguageId()));
        booking.setGroupAccountDepartmentID(String.valueOf(legacyData.getGroupAccountDepartmentID()));
        booking.setAffiliateID(String.valueOf(legacyData.getAffiliateID()));
        booking.setOperatingUnitID(String.valueOf(legacyData.getOperatingUnitID()));
        booking.setAgentAssistedBool(legacyData.getAgentAssistedBoolean() ? "1" : "0");
    }


}
