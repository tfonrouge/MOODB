package tech.fonrouge.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

import java.util.HashMap;

class UI_ChangeListenerComboBox<T> extends UI_ChangeListener<T, ComboBox<T>> {

    private ObjectProperty<T> property;

    UI_ChangeListenerComboBox(ComboBox<T> comboBox, MField<T> mField, MTable linkedTable) {
        super(comboBox, mField, linkedTable);
    }

    @Override
    void propertyAddListener() {
        property.addListener(this);
    }

    private ObservableList<T> getObservableArrayList() {
        ObservableList<T> observableList = FXCollections.observableArrayList();
        if (mField.getTable().getLinkedField() == null) {
            HashMap<T, String> keyValueItems = mField.getValueItems();
            if (keyValueItems != null) {
                keyValueItems.forEach((key, value) -> observableList.add(key));
            }
        } else {
            MTable mTable;

            MField mField1;

            if (linkedTable == null) {
                mTable = mField.getTable().getLinkedField().linkedTable();
                mField1 = mField;
            } else {
                mTable = linkedTable;
                mField1 = mTable.fieldByName(mField.getName());
            }

            if (mTable != null) {
                mTable.tableStatePush();
                if (mField1 != null && mTable.find()) {
                    while (!mTable.getEof()) {
                        T value = (T) mField1.value();
                        observableList.add(value);
                        mTable.next();
                    }
                }
                mTable.tableStatePull();
            }
        }
        return observableList;
    }

    void initialize(ComboBox<T> comboBox) {
        comboBox.setPromptText(mField.getDescription());
        comboBox.setItems(getObservableArrayList());
        property = comboBox.valueProperty();
        property.setValue(mField.value());
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
