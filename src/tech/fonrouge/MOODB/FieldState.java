package tech.fonrouge.MOODB;

import org.bson.Document;
import tech.fonrouge.ui.UI_ChangeListener0;

import java.util.ArrayList;
import java.util.List;

public class FieldState<T> {
    T filterValue;
    T value;
    T defaultValue;
    T origValue;
    MTable linkedTable;
    Document document;
    private List<UI_ChangeListener0> changeListenerList;
    private UI_ChangeListener0 currentChangeListener;

    FieldState() {
        changeListenerList = new ArrayList<>();
    }

    public void addListener(UI_ChangeListener0 ui_changeListener0) {
        changeListenerList.add(ui_changeListener0);
    }

    public void clearCurrentChangeListener() {
        currentChangeListener = null;
    }

    FieldState cloneThis() {
        FieldState fieldState = new FieldState<T>();
        fieldState.filterValue = this.filterValue;
        fieldState.value = this.value;
        fieldState.defaultValue = this.defaultValue;
        fieldState.origValue = this.origValue;
        fieldState.document = this.document;
        return fieldState;
    }

    void nodeSetDisable(boolean readOnly) {
        changeListenerList.forEach(ui_changeListener0 -> ui_changeListener0.node.setDisable(readOnly));
    }

    void removeListener() {
        changeListenerList.forEach(UI_ChangeListener0::removePropertyListener);
        changeListenerList = new ArrayList<>();
    }

    public void updateUI(boolean fullUpdate) {
        changeListenerList.forEach(ui_changeListener0 -> {
            if (!ui_changeListener0.equals(currentChangeListener)) {
                ui_changeListener0.refreshNode(fullUpdate);
            }
        });

        if (currentChangeListener != null) {
            currentChangeListener.refreshNode(fullUpdate);
        }
    }

    public void setCurrentChangeListener(UI_ChangeListener0 changeListener0) {
        currentChangeListener = changeListener0;
    }
}
