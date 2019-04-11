package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MFieldTableField;

public abstract class UI_ChangeListener<T> implements ChangeListener<T> {
    public Node node;
    MFieldTableField mFieldTableField = null;
    MField<T> mField;
    private boolean ignore = false;

    UI_ChangeListener(Node node, MField<T> mField) {
        this.node = node;
        this.mField = mField;
        this.mField.getFieldState().ui_changeListener = this;
    }

    UI_ChangeListener(Node node, MFieldTableField mFieldTableField, String detailField) {
        this.node = node;
        this.mFieldTableField = mFieldTableField;
        this.mField = mFieldTableField.syncedTable().fieldByName(detailField);
        this.mFieldTableField.getFieldState().ui_changeListener = this;
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

    final public void update(Object value) {
        if (mFieldTableField != null && !value.equals(mFieldTableField.linkedTable()._id())) {
            mFieldTableField.syncedTable();
        }
        if (!mField.value().equals(propertyGetValue())) {
            propertySetValue(mField.value());
        }
    }
}
