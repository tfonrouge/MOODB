package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;

public abstract class UI_ChangeListener<T, N extends Node> extends UI_ChangeListener0<T, N, T> {
    private boolean ignore = false;

    UI_ChangeListener(N node, MField<T> mField) {
        super(node, mField);
    }

    abstract T propertyGetValue();

    abstract void propertySetValue(T oldValue);

    @Override
    final public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!ignore) {
            ignore = true;
            if (mField.getTable().getLinkedField() == null) {
                if (!setmFieldValue(newValue)) {
                    Platform.runLater(() -> {
                        propertySetValue(oldValue);
                        ignore = false;
                    });
                }
            } else {
                if (!mField.find(newValue)) {
                    Platform.runLater(() -> {
                        System.out.println("reverting value to " + oldValue);
                        propertySetValue(oldValue);
                        ignore = false;
                    });
                }
            }
        }
    }

    @Override
    public void update(T value) {
        if (mField.getTable().getLinkedField() != null && !value.equals(mField.getTable().getLinkedField().linkedTable()._id())) {
            mField.getTable().getLinkedField().syncedTable();
        }
        if (!mField.value().equals(propertyGetValue())) {
            propertySetValue(mField.value());
        }
    }
}
