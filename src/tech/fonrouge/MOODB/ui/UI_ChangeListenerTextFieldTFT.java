package tech.fonrouge.MOODB.ui;

import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MFieldTableField;

public class UI_ChangeListenerTextFieldTFT extends UI_ChangeListener<String, StringProperty> {

    UI_ChangeListenerTextFieldTFT(TextField textField, MFieldTableField mFieldTableField, String detailField) {
        this.property = textField.textProperty();
        this.mFieldTableField = mFieldTableField;

        mField = (MFieldString) mFieldTableField.syncedTable().fieldByName(detailField);
        property.setValue(mField.valueAsString());
        textField.setPromptText(mFieldTableField.getDescription());

        mFieldTableField.getFieldState().setChangeListener(this);
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
