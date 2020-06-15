package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogMakeModelType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import org.w3c.dom.Node;

/**
 * Created by miawang on 2/7/2017.
 */
public class ASCSReserve {
    @SuppressWarnings("PMD")
    public void buildCarReservationCar(BasicVerificationContext verificationContext, CarInventoryKeyType reqInventoryKey, CarReservationType reservation,
                                       CarsSCSDataSource scsDataSource, StringBuffer eMsg) {
        final Node node_APAM_PNR_AME1_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.RESERVE,GDSMsgNodeTags.AmadeusNodeTags.APAM_PNR_AME1_RESPONSE_TYPE);
        final Node node_ACSQ_CAR_SELL_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE);
        final Node node_ACSQ_CAR_SELL_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_RESPONSE_TYPE);
        final Node node_ARIS_RIFCS_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ARIS_RIFCS_RESPONSE_TYPE);
        final Node node_APCM_PNR_AME2_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.APCM_PNR_AME2_RESPONSE_TYPE);

        if (null == node_APAM_PNR_AME1_RESPONSE || null == node_ACSQ_CAR_SELL_REQUEST || null == node_ACSQ_CAR_SELL_RESPONSE
                || null == node_ARIS_RIFCS_RESPONSE || null == node_APCM_PNR_AME2_RESPONSE) {
            eMsg.append("\nCan Not Find ");
            if (null == node_APAM_PNR_AME1_RESPONSE) {
                eMsg.append("APAM_RESPONSE ");
            }
            if (null == node_ACSQ_CAR_SELL_REQUEST) {
                eMsg.append("/ ACSQ_REQUEST ");
            }
            if (null == node_ACSQ_CAR_SELL_RESPONSE) {
                eMsg.append("/ ACSQ_RESPONSE ");
            }
            if (null == node_ARIS_RIFCS_RESPONSE) {
                eMsg.append("/ ARIS_RESPONSE ");
            }
            if (null == node_APCM_PNR_AME2_RESPONSE) {
                eMsg.append("/ APCM_RESPONSE ");
            }
            eMsg.append(" In Spoofer document.");
        } else {
            buildCarReservationFromCRS(reservation, reqInventoryKey, scsDataSource, node_APAM_PNR_AME1_RESPONSE,
                    node_ACSQ_CAR_SELL_REQUEST, node_ACSQ_CAR_SELL_RESPONSE, node_ARIS_RIFCS_RESPONSE, node_APCM_PNR_AME2_RESPONSE, eMsg);
        }
    }

    @SuppressWarnings("PMD")
    private void buildCarReservationFromCRS(CarReservationType reservation,
                                            CarInventoryKeyType reqInventoryKey,
                                            CarsSCSDataSource scsDataSource,
                                            Node nodeApamPnrAme1Rsp,
                                            Node nodeAcsqCarSellReq, Node nodeAcsqCarSellRsp,
                                            Node nodeArisRifcsRsp, Node nodeApcmPnrAme2Rsp, StringBuffer eMsg) {

        //booked status will impact the ARIS response returned .
        final ACSQRsp acsqRsp = new ACSQRsp();
        final String statusCode = acsqRsp.getstatusCodeFromAcsqCarSellRsp(nodeAcsqCarSellRsp);

        final CarProductType carProduct = new CarProductType();
        reservation.setCarProduct(carProduct);


        // 1. car Inventory
        buildCarsInventory(carProduct, scsDataSource, nodeAcsqCarSellReq, nodeAcsqCarSellRsp, eMsg);

        if(null == carProduct.getCarInventoryKey().getCarItemID())
        {
            carProduct.getCarInventoryKey().setCarItemID(reqInventoryKey.getCarItemID());
        }

        if(null == carProduct.getCarInventoryKey().getSupplySubsetID())
        {
            carProduct.getCarInventoryKey().setSupplySubsetID(reqInventoryKey.getSupplySubsetID());
        }

        if(null == carProduct.getCarInventoryKey().getCarRate())
        {
            carProduct.getCarInventoryKey().setCarRate(new CarRateType());
        }
        if(null == carProduct.getCarInventoryKey().getCarRate().getRateCategoryCode())
        {
            carProduct.getCarInventoryKey().getCarRate().setRateCategoryCode("Standard");
        }

        //2.AvailStatusCode
        carProduct.setAvailStatusCode("A");

        //3.CarCatalogMakeModel
        final ARISRsp arisRsp = new ARISRsp();
        final CarCatalogMakeModelType makeModel = arisRsp.buildCarCatalogMakeModel(nodeArisRifcsRsp);
        if (null != makeModel) {
            carProduct.setCarCatalogMakeModel(makeModel);
            //4.CarDoorCount
            carProduct.setCarDoorCount(makeModel.getCarMinDoorCount());
        }

        //5.CarPickupLocation 6.CarDropOffLocation
        buildPickupOrDropoffLocation(carProduct, nodeArisRifcsRsp, statusCode, eMsg);

        //7.CostList
        buildCarCostList(carProduct, nodeArisRifcsRsp, statusCode, eMsg);

        // 8. CarVehicleOption for special equipment
        buildCarVehicleOption(carProduct, nodeArisRifcsRsp, eMsg);

        // 8. CarVehicleOption for special equipment
        buildSpecialEquiomentList(reservation, nodeArisRifcsRsp, scsDataSource, eMsg);

        // 9. carRateDetail ConditionalCostPriceList
        buildCarRateDetail(carProduct, nodeArisRifcsRsp, statusCode, eMsg);

        /*todo TravelerList and Customer all copy from request
        * <xsl:copy-of select="$request/travel:TravelerList"/>
        *<xsl:copy-of select="$request/travel:Customer"/>-------from XSLT
        */
        // 10. Car TravelerList
        //buildTravelerInfo(reservation, nodeApamPnrAme1Rsp, eMsg);

        /// 11. Customer
         // buildCustomer(reservation, reserveReq);


        /// 12 ReferenceList
        buildReferenceList(reservation, nodeAcsqCarSellRsp, nodeApcmPnrAme2Rsp, eMsg);
        /// 8 Build_AdvisoryTextList
        //String test = TransferTypeUtil.ObjToStr(reservation);
        //Console.WriteLine(test);

        //9 build CarPolicyList
        try {
            arisRsp.buildCarPolicyList(carProduct, nodeArisRifcsRsp);
        } catch (Exception e) {
            eMsg.append("Faild when build CarPolicyList for ARIS response").append(e.getMessage()); //+ e.Source
        }
    }

    private void buildCarsInventory(CarProductType carProduct, CarsSCSDataSource scsDataSource,
                                    Node nodeAcsqCarSellReq, Node nodeAcsqCarSellRsp, StringBuffer eMsg) {
        try {
            final ACSQReq acsqReq = new ACSQReq();
            acsqReq.buildCarInventory(carProduct, nodeAcsqCarSellReq, scsDataSource);

            final ACSQRsp acsqRsp = new ACSQRsp();
            acsqRsp.buildCarInventory(carProduct, nodeAcsqCarSellRsp);
        } catch (Exception e) {
            eMsg.append("Faild when build carInventory ").append(e.getMessage());
        }
    }

    private void buildPickupOrDropoffLocation(CarProductType carProduct,
                                              Node nodeArisRifcsRsp, String statusCode, StringBuffer eMsg) {
        try {
            final ARISRsp arisRsp = new ARISRsp();
            arisRsp.buildPickupAndDropoffLocation(carProduct, nodeArisRifcsRsp, statusCode);
        } catch (Exception e) {
            eMsg.append("Faild when build Pickup or DropoffLocation for ARIS response").append(e.getMessage()); //+ e.Source
        }
    }

    private void buildCarVehicleOption(CarProductType carProduct, Node nodeArisRifcsRsp, StringBuffer eMsg) {
        try {
            final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            commonNodeReader.buildCarVehicleOptionForReserve(carProduct, nodeArisRifcsRsp);
        } catch (Exception e) {
            eMsg.append("Faild when build CarVehicleOption ").append(e.getMessage());
        }
    }

    private void buildSpecialEquiomentList(CarReservationType carReservationType, Node nodeArisRifcsRsp, CarsSCSDataSource scsDataSource, StringBuffer eMsg) {
        try {
            final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
            commonNodeReader.buildSpecialEquipmentForReserve(carReservationType, nodeArisRifcsRsp, scsDataSource);
        } catch (Exception e) {
            eMsg.append("Faild when build CarVehicleOption ").append(e.getMessage());
        }
    }

    private void buildCarCostList(CarProductType carProduct, Node nodeArisRifcsRsp, String statusCode, StringBuffer eMsg) {
        try {
            if (null != statusCode && statusCode.equals("HK")) {
                final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                commonNodeReader.buildCostList(carProduct, nodeArisRifcsRsp, false, false);
            }
        } catch (Exception e) {
            eMsg.append("Faild when build Cost list ").append(e.getMessage());
        }
    }

    private void buildCarRateDetail(CarProductType carProduct,
                                    Node nodeArisRifcsRsp, String statusCode, StringBuffer eMsg) {
        try {
            if (null != statusCode && statusCode.equals("HK")) {
                final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                commonNodeReader.buildCarRateDetail(carProduct, nodeArisRifcsRsp);
            }
        } catch (Exception e) {
            eMsg.append("Faild when build Car Rate Detail Conditional Price list ").append(e.getMessage());
        }
    }

    /*private void buildTravelerInfo(CarReservationType reservation,
                                   Node nodeApamPnrAme1Rsp, StringBuffer eMsg) {
        try {
            final APAMRsp apamRsp = new APAMRsp();
            apamRsp.buildTravelerInfo(reservation, nodeApamPnrAme1Rsp);
        } catch (Exception e) {
            eMsg.append("Faild when Build TravelerInfo ").append(e.getMessage());
        }
    }*/

    private void buildReferenceList(CarReservationType reservation,
                                    Node nodeAcsqCarSellRsp, Node nodeApcmPnrAme2Rsp, StringBuffer eMsg) {
        try {
            final APCMRsp apcmRsp = new APCMRsp();
            apcmRsp.buildReferenceListPNR(reservation, nodeApcmPnrAme2Rsp);

            final ACSQRsp acsqRsp = new ACSQRsp();
            acsqRsp.buildReferenceList(reservation, nodeAcsqCarSellRsp);
            acsqRsp.buildBookingStateCode(reservation, nodeAcsqCarSellRsp);
        } catch (Exception e) {
            eMsg.append("Faild when build referene list ").append(e.getMessage());
        }
    }

    //TODO common can't refer scs obj so should add while use this object.
//    public static void buildCustomer(CarReservationType reservation, CarSupplyConnectivityReserveRequest request)
//    {
//        reservation.setCustomer(request).Customer = request.Customer;
//    }
}
