package com.expedia.s3.cars.framework.test.common.utils;

import com.expedia.e3.data.basetypes.defn.v4.AmountType;
import com.expedia.e3.data.basetypes.defn.v4.MultiplierType;
import com.expedia.e3.data.financetypes.defn.v4.*;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by v-mechen on 11/30/2016.
 */
@SuppressWarnings("PMD")
public final class CostPriceCalculator {
    private CostPriceCalculator(){

    }

    public static double calculateAmountFromCurrencyAmountOrMultiplier(CurrencyAmountType currencyAmount, MultiplierType multiplier, 
                                                                       int roundDigits)
    {
        double amount = 0;
        int amountActual = 0;
        Long decimaiCountActual = 0L;
        if (null != currencyAmount)
        {
            amountActual = currencyAmount.getAmount().getDecimal();
            decimaiCountActual = currencyAmount.getAmount().getDecimalPlaceCount();
        }
        if (null != multiplier)
        {
            amountActual = multiplier.getDecimal();
            decimaiCountActual = multiplier.getDecimalPlaceCount();
        }
        amount = (double)amountActual / (double)Math.pow(10, decimaiCountActual);
        if (roundDigits > 0){
            final BigDecimal amountB = new BigDecimal(String.valueOf(amount));
            amount = amountB.setScale(roundDigits , BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }
  
    public static double calculateCostAmount(CostType cost, int roundDigits, boolean applyFinanceApplicationUnitCount) {
        double amount = 0;
        boolean multiplierBoolean = true;
        final MultiplierOrAmountType multiplierOrAmount = cost.getMultiplierOrAmount();
        if (multiplierOrAmount != null && multiplierOrAmount.getCurrencyAmount() != null) {
            if (StringUtils.isNotEmpty(multiplierOrAmount.getCurrencyAmount().getCurrencyCode())) {
                multiplierBoolean = false;
            }
            amount = multiplierBoolean ? calculateAmountFromCurrencyAmountOrMultiplier(null, multiplierOrAmount.getMultiplier(), 0) :
                    calculateAmountFromCurrencyAmountOrMultiplier(multiplierOrAmount.getCurrencyAmount(), null, 0);
        }
        //In most cases, we need to multiple FinanceApplicationUnitCount, but for some cost, like Commission, we just need the amount, FinanceApplicationUnitCount is 0
        if (applyFinanceApplicationUnitCount) {
            amount = amount * cost.getFinanceApplicationUnitCount();
        }
        if (roundDigits > 0) {
            final BigDecimal amountB = new BigDecimal(String.valueOf(amount));
            amount = amountB.setScale(roundDigits, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }

    //FinanceApplicationCode and DescriptionRawText can be used as optional parameter
    public static double getCostAmountByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode, int roundDigits,
                                                            String financeApplicationCode, String description, String rejectFinanceApplicationCode,
                                                            boolean applyFinanceApplicationUnitCount)
    {
        double amount = 0;
        for (final CostType cost : costList.getCost())
        {
            if(shouldCalCost(cost, currencyCode, financeCategoryCode, financeApplicationCode, description, rejectFinanceApplicationCode))
            {
                amount = calculateCostAmount(cost, roundDigits, applyFinanceApplicationUnitCount);
            }

        }
        return amount;
    }

    public static BigDecimal getCostAmountByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode)
    {
        BigDecimal amount = BigDecimal.ZERO;
        for (final CostType cost : costList.getCost())
        {
            if(shouldCalCost(cost, currencyCode, financeCategoryCode, null, null, null))
            {
                amount = calculateAmountAsBigDecimal(cost.getMultiplierOrAmount().getCurrencyAmount().getAmount());
            }

        }
        return amount;
    }

    public static BigDecimal calculateAmountAsBigDecimal(AmountType amt)
    {
        if (null == amt)
        {
            return BigDecimal.ZERO;
        }

        BigInteger unscaledValue = new BigInteger(String.valueOf(amt.getDecimal()));
        BigDecimal value = new BigDecimal(unscaledValue, (int) amt.getDecimalPlaceCount(), MathContext.DECIMAL64);
        return value;
    }

    public static BigDecimal calculateMultiplierAsBigDecimal(MultiplierType multiplier)
    {
        BigDecimal bd = BigDecimal.ZERO;
        if (null != multiplier)
        {
            bd = BigDecimal.valueOf(multiplier.getDecimal(), (int) multiplier.getDecimalPlaceCount());
        }
        return bd;
    }

    public static BigDecimal getAmountMultipliedByFinUnitCount(CostType cost)
    {
        BigDecimal bd = BigDecimal.ZERO;

        if (null == cost.getMultiplierOrAmount())
        {
            return bd;
        }

        if (null != cost.getMultiplierOrAmount().getCurrencyAmount())
        {
            bd = calculateAmountAsBigDecimal(cost.getMultiplierOrAmount().getCurrencyAmount().getAmount());
        }
        else
        {
            bd = calculateMultiplierAsBigDecimal(cost.getMultiplierOrAmount().getMultiplier());
        }

        long finUnitCount = 1;
        if (null != cost && cost.getFinanceApplicationUnitCount() != null)
        {
            finUnitCount = cost.getFinanceApplicationUnitCount();
        }
        bd = (null != cost) ? bd.multiply(BigDecimal.valueOf(finUnitCount)) : bd;

        return bd;

    }

    public static BigDecimal getAmountMultipliedByFinUnitCount(PriceType price)
    {
        BigDecimal bd = BigDecimal.ZERO;

        if (null == price.getMultiplierOrAmount())
        {
            return bd;
        }

        if (null != price.getMultiplierOrAmount().getCurrencyAmount())
        {
            bd = calculateAmountAsBigDecimal(price.getMultiplierOrAmount().getCurrencyAmount().getAmount());
        }
        else
        {
            bd = calculateMultiplierAsBigDecimal(price.getMultiplierOrAmount().getMultiplier());
        }

        long finUnitCount = 1;
        if (null != price && price.getFinanceApplicationUnitCount() != null)
        {
            finUnitCount = price.getFinanceApplicationUnitCount();
        }
        bd = (null != price) ? bd.multiply(BigDecimal.valueOf(finUnitCount)) : bd;

        return bd;

    }

    public static BigDecimal toMoneyScale(BigDecimal value)
    {
        value = value.setScale(2, RoundingMode.HALF_EVEN);

        return toSafeBigDecimal(value);
    }
    // make the unscaled value not more than max integer since this is what we store in
    // our AmountType or MultiplierType. if you pass anything greater it will stored
    // as negative number.
    public static BigDecimal toSafeBigDecimal(BigDecimal bd)
    {
        do
        {
            BigInteger unscaledvalue = bd.unscaledValue();
            if (unscaledvalue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0)
            {
                bd = bd.setScale(bd.scale() - 1, RoundingMode.HALF_EVEN);
            }
            else
            {
                break;
            }
        }
        while (true);
        return bd;

    }


    //FinanceApplicationCode and DescriptionRawText can be used as optional parameter
    public static long getCostFinanceApplicationUnitCountByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode,
                                                            String financeApplicationCode, String description, String rejectFinanceApplicationCode
                                                            )
    {
        long count = 0;
        for (final CostType cost : costList.getCost())
        {
            if(shouldCalCost(cost, currencyCode, financeCategoryCode, financeApplicationCode, description, rejectFinanceApplicationCode))
            {
                count = cost.getFinanceApplicationUnitCount();
            }

        }
        return count;
    }


    public static boolean shouldCalCost(CostType cost, String currencyCode, String financeCategoryCode, String financeApplicationCode, String description, String rejectFinanceApplicationCode)
    {
        boolean shouldExecute = true;
        if(!isCostCurrencyMatched(cost, currencyCode)){
            shouldExecute = false;
        }

        if(!cost.getFinanceCategoryCode().equals(financeCategoryCode))
        {
            shouldExecute = false;
        }

        if(null != financeApplicationCode && !cost.getFinanceApplicationCode().toLowerCase(Locale.US).contains(financeApplicationCode.toLowerCase(Locale.US))){
            shouldExecute = false;
        }
        if(null != description && !cost.getDescriptionRawText().toLowerCase(Locale.US).contains(description.toLowerCase(Locale.US))){
            shouldExecute = false;
        }
        if(null != rejectFinanceApplicationCode && cost.getFinanceApplicationCode().toLowerCase(Locale.US).contains(rejectFinanceApplicationCode.toLowerCase(Locale.US))){
            shouldExecute = false;
        }
        return shouldExecute;
    }

    public static CostType getCostByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode, String description, String rejectDesc)
    {
        for (final CostType cost : costList.getCost())
        {
            if (isCostCurrencyMatched(cost, currencyCode)
                    && financeCategoryCode.toLowerCase(Locale.US).equals(cost.getFinanceCategoryCode().toLowerCase(Locale.US))
                    && isCostDescriptionMatched(cost, description, rejectDesc)
                    )
            {
                return cost;
            }

        }
        return null;
    }

