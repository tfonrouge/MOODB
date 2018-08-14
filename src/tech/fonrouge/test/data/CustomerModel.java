package tech.fonrouge.test.data;

public class CustomerModel extends EntityModel {

    protected Customer customer;

    public void setTable(Customer customer) {
        this.customer = customer;
    }

    public String getCustomerId() {
        return customer.field_customerId.value();
    }
}
