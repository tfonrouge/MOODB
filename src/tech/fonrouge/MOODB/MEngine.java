package tech.fonrouge.MOODB;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MEngine {

    private static HashMap<String, MongoCollection<Document>> collections;

    MongoCollection<Document> collection;
    Exception exception;
    MongoCursor<Document> mongoCursor;
    private MTable table;
    private List<? extends Bson> pipeline;

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
        if (table.masterSource != null) {
            document = new Document().
                    append(table.masterSourceField.name, table.masterSource._id());
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
            MongoDatabase mongoDatabase = mongoClient.getDatabase(table.getDatabase().getDatabaseName());
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
    public boolean delete() {
        Document document = new Document().append("_id", table._id());
        DeleteResult result = collection.deleteOne(document);
        return result.getDeletedCount() == 1;
    }

    /**
     * executeAggregate
     *
     * @param bsonList bson list for executeAggregate function
     * @return iterable
     */
    public MongoCursor<Document> executeAggregate(List<? extends Bson> bsonList) {
        return collection.aggregate(bsonList).iterator();
    }

    /**
     * find : go to the first document in scope
     *
     * @return
     */
    public boolean find() {
        Document masterSourceFilter = buildMasterSourceFilter();
        if (masterSourceFilter != null) {
            pipeline = Arrays.asList(
                    new Document().
                            append("$match", masterSourceFilter));
        } else {
            pipeline = Arrays.asList();
        }
        return table.setTableDocument(executeAggregate(pipeline));
    }

    /**
     * goTo
     *
     * @param objectId of document to go
     */
    public boolean goTo(Object objectId) {
        pipeline = Arrays.asList(
                new Document().
                        append("$match", new Document().
                                append("_id", objectId)),
                new Document().
                        append("$limit", 1)
        );
        return table.setTableDocument(executeAggregate(pipeline));
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
        if (mongoCursor != null) {
            if (mongoCursor.hasNext()) {
                return table.setTableDocument(mongoCursor);
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
