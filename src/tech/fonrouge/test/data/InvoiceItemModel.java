package tech.fonrouge.test.data;

import org.bson.types.ObjectId;

public class InvoiceItemModel extends BaseModel {

    protected InvoiceItem invoiceitem;

    public void setTable(InvoiceItem invoiceitem) {
        this.invoiceitem = invoiceitem;
    }

    public ObjectId getInvoice() {
        return invoiceitem.field_invoice.value();
    }

    public ObjectId getInvItem() {
        return invoiceitem.field_invItem.value();
    }

    public Double getQty() {
        return invoiceitem.field_qty.value();
    }

    public Double getUnitPrice() {
        return invoiceitem.field_unitPrice.value();
    }

    public Double getTotal() {
        return invoiceitem.field_total.value();
    }
}
