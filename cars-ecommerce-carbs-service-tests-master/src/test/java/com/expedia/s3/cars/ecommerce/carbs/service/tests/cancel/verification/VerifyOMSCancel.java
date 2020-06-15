package com.expedia.s3.cars.ecommerce.carbs.service.tests.cancel.verification;

import com.expedia.e3.data.financetypes.defn.v4.SimpleCurrencyAmountType;
import com.expedia.om.supply.messages.v1.StatusCodeCategoryType;
import com.expedia.s3.cars.ecommerce.carbs.service.commonutils.CarCommonEnumManager;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsBookingHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingAmount;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbooking.BookingItem;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.AmountRefDescType;
import expedia.om.supply.messages.defn.v1.BookingAmountType;
import expedia.om.supply.messages.defn.v1.CommitPrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.GetChangeProcessResponseType;
import expedia.om.supply.messages.defn.v1.PrepareChangeResponseType;
import expedia.om.supply.messages.defn.v1.RollbackPrepareChangeResponseType;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.StringUtils;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/24/2018.
 */
@SuppressWarnings("PMD")
public class VerifyOMSCancel {
    private VerifyOMSCancel(){}

    public static String verifyTotalAmountWithTaxForGetChangeProcess(GetChangeProcessResponseType getChangeProcessResponse, int businessModel)
    {
        final StringBuilder errorMsg = new StringBuilder();
        if (businessModel == 1 && Double.parseDouble(getChangeProcessResponse.getChangeOrderProcess().getTotalAmountWithTax().getSimpleAmount()) != 0)
        {
            errorMsg.append("TotalAmountWithTax is not equal 0 for Agency car in GetChangeProcessResponse.");
        }

        if (businessModel != 1 && Double.parseDouble(getChangeProcessResponse.getChangeOrderProcess().getTotalAmountWithTax().getSimpleAmount()) >= 0)
        {
            errorMsg.append("TotalAmountWithTax is  is not negative value for Agency car in GetChangeProcessResponse.");
        }

        return errorMsg.toString();
    }

    //BookingAmount verification for cancel  BookingVerificationUtils.getBookingItemID(response)
    public static String verifyBookingAmountCancelLog(String bookingItemID, boolean prepareChangeBoolean, boolean rollbackPrepareChangeBoolean) throws DataAccessException {
        final StringBuilder errorMsg = new StringBuilder();
        //Get logging rows for book and cancel
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        final List<BookingAmount> bookRows = carsBookingHelper.getBookingAmountList(bookingItemID, false);
        final List<BookingAmount> cancelRows = carsBookingHelper.getBookingAmountList(bookingItemID, true);

        final List<String> bookingAmountRowGUID = new ArrayList<>();
        for (final BookingAmount cancelRow : cancelRows)
        {
            //Verify CreateDate should be NULL for PrepareChange and not NULL for other cancel logging - CommitPrepareChange or legacy cancel
            if (!prepareChangeBoolean && StringUtils.isEmpty(cancelRow.getCreateDate()))
            {
                errorMsg.append("CreateDate in BookingAmount table should not be empty when cancel is done!\r\n");
            }
            if (prepareChangeBoolean && !StringUtils.isEmpty(cancelRow.getCreateDate()))
            {
                errorMsg.append("CreateDate in BookingAmount table should be NULL for PrepareChange!\r\n");
            }

            //Verify BookingAmountRowGUID for cancelling rows in BookingAmount table is different
            if (bookingAmountRowGUID.contains(cancelRow.getBookingAmountRowGUID())) {
                errorMsg.append("BookingAmountRowGUID in BookingAmount table should be unique for each cancel row!\r\n");
            }
            else {
                bookingAmountRowGUID.add(cancelRow.getBookingAmountRowGUID());
            };

            //Verify cancel row should have the same amount as reserve row
            boolean matchReserveRow = false;
            for (final BookingAmount bookRow : bookRows)
            {
                //Verify BookingAmountRowGUID is unique
                if(cancelRow.getBookingAmountRowGUID().equals(bookRow.getBookingAmountRowGUID()))
                {
                    errorMsg.append("BookingAmountRowGUID in BookingAmount table should be unique for each cancel row!\r\n");
                }

                //Get matched row
                if (Integer.parseInt(bookRow.getBookingAmountSeqNbr()) == (-1) * Integer.parseInt(cancelRow.getBookingAmountSeqNbr())
                        && bookRow.getTransactionAmtPrice().compareTo(cancelRow.getTransactionAmtPrice().negate()) == 0
                        && bookRow.getTransactionAmtCost().compareTo(cancelRow.getTransactionAmtCost().negate()) == 0
                        && bookRow.getCurrencyCodeCost().equals(cancelRow.getCurrencyCodeCost())
                        && bookRow.getCurrencyCodePrice().equals(cancelRow.getCurrencyCodePrice())
                        ) {
                    matchReserveRow = true;
                    break;
                }

            }
            if (!matchReserveRow){
                errorMsg.append("The cancel row can't be mapped to any reserve row in BookingAmount Table(BookingAmountSeqNbr:" + cancelRow.getBookingAmountRowGUID());
            }

        }

        //Verify cancel rows count is same as reserve rows for PrepareChange/CommitPrepareChange/Legacy cancel; verify no cacel rows for RollbackPrepareChange
        if (!rollbackPrepareChangeBoolean && bookRows.size() != cancelRows.size())
        {
            errorMsg.append("Cancel rows count should be same as reserve rows in BookingAmount table!\r\n");
        }
        if (rollbackPrepareChangeBoolean && !cancelRows.isEmpty())
        {
            errorMsg.append("Cancel rows should be deleted in BookingAmount table for RollbackPrepareChange!\r\n");
        }
        return errorMsg.toString();
    }

