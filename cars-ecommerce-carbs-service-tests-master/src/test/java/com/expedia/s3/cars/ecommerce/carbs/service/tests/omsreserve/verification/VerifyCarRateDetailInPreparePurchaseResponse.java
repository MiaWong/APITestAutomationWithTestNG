package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVRBRsp;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
//    CASSS-2125    ConditionalCostList to be returned in xml format
//    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
public class VerifyCarRateDetailInPreparePurchaseResponse implements IVerification<PreparePurshaseVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;
    DataSource titaniumDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    public void setTitaniumDatasource(DataSource titaniumDatasource) {
        this.titaniumDatasource = titaniumDatasource;
    }

    @Override
    public boolean shouldVerify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {

        if (null == input.getRequest() || null == input.getRequest().getConfiguredProductData() ||
                null == input.getRequest().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct()) {
            return false;
        } else {
            final CarProductType carInRequest = input.getRequest().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
            return carInRequest.getCarRateDetail().getCarCoveragesCostList() != null ||
                    carInRequest.getCarRateDetail().getCarAdditionalFeesList() != null;
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        final CarProductType preparePurchaseReqCar = input.getRequest().getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        final CarProductType preparePurchaseRspCar = input.getResponse().getPreparedItems().getBookedItemList().
                getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();

        boolean isPassed = false;
        final List remarks = new ArrayList();

       final CarRateDetailCommonVerifier cclCommonVerifier = new CarRateDetailCommonVerifier();
        if (carsInventoryDatasource == null) {
            remarks.add("carsInventoryDatasource is Null, need initial first");
        } else if (titaniumDatasource == null) {
            remarks.add("titaniumDatasource is Null, need initial first");
        }

        if (CollectionUtils.isNotEmpty(remarks)) {
            return new VerificationResult(getName(), isPassed, remarks);
        } else {

                cclCommonVerifier.verifyCarRateDetail(carsInventoryDatasource, getGDSMsg(verificationContext, preparePurchaseRspCar, remarks),
                        preparePurchaseReqCar, preparePurchaseRspCar, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT, false);

        }
        if (CollectionUtils.isEmpty(remarks))
        { isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    private CarProductType getGDSMsg(BasicVerificationContext verificationContext, CarProductType carRsp, List remarks) throws DataAccessException {
        CarProductType gdsCar = null;

        final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(titaniumDatasource);

        final NodeList tvbrRspNodeList = verificationContext.getSpooferTransactions().getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVBR_RESPONSE_TYPE);
        if (tvbrRspNodeList.getLength() > 0) {
            final  Node tvbrRspNode = tvbrRspNodeList.item(0);

            if (tvbrRspNode != null) {
                final  TVRBRsp tvrrRsp = new TVRBRsp(tvbrRspNode, scsDataSource, true);
                gdsCar = tvrrRsp.getCarProduct();
            }
        } else {
            remarks.add("Not Find Corresponding Car In GDS Msg, TVBR.");
        }

        final  String SIPP = GDSMsgReadHelper.getExternalSupplyServiceSIPP(scsDataSource, carRsp.getCarInventoryKey().getCarCatalogKey().getCarVehicle(),
                remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT);

        if (StringUtil.isNotBlank(SIPP))
        {
            final NodeList tvarRsps = verificationContext.getSpooferTransactions().getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);


            if (null == tvarRsps)
            {
                remarks.add("getExternalSupplyService SIPP failed in " + CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_PREPAREPURSHASE_VERIFICATION_PROMPT + " SIPP : " + SIPP);
            }
           final TVARRsp tvarRsp = new TVARRsp(tvarRsps.item(tvarRsps.getLength() - 1), scsDataSource, true, false);
            for (final CarProductType carProductType : tvarRsp.getCarProduct().getCarProduct()) {
                if (CarProductComparator.isCorrespondingCar(carRsp, carProductType, false, false)) {
                    gdsCar.setCarRateDetail(carProductType.getCarRateDetail());
                }

            }
        }
        return gdsCar;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
