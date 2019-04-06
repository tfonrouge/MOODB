package test01.data.customer;

import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;
import test01.data.entity.Entity;

public class Customer extends Entity {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_customerId = new MFieldString(this, "customerId") {
        @Override
        protected void initialize() {
            description = "Customer Id";
        }
    };

    public final MIndex index_customerId = new MIndex(this, "customerId", "", "customerId", true, false) {
        @Override
        protected void initialize() {
        }
    };

    @Override
    public final String getTableName() {
        return "customer";
    }

    @Override
    public CustomerData getData() {
        return new CustomerData<>(this);
    }
    /* @@ end field descriptor @@ */
}
