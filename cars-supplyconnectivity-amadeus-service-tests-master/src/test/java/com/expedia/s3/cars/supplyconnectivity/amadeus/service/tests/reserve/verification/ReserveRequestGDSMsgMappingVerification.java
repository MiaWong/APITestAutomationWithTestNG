package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.reserve.verification;

import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.AmadeusCommonNodeReader;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping.ACSQReq;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.IReserveVerification;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.reserve.ReserveVerificationInput;
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
public class ReserveRequestGDSMsgMappingVerification implements IReserveVerification
{
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    @Override
    public VerificationResult verify(ReserveVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        try
        {
            if (null != input.getRequest().getCarProduct())
            {
                //Verify CD code sent
                final Node node_ACSQ_CAR_SELL_REQUEST = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext,
                        CommonConstantManager.ActionType.RESERVE, GDSMsgNodeTags.AmadeusNodeTags.ACSQ_CAR_SELL_REQUEST_TYPE);
                IsCDCodePassInGDSRequestCorrectCommonVerifier cdCodePassCommonVerifier = new IsCDCodePassInGDSRequestCorrectCommonVerifier();
                cdCodePassCommonVerifier.verify(input.getRequest().getCarProduct(), getVendorDiscountNumsFromGdsMsg(node_ACSQ_CAR_SELL_REQUEST, remarks), remarks);

                //Verify PointOfSupplyCurrencyCode
                final AmadeusCommonNodeReader commonNodeReader = new AmadeusCommonNodeReader();
                final String gdsReqCurrency = commonNodeReader.readCurrencyCode(node_ACSQ_CAR_SELL_REQUEST);
                final String ascsPointOfSupplyCurrency = input.getRequest().getCarProduct().getPointOfSupplyCurrencyCode();
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
            remarks.add("Reserve Request GDS Msg verification failed.\n");
            return new VerificationResult(this.getName(), isPassed, remarks);
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }

        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

    private Map<Long, String> getVendorDiscountNumsFromGdsMsg(Node node_ACSQ_CAR_SELL_REQUEST, ArrayList remarks) throws DataAccessException
    {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        if (node_ACSQ_CAR_SELL_REQUEST != null)
        {
            final ACSQReq acsqReq = new ACSQReq();
            return acsqReq.buildVendorDiscountNums(node_ACSQ_CAR_SELL_REQUEST, scsDataSource);
        } else
        {
            remarks.add("Not Find ACSQ Request In GDS Msg");
        }

        return null;
    }
}