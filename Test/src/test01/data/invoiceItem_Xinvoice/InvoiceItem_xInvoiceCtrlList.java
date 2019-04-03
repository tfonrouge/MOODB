package test01.data.invoiceItem_xInvoice;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import tech.fonrouge.MOODB.ui.UI_CtrlList;

public class InvoiceItem_xInvoiceCtrlList extends UI_CtrlList<InvoiceItem_xInvoice> {

    public TableView<InvoiceItem_xInvoiceData> tableView_invoiceItem_xInvoice;

    public InvoiceItem_xInvoiceCtrlList(InvoiceItem_xInvoice table) {
        super(table);
    }

    @Override
    protected String[] getColumns() {
        return new String[]{"rowNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }

    @Override
    protected String getResourceRecordName() {
        return "/test01/data/invoiceItem_xInvoice/record.fxml";
    }

    public void onActionButton(ActionEvent actionEvent) {
        Button o = (Button) actionEvent.getSource();

        switch (o.getText()) {
            case "Insert":
                onActionInsertDocument();
                break;
            case "Edit":
                onActionEditDocument();
                break;
            case "Delete":
                onActionDeleteDocument();
                break;
        }
    }
}
