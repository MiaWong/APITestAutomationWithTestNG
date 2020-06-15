package com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.customized;

import com.expedia.e3.data.cartypes.defn.v5.CarCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarCoveragesCostType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleOptionType;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by miawang on 8/30/2016.
 */

@SuppressWarnings("PMD")
public class VerifyConditionalCostListInResponse implements IGetDetailsVerification
{
    @Override
    public boolean shouldVerify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carListInRequest = input.getRequest().getCarProductList().getCarProduct();
        if (carListInRequest.isEmpty()) {
            return false;
        } else {
            return (carListInRequest.get(0).getCarRateDetail().getCarCoveragesCostList() != null ||
                    carListInRequest.get(0).getCarRateDetail().getCarAdditionalFeesList() != null);
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarProductType> carListInRequest = input.getRequest().getCarProductList().getCarProduct();

        final CarProductType getDetailReqCar = carListInRequest.get(0);

        final List<CarProductType> carListInResponse = input.getResponse().getCarProductList().getCarProduct();
        final CarProductType getDetailRspCar = carListInResponse.get(0);

        boolean isPassed = false;
        List<String> remarks = new ArrayList<>();

        /**Don't need to compare VehicleOptionList in req and resp , eg. For MN search response, there is no
        //vehicleOptionList return, then details request car will also not have vehicleOptionList.if compare
        with details response(MN details have VehicleOptionList return) , there will be exception.*/
        if(3L != getDetailRspCar.getProviderID())
        {
            remarks = this.verifyVehicleOptionList(getDetailReqCar, getDetailRspCar, remarks);
            remarks = this.verifyCarCoveragesCostList(getDetailReqCar, getDetailRspCar, remarks);
        }

        remarks = this.verifyCarAdditionalFeesList(getDetailReqCar,getDetailRspCar, remarks);

        if (remarks.size() < 1) {
            isPassed = true;
        }
        return new VerificationResult(getName(), isPassed, remarks);
    }

    private List<String> verifyVehicleOptionList(CarProductType getDetailReqCar, CarProductType getDetailRspCar, List<String> remarks)
    {
        boolean isPassed = false;
        final int reqVOLSize = getDetailReqCar.getCarVehicleOptionList()==null? 0: getDetailReqCar.getCarVehicleOptionList().getCarVehicleOption().size();
        final int rspVOLSize = getDetailRspCar.getCarVehicleOptionList()==null? 0: getDetailRspCar.getCarVehicleOptionList().getCarVehicleOption().size();

        if(rspVOLSize != reqVOLSize) {
            remarks.add("CarVehicleOptionList size in GetDetail Response: ("+rspVOLSize+") is not same as size in GetDetail Request: (" + reqVOLSize + ").");
        }

        if (rspVOLSize != 0) {
            for (final CarVehicleOptionType reqCVOT : getDetailReqCar.getCarVehicleOptionList().getCarVehicleOption()) {
                for (final CarVehicleOptionType rspCVOT : getDetailRspCar.getCarVehicleOptionList().getCarVehicleOption()) {
                    if (reqCVOT.getCarVehicleOptionCategoryCode().equals(rspCVOT.getCarVehicleOptionCategoryCode())
                            && reqCVOT.getCarSpecialEquipmentCode().equals( rspCVOT.getCarSpecialEquipmentCode())
                            && reqCVOT.getCost().getFinanceApplicationCode().equals(rspCVOT.getCost().getFinanceApplicationCode())
                            && reqCVOT.getCost().getFinanceApplicationUnitCount().equals(rspCVOT.getCost().getFinanceApplicationUnitCount())
                            && reqCVOT.getCarVehicleOptionMaxCount() != null
                            && rspCVOT.getCarVehicleOptionMaxCount() != null
                            && reqCVOT.getCarVehicleOptionMaxCount().equals(rspCVOT.getCarVehicleOptionMaxCount())) {
                        isPassed = true;
                        if(rspCVOT.getCost().getFinanceApplicationUnitCount () != 1)
                        {
                            remarks.add("FinanceApplicationUnitCount of CarVehicleOption in GetDetail Request is:"+
                                    rspCVOT.getCost().getFinanceApplicationUnitCount ()+" should be 1.");
                        }
                        break;
                    }
                }
                if (!isPassed) {
                    remarks.add("CarVehicleOption exist in GetDetail Request: ( CarVehicleOptionCategoryCode: " + reqCVOT.getCarVehicleOptionCategoryCode() +
                            " CarSpecialEquipmentCode : "+reqCVOT.getCarSpecialEquipmentCode()+") is not find in GetDetail Response.");
                } else {
                    isPassed = false;
                }
            }
        }
        else
        {
            remarks.add("CarVehicleOption list is empty in GetDetail.");
        }

        return remarks;
    }

