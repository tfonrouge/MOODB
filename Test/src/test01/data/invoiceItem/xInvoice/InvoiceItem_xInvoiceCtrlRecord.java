package test01.data.invoiceItem.xInvoice;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceItem_xInvoiceCtrlRecord extends UI_CtrlRecord<InvoiceItem_xInvoice, InvoiceItem_xInvoiceData<InvoiceItem_xInvoice>> {

    public TextField textField_invoice_docNumber;
    public ComboBox<Object> comboBox_invItem_name;
    public TextField textField_qty;
    public TextField textField_unitPrice;

    @Override
    protected void initData() {

    }
}
