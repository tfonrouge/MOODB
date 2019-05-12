package test01.data.invoiceItem;

import org.bson.types.ObjectId;
import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import test01.data.tableBase.TableBaseData;

@AutoGenerated()
public class InvoiceItemData<T extends InvoiceItem> extends TableBaseData<T> {

    /* @@ begin field descriptor @@ */
    @AutoGenerated()
    public InvoiceItemData(T invoiceitem) {
        super(invoiceitem);
    }

    @AutoGenerated()
    public ObjectId getInvoice() {
        return tableState.getFieldValue(table.field_invoice, ObjectId.class);
    }

    @AutoGenerated()
    public ObjectId getInvItem() {
        return tableState.getFieldValue(table.field_invItem, ObjectId.class);
    }

    @AutoGenerated()
    public Double getQty() {
        return tableState.getFieldValue(table.field_qty, Double.class);
    }

    @AutoGenerated()
    public Double getUnitPrice() {
        return tableState.getFieldValue(table.field_unitPrice, Double.class);
    }

    @AutoGenerated()
    public Double getTotal() {
        return tableState.getFieldValue(table.field_total, Double.class);
    }
}
