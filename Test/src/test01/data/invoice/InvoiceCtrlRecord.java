package test01.data.invoice;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import test01.data.base01.Base01CtrlRecord;

public class InvoiceCtrlRecord extends Base01CtrlRecord<Invoice> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer;
    public TableView<InvoiceData> tableView_invoiceItem;

    @Override
    protected void initData() {
        bindControl(textField_docNumber, table.field_docNumber);
        bindControl(textField_date, table.field_date);
        bindControl(comboBox_customer, table.field_customer, "name");
    }
}
