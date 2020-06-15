package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getcostandavail;


import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarVendor;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.ChainedVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.TestDataUtil;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.basic.VerifyGetCostAndAvailabilityBasic;
import com.expedia.s3.cars.supplyconnectivity.messages.getcostandavailability.defn.v4.CarSupplyConnectivityGetCostAndAvailabilityResponseType;
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


/**
 * Created by jiyu on 8/22/16.
 */
@SuppressWarnings("PMD")
public class CostAndAvailVerification implements IGetCostAndAvailabilityVerification {

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        Assert.assertNotNull(input);
        Assert.assertNotNull(input.getResponse());
        Assert.assertNotNull(input.getResponse().getCarProductList());
        Assert.assertNotNull(input.getResponse().getCarProductList().getCarProduct());

        //invoke a chain of driver age verifications and return the result...
        final ChainedVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> verifications
                = new ChainedVerification<>(getName(),
                Arrays.asList(
                        new VerifyGetCostAndAvailabilityBasic()
                ));

        return verifications.verify(input, verificationContext);
    }

    public static void verifyCarLocationInfo(BasicVerificationContext basicVerificationContext,CarSupplyConnectivityGetCostAndAvailabilityResponseType response, DataSource scsDataSource) throws DataAccessException {
        verifySpooferReqCarLocationInfo(basicVerificationContext,scsDataSource);
        verifySCSResCarLocationInfo(response,scsDataSource);
    }

    @SuppressWarnings("CPD")
    public static void verifySpooferReqCarLocationInfo(BasicVerificationContext basicVerificationContext,DataSource scsDataSource) throws DataAccessException{
        final StringBuilder errorMsg = new StringBuilder();
        final Document spooferDoc =  basicVerificationContext.getSpooferTransactions();
        if(spooferDoc == null){
            errorMsg.append(" GDS Request is null! ");
        }else{
            final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(scsDataSource);
           // final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
            final NodeList reqNodeList = spooferDoc.getElementsByTagName("Request");
            if(reqNodeList != null && reqNodeList.getLength() > 0){
                for(int i =0;i<reqNodeList.getLength();i++){
                    final Node reqNodeRoot = reqNodeList.item(i);
                    final Node reqNode = reqNodeRoot.getFirstChild().getFirstChild().getFirstChild();
                    final String reqNodeName = reqNode.getNodeName();
                    if("ns1:VehAvailRateRQ".equals(reqNodeName)) {//search request GDS
                        final Node node = reqNode.getChildNodes().item(1);
                        final String startLocationCode = node.getChildNodes().item(1).getFirstChild().getAttributes().getNamedItem("LocationCode").getNodeValue();
                        final String endLocationCode = node.getChildNodes().item(1).getLastChild().getAttributes().getNamedItem("LocationCode").getNodeValue();
                        //when scenario is on airport there is no vendorCode in GDS request
                      //  final String vendorCode = node.getChildNodes().item(2) == null ? null : node.getChildNodes().item(2).getFirstChild().getAttributes().getNamedItem("Code").getNodeValue();
                        Node venderNode = PojoXmlUtil.getNodeByTagName(node, "VendorPref");
                        final String vendorCode = venderNode != null ? venderNode.getAttributes().getNamedItem("Code").getTextContent(): null;
                        // final List<CarVendor> vendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                        List<ExternalSupplyServiceDomainValueMap> startLocationMapList = null;
                        List<ExternalSupplyServiceDomainValueMap> endLocationMapList = null;
                        if(StringUtils.isEmpty(vendorCode)){//when scenario is on airport there is no vendorCode in GDS request
                            startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0,0,"CarVendorLocation","",startLocationCode+"%");
                            endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0,0,"CarVendorLocation","",endLocationCode+"%");

                        }else{
                            List<CarVendor> vendorList = getCarVendor(vendorCode);
                            startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode, vendorList);
                            endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", endLocationCode, vendorList);

                        }
                        if (CollectionUtils.isEmpty(startLocationMapList) || CollectionUtils.isEmpty(endLocationMapList)) {
                            errorMsg.append(" VendorLocation send to GDS request is error! ");
                            break;
                        }
                    }else if("ns1:VehRateRuleRQ".equals(reqNodeName)){//getDetails and getCostAndAvail request GDS
                        final String locationInfo = reqNode.getChildNodes().item(1).getAttributes().getNamedItem("ID_Context").getNodeValue();
                        final String startLocationCode = locationInfo.substring(locationInfo.length()-14,locationInfo.length()-7);
                        final String endLocationCode = locationInfo.substring(locationInfo.length()-7,locationInfo.length());
                        final String vendorCode = locationInfo.substring(0,2);
                       // final List<CarVendor> carVendorList = carsInventoryHelper.getCarVendorList(vendorCode);
                        List<CarVendor> carVendorList = getCarVendor(vendorCode);
                        final List<ExternalSupplyServiceDomainValueMap> startLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation", startLocationCode, carVendorList);
                        final List<ExternalSupplyServiceDomainValueMap> endLocationMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap("CarVendorLocation",endLocationCode, carVendorList);
                        //when carlocationId and location code are both invalid,ignore this error
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

    private static List<CarVendor> getCarVendor(String vendorCode) {
        String supplierID = TestDataUtil.getSupplierIDByVendorCode(vendorCode);
        List<CarVendor> vendorList = new ArrayList<>();
        CarVendor carVendor = new CarVendor();
        carVendor.setSupplierID(supplierID);
        vendorList.add(carVendor);
        return vendorList;
    }

    public static void verifySCSResCarLocationInfo(CarSupplyConnectivityGetCostAndAvailabilityResponseType response,DataSource scsDataSource) throws DataAccessException {
        final StringBuilder errorMsg = new StringBuilder("");
        if( null != response.getCarProductList() && !CollectionUtils.isEmpty(response.getCarProductList().getCarProduct())){
            final CarProductType productType = response.getCarProductList().getCarProduct().get(0);
            verifyLocation(errorMsg, productType, scsDataSource);

        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    @SuppressWarnings("CPD")
    public static void verifyLocation(StringBuilder errorMsg, CarProductType productType, DataSource scsDataSource) throws DataAccessException {
        final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        final CarLocationKeyType startLocationReturn2 = productType.getCarPickupLocation().getCarLocationKey();
        final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey();
        final CarLocationKeyType endLocationReturn2 = productType.getCarDropOffLocation().getCarLocationKey();
        final String startCarVendorLocationCodeReturn = startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
        final String startCarVendorLocationCodeReturn2 = startLocationReturn2.getCarLocationCategoryCode() + startLocationReturn2.getSupplierRawText();
        final String endCarVendorLocationCodeReturn = endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
        final String endCarVendorLocationCodeReturn2 = endLocationReturn2.getCarLocationCategoryCode() + endLocationReturn2.getSupplierRawText();
        if((null == startLocationReturn.getCarVendorLocationID() || 0L == startLocationReturn.getCarVendorLocationID()) || (null == startLocationReturn2.getCarVendorLocationID() || 0L == startLocationReturn2.getCarVendorLocationID())){
            errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey or CarPickupLocation!");
        }
        if((null == endLocationReturn.getCarVendorLocationID() || 0L == endLocationReturn.getCarVendorLocationID()) || (null == endLocationReturn2.getCarVendorLocationID() || 0L == endLocationReturn2.getCarVendorLocationID())){
            errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey or CarDropOffLocation!");
        }
      //final CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(carsInventorydatasource);
      //  final CarVendorLocation startLocationVerify = carsInventoryHelper.getCarLocation(startLocationReturn.getCarVendorLocationID());
       // final CarVendorLocation endLocationVerify = carsInventoryHelper.getCarLocation(endLocationReturn.getCarVendorLocationID());
        final  CarsSCSHelper carsSCSHelper = new CarsSCSHelper(scsDataSource);
        final List<ExternalSupplyServiceDomainValueMap> startLocationVerify = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0, "CarVendorLocation", startLocationReturn.getCarVendorLocationID().toString(), null);
        final List<ExternalSupplyServiceDomainValueMap> endLocationVerify = carsSCSHelper.getExternalSupplyServiceDomainValueMap(0, 0, "CarVendorLocation", endLocationReturn.getCarVendorLocationID().toString(), null);

        if(startLocationVerify == null){
            errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
        }
        if(endLocationVerify == null){
            errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
        }
        if(!startLocationReturn.getLocationCode().equals(startLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !startCarVendorLocationCodeReturn.equals(startLocationVerify.get(0).getExternalDomainValue().substring(3,startLocationVerify.get(0).getExternalDomainValue().length()))
                || !startLocationReturn2.getLocationCode().equals(startLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !startCarVendorLocationCodeReturn2.equals(startLocationVerify.get(0).getExternalDomainValue().substring(3,startLocationVerify.get(0).getExternalDomainValue().length())))
        {
            errorMsg.append("Wrong pick up CarVendorLocationInfo returned!");
        }
        if(!endLocationReturn.getLocationCode().equals(endLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !endCarVendorLocationCodeReturn.equals(endLocationVerify.get(0).getExternalDomainValue().substring(3 , endLocationVerify.get(0).getExternalDomainValue().length()))
                || !endLocationReturn2.getLocationCode().equals(endLocationVerify.get(0).getExternalDomainValue().substring(0,3)) || !endCarVendorLocationCodeReturn2.equals(endLocationVerify.get(0).getExternalDomainValue().substring(3, endLocationVerify.get(0).getExternalDomainValue().length())))
        {
            errorMsg.append("Wrong drop off CarVendorLocationInfo returned!");
        }
    }
}

