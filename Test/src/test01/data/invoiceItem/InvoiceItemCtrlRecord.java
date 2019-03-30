package test01.data.invoiceItem;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import test01.data.base01.Base01CtrlRecord;
import test01.data.invoice.Invoice;
import test01.data.invoice.InvoiceData;

public class InvoiceItemCtrlRecord extends Base01CtrlRecord<Invoice> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer;

    @Override
    protected void initData() {
        bindControl(textField_docNumber, table.field_docNumber);
        bindControl(textField_date, table.field_date);
        bindControl(comboBox_customer, table.field_customer, "name");
    }
}
