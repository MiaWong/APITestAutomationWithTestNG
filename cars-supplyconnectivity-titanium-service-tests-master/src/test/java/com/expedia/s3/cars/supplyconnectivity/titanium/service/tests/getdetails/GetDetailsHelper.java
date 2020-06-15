package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.GetDetailsBasicVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.GetDetailsConditionalCostListVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.GetDetailsPhoneListVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.getdetails.verification.gdsmapverification.GDSMapVerification;
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
public class GetDetailsHelper
{
    private final static String MESSAGE_GETDETAILS_VERIFICATION_PROMPT = "GetDetails Verification run: ";

    private GetDetailsHelper() {}

    //--------------- GetDetail--------------------
    public static String getDetailsVerification(GetDetailsVerificationInput getDetailsVerificationInput,
                                                SpooferTransport spooferTransport,
                                                TestScenario scenarios,
                                                String guid,
                                                Logger logger,
                                                boolean isrequiredsRetrieveRecord) throws Exception
    {
        final Document spooferTransactions = isrequiredsRetrieveRecord ? spooferTransport.retrieveRecords(guid) : null;
        final BasicVerificationContext getDetailsVerificationContext = new BasicVerificationContext(spooferTransactions, guid, scenarios);
        final GetDetailsBasicVerification verifier = new GetDetailsBasicVerification();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, getDetailsVerificationContext);

        if (logger != null) {
            logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
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

        return null;
    }

    //  SCS getDetails
    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
    }

    //  SCS getDetails with filter on cr product
    public static GetDetailsVerificationInput getDetails(HttpClient httpClient,
                                                         SCSRequestGenerator requestGenerator,
                                                         String guid,
                                                         boolean isRandomPick,
                                                         long vendorSupplierID,
                                                         long carCategoryCode,
                                                         long carTypeCode,
                                                         long carTransmissionDriveCode,
                                                         long carFuelACCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        if (!isRandomPick) {
            detailsRequest = PropertyResetHelper.setCarGetDetailsFilter(detailsRequest,
                    vendorSupplierID,
                    carCategoryCode,
                    carTypeCode,
                    carTransmissionDriveCode,
                    carFuelACCode);
        }
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);
    }


    //  details GDS map verification
    public static String detailsGDSMapVerification(GetDetailsVerificationInput verificationInput,
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
                logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //  details GDS map verification
    public static String detailsErrorMapVerification(GetDetailsVerificationInput verificationInput,
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
                logger.info(MESSAGE_GETDETAILS_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }
}
