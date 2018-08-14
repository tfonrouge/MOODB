package tech.fonrouge.MOODB;

public abstract class MFieldTable<T> extends MFieldObjectId {

    private MTable mDataObj;

    protected MFieldTable(MTable owner, String name) {
        super(owner, name);
    }

    protected abstract MTable buildTable();

    public final T data() {

        if (mDataObj == null) {
            mDataObj = buildTable();
        }

        if (mDataObj.objectId() == null || mValue == null || (mDataObj.objectId().compareTo(value())) != 0) {
            mDataObj.goTo(mValue);
        }

        return (T) mDataObj;
    }

    public void setValue(MTable mTable) {
        super.setValue(mTable.objectId());
    }
}
