package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.utilities;


import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;

import java.io.IOException;
import java.util.UUID;


/**
 * Created by miawang on 12/6/2016.
 */
public final class ExecutionHelper {
    private ExecutionHelper() {
    }

    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }

    //------- generator for non-SCS-SearchGDSMsgMapAndCarAvailOptimization request------------------
    public static SCSRequestGenerator createSCSRequestGenerator(SearchVerificationInput searchVerificationInput) {
        return new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
    }
}
