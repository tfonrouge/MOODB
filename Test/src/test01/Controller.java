package test01;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import tech.fonrouge.ui.UI_CtrlList;
import test01.data.invoice.Invoice;
import test01.data.invoiceItem.InvoiceItem;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onActionInvoices(ActionEvent actionEvent) {

        UI_CtrlList.ctrlList(new Invoice()).show();

    }

    public void onActionInvoiceItems(ActionEvent actionEvent) {

        UI_CtrlList.ctrlList(new InvoiceItem()).show();

    }
}
