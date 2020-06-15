package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.reservemsgmapping;

import com.expedia.e3.data.cartypes.defn.v5.DescriptiveBillingInfoListType;
import com.expedia.e3.data.cartypes.defn.v5.DescriptiveBillingInfoType;
import com.expedia.e3.data.financetypes.defn.v4.CreditCardType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common.GDSMsgReadHelper;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by miawang on 2/24/2017.
 */
public class ADBIReq {
    public DescriptiveBillingInfoListType getDescriptiveBillingInfoList(Node adbiRequest) {
        final DescriptiveBillingInfoListType descriptiveBillingInfoList = new DescriptiveBillingInfoListType();

        if (adbiRequest != null) {
            ///AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/attributeData[1]/statusDetails/indicator
            final List<Node> attributeDataList = PojoXmlUtil.getNodesByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiRequest, "PAY_ManageDBIData"),
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
            ///AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/attributeData[1]/statusDetails/description
            final Node indicator = PojoXmlUtil.getNodeByTagName(
                    PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"),
                    "indicator");
            if (indicator != null) {
                final Node description = PojoXmlUtil.getNodeByTagName(
                        PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"),
                        "description");

                final DescriptiveBillingInfoType descriptiveBillingInfo = new DescriptiveBillingInfoType();
                descriptiveBillingInfo.setKey(indicator.getTextContent());
                if (description != null) {
                    descriptiveBillingInfo.setValue(description.getTextContent());
                }
                descriptiveBillingInfoList.getDescriptiveBillingInfo().add(descriptiveBillingInfo);
            }
        }
    }

    public CreditCardType getCreditCard(Node adbiRequest, CarsSCSDataSource scsDataSource) throws DataAccessException {
        final CreditCardType creditCard = new CreditCardType();
        //AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/formOfPayment/formOfPayment/type

        if (adbiRequest != null) {
            final Node formOfPaymentNode = PojoXmlUtil.getNodeByTagName(adbiRequest, "formOfPayment");
            final Node type = PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "type");

            buildCreditCard(creditCard, type, formOfPaymentNode, scsDataSource);
        }
        return creditCard;
    }

    private void buildCreditCard(CreditCardType creditCard, Node type, Node formOfPaymentNode, CarsSCSDataSource scsDataSource) throws DataAccessException {
        if (null != type && type.getTextContent().equals("CC")) {
            final Node vendorCode = PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "vendorCode");
            if (vendorCode != null) {
                creditCard.setCreditCardSupplierCode(GDSMsgReadHelper.getDomainValueByDomainTypeAndExternalDomainValue
                        (CommonConstantManager.DomainType.CREDIT_CARD_TYPE, vendorCode.getTextContent(), scsDataSource));
            }

            final Node creditCardNumber = PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "creditCardNumber");
            if (creditCardNumber != null) {
                creditCard.setCreditCardNumberEncrypted(creditCardNumber.getTextContent());
            }

            final Node expiryDate =
                    PojoXmlUtil.getNodeByTagName(formOfPaymentNode, "expiryDate");
            if (expiryDate != null) {
                final Calendar time = Calendar.getInstance();
                time.add(Calendar.MONTH, -time.MONTH + Integer.parseInt(expiryDate.getTextContent().substring(0, 2)));
                time.add(Calendar.YEAR, Integer.parseInt(expiryDate.getTextContent().substring(2)));
                creditCard.setExpirationDate(DateTime.getInstanceByDateTime(time.getTime()));
            }
        }
    }

    @SuppressWarnings("PMD")
    public String getSpecifiedDBIValueFromDBIList(Node adbiRequest, String keyName, boolean existInputedKeyInDBIList) {
        String description = "";
        if (adbiRequest != null) {
            //AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/attributeData[1]/statusDetails/indicator
            final List<Node> attributeDataList = PojoXmlUtil.getNodesByTagName(
                    PojoXmlUtil.getNodeByTagName(adbiRequest, "PAY_ManageDBIData"),
                    "attributeData");
            if (attributeDataList != null) {
                for (final Node attributeData : attributeDataList) {
                    //AmadeusSessionManagerRequest/RawAmadeusXml/PAY_ManageDBIData/attributeData[1]/statusDetails/description
                    final Node indicator = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"), "indicator");
                    if (indicator != null && indicator.getTextContent().equals(keyName)) {
                        final Node descriptionNode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(attributeData, "statusDetails"), "description");
                        if (descriptionNode != null) {
                            description = descriptionNode.getTextContent();
                            break;
                        }
                    } else {
                        continue;
                    }

                }
            }
        }
        return description;
    }
}