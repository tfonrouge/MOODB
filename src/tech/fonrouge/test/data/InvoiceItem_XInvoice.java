package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MIndex;

public class InvoiceItem_XInvoice extends InvoiceItem {

    /* @@ begin field descriptor @@ */

    public final MIndex index_invoice_invItem = new MIndex(this, "invoice_invItem", "invoice", "invItem", false, true);

    private InvoiceItem_XInvoiceModel m;

    public InvoiceItem_XInvoice(Invoice masterSource) {
        setMasterSource(masterSource, field_invoice);
    }

    @Override
    protected void initializeModel() {
        m = new InvoiceItem_XInvoiceModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */
}
