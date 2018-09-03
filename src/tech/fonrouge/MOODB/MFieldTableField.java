package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    private T mDataObj;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    protected abstract T buildTable();

    public final T dataField() {

        if (mDataObj == null) {
            mDataObj = buildTable();
        }

        Object objectId = mDataObj._id();

        if (objectId == null || mValue == null || (objectId.equals(mValue))) {
            mDataObj.goTo(mValue);
        }

        return mDataObj;
    }

    @Override
    public ObjectId emptyValue() {
        return null;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.TABLE_FIELD;
    }

    public boolean setValue(T table) {
        return super.setValue(table._id());
    }
}
