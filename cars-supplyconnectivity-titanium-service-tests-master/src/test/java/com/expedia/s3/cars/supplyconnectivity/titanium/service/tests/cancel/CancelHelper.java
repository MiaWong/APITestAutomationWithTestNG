package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.cancel.defn.v4.CarSupplyConnectivityCancelRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.CancelResponseVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.cancel.verification.gdsmapverification.GDSMapVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.utilities.PropertyResetHelper;

import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.junit.Assert;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by jiyu on 10/20/16.
 */
public class CancelHelper
{
    private final static String MESSAGE_CANCEL_VERIFICATION_PROMPT = "GetReservation Verification run: ";

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

    public static CancelVerificationInput cancel(HttpClient httpClient,
                                                 SCSRequestGenerator requestGenerator,
                                                 String guid,
                                                 String errorCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityCancelRequestType cancelRequest = requestGenerator.createCancelRequest();
        //set error code in PNR
        for (final ReferenceType reference : cancelRequest.getCarReservation().getReferenceList().getReference())
        {
            if (reference.getReferenceCategoryCode().equals("PNR"))
            {
                reference.setReferenceCode(errorCode);
                break;
            }
        }


        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, cancelRequest, guid);
    }


    //cancel GDS map verification
    public static String cancelGDSMapVerification(CancelVerificationInput verificationInput,
                                                          SpooferTransport spooferTransport,
                                                          TestScenario scenarios,
                                                          String guid,
                                                          Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        //System.out.println(PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new GDSMapVerification().verify(verificationInput, verificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_CANCEL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //cancel GDS error map verification
    public static String cancelErrorMapVerification(CancelVerificationInput verificationInput,
                                                  SpooferTransport spooferTransport,
                                                  TestScenario scenarios,
                                                  String guid,
                                                  Logger logger) throws Exception
    {
        final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
        //System.out.println(PojoXmlUtil.toString(spooferTransactions));
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);

        final IVerification.VerificationResult result = new ErrorHandlingVerification().verify(verificationInput, verificationContext);

        if (!result.isPassed()) {
            if (logger != null) {
                logger.info(MESSAGE_CANCEL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }



}
