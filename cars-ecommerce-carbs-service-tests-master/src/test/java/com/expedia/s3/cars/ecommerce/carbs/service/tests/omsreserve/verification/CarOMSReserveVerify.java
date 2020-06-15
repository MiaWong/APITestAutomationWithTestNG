package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CustomerLocationType;
import com.expedia.e3.data.errortypes.defn.v4.FieldInvalidErrorType;
import com.expedia.e3.data.placetypes.defn.v4.AddressType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CommonUtil;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMReserveReqAndRespGenerator;
import com.expedia.s3.cars.ecommerce.carbs.service.requestgenerater.CarbsOMRetrieveReqAndRespGenerator;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestDataErrHandle;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequest;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import expedia.om.supply.messages.defn.v1.SupplyErrorType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by yyang4 on 11/14/2016.
 */
@SuppressWarnings("PMD")
public class CarOMSReserveVerify {

    private CarOMSReserveVerify() {
    }

    public static void existCorrectErrorVerifier(PreparePurchaseResponseType response, TestDataErrHandle errHandle){
        //verify if exists excepted error info in PreparePurchaseResponse
        final StringBuffer errorMessage = new StringBuffer();
        if(response.getResponseStatus().getSupplyErrorList() == null || CollectionUtils.isEmpty(response.getResponseStatus().getSupplyErrorList().getSupplyError())){
            errorMessage.append("No ErrorColection returned in response.");
        }else{
            for(final SupplyErrorType errorType : response.getResponseStatus().getSupplyErrorList().getSupplyError()){
                final List<FieldInvalidErrorType> fieldInvalidErrorTypes= errorType.getErrorDetailData().getCarTransactionalErrorCollection().getFieldInvalidErrorList().getFieldInvalidError();
                if(CollectionUtils.isEmpty(fieldInvalidErrorTypes)){
                    errorMessage.append("No FieldInvalidError Colection returned in response.");
                    break;
                }else{
                    boolean exsitsCorrectError = false;
                    for(final FieldInvalidErrorType fieldInvalidErrorType : fieldInvalidErrorTypes){
                        if(!StringUtils.isEmpty(errHandle.getExpErrorMsg()) && errHandle.getExpErrorMsg().equals(fieldInvalidErrorType.getDescriptionRawText())){
                            exsitsCorrectError = true;
                            break;
                        }
                    }
                    if(!exsitsCorrectError){
                        errorMessage.append("No correct ErrorInfo returned in response.");
                        break;
                    }
                }
            }
        }
        CommonUtil.notNullErrorMsg(String.valueOf(errorMessage));
    }

    public static void  isPreparePurchaseVerify(PreparePurchaseResponseType response){
        //verify if book success
        final StringBuffer errorMessage = new StringBuffer();
        if(response == null){
            errorMessage.append("Verify PreparePurchaseResponse failed. No data return in response.");
        }else if(response.getPreparedItems() == null || response.getPreparedItems().getBookedItemList() == null || CollectionUtils.isEmpty(response.getPreparedItems().getBookedItemList().getBookedItem())){
            errorMessage.append("Verify PreparePurchaseResponse failed. No car product return in response.");
        }else if(response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getBookingStateCode() != null
                && !String.valueOf(CarCommonEnumManager.BookingStateCode.Booked).equals(response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getBookingStateCode())
                && !String.valueOf(CarCommonEnumManager.BookingStateCode.Pending).equals(response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getBookingStateCode())
                && !String.valueOf(CarCommonEnumManager.BookingStateCode.Confirm).equals(response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getBookingStateCode())){
            errorMessage.append("Verify PreparePurchaseResponse failed,BookingStateCode="+response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getBookingStateCode());
        }
        CommonUtil.notNullErrorMsg(String.valueOf(errorMessage));
    }


