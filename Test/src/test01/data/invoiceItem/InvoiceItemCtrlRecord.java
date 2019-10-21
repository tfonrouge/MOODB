package test01.data.invoiceItem;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import test01.data.tableBase.TableBaseCtrlRecord;

public class InvoiceItemCtrlRecord extends TableBaseCtrlRecord<InvoiceItem, InvoiceItemData<InvoiceItem>> {

    public TextField textField_price;
    @FXML
    private ComboBox<Object> comboBox_invItem;
    @FXML
    private TextField spinner_qty;
}
