package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarRateType;
import com.expedia.e3.data.financetypes.defn.v4.*;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.BookingVerificationUtils;
import com.expedia.s3.cars.framework.action.ActionSequenceAbortException;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import com.expedia.s3.cars.framework.test.common.constant.MonetaryCalculationId;
import com.expedia.s3.cars.framework.test.common.constant.RatePeriodMap;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingAmount;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarCommission;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory.CarsInventoryDataSource;
import com.expedia.s3.cars.framework.test.common.utils.CostPriceCalculator;
import expedia.om.supply.messages.defn.v1.PreparePurchaseRequestType;
import expedia.om.supply.messages.defn.v1.PreparePurchaseResponseType;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/4/2018.
 */
@SuppressWarnings("PMD")
public class BookingAmountExpDataGenerator {

    public List<BookingAmount> getExpBookingAmountRows(PreparePurchaseRequestType request, PreparePurchaseResponseType response) throws DataAccessException, ActionSequenceAbortException

    {
        List<BookingAmount> bookingAmountList = new ArrayList<>();
        //Get pos/posu currency code
        final String posCurrencyCode = request.getConfiguredProductData().getCarOfferData().getCarLegacyBookingData().getCurrencyCode();
        final CarProductType requestCar = request.getConfiguredProductData().getCarOfferData().getCarReservation().getCarProduct();
        final String posuCurrencyCode = CostPriceCalculator.getCostPosuCurrencyCode(requestCar.getCostList(), posCurrencyCode);
        //Get car business model ID
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        int carBusinessModelId = carsInventoryDataSource.getCarBusinessModelIDFromCarItem(requestCar.getCarInventoryKey().getCarItemID());
        //Get bookingID/bookingItemID/tuid for every row
        String bookingID = BookingVerificationUtils.getBookingID(response);
        String bookingItemID = BookingVerificationUtils.getBookingItemID(response);
        String tuid = String.valueOf(request.getPointOfSaleCustomerIdentifier().getExpediaLoginUserKey().getUserID());
        //Add expected rows - total and commission from DB for non GDSP net rates car
        CarProductType rspCar = response.getPreparedItems().getBookedItemList().getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation().getCarProduct();
        addBookingAmountRow(bookingAmountList, getExpTotalAmountRow(rspCar, carBusinessModelId, posCurrencyCode, posuCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        //If provider commission exist, then expected commission row will be null
        addBookingAmountRow(bookingAmountList, getExpCommissionAmountRow(rspCar, carBusinessModelId, posCurrencyCode, posuCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        //Add for GDSP net reates car
        addBookingAmountRow(bookingAmountList, getExpGDSPNetRatesTotalAmountRow(rspCar, carBusinessModelId, posCurrencyCode, posuCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        //Add when there is no markup also no commission in DB
        addBookingAmountRow(bookingAmountList, getExpGDSPBaseRateRowAtZeroMarkup(rspCar, carBusinessModelId, posCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        //Add for provider commission if exist
        addBookingAmountRow(bookingAmountList, getExpProviderSpecifiedCommissionAmountRow(rspCar, posCurrencyCode, posuCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        //Add for provider transaction fee if exist
        addBookingAmountRow(bookingAmountList, getExpProviderTransactionFeesAmountRow(rspCar, posCurrencyCode, posuCurrencyCode),
                bookingID, bookingItemID, tuid, posCurrencyCode, posuCurrencyCode);
        return bookingAmountList;
    }

    protected void addBookingAmountRow(List<BookingAmount> bookingAmountList, BookingAmount amountRow, String bookingID, String bookingItemID,
                                       String tuid, String posCurrencyCode, String posuCurrencyCode)
    {
        if(null != amountRow) {
            amountRow.setBookingID(bookingID);
            amountRow.setBookingItemID(bookingItemID);
            amountRow.setCreateTUID(tuid);
            amountRow.setCancelBool("0");
            amountRow.setBookingItemTypeID("11");
            amountRow.setBookingAmountLevelID(String.valueOf(AmountLevelId.BOOKING_ITEM.intValue()));
            amountRow.setBookingAmountRefCodeCost("");
            amountRow.setBookingAmountRefCodePrice("");
            amountRow.setBookingAmountSeqNbr(null);
            amountRow.setBookingItemInventorySeqNbr(null);
            amountRow.setCurrencyCodeCost(posuCurrencyCode);
            amountRow.setCurrencyCodePrice(posCurrencyCode);
            bookingAmountList.add(amountRow);
        }
    }


    protected String determineDescriptionForBaseRate(CarRateType carRate, BigDecimal baseRate)
    {

        String descriptionRawText = "";

        switch (carRate.getRatePeriodCode())
        {
            case "Weekly":
                descriptionRawText = "TBR, Weekly Rate = ";
                break;
            case "Daily":
                descriptionRawText = "TBR, Daily Rate = ";
                break;
            case "Monthly":
                descriptionRawText = "TBR, Monthly rate = ";
                break;
            case "Weekend":
                descriptionRawText = "TBR, Weekend rate = ";
                break;
            default:
                descriptionRawText = "TBR = ";
        }

        return descriptionRawText + baseRate.toPlainString();
    }

    protected int determineMonetaryCalculationIdForTotalRate(CostType baseRate, boolean fGDSP, String ratePeriodCode)
    {
        long monetaryCalculationId = 0;

        if (fGDSP)
        {
            if (null != baseRate && null != baseRate.getLegacyFinanceKey())
            {
                monetaryCalculationId = baseRate.getLegacyFinanceKey().getLegacyMonetaryCalculationID();
            }
            else
            {
                //we may not receive a base rate from details in some cases
                //in those cases, get the monetary calc ID based on the rate period code
                monetaryCalculationId = RatePeriodMap.getMonetaryCalculationId(ratePeriodCode).intValue();
            }
        }
        else
        {
            monetaryCalculationId = MonetaryCalculationId.AGENCY_CARS_ESTIMATED_TOTAL_BASE_RATE.intValue();
        }

        return (int) monetaryCalculationId;
    }

    private String getDescPrice(CarProductType product, CostType baseRate, BigDecimal transactionAmtPrice,
                                PriceType posPrice, int carBusinessModelID) {
        String descPrice = "";

        // get the BookingAmountDescPrice  for agency cars here.
        if (carBusinessModelID == CommonEnumManager.BusinessModel.Agency.getBusinessModel())
        {
            BigDecimal baseRateAmt = CostPriceCalculator.calculateAmountAsBigDecimal(baseRate.getMultiplierOrAmount().getCurrencyAmount().getAmount());
            descPrice = determineDescriptionForBaseRate(product.getCarInventoryKey().getCarRate(), baseRateAmt) + " "
                    + baseRate.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
        }
        // get the BookingAmountDescPrice  for GDSP cars here.
        else if (carBusinessModelID == CommonEnumManager.BusinessModel.GDSP.getBusinessModel())
        {
            descPrice = determineDescriptionForBaseRate(product.getCarInventoryKey().getCarRate(), transactionAmtPrice)
                    + " " + posPrice.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
        }


        return descPrice;

    }

    /**
     * f TFS-53571: log the Cost Total and Price Total for the GDSP net rates cars.
     * The cost amount is the posu price minus markup
     * The price amount is the pos price
     */
    protected BookingAmount getExpGDSPNetRatesTotalAmountRow(CarProductType product, int carBusinessModelID, String posCurrencyCode, String posuCurrencyCode) throws DataAccessException {
        BigDecimal markupPct = getMarkupPct(product);

        // only  for GDSP cars with a markup percentage (GDSP net rates)
        if (CommonEnumManager.BusinessModel.GDSP.getBusinessModel() != carBusinessModelID || (
                CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == carBusinessModelID && markupPct.compareTo(BigDecimal.ZERO) == 0))
        {
            //return null for non-net rates car
            return null;
        }

        List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
        List<PriceType> posuPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);

        BigDecimal markupAmount;
        markupAmount = getMarkupAmountFromMargin(product.getPriceList(), posCurrencyCode, posuCurrencyCode);

        // getting the base rate to log the MonetaryCalculationID of the base rate,
        // similar as to what is done in logTotalAmounts()
        CostType baseRate = CostPriceCalculator.getCostByFinanceCategoryCode(product.getCostList(), posuCurrencyCode,
                CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode(), null, null);

        String ratePeriodCode = getRatePeriodCodeOrEmpty(product);

        PriceType posPrice = posPriceList.get(0);
        PriceType posuPrice = findPriceLike(posPrice, posuPriceList);

        BigDecimal posAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posPrice);
        BigDecimal posuAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posuPrice);

        BigDecimal posuAmountWithoutMarkup = posuAmount.subtract(markupAmount);
        BigDecimal posuAmountCost;
        BigDecimal posAmountPrice;

        posuAmountCost = posuAmountWithoutMarkup;
        posAmountPrice = posAmount;

        int monetaryCalculationId = determineMonetaryCalculationIdForTotalRate(baseRate,
                carBusinessModelID == CommonEnumManager.BusinessModel.GDSP.getBusinessModel(), ratePeriodCode);

        BookingAmount amountRow = new BookingAmount();
        amountRow.setMonetaryCalculationID(String.valueOf(monetaryCalculationId));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(MonetaryCalculationSystemId.MERCHANT_BASE_RATE.shortValue()));
        amountRow.setMonetaryClassID(String.valueOf(MonetaryClassId.BASE_AMOUNT.intValue()));
        amountRow.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(posuAmountCost));
        amountRow.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(posAmountPrice));
        amountRow.setBookingAmountDescCost("Base Rate Cost");
        amountRow.setBookingAmountDescPrice("BaseRate");

        return amountRow;
    }

    protected BookingAmount getExpGDSPBaseRateRowAtZeroMarkup(CarProductType product, int carBusinessModelId, String posCurrencyCode) throws ActionSequenceAbortException, DataAccessException {
        BigDecimal markupPct = getMarkupPct(product);
        CarCommission commissionInfo = getCommissionInfo(product);

        // only  for GDSP cars with a zero markup GDSP net rates)
        if (CommonEnumManager.BusinessModel.GDSP.getBusinessModel() != carBusinessModelId || (
                CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == carBusinessModelId && (markupPct.compareTo(BigDecimal.ZERO) != 0
                || isCommissionPctValid(commissionInfo))))
        {
            //return null for non-net rates car
            return null;
        }

        final String posuCurrencyCode = CostPriceCalculator.getCostPosuCurrencyCode(product.getCostList(), posCurrencyCode);
        final CarsInventoryDataSource carsInventoryDataSource = new CarsInventoryDataSource(DatasourceHelper.getCarInventoryDatasource());
        final int carBusinessModelID = carsInventoryDataSource.getCarBusinessModelIDFromCarItem(product.getCarInventoryKey().getCarItemID());

        List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
        List<PriceType> posuPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);

        // getting the base rate to log the MonetaryCalculationID of the base rate,
        // similar as to what is done in logTotalAmounts()
        CostType baseRate = CostPriceCalculator.getCostByFinanceCategoryCode(product.getCostList(), posuCurrencyCode,
                CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode(), null, null);

        String ratePeriodCode = getRatePeriodCodeOrEmpty(product);

        PriceType posPrice = posPriceList.get(0);
        PriceType posuPrice = findPriceLike(posPrice, posuPriceList);

        BigDecimal posAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posPrice);
        BigDecimal posuAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posuPrice);

        BigDecimal posuAmountCost;
        BigDecimal posAmountPrice;

        posuAmountCost = posuAmount;
        posAmountPrice = posAmount;

        int monetaryCalculationId = determineMonetaryCalculationIdForTotalRate(baseRate,
                carBusinessModelID == CommonEnumManager.BusinessModel.GDSP.getBusinessModel(), ratePeriodCode);
        BookingAmount amountRow = new BookingAmount();
        amountRow.setBookingAmountDescCost("");

        amountRow.setBookingAmountLevelID(String.valueOf(AmountLevelId.BOOKING_ITEM.intValue()));
        amountRow.setBookingAmountRefCodeCost("");
        amountRow.setBookingAmountRefCodePrice("");
        amountRow.setBookingAmountSeqNbr(null);
        amountRow.setBookingItemInventorySeqNbr(null);
        amountRow.setCurrencyCodeCost(posuPrice.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());
        amountRow.setCurrencyCodePrice(posPrice.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode());
        amountRow.setMonetaryCalculationID(String.valueOf(monetaryCalculationId));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(MonetaryCalculationSystemId.MERCHANT_BASE_RATE.shortValue()));
        amountRow.setMonetaryClassID(String.valueOf(MonetaryClassId.BASE_AMOUNT.intValue()));
        amountRow.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(posuAmountCost));
        amountRow.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(posAmountPrice));
        amountRow.setBookingAmountDescCost("Base Rate Cost");
        amountRow.setBookingAmountDescPrice("BaseRate");

        return amountRow;
    }

    private String getRatePeriodCodeOrEmpty(CarProductType product)
    {
        String ratePeriodCode = "";

        if (product.getCarInventoryKey() != null && product.getCarInventoryKey().getCarRate() != null
                && product.getCarInventoryKey().getCarRate().getRatePeriodCode() != null)
        {
            ratePeriodCode = product.getCarInventoryKey().getCarRate().getRatePeriodCode();
        }
        return ratePeriodCode;
    }

    // get the markup from the max or min margin
    // first check the max margin, then the min margin
    private BigDecimal getMarkupAmountFromMargin(PriceListType priceList, String posCurrencyCode, String posuCurrencyCode)
    {
        BigDecimal markupAmount;
        List<PriceType> posMaxMarginList = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList,
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.MaxMarginAmt.getFinanceCategoryCode(), null);
        List<PriceType> posuMaxMarginList = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList,
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.MaxMarginAmt.getFinanceCategoryCode(), null);

        if (null == posMaxMarginList || posMaxMarginList.isEmpty())
        {
            List<PriceType> posMinMarginList = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList,
                    posCurrencyCode, CommonEnumManager.FinanceCategoryCode.MinMarginAmt.getFinanceCategoryCode(), null);
            List<PriceType> posuMinMarginList = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList,
                    posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.MinMarginAmt.getFinanceCategoryCode(), null);

            if (null == posMinMarginList || posMinMarginList.isEmpty())
            {
                markupAmount = BigDecimal.ZERO;
            }
            else
            {
                // there should only be 1 min margin for posu
                PriceType minMargin = findPriceLike(posMinMarginList.get(0), posuMinMarginList);
                markupAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(minMargin);
            }
        }
        else
        {
            // there should only be 1 max margin for posu
            PriceType maxMargin = findPriceLike(posMaxMarginList.get(0), posuMaxMarginList);
            markupAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(maxMargin);
        }
        return markupAmount;
    }

    /*
    Method to get expected total amount row into DB - for Agency and GDSP commission car(GDS net rate has different logic)
     */
    protected BookingAmount getExpTotalAmountRow(CarProductType product, int carBusinessModelID, String posCurrencyCode, String posuCurrencyCode
                                   )
            throws DataAccessException {
        //Get commission info
        CarCommission commissionInfo = getCommissionInfo(product);

        //If it's GDSP net rate car(not agency, and don't have commission from CarCommission table), then skip
        if (CommonEnumManager.BusinessModel.Agency.getBusinessModel() != carBusinessModelID && !isCommissionPctValid(commissionInfo))
        {
            return null;
        }

        //Get total price
        List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
        List<PriceType> posuPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(product.getPriceList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);

        //Get Base Cost
        CostType baseRate = CostPriceCalculator.getCostByFinanceCategoryCode(product.getCostList(), posuCurrencyCode,
                CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode(), null, null);

        //Get TaxesAndFeesAmount(misc and total discount from priceList)
        BigDecimal posuTaxesAndFeesAmount = calculateTaxesAndFees(product.getPriceList(), posuCurrencyCode);
        BigDecimal posTaxesAndFees = calculateTaxesAndFees(product.getPriceList(), posCurrencyCode);


        //If total price not exist, then skip
        if (null == posPriceList)
        {
            return null;
        }

        //Get total amount
        PriceType posPrice = posPriceList.get(0);
        PriceType posuPrice = findPriceLike(posPrice, posuPriceList);
        BigDecimal posuPriceAmt = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posuPrice);
        BigDecimal posPriceAmt = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posPrice);

        //Get expected monetaryCalculationId
        int monetaryCalculationId = determineMonetaryCalculationIdForTotalRate(baseRate,
                carBusinessModelID == CommonEnumManager.BusinessModel.GDSP.getBusinessModel(), getRatePeriodCodeOrEmpty(product));

        BookingAmount amountRow = new BookingAmount();
        amountRow.setBookingAmountDescCost("");
        amountRow.setMonetaryCalculationID(String.valueOf(monetaryCalculationId));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(MonetaryCalculationSystemId.MONETARY_TYPE_GENERIC.shortValue()));
        amountRow.setMonetaryClassID(String.valueOf(MonetaryClassId.BASE_AMOUNT.intValue()));
        BigDecimal transactionAmtCost;
        BigDecimal transactionAmtPrice;
        // f 1378. Resolution: We should not subtract the misc/mandatory charges charges
        // when logging total amount or when calculating commissions for GDSP cars.
        if (CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == carBusinessModelID)
        {
            transactionAmtCost = CostPriceCalculator.toMoneyScale(posuPriceAmt);
            transactionAmtPrice = CostPriceCalculator.toMoneyScale(posPriceAmt);

        }
        else
        {
            transactionAmtCost = CostPriceCalculator.toMoneyScale(posuPriceAmt.subtract(posuTaxesAndFeesAmount));
            transactionAmtPrice = CostPriceCalculator.toMoneyScale(posPriceAmt.subtract(posTaxesAndFees));
        }
        amountRow.setTransactionAmtCost(transactionAmtCost);
        amountRow.setTransactionAmtPrice(transactionAmtPrice);
        amountRow.setBookingAmountDescPrice(getDescPrice(product, baseRate, transactionAmtPrice, posPrice, carBusinessModelID));

        return amountRow;
    }

    /**
     * TFS 216871 - log transaction fees cost element - this is for micronnexus car
     */
    protected BookingAmount getExpProviderTransactionFeesAmountRow(CarProductType product, String posCurrencyCode, String posuCurrencyCode) throws DataAccessException {
        //Get ProviderTransactionFees from CostList
        List<CostType> posCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(product.getCostList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.ProviderTransactionFees.getFinanceCategoryCode(), null, null);
        List<CostType> posuCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(product.getCostList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.ProviderTransactionFees.getFinanceCategoryCode(), null, null);

        //posu cost list for transaction fee should've been sent already from the SCS (MNSCS for now)
        if (CollectionUtils.isEmpty(posuCostList))
        {
            // f 289466:if pos and posu currency are same then posuCostList will be null, so just use the posCostList.
            posuCostList = posCostList;
        }

        //Skip if ProviderTransactionFees not exist
        if (CollectionUtils.isEmpty(posuCostList))
        {
            return null;
        }

        //Get ProviderTransactionFees amount
        CostType posuCost = posuCostList.get(0);
        CostType posCost = findCostLike(posuCost, posCostList);
        BigDecimal posuCostAmt = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posuCost);

        //Skip if ProviderTransactionFees acount is zero
        if (BigDecimal.ZERO.equals(posuCostAmt.stripTrailingZeros()))
        {
            return null;
        }

        String costDesc = posuCost.getDescriptionRawText();
        if (costDesc == null || costDesc.trim().isEmpty())
        {
            costDesc = "Provider Transaction Fees";
        }

        Byte classId = MonetaryClassId.FEE.byteValue();//18
        Short calcSysId = MonetaryCalculationSystemId.MICRONNEXUS_TRANSACTION_FEE.shortValue(); //92
        Integer calcId = MonetaryCalculationId.AGENCY_CARS_TOTAL.intValue();//0

        if (posuCost.getLegacyFinanceKey() != null)
        {
            LegacyFinanceKeyType legacyKey = posuCost.getLegacyFinanceKey();
            classId = (byte) legacyKey.getLegacyMonetaryClassID();
            calcSysId = (short) legacyKey.getLegacyMonetaryCalculationSystemID();
            calcId = (int) legacyKey.getLegacyMonetaryCalculationID();
        }

        BookingAmount amountRow = new BookingAmount();
        amountRow.setMonetaryCalculationID(String.valueOf(calcId));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(calcSysId));
        amountRow.setMonetaryClassID(String.valueOf(classId));
        amountRow.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(posuCostAmt));
        amountRow.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO));
        amountRow.setBookingAmountDescCost(costDesc);
        amountRow.setBookingAmountDescPrice("");

        return amountRow;

    }


    protected BigDecimal calculateTaxesAndFees(PriceListType priceList, String currencyCode)
    {
        BigDecimal posTaxesAndFeesAmount = BigDecimal.ZERO;

        final List<PriceType> estimatedTotalTaxesAndFeesList = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList, currencyCode,
                CommonEnumManager.FinanceCategoryCode.EstimatedTotalTaxesAndFees.getFinanceCategoryCode(), null);
        //final List<PriceType> discountPrices = CostPriceCalculator.getPriceListByFinanceCategoryCode(priceList, currencyCode,
             //   CommonEnumManager.FinanceCategoryCode.TotalDiscount.getFinanceCategoryCode(), null);

        if (estimatedTotalTaxesAndFeesList != null && !estimatedTotalTaxesAndFeesList.isEmpty())
        {
            posTaxesAndFeesAmount = CostPriceCalculator.getAmountMultipliedByFinUnitCount(estimatedTotalTaxesAndFeesList.get(0));
        }

        return posTaxesAndFeesAmount;
    }



    /*
     * It's possible to index the pos to posu mapping ahead of time.  While this
     * would be much faster [ O(1) vs O(n) ] for lookups it would be more complicated to implement.
     * Since each finance application code is going to have two or three elements
     * maximum I think this a linear search is acceptable.
     */
    protected CostType findCostLike(CostType referenceCost, List<CostType> potentialMatches)
    {
        if (null == potentialMatches)
        {
            return referenceCost;
        }

        CostType foundCost = referenceCost;

        BookingAmountCostPriceKey key = new BookingAmountCostPriceKey(referenceCost);

        for (CostType cost : potentialMatches)
        {
            if (key.equals(new BookingAmountCostPriceKey(cost)))
            {
                foundCost = cost;
            }
        }

        return foundCost;
    }

    public static PriceType findPriceLike(PriceType referencePrice, List<PriceType> potentialMatches)
    {
        if (null == potentialMatches)
        {
            return referencePrice;
        }

        PriceType foundPrice = referencePrice;

        BookingAmountCostPriceKey key = new BookingAmountCostPriceKey(referencePrice);

        for (PriceType price : potentialMatches)
        {
            if (key.equals(new BookingAmountCostPriceKey(price)))
            {
                foundPrice = price;
            }
        }

        return foundPrice;
    }

    private String getBusinessModelTypeString(int carBusinessModelId)
    {
        String businessModelType = null;
        if (CommonEnumManager.BusinessModel.Agency.getBusinessModel() == carBusinessModelId)
        {
            businessModelType = "Agency";
        }
        else if (CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == carBusinessModelId)
        {
            businessModelType = "GDSP merchant";
        }
        else if (CommonEnumManager.BusinessModel.Merchant.getBusinessModel() == carBusinessModelId)
        {
            businessModelType = "Merchant";

        }
        else
        {
            businessModelType = "unknown";

        }

        return businessModelType;

    }

    private Boolean hasProviderSpecifiedCommission(CostListType costList, String posCurrencyCode)
    {
        List<CostType> commission = CostPriceCalculator.getCostListByFinanceCategoryCode(costList, posCurrencyCode,
                CommonEnumManager.FinanceCategoryCode.Commission.getFinanceCategoryCode(), null, null);

        if (commission != null && commission.size() > 0)
        {
            return true;
        }
        return false;
    }

    /*
        Method to get expected privder commission amount row into DB for micronnexus
   */
    private BookingAmount getExpProviderSpecifiedCommissionAmountRow(CarProductType product, String posCurrencyCode, String posuCurrencyCode)
    {
        //Get provider commission from CostList
        List<CostType> posCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(product.getCostList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Commission.getFinanceCategoryCode(), null, null);
        List<CostType> posuCostList = CostPriceCalculator.getCostListByFinanceCategoryCode(product.getCostList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.Commission.getFinanceCategoryCode(), null, null);
        if(CollectionUtils.isEmpty(posuCostList))
        {
            return null;
        }
        CostType posuCost = posuCostList.get(0);
        BigDecimal posuCostAmt = CostPriceCalculator.calculateAmountAsBigDecimal(posuCost.getMultiplierOrAmount().getCurrencyAmount().getAmount());

        //If there is no provider commission, then skip
        if (BigDecimal.ZERO.equals(posuCostAmt.stripTrailingZeros()))
        {
            return null;
        }

        //get expected provider commission log
        BookingAmount amountRow = new BookingAmount();
        amountRow.setBookingAmountDescCost("");
        amountRow.setMonetaryCalculationID(String.valueOf(posuCost.getLegacyFinanceKey().getLegacyMonetaryCalculationID()));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(posuCost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID()));
        amountRow.setMonetaryClassID(String.valueOf(MonetaryClassId.COMMISSION.intValue()));
        amountRow.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(posuCostAmt.negate()));
        amountRow.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO));
        amountRow.setBookingAmountDescPrice(posuCost.getDescriptionRawText());

        return amountRow;

    }

    /**
     * Method to get expected commission amount row into DB - commission will be logged for Agency car and GDSP commission car
     * if provider specified commission exist, then no need to log this commission from CarComission table
     */
    protected BookingAmount getExpCommissionAmountRow(CarProductType carProduct, int carBusinessModelId, String posCurrencyCode,
                                                      String posuCurrencyCode) throws DataAccessException {

        //if provider specified commission(micronnexus) exist, then no need to log this commission from CarComission table
        if (hasProviderSpecifiedCommission(carProduct.getCostList(), posuCurrencyCode))
        {
            return null;
        }

        List<PriceType> posPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(carProduct.getPriceList(),
                posCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);
        List<PriceType> posuPriceList = CostPriceCalculator.getPriceListByFinanceCategoryCode(carProduct.getPriceList(),
                posuCurrencyCode, CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode(), null);

        CarCommission commissionInfo = getCommissionInfo(carProduct);

        //For GDS net rate car(no commission  from CarCommission table, we will not log commission row.
        if (CommonEnumManager.BusinessModel.Agency.getBusinessModel() != carBusinessModelId && !isCommissionPctValid(commissionInfo))
        {
            return null;
        }

        //get taxes and fees for Agency car - misc + total discount
        BigDecimal posuTaxesAndFeesAmount = calculateTaxesAndFees(carProduct.getPriceList(), posuCurrencyCode);

        //If total price not exist, we should not log
        if (null == posPriceList)
        {
            return null;
        }

        //Get total posu price amount
        PriceType posPrice = posPriceList.get(0);
        PriceType posuPrice = findPriceLike(posPrice, posuPriceList);
        BigDecimal posuPriceAmt = CostPriceCalculator.getAmountMultipliedByFinUnitCount(posuPrice);
        BigDecimal transactionAmtCost;

        // For GDSP car, apply commission to total; for Agency car, should minus total taxes and fees
        if (CommonEnumManager.BusinessModel.GDSP.getBusinessModel() == carBusinessModelId)
        {
            transactionAmtCost = CostPriceCalculator.toMoneyScale(posuPriceAmt);

        }
        else
        {
            transactionAmtCost = CostPriceCalculator.toMoneyScale(posuPriceAmt.subtract(posuTaxesAndFeesAmount));
        }
        BigDecimal posuCommissionAmt = transactionAmtCost.multiply(commissionInfo.getCommissionPct());

        //If commission amount is zero, then should have no commission log
        if (BigDecimal.ZERO.equals(posuCommissionAmt.stripTrailingZeros()))
        {
            return null;
        }

        //Create expected row
        BookingAmount amountRow = new BookingAmount();
        amountRow.setBookingAmountDescCost("");
        amountRow.setMonetaryCalculationID(String.valueOf(commissionInfo.getCarCommissionLogID()));
        amountRow.setMonetaryCalculationSystemID(String.valueOf(MonetaryCalculationSystemId.CAR_AGENCY_COMMISSION_LOG.shortValue()));
        amountRow.setMonetaryClassID(String.valueOf(MonetaryClassId.COMMISSION.intValue()));

        amountRow.setTransactionAmtCost(CostPriceCalculator.toMoneyScale(posuCommissionAmt.negate()));
        amountRow.setTransactionAmtPrice(CostPriceCalculator.toMoneyScale(BigDecimal.ZERO));
        amountRow.setBookingAmountDescPrice("Commission for " + getBusinessModelTypeString(carBusinessModelId) + " car rental");

        return amountRow;

    }


    protected CarCommission getCommissionInfo(CarProductType carProduct) throws DataAccessException {
        CarsInventoryHelper carsInventoryHelper = new CarsInventoryHelper(DatasourceHelper.getCarInventoryDatasource());
        return carsInventoryHelper.getCommissionInfoByCarCatalogKey(carProduct.getCarInventoryKey());
    }


    protected boolean isCommissionPctValid(CarCommission commissionInfo)
    {
        if (null == commissionInfo)
        {
            return false;
        }

        if (commissionInfo.getCommissionPct() != null
                && commissionInfo.getCommissionPct().compareTo(BigDecimal.ZERO) != 0)
        {
            return true;
        }

        return false;
    }

    /**
     * Return the markup percentage.
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




}