    public static void  isAmadesCar(CarProductType carProductType, DataSource dataSource) throws DataAccessException{
        //verify if car product come from amadeus
        final StringBuffer errorMessage = new StringBuffer();
        if(carProductType == null){
            errorMessage.append("Verify CarproductType failed. No car product return in response.");
        }else{
           final CarsInventoryHelper helper = new CarsInventoryHelper(dataSource);
            if(!helper.isSpecificProviderCar(carProductType,6)){
                errorMessage.append("No Amadeus car product found in response.");
            }
        }
        CommonUtil.notNullErrorMsg(String.valueOf(errorMessage));
    }

    public static void failoverTestFeatureOffVerify(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, BasicVerificationContext basicVerificationContext, TestDataErrHandle errHandle){
        //1.verify if book success
        isPreparePurchaseVerify(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
        //2.verify if exists excepted error info in PreparePurchaseResponse
        existCorrectErrorVerifier(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType(),errHandle);
        //3.verify if exists two VCRR response in GDS
        final StringBuffer errorMessage = new StringBuffer();
        final Document spooferDoc =  basicVerificationContext.getSpooferTransactions();
        if(spooferDoc == null){
            errorMessage.append(" GDS Response is null! ");
        }else{
            final NodeList vcrrReqNodeList = spooferDoc.getElementsByTagNameNS("*","VehicleCreateReservationReq");
            if(vcrrReqNodeList == null || vcrrReqNodeList.getLength() != 2){
                errorMessage.append(" No two VCRR responses retuned in GDS! ");
            }
        }
        CommonUtil.notNullErrorMsg(String.valueOf(errorMessage));
    }

    public static void failoverTestFeatureOnVerify(CarbsOMReserveReqAndRespGenerator carbsOMReserveReqAndRespGenerator, BasicVerificationContext basicVerificationContext, TestDataErrHandle errHandle, DataSource dataSource,int vcrrCount) throws DataAccessException{
        if(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType() == null){
            Assert.fail("Verify PreparePurchaseResponse failed. No data return in response.");
        }
        //1.verify if book success
        isPreparePurchaseVerify(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType());
        //2.verify if car product come from amadeus
        isAmadesCar(carbsOMReserveReqAndRespGenerator.getPreparePurchaseResponseType().getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(),dataSource);
        //3.verify if exists two VCRR response in GDS and if get ACAQ and ACSQ request from GDS
        final StringBuffer errorMessage = new StringBuffer();
        final Document spooferDoc =  basicVerificationContext.getSpooferTransactions();
        if(spooferDoc == null){
            errorMessage.append(" GDS Response is null! ");
        }else{
            //verify if exists match VCRR  response count in GDS
            final NodeList vcrrReqNodeList = spooferDoc.getElementsByTagNameNS("*","VehicleCreateReservationReq");
            if(vcrrReqNodeList == null || vcrrReqNodeList.getLength() != vcrrCount){
                errorMessage.append(" No two VCRR responses found in GDS! ");
            }
            //verify if get ACAQ and ACSQ request from GDS
            final NodeList ascsReqNodeList = spooferDoc.getElementsByTagNameNS("*","AmadeusSessionManagerRequest");
            if(ascsReqNodeList == null || ascsReqNodeList.getLength() < 1){
                errorMessage.append(" No ACAQ and ACSQ requests found in GDS! ");
            }
        }
        CommonUtil.notNullErrorMsg(String.valueOf(errorMessage));
    }

    public static void failoverTestFeatureOnRetrieveVerify(CarbsOMRetrieveReqAndRespGenerator carbsOMRetrieveReqAndRespGenerator, DataSource dataSource)throws DataAccessException{
        if(carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType() == null){
            Assert.fail("Verify RetrieveResponse failed. No data return in response.");
        }
        isAmadesCar(carbsOMRetrieveReqAndRespGenerator.getRetrieveResponseType().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct(),dataSource);
    }

    public static void collectionAndDeliveryVerify(PreparePurchaseRequest request, PreparePurchaseResponseType response) {

        final CustomerLocationType delLocation_req = request.getConfiguredProductData().getCarOfferData().getCarReservation().getDeliveryLocation();
        final CustomerLocationType delLocation_res = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getDeliveryLocation();

        final CustomerLocationType colLocation_req = request.getConfiguredProductData().getCarOfferData().getCarReservation().getCollectionLocation();
        final CustomerLocationType colLocation_res = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCollectionLocation();

        if (null != delLocation_req)
        {
            if (null == delLocation_res)
            {
                Assert.fail("No Delivery location exist in response , but exist in request.");
            }
            else
            {
                final String locationCode_req = delLocation_req.getCustomerLocationCode();
                final String locationCode_res = delLocation_res.getCustomerLocationCode();
                final AddressType add_req = delLocation_req.getAddress();
                final AddressType add_res = delLocation_res.getAddress();

                checkDeliveryAndCollectionMappingInResponse(locationCode_req, locationCode_res, add_req, add_res, "Delivery");

            }
        }

        if (null != colLocation_req)
        {
            if (null == colLocation_res)
            {
                Assert.fail("No Collectioin location exist in response , but exist in request");
            }
            else
            {
                final String locationCode_req = colLocation_req.getCustomerLocationCode();
                final String locationCode_res = colLocation_req.getCustomerLocationCode();
                final AddressType add_req = colLocation_req.getAddress();
                final AddressType add_rsp = colLocation_res.getAddress();

                checkDeliveryAndCollectionMappingInResponse(locationCode_req, locationCode_res, add_req, add_rsp, "Collection");
            }
        }
    }

    private static void checkDeliveryAndCollectionMappingInResponse(String locCodeReq, String locCodeRes, final AddressType add_req, final AddressType add_res, String colOrDel) {

        // locationCode
        mappingInRequestAndResponse(locCodeReq, locCodeRes, colOrDel, "locationCode");

        if (null != add_req)
        {
            //FirstAddressLine
            final String addressInreq = add_req.getFirstAddressLine()
                    +(add_req.getSecondAddressLine() == null ? null : "" + add_req.getSecondAddressLine())
                    +(add_req.getThirdAddressLine() == null ? null : "" + add_req.getThirdAddressLine())
                    +(add_req.getFourthAddressLine() == null ? null : "" + add_req.getFourthAddressLine())
                    +(add_req.getFifthAddressLine() == null ? null : "" + add_req.getFifthAddressLine());

            if (add_res.getFirstAddressLine() != null)
            {
                if (add_res.getFirstAddressLine().length() <= 60)
                {
                    if (!addressInreq.replace(" ", "").toUpperCase().contains(add_res.getFirstAddressLine().replace(" ", "").toUpperCase()))
                    {
                        Assert.fail(String.format("The {0} address maching failed in request and response :" +
                                        "request value is [{1}], response value is [{2}].", colOrDel, addressInreq,
                                add_res.getFirstAddressLine()));
                    }
                }
                else
                {
                    if (addressInreq.length() != 60)
                    {
                        Assert.fail(String.format("The {0} response addressline {1} should remain for 60 character,acutal lengh is {2}",
                                colOrDel, add_res.getFirstAddressLine(), add_res.getFirstAddressLine().length()));
                    }
                }
            }

            //CityName
            mappingInRequestAndResponse(add_req.getCityName(), add_res.getCityName(), colOrDel, "CityName");
            //PostalCode
            mappingInRequestAndResponse(add_req.getPostalCode(), add_res.getPostalCode(), colOrDel, "PostalCode");
            //CountryAlpha3Code
            mappingInRequestAndResponse(add_req.getCountryAlpha3Code(), add_res.getCountryAlpha3Code(), colOrDel, "CountryAlpha3Code");

        }
    }

    private static void mappingInRequestAndResponse(final String val_req,final String val_res,String colOrDel,String verifyName)
    {
        if (null != val_req)
        {
            if (null == val_res)
            {
                Assert.fail(String.format("The {0} {1} assert failed : request value is [{2}] ,but response value is null",
                        colOrDel, verifyName, val_req));
            }
            else
            {
                if (!val_req.toUpperCase().trim().equals(val_res.toUpperCase().trim()))
                {
                    Assert.fail(String.format("The {0} {1} assert failed : request value is [{2}] ,but in response value is [{3}]",
                            colOrDel, verifyName, val_req, val_res));
                }
            }
        }
    }

}