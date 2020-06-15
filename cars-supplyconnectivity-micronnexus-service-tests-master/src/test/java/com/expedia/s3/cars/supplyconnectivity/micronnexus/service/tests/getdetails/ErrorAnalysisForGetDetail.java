package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.getdetails;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.splunkaccess.RetriveSplunkData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.customized.VerifyCostAndAvailErrorAnalysisLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized.VerifyGetDetailsErrorAnalysisLogging;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.ExecutionHelper;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 8/23/2017.
 */
public class ErrorAnalysisForGetDetail extends SuiteCommon {

    @Test(groups = {"splunkRegression"})
    public void testMNErrorAnalysisOnAirport() throws NoSuchMethodException, DataAccessException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        testMNErrorAnalysis(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_CDG.getTestScenario(), CommonEnumManager.TimeDuration.WeeklyDays7, 1056993);
    }

    public void testMNErrorAnalysis(TestScenario scenario, CommonEnumManager.TimeDuration useDays, int tuid) throws IOException, InvocationTargetException, NoSuchMethodException, InstantiationException, DataAccessException, IllegalAccessException {
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String guid = PojoXmlUtil.generateNewOrigGUID(spooferTransport, "ErrorAnalysis_ErrCRS");
        final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), guid, spooferTransport);
        testData.setUseDays(useDays);
        //Search
        final SearchVerificationInput searchVerificationInput = ExecutionHelper.search(testData, spooferTransport,logger, SettingsProvider.CARMNSCSDATASOURCE);
        SCSRequestGenerator requestGenerator = new SCSRequestGenerator(searchVerificationInput.getRequest(), searchVerificationInput.getResponse());


        try
        {
            //getCostAndAvail
            final CarSupplyConnectivityGetCostAndAvailabilityRequestType costAndAvailRequest = requestGenerator.createCostAndAvailRequest();
            costAndAvailRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode("INV");
            costAndAvailRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode("INV");
            GetCostAndAvailabilityVerificationInput getCostAndAvailabilityVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, costAndAvailRequest, guid);

            //getDetail
            final CarSupplyConnectivityGetDetailsRequestType detailsRequest = requestGenerator.createDetailsRequest();
            detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().setLocationCode("INV");
            detailsRequest.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey().setLocationCode("INV");
            final GetDetailsVerificationInput getDetailsVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, detailsRequest, guid);

             //Get splunk data
            Thread.sleep(10000);
            final Date endTime = new Date();
            final String splunkQuery = "index=app  OriginatingGUID=" + guid + " LogType=ErrorAnalysis TUID=" + testData.getTuid() +
                    " earliest=" + (endTime.getTime()/1000 - 300) + " latest=" + (endTime.getTime() / 1000 + 60);
            // Splunk host address
            final String hostName = "https://splunklab6";
            // Splunk host port, default value is 8089
            final int hostPort = 8089;
            final List<Map> splunkResult = RetriveSplunkData.getSplunkDataFromSplunk(hostName, hostPort, splunkQuery);


            final Document spooferTransactions = spooferTransport.retrieveRecords(guid);
            final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransactions, guid,
                    testData.getScenarios());

            //GetDetails
            final VerifyGetDetailsErrorAnalysisLogging detailsVerifier = new VerifyGetDetailsErrorAnalysisLogging();
            detailsVerifier.verify(getDetailsVerificationInput, verificationContext, splunkResult );

            //GetCostAndAvail
            final VerifyCostAndAvailErrorAnalysisLogging costAndAvailVerifier = new VerifyCostAndAvailErrorAnalysisLogging();
            costAndAvailVerifier.verify(getCostAndAvailabilityVerificationInput, verificationContext, splunkResult );


        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage() + e.getStackTrace());
        }

    }
}
