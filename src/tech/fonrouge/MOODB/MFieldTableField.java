package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    private final T linkedTable = initializeTableField();
    private MFieldTableField<T> linkedField;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    public T getLinkedTable() {
        return linkedTable;
    }

    private T initializeTableField() {
        T t = buildTableField();
        t.setLinkedField(this);
        return t;
    }

    protected abstract T buildTableField();

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.TABLE_FIELD;
    }

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

        if (objectId == null || !objectId.equals(value)) {
            linkedTable.goTo(value);
        }

        return linkedTable;
    }
}
