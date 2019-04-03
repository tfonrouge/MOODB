package test01.data.invoice;

import tech.fonrouge.MOODB.ui.UI_CtrlList;

public class InvoiceCtrlList extends UI_CtrlList<Invoice> {

    public InvoiceCtrlList(Invoice table) {
        super(table);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"docNumber", "date", "customer", "customer.name", "customer.address", "itemsCount"};
    }

    @Override
    protected String getResourceRecordName() {
        return "/test01/data/invoice/record.fxml";
    }
}
