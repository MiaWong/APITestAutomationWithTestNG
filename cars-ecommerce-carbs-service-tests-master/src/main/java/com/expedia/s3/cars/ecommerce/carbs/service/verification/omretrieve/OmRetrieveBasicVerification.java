package com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import expedia.om.supply.messages.defn.v1.BookedItemType;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by fehu on 11/10/2016.
 */
public class OmRetrieveBasicVerification implements IVerification<RetrieveVerificationInput, BasicVerificationContext> {
    private static final String MESSAGR_NO_CORRECT_IN_RESPONSE = "Send OMRetrieve message failed. response status not right in response!";
    private static final String MESSAGE_SUCCESS = "Success";
    private static final String RETREIEVE_AFTER_CANCEL = "retrieveAfterCancel";
    private static final String CANCELLED = "Cancelled";
    private static final String BOOKED = "Booked";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(RetrieveVerificationInput input, BasicVerificationContext basicVerificationContext) {
        return null;
    }

    @Override
    public VerificationResult verify(RetrieveVerificationInput retrieveVerificationInput, BasicVerificationContext verificationContext, Map<String, Object> testParameter){

        final boolean retrieveAfterCancel = (boolean)testParameter.get(RETREIEVE_AFTER_CANCEL);
        final RetrieveResponseType response = retrieveVerificationInput.getResponse();

        final BookedItemType bookedItemType = response.getBookedItemList().getBookedItem().get(0);
        final String bookingStateCode = bookedItemType.getItemData().getCarOfferData().getCarReservation().getBookingStateCode();
        if(retrieveAfterCancel){
            //booking state should be cancel and response as success
            if(!bookingStateCode.equalsIgnoreCase(CANCELLED) &&
                    response.getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)){
                return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE));
            }
        }else {
            //booking state should be booked and response as success
            if(!bookingStateCode.equalsIgnoreCase(BOOKED) &&
                    response.getResponseStatus().getStatusCodeCategory().value().equals(MESSAGE_SUCCESS)){
                return new VerificationResult(getName(), false, Arrays.asList(MESSAGR_NO_CORRECT_IN_RESPONSE));
            }
        }
        return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
    }
}
