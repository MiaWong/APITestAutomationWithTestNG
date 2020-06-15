package com.expedia.s3.cars.framework.test.common.dataaccess.dbimpl.carsinventory;

import java.math.BigDecimal;

public class CarCommission
{
    private BigDecimal commissionPct;
    private int carCommissionLogID;

    public BigDecimal getCommissionPct()
    {
        return commissionPct;
    }

    public void setCommissionPct(BigDecimal commissionPct)
    {
        this.commissionPct = commissionPct;
    }

    public int getCarCommissionLogID() {
        return carCommissionLogID;
    }

    public void setCarCommissionLogID(int carCommissionLogID) {
        this.carCommissionLogID = carCommissionLogID;
    }


}
