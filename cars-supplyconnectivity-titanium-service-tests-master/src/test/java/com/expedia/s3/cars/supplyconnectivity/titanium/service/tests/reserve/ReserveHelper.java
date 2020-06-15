package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve;

import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.errorhandlingverification.ErrorHandlingVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.gdsmapverification.GDSMapVerification;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.reserve.verification.ReserveResponseVerification;
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

        return null;
    }


    //  SCS Reserve
    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid,
                                                   boolean isRequiredSpecialEquipment, boolean... isRequiredAirFlight) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);
        if (isRequiredSpecialEquipment) {
            PropertyResetHelper.setSpecialEquipment(reserveRequest, null);
        }
        if(null != isRequiredAirFlight && isRequiredAirFlight.length > 0 && isRequiredAirFlight[0])
        {
            PropertyResetHelper.setAirFlight(reserveRequest);
        }
        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());

        return reserveVerificationInput;
    }

    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid,
                                                   String errorCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        final CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);

        reserveRequest.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).
                setSecondAddressLine(errorCode);
        reserveRequest.getTravelerList().getTraveler().get(0).getContactInformation().getAddressList().getAddress().get(0).setFirstAddressLine("");

        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);

        return reserveVerificationInput;
    }

    //  SCS Reserve with filter set on SpecialEquipment and CarProduct filter
    public static ReserveVerificationInput reserve(HttpClient httpClient,
                                                   SCSRequestGenerator requestGenerator,
                                                   String guid,
                                                   boolean isRequiredSpecialEquipment,
                                                   boolean isRandomPick,
                                                   long vendorSupplierID,
                                                   long carCategoryCode,
                                                   long carTypeCode,
                                                   long carTransmissionDriveCode,
                                                   long carFuelACCode) throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CarSupplyConnectivityReserveRequestType reserveRequest = requestGenerator.createReserveRequest();
        requestGenerator.setReserveReq(reserveRequest);

        //  check if required special equippment
        if (isRequiredSpecialEquipment) {
            PropertyResetHelper.setSpecialEquipment(reserveRequest, null);
        }

        //  check if required specific car product filter
        if (!isRandomPick) {
            reserveRequest = PropertyResetHelper.setCarReserveFilter(reserveRequest,
                    vendorSupplierID,
                    carCategoryCode,
                    carTypeCode,
                    carTransmissionDriveCode,
                    carFuelACCode);
        }

        final ReserveVerificationInput reserveVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, reserveRequest, guid);
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
        return reserveVerificationInput;
    }

    //reserve GDS map verification
    public static String reserveGDSMapVerification(ReserveVerificationInput verificationInput,
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
                logger.info(MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }

    //reserve GDS error map verification
    public static String reserveErrorGDSMapVerification(ReserveVerificationInput verificationInput,
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
                logger.info(MESSAGE_RESERVE_VERIFICATION_PROMPT + result);
            }
            Assert.fail(result.toString());
        }

        return null;
    }



}
