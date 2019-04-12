package tech.fonrouge.MOODB.ui;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.MField;

class UI_ChangeListenerStringProperty<T> extends UI_ChangeListener0<T, TextField, String> {

    private StringProperty property;

    UI_ChangeListenerStringProperty(TextField textField, MField<T> mField) {
        super(textField, mField);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        mField.setValueAsString(newValue);
    }

    @Override
    void initialize(TextField node) {
        node.setPromptText(mField.getDescription());
        property = node.textProperty();
        property.setValue(mField.valueAsString());
    }

    @Override
    public void update(String value) {
        if (!mField.value().equals(property.getValue())) {
            property.setValue(value);
        }
    }
}
