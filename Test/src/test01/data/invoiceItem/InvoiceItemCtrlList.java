package test01.data.invoiceItem;

import javafx.scene.control.TableView;
import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceItemCtrlList extends TableBaseCtrlList<InvoiceItem> {

    public TableView tableView_invoiceItem;

    public InvoiceItemCtrlList(InvoiceItem table) {
        super(table);
    }

    @Override
    protected String[] getFieldColumnList() {
        return new String[]{"rowNumber", "invoice.docNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }

    @Override
    protected String getCtrlRecordFXMLPath() {
        return "/test01/data/invoiceItem/record.fxml";
    }
}
