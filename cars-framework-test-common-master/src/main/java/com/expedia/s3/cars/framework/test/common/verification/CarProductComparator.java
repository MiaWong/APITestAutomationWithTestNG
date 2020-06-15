package com.expedia.s3.cars.framework.test.common.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.test.common.constant.CarTags;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miawang on 12/19/2016.
 */
public class CarProductComparator {
    private CarProductComparator() {
    }

    /**
     * @param expCar
     * @param actCar
     * @param remarks
     * @param ignoreNodeList content is define in common class com.expedia.s3.cars.framework.test.common.constant.CarTags
     */
    @SuppressWarnings("PMD")
    public static void isCarProductEqual(CarProductType expCar, CarProductType actCar, List remarks, List<String> ignoreNodeList) {
        //CarRate compared in inventory key equal function.
        //CarInventoryKey
        if (null == expCar || null == actCar) {
            remarks.add("Expected Car or Actual Car is null. ");
            return;
        }

        StringBuilder errorMsgBuilderTemp = new StringBuilder();
        CarsInventoryKeyComparator.isCarInventoryKeyEqual(expCar.getCarInventoryKey(), actCar.getCarInventoryKey(), errorMsgBuilderTemp, ignoreNodeList);
        if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\n\nCar Inventory Key is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
        }

