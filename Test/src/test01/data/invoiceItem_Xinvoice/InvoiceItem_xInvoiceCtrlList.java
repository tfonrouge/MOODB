package test01.data.invoiceItem_xInvoice;

import javafx.scene.control.TableView;
import tech.fonrouge.MOODB.MTable;
import test01.data.base01.Base01CtrlList;

public class InvoiceItem_xInvoiceCtrlList extends Base01CtrlList {

    public TableView<InvoiceItem_xInvoiceData> tableView_invoiceItem_xInvoice;

    @Override
    protected String[] getColumns() {
        return new String[0];
    }

    @Override
    protected MTable buildTable() {
        return null;
    }

    @Override
    protected String getResourceRecordName() {
        return null;
    }
}
