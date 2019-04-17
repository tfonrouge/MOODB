package test01.data.invoice;

import tech.fonrouge.MOODB.*;
import test01.data.tableBase.TableBase;
import test01.data.customer.Customer;
import test01.data.invoiceItem_xInvoice.InvoiceItem_xInvoice;

public class Invoice extends TableBase {

    private InvoiceItem_xInvoice invoiceItemXInvoice;
    //@formatter:off
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
        }
    };

    public final MFieldInteger field_daysOfCredit = new MFieldInteger(this, "daysOfCredit") {
        @Override
        protected void initialize() {
            required = true;
        }
    };

    public final MFieldLong field_itemsCount = new MFieldLong(this, "itemsCount") {
        @Override
        protected void initialize() {
            calculated = true;
            calcValue = () -> calcField_itemsCount();
        }
    };

    public final MFieldBoolean field_reqShipment = new MFieldBoolean(this, "reqShipment") {
        @Override
        protected void initialize() {
            required = true;
        }
    };

    public final MIndex index_docNumber = new MIndex(this, "docNumber", "", "docNumber", true, false) {
        @Override
        protected void initialize() {
        }
    };

    @Override
    public final String getTableName() {
        return "invoice";
    }

    @Override
    public InvoiceData getData() {
        return new InvoiceData<>(this);
    }
    /* @@ end field descriptor @@ */
    //@formatter:on

    /* @@ begin calcField_itemsCount @@ */
    public Long calcField_itemsCount() {
        return invoiceItemXInvoice().count();
    }
    /* @@ end calcField_itemsCount @@ */

    public InvoiceItem_xInvoice invoiceItemXInvoice() {
        if (invoiceItemXInvoice == null) {
            invoiceItemXInvoice = new InvoiceItem_xInvoice(this);
        }
        return invoiceItemXInvoice;
    }
    // @formatter:on
}
