package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 9/20/2017.
 */
public class IfUpgradeCarReturnedInRspVerifier implements IVerification<SearchVerificationInput, BasicVerificationContext>
{
    @Override
    public String getName()
    {
        return getClass().getSimpleName();
    }

    @Override
    /**
     * if request NeedUpgradeMapBoolean is true, verify if there is Upgrade Car return.
     */
    public VerificationResult verify(SearchVerificationInput searchVerificationInput, BasicVerificationContext verificationContext)
    {
        final List<CarSearchResultType> searchResult = searchVerificationInput.getResponse().getCarSearchResultList().getCarSearchResult();

        boolean isPassed = false;
        final List remarks = new ArrayList();

        if (searchVerificationInput.getRequest().getCarECommerceSearchStrategy().isNeedUpgradeMapBoolean())
        {
            boolean getUpgradeCar = false;
            for (final CarSearchResultType carSearchResult : searchResult)
            {
                for (final CarProductType car : carSearchResult.getCarProductList().getCarProduct())
                {
                    if (null != car.getUpgradeCarProductTokenList() && null != car.getUpgradeCarProductTokenList().getCarProductToken() &&
                            !car.getUpgradeCarProductTokenList().getCarProductToken().isEmpty())
                    {
                        getUpgradeCar = true;
                    }
                }
            }

            if (!getUpgradeCar)
            {
                remarks.add("Do not get any upgrade car while NeedUpgradeMapBoolean in search request is true, please check.");
            }
        }

        if (CollectionUtils.isEmpty(remarks))
        {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }
}