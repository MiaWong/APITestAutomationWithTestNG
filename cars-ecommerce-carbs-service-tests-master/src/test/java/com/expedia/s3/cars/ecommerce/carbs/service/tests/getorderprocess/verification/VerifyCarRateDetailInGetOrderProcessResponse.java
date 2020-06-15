package com.expedia.s3.cars.ecommerce.carbs.service.tests.getorderprocess.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.GetOrderProcessVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TiSCSGDSMsgReadHelper;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by miawang on 8/30/2016.
 */
//    CASSS-2125    ConditionalCostList to be returned in xml format
//    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
@SuppressWarnings("PMD")
public class VerifyCarRateDetailInGetOrderProcessResponse implements IVerification<GetOrderProcessVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;
    DataSource titaniumDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    public void setTitaniumDatasource(DataSource titaniumDatasource) {
        this.titaniumDatasource = titaniumDatasource;
    }

    @Override
    public boolean shouldVerify(GetOrderProcessVerificationInput input, BasicVerificationContext verificationContext) {

        if (null == input.getRequest() || null == input.getRequest().getConfiguredOfferData() ||
                null == input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct()) {
            return false;
        } else {
            final CarProductType carInRequest = input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct();
            return carInRequest.getCarRateDetail().getCarCoveragesCostList() != null ||
                    carInRequest.getCarRateDetail().getCarAdditionalFeesList() != null;
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(GetOrderProcessVerificationInput input, BasicVerificationContext verificationContext) {
        CarProductType getOrderProcessReqCar = null;
        CarProductType getOrderProcessRspCar = null;
        if (input.getRequest() != null && input.getRequest().getConfiguredOfferData() != null &&
                input.getRequest().getConfiguredOfferData().getCarOfferData() != null &&
                input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation() != null) {
            getOrderProcessReqCar = input.getRequest().getConfiguredOfferData().getCarOfferData().getCarReservation().getCarProduct();
        }

        if (input.getResponse() != null && input.getResponse().getOrderProductList() != null &&
                input.getResponse().getOrderProductList().getOrderProduct() != null &&
                input.getResponse().getOrderProductList().getOrderProduct().size() > 0 &&
                input.getResponse().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData() != null &&
                input.getResponse().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData() != null &&
                input.getResponse().getOrderProductList().getOrderProduct().get(0).getConfiguredProductData().getCarOfferData().getCarReservation() != null) {
            getOrderProcessRspCar = input.getResponse().getOrderProductList().getOrderProduct().get(0)
                    .getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        }

        boolean isPassed = false;
        ArrayList remarks = new ArrayList();

        if (getOrderProcessReqCar != null && getOrderProcessRspCar != null) {
            CarRateDetailCommonVerifier CCLCommonVerifier = new CarRateDetailCommonVerifier();
            if (carsInventoryDatasource == null) {
                remarks.add("carsInventoryDatasource is Null, need initial first");
            } else if (titaniumDatasource == null) {
                remarks.add("titaniumDatasource is Null, need initial first");
            }

            if (remarks.size() > 0) {
                return new VerificationResult(getName(), isPassed, remarks);
            } else {
                try {
                    CCLCommonVerifier.verifyCarRateDetail(carsInventoryDatasource, getGDSMsg(verificationContext, remarks),
                            getOrderProcessReqCar, getOrderProcessRspCar, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT, false);
                } catch (Exception e) {
                    remarks.add(e);
                }
            }
        } else {
            remarks.add("Car in " + CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETORDERPROCESS_VERIFICATION_PROMPT + " Request or Response is Null.");
        }
        if (remarks.size() < 1)
            isPassed = true;

        return new VerificationResult(getName(), isPassed, remarks);
    }

    private CarProductType getGDSMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(titaniumDatasource);
        String rateReference = TiSCSGDSMsgReadHelper.getRateReferenceFromTVRRReq(verificationContext);

        // Node vehAvail = TiSCSGDSMsgReadHelper.getTVRRRspVehAvailFromTVARByReferenceID(verificationContext, rateReference);
        NodeList tvarRespList = verificationContext.getSpooferTransactions().getElementsByTagName
                (GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);
        if (null != tvarRespList)
        {
            TVARRsp tvarRsp = new TVARRsp(tvarRespList.item(tvarRespList.getLength() - 1), scsDataSource, true, false);
            for(CarProductType carProductType: tvarRsp.getCarProduct().getCarProduct())
            {
                if(rateReference.equalsIgnoreCase(carProductType.getCarInventoryKey().getCarRate().getCarRateQualifierCode()))
                {
                    return carProductType;
                }
            }

        } else {
            remarks.add("Not Find TVAR message");
        }

        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
