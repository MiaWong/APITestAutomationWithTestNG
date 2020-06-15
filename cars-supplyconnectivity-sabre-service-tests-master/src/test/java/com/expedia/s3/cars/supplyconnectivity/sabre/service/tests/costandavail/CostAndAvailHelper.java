package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.costandavail.verification.CostAndAvailResponseVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by vmohan on 24-11-2016.
 */
public class CostAndAvailHelper {

    private CostAndAvailHelper() {}

    private final static String MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT = "GetCostAndAvailability Verification run: ";


    //--------------CostAndAvail-------------------
    public static String getCostAndAvailabilityVerification(GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput,
                                                            SpooferTransport spooferTransport,
                                                            TestScenario scenarios,
                                                            String guid,
                                                            Logger logger,
                                                            boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getCostAndAvailVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final CostAndAvailResponseVerification verifier = new CostAndAvailResponseVerification();
        final IVerification.VerificationResult result = verifier.verify(getCostAndAvailabilityVerificationInput, getCostAndAvailVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

        return null;
    }


    //  SCS GetCostAndAvailability
    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(HttpClient httpClient,
                                                                                 SCSRequestGenerator requestGenerator,
                                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();

        final GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput = TransportHelper.sendReceive(
                httpClient,
                SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION,
                costandavailRequest, guid);
        return getCostAndAvailVerificationInput;
    }

}
