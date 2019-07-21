package test01.data.invoiceItem;

import javafx.scene.control.TableView;
import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceItemCtrlList extends TableBaseCtrlList<InvoiceItem, InvoiceItemData<InvoiceItem>> {

    public TableView tableView_invoiceItem;

    @Override
    protected String[] getFieldColumnList() {
        return new String[]{"rowNumber", "invoice.docNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }
}