    //BookingItem verification for Cancel
    public static String verifyBookingItemCancelLog(String bookingItemID, boolean prepareChangeBoolean, boolean rollbackPrepareChangeBoolean) throws DataAccessException {
        StringBuilder errorMsg = new StringBuilder();
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        //Get actual rows
        final BookingItem bookingItem = carsBookingHelper.getBookingItem(bookingItemID);

        //Verify BookingItemStateIDPending: PrepareChange set it to CancelPending (102), CommitChangeProcess/RollbackPrepareChange set it to None (0)
        if(prepareChangeBoolean && bookingItem.getBookingItemStateIDPending() != 102)
            errorMsg.append("BookingItemStateIDPending should be 102(CancelPending) in BookingItem table for PrepareChange!\r\n");
        if (!prepareChangeBoolean && bookingItem.getBookingItemStateIDPending() != 0)
            errorMsg.append("BookingItemStateIDPending should be 0 in BookingItem table for non PrepareChange cancel!\r\n");

        //Verify BookingItemStateID: CommitChangeProcess/Legacy cancel set it to Cancel (2), othwise it should be 1
        if ((prepareChangeBoolean || rollbackPrepareChangeBoolean) && bookingItem.getBookingItemStateID() != 1)
            errorMsg.append("BookingItemStateID should be 1 in BookingItem table for PrepareChange/RollbackPrepareChange!\r\n");
        if (!(prepareChangeBoolean || rollbackPrepareChangeBoolean) && bookingItem.getBookingItemStateID() != 2)
            errorMsg.append("BookingItemStateID should be 2 in BookingItem table for CommitPrepareChange/LegacyCancel!\r\n");

        //Verify CancelDate and CancelTUID: not NULL for CommitChangeProcess/Legacy cancel, NULL for PrepareChange/RollbackPrepareChange
        if (!rollbackPrepareChangeBoolean && !prepareChangeBoolean)
        {
            if (StringUtils.isEmpty(bookingItem.getCancelDate()))
            {
                errorMsg.append("CancelDate should not be empty when cancel is done in BookingItem table!\r\n");
            }
            if (StringUtils.isEmpty(bookingItem.getCancelTUID()))
            {
                errorMsg.append("CancelTUID should not be empty when cancel is done in BookingItem table!\r\n");
            }
        }
        else
        {
            if (!StringUtils.isEmpty(bookingItem.getCancelDate()))
            {
                errorMsg.append("CancelDate should be NULL for RollbackPrepareChange in BookingItem table\n");
            }
            if (!StringUtils.isEmpty(bookingItem.getCancelTUID()))
            {
                errorMsg.append("CancelTUID should be NULL for RollbackPrepareChange in BookingItem table!\r\n");
            }
        }


        return errorMsg.toString();
    }

    public static String verifyBookingAmounInPrepareChangeRsp(PrepareChangeResponseType prepareChangeRsp, String bookingItemID) throws DataAccessException {
        final StringBuilder errorMsg = new StringBuilder();

        //Get bookingAmount rows from DB
        final CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        final List<BookingAmount> dbRows = carsBookingHelper.getBookingAmountList(bookingItemID);


        //Convert DB amount list to oms BookingAmountType list
        final List<BookingAmountType> expBookingAmountList = convertDBBookingAmountListToOMSType(dbRows);

        //Get actual BookingAmountType list
        final List<BookingAmountType> actBookingAmountList = prepareChangeRsp.getPreparedItems().getBookedItemList().getBookedItem()
                .get(0).getOrderBookedItemInformation().getBookingAmountList().getBookingAmount();

        //Compare expect and actual BookingAmount list
        final boolean compared = CompareUtil.compareObject(expBookingAmountList, actBookingAmountList, new ArrayList<>(), errorMsg);
        if (!compared)
        {
            return errorMsg.toString();
        }

        return "";
    }

