package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import tech.fonrouge.MOODB.MFieldString;

import java.util.HashMap;

public class UI_ChangeListenerComboBoxString extends UI_ChangeListenerComboBox<String, ObjectProperty<String>> {

    UI_ChangeListenerComboBoxString(ComboBox<String> comboBox, MFieldString fieldString) {
        this.mField = fieldString;
        this.property = comboBox.valueProperty();

        property.setValue(fieldString.value());
        fillComboBoxList(comboBox);
        comboBox.setPromptText(fieldString.getDescription());

        fieldString.getFieldState().setChangeListener(this);
    }

    @Override
    void fillComboBoxList(ComboBox<String> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList());
        HashMap<String, String> keyValueItems = mField.getValueItems();
        if (keyValueItems != null) {
            keyValueItems.forEach((key, value) -> comboBox.getItems().add(key));
        }
    }

    @Override
    String propertyGetValue() {
        return property.getValue();
    }

    @Override
    void propertySetValue(String oldValue) {
        property.setValue(oldValue);
    }
}
