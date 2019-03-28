package test01.data.invoice;

import test01.data.base01.Base01CtrlList;

public class InvoiceCtrlList extends Base01CtrlList<Invoice> {

    @Override
    protected String[] getColumns() {
        return new String[]{"docNumber", "date", "customer", "customer.name", "customer.address"};
    }

    @Override
    protected String getResourceRecordName() {
        return "/test01/data/invoice/record.fxml";
    }

    @Override
    protected Invoice buildTable() {
        return new Invoice();
    }
}
