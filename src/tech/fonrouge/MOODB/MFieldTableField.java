package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    private final T linkedTable = initializeTableField();
    boolean notSynced = false;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.TABLE_FIELD;
    }

    public final T getLinkedTable() {
        return linkedTable;
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

    public final T syncLinkedTable() {

        Object objectId = linkedTable._id();

        if (objectId == null || !objectId.equals(getFieldState().value)) {
            linkedTable.goTo(getFieldState().value);
        }

        return linkedTable;
    }
}
