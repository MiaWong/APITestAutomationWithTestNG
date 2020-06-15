package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.CancelVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.cancel.basic.VerifyCancelBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.basic.VerifyGetReservationBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.basic.VerifyReserveBasic;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic.VerifySearchResponseNotEmpty;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail.GetCostAndAvailCarRateDetailVerification;
import org.apache.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.testng.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

//import org.testng.annotations.AfterClass;
//import org.testng.annotations.BeforeClass;

/**
 * Created by v-mechen on 8/17/2016.
 */


public class BasicRequestActions
{
    Logger logger = Logger.getLogger(getClass());


    public SCSRequestGenerator search(TestData testData) throws IOException, DataAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        //  Create Search Request
        final CarSupplyConnectivitySearchRequestType searchRequest = SearchRequestGenerator.createSearchRequest(testData.getScenarios(), testData.getTuid());

        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(testData.getHttpClient(),
                SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequest, testData.getGuid());

        final BasicVerificationContext searchVerificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifySearchResponseNotEmpty verifier = new VerifySearchResponseNotEmpty();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, searchVerificationContext);

        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }

        final SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchRequest, searchVerificationInput.getResponse());


        return requestGenerator;
    }


    public void getDetail(SCSRequestGenerator requestGenerator, HttpClient httpClient,
                          TestData testData) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        final GetDetailsVerificationInput getDetailsVerificationInput =
                ExecutionHelper.getDetail(httpClient, requestGenerator, testData.getGuid());

        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifyGetDetailsBasic verifier = new VerifyGetDetailsBasic();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        requestGenerator.setDetailsResp(getDetailsVerificationInput.getResponse());

    }

    public void getCostAndAvail(SCSRequestGenerator requestGenerator, HttpClient httpClient,
                                TestData testData) throws Exception {
        //  Create CostAvail request
        final GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput =
                ExecutionHelper.getCostAndAvail(httpClient, requestGenerator, testData.getGuid());


        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final GetCostAndAvailCarRateDetailVerification verifier = new GetCostAndAvailCarRateDetailVerification();
        final IVerification.VerificationResult result = verifier.verify(getCostAndAvailabilityVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        requestGenerator.setCostAndAvailResp(getCostAndAvailabilityVerificationInput.getResponse());

    }

    public void reserve(SCSRequestGenerator requestGenerator, HttpClient httpClient,
                        TestData testData) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final ReserveVerificationInput reserveVerificationInput =
                ExecutionHelper.reserve(httpClient, requestGenerator, testData.getGuid());

        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifyReserveBasic verifier = new VerifyReserveBasic();
        final IVerification.VerificationResult result = verifier.verify(reserveVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        requestGenerator.setReserveResp(reserveVerificationInput.getResponse());
    }

    public void getReservation(SCSRequestGenerator requestGenerator, HttpClient httpClient,
                               TestData testData) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final GetReservationVerificationInput getReservationVerificationInput =
                ExecutionHelper.getReservation(httpClient, requestGenerator, testData.getGuid());

        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifyGetReservationBasic verifier = new VerifyGetReservationBasic();
        final IVerification.VerificationResult result = verifier.verify(getReservationVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        requestGenerator.setGetReservationResp(getReservationVerificationInput.getResponse());


    }


    public void cancel(SCSRequestGenerator requestGenerator, HttpClient httpClient, TestData testData) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        final CancelVerificationInput cancelVerificationInput =
                ExecutionHelper.cancel(httpClient, requestGenerator, testData.getGuid());


        final BasicVerificationContext verificationContext = new BasicVerificationContext(null, testData.getGuid(), testData.getScenarios());

        final VerifyCancelBasic verifier = new VerifyCancelBasic();
        final IVerification.VerificationResult result = verifier.verify(cancelVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        requestGenerator.setCancelResp(cancelVerificationInput.getResponse());

    }
}
