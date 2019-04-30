package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

class UI_ChangeListenerSpinnerInteger extends UI_ChangeListener0<Integer, Spinner<Integer>, Integer> {

    private ObjectProperty<Integer> property;

    UI_ChangeListenerSpinnerInteger(Spinner<Integer> spinner, MField<Integer> mField) {
        super(spinner, mField);
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        setmFieldValue(newValue);
    }

    @Override
    void initialize(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory == null) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE);
            spinner.setValueFactory(valueFactory);
        }
        property = spinner.getValueFactory().valueProperty();
        property.setValue(mField.value());
        property.addListener(this);
    }

    @Override
    public void removePropertyListener() {
        property.removeListener(this);
    }

    @Override
    void set_UI_state(Spinner<Integer> spinner) {
        if (getTable().getState() == MTable.STATE.NORMAL || getWorkField().isReadOnly()) {
            spinner.editableProperty().setValue(false);
            spinner.setDisable(true);
            spinner.setFocusTraversable(false);
        }
    }

    @Override
    public void update(Integer value) {
        if (!mField.valueEquals(property.getValue())) {
            property.setValue(value);
        }
    }
}
