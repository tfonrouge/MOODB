package test01.data.invoiceItem;

import javafx.scene.control.TableView;
import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceItemCtrlList<T extends InvoiceItem> extends TableBaseCtrlList<T, InvoiceItemData<T>> {

    public TableView tableView_invoiceItem;

    @Override
    protected String[] getFieldColumnList() {
        return new String[]{"rowNumber", "invoice.docNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }

    @Override
    protected String getCtrlRecordFXMLPath() {
        return "/test01/data/invoiceItem/record.fxml";
    }
}
