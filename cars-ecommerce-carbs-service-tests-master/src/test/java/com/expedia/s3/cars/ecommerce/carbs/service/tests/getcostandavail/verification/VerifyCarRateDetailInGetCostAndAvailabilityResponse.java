package com.expedia.s3.cars.ecommerce.carbs.service.tests.getcostandavail.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.getcostandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
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
public class VerifyCarRateDetailInGetCostAndAvailabilityResponse implements IVerification<GetCostAndAvailabilityVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;
    DataSource titaniumDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    public void setTitaniumDatasource(DataSource titaniumDatasource) {
        this.titaniumDatasource = titaniumDatasource;
    }

    @Override
    public boolean shouldVerify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) {
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
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        List<CarProductType> carListInReq = null;
        if (input.getRequest() != null && input.getRequest().getCarProductList() != null) {
            carListInReq = input.getRequest().getCarProductList().getCarProduct();
        }
        List<CarProductType> carListInResponse = null;
        if (input.getResponse() != null && input.getResponse().getCarProductList() != null) {
            carListInResponse = input.getResponse().getCarProductList().getCarProduct();
        }

        boolean isPassed = false;
        ArrayList remarks = new ArrayList();

        if (carListInReq != null && carListInReq.size() > 0 && carListInResponse != null && carListInResponse.size() > 0) {
            CarProductType getCostAndAvailReqCar = carListInReq.get(0);
            CarProductType getCostAndAvailRspCar = carListInResponse.get(0);

            CarRateDetailCommonVerifier CCLCommonVerifier = new CarRateDetailCommonVerifier();

            if (carsInventoryDatasource == null) {
                remarks.add("carsInventoryDatasource is Null, need initial first");
            } else if (titaniumDatasource == null) {
                remarks.add("titaniumDatasource is Null, need initial first");
            }

            if (remarks.size() > 0) {
                return new VerificationResult(getName(), isPassed, remarks);
            } else {

                    CCLCommonVerifier.verifyCarRateDetail(carsInventoryDatasource, getGDSMsg(verificationContext, getCostAndAvailRspCar),
                            getCostAndAvailReqCar, getCostAndAvailRspCar, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT, true);

            }
        } else {
            remarks.add("Car in " + CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT + " Request or Response is Null.");
        }
        if (remarks.size() < 1)
            isPassed = true;

        return new VerificationResult(getName(), isPassed, remarks);
    }

    private CarProductType getGDSMsg(BasicVerificationContext verificationContext, CarProductType getCostAndAvailRspCar) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(titaniumDatasource);

      //  String SIPP = GDSMsgReadHelper.getExternalSupplyServiceSIPP(scsDataSource,
       //         getCostAndAvailRspCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle(), remarks,
        //        CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_COSTANDAVAIL_VERIFICATION_PROMPT);

        //if (!StringUtils.isEmpty(SIPP)) {
            NodeList tvarRsps = verificationContext.getSpooferTransactions().getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);
           // Node vehAvail = TiSCSGDSMsgReadHelper.getTVRRRspVehAvailFromTVARBySIPP(TVARRsps.item(TVARRsps.getLength() - 1), SIPP);

            if (tvarRsps != null) {
               for(int i=0 ;i< tvarRsps.getLength();i++)
                {
                    TVARRsp tvarRsp = new TVARRsp(tvarRsps.item(i), scsDataSource, true, false);
                    for (CarProductType carProductType : tvarRsp.getCarProduct().getCarProduct())
                    {
                        if (CarProductComparator.isCorrespondingCar(getCostAndAvailRspCar, carProductType, false, false))
                        {
                            return carProductType;
                        }
                    }
                }

            }
        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