        //CostList
        errorMsgBuilderTemp = new StringBuilder();
        CarNodeComparator.isCostListEqual(expCar.getCostList(), actCar.getCostList(), errorMsgBuilderTemp, ignoreNodeList);
        if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\n\nCar Cost List is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
        }

        //CarMileage
        errorMsgBuilderTemp = new StringBuilder();
        CarNodeComparator.isCarMileageEqual(expCar.getCarMileage(), actCar.getCarMileage(), errorMsgBuilderTemp, ignoreNodeList);
        if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\n\nCar Mileage is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
        }

        //AvailStatusCode
        if (!ignoreNodeList.contains(CarTags.AVAIL_STATUS_CODE) &&
                null != actCar.getAvailStatusCode() && !actCar.getAvailStatusCode().equals(expCar.getAvailStatusCode()))
        {
            remarks.add("\n\nAvailStatusCode is not expected, expected: " + expCar.getAvailStatusCode() +
                    ", actual: " + actCar.getAvailStatusCode() + "!\r\n");
        }

        //Sepcial equipment fee
        if (!ignoreNodeList.contains(CarTags.CAR_VEHICLE_OPTIONLIST))
        {
            errorMsgBuilderTemp = new StringBuilder();
            CompareUtil.compareObject(expCar.getCarVehicleOptionList(), actCar.getCarVehicleOptionList(),
                    new ArrayList<>(), errorMsgBuilderTemp);
            if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim()))
            {
                remarks.add("\nCarVehicleOptionList is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
                remarks.add("\n");
                remarks.add(null == expCar.getCarVehicleOptionList() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar.getCarVehicleOptionList())));
                remarks.add("\n");
                remarks.add(null == actCar.getCarVehicleOptionList() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actCar.getCarVehicleOptionList())));
            }
        }

        //CarCatalogMakeModel
        if (!ignoreNodeList.contains(CarTags.CAR_CATALOG_MAKE_MODEL))
        {
            errorMsgBuilderTemp = new StringBuilder();
            CompareUtil.compareObject(expCar.getCarCatalogMakeModel(), actCar.getCarCatalogMakeModel(),
                    new ArrayList<>(), errorMsgBuilderTemp);
            if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim()))
            {
                remarks.add("\nCarCatalogMakeModel is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
                remarks.add("\n");
                remarks.add(null == expCar.getCarCatalogMakeModel() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar.getCarCatalogMakeModel())));
                remarks.add("\n");
                remarks.add(null == actCar.getCarCatalogMakeModel() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actCar.getCarCatalogMakeModel())));
            }
        }

        //CarRateDetail
        if(!ignoreNodeList.contains(CarTags.CAR_RATE_DETAIL))
        {
            errorMsgBuilderTemp = new StringBuilder();
            CompareUtil.compareObject(expCar.getCarRateDetail(), actCar.getCarRateDetail(), new ArrayList<>(), errorMsgBuilderTemp);
            if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim()))
            {
                remarks.add("\nCarRateDetail is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
                remarks.add("\n");
                remarks.add(null == expCar.getCarRateDetail() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar.getCarRateDetail())));
                remarks.add("\n");
                remarks.add(null == actCar.getCarRateDetail() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actCar.getCarRateDetail())));
            }
        }

        //CarPolicyList
        if(!ignoreNodeList.contains(CarTags.CAR_POLICY_LIST)) {
            errorMsgBuilderTemp = new StringBuilder();

            if((null == expCar.getCarPolicyList() || null == expCar.getCarPolicyList().getCarPolicy())&&(null == actCar.getCarPolicyList() || null == actCar.getCarPolicyList().getCarPolicy()))
            {
                //equals
                errorMsgBuilderTemp = new StringBuilder();
            }
            else
            {
                CompareUtil.compareObject(expCar.getCarPolicyList().getCarPolicy(), actCar.getCarPolicyList().getCarPolicy(),
                        new ArrayList<>(), errorMsgBuilderTemp);
                if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim()))
                {
                    remarks.add("\nCarPolicyList is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
                    remarks.add("\n");
                    remarks.add(null == expCar.getCarPolicyList() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar.getCarPolicyList())));
                    remarks.add("\n");
                    remarks.add(null == actCar.getCarPolicyList() ? "Null" : PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actCar.getCarPolicyList())));
                }
            }
        }
        //Locations
        errorMsgBuilderTemp = new StringBuilder();
        CarNodeComparator.isCarLocationEqual(expCar.getCarPickupLocation(), actCar.getCarPickupLocation(), true, errorMsgBuilderTemp, ignoreNodeList);
        if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\n\nCar Pickup Location is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
        }

        errorMsgBuilderTemp = new StringBuilder();
        CarNodeComparator.isCarLocationEqual(expCar.getCarDropOffLocation(), actCar.getCarDropOffLocation(), false, errorMsgBuilderTemp, ignoreNodeList);
        if (!StringUtils.isEmpty(errorMsgBuilderTemp.toString().trim())) {
            remarks.add("\n\nCar DropOff Location is different in Expect and Actual: " + errorMsgBuilderTemp.toString());
        }

        //Car door count
        if (!ignoreNodeList.contains(CarTags.CAR_DOOR_COUNT))
        {
            if((null == actCar.getCarDoorCount() && null == expCar.getCarDoorCount() ) || (null == actCar.getCarDoorCount() && 0 == expCar.getCarDoorCount() )|| (null == expCar.getCarDoorCount() && 0 == actCar.getCarDoorCount()))
            {
                //equals
            }
            else if (actCar.getCarDoorCount() != expCar.getCarDoorCount())
            {
                remarks.add("\n\nCarDoorCount is not expected, expected: " + expCar.getCarDoorCount()
                        + ", actual: " + actCar.getCarDoorCount() + "!");
            }
        }

        //PrePayBoolean
        if (expCar.getPrePayBoolean() != actCar.getPrePayBoolean()) {
            remarks.add("PrePayBoolean is not expected, exptected " +
                    expCar.getPrePayBoolean() + ", actual: " + actCar.getPrePayBoolean() + "!");
        }

        if(remarks.size() > 0)
        {
            remarks.add("\n\nExpCar \n -- " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(expCar)));
            remarks.add("\n\nactCar \n -- " + PojoXmlUtil.toString(PojoXmlUtil.pojoToDoc(actCar)));
        }
    }

    /**
     * @param expCarList
     * @param actCarList
     * @param serviceProviderID if it is use for scs search verification, pass the provider(1: worldspan, 3: MN, 6: Amadeus, 7: Titanium). if use for bs or ss pass 0, will skip it.
     * @param remarks
     * @param ignoreNodeList    content is define in common class com.expedia.s3.cars.framework.test.common.constant.CarTags
     */
    public static void isCarProductListEqual(List<CarProductType> expCarList, List<CarProductType> actCarList, int serviceProviderID,
                                             List remarks, List<String> ignoreNodeList) {
        boolean carSizeNotEqual = (null == expCarList && null != actCarList) || (null != expCarList && null == actCarList);
        carSizeNotEqual = carSizeNotEqual || (null != expCarList && null != actCarList && expCarList.size() != actCarList.size());
        if (carSizeNotEqual) {
            remarks.add("CarProduct count expected : " + (expCarList == null ? 0 : expCarList.size())
                    + " Actual : " + (actCarList == null ? 0 : actCarList.size()));
        } else if (null != expCarList && null != actCarList) {
            isCarProductListEqualBigLoop(expCarList, actCarList, serviceProviderID, remarks, ignoreNodeList);
        }
    }

    private static void isCarProductListEqualBigLoop(List<CarProductType> expCarList, List<CarProductType> actCarList, int serviceProviderID,
                                                     List remarks, List<String> ignoreNodeList)
    {
        //TODO filter car code in old automation framework, should make them work later
        /*
        //Verify every car
        int filterCar = 0;
        */
        for (final CarProductType expCar : expCarList)
        {
            /*
            //If not all cartegory request, no need to compare extended sipp - SCS should filter
            if (expCar.CarInventoryKey.CarCatalogKey.CarVehicle.CarCategoryCode > 9 && !allCategory)
            {
                filterCar++;
                continue;
            }
            //TODO meichun:done - filter the cars without CarVEndorlOcationiD and locaitonkey - similar as above
            if(expCar.CarInventoryKey.CarCatalogKey.CarPickupLocationKey == null ||
                    (expCar.CarInventoryKey.CarCatalogKey.CarPickupLocationKey.CarVendorLocationID == 0 &&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarPickupLocationKey.LocationCode)&&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarPickupLocationKey.CarLocationCategoryCode) &&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarPickupLocationKey.SupplierRawText))||
                    (expCar.CarInventoryKey.CarCatalogKey.CarDropOffLocationKey.CarVendorLocationID == 0 &&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarDropOffLocationKey.LocationCode) &&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarDropOffLocationKey.CarLocationCategoryCode) &&
                            string.IsNullOrEmpty(expCar.CarInventoryKey.CarCatalogKey.CarDropOffLocationKey.SupplierRawText)))
            {
                filterCar++;
                continue;
            }
            //filter to Get avail car
            if (expCar.AvailStatusCode == "X")
            {
                filterCar++;
                continue;
            }
            */
            boolean carExist = false;
            for (final CarProductType actCar : actCarList)
            {
                carExist = isCarInListEqual(expCar, actCar, serviceProviderID, remarks, ignoreNodeList);

                if (carExist)
                {
                    break;
                }
            }
            if (!carExist)
            {
                remarks.add("Expected CarProduct : VendorSupplierID = " + expCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                        + "| CarCategoryCode = " + expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()
                        + "| CarFuelACCode = " + expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()
                        + "| CarTransmissionDriveCode = " + expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode()
                        + "| CarTypeCode = " + expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode()
                        + "| CarRateQualifierCode = " + expCar.getCarInventoryKey().getCarRate().getCarRateQualifierCode() +
                        " is not returned in response!");
            }
       /*
        }
        */
        }
    }

    private static boolean isCarInListEqual(CarProductType expCar, CarProductType actCar, int serviceProviderID,
                                            List remarks, List<String> ignoreNodeList) {
        final boolean carExist = isCorrespondingCar(expCar, actCar, true, false);

        if (carExist) {
            final List remarksTemp = new ArrayList();
            isCarProductEqual(expCar, actCar, remarksTemp, ignoreNodeList);

            if ((serviceProviderID > 0) && (actCar.getProviderID() != serviceProviderID)) {
                remarksTemp.add("\nCarSCS providerID is wrong expected: " + serviceProviderID + " Actual: " + actCar.getProviderID());
            }

            if (!remarksTemp.isEmpty()) {
                remarks.add("\r\n\r\n\r\n----------------------start Car Product Actual values compare with expect values-----------------\r\n");
                final StringBuffer errorMsg = new StringBuffer("car[").append(actCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID())
                        .append(':').append(actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode())
                        .append('-').append(actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode())
                        .append('-').append(actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode())
                        .append('-').append(actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode())
                        .append("] is not as expected, below is the detail: ");
                remarks.add(errorMsg.toString());
                remarks.addAll(remarksTemp);
                remarks.add("\r\n-----------------------------------END----------------------------------------------\r\n");
            }
        }
        return carExist;
    }

    public static boolean isCorrespondingCar(CarProductType expCar, CarProductType actCar)
    {
        return isCorrespondingCar(expCar, actCar, true, true);
    }

    public static boolean isCorrespondingCar(CarProductType expCar, CarProductType actCar, boolean needLocationEqual, boolean needCdCodeEqual)
    {
        final boolean vendorSupplierIdEqual = expCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                == actCar.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();

        boolean sippEqual = expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode()
                == actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarCategoryCode();
        sippEqual = sippEqual && expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode()
                == actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTypeCode();
        sippEqual = sippEqual && expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode()
                == actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarTransmissionDriveCode();
        sippEqual = sippEqual && expCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode()
                == actCar.getCarInventoryKey().getCarCatalogKey().getCarVehicle().getCarFuelACCode();

        //boolean locationEqual = expCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey().getCarVendorLocationID();
        boolean locationEqual = true;
        if(needLocationEqual)
        {
            locationEqual = CarNodeComparator.isCarLocationKeyEqual(expCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey(),
                    actCar.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey());
            if(locationEqual) {
                locationEqual = locationEqual && CarNodeComparator.isCarLocationKeyEqual(expCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey(),
                        actCar.getCarInventoryKey().getCarCatalogKey().getCarDropOffLocationKey());
            }
        }

        boolean cdCodeEqual = true;
        if(needCdCodeEqual)
        {
            cdCodeEqual = false;
            if (null == expCar.getCarInventoryKey().getCarRate() && null == actCar.getCarInventoryKey().getCarRate())
            {
                cdCodeEqual = true;
            } else if (StringUtils.isEmpty(expCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode()) &&
                    StringUtils.isEmpty(actCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode()))
            {
                cdCodeEqual = true;
            } else if (expCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode().equals(actCar.getCarInventoryKey().getCarRate().getCorporateDiscountCode()))
            {
                cdCodeEqual = true;
            }
        }

        return vendorSupplierIdEqual && sippEqual && cdCodeEqual && locationEqual;
    }
}