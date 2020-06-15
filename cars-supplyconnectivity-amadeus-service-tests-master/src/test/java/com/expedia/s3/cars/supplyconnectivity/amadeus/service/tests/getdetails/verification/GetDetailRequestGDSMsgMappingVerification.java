package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getdetails.verification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ARIAReq;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.CommonVerification.IsCDCodePassInGDSRequestCorrectCommonVerifier;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by miawang on 8/30/2016.
 *
 * 2017-10-12 add automation for verify cd code in search request CASSS-7027 - Amadeus SCS XSLT support for CD code(Shopping/Booking)
 */
public class GetDetailRequestGDSMsgMappingVerification implements IGetDetailsVerification
{
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public VerificationResult verify(GetDetailsVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            if (null != input.getRequest().getCarProductList() && null != input.getRequest().getCarProductList().getCarProduct())
            {
                Node ariaReqNode = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETDETAILS, GDSMsgNodeTags.AmadeusNodeTags.ARIA_CAR_GET_DETAIL_REQUEST_TYPE);
                IsCDCodePassInGDSRequestCorrectCommonVerifier cdCodePassCommonVerifier = new IsCDCodePassInGDSRequestCorrectCommonVerifier();
                cdCodePassCommonVerifier.verify(input.getRequest().getCarProductList().getCarProduct().get(0),
                        getVendorDiscountNumsFromGdsMsg(ariaReqNode, remarks), remarks);

                //Verify PointOfSupplyCurrencyCode
                final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                final String gdsReqCurrency = commonNodeReader.readCurrencyCode(ariaReqNode);
                final String ascsPointOfSupplyCurrency = input.getRequest().getCarProductList().getCarProduct().get(0).getPointOfSupplyCurrencyCode();
                if(!gdsReqCurrency.equals(ascsPointOfSupplyCurrency))
                {
                    remarks.add(String.format("GDS request currency code(%s) is not equal to amadeus request PointOfSupplyCurrencyCode(%s)",
                            gdsReqCurrency, ascsPointOfSupplyCurrency));
                }
            }
        } catch (DataAccessException e)
        {
            e.printStackTrace();
        }

        if (remarks.size() > 0)
        {
            remarks.add("GetDetails Request GDS Msg verification failed.\n");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private Map<Long, String> getVendorDiscountNumsFromGdsMsg(Node ariaReqNode, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        if (ariaReqNode != null) {
            ARIAReq ariaReq = new ARIAReq();
            return ariaReq.buildVendorDiscountNums(ariaReqNode, scsDataSource);
        } else {
            remarks.add("Not Find ARIA Response In GDS Msg");
        }

        return null;
    }
}