package test01.data.invoice;

import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceCtrlList<T extends Invoice> extends TableBaseCtrlList<T, InvoiceData<T>> {

    @Override
    protected String[] getFieldColumnList() {
        return new String[]{"docNumber", "daysOfCredit", "date", "customer", "customer.name", "customer.address", "itemsCount"};
    }

    @Override
    protected String getCtrlRecordFXMLPath() {
        return "/test01/data/invoice/record.fxml";
    }
}
