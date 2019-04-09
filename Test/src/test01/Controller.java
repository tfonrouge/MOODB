package test01;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import tech.fonrouge.MOODB.ui.UI_BaseController;
import test01.data.invoice.Invoice;
import test01.data.invoiceItem.InvoiceItem;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends UI_BaseController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    protected String getBaseResourcePath() {
        return "/test01/ui/baseList.fxml";
    }

    public void onActionInvoices(ActionEvent actionEvent) {

        Invoice invoice = new Invoice();
        invoice.addLookupField("customer.name");

        showList(invoice);

    }

    public void onActionInvoiceItems(ActionEvent actionEvent) {

        showList(new InvoiceItem());

    }
}
