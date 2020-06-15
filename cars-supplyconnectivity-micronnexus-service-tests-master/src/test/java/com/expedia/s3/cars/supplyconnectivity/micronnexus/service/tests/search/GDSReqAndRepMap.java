package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.tests.search;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.TestGroup;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.CommonScenarios;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.suitecommon.SuiteCommon;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VARRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.supplyconnectivity.requestgeneration.SCSSearchRequestGenerator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.transport.TransportHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.basic.VerifySearchResponseNotEmpty;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.search.mapverify.SearchMapVerification;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fehu on 1/3/2017.
 */
public class GDSReqAndRepMap extends SuiteCommon{

    // Test MicronNexus search, verify that PickupLocationCode and DropoffLocationCode is correct in MNSCS response compared with VAR message
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120964LocationCodeMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "LocationCode", 120964);
    }
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120965PickupDropoffTimeMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "PickupDropoffTime", 120965);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120968CarVehicleMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "CarVehicle", 120968);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120969RateCodeMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "RateCode", 120969);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120970CarRateQualifierCodeMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "CarRateQualifierCode", 120970);
    }
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void test120972VendorSupplierIDMNXSearch() throws Exception {
        testBasicMNXSearch(CommonScenarios.MN_FRA_Standalone_RoundTrip_OnAirport_LHR.getTestScenario(), "VendorSupplierID", 120972);
    }

    public void testBasicMNXSearch(TestScenario scenario, String verifyType, int tuid) throws Exception {

            final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
            final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
            final String searchGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
            final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), searchGuid, spooferTransport);
            final CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);

            final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, searchGuid);
            final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(searchGuid), searchGuid, testData.getScenarios());
            // verify
            final VARRsp varRsp = new VARRsp(verificationContext.getSpooferTransactions().getElementsByTagName("Response").item(0), new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));
            SearchMapVerification.AssertMNSCSSearchMessage(searchVerificationInput.getResponse(), varRsp, verifyType);

        }


    //Search cases for MN support multiple Vendor search /Search.RequestAllVendors=1
    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void ts161767verifyRespReturnCorrectlyWhenSearchWithMultipleVendorsAndConfigValueEquels1() throws Exception
    {
        verifySearchWithMultipleVendorsAndConfigValueEquelsOne(CommonScenarios.MN_GBR_Standalone_RoundTrip_OnAirport_AGP.getTestScenario(), 161767);
    }

    @Test(groups = {TestGroup.SHOPPING_REGRESSION})
    public void ts161770verifyRespReturnCorrectlyWhenSearchWithMultipleVendorsAndConfigValueEquels1() throws Exception
    {
        verifySearchWithMultipleVendorsAndConfigValueEquelsOne(CommonScenarios.MN_GBR_Standalone_RoundTrip_OffAirport_LHR.getTestScenario(), 161770);
    }

    public void verifySearchWithMultipleVendorsAndConfigValueEquelsOne(TestScenario scenario, int tuid) throws Exception {

        final SCSSearchRequestGenerator scsSearchRequestGenerator = new SCSSearchRequestGenerator(SettingsProvider.CARMNSCSDATASOURCE);
        final SpooferTransport spooferTransport = SettingsProvider.getSpooferTransport(httpClient);
        final String searchGuid = PojoXmlUtil.generateNewOrigGUID(spooferTransport);
        final TestData testData = new TestData(httpClient, scenario, String.valueOf(tuid), searchGuid, spooferTransport);
        final CarSupplyConnectivitySearchRequestType searchRequestType = scsSearchRequestGenerator.createSearchRequest(testData);

        final SearchVerificationInput searchVerificationInput = TransportHelper.sendReceive(httpClient, SettingsProvider.SERVICE_ADDRESS, SettingsProvider.SERVICE_E3DESTINATION, searchRequestType, searchGuid);
        final BasicVerificationContext verificationContext = new BasicVerificationContext(spooferTransport.retrieveRecords(searchGuid), searchGuid, testData.getScenarios());

        baseVerify(searchVerificationInput, verificationContext);
        // verify
        isVARMessageFromCRSLogCorrectlyWhenConfigValueEqualsOne(testData, verificationContext);
        isMapForRequestAndResponse(searchVerificationInput);
    }

    private void baseVerify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext)
    {
        final VerifySearchResponseNotEmpty verifier = new VerifySearchResponseNotEmpty();
        final IVerification.VerificationResult result = verifier.verify(searchVerificationInput, verificationContext);

        if(!result.isPassed())
        {
            Assert.fail(result.toString());
        }
    }

    private void isMapForRequestAndResponse(SearchVerificationInput searchVerificationInput)
    {
        List<Long> reqSupplierIDList = new ArrayList<>();
        List<Long> respSupplierIDList = new ArrayList<>();

        for (int cnt = 0; cnt < searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().size(); cnt++)
        {
            for (long reqSupplierID : searchVerificationInput.getRequest().getCarSearchCriteriaList().getCarSearchCriteria().get(cnt).getVendorSupplierIDList().getVendorSupplierID())
            {
                if (!reqSupplierIDList.contains(reqSupplierID))
                {
                    reqSupplierIDList.add(reqSupplierID);
                }
            }
        }
        for (int count = 0; count < searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().size(); count++)
        {
            for(CarProductType carProductType :searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult().get(count).getCarProductList().getCarProduct())
            {
                long supplierID = carProductType.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
                if (!respSupplierIDList.contains(supplierID))
                {
                    respSupplierIDList.add(supplierID);
                }
            }

        }

        for (long sID : respSupplierIDList)
        {
            if (!reqSupplierIDList.contains(sID))
            {
                Assert.fail("Response verification failed, there is supplierID: " + sID + " exsits in response but not contained in request.");
            }
        }
    }

    private void isVARMessageFromCRSLogCorrectlyWhenConfigValueEqualsOne(TestData testData, BasicVerificationContext verificationContext) throws XPathExpressionException
    {
        final List<Node> vehAvailRateRQs = PojoXmlUtil.getNodesByTagName(verificationContext.getSpooferTransactions().getDocumentElement(), "VehAvailRateRQ");
        if (CollectionUtils.isEmpty(vehAvailRateRQs))
        {
            Assert.fail("Can't find the VAR");
        }
        //string varReqString = DatabaseWrapper.ByteArrayToString(datalist[0].CRSRequest, EncodingType.ASCII);
        if (testData.getScenarios().isOnAirPort() && vehAvailRateRQs.size() > 1)
        {
            Assert.fail("Verification failed, CRSLog has multiple VAR messages for one search request when Search.RequestAllVendors = 1");
        }
        else if (testData.getScenarios().isOnAirPort() && vehAvailRateRQs.size() == 1)
        {
           /* Should not have node like below when Search.RequestAllVendors=1
            <VendorPrefs>
			<VendorPref Code="TO"></VendorPref>
		   </VendorPrefs>*/
           if(null != PojoXmlUtil.getNodeByTagName(vehAvailRateRQs.get(0), "VendorPref")
                && null != PojoXmlUtil.getNodeByTagName(vehAvailRateRQs.get(0), "VendorPref").getAttributes().getNamedItem("Code"))
            {
                Assert.fail("Verification failed, the Var message in CRSLog has passed specific VendorCode when Search.RequestAllVendors = 1");

            }

        }
        else
        {
            for (int i = 0; i < vehAvailRateRQs.size(); i++)
            {
                VARReq var = new VARReq(vehAvailRateRQs.get(i));
                for (int j = i + 1; j < vehAvailRateRQs.size(); j++)
                {
                    VARReq varj = new VARReq(vehAvailRateRQs.get(j));
                    if (varj.getVendorCode().equals(var.getVendorCode()) && varj.getPickUpLocation().equals(var.getPickUpLocation()) && varj.getReturnLocation().equals(var.getReturnLocation()))
                    {
                        Assert.fail("Verification failed, CRSLog has multiple VAR for one location when Search.RequestAllVendors = 1");
                    }
                }
            }
        }
    }

}
