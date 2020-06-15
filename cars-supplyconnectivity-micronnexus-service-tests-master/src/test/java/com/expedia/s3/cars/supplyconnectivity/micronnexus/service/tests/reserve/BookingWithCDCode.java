package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.reserve;

import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
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
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic.VerifySearchResponseNotEmpty;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.BasicRequestActions;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * 1. Test Plan for User Story 123794 Egencia - Support multiple vendors and CD codes in Search Criteria with validation
 * 2. Just no harm testing for CD code
 * 3. Add verification that CD code in not sent to VAR request *
 * Created by fehu on 4/14/2017.
 */
public class BookingWithCDCode extends SuiteCommon{

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //137819 - Verify FR site behaviour is not broken when send booking request to MNSCS with CD code.
    public void TFS_137819_MNBookingSucceedWithSingleCDCode_FR() throws DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, TransformerConfigurationException {
        verifyBookingSucceed(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), "A411700", "137819");
    }

    @Test(groups = {TestGroup.BOOKING_REGRESSION})
    //137827 - Verify UK site behaviour is not broken when send booking request to MNSCS with CD code.
    public void TFS_137827_MNBookingSucceedWithSingleCDCode_UK() throws DataAccessException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, TransformerConfigurationException {
        verifyBookingSucceed(CommonScenarios.MN_UK_GDSP_Standalone_nonUKLocation_OnAirport.getTestScenario(), "A411700", "137827");
    }


    private void verifyBookingSucceed(TestScenario scenario, String cdCode, String tuid) throws DataAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, TransformerConfigurationException {
        //search
        SpooferTransport spooferTransport  =  SettingsProvider.getSpooferTransport(httpClient);
        SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), PojoXmlUtil.generateNewOrigGUID(spooferTransport));
        CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);
        //set CDCode
        if(null == searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarRate())
        {
            CarRateType carRateType = new CarRateType();
            carRateType.setCarRateQualifierCode(cdCode);
            searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).setCarRate(carRateType);
        }
        else
        {
            searchRequestType.getCarSearchCriteriaList().getCarSearchCriteria().get(0).getCarRate().setCarRateQualifierCode(cdCode);
        }

        SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, testData.getGuid());        BasicRequestActions requestActions = new BasicRequestActions();
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(testData.getGuid()), testData.getGuid(), testData.getScenarios());
        // verify is that cdCode be sent to VAR request
        verify(cdCode, searchVerificationInput, verificationContext);


        SCSRequestGenerator scsRequestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());
        requestActions.reserve(scsRequestGenerator, httpClient, testData);
        requestActions.cancel(scsRequestGenerator, httpClient,testData);
    }

    private void verify(String cdCode, SearchVerificationInput searchVerificationInput , BasicVerificationContext verificationContext) throws TransformerConfigurationException {

        final VerifySearchResponseNotEmpty verifier = new VerifySearchResponseNotEmpty();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, verificationContext);

        if (!result.isPassed())
        {
            Assert.fail(result.toString());
        }

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            t.transform(new DOMSource(verificationContext.getSpooferTransactions().getElementsByTagName("Request").item(0)), new StreamResult(bos));
            String xmlStr = bos.toString();
            if (xmlStr.contains(cdCode)) {
                Assert.fail("cdCode should not be send to VAR request");
            }
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

}
