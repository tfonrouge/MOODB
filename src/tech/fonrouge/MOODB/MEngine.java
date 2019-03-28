package tech.fonrouge.MOODB;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

class MEngine {

    private static HashMap<String, MongoCollection<Document>> collections;

    MongoCollection<Document> collection;
    Exception exception;
    private MTable table;
    private ArrayList<Document> pipeline;
    private MongoDatabase mongoDatabase;

    /* ******************* */
    /* constructor methods */
    /* ******************* */
    MEngine(MTable oTable) {
        table = oTable;
        initialize();
    }

    /* *********************** */
    /* package-private methods */
    /* *********************** */

    /**
     * count
     *
     * @return long of number of documents
     */
    long count() {
        return collection.countDocuments();
    }

    /* *************** */
    /* private methods */
    /* *************** */

    private Document buildMasterSourceFilter() {
        Document document = null;
        if (table.tableState.masterSource != null) {
            document = new Document().
                    append(table.tableState.masterSourceField.name, table.tableState.masterSource._id());
        }
        return document;
    }

    private void initialize() {
        String clientURI = table.getDatabase().getDatabaseURI();
        String tableName = table.getTableName();
        String key = clientURI + "/" + tableName;

        if (collections == null) {
            collections = new HashMap<>();
        }

        if (collections.containsKey(key)) {
            collection = collections.get(key);
        } else {
            MongoClientURI mongoClientURI = new MongoClientURI(clientURI);
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            mongoDatabase = mongoClient.getDatabase(table.getDatabase().getDatabaseName());
            collection = mongoDatabase.getCollection(tableName);
            collections.put(key, collection);
        }
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * delete
     *
     * @return true if operation successful
     */
    @SuppressWarnings("WeakerAccess")
    public boolean delete() {
        Document document = new Document().append("_id", table._id());
        DeleteResult result = collection.deleteOne(document);
        return result.getDeletedCount() == 1;
    }

    /**
     * executeAggregate
     *
     * @param documentList bson list for executeAggregate function
     * @return iterable
     */
    @SuppressWarnings("WeakerAccess")
    public MongoCursor<Document> executeAggregate(List<Document> documentList) {
        if (table.tableState.lookupDocument != null) {
            table.tableState.lookupDocument.forEach((fieldName, o) -> documentList.addAll(getLookupStage(fieldName.substring(1))));
        }
        return collection.aggregate(documentList).iterator();
    }

    private List<Document> getLookupStage(String fieldName) {
        List<Document> documents = new ArrayList<>();

        MFieldTableField mFieldTableField = table.fieldTableFieldByName(fieldName);

        if (mFieldTableField != null) {
            String lookupFieldName = mFieldTableField.name;
            String lookupTableName = mFieldTableField.getLinkedTable().getTableName();
            documents.add(
                    new Document()
                            .append("$lookup", new Document()
                                    .append("from", lookupTableName)
                                    .append("let", new Document()
                                            .append(lookupFieldName + "_id", "$" + lookupFieldName)
                                    )
                                    .append("pipeline", Arrays.asList(
                                            new Document()
                                                    .append("$match", new Document()
                                                            .append("$expr", new Document()
                                                                    .append("$eq", Arrays.asList(
                                                                            "$_id",
                                                                            "$$" + lookupFieldName + "_id"
                                                                            )
                                                                    )
                                                            )
                                                    ),
                                            new Document()
                                                    .append("$limit", 1)
                                            )
                                    )
                                    .append("as", "@" + lookupFieldName)
                            ));

            documents.add(
                    new Document().
                            append("$unwind", new Document().
                                    append("path", "$@" + lookupFieldName))

            );

        }

        return documents;
    }

    /**
     * find : go to the first document in scope
     *
     * @return success on find
     */
    public boolean find() {
        Document masterSourceFilter = buildMasterSourceFilter();
        if (masterSourceFilter != null) {
            pipeline = new ArrayList<>();
            pipeline.add(
                    new Document().
                            append("$match", masterSourceFilter));
        } else {
            pipeline = new ArrayList<>();
        }
        return table.setTableDocument(executeAggregate(pipeline));
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
        return table.setTableDocument(executeAggregate(pipeline));
    }

    /**
     * insert
     *
     * @param document document to insert
     * @return iterable
     */
    @SuppressWarnings("WeakerAccess")
    public boolean insert(Document document) {
        try {
            collection.insertOne(document);
            Object a = document.get("_id");
            table.set_id(a);
        } catch (Exception e) {
            exception = e;
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
            exception = e;
            return false;
        }
        return true;
    }
}
