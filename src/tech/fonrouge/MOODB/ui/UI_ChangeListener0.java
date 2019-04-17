package tech.fonrouge.MOODB.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

public abstract class UI_ChangeListener0<T, N extends Node, U> implements ChangeListener<U> {

    public N node;
    MField<T> mField;

    UI_ChangeListener0(N node, MField<T> mField) {
        this.node = node;
        this.mField = mField;
        initialize(this.node);
        set_UI_state(node);
        addChangeListener(mField);
        if (mField.getTable().getLinkedField() != null) {
            mField.getTable().getLinkedField().getFieldState().addListener(this);
        }
    }

    MTable getTable() {
        return getWorkField().getTable();
    }

    MField getWorkField() {
        if (mField.getTable().getLinkedField() != null) {
            return mField.getTable().getLinkedField();
        }
        return mField;
    }

    abstract void initialize(N node);

    abstract public void update(T value);

    public abstract void removePropertyListener();

    abstract void set_UI_state(N node);

    boolean setmFieldValue(T value) {
        mField.getFieldState().setCurrentChangeListener(this);
        boolean result = mField.setValue(value);
        mField.getFieldState().clearCurrentChangeListener();
        return result;
    }

    void setmFieldValueAsString(String value) {
        mField.getFieldState().setCurrentChangeListener(this);
        mField.setValueAsString(value);
        mField.getFieldState().clearCurrentChangeListener();
    }

    private void addChangeListener(MField mField) {
        mField.getFieldState().addListener(this);
    }

    public void refreshNode() {
        update(mField.value());
    }
}