    private List<String> verifyCarCoveragesCostList(CarProductType getDetailReqCar, CarProductType getDetailRspCar, List<String> remarks)
    {
        boolean isPassed = false;

        final int reqCCCLSize = getDetailReqCar.getCarRateDetail().getCarCoveragesCostList()==null? 0:
                getDetailReqCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost().size();
        final int rspCCCLSize = getDetailRspCar.getCarRateDetail().getCarCoveragesCostList()==null? 0:
                getDetailRspCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost().size();

        if(reqCCCLSize != rspCCCLSize) {
            remarks.add("CarCoveragesCostList size in GetDetail Response: ("+rspCCCLSize+") is not same as size in GetDetail Request: (" + reqCCCLSize + ").");
        }

        if (reqCCCLSize != 0) {
            for (final CarCoveragesCostType reqCCCL : getDetailReqCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost()) {
                for (final CarCoveragesCostType rspCCCL: getDetailRspCar.getCarRateDetail().getCarCoveragesCostList().getCarCoveragesCost()) {
                    if(!rspCCCL.getCarCost().getFinanceCategoryCode().equals("Coverages"))
                    {
                        remarks.add("getFinanceCategoryCode in CarProduct/CarRateDetail/CarCoveragesCostList in GetDetail Response is : ("
                                + rspCCCL.getCarCost().getFinanceCategoryCode() + ") it should be Coverages.");
                        break;
                    }
                    if (rspCCCL.getCarCost().getFinanceCategoryCode().equals(reqCCCL.getCarCost().getFinanceCategoryCode())
                            && rspCCCL.getCarCost().getFinanceSubCategoryCode().equals(reqCCCL.getCarCost().getFinanceSubCategoryCode())
                            && rspCCCL.getCarCost().getFinanceApplicationCode().equals(reqCCCL.getCarCost().getFinanceApplicationCode())
                            && rspCCCL.getCarCost().isRequiredCostBoolean()==reqCCCL.getCarCost().isRequiredCostBoolean()
                            && rspCCCL.getCarCost().getFinanceApplicationUnitCount().equals(reqCCCL.getCarCost().getFinanceApplicationUnitCount())) {
                        if (reqCCCL.getCarDeductible() != null && rspCCCL.getCarDeductible() != null
                                && reqCCCL.getCarDeductible().getExcessAmount() != null && rspCCCL.getCarDeductible().getExcessAmount() != null
                                && reqCCCL.getCarDeductible().getExcessAmount().getCurrencyCode().equals(rspCCCL.getCarDeductible().getExcessAmount().getCurrencyCode())
                                && reqCCCL.getCarDeductible().getExcessAmount().getSimpleAmount().equals(rspCCCL.getCarDeductible().getExcessAmount().getSimpleAmount())) {
                            isPassed = true;
                        }
                        else if(reqCCCL.getCarDeductible() == null && rspCCCL.getCarDeductible() == null) {
                            isPassed = true;
                        }
                        else {
                            isPassed = false;
                        }
                        if(isPassed) {
                            if (reqCCCL.getCarCost().getFinanceApplicationUnitCount() != 0 && reqCCCL.getCarCost().getFinanceApplicationUnitCount() != 1) {
                                remarks.add("FinanceApplicationUnitCount of CarVehicleOption in GetDetail Request is:" +
                                        reqCCCL.getCarCost().getFinanceApplicationUnitCount() + " should be 0 or 1.");
                            }
                            break;
                        }
                    }
                }
                if (!isPassed) {
                    remarks.add("CarCoveragesCostType exist in GetDetail Request: ( FinanceCategoryCode : " + reqCCCL.getCarCost().getFinanceCategoryCode() +
                            " FinanceSubCategoryCode : "+ reqCCCL.getCarCost().getFinanceSubCategoryCode() +
                            " FinanceApplicationCode : "+ reqCCCL.getCarCost().getFinanceApplicationCode() +") is not find in GetDetail Response.");
                } else {
                    isPassed = false;
                }
            }
            if (remarks.size() < 1) {
                isPassed = true;
            }

        }
        else
        {
            remarks.add("CarCoveragesCostList list is empty in GetDetail.");
        }

        return remarks;
    }

