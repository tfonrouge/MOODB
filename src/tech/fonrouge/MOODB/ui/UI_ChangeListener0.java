package tech.fonrouge.MOODB.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import tech.fonrouge.MOODB.MField;

public abstract class UI_ChangeListener0<T, N extends Node, U> implements ChangeListener<U> {

    public N node;
    MField<T> mField;

    UI_ChangeListener0(N node, MField<T> mField) {
        this.node = node;
        this.mField = mField;
        initialize(this.node);
        addChangeListener(mField);
    }

    abstract void initialize(N node);

    abstract public void update(T value);

    public abstract void removePropertyListener();

    boolean setmFieldValue(T value) {
        mField.getFieldState().setCurrentChangeListener(this);
        boolean result = mField.setValue(value);
        mField.getFieldState().clearCurrentChangeListener();
        return result;
    }

    void setmFieldValueAsString(String value) {
        mField.getFieldState().setCurrentChangeListener(this);
        boolean result = mField.setValueAsString(value);
        mField.getFieldState().clearCurrentChangeListener();
    }

    private void addChangeListener(MField mField) {
        mField.getFieldState().addListener(this);
    }

    public void refreshNode() {
        update(mField.value());
    }
}
