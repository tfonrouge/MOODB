package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import tech.fonrouge.MOODB.MField;

class UI_ChangeListenerSpinnerInteger extends UI_ChangeListener0<Integer, Spinner<Integer>, Integer> {

    private ObjectProperty<Integer> property;

    UI_ChangeListenerSpinnerInteger(Spinner<Integer> spinner, MField<Integer> mField) {
        super(spinner, mField);
    }

    @Override
    public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        mField.setValue(newValue);
    }

    @Override
    void initialize(Spinner<Integer> spinner) {
        SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
        if (valueFactory == null) {
            valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, mField.value());
            spinner.setValueFactory(valueFactory);
        } else {
            spinner.getValueFactory().setValue(mField.value());
        }
        property = spinner.getValueFactory().valueProperty();
    }

    @Override
    public void update(Integer value) {
        if (!mField.value().equals(property.getValue())) {
            property.setValue(value);
        }
    }
}
