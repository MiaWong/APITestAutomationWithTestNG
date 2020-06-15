package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.book;

import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentListType;
import com.expedia.e3.data.cartypes.defn.v5.CarSpecialEquipmentType;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRSRsp;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by fehu on 4/6/2017.
 */
@SuppressWarnings("PMD")
public class SpecialEquipVerification implements IReserveVerification{
    @Override
    public VerificationResult verify(ReserveVerificationInput reserveVerificationInput, BasicVerificationContext verificationContext) {

        final StringBuffer errorMsg = new StringBuffer();
        Assert.assertNotNull(reserveVerificationInput.getResponse());
        Assert.assertNotNull(reserveVerificationInput.getResponse().getCarReservation());
        Assert.assertNotNull(reserveVerificationInput.getResponse().getCarReservation().getCarProduct());

        final VRSRsp vrsRsp = new VRSRsp(verificationContext.getSpooferTransactions().getDocumentElement().getElementsByTagName("Response").item(0), new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));

        //1,Verify special equipment info in VRS response is passed to CarSCS Reserve response.
         errorMsg.append(compareCarSpecialEquipmentList(reserveVerificationInput, vrsRsp));

        //2.Verify carSpecialEquipmentWarning
        if (CollectionUtils.isNotEmpty(vrsRsp.getErrors()))
        {
             if (null == reserveVerificationInput.getResponse().getErrorCollection().getSpecialEquipmentNotAvailableError() || null == reserveVerificationInput.getResponse().getErrorCollection().getSpecialEquipmentNotAvailableError().getDescriptionRawText())
             {
                 errorMsg.append("There is no SpecialEquipmentNotAvailableError returned in CarSCS reserve response when the warning of special equipment returned in VRS response.\n");
             }
             else {
                    final String waringMsg = reserveVerificationInput.getResponse().getErrorCollection().getSpecialEquipmentNotAvailableError().getDescriptionRawText();
                    final String[] warningMsgs = waringMsg.split(",");
                    if ((warningMsgs.length - 2) != vrsRsp.getErrors().size()) {
                        errorMsg.append("The special equipment returned in SpecialEquipmentNotAvailableError.DescriptionRawText is incorrect: actual message is " + waringMsg + ", the expected is " + String.join("," , (Iterable<? extends CharSequence>) vrsRsp.getErrors().iterator()));
                    }
                    for (final String spEqExpected : vrsRsp.getErrors())
                    {
                        boolean exist = false;
                        for (final String spEqActual : warningMsgs)
                        {
                            if (spEqActual.trim().equalsIgnoreCase(spEqExpected.trim()))
                            {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist)
                        {
                            errorMsg.append(" The expected Special Equipment : " + spEqExpected + " for warning is not in SpecialEquipmentNotAvailableError.DescriptionRawText : " +waringMsg);
                        }
                    }
                }
            }

     if (StringUtil.isNotBlank(errorMsg.toString()))
     {
         return new VerificationResult("SpecialEquipVerification", false, Arrays.asList("failed",errorMsg.toString()));

     }
        return new VerificationResult("SpecialEquipVerification", true, Arrays.asList("true"));

    }

    private StringBuffer  compareCarSpecialEquipmentList(ReserveVerificationInput reserveVerificationInput, VRSRsp vrsRsp ) {

        final CarSpecialEquipmentListType actualCarSpecialEquipmentListType = reserveVerificationInput.getResponse().getCarReservation().getCarSpecialEquipmentList();
        final CarSpecialEquipmentListType expectCarSpecialEquipmentType = vrsRsp.getCarReservationType().getCarSpecialEquipmentList();

        final StringBuffer errorMsg =new StringBuffer();

        if (null == actualCarSpecialEquipmentListType && null != expectCarSpecialEquipmentType &&
                CollectionUtils.isNotEmpty(expectCarSpecialEquipmentType.getCarSpecialEquipment()))
        {
            errorMsg.append("The actual count of CarSpecialEquipmentList is not equal to expected");
            return errorMsg;
        }

        if (null == expectCarSpecialEquipmentType && null != actualCarSpecialEquipmentListType
                    && CollectionUtils.isNotEmpty(actualCarSpecialEquipmentListType.getCarSpecialEquipment()))
        {
            errorMsg.append("The actual count of CarSpecialEquipmentList is not equal to expected");
            return errorMsg;
        }

        if (null != actualCarSpecialEquipmentListType && null != expectCarSpecialEquipmentType)
        {
           if (CollectionUtils.isNotEmpty(actualCarSpecialEquipmentListType.getCarSpecialEquipment()) && CollectionUtils.isNotEmpty(expectCarSpecialEquipmentType.getCarSpecialEquipment())) {
               if (actualCarSpecialEquipmentListType.getCarSpecialEquipment().size() != expectCarSpecialEquipmentType.getCarSpecialEquipment().size()) {
                   errorMsg.append("The actual count of CarSpecialEquipmentList= " + actualCarSpecialEquipmentListType.getCarSpecialEquipment().size() + "is not the expected=" + expectCarSpecialEquipmentType.getCarSpecialEquipment().size());

               }

               for(final CarSpecialEquipmentType expectCarSpecialEquipment :expectCarSpecialEquipmentType.getCarSpecialEquipment())
               {
                   boolean exist = false;
                   for(final CarSpecialEquipmentType actualCarSpecialEquipmentType : actualCarSpecialEquipmentListType.getCarSpecialEquipment())
                   {

                       if (actualCarSpecialEquipmentType.getCarSpecialEquipmentCode().equals(expectCarSpecialEquipment.getCarSpecialEquipmentCode()))
                       {
                           exist = true;
                           break;
                       }
                   }
                   if (!exist) {
                       errorMsg.append("The expected CarSpecialEquipmentCode: " + expectCarSpecialEquipment.getCarSpecialEquipmentCode() + " can't be found in actual CarSpecialEquipmentList.\n");
                   }
               }
           }
        }

        return errorMsg;
    }
}
