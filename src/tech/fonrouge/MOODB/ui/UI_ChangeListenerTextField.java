package tech.fonrouge.MOODB.ui;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MFieldTableField;

class UI_ChangeListenerTextField extends UI_ChangeListener<String, TextInputControl> {

    private StringProperty property;

    UI_ChangeListenerTextField(TextField textField, MFieldTableField mFieldTableField, String detailField) {
        super(textField, mFieldTableField, detailField);
    }

    UI_ChangeListenerTextField(TextInputControl textInputControl, MFieldString mFieldString) {
        super(textInputControl, mFieldString);
    }

    @Override
    void initialize(TextInputControl textField) {
        textField.setPromptText(mField.getDescription());
        property = textField.textProperty();
        property.setValue(mField.value());
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
