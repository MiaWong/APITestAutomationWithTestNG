package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.CostAndAvailResponseVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.costandavail.verification.gdsmapverification.GDSMapVerification;
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
public class CostAndAvailHelper
{
    private final static String MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT = "GetCostAndAvailability Verification run: ";

    private CostAndAvailHelper() {}
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

        return null;
    }


    //  SCS GetCostAndAvailability
    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(HttpClient httpClient,
                                                                                 SCSRequestGenerator requestGenerator,
                                                                                 String guid) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();
        return TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, costandavailRequest, guid);
    }


    //  SCS GetCostAndAvailability
    public static GetCostAndAvailabilityVerificationInput getCostAndAvailability(HttpClient httpClient,
                                                                                 SCSRequestGenerator requestGenerator,
                                                                                 String guid,
                                                                                 boolean isRandomPick,
                                                                                 long vendorSupplierID,
                                                                                 long carCategoryCode,
                                                                                 long carTypeCode,
                                                                                 long carTransmissionDriveCode,
                                                                                 long carFuelACCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CarSupplyConnectivityGetCostAndAvailabilityRequestType costandavailRequest = requestGenerator.createCostAndAvailRequest();

        if (!isRandomPick) {
            costandavailRequest = PropertyResetHelper.setCarGetCostAndAvailFilter(  costandavailRequest,
                    vendorSupplierID,
                    carCategoryCode,
                    carTypeCode,
                    carTransmissionDriveCode,
                    carFuelACCode);
        }

        final GetCostAndAvailabilityVerificationInput getCostAndAvailVerificationInput = TransportHelper.sendReceive(
                httpClient,
                SettingsProvider.SERVICE_ADDRESS,
                SettingsProvider.SERVICE_E3DESTINATION,
                costandavailRequest, guid);
        return getCostAndAvailVerificationInput;
    }

    //  costAvail GDS map verification
    public static String costAvailGDSMapVerification(GetCostAndAvailabilityVerificationInput verificationInput,
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
                logger.info(MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //  costAvail GDS error map verification
    public static String costAvailErrorMapVerification(GetCostAndAvailabilityVerificationInput verificationInput,
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
                logger.info(MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }




}
