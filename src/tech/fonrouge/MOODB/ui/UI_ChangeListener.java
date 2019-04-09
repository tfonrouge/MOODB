package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MFieldTableField;

public abstract class UI_ChangeListener<T, U> implements ChangeListener<T> {
    U property;
    MFieldTableField mFieldTableField = null;
    MField<T> mField;
    private boolean ignore = false;

    abstract T propertyGetValue();

    abstract void propertySetValue(T oldValue);

    @Override
    final public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (mFieldTableField == null) {
            if (!ignore) {
                if (!mField.setValue(newValue)) {
                    ignore = true;
                    Platform.runLater(() -> {
                        propertySetValue(oldValue);
                        ignore = false;
                    });
                }
            }
        } else {
            if (!ignore) {
                if (!mField.find(newValue)) {
                    ignore = true;
                    Platform.runLater(() -> {
                        propertySetValue(oldValue);
                        ignore = false;
                    });
                }
            }
        }
    }

    final public void update(Object value) {
        if (mFieldTableField != null && !value.equals(mFieldTableField.linkedTable()._id())) {
            mFieldTableField.syncedTable();
        }
        if (!mField.value().equals(propertyGetValue())) {
            propertySetValue(mField.value());
        }
    }
}
