package com.expedia.s3.cars.framework.test.common.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created by miawang on 12/27/2016.
 */
public class CarsInventoryKeyComparator {
    private CarsInventoryKeyComparator() {
    }

    /**
     * @param expectedCarInventoryKey
     * @param actualCarInventoryKey
     * @param eMsg
     * @param ignoreNodeList          content is define in common class com.expedia.s3.cars.framework.test.common.constant.CarTags
     * @return
     */
    public static boolean isCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                 StringBuilder eMsg, List<String> ignoreNodeList) {
        boolean isEqual = true;
        if (!ignoreNodeList.contains(CarTags.CAR_INVENTORY_KEY)) {
            final StringBuilder errorMsg = new StringBuilder();

            // CarInventoryKey if both null, return
            if (CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey, actualCarInventoryKey,
                    errorMsg, "Car CarInventoryKey is null in Expect or Actual; ")) {
                return isEqual;
            } else if (StringUtils.isEmpty(errorMsg.toString().trim())) {
                isCarCatalogKeyInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);

                isCarPickUpDateTimeInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg);

                isCarDropOffDateTimeInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg);

                isCarItemIDAndSupplySubsetIDInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);

                CarNodeComparator.isCarRateEqual(expectedCarInventoryKey.getCarRate(), actualCarInventoryKey.getCarRate(), errorMsg, ignoreNodeList);

                isCarPackageBooleanInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);

                isCarPostPurchaseBooleanInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);
            }

            if (!StringUtils.isEmpty(errorMsg.toString().trim())) {
                isEqual = false;
                if (eMsg != null) {
                    eMsg.append(" Car CarInventoryKey is not As expected, below is the detail : ").append(errorMsg.toString());
                }
            }
        }

        return isEqual;
    }

    private static void isCarCatalogKeyInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                              StringBuilder errorMsg, List<String> ignoreNodeList) {
        final StringBuilder errorMsgBuilder = new StringBuilder();

        final boolean isCatalogKeyBothNull = CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey.getCarCatalogKey(), actualCarInventoryKey.getCarCatalogKey(),
                errorMsgBuilder, "Car CarInventoryKey/CarCatalogKey is null in Expect or Actual; ");
        if (!isCatalogKeyBothNull && StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
            // CarCatalogKey: VendorSupplierID & SIPP
            CarNodeComparator.isVendorIdAndSIPPEqual(expectedCarInventoryKey.getCarCatalogKey(), actualCarInventoryKey.getCarCatalogKey(), errorMsgBuilder);
            if (!StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
                errorMsg.append(errorMsgBuilder.toString()).append('\n');
            }

            isCarPickupLocationKeyInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);

            isCarDropOffLocationKeyInCarInventoryKeyEqual(expectedCarInventoryKey, actualCarInventoryKey, errorMsg, ignoreNodeList);
        }
    }

    private static void isCarPickupLocationKeyInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                     StringBuilder errorMsg, List<String> ignoreNodeList) {
        //CarCatalogKey: CarPickupLocationKey
        final StringBuilder errorMsgBuilder = new StringBuilder();
        final boolean isCarPickupLocationKeyBothNull = CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey(),
                actualCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey(), errorMsg,
                "Car CarInventoryKey/CarCatalogKey/CarPickupLocationKey is null in Expect or Actual; ");
        if (!isCarPickupLocationKeyBothNull && StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
            CarNodeComparator.isCarLocationKeyEqual(expectedCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey(),
                    actualCarInventoryKey.getCarCatalogKey().getCarPickupLocationKey(), errorMsgBuilder, ignoreNodeList);
            if (!StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
                errorMsg.append("Car CarInventoryKey Pickup Location is different in Expect and Actual; ").append(errorMsgBuilder.toString()).append('\n');
            }
        }
    }

    private static void isCarDropOffLocationKeyInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                      StringBuilder errorMsg, List<String> ignoreNodeList) {
        //CarCatalogKey: CarDropOffLocationKey
        final StringBuilder errorMsgBuilder = new StringBuilder();
        final boolean isCarDropOffLocationKeyBothNull = CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey(),
                actualCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey(), errorMsg,
                "Car CarInventoryKey/CarCatalogKey/CarDropOffLocationKey is null in Expect or Actual; ");
        if (!isCarDropOffLocationKeyBothNull && StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
            CarNodeComparator.isCarLocationKeyEqual(expectedCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey(),
                    actualCarInventoryKey.getCarCatalogKey().getCarDropOffLocationKey(), errorMsgBuilder, ignoreNodeList);
            if (!StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
                errorMsg.append("Car CarInventoryKey Drop-Off Location is different in Expect and Actual; ").append(errorMsgBuilder.toString()).append('\n');
            }
        }
    }

    private static void isCarPickUpDateTimeInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                  StringBuilder errorMsg) {
        //pickup DateTime
        final StringBuilder errorMsgBuilder = new StringBuilder();
        CarNodeComparator.isDateTimeEqual(expectedCarInventoryKey.getCarPickUpDateTime(), actualCarInventoryKey.getCarPickUpDateTime(), errorMsgBuilder);
        if (!StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
            errorMsg.append("Car CarInventoryKey PickUpDateTime is different in Expect and Actual; ").append(errorMsgBuilder.toString()).append('\n');
        }
    }

    private static void isCarDropOffDateTimeInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                   StringBuilder errorMsg) {
        //Drop-off datetime
        final StringBuilder errorMsgBuilder = new StringBuilder();
        CarNodeComparator.isDateTimeEqual(expectedCarInventoryKey.getCarDropOffDateTime(), actualCarInventoryKey.getCarDropOffDateTime(), errorMsgBuilder);
        if (!StringUtils.isEmpty(errorMsgBuilder.toString().trim())) {
            errorMsg.append("Car CarInventoryKey DropOffDateTime is different in Expect and Actual; ").append(errorMsgBuilder.toString()).append('\n');
        }
    }

    private static void isCarItemIDAndSupplySubsetIDInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                           StringBuilder errorMsg, List<String> ignoreNodeList) {
        if (!ignoreNodeList.contains(CarTags.SUPPLY_SUBSET_ID)) {
            final boolean carItemIdEqual = (expectedCarInventoryKey.getCarItemID() == actualCarInventoryKey.getCarItemID()) ||
                    (null != expectedCarInventoryKey.getCarItemID() && null != actualCarInventoryKey.getCarItemID() &&
                            expectedCarInventoryKey.getCarItemID().equals(actualCarInventoryKey.getCarItemID()));

            final boolean carSupplySubsetIDEqual = (expectedCarInventoryKey.getSupplySubsetID() == actualCarInventoryKey.getSupplySubsetID()) ||
                    (null != expectedCarInventoryKey.getSupplySubsetID() && null != actualCarInventoryKey.getSupplySubsetID() &&
                            expectedCarInventoryKey.getSupplySubsetID().equals(actualCarInventoryKey.getSupplySubsetID()));

            if (!(carItemIdEqual && carSupplySubsetIDEqual)) {
                errorMsg.append("The actual CarInventoryKey CarItemID:SupplySubsetID=" + actualCarInventoryKey.getCarItemID() + "-" + actualCarInventoryKey.getSupplySubsetID() +
                        " is not equal the expected value " + expectedCarInventoryKey.getCarItemID() + "-" + expectedCarInventoryKey.getSupplySubsetID() + '\n');
            }
        }
    }

    private static void isCarPackageBooleanInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                  StringBuilder errorMsg, List<String> ignoreNodeList) {
        if (!ignoreNodeList.contains(CarTags.PACKAGEBOOLEAN)) {
            final boolean isCarPackageBooleanBothNull = CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey.getPackageBoolean(), actualCarInventoryKey.getPackageBoolean(),
                    errorMsg, "Car PackageBoolean is null in Expected or Actual;");
            if (isCarPackageBooleanBothNull && (expectedCarInventoryKey.getPackageBoolean() != actualCarInventoryKey.getPackageBoolean()) && !StringUtils.isEmpty(errorMsg.toString().trim())) {
                errorMsg.append("Car CarInventoryKey PackageBoolean is different in Expected and Actual. expected: ").append(expectedCarInventoryKey.getPackageBoolean() == null ? "Null" : expectedCarInventoryKey
                        .getPackageBoolean().toString()).append(" actual: ").append(actualCarInventoryKey.getPackageBoolean() == null ? "Null" : actualCarInventoryKey.getPackageBoolean().toString()).append("!\n");
            }
        }
    }

    private static void isCarPostPurchaseBooleanInCarInventoryKeyEqual(CarInventoryKeyType expectedCarInventoryKey, CarInventoryKeyType actualCarInventoryKey,
                                                                       StringBuilder errorMsg, List<String> ignoreNodeList)
    {
        if (!ignoreNodeList.contains(CarTags.CAR_POST_PURCHASE_BOOLEAN))
        {
            final boolean isCarPostPurchaseBooleanBothNull = CarNodeComparator.isExpActObjBothNullCheck(expectedCarInventoryKey.getPostPurchaseBoolean(), actualCarInventoryKey.getPostPurchaseBoolean(), errorMsg, "Car PostPurchaseBoolean is null in Expected or Actual;");
            if (isCarPostPurchaseBooleanBothNull && (expectedCarInventoryKey.getPostPurchaseBoolean() != actualCarInventoryKey.getPostPurchaseBoolean()) && !StringUtils.isEmpty(errorMsg.toString().trim()))
            {
                errorMsg.append("Car CarInventoryKey PostPurchaseBoolean is different in Expect and Actual. expected: ").append(expectedCarInventoryKey.getPostPurchaseBoolean() == null ? "Null" : expectedCarInventoryKey.getPostPurchaseBoolean().toString()).append(" actual: ").append(actualCarInventoryKey.getPostPurchaseBoolean() == null ? "Null" : actualCarInventoryKey.getPostPurchaseBoolean().toString()).append("!\n");
            }
        }
    }
}
