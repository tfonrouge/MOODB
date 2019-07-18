package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import tech.fonrouge.MOODB.ui.UI_Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/* TODO: define behavior on re-declared same field names on subclasses s*/
abstract public class MTable {

    /**
     * field__id : field for table's primary key
     */
    public final MFieldObject field__id = new MFieldObject(this, "_id");

    ArrayList<MField> fieldList;
    ArrayList<MIndex> indexList = new ArrayList<>();

    MEngine engine;
    TableState tableState;
    private int tableStateIndex = 0;
    private ArrayList<TableState> tableStateList = new ArrayList<>();
    private MTable masterSource = null;
    private MFieldTableField masterSourceField;
    private Callable<Boolean> onValidateFields;
    private MIndex index;
    private boolean postResult;

    public MTable() {
        initialize();
    }

    public MTable(MTable masterSource) {
        this.masterSource = masterSource;
        initialize();
    }

    public boolean isPostResult() {
        return postResult;
    }

    /**
     * @return OBJECT_ID for current rawDocument in table
     */
    public Object _id() {
        return field__id.getTypedValue();
    }

    @SuppressWarnings("unused")
    public boolean aggregate(List<Document> documentList) {
        return setMongoCursor(engine.collection.aggregate(documentList).iterator());
    }

    /**
     * buildIndices
     */
    protected void buildIndices() {
        indexList.forEach(MIndex::buildIndex);
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
        if (onBeforeDelete()) {
            if (engine.delete()) {
                onAfterDelete();
                return true;
            }
        }
        return false;
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
        fieldList.forEach(mField -> mField.getFieldState().origValue = mField.getTypedValue());

        tableState.state = STATE.EDIT;

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

    public MFieldTableField fieldTableFieldByName(String name) {
        for (MField mField : fieldList) {
            if (mField.name.contentEquals(name) && mField instanceof MFieldTableField) {
                return (MFieldTableField) mField;
            }
        }
        return null;
    }

    public boolean find() {
        return engine.find();
    }

    @SuppressWarnings("unused")
    public Class<?> getBaseClass() {
        Class<?> clazz = getClass();
        while (clazz != MTable.class) {
            try {
                if (clazz.getDeclaredMethod("getTableName") != null) {
                    return clazz;
                }
            } catch (NoSuchMethodException ignored) {

            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public abstract <U extends MBaseData> U getData();

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

    @SuppressWarnings("unused")
    public void setException(Exception e) {
        tableState.exception = e;
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
    @SuppressWarnings("unused")
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
        return masterSource;
    }

    public MFieldTableField getMasterSourceField() {
        return masterSourceField;
    }

    /**
     * getMDatabaseClass
     *
     * @return
     */
    abstract protected Class getMDatabaseClass();

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
    @SuppressWarnings("WeakerAccess")
    public boolean goTo(Object objectId) {
        return engine.goTo(objectId);
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

    protected void initialize() {
        indexList = new ArrayList<>();

        engine = new MEngine(this);

        buildIndices();
    }

    /**
     * insert
     */
    public boolean insert() {

        if (tableState.state != STATE.NORMAL) {
            UI_Message.error("Table Insert Error", "Table previously on EDIT/INSERT State.", "check code logic.");
            return false;
        }

        if (!onBeforeInsert()) {
            return false;
        }

        MTable masterSource = getMasterSource();

        if (masterSource != null && masterSource.getState() == STATE.INSERT) {
            if (masterSource.getOnValidateFields() != null) {
                boolean valid = false;
                try {
                    valid = masterSource.onValidateFields.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!valid) {
                    return false;
                }
            }
            if (!(masterSource.post() && masterSource.edit())) {
                return false;
            }
        }

        fieldList.forEach(mField -> tableState.setFieldValue(mField.index, null));

        /* fill field values */
        fieldList.forEach(mField -> {
            if (!mField.calculated) {
                if (this.masterSource != null && mField.equals(masterSourceField)) {
                    tableState.setFieldValue(mField.index, this.masterSource._id());
                } else {
                    Object value = mField.getNewValue();
                    if (mField.fieldType == FIELD_TYPE.DATE && value == null && ((MFieldDate) mField).notNull) {
                        value = new Date();
                    }
                    tableState.setFieldValue(mField.index, value);
                    if (tableState.getFieldValue(mField) == null && (mField.notNull || mField.autoInc)) {
                        tableState.setFieldValue(mField.index, mField.getEmptyValue());
                    }
                }
            }
        });
        tableState.state = STATE.INSERT;

        onAfterInsert();

        return true;
    }

    public boolean next() {
        return engine.next();
    }

    protected void onAfterDelete() {

    }

    protected void onAfterInsert() {

    }

    protected void onAfterPost() {
    }

    protected void onAfterPostEdit() {

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
     * onBeforeInsert
     *
     * @return true if insert is allowed
     */
    protected boolean onBeforeInsert() {
        return true;
    }

    protected void onAfterPostInsert() {

    }

    /**
     * @return boolean
     */
    public boolean post() {

        postResult = false;

        if (tableState.state == STATE.NORMAL) {
            throw new RuntimeException("Table Not in EDIT/INSERT State.");
        }

        tableState.exception = null;

        if (!onValidate()) {
            tableState.exception = new RuntimeException("onValidate() not true.");
            return false;
        }

        HashMap<String, String> invalidFieldList = getInvalidFieldList();
        if (invalidFieldList.size() > 0) {
            tableState.exception = new RuntimeException("Invalid field value: " + invalidFieldList.toString());
            return false;
        }

        Document document = new Document();

        /* build rawDocument and checks for validation on fields */

        List<MFieldTableField> refList = new ArrayList<>();

        if (onBeforePost()) {
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
                    postResult = document.size() == 0 || engine.update(document);
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
                    postResult = engine.insert(document);
                    break;
                default:
                    postResult = false;
            }
            if (!postResult) {
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

    protected boolean onBeforeDelete() {
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

    public boolean onValidate() {
        return true;
    }

    void set_id(Object value) {
        field__id.table.tableState.setFieldValue(field__id.index, value);
    }

    boolean setDocumentToTableState(Document document) {
        tableState.eof = document == null;

        fieldList.forEach(mField -> {
            if (!mField.calculated) {
                Object value = document == null ? null : document.getOrDefault(mField.name, null);
                if (value == null && mField.notNull) {
                    value = mField.getEmptyValue();
                }
                if (mField.fieldType == FIELD_TYPE.TABLE_FIELD) {
                    tableState.fieldStateList.get(mField.index).document = null;
                    if (value instanceof Document) {
                        Document fieldTableDocument = (Document) value;
                        tableState.fieldStateList.get(mField.index).document = fieldTableDocument;
                        value = fieldTableDocument.get("_id");
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

    @SuppressWarnings("WeakerAccess")
    public void setFieldFilters() {

    }

    /**
     * setMongoCursor
     */
    boolean setMongoCursor(MongoCursor<Document> mongoCursor) {

        tableState.mongoCursor = mongoCursor;

        Document rawDocument = (mongoCursor == null || !mongoCursor.hasNext()) ? null : mongoCursor.next();

        return setDocumentToTableState(rawDocument);
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
        tableState = new TableState(tableState);

        tableState.exception = null;
        tableState.mongoCursor = null;
        tableState.linkedField = null;
        tableState.state = STATE.NORMAL;

        for (FieldState fieldState : currentTableState.fieldStateList) {
            tableState.fieldStateList.add(fieldState.cloneThis());
        }
        tableStateList.add(tableState);
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

    public Callable<Boolean> getOnValidateFields() {
        return onValidateFields;
    }

    public void setOnValidateFields(Callable<Boolean> onValidateFields) {
        this.onValidateFields = onValidateFields;
    }

    public MIndex getIndex() {
        return index;
    }

    public void setIndex(MIndex index) {
        this.index = index;
    }

    public void setIndex(String indexName) {
        for (MIndex mIndex : indexList) {
            if (mIndex.getName().contentEquals(indexName)) {
                setIndex(mIndex);
                return;
            }
        }
        throw new RuntimeException("index name not exist.");
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
