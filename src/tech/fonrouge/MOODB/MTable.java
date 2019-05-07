package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

abstract public class MTable {

    /**
     * field__id : field for table's primary key
     */
    public final MFieldObject field__id = new MFieldObject(this, "_id");

    ArrayList<MField> fieldList;
    ArrayList<MIndex> indices = new ArrayList<>();

    MEngine engine;
    TableState tableState;
    private int tableStateIndex = 0;
    private ArrayList<TableState> tableStateList = new ArrayList<>();
    private MTable masterSource = null;
    private MFieldTableField masterSourceField;

    public MTable() {
        initialize();
    }

    public MTable(MTable masterSource) {
        this.masterSource = masterSource;
        initialize();
    }

    protected void initialize() {
        indices = new ArrayList<>();

        engine = new MEngine(this);

        buildIndices();
    }

    /**
     * buildIndices
     */
    protected void buildIndices() {
        indices.forEach(MIndex::buildIndex);
    }

    @SuppressWarnings("unused")
    public void addLookupField(String fieldExpression) {

        String[] fields = fieldExpression.split("\\.");

        for (String field : fields) {
            MFieldTableField mFieldTableField = fieldTableFieldByName(field);
            if (mFieldTableField != null) {
                mFieldTableField.setLookupDocument(true);
            }
        }
    }

    public MFieldTableField fieldTableFieldByName(String name) {
        for (MField mField : fieldList) {
            if (mField.name.contentEquals(name) && mField instanceof MFieldTableField) {
                return (MFieldTableField) mField;
            }
        }
        return null;
    }

    public boolean aggregateFind() {
        return engine.aggregateFind();
    }

    /**
     * cancel
     */
    public void cancel() {

        if (tableState.state == STATE.NORMAL) {
            throw new RuntimeException("Attempt to Cancel on Table at Normal State.");
        }

        tableState.clearBindings();

        goTo(field__id.getTypedValue());

        tableState.state = STATE.NORMAL;
    }

    /**
     * goTo
     *
     * @param objectId : _id of rawDocument to go
     */
    @SuppressWarnings("WeakerAccess")
    public boolean goTo(Object objectId) {
        return engine.goTo(objectId);
    }

    /**
     * count
     */
    public long count() {
        return engine.count();
    }

    /**
     * delete
     */
    public boolean delete() {
        if (onBeforeDelete()) {
            if (engine.delete()) {
                onAfterDelete();
                return true;
            }
        }
        return false;
    }

    protected boolean onBeforeDelete() {
        return true;
    }

    protected void onAfterDelete() {

    }