    private  List<String> verifyCarAdditionalFeesList(CarProductType getDetailReqCar, CarProductType getDetailRspCar, List<String> remarks)
    {

        int reqCAFLSize = 0;
        if (getDetailReqCar.getCarRateDetail().getCarAdditionalFeesList() != null &&
                getDetailReqCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() != null) {
            reqCAFLSize = getDetailReqCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().size();
        }

        int rspCAFLSize = 0;
        if (getDetailRspCar.getCarRateDetail().getCarAdditionalFeesList() != null &&
                getDetailRspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees() != null) {
            rspCAFLSize = getDetailRspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees().size();
        }

        if(reqCAFLSize != rspCAFLSize) {
            remarks.add("CarAdditionalFeesList size in GetDetail Response: ("+rspCAFLSize+") is not same as size in GetDetail Request: (" + reqCAFLSize + ").");
        }

        if (reqCAFLSize == 0) {
            remarks.add("CarAdditionalFeesList list is empty in GetDetail.");
            return remarks;
        }

        return verifyCarAdditionalFeesListBigLoop(getDetailReqCar, getDetailRspCar, remarks);
    }

    //  PMD fix verifyCarAdditionalFeesList
    private boolean iaComparedIdentical(CarCostType rspCAFL, CarCostType reqCAFL)
    {
        return (rspCAFL.getFinanceCategoryCode().equals(reqCAFL.getFinanceCategoryCode())
                && rspCAFL.getFinanceSubCategoryCode().equals(reqCAFL.getFinanceSubCategoryCode())
                && rspCAFL.getFinanceApplicationCode().equals(reqCAFL.getFinanceApplicationCode())
                && rspCAFL.isRequiredCostBoolean() == reqCAFL.isRequiredCostBoolean()
                && rspCAFL.getFinanceApplicationUnitCount().equals(reqCAFL.getFinanceApplicationUnitCount()));
    }

    private boolean iaComparedValidUnitCount(CarCostType rspCAFL, CarCostType reqCAFL)
    {
        return (reqCAFL.getFinanceApplicationUnitCount() != 0 && reqCAFL.getFinanceApplicationUnitCount() != 1);
    }

    private List<String> verifyCarAdditionalFeesListSmallLoop(CarProductType getDetailReqCar, CarProductType getDetailRspCar, List<String> remarks, CarCostType reqCAFL)
    {

        for (final CarCostType rspCAFL: getDetailRspCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {

            if (!rspCAFL.getFinanceCategoryCode().equals("Fees"))
            {
                remarks.add("getFinanceCategoryCode in CarProduct/CarRateDetail/CarAdditionalFeesList in GetDetail Response is : ("
                        + rspCAFL.getFinanceCategoryCode() + ") it should be Fees.");

                remarks.add("CarAdditionalFeesList exist in GetDetail Request: ( FinanceCategoryCode :" + reqCAFL.getFinanceCategoryCode() +
                        "FinanceSubCategoryCode : "+ reqCAFL.getFinanceSubCategoryCode() +
                        "FinanceApplicationCode : "+ reqCAFL.getFinanceApplicationCode() +") is not found in GetDetail Response.");

                return remarks;
            }

            if (iaComparedIdentical(rspCAFL, reqCAFL) && iaComparedValidUnitCount(rspCAFL, reqCAFL) ) {
                remarks.add("FinanceApplicationUnitCount of CarVehicleOption in GetDetail Request is:" +
                        reqCAFL.getFinanceApplicationUnitCount() + " should be 0 or 1.");
            }

            if (iaComparedIdentical(rspCAFL, reqCAFL)) {
                return remarks;
            }

        }

        return remarks;
    }


    private List<String> verifyCarAdditionalFeesListBigLoop(CarProductType getDetailReqCar, CarProductType getDetailRspCar, List<String> remarks)
    {
        List<String> msg = remarks;

        for (final CarCostType reqCAFL : getDetailReqCar.getCarRateDetail().getCarAdditionalFeesList().getCarAdditionalFees()) {
            msg = verifyCarAdditionalFeesListSmallLoop(getDetailReqCar, getDetailRspCar, remarks, reqCAFL);
        }

        return msg;
    }

}

