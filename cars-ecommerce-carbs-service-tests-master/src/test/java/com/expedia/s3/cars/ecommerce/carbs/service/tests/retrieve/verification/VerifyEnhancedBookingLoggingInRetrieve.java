package com.expedia.s3.cars.ecommerce.carbs.service.tests.retrieve.verification;

import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.om.supply.messages.v1.ReservationStatusType;
import com.expedia.s3.cars.ecommerce.carbs.service.database.util.DatasourceHelper;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging.VerifyCarReservationData;
import com.expedia.s3.cars.ecommerce.carbs.service.tests.verification.pricelist.TotalPriceVerifier;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omretrieve.RetrieveVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarBSHelper;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carbs.CarReservationData;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import expedia.om.supply.messages.defn.v1.BookedItemType;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by v-mechen on 9/19/2018.
 */
public class VerifyEnhancedBookingLoggingInRetrieve {
    private VerifyEnhancedBookingLoggingInRetrieve(){

    }
    public static StringBuilder verifyEnhancedBookingLoggingInRetrieve(RetrieveVerificationInput retrieveVerificationInput, String posCurrency,
                                                              boolean cancelled) throws DataAccessException, IOException {
        //Get CarReservation from DB
        final CarBSHelper carBSHelper = new CarBSHelper(DatasourceHelper.getCarBSDatasource());
        final String bookingItemId = carBSHelper.getBookingItemIDByBookingRecordLocatorByID(retrieveVerificationInput.getRequest().getRecordLocator());
        final CarReservationData expCarReservationData = carBSHelper.getCarReservationDataByBookingItemID(bookingItemId);
        final CarReservationType carReservationInDB = VerifyCarReservationData.deserializeReservation(expCarReservationData.getCarReservationNodeData());
        //Token/PostPuchase/ProductCategoryCodeList/CostList is not retrieved
        carReservationInDB.getCarProduct().setCarProductToken(null);
        carReservationInDB.getCarProduct().getCarInventoryKey().setPostPurchaseBoolean(null);
        carReservationInDB.getCarProduct().getCarInventoryKey().setProductCategoryCodeList(null);
        carReservationInDB.getCarProduct().setCostList(null);
        //If the car is cancelled, update book status code to Cancelled
        if(cancelled) {
            carReservationInDB.setBookingStateCode(ReservationStatusType.CANCELLED.value());
        }
        //Verify CarReservation
        final StringBuilder errorMsg = new StringBuilder();
        final BookedItemType bookedItem = retrieveVerificationInput.getResponse().getBookedItemList().getBookedItem().get(0);
        final CarReservationType carReservationInRetrieve = bookedItem.getItemData().getCarOfferData().getCarReservation();
        CompareUtil.compareObject(carReservationInDB, carReservationInRetrieve, new ArrayList<>(), errorMsg);

        //Verify total price
        final List<String> errorList = new ArrayList<>();
        TotalPriceVerifier.verifyTotalPriceEqual(retrieveVerificationInput.getCarProductType(),carReservationInRetrieve.getCarProduct(), posCurrency, errorList, false);
        if(!CollectionUtils.isEmpty(errorList))
        {
            errorMsg.append(errorList.toString());
        }

        //Verify status
        if(cancelled && !ReservationStatusType.CANCELLED.value().equals(bookedItem.getOrderBookedItemInformation().getReservationStatus().value()))
        {
            errorMsg.append("ReservationStatus in retrieve response should be Cancelled!\n");
        }
        if(!cancelled && !ReservationStatusType.BOOKED.value().equals(bookedItem.getOrderBookedItemInformation().getReservationStatus().value()))
        {
            errorMsg.append("ReservationStatus in retrieve response should be Booked!\n");
        }

        return errorMsg;
    }





}