    /**
     * edit
     *
     * @return boolean
     */
    public boolean edit() {

        if (tableState.state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (tableState.eof) {
            throw new RuntimeException("Attempt to EDIT table at EOF");
        }

        if (!onBeforeEdit()) {
            return false;
        }

        /* fill field values */
        fieldList.forEach(mField -> {
            mField.getFieldState().origValue = mField.getTypedValue();
        });

        tableState.state = STATE.EDIT;

        return true;
    }

    /**
     * onBeforeEdit
     *
     * @return true if edit is allowed
     */
    protected boolean onBeforeEdit() {
        return true;
    }

    /**
     * @param fieldName name of looked field
     * @return MField
     */
    public <T> MField<T> fieldByName(String fieldName) {
        for (MField mField : fieldList) {
            if (mField.name.contentEquals(fieldName)) {
                return (MField<T>) mField; /* TODO: fix this */
            }
        }
        return null;
    }

    public MTable getChildTable(Class clazz) {
        Method[] a = getClass().getDeclaredMethods();
        for (Method method : a) {
            System.out.println(a);
        }
        return null;
    }

    public abstract MBaseData getData();

    /**
     * getDatabase
     *
     * @return
     */
    protected MDatabase getDatabase() {
        return engine.getMDatabase();
    }

    public MEngine getEngine() {
        return engine;
    }

    /**
     * getEof
     *
     * @return
     */
    public boolean getEof() {
        return tableState.eof;
    }

    /**
     * getException
     *
     * @return exception object from last i/o operation
     */
    public Exception getException() {
        return tableState.exception;
    }

    /**
     * getFieldListStream
     *
     * @return stream of field list
     */
    public Stream<MField> getFieldListStream() {
        return fieldList.stream();
    }

    /**
     * getGenre
     *
     * @return singular of genre
     */
    public String getGenre() {
        return getTableName();
    }

    /**
     * getTableName
     *
     * @return String table name on collection
     */
    abstract public String getTableName();

    /**
     * getGenres
     *
     * @return plural of genre
     */
    public String getGenres() {
        return getTableName() + "'s";
    }

    /**
     * @return linkedField
     */
    @SuppressWarnings("unused")
    public MFieldTableField<? extends MTable> getLinkedField() {
        return tableState.linkedField;
    }

    <T extends MTable> void setLinkedField(MFieldTableField<T> linkedField) {
        tableState.linkedField = linkedField;
    }

    public MFieldTableField getMasterSourceField() {
        return masterSourceField;
    }

    /**
     * getState
     *
     * @return STATE of Table
     */
    public STATE getState() {
        return tableState.state;
    }

    public TableState getTableState() {
        return tableState;
    }

    public void setTableState(TableState tableState) {
        this.tableState = tableState;
    }

    /**
     * @return true if current cursor is valid and his hasNext() is true
     */
    @SuppressWarnings("unused")
    public boolean hasNext() {
        return tableState.mongoCursor != null && tableState.mongoCursor.hasNext();
    }

    /**
     * insert
     */
    public boolean insert() {

        if (tableState.state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (!onBeforeInsert()) {
            return false;
        }

        MTable mTable = getMasterSource();

        if (mTable != null && mTable.getState() == STATE.INSERT) {
            if (!(mTable.post() && mTable.edit())) {
                return false;
            }
        }

        fieldList.forEach(mField -> tableState.setFieldValue(mField.index, null));

        /* fill field values */
        fieldList.forEach(mField -> {
            if (!mField.calculated) {
                if (masterSource != null && mField.equals(masterSourceField)) {
                    tableState.setFieldValue(mField.index, masterSource._id());
                } else {
                    Object value = mField.getNewValue();
                    if (mField.fieldType == FIELD_TYPE.DATE && value == null && ((MFieldDate) mField).required) {
                        value = new Date();
                    }
                    tableState.setFieldValue(mField.index, value);
                    if (tableState.getFieldValue(mField) == null && (mField.required || mField.autoInc)) {
                        tableState.setFieldValue(mField.index, mField.getEmptyValue());
                    }
                }
            }
        });
        tableState.state = STATE.INSERT;

        return true;
    }

    /**
     * onBeforeInsert
     *
     * @return true if insert is allowed
     */
    protected boolean onBeforeInsert() {
        return true;
    }

    /**
     * getMasterSource
     *
     * @return masgter source table
     */
    public MTable getMasterSource() {
        return masterSource;
    }

    public boolean next() {
        return engine.next();
    }

    /**
     * getMDatabaseClass
     *
     * @return
     */
    abstract protected Class getMDatabaseClass();

    /**
     * @return boolean
     */
    public boolean post() {

        if (tableState.state == STATE.NORMAL) {
            throw new RuntimeException("Table Not in EDIT/INSERT State.");
        }

        tableState.exception = null;

        HashMap<String, String> invalidFieldList = getInvalidFieldList();
        if (invalidFieldList.size() > 0) {
            tableState.exception = new RuntimeException("Invalid field value: " + invalidFieldList.toString());
            return false;
        }

        /* checks for empty values on required fields */
        fieldList.forEach(mField -> {
            if (!mField.calculated && mField.required && mField.isEmpty() && !mField.autoInc) {
                tableState.exception = new Exception("Empty Value on Required Field: '" + mField.name + "'");
            }
        });

        if (tableState.exception != null) {
            return false;
        }

        Document document = new Document();

        /* build rawDocument and checks for validation on fields */

        List<MFieldTableField> refList = new ArrayList<>();

        if (onBeforePost()) {
            boolean result;
            switch (tableState.state) {
                case EDIT:
                    fieldList.forEach(mField -> {
                        if (!mField.calculated && !mField.getName().contentEquals("_id") && mField.valueChanged()) {
                            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                                refList.add((MFieldTableField) mField);
                            }
                            document.put(mField.getName(), mField.value());
                        }
                    });
                    result = document.size() == 0 || engine.update(document);
                    break;
                case INSERT:
                    fieldList.forEach(mField -> {
                        if (!mField.calculated) {
                            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                                refList.add((MFieldTableField) mField);
                            }
                            if (mField.autoInc) {
                                document.put(mField.name, mField.getNextSequence());
                            } else if (!(mField instanceof MFieldObjectId && mField.getName().contentEquals("_id")) && !(tableState.getFieldValue(mField) == null)) {
                                document.put(mField.getName(), mField.value());
                            }
                        }
                    });
                    result = engine.insert(document);
                    break;
                default:
                    result = false;
            }
            if (!result) {
                return false;
            }
        }

        if (refList.size() > 0) {
            updateReferentialIntegrity(refList);
        }

        tableState.eof = false;

        STATE prevState = tableState.state;

        tableState.state = STATE.NORMAL;

        onAfterPost();

        switch (prevState) {
            case EDIT:
                onAfterPostEdit();
                break;
            case INSERT:
                onAfterPostInsert();
                break;
        }

        tableState.clearBindings();

        return true;
    }

