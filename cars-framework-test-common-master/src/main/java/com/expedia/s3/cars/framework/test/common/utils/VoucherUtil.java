package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.basetypes.defn.v4.SupplySubsetIDEntryType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarConfigurationFormat;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.SupplySubSetToWorldSpanSupplierItemMap;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyang4 on 1/6/2017.
 */
@SuppressWarnings("PMD")
public class VoucherUtil {
    private VoucherUtil() {
    }

    // <summary>
    //
    // </summary>
    // <param name="reqCarRate"></param>
    // <param name="supplySubSetID"></param>
    // <param name="carItemID"></param>
    // <returns></returns>
    public static String getExpectedCorpDiscBasedOnFormat(CarRateType reqCarRate, long supplySubSetID, long carItemID, DataSource dataSource) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(dataSource);
        String corpDisc = CompareUtil.isObjEmpty(reqCarRate.getCorporateDiscountCode()) ? "" : reqCarRate.getCorporateDiscountCode();
        String corpDiscText = "";
        //Get the CorpDiscFormat from DB based on SupplySubSetID
        final String cdFormat = CompareUtil.isObjEmpty(inventoryHelper.getCarConfigurationFormat(supplySubSetID)) ? "" : inventoryHelper.getCarConfigurationFormat(supplySubSetID).getCarCorpDiscFormat();
        if (CompareUtil.isObjEmpty(cdFormat)) {
            //Get CD code from SupplySubSetToWorldSpanSupplierItemMap
            final List<SupplySubsetIDEntryType> paramList = new ArrayList<SupplySubsetIDEntryType>();
            final SupplySubsetIDEntryType subsetIDEntryType = new SupplySubsetIDEntryType();
            subsetIDEntryType.setSupplySubsetID(supplySubSetID);
            paramList.add(subsetIDEntryType);
            final SupplySubSetToWorldSpanSupplierItemMap supplierItemMap = CompareUtil.isObjEmpty(inventoryHelper.getWorldSpanSupplierItemMap(paramList)) ? null : inventoryHelper.getWorldSpanSupplierItemMap(paramList).get(0);
            final String cdRequired = supplierItemMap.getCorporateDiscountCodeRequiredInBooking();
            final String cdConfig = supplierItemMap.getCorporateDiscountCode();
            if (!CompareUtil.isObjEmpty(cdRequired) && ("1".equals(cdRequired) || "true".equals(cdRequired.toLowerCase()))) {
                corpDisc = cdConfig;
            }
            corpDiscText = corpDisc;
        } else {
            corpDiscText = cdFormat;
            if (corpDiscText.contains("%a%")) {
                final String accountingVendorID = CompareUtil.isObjEmpty(inventoryHelper.getCarItemById(carItemID)) ? "" : inventoryHelper.getCarItemById(carItemID).getAccountingVendorID();
                corpDiscText = corpDiscText.replaceAll("%a%", accountingVendorID);
            }
            if (corpDiscText.contains("%c%")) {
                corpDiscText = corpDiscText.replaceAll("%c%", corpDisc);
            }
        }
        return corpDiscText;
    }


    //<summary>
    //
    // </summary>
    // <param name="reqCarRate"></param>
    // <param name="supplySubSetID"></param>
    // <param name="rentalDays"></param>
    // <returns></returns>
    public static String getExpectedRateCodeBasedOnFormat(CarRateType reqCarRate, long supplySubSetID, long supplierID, DataSource dataSource) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(dataSource);
        final String rateCode = CompareUtil.isObjEmpty(reqCarRate.getRateCode()) ? "" : reqCarRate.getRateCode();
        String rateCodeText = "";
        //Get the CarRateFormat from DB based on SupplySubSetID
        final String rcFormat = CompareUtil.isObjEmpty(inventoryHelper.getCarConfigurationFormat(supplySubSetID)) ? "" : inventoryHelper.getCarConfigurationFormat(supplySubSetID).getCarCorpDiscFormat();
        if (CompareUtil.isObjEmpty(rcFormat)) {
            rateCodeText = rateCode;
            final String reserveUseBESTRateCode = inventoryHelper.getCarBehaviorAttributValue(supplierID, supplySubSetID, 4L);
            if ("1".equals(reserveUseBESTRateCode)) {
                rateCodeText = "BEST";
            }
        } else {
            rateCodeText = rcFormat;
        }

        return rateCodeText;
    }


    // <summary>
    //
    // </summary>
    // <param name="carRate"></param>
    // <param name="supplySubSetID"></param>
    // <param name="rentalDays"></param>
    // <returns></returns>
    public static String getExpectedTourCodeBasedOnFormat(CarRateType reqCarRate, long supplySubSetID, long rentalDays, DataSource dataSource) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(dataSource);
        final String corpDisc = CompareUtil.isObjEmpty(reqCarRate.getCorporateDiscountCode()) ? "" : reqCarRate.getCorporateDiscountCode();
        final List<SupplySubsetIDEntryType> paramList = new ArrayList<SupplySubsetIDEntryType>();
        final SupplySubsetIDEntryType subsetIDEntryType = new SupplySubsetIDEntryType();
        subsetIDEntryType.setSupplySubsetID(supplySubSetID);
        paramList.add(subsetIDEntryType);
        final SupplySubSetToWorldSpanSupplierItemMap supplierItemMap = CompareUtil.isObjEmpty(inventoryHelper.getWorldSpanSupplierItemMap(paramList)) ? null : inventoryHelper.getWorldSpanSupplierItemMap(paramList).get(0);
        final String ITConfig = supplierItemMap.getItNumber();
        String tourCodeText = "";
        //Get the CorpDiscFormat from DB based on SupplySubSetID
        final String itFormat = CompareUtil.isObjEmpty(inventoryHelper.getCarConfigurationFormat(supplySubSetID)) ? "" : inventoryHelper.getCarConfigurationFormat(supplySubSetID).getCarTourIDFormat();
        if (CompareUtil.isObjEmpty(itFormat)) {
            tourCodeText = ITConfig;
        } else {
            tourCodeText = itFormat;
            if (tourCodeText.contains("%c%")) {
                tourCodeText = tourCodeText.replaceAll("%c%", corpDisc);
            }
        }
        return tourCodeText;
    }

    // <summary>
    //
    // </summary>
    // <param name="referenceList"></param>
    // <param name="supplySubSetID"></param>
    // <returns></returns>
    public static String getExpectedVoucherBasedOnFormat(List<ReferenceType> referenceList, long supplySubSetID, DataSource dataSource) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(dataSource);
        String voucher = getVoucherFromReferenceList(referenceList);
        //Get the CarSupplementalInfoFormat, CarVoucherNumberFormat from DB based on SupplySubSetID
        final CarConfigurationFormat format = inventoryHelper.getCarConfigurationFormat(supplySubSetID);

        if (CompareUtil.isObjEmpty(format)) {
            return null;
        } else {
            final String voFormat = format.getCarVoucherNumberFormat();
            if (CompareUtil.isObjEmpty(voFormat)) {
                return null;
            } else {
                if (voFormat.contains("%v%")) {
                    voucher = voFormat.replaceAll("%v%", voucher);
                }
            }
            //else Assert.Fail("No voucher format is defined for this SupplySubSetID: " + supplySubSetID);
        }
        //else Assert.Fail("No car configuration format is defined for this SupplySubSetID: " + supplySubSetID);
        return voucher;
    }


    // <summary>
    //
    // </summary>
    // <param name="referenceList"></param>
    // <returns></returns>
    public static String getVoucherFromReferenceList(List<ReferenceType> referenceList) {
        String voucher = "";
        //Get the referece with ReferenceCategoryCode = "Voucher" in req.referenceList
        for (final ReferenceType reference : referenceList) {
            if ("Voucher".equals(reference.getReferenceCategoryCode())) {
                voucher = reference.getReferenceCode();
                break;
            }
        }

        return voucher;
    }

    // <summary>
    //
    // </summary>
    // <param name="referenceList"></param>
    // <param name="supplySubSetID"></param>
    // <param name="rentalDays"></param>
    // <returns></returns>
    public static String getExpectedSupplementalInfoBasedOnFormat(List<ReferenceType> referenceList, long supplySubSetID, long rentalDays, DataSource dataSource) throws DataAccessException {
        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(dataSource);
        final String voucher = getVoucherFromReferenceList(referenceList);
        String supplementalInfo = "";
        //Get the CarSupplementalInfoFormat, CarVoucherNumberFormat from DB based on SupplySubSetID
        final CarConfigurationFormat format = inventoryHelper.getCarConfigurationFormat(supplySubSetID);
        if (!CompareUtil.isObjEmpty(format)) {
            String siFormat = format.getCarSupplementalInfoFormat();
            if (!"".equals(siFormat)) {
                if (siFormat.contains("%v%"))//VI-%v%
                {
                    siFormat = siFormat.replaceAll("%v%", voucher);
                }
                if (siFormat.contains("%d%")) {
                    siFormat = siFormat.replaceAll("%d%", String.valueOf(rentalDays));
                }
                supplementalInfo = siFormat;
            }
        }
        return supplementalInfo;
    }
}
