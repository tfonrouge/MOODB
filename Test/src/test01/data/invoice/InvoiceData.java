package test01.data.invoice;

import org.bson.types.ObjectId;
import test01.data.base01.Base01Data;

import java.util.Date;

public class InvoiceData<T extends Invoice> extends Base01Data<T> {

    /* @@ begin field descriptor @@ */
    public InvoiceData(T invoice) {
        super(invoice);
    }

    public Integer getDocNumber() {
        return tableState.getFieldValue(table.field_docNumber, Integer.class);
    }

    public ObjectId getCustomer() {
        return tableState.getFieldValue(table.field_customer, ObjectId.class);
    }

    public Date getDate() {
        return tableState.getFieldValue(table.field_date, Date.class);
    }

    public Integer getDaysOfCredit() {
        return tableState.getFieldValue(table.field_daysOfCredit, Integer.class);
    }

    public Long getItemsCount() {
        return tableState.getFieldValue(table.field_itemsCount, Long.class);
    }
    /* @@ end field descriptor @@ */
}

