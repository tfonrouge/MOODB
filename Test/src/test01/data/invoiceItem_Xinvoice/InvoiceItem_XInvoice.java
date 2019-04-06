package test01.data.invoiceItem_xInvoice;

import tech.fonrouge.MOODB.MIndex;
import test01.data.invoice.Invoice;
import test01.data.invoiceItem.InvoiceItem;

public class InvoiceItem_xInvoice extends InvoiceItem {

    /* @@ begin field descriptor @@ */
    public final MIndex index_invoice_invItem = new MIndex(this, "invoice_invItem", "invoice", "invItem", true, false) {
        @Override
        protected void initialize() {
        }
    };

    public InvoiceItem_xInvoice(Invoice masterSource) {
        setMasterSource(masterSource, field_invoice);
    }

    @Override
    public Invoice getMasterSource() {
        return (Invoice) super.getMasterSource();
    }

    @Override
    public InvoiceItem_xInvoiceData getData() {
        return new InvoiceItem_xInvoiceData<>(this);
    }
    /* @@ end field descriptor @@ */
}
