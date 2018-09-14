package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

abstract public class MTable {

    /**
     * field__id : field for table's primary key
     */
    public final MFieldObject field__id = new MFieldObject(this, "_id");

    HashMap<String, MField> fieldList;
    ArrayList<MIndex> indices = new ArrayList<>();
    MEngine engine;
    private MDatabase database;

    private int tableStateIndex = -1;
    private ArrayList<TableState> tableStateList = new ArrayList<>();

    /* ******************* */
    /* constructor methods */
    /* ******************* */
    public MTable() {
        initialize();
    }

    public MTable(MTable masterSource) {
        this.getTableState().masterSource = masterSource;
        initialize();
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

    private void initialize() {
        indices = new ArrayList<>();

        database = newDatabase();
        engine = new MEngine(this);

        buildIndices();
    }

    /**
     * _id
     *
     * @return OBJECT_ID for current document in table
     */
    public Object _id() {
        return field__id.getFieldState().value;
    }

    void set_id(Object value) {
        field__id.getFieldState().value = value;
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

    TableState getTableState() {
        if (tableStateIndex == -1) {
            tableStatePush();
        }
        return tableStateList.get(tableStateIndex);
    }

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
        getTableState().masterSource = masterSourceTable;
        getTableState().masterSourceField = masterSourceField;
    }

    /**
     * setTableDocument
     */
    boolean setTableDocument(MongoCursor<Document> mongoCursor) {

        getTableState().mongoCursor = mongoCursor;

        final Document document = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        getTableState().eof = document == null;

        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated) {
                mField.getFieldState().value = document == null ? null : document.getOrDefault(mField.name, null);
                if (mField.getFieldState().value == null && mField.notNullable) {
                    mField.getFieldState().value = mField.getEmptyValue();
                }
            }
        });

        if (getTableState().linkedField != null && getTableState().linkedField.table.getTableState().state != STATE.NORMAL) {
            getTableState().linkedField.setValue(_id());
        }

        return !getTableState().eof;
    }

    /**
     * cancel
     */
    public void cancel() {

        if (getTableState().state == STATE.NORMAL) {
            throw new RuntimeException("Attempt to Cancel on Table at Normal State.");
        }

        goTo(field__id.getFieldState().value);

        getTableState().state = STATE.NORMAL;
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

        if (getTableState().state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (getTableState().eof) {
            throw new RuntimeException("Attempt to EDIT table at EOF");
        }

        if (!onBeforeEdit()) {
            return false;
        }

        /* fill field values */
        fieldList.forEach((fieldName, mField) -> {
            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                ((MFieldTableField) mField).notSynced = true;
            }
            mField.getFieldState().origValue = mField.getFieldState().value;
        });

        getTableState().state = STATE.EDIT;

        return true;
    }

    public boolean find() {
        return engine.find();
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * getEof
     *
     * @return
     */
    public boolean getEof() {
        return getTableState().eof;
    }

    /**
     * getException
     *
     * @return exception object from last i/o operation
     */
    public Exception getException() {
        return getTableState().exception;
    }

    /**
     * getFieldList
     *
     * @return array of field list
     */
    public HashMap<String, MField> getFieldList() {
        return fieldList;
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
        fieldList.forEach((fieldName, mField) -> {
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

    public MFieldTableField<? extends MTable> getLinkedField() {
        return getTableState().linkedField;
    }

    <T extends MTable> void setLinkedField(MFieldTableField<T> linkedField) {
        getTableState().linkedField = linkedField;
    }

    /**
     * getState
     *
     * @return STATE of Table
     */
    public STATE getState() {
        return getTableState().state;
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
     * @param objectId : _id of document to go
     */
    public boolean goTo(Object objectId) {
        return engine.goTo(objectId);
    }

    public boolean hasNext() {
        return getTableState().mongoCursor != null && getTableState().mongoCursor.hasNext();
    }

    /**
     * insert
     */
    public boolean insert() {

        if (getTableState().state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (!onBeforeInsert()) {
            return false;
        }

        fieldList.forEach((s, mField) -> {
            if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                ((MFieldTableField) mField).notSynced = true;
            }
            mField.getFieldState().value = null;
        });

        /* fill field values */
        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated) {
                if (getTableState().masterSource != null && mField.equals(getTableState().masterSourceField)) {
                    mField.getFieldState().value = getTableState().masterSource._id();
                } else {
                    if (mField.fieldType == FIELD_TYPE.DATE && ((MFieldDate) mField).mNewDate) {
                        mField.getFieldState().value = new Date();
                    } else {
                        mField.getFieldState().value = mField.getNewValue();
                    }
                    if (mField.getFieldState().value == null && mField.notNullable) {
                        mField.getFieldState().value = mField.getEmptyValue();
                    }
                }
            }
        });
        getTableState().state = STATE.INSERT;

        return true;
    }

    public boolean next() {
        return engine.next();
    }

    /**
     * post
     *
     * @return boolean
     */
    public boolean post() {

        if (getTableState().state == STATE.NORMAL) {
            throw new RuntimeException("Table Not in EDIT/INSERT State.");
        }

        getTableState().exception = null;

        HashMap<String, String> invalidFieldList = getInvalidFieldList();
        if (invalidFieldList.size() > 0) {
            return false;
        }

        /* checks for empty values on required fields */
        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated && mField.required && mField.isEmpty()) {
                getTableState().exception = new Exception("Empty Value on Required Field: '" + mField.name + "'");
            }
        });

        if (getTableState().exception != null) {
            return false;
        }

        Document document = new Document();

        /* build document and checks for validation on fields */

        if (onBeforePost()) {
            boolean result;
            switch (getTableState().state) {
                case EDIT:
                    fieldList.forEach((fieldName, mField) -> {
                        if (!mField.calculated && !fieldName.contentEquals("_id") && mField.valueChanged()) {
                            document.put(fieldName, mField.value());
                        }
                    });
                    result = engine.update(document);
                    break;
                case INSERT:
                    fieldList.forEach((fieldName, mField) -> {
                        if (!mField.calculated) {
                            if (!(mField instanceof MFieldObjectId && fieldName.contentEquals("_id")) && !(mField.getFieldState().value == null)) {
                                document.put(fieldName, mField.value());
                            }
                        }
                    });
                    result = engine.insert(document);
                    break;
                default:
                    result = false;
            }
            if (!result) {
                getTableState().exception = engine.exception;
                return false;
            }
        }

        getTableState().eof = false;

        getTableState().state = STATE.NORMAL;

        return true;
    }

    public final void tableStatePush() {
        tableStateList.add(new TableState());
        fieldList.forEach((s, mField) -> mField.fieldStatePush());
        ++tableStateIndex;
    }

    public final void tableStatePull() {
        if (tableStateIndex == 0) {
            throw new RuntimeException("tableStateIndex out of bounds.");
        }
        fieldList.forEach((s, mField) -> mField.fieldStatePull());
        --tableStateIndex;
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

    class TableState {
        MTable masterSource = null;
        MFieldTableField masterSourceField;
        STATE state = STATE.NORMAL;
        MongoCursor<Document> mongoCursor;
        boolean eof = true;
        Exception exception;
        MFieldTableField<? extends MTable> linkedField;

    }
}
