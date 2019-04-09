package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    Document filter;
    private MDatabase database;
    private int tableStateIndex = 0;
    private ArrayList<TableState> tableStateList = new ArrayList<>();

    /* ******************* */
    /* constructor methods */
    /* ******************* */
    public MTable() {
        initialize();
    }

    public MTable(MTable masterSource) {
        this.tableState.masterSource = masterSource;
        initialize();
    }

    public TableState getTableState() {
        return tableState;
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

    /**
     * buildIndices
     */
    protected void buildIndices() {
        indices.forEach(MIndex::buildIndex);
    }

    /* *************** */
    /* private methods */
    /* *************** */

    protected void initialize() {
        indices = new ArrayList<>();

        database = newDatabase();
        engine = new MEngine(this);

        buildIndices();
    }

    @SuppressWarnings("WeakerAccess")
    public void setFieldFilters() {

    }

    void set_id(Object value) {
        field__id.table.tableState.setFieldValue(field__id.index, value);
    }

    public MField fieldByName(String fieldName) {
        for (MField mField : fieldList) {
            if (mField.name.contentEquals(fieldName)) {
                return mField;
            }
        }
        return null;
    }

    public MFieldTableField fieldTableFieldByName(String name) {
        for (MField mField : fieldList) {
            if (mField.name.contentEquals(name) && mField instanceof MFieldTableField) {
                return (MFieldTableField) mField;
            }
        }
        return null;
    }

    /**
     * getDatabase
     *
     * @return
     */
    protected MDatabase getDatabase() {
        return database;
    }

    /* *********************** */
    /* package private methods */
    /* *********************** */

    /**
     * newDatabase
     *
     * @return
     */
    abstract protected MDatabase newDatabase();

    /**
     * onBeforeEdit
     *
     * @return true if edit is allowed
     */
    protected boolean onBeforeEdit() {
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

    /* ***************** */
    /* protected methods */
    /* ***************** */

    /**
     * onBeforePost
     *
     * @return
     */
    protected boolean onBeforePost() {
        return true;
    }

    /**
     * setMasterSource
     *
     * @param masterSourceTable : the master source table
     * @param masterSourceField : the field on the master source table
     */
    protected void setMasterSource(MTable masterSourceTable, MFieldTableField masterSourceField) {
        tableState.masterSource = masterSourceTable;
        tableState.masterSourceField = masterSourceField;
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
                if (value instanceof Document && mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                    Document document = (Document) value;
                    tableState.fieldStateList.get(mField.index).document = document;
                    value = document.get("_id");
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
     * count
     */
    public long count() {
        return engine.count();
    }

    /**
     * delete
     */
    public boolean delete() {
        return engine.delete();
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
            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                ((MFieldTableField) mField).notSynced = true;
            }
            mField.getFieldState().origValue = mField.getTypedValue();
        });

        tableState.state = STATE.EDIT;

        return true;
    }

    public boolean find() {
        return engine.find();
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * @return OBJECT_ID for current rawDocument in table
     */
    public Object _id() {
        return field__id.getTypedValue();
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
     * getGenres
     *
     * @return plural of genre
     */
    public String getGenres() {
        return getTableName() + "'s";
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
     * @return linkedField
     */
    public MFieldTableField<? extends MTable> getLinkedField() {
        return tableState.linkedField;
    }

    <T extends MTable> void setLinkedField(MFieldTableField<T> linkedField) {
        tableState.linkedField = linkedField;
    }

    /**
     * getMasterSource
     *
     * @return masgter source table
     */
    public MTable getMasterSource() {
        return tableState.masterSource;
    }

    /**
     * getState
     *
     * @return STATE of Table
     */
    public STATE getState() {
        return tableState.state;
    }

    /**
     * getTableName
     *
     * @return String table name on collection
     */
    abstract public String getTableName();

    /**
     * goTo
     *
     * @param objectId : _id of rawDocument to go
     */
    public boolean goTo(Object objectId) {
        return engine.goTo(objectId);
    }

    /**
     * @return true if current cursor is valid and his hasNext() is true
     */
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

        fieldList.forEach(mField -> {
            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                ((MFieldTableField) mField).notSynced = true;
            }
            tableState.setFieldValue(mField.index, null);
        });

        /* fill field values */
        fieldList.forEach(mField -> {
            if (!mField.calculated) {
                if (tableState.masterSource != null && mField.equals(tableState.masterSourceField)) {
                    tableState.setFieldValue(mField.index, tableState.masterSource._id());
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

    public boolean next() {
        return engine.next();
    }

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

        if (onBeforePost()) {
            boolean result;
            switch (tableState.state) {
                case EDIT:
                    fieldList.forEach(mField -> {
                        if (!mField.calculated && !mField.getName().contentEquals("_id") && mField.valueChanged()) {
                            document.put(mField.getName(), mField.value());
                        }
                    });
                    result = document.size() == 0 || engine.update(document);
                    break;
                case INSERT:
                    fieldList.forEach(mField -> {
                        if (!mField.calculated) {
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
                tableState.exception = engine.exception;
                return false;
            }
        }

        tableState.eof = false;

        tableState.state = STATE.NORMAL;

        tableState.clearBindings();

        return true;
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
        tableState.fieldStateList = new ArrayList<>(currentTableState.fieldStateList);
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
