package tech.fonrouge.ui;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

public abstract class UI_ChangeListener<T, N extends Node> extends UI_ChangeListener0<T, N, T> {
    private boolean ignore = false;

    UI_ChangeListener(N node, MField<T> mField, MTable linkedTable) {
        super(node, mField, linkedTable);
    }

    @Override
    final public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (!ignore && !mField.valueEquals(newValue)) {
            boolean result;
            if (mField.getTable().getLinkedField() == null) {
                result = setmFieldValue(newValue);
            } else {
                result = mField.find(newValue);
            }
            if (!result) {
                ignore = true;
                propertySetValue(oldValue);
                ignore = false;
            }
        }
    }

    abstract T propertyGetValue();

    abstract void propertySetValue(T oldValue);

    @Override
    public void update(T value) {
        if (value != null) {
            if (mField.getTable().getLinkedField() != null && !value.equals(mField.getTable().getLinkedField().linkedTable()._id())) {
                mField.getTable().getLinkedField().syncedTable();
            }
        }
        if (!mField.valueEquals(propertyGetValue())) {
            propertySetValue(mField.value());
        }
    }
}
