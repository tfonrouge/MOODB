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
    }

    abstract void initialize(N node);

    abstract public void update(U value);

    void setChangeListener(MField mField) {
        mField.getFieldState().ui_changeListener = this;
    }
}
