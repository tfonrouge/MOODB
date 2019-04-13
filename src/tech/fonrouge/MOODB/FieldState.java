package tech.fonrouge.MOODB;

import org.bson.Document;
import tech.fonrouge.MOODB.ui.UI_ChangeListener0;

public class FieldState<T, M extends MTable> {
    public UI_ChangeListener0 ui_changeListener;
    T filterValue;
    T value;
    T defaultValue;
    T origValue;
    Document document;
    private M linkedTable;

    FieldState() {

    }

    FieldState cloneThis() {
        FieldState fieldState = new FieldState<T, M>();
        fieldState.filterValue = this.filterValue;
        fieldState.value = this.value;
        fieldState.defaultValue = this.defaultValue;
        fieldState.origValue = this.origValue;
        fieldState.document = this.document;
        return fieldState;
    }

    public M getLinkedTable() {
        return linkedTable;
    }

    public void setLinkedTable(M linkedTable) {
        this.linkedTable = linkedTable;
    }
}
