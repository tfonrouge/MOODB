package test01.data.customer;

import tech.fonrouge.ui.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;
import test01.data.entity.Entity;
import tech.fonrouge.MOODB.*;

@AutoGenerated()
public class Customer extends Entity {

    /**
     * field_customerId
     */
    @AutoGenerated()
    public final MFieldString field_customerId = new MFieldString(this, "customerId") {
        @Override
        protected void initialize() {
            description = "Customer Id";
        }
    };

    /**
     * index_customerId
     */
    @AutoGenerated()
    public final MIndex index_customerId = new MIndex(this, "customerId", "", "customerId", true, false, null, null) {
        @Override
        protected void initialize() {
        }
    };

    /**
     * getTableName
     */
    @Override
    @AutoGenerated()
    public final String getTableName() {
        return "customer";
    }

    /**
     * getData
     */
    @Override
    @AutoGenerated()
    public CustomerData getData() {
        return new CustomerData<>(this);
    }
}
