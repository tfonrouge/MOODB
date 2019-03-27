package test01.data;

import tech.fonrouge.MOODB.*;

public class InvoiceItem extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldTableField<Invoice> field_invoice = new MFieldTableField<Invoice>(this, "invoice") {
        @Override
        protected void initialize() {
            required = true;
            description = "Invoice";
        }

        @Override
        protected Invoice buildTableField() {
            return new Invoice();
        }
    };
    public final MFieldTableField<Inventory> field_invItem = new MFieldTableField<Inventory>(this, "invItem") {
        @Override
        protected void initialize() {
            required = true;
        }

        @Override
        protected Inventory buildTableField() {
            return new Inventory();
        }
    };
    public final MFieldDouble field_qty = new MFieldDouble(this, "qty") {
        @Override
        protected void initialize() {
            required = true;
            description = "Quantity";
        }
    };
    public final MFieldDouble field_unitPrice = new MFieldDouble(this, "unitPrice") {
        @Override
        protected void initialize() {
            required = true;
            description = "Unit Price";
        }
    };
    public final MFieldDouble field_total = new MFieldDouble(this, "total") {
        @Override
        protected void initialize() {
            calculated = true;
            calcValue = () -> calcField_total();
        }
    };

    public final MIndex index_date = new MIndex(this, "date", "", "date", false, false) {
        @Override
        protected void initialize() {
        }
    };


    @Override
    public final String getTableName() {
        return "invoiceItem";
    }

    @Override
    public InvoiceItemData getData() {
        return new InvoiceItemData<>(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_total @@ */
    private Double calcField_total() {
        return null;
    }
    /* @@ end calcField_total @@ */
}
