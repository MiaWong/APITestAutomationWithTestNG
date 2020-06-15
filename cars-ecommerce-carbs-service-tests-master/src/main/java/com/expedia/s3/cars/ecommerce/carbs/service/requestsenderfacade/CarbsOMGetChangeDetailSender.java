package com.expedia.s3.cars.ecommerce.carbs.service.requestsenderfacade;

import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMGetChangeDetailReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omgetchangedetail.OmGetChangeDetailVerifier;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailRequestType;
import com.expedia.s3.cars.messages.getchangedetail.defn.v1.GetChangeDetailResponseType;
import org.eclipse.jetty.client.HttpClient;

import java.io.IOException;

/**
 * Created by fehu on 9/5/2016.
 */
public class CarbsOMGetChangeDetailSender {

    private CarbsOMGetChangeDetailSender() {
    }

    public static void carBSOMGetChangeDetailSend(TestScenario scenario, String guid, HttpClient httpClient, CarbsOMGetChangeDetailReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, boolean withTPID) throws IOException, DataAccessException {
        final  GetChangeDetailRequestType getChangeDetailRequestType = carbsOMRetrieveReqAndRespGenerator.createGetChangeDetailRequest(withTPID) ;
        final  GetChangeDetailResponseType getChangeDetailResponseType = CarbsOMServiceSender.sendGetChangeDetailResponse(guid, httpClient, getChangeDetailRequestType);
        OmGetChangeDetailVerifier.isOMGetChangeDetailsWorksVerifier(guid, scenario, getChangeDetailRequestType, getChangeDetailResponseType);
        carbsOMRetrieveReqAndRespGenerator.setGetChangeDetailResponseType(getChangeDetailResponseType);
        //errorMsg = CarBSOMGetReservationVerifierForNewNamespace.isSuccessfulOMGetReservationMessageVerifier(omGetReservationResponse);

    }


}
