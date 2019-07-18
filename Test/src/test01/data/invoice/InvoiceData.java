package test01.data.invoice;

import org.bson.types.ObjectId;
import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import test01.data.tableBase.TableBaseData;
import java.util.Date;

@AutoGenerated()
public class InvoiceData<T extends Invoice> extends TableBaseData<T> {

    /**
     * constructor
     */
    @AutoGenerated()
    public InvoiceData(T invoice) {
        super(invoice);
    }

    /**
     * getDocNumber
     */
    @AutoGenerated()
    public Integer getDocNumber() {
        return tableState.getFieldValue(table.field_docNumber, Integer.class);
    }

    /**
     * getDate
     */
    @AutoGenerated()
    public Date getDate() {
        return tableState.getFieldValue(table.field_date, Date.class);
    }

    /**
     * getDaysOfCredit
     */
    @AutoGenerated()
    public Integer getDaysOfCredit() {
        return tableState.getFieldValue(table.field_daysOfCredit, Integer.class);
    }

    /**
     * getItemsCount
     */
    @AutoGenerated()
    public Long getItemsCount() {
        return tableState.getFieldValue(table.field_itemsCount, Long.class);
    }

    /**
     * getReqShipment
     */
    @AutoGenerated()
    public Boolean getReqShipment() {
        return tableState.getFieldValue(table.field_reqShipment, Boolean.class);
    }

    /**
     * getCustomer
     */
    @AutoGenerated()
    public ObjectId getCustomer() {
        return tableState.getFieldValue(table.field_customer, ObjectId.class);
    }
}
