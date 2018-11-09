package test01.data;

import tech.fonrouge.MOODB.*;

public class InvoiceItem_XInvoice extends InvoiceItem {

    /* @@ begin field descriptor @@ */



    public InvoiceItem_XInvoice(Invoice masterSource) {
        setMasterSource(masterSource, field_invoice);
    }
    /* @@ end field descriptor @@ */
}
