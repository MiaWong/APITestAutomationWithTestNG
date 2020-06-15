package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.common;

import com.expedia.e3.data.cartypes.defn.v5.CarCatalogKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarInventoryKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarVehicleType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonConstantManager;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.CarsSCSDataSource;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by miawang on 12/5/2016.
 */
public class GDSMsgReadHelper {
    private GDSMsgReadHelper() {
    }

    /**
     * Read VendorSupplierID from external code
     *
     * @param scsDataSource
     * @param exVendorCode
     * @return
     * @throws DataAccessException
     */
    public static Long readVendorSupplierID(CarsSCSDataSource scsDataSource, String exVendorCode) throws DataAccessException {
        if (!org.apache.commons.lang.StringUtils.isEmpty(exVendorCode)) {
            //String exVendorCode = vendorNode.getAttributes().getNamedItem("Code").getTextContent();
            final List<ExternalSupplyServiceDomainValueMap> extendedVendorList = scsDataSource.
                    getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_VENDOR_EXTENDED, null, exVendorCode);
            if (!extendedVendorList.isEmpty()) {
                return Long.parseLong(extendedVendorList.get(0).getDomainValue().split(":")[0]);
            }

            final List<ExternalSupplyServiceDomainValueMap> vendorList = scsDataSource.
                    getExternalSupplyServiceDomainValueMap(0L, 0L, CommonConstantManager.DomainType.CAR_VENDOR, null, exVendorCode);
            if (!vendorList.isEmpty()) {
                return Long.parseLong(vendorList.get(0).getDomainValue());
            }
        }
        return 0L;
    }

    /**
     * Read CarBS SIPP from VehMakeModel(External SIPP)
     *
     * @param carInventoryKey
     * @param scsDataSource
     * @param externalSIPP
     * @throws DataAccessException
     */
    public static CarInventoryKeyType readVehMakeModel(CarInventoryKeyType carInventoryKey, CarsSCSDataSource scsDataSource,
                                        VehMakeModel externalSIPP) throws DataAccessException {
        final boolean isInputValid = externalSIPP != null && !StringUtils.isEmpty(externalSIPP.getExternalCarCategory()) &&
                !StringUtils.isEmpty(externalSIPP.getExternalCarFuelAirCondition()) &&
                !StringUtils.isEmpty(externalSIPP.getExternalCarTransmissionDrive()) &&
                !StringUtils.isEmpty(externalSIPP.getExternalCarType());
        if (isInputValid) {
            final String carCategory = getDomainValueByDomainTypeAndExternalDomainValue(CommonConstantManager.DomainType.CAR_CATEGORY,
                    externalSIPP.getExternalCarCategory(), scsDataSource);
            if (null == carInventoryKey.getCarCatalogKey()) {
                carInventoryKey.setCarCatalogKey(new CarCatalogKeyType());
            }
            if (null == carInventoryKey.getCarCatalogKey().getCarVehicle()) {
                carInventoryKey.getCarCatalogKey().setCarVehicle(new CarVehicleType());
            }
            if (!StringUtils.isEmpty(carCategory.trim())) {
                carInventoryKey.getCarCatalogKey().getCarVehicle().setCarCategoryCode(Long.parseLong(carCategory.trim()));
            }

            final String carType = getDomainValueByDomainTypeAndExternalDomainValue(CommonConstantManager.DomainType.CAR_TYPE,
                    externalSIPP.getExternalCarType(), scsDataSource);
            if (!StringUtils.isEmpty(carType.trim())) {
                carInventoryKey.getCarCatalogKey().getCarVehicle().setCarTypeCode(Long.parseLong(carType.trim()));
            }

            final String carTransmissionDrive = getDomainValueByDomainTypeAndExternalDomainValue(CommonConstantManager.DomainType.CAR_TRANSMISSIOND_DRIVE,
                    externalSIPP.getExternalCarTransmissionDrive(), scsDataSource);
            if (!StringUtils.isEmpty(carTransmissionDrive.trim())) {
                carInventoryKey.getCarCatalogKey().getCarVehicle().setCarTransmissionDriveCode(Long.parseLong(carTransmissionDrive.trim()));
            }

            final String carFuelAirCondition = getDomainValueByDomainTypeAndExternalDomainValue(CommonConstantManager.DomainType.CAR_FUEL_AIR_CONDITION,
                    externalSIPP.getExternalCarFuelAirCondition(), scsDataSource);
            if (!StringUtils.isEmpty(carFuelAirCondition.trim())) {
                carInventoryKey.getCarCatalogKey().getCarVehicle().setCarFuelACCode(Long.parseLong(carFuelAirCondition.trim()));
            }
        }

        return carInventoryKey;
    }

    /*
    <ns5:CarCategoryCode>7</ns5:CarCategoryCode>
    <ns5:CarTypeCode>1</ns5:CarTypeCode>
    <ns5:CarTransmissionDriveCode>1</ns5:CarTransmissionDriveCode>
    <ns5:CarFuelACCode>1</ns5:CarFuelACCode>
     */

    /**
     * Read External SIPP from CarProductType().getCarInventoryKey().getCarCatalogKey().getCarVehicle()
     *
     * @param sCSDataSource
     * @param vehicle
     * @param remarks
     * @param action
     * @return
     */
    public static String getExternalSupplyServiceSIPP(CarsSCSDataSource sCSDataSource, CarVehicleType vehicle,
                                                      List remarks, String action) {
        final StringBuffer sipp = new StringBuffer();

        try {
            sipp.append(getExternalDomainValueByDomainTypeAndDomainValue(CommonConstantManager.DomainType.CAR_CATEGORY,
                    vehicle.getCarCategoryCode().toString(), sCSDataSource));

            sipp.append(getExternalDomainValueByDomainTypeAndDomainValue(CommonConstantManager.DomainType.CAR_TYPE,
                    vehicle.getCarTypeCode().toString(), sCSDataSource));

            sipp.append(getExternalDomainValueByDomainTypeAndDomainValue(CommonConstantManager.DomainType.CAR_TRANSMISSIOND_DRIVE,
                    vehicle.getCarTransmissionDriveCode().toString(),
                    sCSDataSource));

            sipp.append(getExternalDomainValueByDomainTypeAndDomainValue(CommonConstantManager.DomainType.CAR_FUEL_AIR_CONDITION,
                    vehicle.getCarFuelACCode().toString(), sCSDataSource));

            if (sipp.length() == 4) {
                return sipp.toString();
            }
        } catch (DataAccessException e) {
            remarks.add("getExternalSupplyServiceSIPP failed in " + action + " by DataAccessException, Detail : " + e.getMessage());
        }
        return "";
    }

    private static String getExternalDomainValueByDomainTypeAndDomainValue(String domainType, String domainValue,
                                                                           CarsSCSDataSource scsDataSource) throws DataAccessException {
        String externalDomainValue = null;
        final List<ExternalSupplyServiceDomainValueMap> sippExternalValues = scsDataSource.getExternalSupplyServiceDomainValueMap
                (0, 0, domainType, domainValue, null);
        if (!sippExternalValues.isEmpty()) {
            externalDomainValue = sippExternalValues.get(0).getExternalDomainValue();
        }
        return externalDomainValue;
    }

    public static String getDomainValueByDomainTypeAndExternalDomainValue(String domainType, String externalDomainValue,
                                                                          CarsSCSDataSource scsDataSource) throws DataAccessException {
        String domainValue = null;

        final List<ExternalSupplyServiceDomainValueMap> sippExternalValues = scsDataSource.getExternalSupplyServiceDomainValueMap
                (0L, 0L, domainType, null, externalDomainValue);
        if (!sippExternalValues.isEmpty()) {
            domainValue = sippExternalValues.get(0).getDomainValue();
        }
        return domainValue;
    }

    //Meichun - 2017-02-24 don't read configurationmaster DB, read
    public static String getCountryAlpha3CodeFromCountryCode(String countryCode) {
        switch(countryCode)
        {
            case "US":
                return "USA";
            case "CA":
                return "CAN";
            case "DE":
                return "DEU";
            case "ES":
                return "ESP";
            case "FR":
                return "FRA";
            case "GB":
                return "GBR";
            case "IE":
                return "IRL";
            case "IT":
                return "ITA";
            default:
                return "USA";

        }
        //final ConfigurationMasterHelper configurationMasterHelper = new ConfigurationMasterHelper(configurationMasterDatasource);
        //return configurationMasterHelper.getCountryCodeFromCountryShortCode(countryCode);
    }
}