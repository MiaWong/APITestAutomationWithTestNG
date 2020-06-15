package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.verification.GetDetailsBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.verification.GetDetailsConditionalCostListVerification;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.getdetails.verification.GetDetailsPhoneListVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by vmohan on 24-11-2016.
 */
public class GetDetailsHelper {

    private final static String MESSAGE_GETDETAILS_VERIFICATION_PROMPT = "GetDetails Verification run: ";

    private GetDetailsHelper() {
    }

    //--------------- GetDetail--------------------

    //  SCS GetDetail
    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
    }

    //  GetDetail Verification
    public static String getDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                SpooferTransport spooferTransport,
                                                TestScenario scenarios,
                                                String guid,
                                                Logger logger,
                                                boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? (SettingsProvider.SPOOFER_ENABLE ? spooferTransport.retrieveRecords(guid) : null) : null;
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsBasicVerification verifier = new GetDetailsBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

        return null;
    }


    public static String getDetailsConditionalCostListVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                                   SpooferTransport spooferTransport,
                                                                   TestScenario scenarios,
                                                                   String guid,
                                                                   Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsConditionalCostListVerification verifier = new GetDetailsConditionalCostListVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

        return null;
    }

    public static String getDetailsPhoneListVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                         SpooferTransport spooferTransport,
                                                         TestScenario scenarios,
                                                         String guid,
                                                         Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsPhoneListVerification verifier = new GetDetailsPhoneListVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

        return null;
    }
}
