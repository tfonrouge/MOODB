package tech.fonrouge.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;

import java.lang.reflect.Method;

public abstract class UI_ChangeListener0<T, N extends Node, U> implements ChangeListener<U> {

    public N node;
    MField<T> mField;
    MTable linkedTable;

    UI_ChangeListener0(N node, MField<T> mField, MTable linkedTable) {
        this.node = node;
        this.mField = mField;
        this.linkedTable = linkedTable;
        initialize(node);
        propertyAddListener();
        set_UI_state(node);
        addChangeListener(mField);
        if (mField.getTable().getLinkedField() != null) {
            mField.getTable().getLinkedField().getFieldState().addListener(this);
        }
    }

    private void addChangeListener(MField mField) {
        mField.getFieldState().addListener(this);
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

    abstract void propertyAddListener();

    abstract void initialize(N node);

    public void refreshNode(boolean fullUpdate) {
        if (fullUpdate) {
            initialize(node);
            set_UI_state(node);
        }
        update(mField.value());
    }

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

    abstract public void update(T value);
}