    public static String verifyGDSCancelRequestSent(Document spooferDoc, boolean prepareChangeBoolean)
    {
        final StringBuilder errorMsg = new StringBuilder();

        //PrepareChange should not send GDS cancel request
        if(prepareChangeBoolean && isGDSCancelSent(spooferDoc))
        {
            errorMsg.append("GDS cancel request should not be sent for PrepareChange!\r\n");
        }

        //CommitChange should send GDS cancel request
        if(!prepareChangeBoolean && !isGDSCancelSent(spooferDoc))
        {
            errorMsg.append("GDS cancel request should be sent for CommitChange!\r\n");
        }

        return errorMsg.toString();
    }

    public static boolean isGDSCancelSent(Document spooferDoc)
    {
        boolean exist = false;
        if(isSpecificNodeExist(spooferDoc, "PNR_Cancel") || isSpecificNodeExist(spooferDoc, "VehicleCancelReq") ||
                isSpecificNodeExist(spooferDoc, "VehCancelResRQ") || isSpecificNodeExist(spooferDoc, "OTA_VehCancelRQ"))
        {
            exist = true;
        }
        return exist;
    }

    public static boolean isSpecificNodeExist(Document spooferDoc, String nodeName)
    {
        boolean exist = true;
        final NodeList nodeList = spooferDoc.getElementsByTagNameNS("*", nodeName);
        if(nodeList == null || nodeList.getLength() == 0)
        {
            exist = false;
        }
        return exist;
    }

    //Convert DB BookingAmount list to oms BookingAmount list for oms response compare
    public static List<BookingAmountType> convertDBBookingAmountListToOMSType(List<BookingAmount> dbRows)
    {
        final List<BookingAmountType> omsBookingAmountList = new ArrayList<>();
        for(final BookingAmount dbAmount : dbRows)
        {
            omsBookingAmountList.add(convertDBBookingAmountToOMSType(dbAmount));
        }

        return omsBookingAmountList;
    }

    //Convert DB BookingAmount to oms BookingAmount for oms response compare
    public static BookingAmountType convertDBBookingAmountToOMSType(BookingAmount dbAmount){

        final BookingAmountType omsAmount = new BookingAmountType();
        omsAmount.setBookingSequenceNumber(Integer.parseInt(dbAmount.getBookingAmountSeqNbr()));
        omsAmount.setBookingAmountID(dbAmount.getBookingAmountRowGUID());
        omsAmount.setIsCancel(dbAmount.getCancelBool().equals("1") ? true : false);
        omsAmount.setAmountLevelID(new BigInteger(dbAmount.getBookingAmountLevelID()));
        omsAmount.setMonetaryClassID(new BigInteger(dbAmount.getMonetaryClassID()));
        omsAmount.setMonetaryCalculationSystemID(new BigInteger(dbAmount.getMonetaryCalculationSystemID()));
        omsAmount.setMonetaryCalculationID(new BigInteger(dbAmount.getMonetaryCalculationID()));

        omsAmount.setCost(new AmountRefDescType());
        omsAmount.getCost().setAmount(new SimpleCurrencyAmountType());
        omsAmount.getCost().getAmount().setSimpleAmount(dbAmount.getTransactionAmtCost().toPlainString());
        omsAmount.getCost().getAmount().setCurrencyCode(dbAmount.getCurrencyCodeCost());
        omsAmount.getCost().setAmountDescription(dbAmount.getBookingAmountDescCost());
        omsAmount.getCost().setAmountReferenceCode(StringUtils.isEmpty(dbAmount.getBookingAmountRefCodeCost()) ? "" : dbAmount.getBookingAmountRefCodeCost());

        omsAmount.setPrice(new AmountRefDescType());
        omsAmount.getPrice().setAmount(new SimpleCurrencyAmountType());
        omsAmount.getPrice().getAmount().setSimpleAmount(dbAmount.getTransactionAmtPrice().toPlainString());
        omsAmount.getPrice().getAmount().setCurrencyCode(dbAmount.getCurrencyCodePrice());
        omsAmount.getPrice().setAmountDescription(dbAmount.getBookingAmountDescPrice());
        omsAmount.getPrice().setAmountReferenceCode(StringUtils.isEmpty(dbAmount.getBookingAmountRefCodePrice()) ? "" : dbAmount.getBookingAmountRefCodePrice());

        return omsAmount;
    }

