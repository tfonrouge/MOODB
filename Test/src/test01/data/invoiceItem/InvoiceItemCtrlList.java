package test01.data.invoiceItem;

import javafx.scene.control.TableView;
import tech.fonrouge.MOODB.ui.UI_CtrlList;

public class InvoiceItemCtrlList extends UI_CtrlList<InvoiceItem> {

    public TableView tableView_invoiceItem;

    public InvoiceItemCtrlList(InvoiceItem table) {
        super(table);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"rowNumber", "invoice.docNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }

    @Override
    protected String getResourceRecordName() {
        return "/test01/data/invoiceItem/record.fxml";
    }
}
