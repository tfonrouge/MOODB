package test01.data.invoice;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceCtrlRecord extends UI_CtrlRecord<Invoice> {

    public TextField textField_docNumber;
    public TextField textField_date;
    public ComboBox<Object> comboBox_customer;

    @FXML
    public TableView<InvoiceData> tableView_invoiceItem_xInvoice;

    @Override
    protected void initData() {
        bindControl(textField_docNumber, table.field_docNumber);
        bindControl(textField_date, table.field_date);
        bindControl(comboBox_customer, table.field_customer, "name");
        System.out.println(tableView_invoiceItem_xInvoice);
    }
}
