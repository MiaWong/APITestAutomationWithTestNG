package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import java.util.List;

/**
 * Created by fehu on 8/5/2016.
 */
public class GetReservationVerifier implements IVerification {
    public static void  isGetReservationWorksVerifier(CarSupplyConnectivityGetReservationResponseType carSCSGetReservationResponse)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == carSCSGetReservationResponse)
        {
            errorMsg.append("No data return in GetReservationResponse.");
        }
        else {
            if (null == carSCSGetReservationResponse.getCarReservationList() || CollectionUtils.isEmpty(carSCSGetReservationResponse.getCarReservationList().getCarReservation()))
            {
                errorMsg.append("No CarReservation return in GetReservationResponse.");
            }

            if (null != carSCSGetReservationResponse.getErrorCollection()) {
                List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(carSCSGetReservationResponse.getErrorCollection(), "DescriptionRawText");
                if (!CollectionUtils.isEmpty(descriptionRawTextList)) {
                    errorMsg.append("Exist error in GetReservationResponse. DescriptionRawText=");
                }
                for(String descriptionRawText : descriptionRawTextList)
                {
                    errorMsg.append(descriptionRawText);
                }
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
