package tech.fonrouge.MOODB;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import tech.fonrouge.MOODB.ui.UI_Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MEngine {

    private static HashMap<Class, MDatabase> mDatabaseHashMap;

    MongoCollection<Document> collection;
    MDatabase mDatabase;
    private MTable table;
    private ArrayList<Document> pipeline;
    private MongoDatabase mongoDatabase;

    MEngine(MTable oTable) {
        table = oTable;
        initialize();
    }

    /**
     * aggregateFind : go to the first document in scope
     *
     * @return success on aggregateFind
     */
    public boolean aggregateFind() {
        Document masterSourceFilter = buildMasterSourceFilter();
        if (masterSourceFilter != null) {
            pipeline = new ArrayList<>();
            pipeline.add(
                    new Document().
                            append("$match", masterSourceFilter));
        } else {
            pipeline = new ArrayList<>();
        }
        return table.setTableDocument(aggregateFind(pipeline));
    }

    /**
     * aggregateFind
     *
     * @param documentList bson list for aggregateFind function
     * @return iterable
     */
    public MongoCursor<Document> aggregateFind(List<Document> documentList) {
        for (int i = 0; i < table.fieldList.size(); i++) {
            if (table.fieldList.get(i).fieldType == MTable.FIELD_TYPE.TABLE_FIELD) {
                MFieldTableField mFieldTableField = (MFieldTableField) table.fieldList.get(i);
                if (!mFieldTableField.calculated && mFieldTableField.isLookupDocument()) {
                    documentList.addAll(getLookupStage(mFieldTableField.name));
                }
            }
        }
        return collection.aggregate(documentList).iterator();
    }

    private Document buildMasterSourceFilter() {
        Document document = new Document();
        if (table.getMasterSource() != null) {
            document.append(table.getMasterSourceField().name, table.getMasterSource()._id());
        }
        table.setFieldFilters();
        table.fieldList.forEach(mField -> {
            if (!mField.isCalculated() && mField.getFieldState().filterValue != null) {
                document.append(mField.name, mField.getFieldState().filterValue);
            }
        });
        return document;
    }

    /**
     * count
     *
     * @return long of number of documents
     */
    long count() {
        Document document = buildMasterSourceFilter();
        if (document != null) {
            return collection.countDocuments(document);
        }
        return collection.countDocuments();
    }

    /**
     * delete
     *
     * @return true if operation successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean delete() {
        Object _id = table._id();
        ClientSession session = mDatabase.mongoClient.startSession();
        session.startTransaction();
        for (Document document : mDatabase.getReferentialIntegrityTable().find(session, new Document("master", table.getTableName()))) {
            MongoCollection<Document> detailCollection = mongoDatabase.getCollection(document.getString("detail"));
            MongoCursor<Document> detailCursor = detailCollection.find(session, new Document(document.getString("detailField"), _id)).iterator();
            if (detailCursor.hasNext()) {
                session.abortTransaction();
                session.close();
                table.tableState.exception = new RuntimeException("Delete error: Document has child references...");
                return false;
            }
        }
        boolean result = collection.deleteOne(session, new Document().append("_id", _id)).getDeletedCount() == 1;
        session.commitTransaction();
        session.close();
        return result;
    }

    private List<Document> getLookupStage(String fieldName) {
        List<Document> documents = new ArrayList<>();

        MFieldTableField mFieldTableField = table.fieldTableFieldByName(fieldName);

        if (mFieldTableField != null) {
            String lookupFieldName = mFieldTableField.name;
            String lookupTableName = mFieldTableField.linkedTable().getTableName();
            String varName = lookupFieldName.substring(0, 1).toLowerCase() + lookupFieldName.substring(1) + "_id";
            documents.add(
                    new Document()
                            .append("$lookup", new Document()
                                    .append("from", lookupTableName)
                                    .append("let", new Document()
                                            .append(varName, "$" + lookupFieldName)
                                    )
                                    .append("pipeline", Arrays.asList(
                                            new Document()
                                                    .append("$match", new Document()
                                                            .append("$expr", new Document()
                                                                    .append("$eq", Arrays.asList(
                                                                            "$_id",
                                                                            "$$" + varName
                                                                            )
                                                                    )
                                                            )
                                                    ),
                                            new Document()
                                                    .append("$limit", 1)
                                            )
                                    )
                                    .append("as", "" + lookupFieldName)
                            ));

            documents.add(
                    new Document().
                            append("$unwind", new Document().
                                    append("path", "$" + lookupFieldName).
                                    append("preserveNullAndEmptyArrays", true)
                            )

            );

        }

        return documents;
    }

    MDatabase getMDatabase() {
        return mDatabase;
    }

    @SuppressWarnings("unused")
    public MongoClient getMongoClient() {
        return mDatabase.mongoClient;
    }

    @SuppressWarnings("WeakerAccess")
    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    /**
     * goTo
     *
     * @param objectId of document to go
     */
    @SuppressWarnings("WeakerAccess")
    public boolean goTo(Object objectId) {
        pipeline = new ArrayList<>();
        pipeline.add(
                new Document().
                        append("$match", new Document().
                                append("_id", objectId)));
        pipeline.add(
                new Document().
                        append("$limit", 1));
        return table.setTableDocument(aggregateFind(pipeline));
    }

    private void initialize() {
        Class<?> mDatabaseClass = table.getMDatabaseClass();

        if (mDatabaseHashMap == null) {
            mDatabaseHashMap = new HashMap<>();
        }

        mDatabase = mDatabaseHashMap.get(mDatabaseClass);

        if (mDatabase == null) {
            try {
                Constructor<?> ctor = mDatabaseClass.getConstructor(MTable.class);
                mDatabase = (MDatabase) ctor.newInstance(table);
                mDatabaseHashMap.put(mDatabaseClass, mDatabase);
                mongoDatabase = mDatabase.mongoDatabase;
                collection = mDatabase.getCollection(table.getTableName());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                UI_Message.Error("Database Error", "MongoDb Engine Error", e.getMessage());
            }
        }
    }

    /**
     * insert
     *
     * @param document document to insert
     * @return iterable
     */
    public boolean insert(Document document) {
        try {
            collection.insertOne(document);
            Object a = document.get("_id");
            table.set_id(a);
        } catch (Exception e) {
            table.tableState.exception = e;
            return false;
        }
        return true;
    }

    /**
     * next
     *
     * @return boolean if next is valid document
     */
    public boolean next() {
        if (table.tableState.mongoCursor != null) {
            if (table.tableState.mongoCursor.hasNext()) {
                return table.setTableDocument(table.tableState.mongoCursor);
            }
        }
        return table.setTableDocument(null);
    }

    /**
     * update
     *
     * @param document document to update
     * @return boolean on success
     */
    @SuppressWarnings("WeakerAccess")
    public boolean update(Document document) {
        try {
            Bson bson = new Document("$set", document);
            collection.updateOne(eq("_id", table._id()), bson);
        } catch (Exception e) {
            table.tableState.exception = e;
            return false;
        }
        return true;
    }
}
