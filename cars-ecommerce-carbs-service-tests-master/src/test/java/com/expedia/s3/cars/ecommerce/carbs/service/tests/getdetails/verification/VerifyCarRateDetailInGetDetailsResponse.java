package com.expedia.s3.cars.ecommerce.carbs.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getdetail.GetDetailsVerificationInput;
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
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
//    CASSS-2125    ConditionalCostList to be returned in xml format
//    CASSS-2485    CMA - return all the fees payable at the counter in both POS and POSu currency and also return a grand total price that represents the total cost to customer
@SuppressWarnings("PMD")
public class VerifyCarRateDetailInGetDetailsResponse implements IVerification<GetDetailsVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;
    DataSource titaniumDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    public void setTitaniumDatasource(DataSource titaniumDatasource) {
        this.titaniumDatasource = titaniumDatasource;
    }

    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carListInRequest = input.getRequest().getCarProductList().getCarProduct();
        if (carListInRequest.isEmpty()) {
            return false;
        } else {
            return carListInRequest.get(0).getCarRateDetail().getCarCoveragesCostList() != null ||
                    carListInRequest.get(0).getCarRateDetail().getCarAdditionalFeesList() != null;
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        List<CarProductType> carListInRequest = null;
        if (input.getRequest() != null && input.getRequest().getCarProductList() != null) {
            carListInRequest = input.getRequest().getCarProductList().getCarProduct();
        }
        List<CarProductType> carListInResponse = null;
        if (input.getResponse() != null && input.getResponse().getCarProductList() != null) {
            carListInResponse = input.getResponse().getCarProductList().getCarProduct();
        }

        boolean isPassed = false;
        ArrayList remarks = new ArrayList();

        if (carListInRequest != null && carListInRequest.size() > 0 && carListInResponse != null && carListInResponse.size() > 0) {
            CarProductType getDetailReqCar = carListInRequest.get(0);
            CarProductType getDetailRspCar = carListInResponse.get(0);

            CarRateDetailCommonVerifier CCLCommonVerifier = new CarRateDetailCommonVerifier();
            if (carsInventoryDatasource == null) {
                remarks.add("carsInventoryDatasource is Null, need initial first");
            } else if (titaniumDatasource == null) {
                remarks.add("titaniumDatasource is Null, need initial first");
            }

            if (remarks.size() > 0) {
                return new VerificationResult(getName(), isPassed, remarks);
            } else {

                    CCLCommonVerifier.verifyCarRateDetail(carsInventoryDatasource, getGDSMsg(verificationContext, remarks),
                            getDetailReqCar, getDetailRspCar, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT, true);

            }
        } else {
            remarks.add("Car in " + CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_GETDETAILS_VERIFICATION_PROMPT + " Request or Response is Null.");
        }
        if (remarks.size() < 1) {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }

    private CarProductType getGDSMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(titaniumDatasource);

        String rateReference = TiSCSGDSMsgReadHelper.getRateReferenceFromTVRRReq(verificationContext);

       // Node vehAvail = TiSCSGDSMsgReadHelper.getTVRRRspVehAvailFromTVARByReferenceID(verificationContext, rateReference);
        NodeList tvarResps = verificationContext.getSpooferTransactions().getElementsByTagName
        (GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);
        if (null != tvarResps)
        {
            TVARRsp tvarRsp = new TVARRsp(tvarResps.item(tvarResps.getLength() - 1), scsDataSource, true, false);
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