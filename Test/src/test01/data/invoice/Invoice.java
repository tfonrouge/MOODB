package test01.data.invoice;

import tech.fonrouge.MOODB.*;
import test01.data.base01.Base01;
import test01.data.customer.Customer;
import test01.data.invoiceItem_xInvoice.InvoiceItem_xInvoice;

public class Invoice extends Base01 {

    /* @@ begin field descriptor @@ */

    public final MFieldInteger field_docNumber = new MFieldInteger(this, "docNumber") {
        @Override
        protected void initialize() {
            autoInc = true;
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
    public final MFieldLong field_itemsCount = new MFieldLong(this, "itemsCount") {
        @Override
        protected void initialize() {
            calculated = true;
            calcValue = () -> calcField_itemsCount();
        }
    };

    public final MIndex index_docNumber = new MIndex(this, "docNumber", "", "docNumber", true, false) {
        @Override
        protected void initialize() {
        }
    };

    public final InvoiceItem_xInvoice childTable_invoiceItem() {
        return new InvoiceItem_xInvoice(this);
    }

    @Override
    public final String getTableName() {
        return "invoice";
    }

    @Override
    public InvoiceData getData() {
        return new InvoiceData<>(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_itemsCount @@ */
    private Long calcField_itemsCount() {
        return childTable_invoiceItem().count();
    }
    /* @@ end calcField_itemsCount @@ */
}
