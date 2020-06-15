package com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.search.verification.gdsmapverification;

import com.expedia.e3.data.cartypes.defn.v5.CarSearchCriteriaType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.SupplierItemMap;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARReq;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import com.expedia.s3.cars.framework.test.common.verification.SearchCriteriaComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.utilities.VerificationHelper;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchRequestType;
import com.expedia.s3.cars.supplyconnectivity.titanium.service.tests.SettingsProvider;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by v-mechen on 12/7/2016.
 */


public class VerifyTVARReqestIsSentCorrectly implements ISearchVerification {
    final private DataSource carTitaniumSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_SERVER, SettingsProvider.DB_CAR_Titanium_SCS_DATABASE_NAME,
    SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext)
    {
        return true;
    }

    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final String errorMessage = this.verifyTVARReqMatched((CarSupplyConnectivitySearchRequestType)input.getRequest(), verificationContext);
        /*try {
            errorMessage = this.verifyTVARReqMatched((CarSupplyConnectivitySearchRequestType)input.getRequest(), verificationContext);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            return new VerificationResult(this.getName(), false, Arrays.asList(new String[]{e.getMessage() + e.getStackTrace()}));
        }*/

        return VerificationHelper.getVerificationResult(errorMessage, this.getName());
    }



    private String verifyTVARReqMatched(CarSupplyConnectivitySearchRequestType request, BasicVerificationContext verificationContext) throws DataAccessException, ParserConfigurationException {
        final StringBuilder errorMsg = new StringBuilder();
        //Get GDS request/response list from spoofer
        final NodeList transactions = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "Transaction");
        //Convert all TVAR request
        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(carTitaniumSCSDatasource);
        final List<TVARReq> tvarReqs = parseTVARs(transactions, scsDataSource);

        //Compare request
        for(final CarSearchCriteriaType scsSearchCriteria : request.getCarSearchCriteriaList().getCarSearchCriteria()){
            //If locationID exist, filter out invalid location ids - not in SCS domain value map table
            final boolean isValid = isValidLocation(scsSearchCriteria, scsDataSource);
            if(!isValid){
                continue;
            }

            //get expected PrimaryLangID
            final String expPrimaryLangID = getExpectedLangID(request);

            //Get matched TVAR and do verify
            boolean searchCriteriaMatch = false;
            final StringBuilder matchErrorMsg = new StringBuilder();
            for(int i= 0; i< transactions.getLength(); i++){
                final TVARReq tvarReq = tvarReqs.get(i);
                //Get matched TVAR request by SearchCriteria
                if(SearchCriteriaComparator.isSearchCriteriaEqual(scsSearchCriteria, tvarReq.getCarSearchCriteria(), matchErrorMsg)){
                    searchCriteriaMatch = true;

                    //Verify langID
                    CarNodeComparator.isStringNodeEqual("PrimaryLangID", expPrimaryLangID.toLowerCase(Locale.US), tvarReq.getPrimaryLangID().toLowerCase(Locale.US), errorMsg, new ArrayList<String>());

                    //Verify ISOCountry
                    CarNodeComparator.isStringNodeEqual("ISOCountry", getExpISOCountry(request), tvarReq.getIsoCountry(), errorMsg, new ArrayList<String>());

                    //Verify SupplierItemMap
                    final List<SupplierItemMap> scsSupplierItemMaps = scsDataSource.getSupplierItemMap(scsSearchCriteria.getSupplySubsetIDEntryList().getSupplySubsetIDEntry().get(0).getSupplySubsetID());
                    CompareUtil.compareObject(scsSupplierItemMaps, handleSupplierItemMap(scsSupplierItemMaps,tvarReq.getSupplierItemMaps()), new ArrayList<String>(), errorMsg);

                    //If search criteria is matched, add response to matched list for response veification - add sequence
                    final Node matchedRsp = PojoXmlUtil.getNodeByTagName(transactions.item(i), CommonConstantManager.TitaniumGDSMessageName.COSTAVAILRESPONSE);
                    matchedRsp.getAttributes().setNamedItem(verificationContext.getSpooferTransactions().createAttribute("Sequence"));
                    matchedRsp.getAttributes().getNamedItem("Sequence").setNodeValue(String.valueOf(scsSearchCriteria.getSequence()));


                    break;
                }
            }
            if(!searchCriteriaMatch){
                errorMsg.append("\n-----SearchCriteria is not sent to TVAR request with Sequence ").append(scsSearchCriteria.getSequence()).append("Matching erros as below:\n").append(matchErrorMsg.toString());
            }
        }

        /*System.out.println("SpooferTransactions");
        System.out.println(PojoXmlUtil.toString(verificationContext.getSpooferTransactions()));*/
        //verificationContext.setSpooferTransactions(matchedDoc);

        return errorMsg.toString().isEmpty()?null:errorMsg.toString();
    }


    //Remove suppliterItemMap with contractType if request supplysubsetID doesn't have, this is to fix issue - both CarVendorExtended and CarVendor exist in DB for one supplier, and request supplysubset doesn't have contractType
    private List<SupplierItemMap> handleSupplierItemMap(List<SupplierItemMap> scsSupplierItemMaps, List<SupplierItemMap> gdsSupplierItemMaps)
    {
        boolean scsContractTypeExist = false;
        for(final SupplierItemMap scsItemMap :  scsSupplierItemMaps)
        {
            if(scsItemMap.getItemKey().equals("ContractType"))
            {
                scsContractTypeExist = true;
                break;
            }
        }
        if(scsContractTypeExist)
        {
            return gdsSupplierItemMaps;
        }
        else
        {
            final List<SupplierItemMap> supplierItemMaps = new ArrayList<SupplierItemMap>();
            for(final SupplierItemMap gdsItemMap :  gdsSupplierItemMaps)
            {
                if(!gdsItemMap.getItemKey().equals("ContractType"))
                {
                    supplierItemMaps.add(gdsItemMap);
                }
            }
            return supplierItemMaps;
        }
    }
    //Parse all TVAR requests
    private List<TVARReq> parseTVARs(NodeList transactions, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final List<TVARReq> tvarReqs = new ArrayList<TVARReq>();
        for(int i= 0; i< transactions.getLength(); i++) {
            final TVARReq req = new TVARReq(PojoXmlUtil.getNodeByTagName(transactions.item(i), CommonConstantManager.TitaniumGDSMessageName.COSTAVAILREQUEST), scsDataSource);
            tvarReqs.add(req);
        }
        return tvarReqs;
    }

    /*
    Get expected PrimaryLangID from request
     */
    private String getExpectedLangID(CarSupplyConnectivitySearchRequestType request){
        String expPrimaryLangID = null;
        if(null != request.getLanguage() && null !=request.getLanguage().getLanguageCode()){
            expPrimaryLangID = request.getLanguage().getLanguageCode() + "-" + request.getLanguage().getCountryAlpha2Code();
        }
        return expPrimaryLangID;
    }

    /*
    Get expected ISOCountry from request
     */
    private String getExpISOCountry(CarSupplyConnectivitySearchRequestType request){
        return request.getPointOfSaleKey().getJurisdictionCountryCode().substring(0,2);
    }



    /*
    See if it's a valid Sarch scriteria by location - for offairport, we should can get external location from map table
     */
    private boolean isValidLocation(CarSearchCriteriaType scsSearchCriteria, CarsSCSDataSource scsDataSource) throws DataAccessException {
        boolean isValid = true;
        if (null != scsSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID()
                && scsSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID() > 0) {
            final List<ExternalSupplyServiceDomainValueMap> locationMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(scsSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().get(0),
                    0L, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, String.valueOf(scsSearchCriteria.getCarTransportationSegment().getStartCarLocationKey().getCarVendorLocationID()), null);
            if (locationMapList.isEmpty()) {
                isValid = false;
            }
        }

        if (null != scsSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarVendorLocationID()
                && scsSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarVendorLocationID() > 0) {
            final List<ExternalSupplyServiceDomainValueMap> locationMapList = scsDataSource.getExternalSupplyServiceDomainValueMap(scsSearchCriteria.getVendorSupplierIDList().getVendorSupplierID().get(0),
                    0L, CommonConstantManager.DomainType.CAR_VENDOR_LOCATTION, String.valueOf(scsSearchCriteria.getCarTransportationSegment().getEndCarLocationKey().getCarVendorLocationID()), null);
            if (locationMapList.isEmpty()) {
                isValid = false;
            }
        }
        return isValid;
    }


}

