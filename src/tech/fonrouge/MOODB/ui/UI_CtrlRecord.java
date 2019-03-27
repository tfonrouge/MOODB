package tech.fonrouge.MOODB.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

import java.util.HashMap;
import java.util.Map;

public abstract class UI_CtrlRecord<T extends MTable> extends UI_Binding {

    protected T table;
    private UI_CtrlList<T> ctrlList;

    <U extends UI_CtrlList<T>> void setCtrlList(U ctrlList) {
        this.ctrlList = ctrlList;
        this.table = ctrlList.getTable();
        initData();
    }

    protected abstract void initData();

    @FXML
    private void onActionButtonAceptar(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        if (table.getState() != MTable.STATE.NORMAL) {
            HashMap<String, String> list = table.getInvalidFieldList();
            if (list.size() > 0) {
                Map.Entry<String, String> i = list.entrySet().iterator().next();
                final MField[] mField = new MField[1];
                table.getFieldListStream().forEach(mField1 -> {
                    if (mField1.getName().contentEquals(i.getKey())) {
                        mField[0] = mField1;
                    }
                });
                new Alert(Alert.AlertType.WARNING, i.getValue() + ": '" + mField[0].getLabel() + "'").showAndWait();
                Node node = nodeHashMap.get(i.getKey());
                if (node != null) {
                    node.requestFocus();
                }
                return;
            }
            if (!table.post()) {
                System.out.println("Error: " + table.getException());
                table.cancel();
            }
            ctrlList.populateList();
        }
        if (source != null) {
            source.getScene().getWindow().hide();
        }
    }
}