    public static String isExpectedErrorExistsInPrepareChangeMessageVerifier(PrepareChangeResponseType prepareChangeResponse, StatusCodeCategoryType expectedStatusCodeCategory)
    {
        String errorMsg = "";
        if (null == prepareChangeResponse)
        {
            errorMsg = "Verify prepareChangeResponse message failed. prepareChangeResponse is null.";
        }
        else if (!prepareChangeResponse.getResponseStatus().getStatusCodeCategory().value().equals(expectedStatusCodeCategory.value()))
        {
            errorMsg = "Expected error type " + expectedStatusCodeCategory + " is not  returned in prepareChange response, StatusCodeCategory " + prepareChangeResponse.getResponseStatus().getStatusCodeCategory() + "returned!";
        }
        return errorMsg;
    }

    public static String isExpectedErrorExistsInCommitPrepareChangeMessageVerifier(CommitPrepareChangeResponseType commitPrepareChangeResponse, StatusCodeCategoryType expectedStatusCodeCategory)
    {
        String errorMsg = "";
        if (null == commitPrepareChangeResponse)
        {
            errorMsg = "Verify commitPrepareChangeResponse message failed. commitPrepareChangeResponse is null.";
        }
        else if (!commitPrepareChangeResponse.getResponseStatus().getStatusCodeCategory().value().equals(expectedStatusCodeCategory.value()))
        {
            errorMsg = "Expected error type " + expectedStatusCodeCategory + " don't be returned in commitPrepareChangeResponse response, StatusCodeCategory " + commitPrepareChangeResponse.getResponseStatus().getStatusCodeCategory() + " returned!";
        }
        return errorMsg;
    }

    public static String isExpectedErrorExistsInRollbackPrepareChangeMessageVerifier(RollbackPrepareChangeResponseType rollbackPrepareChangeResponse, StatusCodeCategoryType expectedStatusCodeCategory)
    {
        String errorMsg = "";
        if (null == rollbackPrepareChangeResponse)
        {
            errorMsg = "Verify rollbackPrepareChangeResponse message failed. rollbackPrepareChangeResponse is null.";
        }
        else if (!rollbackPrepareChangeResponse.getResponseStatus().getStatusCodeCategory().value().equals(expectedStatusCodeCategory.value()))
        {
            errorMsg = "Expected error type " + expectedStatusCodeCategory + " don't be returned in rollbackPrepareChangeResponse response, StatusCodeCategory " + rollbackPrepareChangeResponse.getResponseStatus().getStatusCodeCategory() + "returned!";
        }
        return errorMsg;
    }

    public static void verifyBookingAmountTUID(String bookingItemID, long userIDInRequst) throws DataAccessException
    {
        CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        List<BookingAmount> bookingAmountList = carsBookingHelper.getBookingAmountList(bookingItemID);
        long createTUIDInDB = 0;
        if (CollectionUtils.isNotEmpty(bookingAmountList))
        {
            createTUIDInDB = Long.parseLong(bookingAmountList.get(0).getCreateTUID());
        }
        else
        {
            Assert.fail("No bookingAmount DB data find by  bookingItemID " + bookingItemID);
        }
        if (createTUIDInDB != userIDInRequst)
        {
            Assert.fail(String.format("CreateIUID is not same between BookingAmount and preparePurchaseRequest, value in BookingAmount: %d, value in preparePurchaseRequest: %d, bookingItemID: %d\r\n", createTUIDInDB, userIDInRequst, bookingItemID));

        }
    }

    public static void verifyBookingItemTUID(String bookingItemID, CarCommonEnumManager.OMCancelMessageType cancelMessage) throws DataAccessException
    {
        CarsBookingHelper carsBookingHelper = new CarsBookingHelper(DatasourceHelper.getCarsBookingDatasource());
        BookingItem bookingItem = carsBookingHelper.getBookingItem(bookingItemID);
        String cancelTUIDInDB = "";

        if (null != bookingItem)
        {
             cancelTUIDInDB = bookingItem.getCancelTUID();
        }
        else
        {
        Assert.fail("No bookingItem DB data find by bookingItemID " + bookingItemID);
        }


        //rollbackPrepareChange no CancelTUID is updated
        if (cancelMessage.equals(CarCommonEnumManager.OMCancelMessageType.RollbackPrepareChange))
        {
            if (StringUtil.isNotBlank(cancelTUIDInDB))
            {
                Assert.fail(String.format("rollbackPrepareChange error,the CancelTUID in BookingItem table be updated,bookingItemID: %d\r\n", bookingItemID));

            }
        }
    }
}
