package com.expedia.s3.cars.supply.service.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supply.messages.getreservation.defn.v4.CarSupplyGetReservationResponseType;
import org.testng.Assert;

import java.util.List;

/**
 * Created by yyang4 on 8/24/2016.
 */
public class GetReservationVerifier implements IVerification {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }

    public static void verifyReturn(CarSupplyGetReservationResponseType response) {
        //verifyReturn car product returned
        final StringBuilder errorMsg = new StringBuilder();
        if (null == response) {
            errorMsg.append("No data return in GetReservation response.");
        }
        if (null == response.getCarReservationList() || response.getCarReservationList().getCarReservation() == null || response.getCarReservationList().getCarReservation().isEmpty()) {
            errorMsg.append("No CarProduct return in GetReservation response.");
        }
        if (null != response.getErrorCollection()) {
            final List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(response.getErrorCollection(), "DescriptionRawText");
            if (!descriptionRawTextList.isEmpty()) {
                errorMsg.append("ErrorCollection is present in GetReservation response");
                descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
            }

        }
        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }


    }
}
