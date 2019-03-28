package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import tech.fonrouge.MOODB.*;

import java.util.HashMap;

public class UI_Binding {

    @SuppressWarnings("WeakerAccess")
    protected final HashMap<String, Node> nodeHashMap = new HashMap<>();

    @SuppressWarnings("unused")
    final protected <T extends MTable> void bindControl(ComboBox<Object> comboBox, MFieldTableField<T> fieldTableField, String detailField) {
        nodeHashMap.put(fieldTableField.getName(), comboBox);
        final boolean[] ignore = {false};
        MField mField = fieldTableField.getLinkedTable().fieldByName(detailField);
        Object a = fieldTableField.syncLinkedTable().fieldByName(detailField).value();
        comboBox.valueProperty().setValue(a);
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignore[0]) {
                if (!mField.find(newValue)) {
                    ignore[0] = true;
                    Platform.runLater(() -> {
                        comboBox.valueProperty().setValue(oldValue);
                        ignore[0] = false;
                    });
                }
            }
        });
        fillComboBoxList(comboBox, fieldTableField, detailField);
        comboBox.setPromptText(fieldTableField.getDescription());
    }

    private <T extends MTable> void fillComboBoxList(ComboBox<Object> comboBox, MFieldTableField<T> fieldTableField, String detailField) {
        ObservableList<Object> o = FXCollections.observableArrayList();
        T mTable = fieldTableField.getLinkedTable();
        mTable.tableStatePush();
        MField mField = mTable.fieldByName(detailField);
        if (mField != null && mTable.find()) {
            while (!mTable.getEof()) {
                Object value = mField.value();
                o.add(value);
                mTable.next();
            }
        }
        mTable.tableStatePull();
        comboBox.setItems(o);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(ComboBox<String> comboBox, MFieldString fieldString) {
        nodeHashMap.put(fieldString.getName(), comboBox);
        final boolean[] ignore = {false};
        comboBox.valueProperty().setValue(fieldString.valueAsString());
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignore[0]) {
                if (!fieldString.setValue(newValue)) {
                    ignore[0] = true;
                    Platform.runLater(() -> {
                        comboBox.valueProperty().setValue(oldValue);
                        ignore[0] = false;
                    });
                }
            }
        });
        comboBox.setItems(FXCollections.observableArrayList());
        HashMap<String, String> keyValueItems = fieldString.getValueItems();
        if (keyValueItems != null) {
            keyValueItems.forEach((key, value) -> comboBox.getItems().add(key));
        }
        comboBox.setPromptText(fieldString.getDescription());
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldInteger fieldInteger) {
        nodeHashMap.put(fieldInteger.getName(), textField);

        textField.textProperty().set(String.valueOf(fieldInteger.value()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldInteger.setValue(Integer.valueOf(newValue)));
        textField.setPromptText(fieldInteger.getDescription());
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDouble fieldDouble) {
        nodeHashMap.put(fieldDouble.getName(), textField);

        textField.textProperty().set(String.valueOf(fieldDouble.value()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldDouble.setValue(Double.valueOf(newValue)));
        textField.setPromptText(fieldDouble.getDescription());
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextInputControl textField, MFieldString fieldString) {
        nodeHashMap.put(fieldString.getName(), textField);

        textField.textProperty().set(fieldString.value());
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldString.setValue(newValue));
        textField.setPromptText(fieldString.getDescription());
    }

    @SuppressWarnings("unused")
    final protected void bindControl(Spinner<Integer> integerSpinner, MFieldInteger fieldInteger) {
        nodeHashMap.put(fieldInteger.getName(), integerSpinner);

        integerSpinner.getValueFactory().setValue(fieldInteger.value());
        integerSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> fieldInteger.setValue(newValue));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(CheckBox checkBox, MFieldBoolean mFieldBoolean) {
        nodeHashMap.put(mFieldBoolean.getName(), checkBox);

        checkBox.setSelected(mFieldBoolean.value());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> mFieldBoolean.setValue(newValue));
    }

    final protected void bindControl(TextField textField, MFieldDate mFieldDate) {
        nodeHashMap.put(mFieldDate.getName(), textField);

        textField.textProperty().set(mFieldDate.valueAsString());
        textField.textProperty().addListener((observable, oldValue, newValue) -> mFieldDate.setValueAsString(newValue));
        textField.setPromptText(mFieldDate.getDescription());
    }
}
