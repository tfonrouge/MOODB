package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MFieldTableField;
import tech.fonrouge.MOODB.MTable;

import java.util.HashMap;

class UI_ChangeListenerComboBox<T> extends UI_ChangeListener<T> {

    private ObjectProperty<T> property;

    UI_ChangeListenerComboBox(ComboBox<T> comboBox, MField<T> mField) {
        super(comboBox, mField);
        initialize(comboBox);
    }

    UI_ChangeListenerComboBox(ComboBox<T> comboBox, MFieldTableField fieldTableField, String detailField) {
        super(comboBox, fieldTableField, detailField);
        initialize(comboBox);
    }

    private void initialize(ComboBox<T> comboBox) {
        comboBox.setPromptText(mField.getDescription());
        comboBox.setItems(getObservableArrayList());
        property = comboBox.valueProperty();
        property.setValue(mField.value());
    }

    private ObservableList<T> getObservableArrayList() {
        ObservableList<T> observableList = FXCollections.observableArrayList();
        if (mFieldTableField == null) {
            HashMap<T, String> keyValueItems = mField.getValueItems();
            if (keyValueItems != null) {
                keyValueItems.forEach((key, value) -> observableList.add(key));
            }
        } else {
            MTable mTable = mFieldTableField.linkedTable();
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

    @Override
    T propertyGetValue() {
        return property.getValue();
    }

    @Override
    void propertySetValue(T oldValue) {
        property.setValue(oldValue);
    }
}