    /**
     * getInvalidFieldList
     *
     * @return hash of invalid fields info
     */
    public HashMap<String, String> getInvalidFieldList() {
        HashMap<String, String> result = new HashMap<>();
        fieldList.forEach(mField -> {
            String item = null;
            if (!mField.getValidStatus()) {
                item = mField.getInvalidCause();
            }
            if (item != null) {
                result.put(mField.name, item);
            }
        });
        return result;
    }

    /**
     * onBeforePost
     *
     * @return
     */
    protected boolean onBeforePost() {
        return true;
    }

    private void updateReferentialIntegrity(List<MFieldTableField> refList) {
        List<UpdateOneModel<Document>> updates = new ArrayList<>();
        refList.forEach(mFieldTableField -> {
            Document filter = new Document("detail", getTableName()).append("master", mFieldTableField.linkedTable().getTableName());
            Document update = new Document("$set", new Document("detailField", mFieldTableField.getName()));
            UpdateOptions options = new UpdateOptions().upsert(true);
            UpdateOneModel<Document> updateOneModel = new UpdateOneModel<>(filter, update, options);
            updates.add(updateOneModel);
        });
        engine.mDatabase.getReferentialIntegrityTable().bulkWrite(updates);
    }

    protected void onAfterPost() {
    }

    protected void onAfterPostEdit() {

    }

    protected void onAfterPostInsert() {

    }

    void set_id(Object value) {
        field__id.table.tableState.setFieldValue(field__id.index, value);
    }

    @SuppressWarnings("WeakerAccess")
    public void setFieldFilters() {

    }

    /**
     * setMasterSource
     *
     * @param masterSourceTable : the master source table
     * @param masterSourceField : the field on the master source table
     */
    protected void setMasterSource(MTable masterSourceTable, MFieldTableField masterSourceField) {
        masterSource = masterSourceTable;
        this.masterSourceField = masterSourceField;
    }

    /**
     * setTableDocument
     */
    boolean setTableDocument(MongoCursor<Document> mongoCursor) {

        tableState.mongoCursor = mongoCursor;

        Document rawDocument = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        tableState.eof = rawDocument == null;

        fieldList.forEach(mField -> {
            if (!mField.calculated) {
                Object value = rawDocument == null ? null : rawDocument.getOrDefault(mField.name, null);
                if (value == null && mField.required) {
                    value = mField.getEmptyValue();
                }
                if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                    tableState.fieldStateList.get(mField.index).document = null;
                    if (value instanceof Document) {
                        Document document = (Document) value;
                        tableState.fieldStateList.get(mField.index).document = document;
                        value = document.get("_id");
                    }
                }
                tableState.setFieldValue(mField.index, value);
            }
        });

        if (tableState.linkedField != null && tableState.linkedField.table.tableState.state != STATE.NORMAL) {
            Object value = tableState.linkedField.getTypedValue();
            if ((value == null && _id() != null) || _id() != null && !_id().equals(value)) {
                tableState.linkedField.setValue(_id());
            }
        }

        return !tableState.eof;
    }

    /**
     * @return OBJECT_ID for current rawDocument in table
     */
    public Object _id() {
        return field__id.getTypedValue();
    }

    @SuppressWarnings("unused")
    public final synchronized void tableStatePull() {
        if (tableStateIndex == 0) {
            throw new RuntimeException("tableStateIndex out of bounds.");
        }
        tableState = tableStateList.get(--tableStateIndex);
    }

    @SuppressWarnings("unused")
    public final synchronized void tableStatePush() {
        TableState currentTableState = tableState;
        if (tableStateIndex == 0) {
            tableStateList.add(currentTableState);
        }
        ++tableStateIndex;
        tableState = new TableState();
        for (FieldState fieldState : currentTableState.fieldStateList) {
            tableState.fieldStateList.add(fieldState.cloneThis());
        }
        tableStateList.add(tableState);
    }

    public enum FIELD_TYPE {
        BINARY,
        BOOLEAN,
        DATE,
        DOUBLE,
        INTEGER,
        LONG,
        OBJECT,
        OBJECT_ID,
        STRING,
        TABLE_FIELD
    }

    public enum STATE {
        NORMAL,
        EDIT,
        INSERT
    }
}
