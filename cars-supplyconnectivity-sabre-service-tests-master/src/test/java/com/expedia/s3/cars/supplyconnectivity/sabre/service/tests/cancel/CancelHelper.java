package com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.cancel;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.sabre.service.tests.cancel.verification.CancelResponseVerification;
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
public class CancelHelper
{
    private final static String MESSAGE_CANCEL_VERIFICATION_PROMPT = "Cancel Verification run: ";

    private CancelHelper() {}
    //---------------Cancel----------------------
    public static String cancelVerify(CancelVerificationInput cancelVerificationInput,
                                      SpooferTransport spooferTransport,
                                      TestScenario scenarios,
                                      String guid,
                                      boolean isRequiredSpecialEquipment,
                                      Logger logger,
                                      boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext cancelVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final CancelResponseVerification verifier = new CancelResponseVerification(isRequiredSpecialEquipment);
        final IVerification.VerificationResult result = verifier.verify(cancelVerificationInput, cancelVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_CANCEL_VERIFICATION_PROMPT + result);
        }

        if (!result.isPassed()) {
            Assert.fail(result.toString());
        }
        return null;
    }


    public static CancelVerificationInput cancel(HttpClient httpClient,
                                                 SCSRequestGenerator requestGenerator,
                                                 String guid,
                                                 boolean isRequiredSpecialEquipment) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityCancelRequestType cancelRequest = requestGenerator.createCancelRequest();
        //  check if required special equippment
        if (isRequiredSpecialEquipment) {
            PropertyResetHelper.setSpecialEquipment(requestGenerator, cancelRequest);
        }
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, cancelRequest, guid);
    }


}
