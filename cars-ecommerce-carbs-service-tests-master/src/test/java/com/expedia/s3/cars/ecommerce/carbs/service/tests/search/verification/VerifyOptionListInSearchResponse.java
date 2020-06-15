package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.OptionListCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
public class VerifyOptionListInSearchResponse implements IVerification<SearchVerificationInput, BasicVerificationContext> {
    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarSearchResultType> carListInResponse = input.getResponse().getCarSearchResultList().getCarSearchResult();
        if (carListInResponse.isEmpty()) {
            return false;
        } else {
            return null != carListInResponse.get(0).getCarProductList()
                    && null != carListInResponse.get(0).getCarProductList().getCarProduct()
                    && !carListInResponse.get(0).getCarProductList().getCarProduct().isEmpty();
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws DataAccessException
    {
        boolean isPassed = false;
       final ArrayList remarks = new ArrayList();
        //select one expect car to verify
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final CarProductType selectedCarProduct = inventoryHelper.selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList(
                input.getResponse().getCarSearchResultList(), verificationContext.getScenario().getBusinessModel(), verificationContext.getScenario().getServiceProviderID(), false);

        final OptionListCommonVerifier optionListCommonVerifier = new OptionListCommonVerifier();
        optionListCommonVerifier.verifyOptionListInSearchResult(input.getRequest().getCarECommerceSearchCriteriaList().getCarECommerceSearchCriteria().get(0).getCurrencyCode(),
                selectedCarProduct, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT);


        if (CollectionUtils.isEmpty(remarks))
        {
            isPassed = true;}

        return new VerificationResult(getName(), isPassed, remarks);
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
