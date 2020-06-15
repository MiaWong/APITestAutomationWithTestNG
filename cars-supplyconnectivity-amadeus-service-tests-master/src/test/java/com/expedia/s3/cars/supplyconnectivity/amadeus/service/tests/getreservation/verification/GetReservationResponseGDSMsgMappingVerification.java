package com.expedia.s3.cars.supplyconnectivity.amadeus.service.tests.getreservation.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.DatabaseSetting;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.ASCSGDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.getresevationmapping.ASCSGetReservation;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.GetReservationVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getreservation.IGetReservationVerification;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.SettingsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by miawang on 2/14/2017.
 */
public class GetReservationResponseGDSMsgMappingVerification implements IGetReservationVerification
{
    Logger logger = Logger.getLogger(getClass());
    final private DataSource amadeusSCSDatasource = DatabaseSetting.createDataSource(SettingsProvider.DB_CARS_SCS_DATABASE_SERVER, SettingsProvider.DB_CARS_SCS_DATABASE_NAME,
            SettingsProvider.DB_USER_DOMAIN, SettingsProvider.DB_USER_NAME, SettingsProvider.DB_PASSWORD, SettingsProvider.DB_USE_PASSWORD);

    public VerificationResult verify(GetReservationVerificationInput input, BasicVerificationContext verificationContext)
    {
        ArrayList remarks = new ArrayList();
        boolean isPassed = false;

        //TODO Mia remove ignore case after add all node.
        List<String> ignoreList = new ArrayList<>();
        ignoreList.add(CarTags.SUPPLY_SUBSET_ID);
        ignoreList.add(CarTags.RATE_PERIOD_CODE);
        ignoreList.add(CarTags.CAR_DOOR_COUNT);

        //This is get from request, so ignore.
        ignoreList.add(CarTags.RATE_CODE);
        ignoreList.add(CarTags.CAR_RATE_QUALIFIER_CODE);

        if (remarks.size() > 0)
        {
            return new VerificationResult(getName(), isPassed, remarks);
        } else
        {
            try
            {
                CarReservationType rspCarReservation = input.getResponse().getCarReservationList().getCarReservation().get(0);
                CarProductType getReservationCar = rspCarReservation.getCarProduct();
                CarReservationType gdsCarReservation = getGdsCarReservation(verificationContext, remarks);
                CarProductType expCar = gdsCarReservation.getCarProduct();
                logger.warn("Expected Car: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar)));
                logger.warn("Actual Car: " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(getReservationCar)));

                //Compare CarProduct
                CarProductComparator.isCarProductEqual(expCar, getReservationCar, remarks, ignoreList);

                //Compapre other node under CarReservation
                final StringBuilder erroMsg = new StringBuilder();
                ignoreList.add("carProduct");
                ignoreList.add("age");
                gdsCarReservation.setCustomer(input.getRequest().getCarReservationList().getCarReservation().get(0).getCustomer());
                final boolean compared = CompareUtil.compareObject(gdsCarReservation, rspCarReservation, ignoreList, erroMsg);
                if(!compared){
                    remarks.add(erroMsg.toString());
                }

                //Verify warn
                verifyWarn(input, verificationContext, remarks);

            } catch (DataAccessException e)
            {
                remarks.add("GetReservationGDSMsgMap Response compare with GDS Msg verification failed. error message is like below: " + e.getMessage());
            }

            if (remarks.size() > 0)
            {
                remarks.add("GetReservationGDSMsgMap Response compare with GDS Msg verification failed.");
                return new VerificationResult(this.getName(), isPassed, remarks);
            }
        }
        if (remarks.size() < 1)
        {
            isPassed = true;
        }
        return new VerificationResult(this.getName(), isPassed, Arrays.asList(new String[]{"Success"}));
    }

//    public const String PNR_AME = "APAM";
//    public const String CAR_SELL = "ACSQ";
//    public const String RIFCS = "ARIS";
//    public const String PNR_AME2 = "APCM";

    private CarReservationType getGdsCarReservation(BasicVerificationContext verificationContext, ArrayList remarks) throws DataAccessException {
        CarsSCSDataSource scsDataSource = new CarsSCSDataSource(amadeusSCSDatasource);

        ASCSGetReservation getReservationMapping = new ASCSGetReservation();
        StringBuffer eMsg = new StringBuffer();

        CarReservationType carReservation = new CarReservationType();
        getReservationMapping.buildCarReservationCar(verificationContext, carReservation, scsDataSource, eMsg);
        if (!StringUtils.isEmpty(eMsg.toString())) {
            remarks.add(eMsg);
        }
        return carReservation;
    }

    private void verifyWarn(GetReservationVerificationInput input, BasicVerificationContext verificationContext, ArrayList remarks)
    {
        final Node node_APRQ_PNR_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETRESERVATION, GDSMsgNodeTags.AmadeusNodeTags.APRQ_PNR_RESPONSE_TYPE);
        final Node node_ARIS_RIFCS_RESPONSE = ASCSGDSMsgReadHelper.getSpecifyNodeFromSpoofer(verificationContext, CommonConstantManager.ActionType.GETRESERVATION, GDSMsgNodeTags.AmadeusNodeTags.ARIS_RIFCS_RESPONSE_TYPE);

        if((!CollectionUtils.isEmpty(PojoXmlUtil.getNodesByTagName(node_APRQ_PNR_RESPONSE, "errorWarning")) ||
                !CollectionUtils.isEmpty(PojoXmlUtil.getNodesByTagName(node_ARIS_RIFCS_RESPONSE, "errorWarning")))
            && input.getResponse().getErrorCollection().getUnclassifiedErrorList().getUnclassifiedError().isEmpty())
        {
            remarks.add("Error should exist in ASCS GetReservation response with CarProduct when Warning exist in GDS response!");
        }
    }
}
