package com.expedia.s3.cars.ecommerce.carbs.service.tests.omsreserve.verification.bookinglogging;

import com.expedia.e3.data.financetypes.defn.v4.CostType;
import com.expedia.e3.data.financetypes.defn.v4.PriceType;

/**
 * Created by v-mechen on 9/4/2018.
 */
@SuppressWarnings("PMD")
public class BookingAmountCostPriceKey  // NOPMD - generated code
{
    private long m_classId;
    private long m_calculationId;
    private long m_calculationSystemId;
    private String m_financeApplicationCode;

    public BookingAmountCostPriceKey(CostType cost)
    {
        m_classId = cost.getLegacyFinanceKey().getLegacyMonetaryClassID();
        m_calculationId = cost.getLegacyFinanceKey().getLegacyMonetaryCalculationID();
        m_calculationSystemId = cost.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID();
        m_financeApplicationCode = cost.getFinanceApplicationCode();
    }

    public BookingAmountCostPriceKey(PriceType price)
    {
        if (price.getLegacyFinanceKey() != null)
        {
            m_classId = price.getLegacyFinanceKey().getLegacyMonetaryClassID();
            m_calculationId = price.getLegacyFinanceKey().getLegacyMonetaryCalculationID();
            m_calculationSystemId = price.getLegacyFinanceKey().getLegacyMonetaryCalculationSystemID();
        }
        else
        {
            m_classId = 0;
            m_calculationId = 0;
            m_calculationSystemId = 0;
        }

        m_financeApplicationCode = price.getFinanceApplicationCode();
    }

    @Override
    public int hashCode() // NOPMD - generated code
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (m_calculationId ^ (m_calculationId >>> 32));
        result = prime * result + (int) (m_calculationSystemId ^ (m_calculationSystemId >>> 32));
        result = prime * result + (int) (m_classId ^ (m_classId >>> 32));
        result = prime * result + ((m_financeApplicationCode == null) ? 0 : m_financeApplicationCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) // NOPMD - generated code
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        BookingAmountCostPriceKey other = (BookingAmountCostPriceKey) obj;
        if (m_calculationId != other.m_calculationId)
        {
            return false;
        }
        if (m_calculationSystemId != other.m_calculationSystemId)
        {
            return false;
        }
        if (m_classId != other.m_classId)
        {
            return false;
        }
        if (m_financeApplicationCode == null)
        {
            if (other.m_financeApplicationCode != null)
            {
                return false;
            }
        }
        else if (!m_financeApplicationCode.equals(other.m_financeApplicationCode))
        {
            return false;
        }
        return true;
    }
}

