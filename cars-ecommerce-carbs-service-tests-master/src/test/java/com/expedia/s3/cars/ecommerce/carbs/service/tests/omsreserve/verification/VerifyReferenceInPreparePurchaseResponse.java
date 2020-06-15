package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification;

import com.expedia.e3.data.basetypes.defn.v4.ReferenceType;
import com.expedia.e3.data.cartypes.defn.v5.CarProductType;
import com.expedia.e3.data.cartypes.defn.v5.CarReservationType;
import com.expedia.s3.cars.ecommerce.carbs.service.verification.omreserve.input.PreparePurshaseVerificationInput;
import com.expedia.s3.cars.framework.core.dataaccess.DataAccessException;
import com.expedia.s3.cars.framework.test.common.dataaccess.dbhelper.CarsInventoryHelper;
import com.expedia.s3.cars.framework.test.common.execution.verification.BasicVerificationContext;
import com.expedia.s3.cars.framework.test.common.execution.verification.IVerification;
import com.expedia.s3.cars.framework.test.common.utils.CompareUtil;
import org.testng.Assert;

import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * Created by meichun on 7/14/2017.
 */
public class VerifyReferenceInPreparePurchaseResponse implements IVerification<PreparePurshaseVerificationInput, BasicVerificationContext> {

    @Override
    public boolean shouldVerify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {
        return true;
    }

    @Override
    public VerificationResult verify(PreparePurshaseVerificationInput input, BasicVerificationContext verificationContext) {

        return new VerificationResult(getName(), true, new ArrayList());
    }


    public void verify(PreparePurshaseVerificationInput input,  DataSource carsInventoryDatasource) throws DataAccessException {

        final CarReservationType responseCarReservation = input.getResponse().getPreparedItems().getBookedItemList().
                getBookedItem().get(0).getItemData().getCarOfferData().getCarReservation();
        final CarProductType preparePurchaseRspCar = responseCarReservation.getCarProduct();
        final long carItemID = preparePurchaseRspCar.getCarInventoryKey().getCarItemID();

        final CarsInventoryHelper inventoryHelper = new CarsInventoryHelper(carsInventoryDatasource);

        //Get expected accountingVendorID from DB
        final String expAccountingVendorID = CompareUtil.isObjEmpty(inventoryHelper.getCarItemById(carItemID)) ? "0" : inventoryHelper.getCarItemById(carItemID).getAccountingVendorID();

        //Get actual accountingVendorID AccountingVendorID
        String actAccountingVendorID = null;
        for(final ReferenceType reference : responseCarReservation.getReferenceList().getReference())
        {
            if("AccountingVendorID".equals(reference.getReferenceCategoryCode()))
            {
                actAccountingVendorID = reference.getReferenceCode();

            }
        }

        //Compare
       // CarNodeComparator.isStringNodeEqual("AccountingVendorID", expAccountingVendorID, actAccountingVendorID, remarks, new ArrayList<String>());
         if(!(actAccountingVendorID.equals(expAccountingVendorID) || (null== expAccountingVendorID
         && "0".equals(actAccountingVendorID))))
         {
             Assert.fail("The expectAccountVendorID " + expAccountingVendorID
                     + " is not equal to actualAccountVendorID " + actAccountingVendorID + " when booking");
         }

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
