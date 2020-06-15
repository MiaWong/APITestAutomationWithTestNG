package com.expedia.s3.cars.ecommerce.carbs.service.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.SettingsProvider;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.messages.getdetails.defn.v4.CarECommerceGetDetailsResponseType;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.PosConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarPromotion;
import com.expedia.s3.cars.framework.test.common.execution.requestgeneration.testparameter.TestData;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 9/19/2016.
 */
@SuppressWarnings("PMD")
public class CarPromotionsCommonVerifier {

    public void setCarsPromotionsPosConfigs(TestData testData, String shoppingCarVendorPromotionsEnableExpValue, String shoppingCarInclusionDetailsEnableExpValue,
                                            String shoppingCarExclusionDetailsEnableExpValue, String shoppingCarMiscellaneousInfoEnableExpValue) throws Exception {
        final PosConfigHelper posConfigHelper = new PosConfigHelper(DatasourceHelper.getCarBSDatasource(), SettingsProvider.CARBS_POS_SET_ADDRESS, SettingsProvider.ENVIRONMENT_NAME);
        posConfigHelper.setFeatureEnable(testData.getScenarios(), shoppingCarVendorPromotionsEnableExpValue, PosConfigSettingName.SHOPPING_CARVENDORPROMOTIONS_ENABLE, false);
        posConfigHelper.setFeatureEnable(testData.getScenarios(), shoppingCarInclusionDetailsEnableExpValue, PosConfigSettingName.SHOPPING_CARINCLUSIONDETAILS_ENABLE, false);
        posConfigHelper.setFeatureEnable(testData.getScenarios(), shoppingCarExclusionDetailsEnableExpValue, PosConfigSettingName.SHOPPING_CAREXCLUSIONDETAILS_ENABLE, false);
        posConfigHelper.setFeatureEnable(testData.getScenarios(), shoppingCarMiscellaneousInfoEnableExpValue, PosConfigSettingName.SHOPPING_CARMISCELLANEOUSINFO_ENABLE, false);
    }

    //get valid CarPromotion should return in response.
    private List<CarPromotion> getCarsPromotionsConfigured(TestScenario scenario, long vendorSupplierID, String requestTravelStartDateStr, String requestTravelEndDateStr, String shoppingCarVendorPromotions,
                                                           String shoppingCarInclusionDetails, String shoppingCarExclusionDetails, String shoppingCarMiscellaneousInfo) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        final List<CarPromotion> carPromotions = inventoryHelper.getCarsPromotionsConfigured(scenario, String.valueOf(vendorSupplierID));

