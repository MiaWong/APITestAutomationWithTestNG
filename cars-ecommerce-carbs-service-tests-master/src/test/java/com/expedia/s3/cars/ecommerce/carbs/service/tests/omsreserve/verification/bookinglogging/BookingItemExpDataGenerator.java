package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceListType;
import com.expedia.e3.data.cartypes.defn.v5.CarLocationKeyType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.PosConfigSettingName;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItem;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarItem;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.PurchaseType;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Created by v-mechen on 9/11/2018.
 */
public class BookingItemExpDataGenerator {
    final CarsInventoryHelper m_carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

    public BookingItem getExpBookingItem(PreparePurchaseRequestType request, PreparePurchaseResponseType response, boolean cancelled
            , TestScenario testScenario) throws DataAccessException, SQLException {
        final BookingItem bookingItem = new BookingItem();
        final String bookingID = BookingVerificationUtils.getBookingID(response);
        final String bookingItemID = BookingVerificationUtils.getBookingItemID(response);
        bookingItem.setBookingID(bookingID);
        bookingItem.setBookingItemID(bookingItemID);
        bookingItem.setBookingItemTypeID("11");
        if(cancelled)
        {
            bookingItem.setBookingItemStateID(BookingItemState.CANCELLED.intValue());
        }
        else
        {
            bookingItem.setBookingItemStateID(BookingItemState.BOOKED.intValue());
        }
        bookingItem.setBookingItemStateIDPending(BookingItemStateIDPending.NONE.intValue());
        bookingItem.setBookingFulfillmentMethodID(1);
        bookingItem.setBookingFulfillmentStateID(0);
        final CarProductType requestCar = request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        bookingItem.setUseDateBegin(new Timestamp(requestCar.getCarInventoryKey().getCarPickUpDateTime().toCalendar().getTimeInMillis()));
        bookingItem.setUseDateEnd(new Timestamp(requestCar.getCarInventoryKey().getCarDropOffDateTime().toCalendar().getTimeInMillis()));
        fillBookingRecordSystemID(bookingItem, requestCar, testScenario);
        //PNR to BookingRecordSystemReferenceCode and vendor to SupplierBookingConfirmationCode
        fillFromResponseReferenceList(bookingItem, response);
        final CarItem carItemInfo = m_carsInventoryHelper.getCarItemById(requestCar.getCarInventoryKey().getCarItemID());
        bookingItem.setAccountingVendorID("0");
        if (null != carItemInfo) {
            bookingItem.setAccountingVendorID(carItemInfo.getAccountingVendorID());
            if(null == carItemInfo.getAccountingVendorID())
            {
                bookingItem.setAccountingVendorID("0");
            }
        }

        fillBookingItemDesc(bookingItem, requestCar, carItemInfo);
        fillTuidTpid(bookingItem, request, cancelled);
        fillRevenueReportingTypeID(bookingItem, carItemInfo, testScenario);

        return bookingItem;
    }

    protected void fillTuidTpid(BookingItem bookingItem, PreparePurchaseRequestType request, boolean cancelled) throws DataAccessException, SQLException {
        // get tuid
        final String tuid = String.valueOf(request.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getUserID());
        bookingItem.setCreateTUID(tuid);
        bookingItem.setBookTUID(tuid);
        bookingItem.setUpdateTUID(tuid);
        if(cancelled)
        {
            bookingItem.setCancelTUID(tuid);
        }
        //get TPID
        final String tpid = String.valueOf(request.getSiteMessageInfo().getLegacySiteKey().getTPID());
        bookingItem.setUpdateTravelProductID(tpid);
    }

    protected void fillBookingRecordSystemID(BookingItem bookingItem, CarProductType carProduct, TestScenario testScenario) throws DataAccessException, SQLException {
        // get providerID
        final Integer providerID = m_carsInventoryHelper.getProviderId(carProduct);
        //Get BookingRecordSystemID based on providerID
        final Integer bookingRecordSystemID = getBookingRecordSystemID(providerID, testScenario);
        bookingItem.setBookingRecordSystemID(bookingRecordSystemID.intValue());
    }

