package test01.data.invoiceItem;

import org.bson.types.ObjectId;
import test01.data.base.Base01Data;

public class InvoiceItemData<T extends InvoiceItem> extends Base01Data<T> {

    /* @@ begin field descriptor @@ */
    public InvoiceItemData(T invoiceitem) {
        super(invoiceitem);
    }

    public ObjectId getInvoice() {
        return tableState.getFieldValue(table.field_invoice, ObjectId.class);
    }

    public ObjectId getInvItem() {
        return tableState.getFieldValue(table.field_invItem, ObjectId.class);
    }

    public Double getQty() {
        return tableState.getFieldValue(table.field_qty, Double.class);
    }

    public Double getUnitPrice() {
        return tableState.getFieldValue(table.field_unitPrice, Double.class);
    }

    public Double getTotal() {
        return tableState.getFieldValue(table.field_total, Double.class);
    }
    /* @@ end field descriptor @@ */
}

