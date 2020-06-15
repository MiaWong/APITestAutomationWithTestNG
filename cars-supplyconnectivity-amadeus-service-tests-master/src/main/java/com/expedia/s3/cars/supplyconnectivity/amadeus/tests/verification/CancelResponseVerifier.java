package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.CarCommonEnumManager;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.List;

/**
 * Created by fehu on 8/7/2016.
 */
public class CancelResponseVerifier implements IVerification {

    public static void  isCancelWorksVerifier(CarSupplyConnectivityCancelResponseType carSCSCancelResponse) {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == carSCSCancelResponse) {
            errorMsg.append("No data return in CancelResponse.");
        }
        else
        {
        if (null != carSCSCancelResponse.getErrorCollection()) {
            List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(carSCSCancelResponse.getErrorCollection(), "DescriptionRawText");
            if (!CollectionUtils.isEmpty(descriptionRawTextList )) {
                errorMsg.append("Exist error in Cancelresponse. DescriptionRawText=");
            }
            for (String  descriptionRawText : descriptionRawTextList)
            {
                errorMsg.append(descriptionRawText);
            }
        }
             if (null == carSCSCancelResponse.getCarReservation())
             {
                 errorMsg.append("No CarReservation in CancelResponse");
             }
            else if (null != carSCSCancelResponse.getCarReservation()&& null == carSCSCancelResponse.getCarReservation().getBookingStateCode()) {
                errorMsg.append("No BookingStateCode return in Cancelresponse.");
            } else if (null != carSCSCancelResponse.getCarReservation() && !carSCSCancelResponse.getCarReservation().getBookingStateCode().equals(CarCommonEnumManager.BookingStateCode.Cancelled.toString()))
            {
                errorMsg.append(String.format("Booking failed. BookingStateCode={0}.", carSCSCancelResponse.getCarReservation().getBookingStateCode()));
            }
        }
       if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
       {
           Assert.fail(errorMsg.toString());
       }

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }
}
