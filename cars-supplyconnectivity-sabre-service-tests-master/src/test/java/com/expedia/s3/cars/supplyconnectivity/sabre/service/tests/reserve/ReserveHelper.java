package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.reserve;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.reserve.verification.ReserveResponseVerification;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.utils.PropertyResetHelper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by vmohan on 11/24/16.
 */
public class ReserveHelper
{
    private final static String MESSAGE_RESERVE_VERIFICATION_PROMPT = "Reserve Verification run: ";

    private ReserveHelper() {}

    //---------------Reserve --------------------
    // SCS reserve verification with special equipment option
    public static String reserveVerify(ReserveVerificationInput reserveVerificationInput,
                                       SpooferTransport spooferTransport,
                                       TestScenario scenarios,
                                       String guid,
                                       boolean isRequiredSpecialEquipment,
                                       Logger logger,
                                       boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext reserveVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final ReserveResponseVerification verifier = new ReserveResponseVerification(isRequiredSpecialEquipment);
        final IVerification.VerificationResult result = verifier.verify(reserveVerificationInput, reserveVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }

        return null;
    }


    //  SCS Reserve
    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid,
                                                   boolean isRequiredSpecialEquipment) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);
        if (isRequiredSpecialEquipment) {
            PropertyResetHelper.setSpecialEquipment(reserveRequest, null);
        }
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        return reserveVerificationInput;
    }


}
