package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
//import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getreservation.defn.v4.CarSupplyConnectivityGetReservationRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.GetReservationResponseVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getreservation.verification.gdsmapverification.GDSMapVerification;
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
public class GetReservationHelper
{
    private final static String MESSAGE_GETRESERVATION_VERIFICATION_PROMPT = "GetReservation Verification run: ";

    private GetReservationHelper() {}
    //----------GetReservation-------------------
    public static String getreservationVerify(GetReservationVerificationInput getreservationVerificationInput,
                                              SpooferTransport spooferTransport,
                                              TestScenario scenarios,
                                              String guid,
                                              boolean isRequiredSpecialEquipment,
                                              Logger logger,
                                              boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getreservationVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetReservationResponseVerification verifier = new GetReservationResponseVerification(isRequiredSpecialEquipment);
        final IVerification.VerificationResult result = verifier.verify(getreservationVerificationInput, getreservationVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
        }

        return null;
    }



    public static GetReservationVerificationInput retrieveReservation(HttpClient httpClient,
                                                                      SCSRequestGenerator requestGenerator,
                                                                      String guid,
                                                                      boolean isRequiredSpecialEquipment) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator.createGetReservationRequest();
        //  check if required special equippment
        if (isRequiredSpecialEquipment) {
            PropertyResetHelper.setSpecialEquipment(requestGenerator, getReservationRequest);
        }
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, guid);
    }

    public static GetReservationVerificationInput retrieveReservation(HttpClient httpClient,
                                                                      SCSRequestGenerator requestGenerator,
                                                                      String guid,
                                                                      String errorCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetReservationRequestType getReservationRequest = requestGenerator.createGetReservationRequest();
        //set error code in PNR
        for (final ReferenceType reference : getReservationRequest.getCarReservationList().getCarReservation().get(0).getReferenceList().getReference())
        {
            if (reference.getReferenceCategoryCode().equals("PNR"))
            {
                reference.setReferenceCode(errorCode);
                break;
            }
        }

        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, getReservationRequest, guid);
    }

    //getReservation GDS map verification
    public static String getReservationGDSMapVerification(GetReservationVerificationInput verificationInput,
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
                logger.info(MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //getReservation error map verification
    public static String getReservationErrorMapVerification(GetReservationVerificationInput verificationInput,
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
                logger.info(MESSAGE_GETRESERVATION_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }


}
