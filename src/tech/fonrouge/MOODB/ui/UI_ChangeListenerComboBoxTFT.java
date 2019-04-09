package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import tech.fonrouge.MOODB.MFieldTableField;
import tech.fonrouge.MOODB.MTable;

public class UI_ChangeListenerComboBoxTFT extends UI_ChangeListenerComboBox<Object, ObjectProperty<Object>> {

    UI_ChangeListenerComboBoxTFT(ComboBox<Object> comboBox, MFieldTableField mFieldTableField, String detailField) {
        property = comboBox.valueProperty();
        this.mFieldTableField = mFieldTableField;
        mField = mFieldTableField.syncedTable().fieldByName(detailField);

        property.setValue(mField.value());
        fillComboBoxList(comboBox);
        comboBox.setPromptText(mFieldTableField.getDescription());

        mFieldTableField.getFieldState().setChangeListener(this);
    }

    @Override
    void fillComboBoxList(ComboBox<Object> comboBox) {
        ObservableList<Object> o = FXCollections.observableArrayList();
        MTable mTable = mFieldTableField.linkedTable();
        mTable.tableStatePush();

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

    @Override
    Object propertyGetValue() {
        return property.getValue();
    }

    @Override
    void propertySetValue(Object oldValue) {
        property.setValue(oldValue);
    }
}
