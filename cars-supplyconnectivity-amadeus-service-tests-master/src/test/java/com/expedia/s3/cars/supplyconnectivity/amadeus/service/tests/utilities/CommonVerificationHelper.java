package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities;

import com.expedia.e3.data.cartypes.defn.v5.CarProductListType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.errortypes.defn.v4.UnclassifiedErrorListType;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.SpecialEquipmentCostVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.SupportHandleWarningVerification;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by miawang on 2/12/2017.
 */
public class CommonVerificationHelper
{
    public static String errorHandlingVerification(Document rspDoc, String errorMsgHeaders, String expectMessage,
                                                   Logger logger) throws IOException
    {
        final ErrorHandlingVerification errorHandlingVerification = new ErrorHandlingVerification();
        final IVerification.VerificationResult result = errorHandlingVerification.isExpectMessageVerification(rspDoc, expectMessage);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(errorMsgHeaders + result);
                //CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String specialEquipmentCostVerification(CarProductType carProduct,
                                                                            String errorMsgHeaders, Logger logger) throws IOException
    {
        final SpecialEquipmentCostVerification seCostVerifier = new SpecialEquipmentCostVerification();
        final IVerification.VerificationResult result = seCostVerifier.verify(carProduct, null);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(errorMsgHeaders + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String supportHandleWarningVerification(UnclassifiedErrorListType unclassifiedErrorList, Object errorCollection,
                                                          CarProductListType carProductList,  boolean isGetDetailsAction, String errorMsgHeaders, Logger logger) throws IOException
    {
        final SupportHandleWarningVerification supportHandleWarningVerifier = new SupportHandleWarningVerification();
        final IVerification.VerificationResult result = supportHandleWarningVerifier.isSupportHandleWarningVerification
                (unclassifiedErrorList, errorCollection, carProductList, isGetDetailsAction);

        if (!result.isPassed())
        {
            if (logger != null)
            {
                logger.info(errorMsgHeaders + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}