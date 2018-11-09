package test01.data;

import tech.fonrouge.MOODB.*;

public class Customer extends Entity {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_customerId = new MFieldString(this, "customerId") {
        @Override
        protected void initialize() {
            description = "Customer Id";
        }
    };



    @Override
    public final String getTableName() {
        return "customer";
    }
    /* @@ end field descriptor @@ */

    @Override
    public MBaseData getData() {
        return null;
    }

}
