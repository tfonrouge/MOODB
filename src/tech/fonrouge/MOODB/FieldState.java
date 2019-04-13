package tech.fonrouge.MOODB;

import org.bson.Document;
import tech.fonrouge.MOODB.ui.UI_ChangeListener0;

public class FieldState<T> {
    public UI_ChangeListener0 ui_changeListener;
    T filterValue;
    T value;
    T defaultValue;
    T origValue;
    MTable linkedTable;
    Document document;

    FieldState() {

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
}
