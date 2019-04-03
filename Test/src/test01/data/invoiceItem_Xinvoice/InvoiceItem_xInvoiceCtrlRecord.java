package test01.data.invoiceItem_xInvoice;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.ui.UI_CtrlRecord;

public class InvoiceItem_xInvoiceCtrlRecord extends UI_CtrlRecord<InvoiceItem_xInvoice> {

    public TextField textField_invoiceNo;
    public ComboBox<Object> comboBox_invItem;
    public TextField textField_qty;
    public TextField textField_price;

    @Override
    protected void initData() {
        bindControl(textField_invoiceNo, table.field_invoice.syncLinkedTable().field_docNumber);
        bindControl(comboBox_invItem, table.field_invItem, "name");
        bindControl(textField_qty, table.field_qty);
        bindControl(textField_price, table.field_unitPrice);
    }
}
