package test01.data.invoiceItem;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceItemCtrlRecord extends UI_CtrlRecord<InvoiceItem> {

    public TextField textField_price;
    @FXML
    private ComboBox<Object> comboBox_invItem;
    @FXML
    private TextField spinner_qty;

    @Override
    protected void initData() {

        bindControls();

    }
}
