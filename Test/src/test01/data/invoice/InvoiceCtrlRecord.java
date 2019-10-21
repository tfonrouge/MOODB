package test01.data.invoice;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import test01.data.tableBase.TableBaseCtrlRecord;

public class InvoiceCtrlRecord<T extends Invoice, U extends InvoiceData<T>> extends TableBaseCtrlRecord<T, U> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer_name;
    public TextField textField_daysOfCredit;
    public Spinner<Integer> spinner_daysOfCredit;
    public CheckBox checkBox_reqShipment;
    public TextField textField_customer_address;
    public TextField textField_deliveryCost;
}
