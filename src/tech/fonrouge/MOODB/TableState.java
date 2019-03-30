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
    Document lookupDocument;

    TableState() {
        fieldStateList = new ArrayList<>();
        lookupDocument = new Document();
    }

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
        lookupDocument = new Document(tableState.lookupDocument);
    }

    public Document getLookupDocument() {
        return lookupDocument;
    }

    <T> void setFieldValue(int index, T value) {
        fieldStateList.get(index).value = value;
    }

    public Object getFieldValue(int index) {
        return fieldStateList.get(index).value;
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
        return clazz.cast(o);
    }
}
