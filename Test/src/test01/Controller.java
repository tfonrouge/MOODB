package test01;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import tech.fonrouge.MOODB.ui.UI_BaseController;
import test01.data.invoice.InvoiceCtrlList;

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

    public void onActionButton(ActionEvent actionEvent) {

        showList(new InvoiceCtrlList());

    }
}
