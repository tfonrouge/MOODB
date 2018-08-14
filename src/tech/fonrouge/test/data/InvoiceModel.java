package tech.fonrouge.test.data;

import org.bson.types.ObjectId;
import java.util.Date;

public class InvoiceModel extends BaseModel {

    protected Invoice invoice;

    public void setTable(Invoice invoice) {
        this.invoice = invoice;
    }

    public Integer getDocNumber() {
        return invoice.field_docNumber.value();
    }

    public ObjectId getCustomer() {
        return invoice.field_customer.value();
    }

    public Date getDate() {
        return invoice.field_date.value();
    }
}
