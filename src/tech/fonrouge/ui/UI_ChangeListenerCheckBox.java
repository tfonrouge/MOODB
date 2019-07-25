package tech.fonrouge.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

class UI_ChangeListenerCheckBox extends UI_ChangeListener0<Boolean, CheckBox, Boolean> {

    private BooleanProperty property;

    UI_ChangeListenerCheckBox(CheckBox checkBox, MField<Boolean> mField) {
        super(checkBox, mField);
    }

    @Override
    void propertyAddListener() {
        property.addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        setmFieldValue(newValue);
    }

    @Override
    void initialize(CheckBox checkBox) {
        property = checkBox.selectedProperty();
        property.setValue(mField.value());
    }

    @Override
    public void removePropertyListener() {
        property.removeListener(this);
    }

    @Override
    public void update(Boolean value) {
        if (!mField.valueEquals(property.getValue())) {
            property.setValue(value);
        }
    }

    @Override
    void set_UI_state(CheckBox checkBox) {
        if (getTable().getState() == MTable.STATE.NORMAL || getWorkField().isReadOnly()) {
            checkBox.setDisable(true);
            checkBox.setFocusTraversable(false);
        }
    }
}
