package test01.data;

import org.bson.types.Binary;

import org.bson.types.ObjectId;

import java.util.Date;

public class InvoiceData<T extends Invoice> extends BaseData<T> {

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
    /* @@ end field descriptor @@ */
}

