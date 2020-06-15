package com.expedia.s3.cars.framework.test.common.testdatagenerator.gdsmessagereader.amadeusmessage;

import com.expedia.s3.cars.framework.test.common.constant.CommonEnumManager;
import org.apache.commons.lang.StringUtils;

/**
 * Created by miawang on 12/29/2016.
 */
@SuppressWarnings("PMD")
public class ASCSGDSReaderUtil {
    private ASCSGDSReaderUtil() {
    }

    //now is use for amadeus, not sure other scs need it or not.
    public static int calculatePeriodUnit(String periodType, int daysCount, double baseRate, double totalRate) {
        if (null != periodType && StringUtils.isEmpty(periodType)) {
            if (periodType.equals(CommonEnumManager.FinanceApplicationCode.Weekly.getFinanceApplicationCode())) {
                if ((daysCount / 5) * baseRate <= totalRate) {
                    return daysCount / 5;
                } else if ((daysCount / 6) * baseRate <= totalRate) {
                    return daysCount / 6;
                } else {
                    return daysCount / 7;
                }
            } else if (periodType.equals(CommonEnumManager.FinanceApplicationCode.Monthly.getFinanceApplicationCode())) {
                if ((daysCount / 28) * baseRate < totalRate) {
                    return daysCount / 28;
                } else {
                    return daysCount / 29;
                }
            }
        }
        return 1;
    }

    public static String getChargeDetailsPeriodType(int ratecode) {
        switch (ratecode) {
            case 1:
                return "Daily";
            case 2:
                return "Weekly";
            case 3:
                return "Monthly";
            case 4:
                return "Trip";
            case 12:
                return "Base";
            case 13:
                return "Trip";
            default:
                return "Trip";
        }
    }

    public static String getFinanceApplicationCodeByRatePlanIndicator(String ratePlanIndicator) {
        switch (ratePlanIndicator) {
            case "DY":
                return CommonEnumManager.FinanceApplicationCode.Daily.getFinanceApplicationCode();
            case "MY":
                return CommonEnumManager.FinanceApplicationCode.Monthly.getFinanceApplicationCode();
            case "WD":
                return CommonEnumManager.FinanceApplicationCode.Weekend.getFinanceApplicationCode();
            case "WY":
                return CommonEnumManager.FinanceApplicationCode.Weekly.getFinanceApplicationCode();
            default:
                return CommonEnumManager.FinanceApplicationCode.Trip.getFinanceApplicationCode();
        }
    }

    /*
  Misc charge <xsl:variable name="financeCategoryCode">
   <xsl:choose>
   <xsl:when test="$chargeType = '108'">Surcharge</xsl:when>
   <xsl:when test="$chargeType = '045'">Taxes</xsl:when>
   <xsl:when test="$chargeType = 'COV'">Fee</xsl:when>
   <xsl:when test="$chargeType = '013'">Fee</xsl:when>
   <xsl:otherwise>Taxes</xsl:otherwise>
   </xsl:choose>
   </xsl:variable>*/
    public static String getFinanceCategoryCodeByChargeDetailsType(String chargeDetailsType)
    {

        /* get from xslt
        xsl:when test="$chargeType = '108'">Surcharge</xsl:when>
        <xsl:when test="$chargeType = '045'">Taxes</xsl:when>
        <xsl:when test="$chargeType = '013'">SpecialEquipment</xsl:when>
        <xsl:when test="$chargeType = 'COV' or $chargeType = 'E' or $chargeType = 'LIA'">Coverage</xsl:when>
        <xsl:when test="$chargeType = '008' or $chargeType='009'">Extra</xsl:when>
         */
        switch (chargeDetailsType)
        {
            case "RB":
                return CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode();
            case "904":
                return CommonEnumManager.FinanceCategoryCode.Total.getFinanceCategoryCode();
            case "RP":
                return CommonEnumManager.FinanceCategoryCode.Base.getFinanceCategoryCode();
            case "45":
            case "045":
                return CommonEnumManager.FinanceCategoryCode.Taxes.getFinanceCategoryCode();
            case "108":
                return CommonEnumManager.FinanceCategoryCode.Surcharge.getFinanceCategoryCode();
            case "113":
                return CommonEnumManager.FinanceCategoryCode.Prepayment.getFinanceCategoryCode();
            case "13":
            case "013":
                //ARIS response SpecialEquipment
                return CommonEnumManager.FinanceCategoryCode.SpecialEquipment.getFinanceCategoryCode();
                //COV is mapping Fee
            //case "COV":
            case "E":
            case "LIA":
                return CommonEnumManager.FinanceCategoryCode.Coverage.getFinanceCategoryCode();
            case "008":
            case "009":
                return CommonEnumManager.FinanceCategoryCode.Extra.getFinanceCategoryCode();
            default:
                return CommonEnumManager.FinanceCategoryCode.Fee.getFinanceCategoryCode();
        }
    }

