package test01.data;

import tech.fonrouge.MOODB.*;

public class Invoice extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldInteger field_docNumber = new MFieldInteger(this, "docNumber") {
        @Override
        protected void initialize() {
            required = true;
            description = "Invoice number";
        }
    };
    public final MFieldTableField<Customer> field_customer = new MFieldTableField<Customer>(this, "customer") {
        @Override
        protected void initialize() {
            required = true;
        }

        @Override
        protected Customer buildTableField() {
            return new Customer();
        }
    };
    public final MFieldDate field_date = new MFieldDate(this, "date") {
        @Override
        protected void initialize() {
            required = true;
            mNewDate = true;
        }
    };


    public Invoice() {
    }

    public Invoice(MTable masterSource) {
        super(masterSource);
    }

    @Override
    public final String getTableName() {
        return "invoice";
    }
    /* @@ end field descriptor @@ */

    @Override
    public MBaseData getData() {
        return null;
    }

}
