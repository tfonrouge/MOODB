package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;

abstract public class MTable {


    protected MTable mMasterSource = null;
    protected MField mMasterSourceField;
    protected ArrayList<MField> mFieldList = new ArrayList<>();
    public final MFieldObjectId field__id = new MFieldObjectId(this, "_id") {
        @Override
        protected void initialize() {
            mFieldList.add(this);
            mDescription = "_id";
        }
    };
    protected ArrayList<MIndex> mIndices = new ArrayList<>();
    STATE mState = STATE.NORMAL;
    MEngine mEngine;
    boolean mEof = true;
    private Exception mException;
    private MDatabase mDatabase;

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

    /* *************** */
    /* private methods */
    /* *************** */

    /**
     * buildIndices
     */
    protected void buildIndices() {
        mIndices.forEach(MIndex::buildIndex);
    }

    private void initialize() {
        mIndices = new ArrayList<>();

        initializeModel();

        mDatabase = newDatabase();
        mEngine = new MEngine(this);

        buildIndices();
    }

    /* *********************** */
    /* package private methods */
    /* *********************** */

    /**
     * objectId
     *
     * @return ObjectId for current document in table
     */
    public ObjectId objectId() {
        return field__id.mValue;
    }

    void setObjectId(ObjectId objectId) {
        field__id.mValue = objectId;
    }


    /* ***************** */
    /* protected methods */
    /* ***************** */

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

    protected void setMasterSource(MTable masterSource, MField masterSourceField) {
        mMasterSource = masterSource;
        mMasterSourceField = masterSourceField;
    }

    /**
     * setTableDocument
     */
    protected boolean setTableDocument(MongoCursor<Document> mongoCursor) {

        final Document document = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        mEof = document == null;

        mFieldList.forEach(mField -> {
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
        mFieldList.forEach(mField -> {
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
    public ArrayList<MField> fieldList() {
        return mFieldList;
    }

    public boolean find() {
        return mEngine.find();
    }

    /**
     * getException
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
     * @param objectId : objectId of document to go
     */
    public boolean goTo(ObjectId objectId) {
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
        mFieldList.forEach(mField -> {
            if (!mField.mCalculated) {
                mField.mValue = mField.getNewValue();
                if (mMasterSource != null && mField.equals(mMasterSourceField)) {
                    mField.mValue = mMasterSource.objectId();
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

        /* checks for empty values on required fields */
        for (MField mField : mFieldList) {
            if (!mField.mCalculated && mField.mRequired && mField.isEmpty()) {
                mException = new Exception("Empty Value on Required Field: '" + mField.mName + "'");
                return false;
            }
        }

        Document document = new Document();

        /* build document and checks for validation on fields */

        if (onBeforePost()) {
            boolean result;
            switch (mState) {
                case EDIT:
                    mFieldList.forEach(mField -> {
                        if (!mField.mCalculated && !mField.mName.contentEquals("_id") && mField.valueChanged()) {
                            document.put(mField.mName, mField.value());
                        }
                    });
                    result = mEngine.update(document);
                    break;
                case INSERT:
                    mFieldList.forEach(mField -> {
                        if (!mField.mCalculated) {
                            if (!mField.mName.contentEquals("_id") && !(mField.mValue == null)) {
                                document.put(mField.mName, mField.value());
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
