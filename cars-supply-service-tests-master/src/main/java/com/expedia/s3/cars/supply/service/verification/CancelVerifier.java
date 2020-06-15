package com.expedia.s3.cars.supply.service.verification;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supply.messages.cancel.defn.v4.CarSupplyCancelResponseType;
import com.expedia.s3.cars.supply.service.common.CarCommonEnumManager;
import org.testng.Assert;

import java.util.List;

/**
 * Created by yyang4 on 8/24/2016.
 */
public class CancelVerifier implements IVerification {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }

    public static void verifyReturn(CarSupplyCancelResponseType response, TestScenario scenarios) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();
        if (null == response) {
            errorMsg.append("cancel Response is null.");
        } else if (null == response.getCarReservation()) {
            errorMsg.append("No CancelResult returns in Cancel response.");
        } else if (null == response.getCarReservation().getBookingStateCode()) {
            errorMsg.append("No BookingStateCode return in Cancel response.");
        } else if (!CarCommonEnumManager.BookingStateCode.Cancelled.toString().equals(response.getCarReservation().getBookingStateCode())) {
            errorMsg.append(String.format("Verify Cancel failed. BookingStateCode={0}.", response.getCarReservation().getBookingStateCode()));
        }

        if (null != response.getErrorCollection()) {
            final List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getErrorCollection(), "DescriptionRawText");
            if (!descriptionRawTextList.isEmpty()) {
                errorMsg.append("ErrorCollection is present in Cancel response");
                descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
            }

        }
        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }


    }
}
