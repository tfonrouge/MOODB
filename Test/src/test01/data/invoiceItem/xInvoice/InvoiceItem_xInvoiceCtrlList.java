package test01.data.invoiceItem.xInvoice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import test01.data.tableBase.TableBaseCtrlList;

public class InvoiceItem_xInvoiceCtrlList<T extends InvoiceItem_xInvoice> extends TableBaseCtrlList<T, InvoiceItem_xInvoiceData<T>> {

    public TableView<InvoiceItem_xInvoiceData> tableView_invoiceItem_xInvoice;

    @Override
    protected String[] getFieldColumnList() {
        return new String[]{"rowNumber", "invoice.customer", "invItem.name", "qty", "unitPrice"};
    }

    @FXML
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
            case "View":
                onActionViewDocument();
                break;
        }
    }
}
