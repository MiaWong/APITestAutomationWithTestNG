package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.basetypes.defn.v4.*;
import com.expedia.e3.data.cartypes.defn.v5.*;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.e3.data.messagetypes.defn.v5.MessageInfoType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerListType;
import com.expedia.e3.data.traveltypes.defn.v4.TravelerType;
import com.expedia.e3.platform.messaging.core.types.DateTime;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.utilities.VerificationHelper;
import com.expedia.s3.cars.ecommerce.messages.search.defn.v4.CarECommerceSearchResponseType;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.*;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.ClientConfigHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItemCar;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarCommission;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarItem;
import com.expedia.s3.cars.framework.test.common.execution.scenarios.TestScenario;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage.CustomerReferenceInfo;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.mnmessage.VRSRsp;
import com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.uapimessage.UAPICommonNodeReader;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import com.expedia.s3.cars.framework.test.common.utils.PojoXmlUtil;
import com.expedia.s3.cars.framework.test.common.utils.SpooferTransport;
import com.expedia.s3.cars.framework.test.common.verification.CarNodeComparator;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.ShortValidator;
import org.eclipse.jetty.util.StringUtil;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/7/2018.
 */
@SuppressWarnings("PMD")
public class BookingItemCarExpDataGenerator {
    private BookingItemCar m_bookingItemCar;
    private int m_clientId;
    private CarItem m_carItemInfo;
    private CarRateType m_carRateReq;
    private CarRateType m_carRateRsp;
    private CarProductType m_carProdReq;
    private CarProductType m_carProdRsp;
    private CarInventoryKeyType m_carInvKeyReq;
    private CarInventoryKeyType m_carInvKeyRsp;
    private CarCatalogKeyType m_carCatKeyReq;
    private CarVehicleType m_carVehicleReq;
    private CarLocationKeyType m_carPickUpLocKeyReq;
    private CarLocationKeyType m_carDropOffLocKey;
    private String m_posCurrencyCode;
    private String m_posuCurrencyCode;
    private CarsInventoryHelper m_carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());

    public BookingItemCar getExpBookingItemCar(PreparePurchaseRequestType request, PreparePurchaseResponseType response,
                                               TestScenario testScenario, String guid, CarECommerceSearchResponseType standaloneSearchRsp,
                                               SpooferTransport spooferTransport) throws DataAccessException, SQLException, IOException {
        m_bookingItemCar = new BookingItemCar();
        // m_pBookingID
        //m_bookingItemCar.setBookingID(((ICarReservationInfoSharedData) m_reserveData).getBookingID());

        // m_pBookingItemID
        m_bookingItemCar.setBookingItemID(BookingVerificationUtils.getBookingItemID(response));

        CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        m_clientId = carBSHelper.getClientIDByCode(request.getConfiguredProductData().getCarOfferData().getCarReservation().getClientCode());

        m_carProdRsp = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
        m_carItemInfo = m_carsInventoryHelper.getCarItemById(m_carProdRsp.getCarInventoryKey().getCarItemID());

        //TODO
        // setting details price, before price adjustment for "Booking.adjustReservePriceDifference/enable" feature
        /*m_bookingItemCar.setShoppingTransactionAmtCost(
                ((IAdjustingReservePriceSharedData) m_reserveData).getDetailsTransactionAmtCost());
        m_bookingItemCar.setShoppingEstimatedTotalTaxAndFeeAmt(
                ((IAdjustingReservePriceSharedData) m_reserveData).getDetailsPosuTaxesAndFeesAmount());
*/
        fillAdditionalBookingAttributes(testScenario);
        fillBookingTypeIdentifier(testScenario);
        fillFromMessageInfo(request);
        fillFromRequestCarProduct(request, response, testScenario, standaloneSearchRsp);
        fillFromResponseCarProduct(response);
        fillFromRequestAirSegmentSummaryType(request);
        fillFromRequestLegacyData(request);
        fillFromRequestTravelerList(request);
        fillFromResponseTravelerList(response);
        fillFromExchangeRateInformation(request);
        fillFromSpecialRequest(request);
        fillFromRequestCreditCard(request);
        fillFromSpooferTransaction(guid, spooferTransport);

        return m_bookingItemCar;
    }

    private void fillFromRequestLegacyData(PreparePurchaseRequestType request)
    {
        CarLegacyBookingDataType legacyData = null;
        legacyData = request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData();

        if (null != legacyData)
        {
            if (null != legacyData.getSearchTypeIDPickup())
            {
                // m_pSearchTypeIDPickup
                m_bookingItemCar
                        .setSearchTypeIDPickup(String.valueOf(legacyData.getSearchTypeIDPickup().byteValue()));
            }
            if (null != legacyData.getResultTypeIDPickUp())
            {
                // m_pResultTypeIDPickUp
                m_bookingItemCar
                        .setResultTypeIDPickUp(String.valueOf(legacyData.getResultTypeIDPickUp().byteValue()));
            }

            // m_pCarSpecialEquipmentMask
            m_bookingItemCar.setCarSpecialEquipmentMask(legacyData.getCarSpecialEquipmentMask().toString());
            // m_pCarInsuranceAndWaiverMask
            m_bookingItemCar.setCarInsuranceAndWaiverMask(legacyData.getCarInsuranceAndWaiverMask().toString());

            if (null != legacyData.getCarRateOptionTypeIDTraveler())
            {
                // m_pCarRateOptionTypeIDTraveler
                m_bookingItemCar.setCarRateOptionTypeIDTraveler(
                        String.valueOf(legacyData.getCarRateOptionTypeIDTraveler().byteValue()));
            }

        }
    }


    private void fillFromRequestAirSegmentSummaryType(PreparePurchaseRequestType request)
    {
        AirFlightType airFlight = null;

        airFlight = request.getConfiguredProductData().getCarOfferData().getAirFlight();

        if (null != airFlight)
        {
            // m_pAirlineCodeArrival
            if (null != airFlight.getAirCarrierCode())
            {
                m_bookingItemCar.setAirlineCodeArrival(airFlight.getAirCarrierCode());
            }
            if (null != airFlight.getFlightNumber())
            {
                Short flightNumber = ShortValidator.getInstance().validate(airFlight.getFlightNumber());
                if (flightNumber == null)
                {
                    m_bookingItemCar.setFlightNumberArrival("0");
                }
                else
                {
                    // m_pFlightNumberArrival
                    m_bookingItemCar.setFlightNumberArrival(String.valueOf(flightNumber));
                }

            }
        }
    }

    private void fillFromRequestCarVehicle()
    {
        if (null != m_carCatKeyReq.getCarVehicle())
        {
            m_carVehicleReq = m_carCatKeyReq.getCarVehicle();
            // m_pCarCategoryID
            if (null != m_carVehicleReq.getCarCategoryCode())
            {
                m_bookingItemCar.setCarCategoryID(String.valueOf(m_carVehicleReq.getCarCategoryCode()));
            }
            // m_pCarTypeID
            if (null != m_carVehicleReq.getCarTypeCode())
            {
                m_bookingItemCar.setCarTypeID(String.valueOf(m_carVehicleReq.getCarTypeCode()));
            }
            // m_pCarTransmissionDriveID
            if (null != m_carVehicleReq.getCarTransmissionDriveCode())
            {
                m_bookingItemCar
                        .setCarTransmissionDriveID(String.valueOf(m_carVehicleReq.getCarTransmissionDriveCode()));
            }
            // m_pCarFuelAirConditionID
            if (null != m_carVehicleReq.getCarFuelACCode())
            {
                m_bookingItemCar.setCarFuelAirConditionID(String.valueOf(m_carVehicleReq.getCarFuelACCode()));
            }
        }

    }

    private void fillFromRequestCarPickUpLocation()
    {
        if (null != m_carPickUpLocKeyReq)
        {
            // m_pAirportCodePickUp
            if (null != m_carPickUpLocKeyReq.getLocationCode())
            {
                m_bookingItemCar.setAirportCodePickUp(m_carPickUpLocKeyReq.getLocationCode());

            }

            StringBuffer vendorLocCodePickUp = new StringBuffer();
            if (null != m_carPickUpLocKeyReq.getCarLocationCategoryCode())
            {
                vendorLocCodePickUp.append(m_carPickUpLocKeyReq.getCarLocationCategoryCode());
            }
            if (null != m_carPickUpLocKeyReq.getSupplierRawText())
            {
                vendorLocCodePickUp.append(m_carPickUpLocKeyReq.getSupplierRawText());
            }

            if (null != vendorLocCodePickUp.toString())
            {
                // m_pCarVendorLocationCodePickUp
                m_bookingItemCar.setCarVendorLocationCodePickUp(vendorLocCodePickUp.toString());
            }
        }

    }

    private void fillFromRequestCarCatalogKey()
    {
        if (null != m_carCatKeyReq)
        {
            if (null != m_carCatKeyReq.getCarPickupLocationKey())
            {
                m_carPickUpLocKeyReq = m_carCatKeyReq.getCarPickupLocationKey();
                fillFromRequestCarPickUpLocation();

            }
            if (null != m_carCatKeyReq.getCarVehicle())
            {
                m_carVehicleReq = m_carCatKeyReq.getCarVehicle();
                fillFromRequestCarVehicle();
            }
        }


    }

    private void fillFromRequestCarRate()
    {
        if (null != m_carRateReq)
        {
            if (null != m_carRateReq.getCarAgreementID())
            {
                // m_pCarAgreementID
                m_bookingItemCar.setCarAgreementID(String.valueOf(m_carRateReq.getCarAgreementID()));
            }

            if (null != m_carRateReq.getCarVendorAgreementCode())
            {
                // m_pCarVendorAgreementNbr
                m_bookingItemCar.setCarVendorAgreementNbr(m_carRateReq.getCarVendorAgreementCode());
            }

            if (null != m_carRateReq.getCorporateDiscountCode())
            {
                // m_pCarCorpDiscountCodeTraveler
                m_bookingItemCar.setCarCorpDiscountCodeTraveler(m_carRateReq.getCorporateDiscountCode());
                // m_pCarCorpDiscountCodeCRSReq
                m_bookingItemCar.setCarCorpDiscountCodeCRSReq(m_carRateReq.getCorporateDiscountCode());
            }

            if (null != m_carRateReq.getRateCode())
            {
                //m_pCarRateCodeTraveler
                m_bookingItemCar.setCarRateCodeTraveler(m_carRateReq.getRateCode());
                // m_pCarRateCodeCRSReq
                m_bookingItemCar.setCarRateCodeCRSReq(m_carRateReq.getRateCode());
            }

            if (null != m_carRateReq.getPromoCode())
            {
                //m_pCarCouponCodeTraveler
                m_bookingItemCar.setCarCouponCodeTraveler(m_carRateReq.getPromoCode());
                //m_pCarCouponCodeCRSReq
                m_bookingItemCar.setCarCouponCodeCRSReq(m_carRateReq.getPromoCode());
            }

            if (null != m_carRateReq.getRatePeriodCode())
            {
                String ratePeriodCode = m_carRateReq.getRatePeriodCode();
                byte ratePeriodId = RatePeriodMap.getRatePlan(ratePeriodCode);
                // m_pCarBasePricePeriodPublishedID
                m_bookingItemCar.setCarBasePricePeriodPublishedID(String.valueOf(ratePeriodId));
            }

            LoyaltyProgramType loyaltyProgram = m_carRateReq.getLoyaltyProgram();
            if (null != loyaltyProgram)
            {
                if (null != loyaltyProgram.getLoyaltyProgramMembershipCode())
                {
                    // m_pCarClubNbrTraveler
                    m_bookingItemCar.setCarClubNbrTraveler(loyaltyProgram.getLoyaltyProgramMembershipCode());
                    // m_pCarClubNbrCRSReq
                    m_bookingItemCar.setCarClubNbrCRSReq(loyaltyProgram.getLoyaltyProgramMembershipCode());
                }

            }
        }

    }

    private void fillFromRequestCarInventoryKey()
    {
        if (null != m_carInvKeyReq)
        {
            DateTime pickUpDate = m_carInvKeyReq.getCarPickUpDateTime();
            DateTime dropOffDate = m_carInvKeyReq.getCarDropOffDateTime();
            /*if (null != pickUpDate && m_bookingItemCar.getUseDateBegin() == null)
            {
                Calendar calPickup = pickUpDate.toCalendar();

                m_bookingItemCar.setUseDateBegin(new Timestamp(calPickup.getTimeInMillis()));
            }
            DateTime dropOffDate = m_carInvKeyReq.getCarDropOffDateTime();
            if (null != dropOffDate && m_bookingItemCarAddReq.getUseDateEnd() == null)
            {
                Calendar calDropOff = dropOffDate.toCalendar();

                m_bookingItemCar.setUseDateEnd(new Timestamp(calDropOff.getTimeInMillis()));
            }*/

            // m_pCarRentalDaysCnt
            if (null != pickUpDate && null != dropOffDate && m_bookingItemCar.getCarRentalDaysCnt() == null)
            {
                m_bookingItemCar
                        .setCarRentalDaysCnt(String.valueOf((short) BillingHelper.getNumberOfBillingDays(pickUpDate, dropOffDate)));
            }

            long vendorId = m_carInvKeyReq.getCarCatalogKey().getVendorSupplierID();
            String vendorCode = getVendorCode(vendorId);

            if (null != vendorCode && (m_bookingItemCar.getCarVendorCode() == null
                    || m_bookingItemCar.getCarVendorCode().length() == 0))
            {
                // m_pCarVendorCode
                m_bookingItemCar.setCarVendorCode(vendorCode);
            }

            if (null != m_carInvKeyReq.getSupplySubsetID())
            {
                // m_pSupplySubsetID
                m_bookingItemCar
                        .setSupplySubsetID(String.valueOf(m_carInvKeyReq.getSupplySubsetID().intValue()));
            }

            m_carCatKeyReq = m_carInvKeyReq.getCarCatalogKey();

            fillFromRequestCarCatalogKey();

            m_carRateReq = m_carInvKeyReq.getCarRate();
            fillFromRequestCarRate();
        }

    }

    private void fillFromRequestCarPriceListCurrencyAmount(CurrencyAmountType currAmtReq, boolean fMaxMarginAmt)
    {
        if (null != currAmtReq)
        {
            if (null != currAmtReq.getCurrencyCode())
            {
                if (fMaxMarginAmt)
                {
                    // m_pMarginMaxCurrencyCode
                    m_bookingItemCar.setMarginMaxCurrencyCode(currAmtReq.getCurrencyCode());
                }
                else
                {
                    // m_pMarginMinCurrencyCode
                    m_bookingItemCar.setMarginMinCurrencyCode(currAmtReq.getCurrencyCode());
                }
            }

            AmountType amt = currAmtReq.getAmount();
            if (null != amt)
            {
                BigDecimal totalAmt = new BigDecimal(BigInteger.valueOf((long) amt.getDecimal()),
                        (int) amt.getDecimalPlaceCount());

                if (fMaxMarginAmt)
                {
                    // m_pMarginMaxAmt
                    m_bookingItemCar.setMarginMaxAmt(CostPriceCalculator.toMoneyScale(totalAmt).toPlainString());
                }
                else
                {
                    // m_pMarginMinAmt
                    m_bookingItemCar.setMarginMinAmt(CostPriceCalculator.toMoneyScale(totalAmt).toPlainString());
                }
            }
        }

    }

    /**
     * Fill the published price amount.
     * This should be run for merchant cars, or GDS net rates cars.
     * GDS net rates cars have the business model of GDSP and a markup percentage.
     *
     * @param testScenario
     * @param markup
     */
    private void fillPublishedPriceAmount(TestScenario testScenario, BigDecimal markup,
                                           CarECommerceSearchResponseType standaloneSearchRsp) throws DataAccessException, SQLException {
        int businessModel = m_carItemInfo.getCarBusinessModelID();

        if(null == standaloneSearchRsp && (businessModel == CommonEnumManager.BusinessModel.Agency.getBusinessModel())){
            return;
        }
        else if(null == standaloneSearchRsp){
            m_bookingItemCar.setPublishedPriceAmt(BigDecimal.ZERO.toPlainString());
            m_bookingItemCar.setCurrencyCodePublishedPrice(m_posCurrencyCode);
            return;
        }
        BigDecimal markupPct = markup;

        boolean shouldUseReferencePrice = VerificationHelper.isPosConfigEnabled(
                PosConfigSettingName.BOOKING_REFERENCEPRICELOG_OVERRIDEPUBLISHEDPRICE, testScenario);



        //get corresponding car from standalone search response
        CarProductType matchedStandaloneCar = null;
        for(final CarSearchResultType carSearchResult : standaloneSearchRsp.getCarSearchResultList().getCarSearchResult())
        {
            for(final CarProductType carProduct : carSearchResult.getCarProductList().getCarProduct())
            {
                if(compareProducts(m_carProdReq, carProduct, testScenario))
                {
                    matchedStandaloneCar = carProduct;
                    break;
                }
            }
            if(null != matchedStandaloneCar)
            {
                break;
            }
        }
        CarItem standaloneCarItem = null == matchedStandaloneCar ? null : fetchCarItem(matchedStandaloneCar.getCarInventoryKey().getCarItemID());

        //get into this if business model is merchant or GDSP (regardless of markup or commission)
        if (businessModel == CommonEnumManager.BusinessModel.Merchant.getBusinessModel() ||
                businessModel == CommonEnumManager.BusinessModel.GDSP.getBusinessModel() || (
                businessModel == CommonEnumManager.BusinessModel.Agency.getBusinessModel() &&
                        m_carItemInfo.isPrepaidBool() && VerificationHelper.isPosConfigEnabled(
                        PosConfigSettingName.BOOKING_LOG_PREPAID_AGENCY_PUBLISHED_PRICE_AMT,
                        testScenario)))
        {
            String publishedCurrencyCode = m_posCurrencyCode;
            BigDecimal publishedPriceAmt = BigDecimal.ZERO;


            //look for published price only if merchant or markup pct > 0 in case of GDSP; that is how it's always been
            if ((businessModel == CommonEnumManager.BusinessModel.Merchant.getBusinessModel() || markupPct.compareTo(BigDecimal.ZERO) > 0)
                    && null != standaloneCarItem && standaloneCarItem.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Agency.getBusinessModel())
            {
                try
                {
                    List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(matchedStandaloneCar.getPriceList(),
                            m_posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
                    BigDecimal publishedPrice = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posPriceList.get(0));;

                    publishedPriceAmt = publishedPrice;
                    publishedCurrencyCode = m_posCurrencyCode;
                }
                catch (Exception e)
                {
                    Assert.fail("Exception when trying to get published price.\r\n " + e.getMessage());

                }
            }

            if (shouldUseReferencePrice)
            {
                //if reference price exists, override any published price details with that
                if ( null != matchedStandaloneCar)
                {
                    List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(matchedStandaloneCar.getPriceList(),
                            m_posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
                    BigDecimal refPrice = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posPriceList.get(0));;

                    if (BigDecimal.ZERO.compareTo(refPrice) < 0)
                    {
                        publishedPriceAmt = refPrice;
                        publishedCurrencyCode = m_posCurrencyCode;
                    }
                }
            }

            //
            // For merchant cars the published price cannot be null.
            //
            m_bookingItemCar.setPublishedPriceAmt(publishedPriceAmt.toPlainString());
            m_bookingItemCar.setCurrencyCodePublishedPrice(publishedCurrencyCode);
        }
    }

    private boolean compareProducts(CarProductType prod1, CarProductType prod2, TestScenario testScenario) throws DataAccessException, SQLException {
        CarCatalogKeyType catalogKey1 = prod1.getCarInventoryKey().getCarCatalogKey();
        CarCatalogKeyType catalogKey2 = prod2.getCarInventoryKey().getCarCatalogKey();
        CarInventoryKeyType inventoryKey1 = prod1.getCarInventoryKey();
        CarInventoryKeyType inventoryKey2 = prod2.getCarInventoryKey();
        CarItem carItem1 = fetchCarItem(inventoryKey1.getCarItemID());
        CarItem carItem2 = fetchCarItem(inventoryKey2.getCarItemID());

        boolean equality = CarNodeComparator.isVendorIdAndSIPPEqual(catalogKey1, catalogKey2, new StringBuilder());

        boolean productsEqual = false;

        if (equality)
        {
            if (CarNodeComparator.isCarLocationKeyEqual(catalogKey1.getCarPickupLocationKey(), catalogKey2.getCarPickupLocationKey())
                    && CarNodeComparator.isCarLocationKeyEqual(catalogKey1.getCarDropOffLocationKey(), catalogKey2.getCarDropOffLocationKey())
                    && 0 == inventoryKey1.getCarPickUpDateTime().compareTo(inventoryKey2.getCarPickUpDateTime())
                    && 0 == inventoryKey1.getCarDropOffDateTime().compareTo(inventoryKey2.getCarDropOffDateTime()))
            {
                //if the reserve product is prepaid agency, then we need to select the corresponding post pay agency
                // offer as published product
                if (carItem1 != null && carItem1.isPrepaidBool() && carItem1.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Agency.getBusinessModel()
                        && VerificationHelper.isPosConfigEnabled(
                        PosConfigSettingName.BOOKING_LOG_PREPAID_AGENCY_PUBLISHED_PRICE_AMT,
                        testScenario))
                {
                    if (carItem2 != null && !carItem2.isPrepaidBool()
                            && carItem2.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Agency.getBusinessModel())
                    {
                        productsEqual = true;
                    }
                    else
                    {
                        productsEqual = false;
                    }
                }
                else
                {
                    productsEqual = true;
                }
            }
        }

        return productsEqual;
    }

    private CarItem fetchCarItem(long carItemId)
    {
        CarItem carItem = null;
        try
        {
            carItem = m_carsInventoryHelper.getCarItemById(carItemId);
        }
        catch (DataAccessException e) {
            Assert.fail("Could not find carItem for carItemId = " + carItemId);
        }
        return carItem;
    }

    private void fillEstimatedTaxAndFeeAmount(CarProductType carProduct, String posuCurrencyCode)
    {
        BookingAmountExpDataGenerator bookingAmountExpDataGenerator = new BookingAmountExpDataGenerator();
        BigDecimal estimatedTaxesAndFees = bookingAmountExpDataGenerator.calculateTaxesAndFees(carProduct.getPriceList(), posuCurrencyCode);

        if(estimatedTaxesAndFees.compareTo(BigDecimal.ZERO) > 0) {
            m_bookingItemCar.setEstimatedTotalTaxAndFeeAmtCurrencyCode(posuCurrencyCode);
        }
        else
        {
            m_bookingItemCar.setEstimatedTotalTaxAndFeeAmtCurrencyCode(m_posCurrencyCode);
        }
        m_bookingItemCar.setEstimatedTotalTaxAndFeeAmt(estimatedTaxesAndFees.toPlainString());
    }

    private void fillDiscountAmount(CarProductType carProduct) throws DataAccessException {
        BigDecimal discountAmount = BigDecimal.ZERO;
        String discountCurrencyCode = "";

        final ClientConfigHelper clientConfigHelper = new ClientConfigHelper(DatasourceHelper.getCarBSDatasource());
        if (clientConfigHelper.checkClientConfig(PojoXmlUtil.getEnvironment(), ClientConfigSettigName.PRICING_SURFACE_DISCOUNT.stringValue(),
                m_clientId, "1"))
        {
            List<PriceType> discountPrice = CostPriceCalculator.getPriceListByFinanceCategoryCode(carProduct.getPriceList(),
                    m_posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.TotalDiscount.getFinanceCategoryCode(), null);

            if (discountPrice != null)
            {
                for (PriceType price : discountPrice)
                {
                    discountAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(price);
                    discountCurrencyCode = price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                }
            }
        }

        else
        {
            List<PriceType> mandatoryCharges = CostPriceCalculator.getPriceListByFinanceCategoryCode(carProduct.getPriceList(),
                    m_posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.MandatoryCharge.getFinanceCategoryCode(), null);

            if (mandatoryCharges != null)
            {
                for (PriceType price : mandatoryCharges)
                {
                    if (price.getDescriptionRawText().equals("TotalDiscount"))
                    {
                        discountAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(price);
                        discountCurrencyCode = price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
                    }
                }
            }
        }

        m_bookingItemCar.setDiscountAmtCurrencyCode(discountCurrencyCode);
        m_bookingItemCar.setDiscountAmt(discountAmount.toPlainString());
    }

    /**
     * Return the markup percentage.  If useTargetPricing() then return the target price markup.
     *
     * @param carProduct
     * @return
     */
    private BigDecimal getMarkupPct(CarProductType carProduct)
    {
        BigDecimal markup = BigDecimal.ZERO;
        if (carProduct != null && carProduct.getCarMarkupInfo() != null)
        {
            MultiplierType markupMul = carProduct.getCarMarkupInfo().getAppliedMarkupRate();
            markup = CostPriceCalculator.calculateMultiplierAsBigDecimal(markupMul);
        }

        return markup;
    }

    private void fillFromRequestCarProduct(PreparePurchaseRequestType request, PreparePurchaseResponseType response,
                                           TestScenario testScenario,
                                           CarECommerceSearchResponseType standaloneSearchRsp) throws DataAccessException, SQLException {

        m_carProdReq = request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        m_bookingItemCar.setReservationGuaranteeMethodID("0");
        if (null != m_carProdReq)
        {
            m_carInvKeyReq = m_carProdReq.getCarInventoryKey();
            fillFromRequestCarInventoryKey();

            if (CarCommonEnumManager.ReservationGuaranteeCategory.Required.name().equals(m_carProdReq.getReservationGuaranteeCategory()))
            {
                //ReservationGuaranteeMethodID
                m_bookingItemCar
                        .setReservationGuaranteeMethodID("1");
            }

            BigDecimal markup = getMarkupPct(m_carProdRsp);
            m_posCurrencyCode = request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData().getCurrencyCode();
            m_posuCurrencyCode = CostPriceCalculator.getCostPosuCurrencyCode(m_carProdReq.getCostList(), m_posCurrencyCode);

            int carItemId = m_carInvKeyReq.getCarItemID().intValue();
            // m_pCarItemID
            m_bookingItemCar.setCarItemID(String.valueOf(carItemId));

            if (m_carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.Agency.getBusinessModel()
                    || m_carItemInfo.getCarBusinessModelID() == CommonEnumManager.BusinessModel.GDSP.getBusinessModel())
            {
                fillEstimatedTaxAndFeeAmount(m_carProdRsp, m_posuCurrencyCode);
            }

            fillPublishedPriceAmount(testScenario, markup, standaloneSearchRsp);

            // m_pMarkupPct
            m_bookingItemCar.setMarkupPct(markup.toPlainString());

            fillFromRequestMarginAmt();
            fillDiscountAmount(m_carProdRsp);
        }
    }


    private void fillFromRequestMarginAmt()
    {
        List<PriceType> posMaxMarginPrices = CostPriceCalculator.getPriceListByFinanceCategoryCode(m_carProdReq.getPriceList(),
                m_posCurrencyCode, CommonEnumManager.FinanceCategoryCode.MaxMarginAmt.getFinanceCategoryCode(), null);
        List<PriceType> posMinMarginPrices = CostPriceCalculator.getPriceListByFinanceCategoryCode(m_carProdReq.getPriceList(),
                m_posCurrencyCode, CommonEnumManager.FinanceCategoryCode.MinMarginAmt.getFinanceCategoryCode(), null);

        fillFromRequestMarginAmt(posMaxMarginPrices, true);
        fillFromRequestMarginAmt(posMinMarginPrices, false);
    }

    private void fillFromRequestMarginAmt(List<PriceType> posMarginPrices, boolean fMaxMarginAmt)
    {
        if (null == posMarginPrices || posMarginPrices.isEmpty())
        {
            return;
        }
        for (PriceType price : posMarginPrices)
        {
            MultiplierOrAmountType multOrAmt = price.getMultiplierOrAmount();
            if (null != multOrAmt)
            {
                fillFromRequestCarPriceListCurrencyAmount(multOrAmt.getCurrencyAmount(), fMaxMarginAmt);
            }
        }
    }

    private void fillFromCarDropOffLocation()
    {
        if (null != m_carDropOffLocKey)
        {
            // m_pAirportCodeDropOff
            if (null != m_carDropOffLocKey.getLocationCode())
            {
                m_bookingItemCar.setAirportCodeDropOff(m_carDropOffLocKey.getLocationCode());
            }

            StringBuffer vendorLocCode = new StringBuffer();
            if (null != m_carDropOffLocKey.getCarLocationCategoryCode())
            {
                vendorLocCode.append(m_carDropOffLocKey.getCarLocationCategoryCode());
            }
            if (null != m_carDropOffLocKey.getSupplierRawText())
            {
                vendorLocCode.append(m_carDropOffLocKey.getSupplierRawText());
            }

            if (null != vendorLocCode.toString())
            {
                // m_pCarVendorLocationCodeDropOff
                m_bookingItemCar.setCarVendorLocationCodeDropOff(vendorLocCode.toString());
            }
        }

    }

    protected void fillFromFreeDistance(DistanceType distance)
    {
        Boolean unlimitedMileage = Boolean.TRUE;

        if (null != distance && null != distance.getDistanceUnitCount() && distance.getDistanceUnitCount() >= 0)
        {
            unlimitedMileage = Boolean.FALSE;
        }

        m_bookingItemCar.setUnlimitedMileageBool(unlimitedMileage ? "1" : "0");
    }

    private void fillFromExtraCostDistance(DistanceType extraDistanceUnit)
    {
        m_bookingItemCar.setMileageUnitInKMBool("0");
        if (extraDistanceUnit != null && null != extraDistanceUnit.getDistanceUnit())
        {
            // m_pMileageUnitInKMBool
            if (extraDistanceUnit.getDistanceUnit().compareToIgnoreCase("KM") == 0)
            {
                m_bookingItemCar.setMileageUnitInKMBool("1");
            }
        }
    }

    private void fillFromResponseExtraCostCurrencyAmount(CurrencyAmountType extraCostPerDistance)
    {
        if (null != extraCostPerDistance)
        {
            // m_pMileageChargeAmtCurrencyCode
            if (null != extraCostPerDistance.getCurrencyCode())
            {
                m_bookingItemCar.setMileageChargeAmtCurrencyCode(extraCostPerDistance.getCurrencyCode());
            }

            AmountType amt = extraCostPerDistance.getAmount();
            if (null != amt)
            {
                BigDecimal mileageChargeAmt = new BigDecimal(BigInteger.valueOf((long) amt.getDecimal()),
                        (int) amt.getDecimalPlaceCount());
                // m_pMileageChargeAmt
                m_bookingItemCar.setMileageChargeAmt(mileageChargeAmt.toPlainString());

            }
        }

    }

    private void fillFromResponseTravelerList(PreparePurchaseResponseType response)
    {
        TravelerListType travelerList = null;
        CarReservationType carResv = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation();
        if (null != carResv)
        {
            travelerList = carResv.getTravelerList();
        }
        if (null != travelerList && null != travelerList.getTraveler() && !travelerList.getTraveler().isEmpty())
        {
            TravelerType traveler = travelerList.getTraveler().get(0);

            if (null != traveler.getLoyaltyProgramList() && null != traveler.getLoyaltyProgramList().getLoyaltyProgram()
                    && !traveler.getLoyaltyProgramList().getLoyaltyProgram().isEmpty())
            {
                LoyaltyProgramType carLoyalProg = getLoyaltyProgramByCategory(traveler.getLoyaltyProgramList(),
                        "Car");

                if (null != carLoyalProg && null != carLoyalProg.getLoyaltyProgramMembershipCode())
                {
                    // m_pCarClubNbrCRSResp
                    m_bookingItemCar.setCarClubNbrCRSResp(carLoyalProg.getLoyaltyProgramMembershipCode());
                }
            }
        }
    }

    private void fillFromResponseCarRate()
    {
        if (null != m_carRateRsp)
        {
            if (null != m_carRateRsp.getCorporateDiscountCode())
            {
                // m_pCarCorpDiscountCodeCRSResp
                m_bookingItemCar.setCarCorpDiscountCodeCRSResp(m_carRateRsp.getCorporateDiscountCode());
            }

            if (null != m_carRateRsp.getRateCode())
            {
                // m_pCarRateCodeCRSResp
                m_bookingItemCar.setCarRateCodeCRSResp(m_carRateRsp.getRateCode());
            }

            if (null != m_carRateRsp.getPromoCode())
            {
                //m_pCarCouponCodeCRSResp
                m_bookingItemCar.setCarCouponCodeCRSResp(m_carRateRsp.getPromoCode());
            }

            LoyaltyProgramType loyaltyProgram = m_carRateRsp.getLoyaltyProgram();
            if (null != loyaltyProgram)
            {
                if (null != loyaltyProgram.getLoyaltyProgramMembershipCode())
                {
                    // m_pCarClubNbrCRSResp
                    m_bookingItemCar.setCarClubNbrCRSResp(loyaltyProgram.getLoyaltyProgramMembershipCode());
                }

            }
        }

    }

    private void fillFromResponseCarInventoryKey()
    {
        if (null != m_carInvKeyRsp)
        {
            DateTime pickUpDate = m_carInvKeyRsp.getCarPickUpDateTime();
            DateTime dropOffDate = m_carInvKeyRsp.getCarDropOffDateTime();

            // m_pCarRentalDaysCnt
            if (null != pickUpDate && null != dropOffDate && m_bookingItemCar.getCarRentalDaysCnt() == null)
            {
                m_bookingItemCar
                        .setCarRentalDaysCnt(String.valueOf((short) BillingHelper.getNumberOfBillingDays(pickUpDate, dropOffDate)));
            }

            if (null != m_carInvKeyRsp.getCarCatalogKey())
            {
                CarCatalogKeyType carCatKey = m_carInvKeyRsp.getCarCatalogKey();
                if (null != carCatKey.getCarDropOffLocationKey())
                {
                    m_carDropOffLocKey = carCatKey.getCarDropOffLocationKey();
                }
                else
                {
                    m_carDropOffLocKey = m_carPickUpLocKeyReq;
                }
                fillFromCarDropOffLocation();

                long vendorId = m_carInvKeyRsp.getCarCatalogKey().getVendorSupplierID();
                String vendorCode = getVendorCode(vendorId);

                if (null != vendorCode && (m_bookingItemCar.getCarVendorCode() == null
                        || m_bookingItemCar.getCarVendorCode().length() == 0))
                {
                    // m_pCarVendorCode
                    m_bookingItemCar.setCarVendorCode(vendorCode);
                }
            }

            m_carRateRsp = m_carInvKeyRsp.getCarRate();
            fillFromResponseCarRate();
        }

    }

    private String getVendorCode(long vendorId)
    {
        String vendorCode = null;

        try
        {
            vendorCode = m_carsInventoryHelper.getCarVendorCodeBySupplierID(vendorId);

        }
        catch (DataAccessException e) {
            Assert.fail("Failed to look up to vendor code for vendor id " + vendorId);
        }

        return vendorCode;

    }

    private void fillFromResponseCarProduct(PreparePurchaseResponseType response) throws DataAccessException {
        m_carProdRsp = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
        if (null != m_carProdRsp)
        {
            m_carInvKeyRsp = m_carProdRsp.getCarInventoryKey();
            fillFromResponseCarInventoryKey();

            CarMileageType carMileage = m_carProdRsp.getCarMileage();
            fillFromCarMileage(carMileage);

        }
    }

    protected void fillFromCarMileage(CarMileageType carMileage)
    {
        m_bookingItemCar.setMileageUnitInKMBool("0");
        if (null != carMileage)
        {
            fillFromFreeDistance(carMileage.getFreeDistance());
            if (null != carMileage.getExtraCostPerDistance())
            {
                fillFromExtraCostDistance(carMileage.getExtraCostPerDistance().getDistance());
                fillFromResponseExtraCostCurrencyAmount(carMileage.getExtraCostPerDistance().getCostCurrencyAmount());
            }
        }
    }

    private void fillFromRequestAirLoyaltyProgram(LoyaltyProgramType loyalProg)
    {
        if (null != loyalProg)
        {
            if (null != loyalProg.getLoyaltyProgramCode())
            {
                // m_pFrequentFlyerPlanName
                m_bookingItemCar.setFrequentFlyerPlanName(loyalProg.getLoyaltyProgramCode());
            }

            if (null != loyalProg.getLoyaltyProgramMembershipCode())
            {
                // m_pFrequentFlyerPlanNumber
                m_bookingItemCar.setFrequentFlyerPlanNumber(loyalProg.getLoyaltyProgramMembershipCode());
            }
        }
    }

    private void fillFromRequestLoyaltyProgramList(TravelerType traveler)
    {
        LoyaltyProgramListType loyalProgList = null;
        if (null != traveler)
        {
            loyalProgList = traveler.getLoyaltyProgramList();
        }

        if (null != loyalProgList && null != loyalProgList.getLoyaltyProgram() && 0 != loyalProgList.getLoyaltyProgram()
                .size())
        {
            LoyaltyProgramType carLoyalProg = getLoyaltyProgramByCategory(loyalProgList, "Car");
            LoyaltyProgramType airLoyalProg = getLoyaltyProgramByCategory(loyalProgList, "Air");

            //TFS 430961 - CarClub number from Traveler/LoyaltyProgramList if it is not already in CarRate
            if (null == m_carRateReq || m_carRateReq.getLoyaltyProgram() == null)
            {
                if (null != carLoyalProg && null != carLoyalProg.getLoyaltyProgramMembershipCode())
                {
                    // m_pCarClubNbrTraveler
                    m_bookingItemCar.setCarClubNbrTraveler(carLoyalProg.getLoyaltyProgramMembershipCode());
                    // m_pCarClubNbrCRSReq
                    m_bookingItemCar.setCarClubNbrCRSReq(carLoyalProg.getLoyaltyProgramMembershipCode());
                }
            }

            //fill Air LoyaltyProgram data
            if (null != airLoyalProg)
            {
                fillFromRequestAirLoyaltyProgram(airLoyalProg);
            }
            //Support Pre-TFS 430961 behavior: first item in LoyalProgramList is Air LoyaltyProgram
            else if (null == carLoyalProg)
            {
                fillFromRequestAirLoyaltyProgram(loyalProgList.getLoyaltyProgram().get(0));
            }
        }
    }

    private LoyaltyProgramType getLoyaltyProgramByCategory(LoyaltyProgramListType loyalProgList,
                                                           String category)
    {
        LoyaltyProgramType loyalProg = null;

        for (LoyaltyProgramType prog : loyalProgList.getLoyaltyProgram())
        {
            if (category.equals(prog.getLoyaltyProgramCategoryCode()))
            {
                loyalProg = prog;
                break; // just the first LoyalProgram is enough
            }
        }

        return loyalProg;
    }

    private void fillFromRequestTravelerList(PreparePurchaseRequestType request)
    {
        TravelerListType travelerList = null;
        travelerList = request.getConfiguredProductData().getCarOfferData().getCarReservation().getTravelerList();

        if (null != travelerList && null != travelerList.getTraveler() && 0 != travelerList.getTraveler().size())
        {
            for (TravelerType traveler : travelerList.getTraveler())
            {
                fillFromRequestLoyaltyProgramList(traveler);
                break; // just the first traveler is enough
            }
        }
    }

    private void fillFromMessageInfo(PreparePurchaseRequestType request)
    {
        MessageInfoType msgInfo = request.getMessageInfo();
        if (null != msgInfo && null != msgInfo.getMessageGUID())
        {
            // m_pTransactionGUID
            //m_bookingItemCar.setTransactionGUID(UUIDHelper.convertStringToByteArray(msgInfo.getMessageGUID()));

        }
    }

    private void fillAdditionalBookingAttributes(TestScenario testScenarios) throws DataAccessException, SQLException {
        if (VerificationHelper.isPosConfigEnabled(PosConfigSettingName.BOOKING_SETADDITIONALBOOKINGATTRIBUTES_ENABLE, testScenarios))
        {
            // m_pProductCategoryCodes
            ProductCategoryCodeListType productCategoryCodes = m_carProdRsp.getCarInventoryKey().getProductCategoryCodeList();
            if (null != productCategoryCodes && null != productCategoryCodes.getProductCategoryCode())
            {
                List<String> listProductCategoryCodes = productCategoryCodes.getProductCategoryCode();
                String productCatCodesCsv = StringUtils.join(listProductCategoryCodes, ',');
                m_bookingItemCar.setProductCategoryCodes(productCatCodesCsv);
            }
            //m_pIsMarkup
            Integer businessModelId = null;
            if (null != m_carItemInfo)
            {
                businessModelId = m_carItemInfo.getCarBusinessModelID();
                BigDecimal markupPct = getMarkupPct(m_carProdRsp);
                if (null != markupPct)
                {
                    m_bookingItemCar.setIsMarkup((CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == businessModelId
                            && markupPct.compareTo(BigDecimal.ZERO) > 0) ? "1" : "0");
                }
            }

            //m_IsCommissionable
            if (null != m_carProdRsp)
            {
                BookingAmountExpDataGenerator bookingAmountExpDataGenerator = new BookingAmountExpDataGenerator();
                CarCommission commissionInfo = bookingAmountExpDataGenerator.getCommissionInfo(m_carProdRsp);

                if (null != commissionInfo && null != commissionInfo.getCommissionPct())
                {
                    //setting the isCommissionable flag to true if it is either Agency or GDSP commission car
                    m_bookingItemCar.setIsCommissionable((CommonEnumManager.BusinessModel.Agency.getBusinessModel() == businessModelId || (
                            CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == businessModelId
                                    && commissionInfo.getCommissionPct().compareTo(BigDecimal.ZERO) > 0)) ? "1" :"0");
                }
                else
                {
                    m_bookingItemCar.setIsCommissionable("0");
                }
            }
        }
    }

    private void fillBookingTypeIdentifier(TestScenario testScenarios) throws DataAccessException, SQLException {
        if (null != m_carItemInfo)
        {
            // m_pAccountingVendorID
            //m_bookingItemCar.setAccountingVendorID(m_carItemInfo.getAccountingVendorID());
            // m_pCarBusinessModelID
            m_bookingItemCar.setCarBusinessModelID(String.valueOf(m_carItemInfo.getCarBusinessModelID()));
            // m_pPrePay
            // m_pMerchantOfRecord
            if (VerificationHelper.isPosConfigEnabled(PosConfigSettingName.BOOKING_LOG_BOOKING_TYPE_IDENTIFIER.toString(), testScenarios))
            {
                Integer businessModelId = m_carItemInfo.getCarBusinessModelID();
                if (businessModelId != null && (businessModelId == CommonEnumManager.BusinessModel.Merchant.getBusinessModel()
                        || businessModelId == CommonEnumManager.BusinessModel.GDSP.getBusinessModel()))
                {
                    m_bookingItemCar.setMerchantOfRecord("Expedia");
                    m_bookingItemCar.setPrePay("1");
                }
                else
                {
                    m_bookingItemCar.setMerchantOfRecord("Supplier");
                    m_bookingItemCar.setPrePay(m_carItemInfo.isPrepaidBool() ? "1" : "0");
                }
            }
        }
    }

    private void fillFromSpooferTransaction(String guid, SpooferTransport spooferTransport) throws IOException {
        final Document spooferDoc = spooferTransport.retrieveRecords(guid);
        NodeList vrsRspList = spooferDoc.getElementsByTagNameNS("*", "VehResRS");
        if(vrsRspList.getLength() > 0)
        {
            fillFromVRS(vrsRspList.item(0));
            return;
        }
        NodeList vcrrRspList = spooferDoc.getElementsByTagNameNS("*", "VehicleCreateReservationRsp");
        if(vcrrRspList.getLength() > 0)
        {
            fillFromVCRR(spooferDoc.getElementsByTagNameNS("*", "VehicleCreateReservationReq").item(0), vcrrRspList.item(0));
            return;
        }
        NodeList acsqRspList = spooferDoc.getElementsByTagNameNS("*", "Car_SellReply");
        if(acsqRspList.getLength() > 0)
        {
            fillFromACSQ(spooferDoc.getElementsByTagNameNS("*", "Car_Sell").item(0),
                    acsqRspList.item(0));
            return;
        }
    }

    private void fillFromAdvisoryText(AdvisoryTextListType advisoryTextContainer)
    {
        if (null != advisoryTextContainer && null != advisoryTextContainer.getAdvisoryText()
                && advisoryTextContainer.getAdvisoryText().size() > 0)
        {
            String advisoryText = advisoryTextContainer.getAdvisoryText().get(0);

            if (null != advisoryText)
            {
                int availableAdvistoryText = Math.min(advisoryText.length(), 90);
                m_bookingItemCar.setCarSupplementalCodeCRSResp(advisoryText.substring(0, availableAdvistoryText));
            }
        }
    }

    private void fillFromRequestCreditCard(PreparePurchaseRequestType request)
    {
        if (null == request.getConfiguredProductData().getCarOfferData().getCarReservation()
                .getPaymentInfo()){
            return;
        }
        CreditCardFormOfPaymentType creditFop = request.getConfiguredProductData().getCarOfferData().getCarReservation()
                .getPaymentInfo().getCreditCardFormOfPayment();

        if (null != creditFop && null != creditFop.getCreditCard())
        {
            CreditCardType creditCard = creditFop.getCreditCard();

            String supplierCode = creditCard.getCreditCardSupplierCode();
            String lastFourDigits = creditCard.getMaskedCreditCardNumber();

            if (null != supplierCode)
            {
                CreditCardSupplier ccSupplier = CreditCardSupplier.fromCode(supplierCode);

                m_bookingItemCar.setCreditCardLastFourDigitNbr(lastFourDigits);
                m_bookingItemCar.setCreditCardTypeID(String.valueOf(ccSupplier.getId()));
            }
        }
    }

    private void fillFromSpecialRequest(PreparePurchaseRequestType request)
    {
        SpecialRequestListType specialRequestList = request.getConfiguredProductData().getCarOfferData().getCarReservation()
                .getSpecialRequestList();
        if (null != specialRequestList && null != specialRequestList.getSpecialRequest() && 0 != specialRequestList
                .getSpecialRequest().size())
        {
            for (SpecialRequestType specialReq : specialRequestList.getSpecialRequest())
            {
                if (null != specialReq.getSpecialRequestRawText())
                {
                    // m_pCarSupplementalCodeTraveler
                    m_bookingItemCar.setCarSupplementalCodeTraveler(specialReq.getSpecialRequestRawText());

                    // m_pCarSupplementalCodeCRSReq
                    m_bookingItemCar.setCarSupplementalCodeCRSReq(specialReq.getSpecialRequestRawText());

                }
            }
        }
    }


    private void fillFromExchangeRateInformation(PreparePurchaseRequestType request)
    {
        MultiplierType exchangeMultiplier = request.getConfiguredProductData().getCarOfferData().getCarReservation()
                .getPointOfSaleToPointOfSupplyExchangeRate();
        String pointOfSaleCurrencyCode = m_posCurrencyCode;

        BigDecimal exchangeRate = null;

        if (null != exchangeMultiplier)
        {
            exchangeRate = CostPriceCalculator.calculateMultiplierAsBigDecimal(exchangeMultiplier);
        }

        m_bookingItemCar.setExchangeRate(exchangeRate.toPlainString());
        m_bookingItemCar.setExchangeRateCurrencyCode(pointOfSaleCurrencyCode);
    }

    private void fillFromVRS(Node nodeObject)
    {
        fillFromAdvisoryText(VRSRsp.getAdvisoryTextList(nodeObject));

        //Response CD code and RateCode
        final Node corpDiscountNmbr = PojoXmlUtil.getNodeByTagName(nodeObject, "RateQualifier").
                getAttributes().getNamedItem("CorpDiscountNmbr");
        if (null != corpDiscountNmbr) {
            m_bookingItemCar.setCarCorpDiscountCodeCRSResp(corpDiscountNmbr.getTextContent());
        }


        final Node rateCode = PojoXmlUtil.getNodeByTagName(nodeObject, "RateQualifier").
                getAttributes().getNamedItem("RateCategory");
        if (null != rateCode && StringUtil.isNotBlank(rateCode.getTextContent())) {
            final int rateCodeLength = Math.min(rateCode.getTextContent().length(), 6);
            m_bookingItemCar.setCarRateCodeCRSResp(rateCode.getTextContent().substring(0,rateCodeLength));
        }
    }

    private void fillFromTVRS(Node nodeObject)
    {

    }

    private void fillFromACSQ(Node reqNode, Node rspNode)
    {
        //CD in request
        final Node customerInfoNode = PojoXmlUtil.getNodeByTagName(reqNode, "customerInfo");
        //ChildNodes of loyaltyNumbersList
        if(null != customerInfoNode)
        {
            final List<Node> customerReferenceInfoNodeList = PojoXmlUtil.getNodesByTagName(customerInfoNode, "customerReferences");
            if (null != customerReferenceInfoNodeList && !customerReferenceInfoNodeList.isEmpty())
            {
                for (final Node cstRefInfo : customerReferenceInfoNodeList)
                {
                    final CustomerReferenceInfo customerReferenceInfo = new CustomerReferenceInfo(cstRefInfo);
                    if (customerReferenceInfo.getReferenceQualifier().equals("CD"))
                    {
                        m_bookingItemCar.setCarCorpDiscountCodeCRSReq(customerReferenceInfo.getReferenceNumber());
                    }
                }
            }
        }

        //CD in response
        final Node carSegmentNode = PojoXmlUtil.getNodeByTagName(rspNode, "carSegment");
        final Node typicalDataNode = PojoXmlUtil.getNodeByTagName(carSegmentNode, "typicalCarData");
        final String rateCode = PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(PojoXmlUtil.getNodeByTagName(typicalDataNode, "rateCodeInfo"),
                "fareCategories"), "fareType").getTextContent();
        m_bookingItemCar.setCarRateCodeCRSResp(rateCode);
        final Node loyaltyNumbersNodeList = PojoXmlUtil.getNodeByTagName(typicalDataNode, "customerInfo");
        final List<Node> referenceQualifierNodeList = PojoXmlUtil.getNodesByTagName(loyaltyNumbersNodeList, "referenceQualifier");
        boolean cdCodeExistInRsp = false;
        if(null != referenceQualifierNodeList && !referenceQualifierNodeList.isEmpty()) {
            for (int count = 0; count < referenceQualifierNodeList.size(); count++) {
                if (referenceQualifierNodeList.get(count).getTextContent().equals("CD")) {
                    cdCodeExistInRsp = true;
                    m_bookingItemCar.setCarCorpDiscountCodeCRSResp(PojoXmlUtil.getNodesByTagName(loyaltyNumbersNodeList, "referenceNumber").get(count).getTextContent());
                }
            }
        }
        if(!cdCodeExistInRsp)
        {
            m_bookingItemCar.setCarCorpDiscountCodeCRSResp(null);
        }

        //AdvisoryText
        final AdvisoryTextListType advisoryTextList = new AdvisoryTextListType();
        if (null == advisoryTextList.getAdvisoryText())
        {
            advisoryTextList.setAdvisoryText(new ArrayList<>());
        }
        ///AmadeusSessionManagerResponse/RawAmadeusXml/PNR_Reply/originDestinationDetails/itineraryInfo/typicalCarData/marketingInfo/text[1]
        final List<Node> texts = PojoXmlUtil.getNodesByTagName(PojoXmlUtil.getNodeByTagName(rspNode, "marketingInfo"), "freeText");
        if (texts != null)
        {
            for (final Node text : texts)
            {
                advisoryTextList.getAdvisoryText().add(text.getTextContent());
            }
        }
        fillFromAdvisoryText(advisoryTextList);
    }


    private void fillFromVCRR(Node reqNode, Node rspNode)
    {
        //Read Rate
        final Node rateNode = PojoXmlUtil.getNodeByTagName(reqNode,"VehicleRate");
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("RateCode"))) {
            m_bookingItemCar.setCarRateCodeCRSReq(rateNode.getAttributes().getNamedItem("RateCode").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("DiscountNumber"))) {
            m_bookingItemCar.setCarCorpDiscountCodeCRSReq(rateNode.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNode.getAttributes().getNamedItem("PromotionalCode"))) {
            m_bookingItemCar.setCarCouponCodeCRSReq(rateNode.getAttributes().getNamedItem("PromotionalCode").getNodeValue());
        }

        //Read voucher
        m_bookingItemCar.setCarVoucherNbr(UAPICommonNodeReader.readVoucher(reqNode));

        //Read SI
        final Node vehicleSpecialRequest = PojoXmlUtil.getNodeByTagName(reqNode,"VehicleSpecialRequest");
        if (!CompareUtil.isObjEmpty(vehicleSpecialRequest)  && !CompareUtil.isObjEmpty(vehicleSpecialRequest.getAttributes().getNamedItem("Key"))) {
            m_bookingItemCar.setCarSupplementalCodeCRSReq(vehicleSpecialRequest.getAttributes().getNamedItem("Key").getNodeValue());
        }

        //LoyaltyNumber
        final List<Node> loyaltyCardNL = PojoXmlUtil.getNodesByTagName(reqNode, "LoyaltyCard");
        for (final Node loyaltyCardN : loyaltyCardNL) {
            if ("Vehicle".equals(loyaltyCardN.getAttributes().getNamedItem("SupplierType").getNodeValue()) &&
                    !CompareUtil.isObjEmpty(loyaltyCardN.getAttributes().getNamedItem("CardNumber"))) {
                m_bookingItemCar.setCarClubNbrCRSReq(loyaltyCardN.getAttributes().getNamedItem("CardNumber").getNodeValue());
            }
        }

        //Response RateCode/CD code
        final Node rateNodeEsp = PojoXmlUtil.getNodeByTagName(rspNode,"VehicleRate");
        if (!CompareUtil.isObjEmpty(rateNodeEsp.getAttributes().getNamedItem("RateCode"))) {
            m_bookingItemCar.setCarRateCodeCRSResp(rateNodeEsp.getAttributes().getNamedItem("RateCode").getNodeValue());
        }
        // CASSS-899 Passing CD code down in every message for hertz via uAPI
        boolean corporateRate = false;
        String discountNumberApplied = "";
        if (!CompareUtil.isObjEmpty(rateNodeEsp.getAttributes().getNamedItem("DiscountNumberApplied"))) {
            discountNumberApplied = rateNodeEsp.getAttributes().getNamedItem("DiscountNumberApplied").getNodeValue();
        }
        if (!CompareUtil.isObjEmpty(rateNodeEsp.getAttributes().getNamedItem("CorporateRate"))) {
            corporateRate = Boolean.parseBoolean(rateNodeEsp.getAttributes().getNamedItem("CorporateRate").getNodeValue());
        }
        if (!CompareUtil.isObjEmpty(rateNodeEsp.getAttributes().getNamedItem("DiscountNumber"))
                && (40 == m_carProdReq.getCarInventoryKey().getCarCatalogKey().getVendorSupplierID()
                || (corporateRate && Boolean.parseBoolean(discountNumberApplied)))) {
            m_bookingItemCar.setCarCorpDiscountCodeCRSResp(rateNodeEsp.getAttributes().getNamedItem("DiscountNumber").getNodeValue());
        }

        //AdvisoryTextList
        fillFromAdvisoryText(UAPICommonNodeReader.readAdvisoryTextList(rspNode));
    }
}
