package test01.data.invoice;

import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceCtrlList extends TableBaseCtrlList<Invoice> {

    public InvoiceCtrlList(Invoice table) {
        super(table);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"docNumber", "date", "customer", "customer.name", "customer.address", "itemsCount"};
    }

    @Override
    protected String getCtrlRecordFXMLPath() {
        return "/test01/data/invoice/record.fxml";
    }
}
