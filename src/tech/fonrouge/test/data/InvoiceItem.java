package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MFieldDouble;
import tech.fonrouge.MOODB.MFieldTableField;
import tech.fonrouge.MOODB.MIndex;

public class InvoiceItem extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldTableField<Invoice> field_invoice = new MFieldTableField<Invoice>(this, "invoice") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Invoice";
        }

        @Override
        protected Invoice buildTable() {
            return new Invoice();
        }
    };
    public final MFieldTableField<Inventory> field_invItem = new MFieldTableField<Inventory>(this, "invItem") {
        @Override
        protected void initialize() {
            mRequired = true;
        }

        @Override
        protected Inventory buildTable() {
            return new Inventory();
        }
    };
    public final MFieldDouble field_qty = new MFieldDouble(this, "qty") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Quantity";
        }
    };
    public final MFieldDouble field_unitPrice = new MFieldDouble(this, "unitPrice") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Unit Price";
        }
    };
    public final MFieldDouble field_total = new MFieldDouble(this, "total") {
        @Override
        protected void initialize() {
            mCalculated = true;
            calcValue = () -> calcField_total();
        }
    };

    public final MIndex index_date = new MIndex(this, "date", "", "date", false, false);

    private InvoiceItemModel m;

    @Override
    public final String getTableName() {
        return "invoiceItem";
    }

    @Override
    protected void initializeModel() {
        m = new InvoiceItemModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_total @@ */
    public Double calcField_total() {
        return field_qty.value() * field_unitPrice.value();
    }
    /* @@ end calcField_total @@ */
}
