package tech.fonrouge.MOODB;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    /**
     * true if document has to be included in aggregated lookup field
     */
    private boolean lookupDocument = true;

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
        return (T) getFieldState().linkedTable; // TODO: solve this unchecked cast
    }

    private MFieldTableField<?> findMasterSourceField(Class<?> clazzParam, MTable table) {
        MFieldTableField<?> mFieldTableField = null;

        for (MField mField : table.fieldList) {
            if (mFieldTableField == null &&
                    mField.fieldType == MTable.FIELD_TYPE.TABLE_FIELD &&
                    clazzParam.isAssignableFrom(((MFieldTableField) mField).getTableClass())) {
                mFieldTableField = (MFieldTableField) mField;
            }
        }

        return mFieldTableField;
    }

    private T initializeTableField() {
        T t = null;
        Class<?> clazz = getTableClass();
        try {
            Constructor<?> ctor = clazz.getDeclaredConstructors()[0];
            if (ctor.getParameterCount() == 0) {
                t = (T) ctor.newInstance();
            } else {
                Class<?> clazzParam = ctor.getParameterTypes()[0];
                MFieldTableField mFieldTableField = null;
                if (table.getMasterSource() != null) {
                    mFieldTableField = findMasterSourceField(clazzParam, table.getMasterSource());
                }
                if (mFieldTableField == null) {
                    mFieldTableField = findMasterSourceField(clazzParam, table);
                }
                if (mFieldTableField != null) {
                    Object a = null;
                    ctor.newInstance(a);
                    t = (T) ctor.newInstance(mFieldTableField.linkedTable());
                }
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (t == null) {
            throw new RuntimeException("Error creating table(" + table + ") for table field(" + name + ") ");
        } else {
            t.setLinkedField(this);
        }
        return t;
    }

    protected abstract Class<T> getTableClass();

    @Override
    public ObjectId getEmptyValue() {
        return null;
    }

    public boolean isLookupDocument() {
        return lookupDocument;
    }

    public void setLookupDocument(boolean lookupDocument) {
        this.lookupDocument = lookupDocument;
    }

    @SuppressWarnings("unused")
    public boolean setValue(T table) {
        return super.setValue(table._id());
    }

    public final T syncedTable() {

        T linkedTable = linkedTable();

        Object objectId = linkedTable._id();

        if (objectId == null || !objectId.equals(table.tableState.getFieldValue(this))) {
            Document fieldTableDocument = table.tableState.fieldStateList.get(index).document;
            if (fieldTableDocument != null) {
                linkedTable.setDocumentToTableState(fieldTableDocument);
            } else {
                linkedTable.goTo(table.tableState.getFieldValue(this));
            }
        }

        return linkedTable();
    }
}
