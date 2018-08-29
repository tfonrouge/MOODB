package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;

abstract public class MTable {

    /**
     * field__id : field for table's primary key
     */
    public final MFieldObject field__id = new MFieldObject(this, "_id");

    HashMap<String, MField> mFieldList;
    MTable mMasterSource = null;
    MFieldTableField mMasterSourceField;
    ArrayList<MIndex> mIndices = new ArrayList<>();
    STATE mState = STATE.NORMAL;
    MEngine mEngine;
    private boolean mEof = true;
    private Exception mException;
    private MDatabase mDatabase;

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
        mMasterSource = masterSource;
        initialize();
    }

    /**
     * buildIndices
     */
    protected void buildIndices() {
        mIndices.forEach(MIndex::buildIndex);
    }

    /* *********************** */
    /* package private methods */
    /* *********************** */

    private void initialize() {
        mIndices = new ArrayList<>();

        initializeModel();

        mDatabase = newDatabase();
        mEngine = new MEngine(this);

        buildIndices();
    }

    /**
     * _id
     *
     * @return ObjectId for current document in table
     */
    public Object _id() {
        return field__id.mValue;
    }


    /* ***************** */
    /* protected methods */
    /* ***************** */

    void set_id(Object value) {
        field__id.mValue = value;
    }

    /**
     * database
     *
     * @return
     */
    protected MDatabase database() {
        return mDatabase;
    }

    /**
     * initializeModel
     */
    abstract protected void initializeModel();

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
        mMasterSource = masterSourceTable;
        mMasterSourceField = masterSourceField;
    }

    /**
     * setTableDocument
     */
    protected boolean setTableDocument(MongoCursor<Document> mongoCursor) {

        final Document document = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        mEof = document == null;

        mFieldList.forEach((fieldName, mField) -> {
            if (!mField.mCalculated) {
                mField.mValue = document == null ? null : document.getOrDefault(mField.mName, null);
            }
        });

        return !mEof;
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * cancel
     */
    public void cancel() {

        if (mState == STATE.NORMAL) {
            throw new RuntimeException("Attempt to Cancel on Table at Normal State.");
        }

        goTo(field__id.mValue);

        mState = STATE.NORMAL;
    }

    /**
     * count
     */
    public long count() {
        return mEngine.count();
    }

    /**
     * edit
     *
     * @return boolean
     */
    public boolean edit() {

        if (mState != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (mEof) {
            throw new RuntimeException("Attempt to EDIT table at EOF");
        }

        if (!onBeforeEdit()) {
            return false;
        }

        /* fill field values */
        mFieldList.forEach((fieldName, mField) -> {
            if (!mField.mCalculated) {
                mField.mOrigValue = mField.mValue;
            }
        });

        mState = STATE.EDIT;

        return true;
    }

    /**
     * eof
     *
     * @return
     */
    public boolean eof() {
        return mEof;
    }

    /**
     * fieldList
     *
     * @return array of field list
     */
    public HashMap<String, MField> fieldList() {
        return mFieldList;
    }

    public boolean find() {
        return mEngine.find();
    }

    /**
     * exception
     *
     * @return exception object from last i/o operation
     */
    public Exception exception() {
        return mException;
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
        return mEngine.goTo(objectId);
    }

    /**
     * insert
     */
    public boolean insert() {

        if (mState != STATE.NORMAL) {
            throw new RuntimeException("Table previously on EDIT/INSERT State.");
        }

        if (!onBeforeInsert()) {
            return false;
        }

        /* fill field values */
        mFieldList.forEach((fieldName, mField) -> {
            if (!mField.mCalculated) {
                mField.mValue = mField.getNewValue();
                if (mMasterSource != null && mField.equals(mMasterSourceField)) {
                    mField.mValue = mMasterSource._id();
                }
            }
        });
        mState = STATE.INSERT;

        return true;
    }

    public boolean next() {
        return mEngine.next();
    }

    /**
     * post
     *
     * @return boolean
     */
    public boolean post() {

        if (mState == STATE.NORMAL) {
            throw new RuntimeException("Table Not in EDIT/INSERT State.");
        }

        mException = null;

        /* checks for empty values on required fields */
        mFieldList.forEach((fieldName, mField) -> {
            if (!mField.mCalculated && mField.mRequired && mField.isEmpty()) {
                mException = new Exception("Empty Value on Required Field: '" + mField.mName + "'");
            }
        });

        if (mException != null) {
            return false;
        }

        Document document = new Document();

        /* build document and checks for validation on fields */

        if (onBeforePost()) {
            boolean result;
            switch (mState) {
                case EDIT:
                    mFieldList.forEach((fieldName, mField) -> {
                        if (!mField.mCalculated && !fieldName.contentEquals("_id") && mField.valueChanged()) {
                            document.put(fieldName, mField.value());
                        }
                    });
                    result = mEngine.update(document);
                    break;
                case INSERT:
                    mFieldList.forEach((fieldName, mField) -> {
                        if (!mField.mCalculated) {
                            if (!(mField instanceof MFieldObjectId && fieldName.contentEquals("_id")) && !(mField.mValue == null)) {
                                document.put(fieldName, mField.value());
                            }
                        }
                    });
                    result = mEngine.insert(document);
                    break;
                default:
                    result = false;
            }
            if (!result) {
                mException = mEngine.mException;
                return false;
            }
        }

        mEof = false;

        mState = STATE.NORMAL;

        return true;
    }

    /**
     * state
     *
     * @return STATE of Table
     */
    public STATE state() {
        return mState;
    }

    public enum STATE {
        NORMAL,
        EDIT,
        INSERT
    }
}
