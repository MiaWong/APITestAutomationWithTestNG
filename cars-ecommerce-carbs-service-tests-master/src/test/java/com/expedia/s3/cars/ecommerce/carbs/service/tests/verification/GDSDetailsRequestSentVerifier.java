package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification;

import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Created by v-mechen on 10/24/2018.
 */
public class GDSDetailsRequestSentVerifier {
    public void verifyGDSDetailsRequestSent(SpooferTransport spooferTransport, String guid) throws IOException {
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        if(spooferDoc.getElementsByTagNameNS("*", "Car_RateInformationFromAvailability").getLength() == 0
            && spooferDoc.getElementsByTagNameNS("*", "VehRateRuleRQ").getLength() == 0
                && spooferDoc.getElementsByTagNameNS("*", "OTA_VehRateRuleRQ").getLength() == 0
                && spooferDoc.getElementsByTagNameNS("*", "VehicleRulesReq").getLength() == 0)
        {
            Assert.fail("GDS getdetails request is not sent");
        }
    }

}
