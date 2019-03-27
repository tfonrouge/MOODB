package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;

public class TableState implements Cloneable {

    MTable masterSource = null;
    MFieldTableField masterSourceField;
    MTable.STATE state = MTable.STATE.NORMAL;
    MongoCursor<Document> mongoCursor;
    boolean eof = true;
    Exception exception;
    MFieldTableField<? extends MTable> linkedField;
    ArrayList<Object> fieldValueList = new ArrayList<>();
    ArrayList<String[]> lookupFieldList;
    Document lookupDocument = new Document();

    public Document getLookupDocument() {
        return lookupDocument;
    }

    <T> void setFieldValue(int index, T value) {
        if (fieldValueList != null) {
            fieldValueList.set(index, value);
        }
    }

    public Object getFieldValue(int index) {
        if (fieldValueList != null) {
            return fieldValueList.get(index);
        }
        return null;
    }

    public <T> T getFieldValue(MField mField, Class<T> clazz) {
        if (fieldValueList != null) {
            Object o;
            if (mField.calculated) {
                TableState tableState = mField.table.tableState;
                mField.table.tableState = this;
                o = mField.table.fieldList.get(mField.index).value();
                mField.table.tableState = tableState;
            } else {
                o = fieldValueList.get(mField.index);
            }
            return clazz.cast(o);
        }
        return null;
    }

    @Override
    protected TableState clone() throws CloneNotSupportedException {
        TableState cloned = (TableState) super.clone();
        if (fieldValueList != null) {
            cloned.fieldValueList = new ArrayList<>(fieldValueList);
        }
        if (lookupFieldList != null) {
            cloned.lookupFieldList = new ArrayList<>(lookupFieldList);
        }
        return cloned;
    }
}
