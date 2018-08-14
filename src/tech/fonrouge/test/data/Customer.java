package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.*;

public class Customer extends Entity {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_customerId = new MFieldString(this, "customerId") {
        @Override
        protected void initialize() {
            mDescription = "Customer Id";
        }
    };

    public final MIndex index_customerId = new MIndex(this, "customerId", "", "customerId", false, true);

    private CustomerModel m;

    @Override
    public final String getTableName() {
        return "customer";
    }

    @Override
    protected void initializeModel() {
        m = new CustomerModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */
}
