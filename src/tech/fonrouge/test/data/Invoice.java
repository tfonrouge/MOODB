package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.*;

import java.util.Date;

public class Invoice extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldInteger field_docNumber = new MFieldInteger(this, "docNumber") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Invoice number";
        }
    };
    public final MFieldTableField<Customer> field_customer = new MFieldTableField<Customer>(this, "customer") {
        @Override
        protected void initialize() {
            mRequired = true;
        }

        @Override
        protected Customer buildTable() {
            return new Customer();
        }
    };
    public final MFieldDate field_date = new MFieldDate(this, "date") {
        @Override
        protected void initialize() {
            mRequired = true;
        }

        @Override
        protected Date getNewValue() {
            return new Date();
        }
    };

    public final MIndex index_docNumber = new MIndex(this, "docNumber", "", "docNumber", false, true);

    private InvoiceModel m;

    @Override
    public final String getTableName() {
        return "invoice";
    }

    @Override
    protected void initializeModel() {
        m = new InvoiceModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */
}
