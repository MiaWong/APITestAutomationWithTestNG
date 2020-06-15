package com.expedia.s3.cars.ecommerce.carbs.service.verification.omgetchangedetail;

import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by fehu on 11/10/2016.
 */
@SuppressWarnings("PMD")
public class OmGetChangeDetailBasicVerification implements IVerification<GetChangeDetailsVerificationInput, BasicVerificationContext> {
    private static final String MESSAGE_SUCCESS = "Success";

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public VerificationResult verify(GetChangeDetailsVerificationInput input, BasicVerificationContext basicVerificationContext) {

       final StringBuffer errorMsg = new StringBuffer();

        if (null == input.getResponse()) {
            errorMsg.append("Returned response is equal null.");

        }
        if (null != input.getResponse().getGetChangeDetailErrorCollection()) {
          final List<String> descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(input.getResponse().getGetChangeDetailErrorCollection(), "DescriptionRawText");
            if (CollectionUtils.isNotEmpty(descriptionRawTextList)) {
                errorMsg.append("Exist error in response: ");
                for (final String descriptionRawText : descriptionRawTextList) {
                    errorMsg.append(descriptionRawText);
                }
            }
        }

        if (null != input.getResponse()
                && null != input.getResponse().getChangeDetailTargetData()
                && (null == input.getResponse().getChangeDetailTargetData().getTotalAmountWithTax().getSimpleAmount() ||null == input.getResponse().getChangeDetailTargetData().getTotalAmountWithTax().getCurrencyCode()))
        {
            errorMsg.append("GetChangeDetail response is not null but amount or currency code is null");
        }

        if (StringUtils.isEmpty(errorMsg.toString())) {
            return new VerificationResult(getName(), true, Arrays.asList(MESSAGE_SUCCESS));
        }
        return new VerificationResult(getName(), false, Arrays.asList(errorMsg.toString()));

    }
}
