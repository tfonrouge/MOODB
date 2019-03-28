package test01.data.customer;

import test01.data.entity.EntityData;

public class CustomerData<T extends Customer> extends EntityData<T> {

    /* @@ begin field descriptor @@ */
    public CustomerData(T customer) {
        super(customer);
    }

    public String getCustomerId() {
        return tableState.getFieldValue(table.field_customerId, String.class);
    }
    /* @@ end field descriptor @@ */
}

