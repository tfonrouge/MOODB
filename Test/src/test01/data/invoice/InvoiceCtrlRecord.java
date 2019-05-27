package test01.data.invoice;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceCtrlRecord<T extends Invoice> extends UI_CtrlRecord<T, InvoiceData<T>> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer_name;
    public TextField textField_daysOfCredit;
    public Spinner<Integer> spinner_daysOfCredit;
    public CheckBox checkBox_reqShipment;
    public TextField textField_customer_address;

    @Override
    protected void initData() {

    }
}
