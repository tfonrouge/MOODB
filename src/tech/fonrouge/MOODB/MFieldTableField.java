package tech.fonrouge.MOODB;

import org.bson.Document;
import org.bson.types.ObjectId;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    /**
     * true if document has to be included in aggregated lookup field
     */
    private boolean lookupDocument = false;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.TABLE_FIELD;
    }

    @Override
    protected Object getTypedValue() {
        Object o = table.tableState.getFieldValue(this);
        if (o instanceof Document) {
            Document d = (Document) o;
            return d.get("_id");
        }
        return o;
    }

    public final T linkedTable() {
        if (getFieldState().linkedTable == null) {
            getFieldState().linkedTable = initializeTableField();
        }
        return (T) getFieldState().linkedTable;
    }

    private T initializeTableField() {
        T t = buildTableField();
        t.setLinkedField(this);
        return t;
    }

    protected abstract T buildTableField();

    @Override
    public ObjectId getEmptyValue() {
        return null;
    }

    @SuppressWarnings("unused")
    public boolean setValue(T table) {
        return super.setValue(table._id());
    }

    public final T syncedTable() {

        Object objectId = linkedTable()._id();

        if (objectId == null || !objectId.equals(table.tableState.getFieldValue(this))) {
            linkedTable().goTo(table.tableState.getFieldValue(this));
        }

        return linkedTable();
    }

    public boolean isLookupDocument() {
        return lookupDocument;
    }

    public void setLookupDocument(boolean lookupDocument) {
        this.lookupDocument = lookupDocument;
    }
}
