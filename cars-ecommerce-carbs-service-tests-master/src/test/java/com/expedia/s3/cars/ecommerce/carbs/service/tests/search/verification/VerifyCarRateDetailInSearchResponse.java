package com.expedia.s3.cars.ecommerce.carbs.service.tests.search.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarSearchResultType;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.CarRateDetailCommonVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.search.SearchVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonErrorMsgs;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.GDSMsgNodeTags;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.titaniummessage.TVARRsp;
import com.expedia.s3.cars.framework.test.common.verification.CarProductComparator;
import org.apache.commons.collections.CollectionUtils;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 8/30/2016.
 */
public class VerifyCarRateDetailInSearchResponse implements IVerification<SearchVerificationInput, BasicVerificationContext> {
    DataSource carsInventoryDatasource;
    DataSource titaniumDatasource;

    public void setCarsInventoryDatasource(DataSource carsInventoryDatasource) {
        this.carsInventoryDatasource = carsInventoryDatasource;
    }

    public void setTitaniumDatasource(DataSource titaniumDatasource) {
        this.titaniumDatasource = titaniumDatasource;
    }


    @Override
    public boolean shouldVerify(SearchVerificationInput input, BasicVerificationContext verificationContext) {
        final List<CarSearchResultType> searchResult = input.getResponse().getCarSearchResultList().getCarSearchResult();
        if (searchResult.isEmpty()) {
            return false;
        } else {
            return searchResult.get(0).getCarProductList() != null ||
                    searchResult.get(0).getCarProductList().getCarProduct() != null;
        }
    }

    //compare the response with the request.
    @Override
    public VerificationResult verify(SearchVerificationInput input, BasicVerificationContext verificationContext) throws Exception {
        boolean isPassed = false;
        final List remarks = new ArrayList();

        final CarRateDetailCommonVerifier commonVerifier = new CarRateDetailCommonVerifier();
        if (carsInventoryDatasource == null) {
            remarks.add("carsInventoryDatasource is Null, need initial first");
        } else if (titaniumDatasource == null) {
            remarks.add("titaniumDatasource is Null, need initial first");
        }

        if (CollectionUtils.isNotEmpty(remarks))
        {
            return new VerificationResult(getName(), isPassed, remarks);
        }
        else
        {
            final CarsSCSDataSource scsDataSource = new CarsSCSDataSource(titaniumDatasource);
            final NodeList tvarRsps = verificationContext.getSpooferTransactions().getElementsByTagName(GDSMsgNodeTags.TitaniumNodeTags.TVAR_RESPONSE_TYPE);
            final List<CarProductType> gdsCarproductList = new ArrayList<>();
            if (tvarRsps != null)
            {
                for (int i = 0; i < tvarRsps.getLength(); i++)
                {
                    final TVARRsp tvarRsp = new TVARRsp(tvarRsps.item(i), scsDataSource, true, false);
                    gdsCarproductList.addAll(tvarRsp.getCarProduct().getCarProduct());
                }

               /*for (final CarSearchResultType carSearchResult : searchResult)
                {
                    for (final CarProductType car : carSearchResult.getCarProductList().getCarProduct())
                    {
                        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);
                        if (inventoryHelper.isSpecificProviderCar(car, 7))
                        {
                            commonVerifier.verifyCarRateDetailCostNoCompareWithRequest(carsInventoryDatasource,
                                    getGDSMsg(gdsCarproductList, car, remarks), car, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT);
                        }
                    }
                }*/
               //select one expect car to verify
                final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);
                final CarProductType selectedCarProduct = inventoryHelper.selectCarByBusinessModelIDAndServiceProviderIDFromCarSearchResultList(
                        input.getResponse().getCarSearchResultList(), verificationContext.getScenario().getBusinessModel()
                        , verificationContext.getScenario().getServiceProviderID(), false);
                commonVerifier.verifyCarRateDetailCostNoCompareWithRequest(carsInventoryDatasource,
                        getGDSMsg(gdsCarproductList, selectedCarProduct, remarks), selectedCarProduct, remarks, CommonErrorMsgs.ErrorMsgHeaders.MESSAGE_SEARCH_VERIFICATION_PROMPT);



            }
        }

        if (CollectionUtils.isEmpty(remarks))
        {
            isPassed = true;
        }

        return new VerificationResult(getName(), isPassed, remarks);
    }


    private CarProductType getGDSMsg(List<CarProductType> gdsCarproductList, CarProductType responseCar, List remarks) throws DataAccessException {


        for (final CarProductType carProductType : gdsCarproductList)
        {
            if (CarProductComparator.isCorrespondingCar(responseCar, carProductType, false, false))
            {
                return carProductType;
            }
        }

        remarks.add("Not get GDS car for Search, car type is " + responseCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()
        + ","+responseCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode()
        + ","+responseCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode()
        + ","+responseCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()
        + "/"+responseCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID());

        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}