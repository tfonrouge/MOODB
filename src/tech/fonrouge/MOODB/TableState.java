package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TableState {

    MTable.STATE state = MTable.STATE.NORMAL;
    MongoCursor<Document> mongoCursor;
    boolean eof = true;
    Exception exception;
    MFieldTableField<? extends MTable> linkedField;

    List<FieldState> fieldStateList;

    TableState() {
        fieldStateList = new ArrayList<>();
    }

    /**
     * @param tableState : copy constructor
     */
    TableState(TableState tableState) {
        state = tableState.state;
        mongoCursor = tableState.mongoCursor;
        eof = tableState.eof;
        exception = tableState.exception;
        linkedField = tableState.linkedField;
        fieldStateList = new ArrayList<>();
        tableState.fieldStateList.forEach(fieldState -> fieldStateList.add(fieldState.cloneThis()));
    }

    void clearBindings() {
        for (FieldState fieldState : fieldStateList) {
            fieldState.removeListener();
        }
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
        FieldState fieldState = fieldStateList.get(index);
        Object oldValue = fieldState.value;
        if ((value != null && !value.equals(oldValue)) || (oldValue != null && !oldValue.equals(value))) {
            fieldState.value = value;
            return true;
        }
        return false;
    }

    void set_UI_state(MField mField) {
        FieldState fieldState = fieldStateList.get(mField.index);
        fieldState.nodeSetDisable(mField.isReadOnly());
    }

    public Document getFieldStateDocument(int index) {
        return fieldStateList.get(index).document;
    }
}