    public static String getConditionalCostPriceFinanceCategoryCode(String chargeDetailsType)
    {

        /*
    ConditionalCostPrice <xsl:variable name="financeCategoryCode">
    <xsl:choose>
    <xsl:when test="$chargeType = '108'">Surcharge</xsl:when>
    <xsl:when test="$chargeType = '045'">Taxes</xsl:when>
    <xsl:when test="$chargeType = '013'">SpecialEquipment</xsl:when>
    <xsl:when test="$chargeType = 'COV' or $chargeType = 'E' or $chargeType = 'LIA'">Coverage</xsl:when>
    <xsl:when test="$chargeType = '008' or $chargeType='009'">Extra</xsl:when>
    <xsl:otherwise>MiscBase</xsl:otherwise>
    </xsl:choose>
    </xsl:variable>*/
        switch (chargeDetailsType)
        {
            case "045":
                return CommonEnumManager.FinanceCategoryCode.Taxes.getFinanceCategoryCode();
            case "108":
                return CommonEnumManager.FinanceCategoryCode.Surcharge.getFinanceCategoryCode();
            case "013":
                //ARIS response SpecialEquipment
                return CommonEnumManager.FinanceCategoryCode.SpecialEquipment.getFinanceCategoryCode();
            case "COV":
            case "E":
            case "LIA":
                return CommonEnumManager.FinanceCategoryCode.Coverage.getFinanceCategoryCode();
            case "008":
            case "009":
                return CommonEnumManager.FinanceCategoryCode.Extra.getFinanceCategoryCode();
            default:
                return CommonEnumManager.FinanceCategoryCode.MiscBase.getFinanceCategoryCode();
        }
    }






    public static int getNumberOfDaysInPeriod(String ratecode) {
        switch (ratecode) {
            case "DY":
                return 1;
            case "MY":
                //how to set 28-31 days  
                return 30;
            case "WD":
                return 2;
            case "WY":
                return 7;
            default:
                return 0;
        }
    }

    public static String getRateCategoryCode(String rateCode) {
        String rateCatagoryCode = null;
        switch (rateCode) {
            case "002":
                rateCatagoryCode = "Inclusive";
                break;
            case "006":
                rateCatagoryCode = "Convention";
                break;
            case "007":
                rateCatagoryCode = "Corporate";
                break;
            case "009":
                rateCatagoryCode = "Government";
                break;
            case "011":
                rateCatagoryCode = "Package";
                break;
            case "019":
                rateCatagoryCode = "Association";
                break;
            case "24":
            case "024":
                rateCatagoryCode = "Standard";
                break;
            default:
                break;
        }
        return rateCatagoryCode;
    }

    public static String getBookingStateCode(String statusNodeValue) {
        String bookingStateCode = null;

        switch (statusNodeValue) {
            case "HK":
                bookingStateCode = CommonEnumManager.BookStatusCode.BOOKED.getStatusCode();
                break;
            case "HN":
                bookingStateCode = CommonEnumManager.BookStatusCode.RESERVED.getStatusCode();
                break;
            case "SS":
                bookingStateCode = CommonEnumManager.BookStatusCode.RESERVED.getStatusCode();
                break;
            case "UC":
                bookingStateCode = CommonEnumManager.BookStatusCode.UNKNOWN.getStatusCode();
                break;
            default:
                break;
        }

        return bookingStateCode;
    }

    public class PickupOrDropoffType
    {
        public static final String PICKUP_TYPE = "176";
        public static final String DROPOFF_TYPE = "DOL";
    }
}
