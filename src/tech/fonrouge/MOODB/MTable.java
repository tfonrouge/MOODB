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
    MTable masterSource = null;
    MFieldTableField masterSourceField;
    ArrayList<MIndex> indices = new ArrayList<>();
    STATE state = STATE.NORMAL;
    MEngine engine;
    private boolean eof = true;
    private Exception exception;
    private MDatabase database;
    private MFieldTableField linkedField;

    /* *************** */
    /* private methods */
    /* *************** */

    /* ******************* */
    /* constructor methods */
    /* ******************* */
    public MTable() {
        initialize();
    }

    public MTable(MTable masterSource) {
        this.masterSource = masterSource;
        initialize();
    }

    /**
     * buildIndices
     */
    protected void buildIndices() {
        indices.forEach(MIndex::buildIndex);
    }

    private void initialize() {
        indices = new ArrayList<>();

        database = newDatabase();
        engine = new MEngine(this);

        buildIndices();
    }

    /* *********************** */
    /* package private methods */
    /* *********************** */

    /**
     * _id
     *
     * @return OBJECT_ID for current document in table
     */
    public Object _id() {
        return field__id.value;
    }

    void set_id(Object value) {
        field__id.value = value;
    }

    /**
     * getDatabase
     *
     * @return
     */
    protected MDatabase getDatabase() {
        return database;
    }

    /* ***************** */
    /* protected methods */
    /* ***************** */

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
        masterSource = masterSourceTable;
        this.masterSourceField = masterSourceField;
    }

    /**
     * setTableDocument
     */
    protected boolean setTableDocument(MongoCursor<Document> mongoCursor) {

        engine.mongoCursor = mongoCursor;

        final Document document = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        eof = document == null;

        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated) {
                mField.value = document == null ? null : document.getOrDefault(mField.name, null);
            }
        });

        return !eof;
    }

    /**
     * cancel
     */
    public void cancel() {

        if (state == STATE.NORMAL) {
            throw new RuntimeException("Attempt to Cancel on Table at Normal State.");
        }

        goTo(field__id.value);

        state = STATE.NORMAL;
    }

    /**
     * count
     */
    public long count() {
        return engine.count();
    }

    /* ************** */
    /* public methods */
    /* ************** */

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

        if (state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (eof) {
            throw new RuntimeException("Attempt to EDIT table at EOF");
        }

        if (!onBeforeEdit()) {
            return false;
        }

        /* fill field values */
        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated) {
                mField.origValue = mField.value;
            }
        });

        state = STATE.EDIT;

        return true;
    }

    public boolean find() {
        return engine.find();
    }

    /**
     * getEof
     *
     * @return
     */
    public boolean getEof() {
        return eof;
    }

    /**
     * getException
     *
     * @return exception object from last i/o operation
     */
    public Exception getException() {
        return exception;
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

    public MFieldTableField getLinkedField() {
        return linkedField;
    }

    void setLinkedField(MFieldTableField linkedField) {
        this.linkedField = linkedField;
    }

    /**
     * getState
     *
     * @return STATE of Table
     */
    public STATE getState() {
        return state;
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
        return engine.mongoCursor != null && engine.mongoCursor.hasNext();
    }

    /**
     * insert
     */
    public boolean insert() {

        if (state != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        fieldList.forEach((s, mField) -> mField.value = null);

        if (!onBeforeInsert()) {
            return false;
        }

        /* fill field values */
        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated) {
                if (masterSource != null && mField.equals(masterSourceField)) {
                    mField.value = masterSource._id();
                } else {
                    if (mField.fieldType() == FIELD_TYPE.DATE && ((MFieldDate) mField).mNewDate) {
                        mField.value = new Date();
                    } else {
                        mField.value = mField.getNewValue();
                    }
                    if (mField.value == null && mField.notNull) {
                        mField.value = mField.getEmptyValue();
                    }
                }
            }
        });
        state = STATE.INSERT;

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

        if (state == STATE.NORMAL) {
            throw new RuntimeException("Table Not in EDIT/INSERT State.");
        }

        exception = null;

        HashMap<String, String> invalidFieldList = getInvalidFieldList();
        if (invalidFieldList.size() > 0) {
            return false;
        }

        /* checks for empty values on required fields */
        fieldList.forEach((fieldName, mField) -> {
            if (!mField.calculated && mField.required && mField.isEmpty()) {
                exception = new Exception("Empty Value on Required Field: '" + mField.name + "'");
            }
        });

        if (exception != null) {
            return false;
        }

        Document document = new Document();

        /* build document and checks for validation on fields */

        if (onBeforePost()) {
            boolean result;
            switch (state) {
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
                            if (!(mField instanceof MFieldObjectId && fieldName.contentEquals("_id")) && !(mField.value == null)) {
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
                exception = engine.exception;
                return false;
            }
        }

        eof = false;

        state = STATE.NORMAL;

        return true;
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
