package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by miawang on 9/11/2017.
 */
public class GetDetailsCarRateQualifierPassVerification implements IGetDetailsVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public IVerification.VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        if (remarks.size() > 0)
        {
            return new IVerification.VerificationResult(getName(), isPassed, remarks);
        } else
        {
            try
            {
                verifyCarRateQualifierIsPassing(input.getResponse().getCarProductList().getCarProduct().get(0), verificationContext, remarks);
            } catch (DataAccessException e)
            {
                remarks.add("GetDetailsCarRateQualifierPass verification failed. error message is like below: " + e.getMessage());
                e.printStackTrace();
            }
            if (remarks.size() > 0)
            {
                remarks.add("GetDetailsCarRateQualifierPass verification failed.");
                return new IVerification.VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }
        return new IVerification.VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private void verifyCarRateQualifierIsPassing(CarProductType getDetailsRspCar, BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        Node acaqRspNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.SEARCH, GDSMsgNodeTags.RESPONSE);
        NodeList gdsNodeReqList = ASCSGDSMsgReadHelper.getSpecifyNodeListFromSpoofer(verificationContext, GDSMsgNodeTags.REQUEST);

        if (null != acaqRspNode && gdsNodeReqList.getLength() > 1)
        {
            Node ariaReqNode = gdsNodeReqList.item(1);

            ACAQRsp acaqRsp = new ACAQRsp(acaqRspNode, null , scsDataSource);

            boolean findCorrespondingCar = false;
            for(CarProductType gdsCar : acaqRsp.getGdsCarProductList())
            {
                if(CarProductComparator.isCorrespondingCar(gdsCar, getDetailsRspCar, false, false))
                {
                    findCorrespondingCar = true;
                    final String carRateQualifierInARIARequest = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(ariaReqNode, "rateInfo"), "rateType").getTextContent();

                    if(!gdsCar.getCarInventoryKey().getCarRate().getCarRateQualifierCode().equals(carRateQualifierInARIARequest))
                    {
                        remarks.add("CarRateQualifier In ARIA request : "+ carRateQualifierInARIARequest +
                                " is not the value in ACAQ response : "+ gdsCar.getCarInventoryKey().getCarRate().getCarRateQualifierCode() +", please check.");
                    }
                    break;
                }
            }
            if(!findCorrespondingCar)
            {
                remarks.add("Do not find CorrespondingCar In ACAQ Response, please check.");
            }
        } else
        {
            remarks.add("Not Find ACAQ or ARIA Response In GDS Msg");
        }
    }
}