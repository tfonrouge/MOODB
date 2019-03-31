package test01.data.invoiceItem;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import test01.data.base01.Base01CtrlRecord;

public class InvoiceItemCtrlRecord extends Base01CtrlRecord<InvoiceItem> {

    public TextField textField_price;
    @FXML
    private ComboBox<Object> comboBox_invItem;
    @FXML
    private TextField spinner_qty;

    @Override
    protected void initData() {
        bindControl(comboBox_invItem, table.field_invItem, "name");
        bindControl(spinner_qty, table.field_qty);
        bindControl(textField_price, table.field_unitPrice);
    }
}
