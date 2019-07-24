package tech.fonrouge.ui;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextInputControl;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

class UI_ChangeListenerTextInputControl<T> extends UI_ChangeListener0<T, TextInputControl, String> {

    private StringProperty property;

    UI_ChangeListenerTextInputControl(TextInputControl textInputControl, MField<T> mField) {
        super(textInputControl, mField);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        setmFieldValueAsString(newValue);
    }

    @Override
    void initialize(TextInputControl node) {
        node.setPromptText(mField.getDescription());
        property = node.textProperty();
        property.setValue(mField.valueAsString());
        property.addListener(this);
    }

    @Override
    public void removePropertyListener() {
        property.removeListener(this);
    }

    @Override
    void set_UI_state(TextInputControl textInputControl) {
        if (getTable().getState() == MTable.STATE.NORMAL || getWorkField().isReadOnly()) {
            textInputControl.editableProperty().setValue(false);
            textInputControl.setFocusTraversable(false);
        }
    }

    @Override
    public void update(T value) {
        if (!mField.valueAsString().equals(property.getValue())) {
            property.setValue(mField.valueAsString(value));
        }
    }
}
