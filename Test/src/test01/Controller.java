package test01;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import tech.fonrouge.MOODB.ui.UI_CtrlList;
import test01.data.invoice.Invoice;
import test01.data.invoiceItem.InvoiceItem;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onActionInvoices(ActionEvent actionEvent) {

        UI_CtrlList.showList(new Invoice());

    }

    public void onActionInvoiceItems(ActionEvent actionEvent) {

        UI_CtrlList.showList(new InvoiceItem());

    }
}
