package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

import java.util.HashMap;

class UI_ChangeListenerComboBox<T> extends UI_ChangeListener<T, ComboBox<T>> {

    private ObjectProperty<T> property;

    UI_ChangeListenerComboBox(ComboBox<T> comboBox, MField<T> mField) {
        super(comboBox, mField);
    }

    private ObservableList<T> getObservableArrayList() {
        ObservableList<T> observableList = FXCollections.observableArrayList();
        if (mField.getTable().getLinkedField() == null) {
            HashMap<T, String> keyValueItems = mField.getValueItems();
            if (keyValueItems != null) {
                keyValueItems.forEach((key, value) -> observableList.add(key));
            }
        } else {
            MTable mTable = mField.getTable().getLinkedField().linkedTable();
            mTable.tableStatePush();

            if (mField != null && mTable.find()) {
                while (!mTable.getEof()) {
                    T value = mField.value();
                    observableList.add(value);
                    mTable.next();
                }
            }
            mTable.tableStatePull();
        }
        return observableList;
    }

    void initialize(ComboBox<T> comboBox) {
        comboBox.setPromptText(mField.getDescription());
        comboBox.setItems(getObservableArrayList());
        property = comboBox.valueProperty();
        property.setValue(mField.value());
        property.addListener(this);
    }

    @Override
    T propertyGetValue() {
        return property.getValue();
    }

    @Override
    void propertySetValue(T oldValue) {
        property.setValue(oldValue);
    }

    @Override
    public void removePropertyListener() {
        property.removeListener(this);
    }

    @Override
    void set_UI_state(ComboBox<T> comboBox) {
        if (getTable().getState() == MTable.STATE.NORMAL || getWorkField().isReadOnly()) {
            comboBox.setItems(null);
            comboBox.getEditor().editableProperty().setValue(false);
            comboBox.setFocusTraversable(false);
        }
    }
}