        if (!carPromotions.isEmpty()) {
            final List<CarPromotion> results = new ArrayList<>(carPromotions);
            for (final CarPromotion cp : carPromotions) {
                //compare search start date and end date as well as travel start date and end date.
                final DateTimeFormatter f = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.s");
                final DateTime searchStartDate = f.parseDateTime(cp.getSearchStartDate());
                final DateTime searchEndDate = f.parseDateTime(cp.getSearchEndDate());
                final DateTime travelStartDate = f.parseDateTime(cp.getTravelStartDate());
                final DateTime travelEndDate = f.parseDateTime(cp.getTravelEndDate());
                final DateTime requestTravelStartDate = DateTime.parse(requestTravelStartDateStr);
                final DateTime requestTravelEndDate = DateTime.parse(requestTravelEndDateStr);
                final DateTime currentTime = new DateTime();
                if (searchStartDate.isAfter(currentTime) || currentTime.isAfter(searchEndDate) || requestTravelStartDate.isAfter(travelEndDate) || travelStartDate.isAfter(requestTravelEndDate)
                        || ("0".equals(shoppingCarVendorPromotions) && cp.getCarPromotionType().equals("VendorPromotion")) || ("0".equals(shoppingCarInclusionDetails) && cp.getCarPromotionType().equals("Inclusion"))
                        || ("0".equals(shoppingCarExclusionDetails) && cp.getCarPromotionType().equals("Exclusion")) || ("0".equals(shoppingCarMiscellaneousInfo) && cp.getCarPromotionType().equals("Miscellaneous"))) {
                    results.remove(cp);
                }
            }
            return results;
        }
        return null;
    }

    public void verifyCarPromotionsInGetDetailsResponse(CarECommerceGetDetailsResponseType response, TestData testData, String shoppingCarVendorPromotions, String shoppingCarInclusionDetails,
                                                        String shoppingCarExclusionDetails, String shoppingCarMiscellaneousInfo) throws DataAccessException {
        //get Cars Promotions Configured in Inventory, and valid, should return in response.
        final List<CarPromotion> carPromotions = getCarsPromotionsConfigured(testData.getScenarios(), response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarCatalogKey()
                .getVendorSupplierID(), response.getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarPickUpDateTime().toString(), response.getCarProductList().getCarProduct().get(0)
                .getCarInventoryKey().getCarDropOffDateTime().toString(), shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);
        checkAndVerifyCarPromotions(response.getCarProductList().getCarProduct().get(0).getCarPromotionList(), carPromotions);
    }

    public void verifyCarPromotionsInSearchResponse(CarECommerceSearchResponseType response, TestData testData, String shoppingCarVendorPromotions, String shoppingCarInclusionDetails,
                                                    String shoppingCarExclusionDetails, String shoppingCarMiscellaneousInfo) throws DataAccessException {
        //get Cars Promotions Configured in Inventory, and valid, should return in response.
        final String pickUpDateTime = response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarPickUpDateTime().toString();
        final String dropOffDateTime = response.getCarSearchResultList().getCarSearchResult().get(0).getCarProductList().getCarProduct().get(0).getCarInventoryKey().getCarDropOffDateTime().toString();

        for (final CarSearchResultType searchResult : response.getCarSearchResultList().getCarSearchResult()) {
            for (final CarProductType car : searchResult.getCarProductList().getCarProduct()) {
                final List<CarPromotion> carPromotions = getCarsPromotionsConfigured(testData.getScenarios(), car.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID(), pickUpDateTime,
                        dropOffDateTime, shoppingCarVendorPromotions, shoppingCarInclusionDetails, shoppingCarExclusionDetails, shoppingCarMiscellaneousInfo);
                checkAndVerifyCarPromotions(car.getCarPromotionList(), carPromotions);
            }
        }
    }

    private void checkAndVerifyCarPromotions(CarPromotionListType carPromotionList, List<CarPromotion> carPromotions) {
        if ((CompareUtil.isObjEmpty(carPromotionList) && !CompareUtil.isObjEmpty(carPromotions)) || !CompareUtil.isObjEmpty(carPromotionList) && CompareUtil.isObjEmpty(carPromotions)) {
            Assert.fail("carPromotions from DB or getCarPromotionList from response is null, please check");
        } else if (!CompareUtil.isObjEmpty(carPromotionList) && !CompareUtil.isObjEmpty(carPromotions)) {
            verifyCarPromotionsFromDBAndResponse(carPromotionList, carPromotions);
        }
    }

    //do the CarPromotionList compare form DB and Response
    private void verifyCarPromotionsFromDBAndResponse(CarPromotionListType resCarPromotionList, List<CarPromotion> carPromotions) {
        boolean promotionExists;
        for (final CarPromotionType resCarPromotion : resCarPromotionList.getCarPromotion()) {
            promotionExists = false;
            for (final CarPromotion dbCarPromotion : carPromotions) {
                if (resCarPromotion.getCarPromotionType().equals(dbCarPromotion.getCarPromotionType()) &&
                        resCarPromotion.getCarPromotionName().equals(dbCarPromotion.getCarPromotionName()) &&
                        resCarPromotion.getCarPromotionDescription().equals(dbCarPromotion.getCarPromotionDescription()) &&
                        resCarPromotion.getCarOfferType().equals(dbCarPromotion.getCarOfferType())) {
                    promotionExists = true;
                    break;
                }
            }
            if (!promotionExists) {
                Assert.fail("CarPromotion details from Db and getCarPromotionList from response has different values, please check");
            }
        }
    }
}