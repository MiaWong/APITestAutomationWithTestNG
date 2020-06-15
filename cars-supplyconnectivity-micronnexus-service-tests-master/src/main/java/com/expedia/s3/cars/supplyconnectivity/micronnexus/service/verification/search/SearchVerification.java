package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.search;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil.getXmlFieldValue;


/**
 * Created by mpaudel on 5/18/16.
 */
@SuppressWarnings("PMD")
public class SearchVerification implements ISearchVerification {
    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarSearchResultList());

        if (input.getResponse().getCarSearchResultList().getCarSearchResult().size() == 0) {
            Assert.fail("No SearchResult return in response.");
        }

        //verify car product returned
        final StringBuilder errorMsg = new StringBuilder();
        boolean matchedCarReturned = false;
        for (final CarSearchResultType result : input.getResponse().getCarSearchResultList().getCarSearchResult()) {
            if (null != result.getCarProductList()
                    && null != result.getCarProductList().getCarProduct()
                    && result.getCarProductList().getCarProduct().size() > 0) {
                matchedCarReturned = true;
                break;
            }
        }

        if (!matchedCarReturned) {
            errorMsg.append("No Car returned in CarSCS response.");
        }

        if (null != input.getResponse().getErrorCollectionList()) {
            final List<String> descriptionRawTextList = getXmlFieldValue(input.getResponse().getErrorCollectionList().getErrorCollection(),
                    "DescriptionRawText");
            if (!descriptionRawTextList.isEmpty()) {
                errorMsg.append("ErrorCollection is present in response");
                descriptionRawTextList.parallelStream().forEach((String s) -> errorMsg.append(s));
            }
        }

        if (errorMsg.toString().trim().length() > 0) {
            Assert.fail(errorMsg.toString());
        }

        return new VerificationResult(getName(), true, Arrays.asList("Success"));
    }

    public static void verifyExistsResponseError(SearchVerificationInput input){
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        if(input.getResponse().getCarSearchResultList() != null && !CollectionUtils.isEmpty(input.getResponse().getCarSearchResultList().getCarSearchResult())) {
            for (CarSearchResultType result : input.getResponse().getCarSearchResultList().getCarSearchResult()) {
                if (null != result.getCarProductList()
                        && null != result.getCarProductList().getCarProduct()
                        && result.getCarProductList().getCarProduct().size() > 0) {
                    Assert.fail("Car returned error,except no car return!");
                    break;
                }
            }
        }
        if (null == input.getResponse().getErrorCollectionList()) {
            Assert.fail("No error collection return in response.");
        }else{
            final List<ErrorCollectionType>  errorCollectionTypeList = input.getResponse().getErrorCollectionList().getErrorCollection();
            if (!CollectionUtils.isEmpty(errorCollectionTypeList)) {
                for(ErrorCollectionType errorCollectionType : errorCollectionTypeList) {
                    if(errorCollectionType.getUnclassifiedErrorList() == null || CollectionUtils.isEmpty(errorCollectionType.getUnclassifiedErrorList().getUnclassifiedError())) {
                        Assert.fail("No except error return in response.");
                        break;
                    }
                }
            }
        }
    }

    public static void verifyCarLocationInfo(BasicVerificationContext basicVerificationContext, CarSupplyConnectivitySearchResponseType response, DataSource scsDataSource, String ignoreFlag, TestScenario scenario) throws DataAccessException {
        verifySpooferReqCarLocationInfo(basicVerificationContext,scsDataSource,ignoreFlag,scenario);
        verifySCSResCarLocationInfo(response,ignoreFlag, scsDataSource,scenario);
    }

    private static void verifySpooferReqCarLocationInfo(BasicVerificationContext basicVerificationContext,DataSource scsDataSource, String ignoreFlag,TestScenario scenario) throws DataAccessException{
        StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc =  basicVerificationContext.getSpooferTransactions();
        if(spooferDoc == null){
            errorMsg.append(" GDS Request is null! ");
        }else{
            final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(scsDataSource);
           // final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
            final NodeList reqNodeList = spooferDoc.getElementsByTagName("Request");
            if(reqNodeList != null && reqNodeList.getLength() > 0){
                for(int i =0;i<reqNodeList.getLength();i++){
                    final Node reqNode = reqNodeList.item(i).getFirstChild().getFirstChild().getFirstChild();
                    final String reqNodeName = reqNode.getNodeName();
                    if("ns1:VehAvailRateRQ".equals(reqNodeName)) {//search request GDS
                        final Node node = reqNode.getChildNodes().item(1);
                       //when scenario is on airport there is no vendorCode in GDS request
                        final String vendorCode = node.getChildNodes().item(2) == null ? null : node.getChildNodes().item(2).getFirstChild().getAttributes().getNamedItem("Code").getNodeValue();
                        // List<CarVendor> vendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                        final String startLocationCode = node.getChildNodes().item(1).getFirstChild().getAttributes().getNamedItem("LocationCode").getNodeValue();
                        final String endLocationCode = node.getChildNodes().item(1).getLastChild().getAttributes().getNamedItem("LocationCode").getNodeValue();

                        List<ExternalSupplyServiceDomainValueMap> startLocationMapList = null;
                        List<ExternalSupplyServiceDomainValueMap> endLocationMapList = null;
                        if(scenario.isOnAirPort()){//when scenario is on airport there is no vendorCode in GDS request
                            startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0,0,"CarVendorLocation","",startLocationCode+"%");
                            endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0,0,"CarVendorLocation","",endLocationCode+"%");

                        }else{
                            List<CarVendor> vendorList = getCarVendorlist(vendorCode);
                            startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode, vendorList);
                            endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endLocationCode, vendorList);

                        }
                        //when carlocationId and location code are both invalid,ignore this error
                        if(isaBoolean(ignoreFlag, startLocationCode, endLocationCode)){
                            continue;
                        }
                        if (CollectionUtils.isEmpty(startLocationMapList) || CollectionUtils.isEmpty(endLocationMapList)) {
                            errorMsg.append(" VendorLocation send to GDS request is error! ");
                            break;
                        }
                    }else if("ns1:VehRateRuleRQ".equals(reqNodeName)){//getDetails and getCostAndAvail request GDS
                        final String locationInfo = reqNode.getChildNodes().item(1).getAttributes().getNamedItem("ID_Context").getNodeValue();

                        final String vendorCode = locationInfo.substring(0,2);
                        final List<CarVendor> vendorList = getCarVendorlist(vendorCode);

                        final String startLocationCode = locationInfo.substring(locationInfo.length()-14,locationInfo.length()-7);
                        final String endLocationCode = locationInfo.substring(locationInfo.length()-7,locationInfo.length());

                        List<ExternalSupplyServiceDomainValueMap> startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode, vendorList);
                        List<ExternalSupplyServiceDomainValueMap> endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation",endLocationCode, vendorList);


                        //when carlocationId and location code are both invalid,ignore this error
                        if(isaBoolean(ignoreFlag, startLocationCode, endLocationCode)){
                            continue;
                        }
                        if (CollectionUtils.isEmpty(startLocationMapList) || CollectionUtils.isEmpty(endLocationMapList)) {
                            errorMsg.append(" VendorLocation send to GDS request is error! ");
                            break;
                        }

                    }
                    if(!StringUtils.isEmpty(String.valueOf(errorMsg))){
                        break;
                    }
                }
            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg))){
            Assert.fail(errorMsg.toString());
        }

    }

    private static List<CarVendor> getCarVendorlist(String vendorCode) {
        String supplierID = TestDataUtil.getSupplierIDByVendorCode(vendorCode);
        List<CarVendor> carVendorList = new ArrayList<>();
        CarVendor carVendor = new CarVendor();
        carVendor.setSupplierID(supplierID);
        carVendorList.add(carVendor);
        return carVendorList;
    }

    private static boolean isaBoolean(String ignoreFlag, String startLocationCode, String endLocationCode) {
        return "1".equals(ignoreFlag) && (startLocationCode.contains("XXX") || endLocationCode.contains("XXX"));
    }


    private static void verifySCSResCarLocationInfo(CarSupplyConnectivitySearchResponseType response, String ignoreFlag, DataSource scsDataSource,TestScenario scenario) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder();
        final CarSearchResultListType carSearchResultListType = response.getCarSearchResultList();
        if(!CollectionUtils.isEmpty(carSearchResultListType.getCarSearchResult())){
            for(CarSearchResultType resultType : carSearchResultListType.getCarSearchResult()){
                if(null != resultType.getCarProductList() && !CollectionUtils.isEmpty(resultType.getCarProductList().getCarProduct())){
                    for(CarProductType productType : resultType.getCarProductList().getCarProduct()){
                        final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
                        final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
                        final String startCarVendorLocationCodeReturn = startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
                        final String endCarVendorLocationCodeReturn = endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
                        //when carlocationId and location code are both invalid,ignore this error
                        if(isaBoolean(ignoreFlag, startLocationReturn.getLocationCode(), endLocationReturn.getLocationCode())){
                            continue;
                        }
                        if(!scenario.isOnAirPort() && (null == startLocationReturn.getCarVendorLocationID() || 0L == startLocationReturn.getCarVendorLocationID())){
                            errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey!");
                            break;
                        }
                        if(!scenario.isOnAirPort() && (null == endLocationReturn.getCarVendorLocationID() || 0L == endLocationReturn.getCarVendorLocationID())){
                            errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey!");
                            break;
                        }
                      //  final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
                        //final CarVendorLocation startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
                      //  final CarVendorLocation endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());

                        final  CarsSCSHelper carsSCSHelper = new CarsSCSHelper(scsDataSource);
                        if(!CompareUtil.isObjEmpty(startLocationReturn.getCarVendorLocationID()) && !CompareUtil.isObjEmpty(endLocationReturn.getCarVendorLocationID())) {
                            final List<ExternalSupplyServiceDomainValueMap> startLocationVerify = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0, "CarVendorLocation", startLocationReturn.getCarVendorLocationID().toString(), null);
                            final List<ExternalSupplyServiceDomainValueMap> endLocationVerify = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0, "CarVendorLocation", endLocationReturn.getCarVendorLocationID().toString(), null);
                            if(startLocationVerify == null){
                                errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
                                break;
                            }
                            if(endLocationVerify == null){
                                errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
                                break;
                            }
                            if(!startLocationReturn.getLocationCode().equals(startLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !startCarVendorLocationCodeReturn.equals(startLocationVerify.get(0).getExternalDomainValue().substring(3, startLocationVerify.get(0).getExternalDomainValue().length()))){
                                errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
                                break;
                            }
                            if(!endLocationReturn.getLocationCode().equals(endLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !endCarVendorLocationCodeReturn.equals(endLocationVerify.get(0).getExternalDomainValue().substring(3, endLocationVerify.get(0).getExternalDomainValue().length())))
                            {
                                errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
                                break;
                            }
                        }
                    }
                }
                if(!StringUtils.isEmpty(String.valueOf(errorMsg))){
                    break;
                }

            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg))){
            Assert.fail(errorMsg.toString());
        }
    }
}
