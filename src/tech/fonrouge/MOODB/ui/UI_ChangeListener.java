package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MFieldTableField;

public abstract class UI_ChangeListener<T, N extends Node> extends UI_ChangeListener0<T, N, T> {
    MFieldTableField mFieldTableField = null;
    private boolean ignore = false;

    UI_ChangeListener(N node, MField<T> mField) {
        super(node, mField);
        setChangeListener(mField);
    }

    UI_ChangeListener(N node, MFieldTableField mFieldTableField, String detailField) {
        super(node, mFieldTableField.syncedTable().fieldByName(detailField));
        this.mFieldTableField = mFieldTableField;
        setChangeListener(mFieldTableField);
    }

    abstract T propertyGetValue();

    abstract void propertySetValue(T oldValue);

    @Override
    final public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!ignore) {
            if (mFieldTableField == null) {
                if (!mField.setValue(newValue)) {
                    ignore = true;
                    Platform.runLater(() -> {
                        propertySetValue(oldValue);
                        ignore = false;
                    });
                }
            } else {
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

    @Override
    public void update(Object value) {
        if (mFieldTableField != null && !value.equals(mFieldTableField.linkedTable()._id())) {
            mFieldTableField.syncedTable();
        }
        if (!mField.value().equals(propertyGetValue())) {
            propertySetValue(mField.value());
        }
    }
}
