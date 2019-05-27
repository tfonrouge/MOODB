package tech.fonrouge.MOODB.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import tech.fonrouge.MOODB.Annotations.NoAutoBinding;
import tech.fonrouge.MOODB.MBaseData;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

import java.util.HashMap;
import java.util.Map;

public abstract class UI_CtrlRecord<T extends MTable, U extends MBaseData<T>> extends UI_Binding<T> {

    @SuppressWarnings("WeakerAccess")
    protected Parent parent;
    private UI_CtrlList<T, U> ctrlList;

    protected abstract void initData();

    @SuppressWarnings("unused")
    @FXML
    private void onActionButtonAccept(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        MTable.STATE state = table.getState();
        TableView<U> tableView = ctrlList.getTableView();
        int focusedIndex = tableView.getSelectionModel().getFocusedIndex();
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (state != MTable.STATE.NORMAL) {
            if (!testValidFields()) {
                return;
            }
            if (!table.post()) {
                table.cancel();
                Exception e = table.getException();
                if (e != null) {
                    String errMsg = e.toString();
                    UI_Message.warning("Error", "Post Error", errMsg);
                }
            }
        }
        if (source != null) {
            source.getScene().getWindow().hide();
        }
    }

    <V extends UI_CtrlList<T, U>> void setCtrlList(V ctrlList) {
        this.ctrlList = ctrlList;
        this.table = ctrlList.table;
        this.parent = ctrlList.parent;
        NoAutoBinding noAutoBinding = getClass().getAnnotation(NoAutoBinding.class);
        if (noAutoBinding == null) {
            bindControls();
        }
        initData();
    }

    boolean testValidFields() {
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
                return false;
            }
            return true;
        }
        return false;
    }
}
