package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.basic.VerifyGetDetailsBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail.GetDetailsRetrySearchVerification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by fehu on 4/5/2017.
 * TFS 210436
 */
public class RetrySearchForGetDetails extends SuiteCommon {

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void retrySearch() throws IOException, DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        retrySearchForInvalidateCarRateQualifierCode(CommonScenarios.MN_GBR_Standalone_Oneway_OnAirport_AGP.getTestScenario(), "210436");
    }

    private void retrySearchForInvalidateCarRateQualifierCode(TestScenario scenario, String tuid) throws IOException, DataAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
       //search
        BasicRequestActions requestActions = new BasicRequestActions();
        TestData testData = new TestData(httpClient, scenario, tuid, PojoXmlUtil.getRandomGuid());
        SCSRequestGenerator requestGenerator = requestActions.search(testData);

        //getDetail
        SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        String getDetailsGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
        detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarRate().setCarRateQualifierCode("INV");
        GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, getDetailsGuid);

        //verify is that GDS contains VAR and VRR
        verify(testData, spooferTransport, getDetailsGuid, getDetailsVerificationInput);
    }

    private void verify(TestData testData, SpooferTransport spooferTransport, String guid, GetDetailsVerificationInput getDetailsVerificationInput) throws IOException {
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(guid), guid, testData.getScenarios());

        final VerifyGetDetailsBasic verifier = new VerifyGetDetailsBasic();
        final IVerification.VerificationResult result = verifier.verify(getDetailsVerificationInput, verificationContext);
        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }
        GetDetailsRetrySearchVerification verification = new GetDetailsRetrySearchVerification();
        final IVerification.VerificationResult verificationResult = verification.verify(getDetailsVerificationInput, verificationContext);
        if (!verificationResult.isPassed())
        {
            Assert.fail(result.toString());
        }
    }
}
