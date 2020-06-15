package com.expedia.s3.cars.supplyconnectivity.micronnexus.service.verification.getdetail;

import com.expedia.e3.data.cartypes.defn.v5.CarPolicyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRRRsp;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.GetDetailsVerificationInput;
import com.expedia.s3.cars.framework.test.supplyconnectivity.verification.getdetails.IGetDetailsVerification;
import com.expedia.s3.cars.supplyconnectivity.messages.getdetails.defn.v4.CarSupplyConnectivityGetDetailsResponseType;
import com.expedia.s3.cars.supplyconnectivity.micronnexus.service.utils.SettingsProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by fehu on 6/26/2017.
 */
public class GetDetailsMapVerification implements IGetDetailsVerification {
    final private Logger logger = Logger.getLogger(GetDetailsMapVerification.class.getName());

    public VerificationResult verifyForMap(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) throws Exception {


            final CarSupplyConnectivityGetDetailsResponseType getDetailsResponseType = getDetailsVerificationInput.getResponse();
            final Node vehRateRuleRSNode = verificationContext.getSpooferTransactions().getElementsByTagNameNS("*", "VehRateRuleRS").item(0);
            final VRRRsp vrrRsp = new VRRRsp(vehRateRuleRSNode, new CarsSCSDataSource(SettingsProvider.CARMNSCSDATASOURCE));

            final CarProductType expectCarProduct = vrrRsp.getCarProduct();
            final CarProductType actualCarProduct = getDetailsResponseType.getCarProductList().getCarProduct().get(0);
            logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expectCarProduct)));
            logger.info(PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actualCarProduct)));
            final List<String> errorMsg = new ArrayList<>();

            //Response Map (costList/carVehicleOptionList)
            CarProductComparator.isCarProductEqual(expectCarProduct, actualCarProduct, errorMsg,
                    Arrays.asList(CarTags.LEGACY_FINANCE_KEY, CarTags.CAR_INVENTORY_KEY, CarTags.CAR_POLICY_LIST));
            //PaymentRule and  ArrivalInfo
            verifyCarPolicy(expectCarProduct, actualCarProduct, errorMsg);


            if (CollectionUtils.isNotEmpty(errorMsg)) {
                return new VerificationResult(this.getName(), false, errorMsg);
            }

            return new VerificationResult(this.getName(), true, Arrays.asList(new String[]{"Map Successful"}));
        }


    private void verifyCarPolicy(CarProductType expectCarProduct, CarProductType actualCarProduct, List<String> errorMsg) {

        final List<CarPolicyType> acturalCarPolicys = actualCarProduct.getCarPolicyList().getCarPolicy();
        final List<CarPolicyType> expecteCarPolicys = expectCarProduct.getCarPolicyList().getCarPolicy();

        for(final CarPolicyType  expecteCarPolicy : expecteCarPolicys)
        {
            int count = 0;
            for(final CarPolicyType acturalCarPolicy : acturalCarPolicys)
            {

                if (expecteCarPolicy.getCarPolicyCategoryCode().equalsIgnoreCase(acturalCarPolicy.getCarPolicyCategoryCode())
                        && expecteCarPolicy.getCarPolicyRawText().equalsIgnoreCase(acturalCarPolicy.getCarPolicyRawText())) {

                    count ++;
                    break;
                 }
            }
            if (0 == count)
            {
                errorMsg.add("expectPaymentRule is not exist in acturalPamentRule, expectPaymentRule :" + expecteCarPolicy.getCarPolicyCategoryCode()
                + " " + expecteCarPolicy.getCarPolicyRawText());
            }

        }

    }

    @Override
    public VerificationResult verify(GetDetailsVerificationInput getDetailsVerificationInput, BasicVerificationContext verificationContext) {
        return null;
    }
}
