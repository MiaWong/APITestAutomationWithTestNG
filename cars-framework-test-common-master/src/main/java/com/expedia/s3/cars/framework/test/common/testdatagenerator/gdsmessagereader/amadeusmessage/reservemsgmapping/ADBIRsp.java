package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.cartypes.defn.v5.DescriptiveBillingInfoListType;
import com.expedia.e3.data.cartypes.defn.v5.DescriptiveBillingInfoType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 2/24/2017.
 */
@SuppressWarnings("CPD-START")
public class ADBIRsp {
    public DescriptiveBillingInfoListType getDescriptiveBillingInfoList(Node adbiResponse) {
        final DescriptiveBillingInfoListType descriptiveBillingInfoList = new DescriptiveBillingInfoListType();

        if (adbiResponse != null) {
            //AmadeusSessionManagerResponse/RawAmadeusXml/PAY_ManageDBIDataReply/attributeData[1]/statusDetails
            //AmadeusSessionManagerResponse/RawAmadeusXml/PAY_ManageDBIDataReply/attributeData[5]/otherStatusDetails/description
            final List<Node> attributeDataList = PojoXmlUtil.getNodesByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiResponse, "PAY_ManageDBIDataReply"),
                    "attributeData");

            if (null != attributeDataList && !attributeDataList.isEmpty()) {
                buildDescriptiveBillingInfo(descriptiveBillingInfoList, attributeDataList);
            }
        }
        return descriptiveBillingInfoList;
    }

    private void buildDescriptiveBillingInfo(DescriptiveBillingInfoListType descriptiveBillingInfoList, List<Node> attributeDataList) {
        if (null == descriptiveBillingInfoList.getDescriptiveBillingInfo()) {
            descriptiveBillingInfoList.setDescriptiveBillingInfo(new ArrayList<>());
        }
        for (final Node attributeData : attributeDataList) {
            //AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/attributeData[1]/statusDetails/description
            final Node indicator = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"), "indicator");

            if (indicator != null) {
                final Node value = PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"),
                        "description");

                final Node description = PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(attributeData, "otherStatusDetails"),
                        "description");

                final DescriptiveBillingInfoType descriptiveBillingInfo = new DescriptiveBillingInfoType();
                descriptiveBillingInfo.setKey(indicator.getTextContent());
                if (value != null) {
                    descriptiveBillingInfo.setDescription(value.getTextContent());
                }
                if (description != null) {
                    descriptiveBillingInfo.setValue(description.getTextContent());
                }
                descriptiveBillingInfoList.getDescriptiveBillingInfo().add(descriptiveBillingInfo);
            }
        }
    }

    public CreditCardType getCreditCard(Node adbiResponse, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final CreditCardType creditCard = new CreditCardType();
        //AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/formOfPayment/formOfPayment/type
        if (adbiResponse != null) {
            final Node type = PojoXmlUtil.getNodeByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiResponse, "formOfPayment"),
                    "type");

            buildCreditCard(creditCard, adbiResponse, type, scsDataSource);
        }
        return creditCard;
    }
    @SuppressWarnings("CPD-END")
    private void buildCreditCard(CreditCardType creditCard, Node adbiResponse, Node type, CarsSCSDataSource scsDataSource) throws DataAccessException {
        if (type != null && type.getTextContent().equals("CC")) {
            final Node vendorCode = PojoXmlUtil.getNodeByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiResponse, "formOfPayment"),
                    "vendorCode");
            if (null != vendorCode) {
                creditCard.setCreditCardSupplierCode(GDSMsgReadHelper.getDomainValueByDomainTypeAndExternalDomainValue
                        (CommonConstantManager.DomainType.CREDIT_CARD_TYPE, vendorCode.getTextContent(), scsDataSource));
            }

            final Node creditCardNumber = PojoXmlUtil.getNodeByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiResponse, "formOfPayment"),
                    "creditCardNumber");
            if (null != creditCardNumber) {
                creditCard.setCreditCardNumberEncrypted(creditCardNumber.getTextContent());
            }
        }
    }
}