    protected Integer getBookingRecordSystemID(Integer providerID, TestScenario testScenario) throws DataAccessException, SQLException {
        //by default set 1 as the bookingRecordSystemID
        Integer bookingRecordSystemID = BookingRecordSystemID.WORLDSPAN.intValue();

        //set the bookingRecordSystemID to 0 for titanium if just the titanium feature is enabled
        if (VerificationHelper.isPosConfigEnabled(PosConfigSettingName.BOOKING_SETBOOKINGRECORDSYSTEMID_TITANIUM_ENABLE, testScenario))
        {
            if (providerID.equals(ProviderID.TITANIUM.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.UNKNOWN.intValue();
            }
        }
        //log the proper BookingRecordSystemID based on provider
        else if (VerificationHelper.isPosConfigEnabled(PosConfigSettingName.BOOKING_SETBOOKINGRECORDSYSTEMID_ENABLE, testScenario))
        {
            if (providerID.equals(ProviderID.UNKNOWN.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.UNKNOWN.intValue();
            }
            else if (providerID.equals(ProviderID.AMADEUS.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.AMADEUS.intValue();
            }
            else if (providerID.equals(ProviderID.WORLDSPAN.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.WORLDSPAN.intValue();
            }
            else if (providerID.equals(ProviderID.MICRONNEXUS.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.MICRONNEXUS.intValue();
            }
            else if (providerID.equals(ProviderID.TITANIUM.intValue()))
            {
                bookingRecordSystemID = BookingRecordSystemID.TITANIUM.intValue();
            }
            else
            {
                bookingRecordSystemID = BookingRecordSystemID.UNKNOWN.intValue();
            }
        }


        return bookingRecordSystemID;
    }

    private void fillFromResponseReferenceList(BookingItem bookingItem, PreparePurchaseResponseType response)
    {
        final ReferenceListType referenceList = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData()
                .getCarReservation().getReferenceList();
        if (null != referenceList)
        {
            if (null != referenceList.getReference())
            {
                final String pnr = BookingVerificationUtils
                        .findReferenceCodeForCategory(referenceList.getReference(), CommonEnumManager.ReferenceCategoryCode.PNR.toString());
                if (null != pnr)
                {
                    // m_pBookingRecordSystemReferenceCode
                    final int pnrLength = Math.min(pnr.length(), 35);
                    bookingItem.setBookingRecordSystemReferenceCode(pnr.substring(0, pnrLength));
                }

                final String vendorConfirmation = BookingVerificationUtils
                        .findReferenceCodeForCategory(referenceList.getReference(), CommonEnumManager.ReferenceCategoryCode.Vendor.toString());
                if (null != vendorConfirmation)
                {
                    // m_pSupplierBookingConfirmationCode
                    bookingItem.setSupplierBookingConfirmationCode(vendorConfirmation);
                }

            }
        }
    }

    private void fillBookingItemDesc(BookingItem bookingItem, CarProductType carProduct, CarItem carItemInfo)
    {
        final CarLocationKeyType carPickUpLocKeyReq = carProduct.getCarInventoryKey().getCarCatalogKey().getCarPickupLocationKey();
        if (null != carPickUpLocKeyReq)
        {
            // m_pAirportCodePickUp
            if (null != carPickUpLocKeyReq.getLocationCode())
            {
                final String pickLocationCode = carPickUpLocKeyReq.getLocationCode();

                // m_pBookingItemDesc
                if (carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.GDSP.getBusinessModel())
                {
                    bookingItem
                            .setBookingItemDesc("GDSP car booking in " + pickLocationCode);
                }
                else if (carItemInfo.getCarBusinessModelID()== CommonEnumManager.BusinessModel.Merchant.getBusinessModel())
                {
                    bookingItem
                            .setBookingItemDesc("Merchant car booking in " + pickLocationCode);
                }
                else
                {
                    bookingItem
                            .setBookingItemDesc("Agency car booking in " + pickLocationCode);
                }
            }

        }

    }

    private void fillRevenueReportingTypeID(BookingItem bookingItem, CarItem carItemInfo, TestScenario testScenario) throws DataAccessException, SQLException {
        if (carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.GDSP.getBusinessModel())
        {
            if (PurchaseType.CarOnly.equals(testScenario.getPurchaseType()) && VerificationHelper.isPosConfigEnabled(
                    PosConfigSettingName.BOOKING_MERCHANTCOMMISSIONMARKUP_SETREVENUEREPORTINGTYPEID_ENABLE
                            ,testScenario))
            {
                //log RevenueReportingTypeID as 10 for merchant commission markup product
                bookingItem.setRevenueReportingTypeID(
                        Integer.valueOf(RevenueReportingTypeID.MERCHANT_COMMISSION_MARKUP.intValue()));
            }
            else
            {
                bookingItem
                        .setRevenueReportingTypeID(Integer.valueOf(RevenueReportingTypeID.GDSPMERCHANT.intValue()));
            }
        }
        else if (carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Merchant.getBusinessModel())
        {
            bookingItem
                    .setRevenueReportingTypeID(Integer.valueOf(RevenueReportingTypeID.MERCHANT_USE.intValue()));
        }
        else if (carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Agency.getBusinessModel() && carItemInfo
                .isPrepaidBool() && VerificationHelper.isPosConfigEnabled(
                PosConfigSettingName.BOOKING_PREPAIDAGENCY_SETREVENUEREPORTINGTYPEID_ENABLE, testScenario))
        {
            bookingItem
                    .setRevenueReportingTypeID(RevenueReportingTypeID.AGENCY_PREPAID_BOOKING.intValue());
        }
        else
        {
            bookingItem
                    .setRevenueReportingTypeID(Integer.valueOf(RevenueReportingTypeID.AGENT_BOOKING.intValue()));
        }
    }
}
