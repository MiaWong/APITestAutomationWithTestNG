
package com.expedia.s3.cars.ecommerce.carbs.service.commonutils;

/**
 * Created by fehu on 8/24/2016.
 */
public class CarCommonEnumManager {

    public static final int PACKAGE = 1;
    public static final int STANDALONE = 0;
    public static final String PICKLOCATIONKEY = "pick-up";
    public static final String DROPLOCATIONKEY = "drop-off";

    public enum PurchaseTypeMask {
        CarOnly(128),
        HCPackage(4),
        FCPackage(8),
        FHCPackage(16),
        TCPackage(256),
        FCBundle(512),
        HCBundle(1024),
        THCPackage(4096),
        FHCBundle(8192);

        private long purchaseType;

        PurchaseTypeMask(long purchaseType) {
            this.purchaseType = purchaseType;
        }

        public long getPurchaseType() {
            return purchaseType;
        }

    }

    public enum ReferenceCategoryCode {
        TRL,
        Voucher,
        PNR,
        BookingID,
        BookingItemID,
        ItineraryBookingDirectoryID,
        Segment,
        Vendor,
        BillingNumber,
    }

    public enum OMReserveMessageType {
        GetOrderProcess,
        CreateRecord,
        PreparePurchase,
        PreparePurchaseWithoutCommitAndRollbackAction,
        CommitPurchase,
        RollbackPurchase,
        PreparePurchaseError,
        CommitPruchaseError
    }

    public enum OMCancelMessageType {
        GetChangeProcess,
        PrepareChange,
        CommitPrepareChange,
        RollbackPrepareChange,
        GetChangeProcessError,
        PrepareChangeError,
        CommitPrepareChangeError
    }

    public enum BookingStateCode {
        Booked,
        Pending,
        Confirm,
        Reserved,
        Cancelled,
        NotBooked,
        Unconfirmed,
        PendingCancel
    }

    // using for build CERR and COPP request, like build a CERR with invalid CD code
    public enum InValidFildType {
        InvalidCDCode,
        InvalidRateCode,
        InvalidFrequentTravelerNumber,
        InvalidSpecialRequest,
        InvalidCouponCode,
        InvalidSpecialEquipmentCode,
        InvalidCarClubNumber,
        InvalidLoyaltyProgram
    }

    public enum ClientID {
        ClientID_1("1"), // expweb
        ClientID_2("2"), // cars tools
        ClientID_3("3"), // Egencia
        ClientID_4("4"), // Recon
        ClientID_5("5"), // WorldSpane,MicronNex
        ClientID_6("6"), // EWS
        ClientID_7("7"), // Emain
        ClientID_null("null"),
        ClientID_empty("");
        private String value;

        ClientID(String cliendId) {
            this.value = cliendId;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CollectionSet {
        NoCollection(false),
        HasCollection(true),
        NonExist(false);

        private boolean collectionSet;

        CollectionSet(boolean collectionSet) {
            this.collectionSet = collectionSet;
        }

        public boolean isCollectionSet() {
            return collectionSet;
        }
    }

    public enum DeliverySet {
        NoDelivery(false),
        HasDelivery(true),
        NonExist(false);

        private boolean deliverySet;

        DeliverySet(boolean deliverySet) {
            this.deliverySet = deliverySet;
        }

        public boolean isDeliverySet() {
            return deliverySet;
        }
    }

    public enum OutOfOfficeHourBooleanSet {
        NoOutOfOfficeHourBoolean(false),
        HasOutOfOfficeHourBoolean(true),
        NonExist(false);
        private boolean outOfOOfficeeHourBooleanSet;

        public boolean isOutOfOOfficeeHourBooleanSet() {
            return outOfOOfficeeHourBooleanSet;
        }

        OutOfOfficeHourBooleanSet(boolean outOfOOfficeeHourBooleanSet) {
            this.outOfOOfficeeHourBooleanSet = outOfOOfficeeHourBooleanSet;
        }


        public enum CollectionSet {
            NoCollection(false),
            HasCollection(true),
            NonExist(false);

            private boolean collectionSet;

            CollectionSet(boolean collectionSet) {
                this.collectionSet = collectionSet;
            }

            public boolean isCollectionSet() {
                return collectionSet;
            }
        }

        public enum DeliverySet {
            NoDelivery(false),
            HasDelivery(true),
            NonExist(false);

            private boolean deliverySet;

            DeliverySet(boolean deliverySet) {
                this.deliverySet = deliverySet;
            }

            public boolean isDeliverySet() {
                return deliverySet;
            }
        }

    }

    public enum PackageSearchFilterType {
        Cheapest,
        Capacity,
        Margin,
        Savings
    }

    public enum ReservationGuaranteeCategory {
        Required,
        Guarantee
    }
}

