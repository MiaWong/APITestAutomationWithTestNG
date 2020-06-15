package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by aaniyath on 04-10-2016.
 */
public class ExecutionHelper
{

    private ExecutionHelper(){}

    //------- OrigGuid ----------------
    public static String generateNewOrigGUID(SpooferTransport spooferTransport) throws IOException
    {
        final String randomGuid = UUID.randomUUID().toString();
        spooferTransport.setOverrides(SpooferTransport.OverridesBuilder.newBuilder().build(), randomGuid);
        return randomGuid;
    }
    //  generator for non-SCS-Search request
    public static SCSRequestGenerator createSCSRequestGenerator(SearchVerificationInput searchVerificationInput)
    {
        return new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
    }

}
