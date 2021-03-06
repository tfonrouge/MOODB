package test01.data.customer;

import tech.fonrouge.ui.Annotations.AutoGenerated;
import test01.data.entity.EntityData;

@AutoGenerated()
public class CustomerData<T extends Customer> extends EntityData<T> {

    /**
     * constructor
     */
    @AutoGenerated()
    public CustomerData(T customer) {
        super(customer);
    }

    /**
     * getCustomerId
     */
    @AutoGenerated()
    public String getCustomerId() {
        return tableState.getFieldValue(table.field_customerId, String.class);
    }
}
