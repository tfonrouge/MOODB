package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TableState {

    MTable masterSource = null;
    MFieldTableField masterSourceField;
    MTable.STATE state = MTable.STATE.NORMAL;
    MongoCursor<Document> mongoCursor;
    boolean eof = true;
    Exception exception;
    MFieldTableField<? extends MTable> linkedField;

    List<MField.FieldState> fieldStateList;

    TableState() {
        fieldStateList = new ArrayList<>();
    }

    /**
     * @param tableState : copy constructor
     */
    TableState(TableState tableState) {
        masterSource = tableState.masterSource;
        masterSourceField = tableState.masterSourceField;
        state = tableState.state;
        mongoCursor = tableState.mongoCursor;
        eof = tableState.eof;
        exception = tableState.exception;
        linkedField = tableState.linkedField;
        fieldStateList = new ArrayList<>();
        tableState.fieldStateList.forEach(fieldState -> fieldStateList.add(fieldState.cloneThis()));
    }

    void clearBindings() {
        for (MField.FieldState fieldState : fieldStateList) {
            if (fieldState.ui_changeListener != null) {
                fieldState.ui_changeListener = null;
            }
            if (fieldState.node != null) {
                fieldState.node = null;
            }
        }
    }

    public MField.FieldState getFieldStateList(int index) {
        return fieldStateList.get(index);
    }

    public Object getFieldValue(MField mField) {
        return getFieldValue(mField, Object.class);
    }

    public <T> T getFieldValue(MField mField, Class<T> clazz) {
        Object o;
        if (mField.isCalculated()) {
            TableState tableState = mField.table.tableState;
            mField.table.tableState = this;
            o = mField.table.fieldList.get(mField.index).value();
            mField.table.tableState = tableState;
        } else {
            o = fieldStateList.get(mField.index).value;
        }
        T result = null;
        if (o != null) {
            if (clazz.isAssignableFrom(o.getClass())) {
                result = clazz.cast(o);
            }
        }
        return result;
    }

    <T> boolean setFieldValue(int index, T value) {
        MField.FieldState fieldState = fieldStateList.get(index);
        Object oldValue = fieldState.value;
        if ((value != null && !value.equals(oldValue)) || (oldValue != null && !oldValue.equals(value))) {
            fieldState.value = value;
            if (fieldState.ui_changeListener != null) {
                System.out.println("::: + " + fieldState.ui_changeListener);
                fieldState.ui_changeListener.update(value);
            }
            return true;
        }
        return false;
    }

    void set_UI_state(MField mField) {
        MField.FieldState fieldState = fieldStateList.get(mField.index);
        if (fieldState.node != null) {
            fieldState.node.setDisable(mField.isReadOnly());
        }
    }

    public Document getFieldStateDocument(int index) {
        return fieldStateList.get(index).document;
    }
}
