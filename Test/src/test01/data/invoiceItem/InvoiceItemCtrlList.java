package test01.data.invoiceItem;

import test01.data.base01.Base01CtrlList;

public class InvoiceItemCtrlList extends Base01CtrlList<InvoiceItem> {

    @Override
    protected String[] getColumns() {
        return new String[]{"rowNumber", "invoice.customer", "invItem.name", "qty"};
    }

    @Override
    protected String getResourceRecordName() {
        return "/test01/data/invoiceItem/record.fxml";
    }

    @Override
    protected InvoiceItem buildTable() {
        return new InvoiceItem();
    }
}
