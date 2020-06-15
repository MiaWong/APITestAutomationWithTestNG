package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.costandavail.verification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ACAQReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.GetCostAndAvailabilityVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.costandavail.IGetCostAndAvailabilityVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.IsCDCodePassInGDSRequestCorrectCommonVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by miawang on 8/30/2016.
 *
 * 2017-10-12 add automation for verify cd code in search request CASSS-7027 - Amadeus SCS XSLT support for CD code(Shopping/Booking)
 */
public class GetCostAndAvailRequestGDSMsgMappingVerification implements IGetCostAndAvailabilityVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public VerificationResult verify(GetCostAndAvailabilityVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            if (null != input.getRequest().getCarProductList() && null != input.getRequest().getCarProductList().getCarProduct())
            {
                ACAQReq acaqReq = GetGdsMsg(verificationContext, remarks);

                IsCDCodePassInGDSRequestCorrectCommonVerifier cdCodePassCommonVerifier = new IsCDCodePassInGDSRequestCorrectCommonVerifier();
                cdCodePassCommonVerifier.verify(input.getRequest().getCarProductList().getCarProduct().get(0), acaqReq.getVendorDiscountNumsMap(), remarks);
            }
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }



        if (remarks.size() > 0)
        {
            remarks.add("Cost&Avail Request GDS Msg verification failed.\n");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private ACAQReq GetGdsMsg(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        Node acaqReqNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.SEARCH, GDSMsgNodeTags.REQUEST);

        if (acaqReqNode != null)
        {
            return new ACAQReq(acaqReqNode, scsDataSource);
        } else
        {
            remarks.add("Not Find ACAQ Request In GDS Msg");
        }

        return null;
    }
}