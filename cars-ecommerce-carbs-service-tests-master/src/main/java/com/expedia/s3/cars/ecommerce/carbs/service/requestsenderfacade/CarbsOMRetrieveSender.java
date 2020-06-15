package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;


import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.OmRetrieveVerifier;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import expedia.om.supply.messages.defn.v1.RetrieveRequest;
import expedia.om.supply.messages.defn.v1.RetrieveResponseType;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;

/**
 * Created by fehu on 9/5/2016.
 */
@SuppressWarnings("PMD")
public class CarbsOMRetrieveSender {

    /**
     *
     * @param scenario
     * @param guid
     * @param httpClient
     * @param carbsOMRetrieveReqAndRespGenerator
     * @param retrieveAfterCancel - true if retrieve request send after cancel booking request
     * @throws IOException
     * @throws DataAccessException
     */
    public static void carBSOMRetrieveSend(TestScenario scenario, String guid, HttpClient httpClient, CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, boolean retrieveAfterCancel) throws IOException, DataAccessException {
        RetrieveRequest retrieveRequest = carbsOMRetrieveReqAndRespGenerator.createRetrieveRequest();
        RetrieveResponseType retrieveResponseType = CarbsOMServiceSender.sendRetrieveResponse(guid, httpClient,retrieveRequest);
        OmRetrieveVerifier.isOMRetrieveWorksVerifier(guid, scenario, retrieveRequest, retrieveResponseType,retrieveAfterCancel);
        carbsOMRetrieveReqAndRespGenerator.setRetrieveRequestType(retrieveRequest);
        carbsOMRetrieveReqAndRespGenerator.setRetrieveResponseType(retrieveResponseType);
        //errorMsg = CarBSOMGetReservationVerifierForNewNamespace.isSuccessfulOMGetReservationMessageVerifier(omGetReservationResponse);
    }

    public static void carBSOMRetrieveSend(TestData testData, HttpClient httpClient, CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, boolean retrieveAfterCancel) throws IOException, DataAccessException {
        RetrieveRequest retrieveRequest = carbsOMRetrieveReqAndRespGenerator.createRetrieveRequest(testData);
        RetrieveResponseType retrieveResponseType = CarbsOMServiceSender.sendRetrieveResponse(testData.getGuid(), httpClient,retrieveRequest);
        OmRetrieveVerifier.isOMRetrieveWorksVerifier(testData.getGuid(), testData.getScenarios(), retrieveRequest, retrieveResponseType,retrieveAfterCancel);
        carbsOMRetrieveReqAndRespGenerator.setRetrieveRequestType(retrieveRequest);
        carbsOMRetrieveReqAndRespGenerator.setRetrieveResponseType(retrieveResponseType);
        //errorMsg = CarBSOMGetReservationVerifierForNewNamespace.isSuccessfulOMGetReservationMessageVerifier(omGetReservationResponse);
    }


}
