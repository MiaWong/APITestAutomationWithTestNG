package com.expedia.s3.cars.supplyconnectivity.amadeus.tests.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsSCSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carscs.ExternalSupplyServiceDomainValueMap;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.supplyconnectivity.amadeus.tests.common.CarCommonEnumManager;
import com.expedia.s3.cars.supplyconnectivity.messages.reserve.defn.v4.CarSupplyConnectivityReserveResponseType;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.testng.Assert;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by fehu on 8/4/2016.
 */
public class ReserveVerifier implements IVerification
{
    public  static void isReserveWorksVerifier(CarSupplyConnectivityReserveResponseType carSCSReserveResponse)
    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null == carSCSReserveResponse)
        {
            errorMsg.append("Verify Reseve failed. No data return in Reserveresponse.");
        }
       else
        {
            if (null == carSCSReserveResponse.getCarReservation())
            {
                errorMsg.append("Verify Reseve failed. No getCarReservation return in Reserveresponse.");
                Assert.assertNotNull(errorMsg);
            }
            if (null == carSCSReserveResponse.getCarReservation().getBookingStateCode())
            {
                errorMsg.append("Verify Reseve failed. No BookingStateCode return in Reserveresponse.");
            }
            else if (null != carSCSReserveResponse.getCarReservation().getBookingStateCode()
                    && !carSCSReserveResponse.getCarReservation().getBookingStateCode().equals(CarCommonEnumManager.BookingStateCode.Booked.toString())
                    && !carSCSReserveResponse.getCarReservation().getBookingStateCode().equals(CarCommonEnumManager.BookingStateCode.Pending.toString())
                    && !carSCSReserveResponse.getCarReservation().getBookingStateCode().equals(CarCommonEnumManager.BookingStateCode.Reserved.toString())
                    && !carSCSReserveResponse.getCarReservation().getBookingStateCode().equals(CarCommonEnumManager.BookingStateCode.Confirm.toString()))
            {
                errorMsg.append(String.format("Verify Reseve failed. BookingStateCode={0}.", carSCSReserveResponse.getCarReservation().getBookingStateCode()));
            }
            if (null != carSCSReserveResponse.getErrorCollection())
            {
                List<String > descriptionRawTextList = PojoXmlUtil.getXmlFieldValue(carSCSReserveResponse.getErrorCollection(), "DescriptionRawText");
                if (descriptionRawTextList.size() > 0)
                {
                    errorMsg.append("Verify Reseve failed. Exist error in Reserveresponse.");
                }
                for (String descriptionRawText : descriptionRawTextList)
                {
                    errorMsg.append(descriptionRawText);
                }
            }
            if (carSCSReserveResponse.getCarReservation().getBookingStateCode() != null)
                errorMsg = new StringBuilder("");
        }
        if(!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    @SuppressWarnings("CPD-START")
    public static void verifyCarLocationInfo(CarSupplyConnectivityReserveResponseType response, DataSource datasource) throws DataAccessException

    {
        StringBuilder errorMsg = new StringBuilder("");
        if (null != response.getCarReservation() && null != response.getCarReservation().getCarProduct())
        {
            final CarProductType productType = response.getCarReservation().getCarProduct();

            final CarLocationKeyType startLocationReturn = productType.getCarInventoryKey().getCarCatalogKey()
                    .getCarPickupLocationKey();
            final CarLocationKeyType startLocationReturn2 = productType.getCarPickupLocation().getCarLocationKey();
            final String startCarVendorLocationReturn = startLocationReturn.getLocationCode() +
                    startLocationReturn.getCarLocationCategoryCode() + startLocationReturn.getSupplierRawText();
            final Long startDomainValue = startLocationReturn.getCarVendorLocationID();


            final CarLocationKeyType endLocationReturn = productType.getCarInventoryKey().getCarCatalogKey()
                    .getCarDropOffLocationKey();
            final CarLocationKeyType endLocationReturn2 = productType.getCarDropOffLocation().getCarLocationKey();
            final String endCarVendorLocationReturn = endLocationReturn.getLocationCode() +
                    endLocationReturn.getCarLocationCategoryCode() + endLocationReturn.getSupplierRawText();
            final Long endDomainValue = endLocationReturn.getCarVendorLocationID();

            if (null == startDomainValue || 0L == startDomainValue || null == startLocationReturn2.getCarVendorLocationID() ||
                    0L == startLocationReturn2.getCarVendorLocationID())
            {
                errorMsg.append("No CarVendorLocationID returned in CarPickupLocationKey or CarPickupLocation!");
            }
            if (null == endDomainValue || 0L == endDomainValue || null == endLocationReturn2.getCarVendorLocationID() ||
                    0L == endLocationReturn2.getCarVendorLocationID())
            {
                errorMsg.append("No CarVendorLocationID returned in CarDropOffLocationKey or CarDropOffLocation!");
            }

            final long supplierId = productType.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID();
            final String domainType = "CarVendorLocation";

            final CarsSCSHelper carsSCSHelper = new CarsSCSHelper(datasource);
            List<ExternalSupplyServiceDomainValueMap> startMapList = carsSCSHelper
                    .getExternalSupplyServiceDomainValueMap(supplierId,
                    0, domainType, String.valueOf(startDomainValue), startCarVendorLocationReturn);
            if (CollectionUtils.isEmpty(startMapList))
            {
                //need to check if  type like 'MADT01' exist.
                startMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId, 0, domainType, String
                                .valueOf(startDomainValue),
                        startCarVendorLocationReturn.substring(0, 4) + startCarVendorLocationReturn.substring(5,
                                startCarVendorLocationReturn.length()));
            }

            List<ExternalSupplyServiceDomainValueMap> endMapList = carsSCSHelper
                    .getExternalSupplyServiceDomainValueMap(supplierId,
                    0, domainType, String.valueOf(endDomainValue), endCarVendorLocationReturn);
            if (CollectionUtils.isEmpty(endMapList))
            {
                //need to check if  type like 'MADT01' exist.
                endMapList = carsSCSHelper.getExternalSupplyServiceDomainValueMap(supplierId, 0, domainType, String
                                .valueOf(endDomainValue),
                        endCarVendorLocationReturn.substring(0, 4) + endCarVendorLocationReturn.substring(5,
                                endCarVendorLocationReturn.length()));
            }

            if (null == startMapList || startMapList.isEmpty())
            {
                errorMsg.append("No matched pick up CarVendorLocation recode find in DB CarVendorLocationID!");
            }
            if (null == endMapList || endMapList.isEmpty())
            {
                errorMsg.append("No matched drop off CarVendorLocation recode find in DB by CarVendorLocationID!");
            }
        }
        if (!StringUtils.isEmpty(String.valueOf(errorMsg)))
        {
            Assert.fail(errorMsg.toString());
        }
    }

    @Override
    public String getName() {
        return null;
    }
    @SuppressWarnings("CPD-END")

    @Override
    public VerificationResult verify(Object o, Object verificationContext) {
        return null;
    }
}
