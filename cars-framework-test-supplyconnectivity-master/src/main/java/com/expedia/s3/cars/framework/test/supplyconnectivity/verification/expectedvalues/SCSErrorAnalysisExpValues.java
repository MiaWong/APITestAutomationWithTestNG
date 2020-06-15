package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.expectedvalues;

import com.expedia.e3.data.cartypes.defn.v5.AuditLogTrackingDataType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.e3.data.placetypes.defn.v4.PointOfSaleKeyType;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.BasicKeys;
import com.expedia.s3.cars.framework.test.common.constant.datalogkeys.DataLogKeys_ErrorAnalysis;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fehu on 12/5/2016.
 */
public class SCSErrorAnalysisExpValues {
    @SuppressWarnings("CPD-START")
    private SCSErrorAnalysisExpValues() {
    }

    /// Generate expected ErrorAnalysis values for MNSCS search
    public static List<Map<String, String>> genExpeErrorAnalysisMNSCSSearch(TestData testdata, CarSupplyConnectivitySearchRequestType req, CarSupplyConnectivitySearchResponseType rsp, Document spooferTransactions)
    {
        final List<Map<String, String>> expectedValues = new ArrayList<>();

        //Get response xpath error
        final String rspXPathError = getXPathsFromErrprCollection(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rsp)));

        if (testdata.getScenarios().isOnAirPort()) {
            verifyForOnAirport(req, spooferTransactions, expectedValues, rspXPathError);
        } else {
            verifyForOffairport(req, spooferTransactions, expectedValues, rspXPathError);

        }

        return expectedValues;
    }

    private static void verifyForOffairport(CarSupplyConnectivitySearchRequestType req, Document spooferTransactions, List<Map<String, String>> expectedValues, String rspXPathError) {
       final NodeList nodeListReq = spooferTransactions.getElementsByTagNameNS("*", "VehAvailRateRQ");
       final NodeList nodeListResp = spooferTransactions.getElementsByTagNameNS("*", "VehAvailRateRS");
        for(int i=0; nodeListReq.getLength()>i ;i++)
        {
            final String vendorCode = PojoXmlUtil.getNodeByTagName(nodeListReq.item(i), "VendorPref").getAttributes().getNamedItem("Code").getTextContent();
            final String reqSupplierIDs = TestDataUtil.getSupplierIDByVendorCode(vendorCode);

            //Get GDS response error List
            final List<Node> errorList = PojoXmlUtil.getNodesByTagName(nodeListResp.item(i), "Error");
            for(int j=0; errorList.size()>j ;j++)
            {
                final Map<String, String> expectedValue = new HashMap<>();
                //Error info expected values
                expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE, errorList.get(j).getAttributes().getNamedItem("Code").getTextContent());
                expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORTEXT, errorList.get(j).getTextContent());
                //Common expected values
                genExpErrorAnalysisComVal(req.getAuditLogTrackingData(), req.getPointOfSaleKey(), reqSupplierIDs, rspXPathError,
                        CommonConstantManager.ActionType.SEARCH.toString(), "VAR", expectedValue);

                expectedValues.add(expectedValue);
            }

        }
    }

    private static void verifyForOnAirport(CarSupplyConnectivitySearchRequestType req, Document spooferTransactions, List<Map<String, String>> expectedValues, String rspXPathError) {
        //Get request supplierIDs
        final List<Long> reqSupplierList = new ArrayList<>();
        for (final CarSearchCriteriaType searchCriteria : req.getCarSearchCriteriaList().getCarSearchCriteria()) {
            reqSupplierList.addAll(searchCriteria.getVendorSupplierIDList().getVendorSupplierID());
        }
        final String reqSupplierIDs = reqSupplierList.toString();


        //Get GDS response error List
        final NodeList errorList = spooferTransactions.getElementsByTagNameNS("*","Error");
        for(int i=0; errorList.getLength()>i ;i++)
        {
            final Map<String, String> expectedValue = new HashMap<>();
            //Error info expected values
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE, errorList.item(i).getAttributes().getNamedItem("Code").getTextContent());
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORTEXT, errorList.item(i).getTextContent());
            //Common expected values
            genExpErrorAnalysisComVal(req.getAuditLogTrackingData(), req.getPointOfSaleKey(), reqSupplierIDs, rspXPathError,
                    CommonConstantManager.ActionType.SEARCH.toString(), "VAR", expectedValue);

            expectedValues.add(expectedValue);
        }
    }

    public static  List<Map<String, String>> genExpeErrorAnalysisMNSCSDetail(CarSupplyConnectivityGetDetailsRequestType req,
                                                                    CarSupplyConnectivityGetDetailsResponseType rsp,
                                                                    Document spooferTransactions)
    {
      
        final List<Map<String, String>> expectedValues = new ArrayList<>();

        //Get request supplierIDs
       final long reqSupplierIDs = req.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();

        //Get response xpath error
      final String xPathError = getXPathsFromErrprCollection(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rsp)));

        //Get GDS response error List
        final NodeList errorNodeList = spooferTransactions.getElementsByTagNameNS("*","Error");
        for(int i=0; errorNodeList.getLength()>i ;i++)
        {
           final Map<String, String> expectedValue = new HashMap<>();
            //Error info expected values
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE, errorNodeList.item(i).getAttributes().getNamedItem("Code").getTextContent());
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORTEXT, errorNodeList.item(i).getTextContent());
            //Common expected values
            genExpErrorAnalysisComVal(req.getAuditLogTrackingData(), req.getPointOfSaleKey(), String.valueOf(reqSupplierIDs), xPathError,
                    CommonConstantManager.ActionType.GETDETAILS.toString(), "VRR", expectedValue);

            expectedValues.add(expectedValue);
        }

        return expectedValues;
    }

    public static  List<Map<String, String>> genExpeErrorAnalysisMNSCSCostAndAvail(CarSupplyConnectivityGetCostAndAvailabilityRequestType req,
                                                                                   CarSupplyConnectivityGetCostAndAvailabilityResponseType rsp,
                                                                                   Document spooferTransactions)
    {

        final List<Map<String, String>> expectedValues = new ArrayList<>();

        //Get request supplierIDs
        final long supplierIDs = req.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();

        //Get response xpath error
        final String rspXPathError = getXPathsFromErrprCollection(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rsp)));

        //Get GDS response error List
        final NodeList errorLists = spooferTransactions.getElementsByTagNameNS("*","Error");
        for(int i=0; errorLists.getLength()>i ;i++)
        {
            final Map<String, String> expectedValue = new HashMap<>();
            //Error info expected values
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE, errorLists.item(i).getAttributes().getNamedItem("Code").getTextContent());
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORTEXT, errorLists.item(i).getTextContent());
            //Common expected values
            genExpErrorAnalysisComVal(req.getAuditLogTrackingData(), req.getPointOfSaleKey(), String.valueOf(supplierIDs), rspXPathError,
                    CommonConstantManager.ActionType.GETCOSTANDAVAILABILITY.toString(), "VRR", expectedValue);

            expectedValues.add(expectedValue);
        }

        return expectedValues;
    }


    /// Generate common values for MNSCS ErrorAnalysis
    public  static void genExpErrorAnalysisComVal(AuditLogTrackingDataType auditLogTrackingData, PointOfSaleKeyType pointOfSaleKey, String reqSupplierIDs,
                                                           String rspXPathError, String actionType, String gdsMessageType, Map<String, String> expectedValue)
    {
        //Common values
        expectedValue.put(BasicKeys.ACTIONTYPE, actionType);
        expectedValue.put(BasicKeys.LOGTYPE, CommonEnumManager.DataLogType.ErrorAnalysis.toString());
        expectedValue.put(DataLogKeys_ErrorAnalysis.DOWNSTREAMMESSAGETYPE, gdsMessageType);
        expectedValue.put(DataLogKeys_ErrorAnalysis.TUID, String.valueOf(auditLogTrackingData.getLogonUserKey().getUserID()));
        expectedValue.put(DataLogKeys_ErrorAnalysis.TPID, String.valueOf(auditLogTrackingData.getAuditLogTPID()));
        expectedValue.put(DataLogKeys_ErrorAnalysis.POSJURISDICTION, pointOfSaleKey.getJurisdictionCountryCode());
        expectedValue.put(DataLogKeys_ErrorAnalysis.POSMANAGEMENTUNIT, pointOfSaleKey.getManagementUnitCode());
        expectedValue.put(DataLogKeys_ErrorAnalysis.POSCOMPANY, pointOfSaleKey.getCompanyCode());
        expectedValue.put(DataLogKeys_ErrorAnalysis.REQUESTSUPPLIERIDS, reqSupplierIDs);
        if (StringUtil.isNotBlank(rspXPathError))
        {
            expectedValue.put(DataLogKeys_ErrorAnalysis.FIELDXPATH, rspXPathError);
        }
    }



    public static  List<Map<String, String>> genExpeErrorAnalysisWSCSReserve(CarSupplyConnectivityReserveRequestType req,
                                                                             CarSupplyConnectivityReserveResponseType rsp,
                                                                             Document spooferTransactions)
    {

        final List<Map<String, String>> expectedValues = new ArrayList<>();

        //Get request supplierIDs
        final long supplierIDs = req.getCarProduct().getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();

        //Get response xpath error
        final String rspXPathError = getXPathsFromErrprCollection(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(rsp)));

        //Get GDS response error List
        final NodeList errorLists = spooferTransactions.getElementsByTagNameNS("*","Error");
        for(int i=0; errorLists.getLength()>i ;i++)
        {
            final Map<String, String> expectedValue = new HashMap<>();
            //Error info expected values
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORRETURNCODE, errorLists.item(i).getAttributes().getNamedItem("Code").getTextContent());
            expectedValue.put(DataLogKeys_ErrorAnalysis.ERRORTEXT, errorLists.item(i).getTextContent());
            //Common expected values
            genExpErrorAnalysisComVal(req.getAuditLogTrackingData(), req.getPointOfSaleKey(), String.valueOf(supplierIDs), rspXPathError,
                    CommonConstantManager.ActionType.RESERVE.toString(), "VCRR", expectedValue);

            expectedValues.add(expectedValue);
        }

        return expectedValues;
    }
    @SuppressWarnings("CPD-END")
    //region MNSCS ErrorAnalysis
    public  static String getXPathsFromErrprCollection(String responseErrorCollectionStr)
    {
        final StringBuffer rspXPathError = new StringBuffer();
        final Document errorCollection = PojoXmlUtil.stringToXml(responseErrorCollectionStr);
        final NodeList errors = errorCollection.getElementsByTagName("XPath");
        if (errors != null && errors.getLength() > 0 )
        {
            for (int i=0; errors.getLength()>i; i++)
            {
                if (!StringUtils.isEmpty(errors.item(i).getTextContent()))
                {
                    rspXPathError.append(errors.item(i).getTextContent());
                    rspXPathError.append(',');
                }
            }
        }
        return StringUtils.isEmpty(String.valueOf(rspXPathError)) ? String.valueOf(rspXPathError) : String.valueOf(rspXPathError).substring(0, rspXPathError.length()-1);
    }
}