    public static List<CostType> getCostListByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode, String description, String rejectDesc)
    {
        final List<CostType> mappedList = new ArrayList<CostType>();
        for (final CostType cost : costList.getCost())
        {
            if (isCostCurrencyMatched(cost, currencyCode)
                    && financeCategoryCode.toLowerCase(Locale.US).equals(cost.getFinanceCategoryCode().toLowerCase(Locale.US))
                    && isCostDescriptionMatched(cost, description, rejectDesc)
                    )
            {
                mappedList.add(cost);
            }

        }
        return mappedList;
    }

    public static boolean isCostDescriptionMatched(CostType cost, String description, String rejectDesc){
        boolean isDescMatched = true;
        if(description != null && !cost.getDescriptionRawText().toLowerCase(Locale.US).contains(description.toLowerCase(Locale.US)))
        {
            isDescMatched = false;
        }
        if((rejectDesc != null && cost.getDescriptionRawText().toLowerCase(Locale.US).contains(rejectDesc.toLowerCase(Locale.US))))
        {
            isDescMatched = false;
        }
        return isDescMatched;
    }

    public static boolean isCostCurrencyMatched(CostType cost, String currencyCode){
        boolean isCurrencyEqual = false;
        if(StringUtils.isNotEmpty(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()) && currencyCode.equals(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())){
            isCurrencyEqual = true;
        }
        else if(StringUtils.isEmpty(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())){
            isCurrencyEqual = true;
        }
        return isCurrencyEqual;
    }


    //This method can be used to get Tax and Fee list because Taxes have the same financeCategoryCode
    public static Dictionary<String, Double> getCostAmountListByFinanceCategoryCode(CostListType costList, String currencyCode, String financeCategoryCode, int roundDigits)
    {
        final Dictionary<String, Double> result = new Hashtable<String, Double>();
        for (final CostType cost : costList.getCost())
        {
            if (isCostCurrencyMatched(cost, currencyCode)
                    && financeCategoryCode.toLowerCase(Locale.US).equals(cost.getFinanceCategoryCode().toLowerCase(Locale.US))
                    && calculateCostAmount(cost, roundDigits, true) != 0) //ignore the 0 values
            {
                result.put(cost.getDescriptionRawText().trim(), calculateCostAmount(cost, roundDigits, true));
            }

        }
        return result;
    }

    public static String getCostPosuCurrencyCode(CostListType costList, String posCurrencyCode)
    {
        for (final CostType cost : costList.getCost())
        {

            if (StringUtils.isNotEmpty(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()) && !posCurrencyCode.equals(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
            {
                return cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
            }
        }
        return posCurrencyCode;
    }

    public static String getPricePosuCurrencyCode(PriceListType priceList, String posCurrencyCode)
    {
        for (final PriceType cost : priceList.getPrice())
        {

            if (StringUtils.isNotEmpty(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()) && !posCurrencyCode.equals(cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
            {
                return cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode();
            }
        }
        return posCurrencyCode;
    }

    public static double getPosTotalCost(CostListType costList, String posCurrencyCode)
    {
        return getCostAmountByFinanceCategoryCode(costList, posCurrencyCode, "Total", 4, null, null, null, true);
    }

    public static double getPosTotalCostForMarkup(CostListType costList, String posCurrencyCode)
    {
        double amount = 0;
        for (final CostType cost : costList.getCost())
        {
            if (cost.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode().equals(posCurrencyCode)
                    && cost.getFinanceCategoryCode().equals("Total"))
            {
                amount = cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimal() / Math.pow(10, cost.getMultiplierOrAmount().getCurrencyAmount().getAmount().getDecimalPlaceCount());
            }
        }
        return amount;
    }

    public static double getPosuTotalCost(CostListType costList, String posCurrencyCode)
    {
        final String posuCurrencyCode = getCostPosuCurrencyCode(costList, posCurrencyCode);
        //return posuCurrencyCode.equals(posCurrencyCode) ? 0 : (BigDecimal)getCostAmountByFinanceCategoryCode(costList, posuCurrencyCode, "Total", 4);
        return getCostAmountByFinanceCategoryCode(costList, posuCurrencyCode, "Total", 4, null, null, null, true);
    }

    public static double getCommissionFromCostList(CostListType costList, String posCurrencyCode)
    {
        final String posuCurrencyCode = getCostPosuCurrencyCode(costList, posCurrencyCode);
        //If currency conversation exist, there should be only the commission : Posu currency, and we should get that value
        //If no currency conversation exist, the commission should have the same currency with POS currency, and we should get that value.
        return getCostAmountByFinanceCategoryCode(costList, posuCurrencyCode, "Commission", 4, null, null, null, true);
    }

    public static String getCommissionCurrencyFromCostList(CostListType costList, String posCurrencyCode)
    {
        return getCostPosuCurrencyCode(costList, posCurrencyCode);
    }

    public static double calculatePriceAmount(PriceType price, int roundDigits, boolean applyFinanceApplicationUnitCount) {
        double amount = 0;
        boolean multiplierBoolean = true;
        final MultiplierOrAmountType multiplierOrAmount = price.getMultiplierOrAmount();
        if (multiplierOrAmount != null && multiplierOrAmount.getCurrencyAmount() != null) {
            if (StringUtils.isNotEmpty(multiplierOrAmount.getCurrencyAmount().getCurrencyCode())) {
                multiplierBoolean = false;
            }
            amount = multiplierBoolean ? calculateAmountFromCurrencyAmountOrMultiplier(null, multiplierOrAmount.getMultiplier(), 0) :
                    calculateAmountFromCurrencyAmountOrMultiplier(multiplierOrAmount.getCurrencyAmount(), null, 0);
        }

        if(applyFinanceApplicationUnitCount){
            amount = amount * price.getFinanceApplicationUnitCount();

        }

        if (roundDigits > 0) {
            final BigDecimal amountB = new BigDecimal(String.valueOf(amount));
            amount = amountB.setScale(roundDigits, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }

    //DescriptionRawText can be used as optional parameter
    public static double getPriceAmountByFinanceCategoryCode(PriceListType priceList, String currencyCode, String financeCategoryCode,
                                                             int roundDigits,
                                                             String description,
                                                             boolean applyFinanceApplicationUnitCount)
    {
        double amount = 0;
        for (final PriceType price : priceList.getPrice())
        {
            if (priceMatched(price, currencyCode, financeCategoryCode, description))
            {
                amount = calculatePriceAmount(price, roundDigits, applyFinanceApplicationUnitCount);
            }
        }
        return amount;
    }

    public static double getPriceValueByFinanceCategoryCode(PriceListType priceList, String currencyCode, String financeCategoryCode, String description)
    {
        double amount = 0;
        for (final PriceType price : priceList.getPrice())
        {
            if (priceMatched(price, currencyCode, financeCategoryCode, description))
            {
                amount = getPriceValue(price,4);
            }
        }
        return amount;
    }

    public static double getPriceValue(PriceType price, int roundDigits) {
        double amount = 0;
        boolean mulBoolean = true;
        final MultiplierOrAmountType multiplierOrAmount = price.getMultiplierOrAmount();
        if (multiplierOrAmount != null && multiplierOrAmount.getCurrencyAmount() != null) {
            if (StringUtils.isNotEmpty(multiplierOrAmount.getCurrencyAmount().getCurrencyCode())) {
                mulBoolean = false;
            }
            amount = mulBoolean ? calculateAmountFromCurrencyAmountOrMultiplier(null, multiplierOrAmount.getMultiplier(), 0) :
                    calculateAmountFromCurrencyAmountOrMultiplier(multiplierOrAmount.getCurrencyAmount(), null, 0);

        }
        if (roundDigits > 0) {
            final BigDecimal amountB = new BigDecimal(String.valueOf(amount));
            amount = amountB.setScale(roundDigits, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return amount;
    }

    public static PriceType getPriceByFinanceCategoryCode(PriceListType priceList, String currencyCode, String financeCategoryCode, String description)
    {
        for (final PriceType price : priceList.getPrice())
        {
            if (priceMatched(price, currencyCode, financeCategoryCode, description))
            {
                return price;
            }
        }
        return null;
    }

    public static List<PriceType> getPriceListByFinanceCategoryCode(PriceListType priceList, String currencyCode, String financeCategoryCode, String description)
    {
        final List<PriceType> result = new ArrayList<>();
        for (final PriceType price : priceList.getPrice())
        {
            if (priceMatched(price, currencyCode, financeCategoryCode, description))
            {
                result.add(price);
            }
        }
        return result;
    }

    public static boolean priceMatched(PriceType price, String currencyCode, String financeCategoryCode, String description)
    {
        boolean matched = false;
        if (((StringUtils.isNotEmpty(price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode())
                    && currencyCode.equals(price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
                    || StringUtils.isEmpty(price.getMultiplierOrAmount().getCurrencyAmount().getCurrencyCode()))
                    && financeCategoryCode.toLowerCase(Locale.US).equals(price.getFinanceCategoryCode().toLowerCase(Locale.US))
                    && (description == null || (description != null && price.getDescriptionRawText().toLowerCase(Locale.US).contains(description.toLowerCase(Locale.US))))
                    )
        {
            matched = true;
        }
        return matched;
    }

    public static double getPosTotalPrice(PriceListType priceList, String posCurrencyCode, boolean applyFinanceApplicationUnitCount)
    {
        return getPriceAmountByFinanceCategoryCode(priceList, posCurrencyCode, "Total", 4, null, applyFinanceApplicationUnitCount);
    }

    public static double getPosuTotalPrice(PriceListType priceList, String posCurrencyCode, boolean applyFinanceApplicationUnitCount)
    {
        final String posuCurrencyCode = getPricePosuCurrencyCode(priceList, posCurrencyCode);
        return getPriceAmountByFinanceCategoryCode(priceList, posuCurrencyCode, "Total", 4, null, applyFinanceApplicationUnitCount);
    }

}
