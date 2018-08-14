package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldTableField<T> extends MField<ObjectId> {

    private T mDataObj;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    protected abstract T buildTable();

    public final T dataField() {

        if (mDataObj == null) {
            mDataObj = buildTable();
        }

        ObjectId objectId = ((MTable) mDataObj).objectId();

        if (objectId == null || mValue == null || (objectId.compareTo(mValue)) != 0) {
            ((MTable) mDataObj).goTo(mValue);
        }

        return mDataObj;
    }

    @Override
    public ObjectId emptyValue() {
        return null;
    }

    public boolean setTable(T value) {
        return super.setValue(((MTable) value).objectId());
    }
}
