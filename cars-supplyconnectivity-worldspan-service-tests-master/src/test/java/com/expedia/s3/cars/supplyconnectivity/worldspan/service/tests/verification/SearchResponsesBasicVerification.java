package com.expedia.s3.cars.supplyconnectivity.worldspan.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendorLocation;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubset;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.ISearchVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.CarSupplyConnectivitySearchResponseType;
import com.expedia.s3.cars.supplyconnectivity.messages.search.defn.v4.ErrorCollectionType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
public class SearchResponsesBasicVerification implements ISearchVerification {
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
                    if(errorCollectionType.getFieldInvalidErrorList() == null || CollectionUtils.isEmpty(errorCollectionType.getFieldInvalidErrorList().getFieldInvalidError())) {
                        Assert.fail("No except error return in response.");
                        break;
                    }
                }
            }
        }

    }

    public static void verifyCarLocationInfo(BasicVerificationContext basicVerificationContext,CarSupplyConnectivitySearchResponseType response, DataSource carWorldSpandataSource,DataSource carsInventorydatasource,String ignoreFlag, TestScenario scenario) throws DataAccessException {
        verifySpooferReqCarLocationInfo(basicVerificationContext,carWorldSpandataSource,carsInventorydatasource,ignoreFlag,scenario);
        verifySCSResCarLocationInfo(response,carsInventorydatasource,ignoreFlag,scenario);
    }
    private static void verifySpooferReqCarLocationInfo(BasicVerificationContext basicVerificationContext,DataSource carWorldSpandataSource,DataSource carsInventorydatasource,String ignoreFlag, TestScenario scenario) throws DataAccessException{
        StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc =  basicVerificationContext.getSpooferTransactions();
        if(spooferDoc == null){
            errorMsg.append(" GDS Request is null! ");
        }else{
            final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(carWorldSpandataSource);
            final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
            final NodeList reqNodeList = spooferDoc.getElementsByTagName("Request");
            if(reqNodeList != null && reqNodeList.getLength() > 0){
                for(int i =0;i<reqNodeList.getLength();i++){
                    final Node reqNodeRoot = reqNodeList.item(i);
                    final Node reqNode = reqNodeRoot.getFirstChild().getFirstChild().getFirstChild();
                    final String reqNodeName = reqNode.getNodeName();
                    if("veh:VehicleSearchAvailabilityReq".equals(reqNodeName)) {
                        final NodeList vendorLocationList = reqNode.getChildNodes().item(1).getChildNodes();
                        if (vendorLocationList != null && vendorLocationList.getLength() > 0) {
                            for (int j = 0; j < vendorLocationList.getLength(); j++) {
                                final Node locationNode = vendorLocationList.item(j);
                                final NamedNodeMap attrs = locationNode.getAttributes();
                                final String locationCode = attrs.getNamedItem("LocationCode").getNodeValue();
                                final String vendorCode = attrs.getNamedItem("VendorCode").getNodeValue();
                                final String vendorLocationID = attrs.getNamedItem("VendorLocationID") == null ? "" : attrs.getNamedItem("VendorLocationID").getNodeValue().substring(1);
                                final List<CarVendor> carVendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                                final List<ExternalSupplyServiceDomainValueMap> locationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", locationCode + "%" + vendorLocationID, carVendorList);
                                //when carlocationId and location code are both invalid,ignore this error
                                if("1".equals(ignoreFlag) && "XXX".equals(locationCode)){
                                    continue;
                                }
                                if(CollectionUtils.isEmpty(locationMapList)) {
                                    errorMsg.append(" VendorLocation send to GDS request is error! ");
                                    break;
                                }
                            }
                        }
                    }else if("veh:VehicleRulesReq".equals(reqNodeName)){
                        final Node vendorLocationNode = reqNode.getChildNodes().item(1).getFirstChild();
                        final Node vendorCodeNode = reqNode.getChildNodes().item(1).getLastChild().getFirstChild().getFirstChild();
                        final NamedNodeMap locationAttrs = vendorLocationNode.getAttributes();
                        final String startLocationCode = locationAttrs.getNamedItem("PickupLocation").getNodeValue();
                        final String startVendorLocationID = locationAttrs.getNamedItem("PickupLocationNumber").getNodeValue().substring(1);
                        final String endLocationCode = locationAttrs.getNamedItem("ReturnLocation").getNodeValue();
                        final String endVendorLocationID = locationAttrs.getNamedItem("ReturnLocationNumber").getNodeValue().substring(1);
                        final String vendorCode = vendorCodeNode.getAttributes().getNamedItem("Code").getNodeValue();
                        final List<CarVendor> carVendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                        List<ExternalSupplyServiceDomainValueMap> startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode + "%" + startVendorLocationID, carVendorList);
                        List<ExternalSupplyServiceDomainValueMap> endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endLocationCode + "%" + endVendorLocationID, carVendorList);

                        //when carlocationId and location code are both invalid,ignore this error
                        if("1".equals(ignoreFlag) && "XXX".equals(startLocationCode)){
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
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }

    }

    private static void verifySCSResCarLocationInfo(CarSupplyConnectivitySearchResponseType response, DataSource carsInventorydatasource,String ignoreFlag, TestScenario scenario) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder();
        final CarSearchResultListType carSearchResultListType = response.getCarSearchResultList();
        if(!CollectionUtils.isEmpty(carSearchResultListType.getCarSearchResult())){
            for(CarSearchResultType resultType : carSearchResultListType.getCarSearchResult()){
                if(null != resultType.getCarProductList() && !CollectionUtils.isEmpty(resultType.getCarProductList().getCarProduct())){
                    for(CarProductType productType : resultType.getCarProductList().getCarProduct()){
                        final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
                        final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
                        //when carlocationId and location code are both invalid,ignore this error
                        if("1".equals(ignoreFlag) && "XXX".equals(startLocationReturn.getLocationCode())){
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
                        final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
                        CarVendorLocation startLocationVerify = null;
                        CarVendorLocation endLocationVerify = null;
                        if(scenario.isOnAirPort()){
                            List<SupplySubset> supplySubsets = carsInventoryHelper.getDistinctSupplierIds(scenario);
                            List<CarVendorLocation> startCarLocationList = carsInventoryHelper.getCarLocationList(startLocationReturn.getLocationCode(),startLocationReturn.getCarLocationCategoryCode()+startLocationReturn.getSupplierRawText(),supplySubsets);
                            startLocationVerify = CompareUtil.isObjEmpty(startCarLocationList) ? null : startCarLocationList.get(0);
                            List<CarVendorLocation> endCarLocationList = carsInventoryHelper.getCarLocationList(endLocationReturn.getLocationCode(),endLocationReturn.getCarLocationCategoryCode()+endLocationReturn.getSupplierRawText(),supplySubsets);
                            endLocationVerify = CompareUtil.isObjEmpty(endCarLocationList) ? null : endCarLocationList.get(0);
                        }else{
                            startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
                            endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());
                        }
                        if(startLocationVerify == null){
                            errorMsg.append("No matched pick up CarVendorLocation recode find in DB!");
                            break;
                        }
                        if(endLocationVerify == null){
                            errorMsg.append("No matched drop off CarVendorLocation recode find in DB!");
                            break;
                        }
                        if(!startLocationReturn.getLocationCode().equals(startLocationVerify.getLocationCode()) || !startLocationReturn.getCarLocationCategoryCode().equals(startLocationVerify.getCarLocationCategoryCode()) || !startLocationReturn.getSupplierRawText().equals(startLocationVerify.getSupplierRawText())){
                            errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
                            break;
                        }
                        if(!endLocationReturn.getLocationCode().equals(endLocationVerify.getLocationCode()) || !endLocationReturn.getCarLocationCategoryCode().equals(endLocationVerify.getCarLocationCategoryCode()) || !endLocationReturn.getSupplierRawText().equals(endLocationVerify.getSupplierRawText())){
                            errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
                            break;
                        }

                    }
                }
                if(!StringUtils.isEmpty(String.valueOf(errorMsg))){
                    break;
                }

            }
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }

    }
}
