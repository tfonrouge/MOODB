package test01.data.invoice;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceCtrlRecord extends UI_CtrlRecord<Invoice> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer_name;
    public TextField textField_daysOfCredit;
    public Spinner<Integer> spinner_daysOfCredit;

    @Override
    protected void initData() {

        bindControls();

    }
}
