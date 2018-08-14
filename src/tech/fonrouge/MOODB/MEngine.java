package tech.fonrouge.MOODB;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MEngine {

    static HashMap<String, MongoCollection<Document>> collections;

    MongoCollection<Document> mCollection;
    Exception mException;
    private MongoCursor<Document> mMongoCursor;
    private MTable mTable;
    private List<? extends Bson> pipeline;

    /* ******************* */
    /* constructor methods */
    /* ******************* */
    MEngine(MTable oTable) {
        mTable = oTable;
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
        return mCollection.countDocuments();
    }

    /* *************** */
    /* private methods */
    /* *************** */

    private Document buildMasterSourceFilter() {
        Document document = null;
        if (mTable.mMasterSource != null) {
            document = new Document().
                    append(mTable.mMasterSourceField.mName, mTable.mMasterSource.objectId());
        }
        return document;
    }

    private void initialize() {
        String clientURI = mTable.database().getDatabaseURI();
        String tableName = mTable.getTableName();
        String key = clientURI + "/" + tableName;

        if (collections == null) {
            collections = new HashMap<>();
        }

        if (collections.containsKey(key)) {
            mCollection = collections.get(key);
        } else {
            MongoClientURI mongoClientURI = new MongoClientURI(clientURI);
            MongoClient mongoClient = new MongoClient(mongoClientURI);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(mTable.database().getDatabaseName());
            mCollection = mongoDatabase.getCollection(tableName);
            collections.put(key, mCollection);
        }
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * executeAggregate
     *
     * @param bsonList bson list for executeAggregate function
     * @return iterable
     */
    public boolean executeAggregate(List<? extends Bson> bsonList) {
        mMongoCursor = mCollection.aggregate(bsonList).iterator();
        return mTable.setTableDocument(mMongoCursor);
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
        return executeAggregate(pipeline);
    }

    /**
     * goTo
     *
     * @param objectId of document to go
     */
    public boolean goTo(ObjectId objectId) {
        pipeline = Arrays.asList(
                new Document().
                        append("$match", new Document().
                                append("_id", objectId)),
                new Document().
                        append("$limit", 1)
        );
        return executeAggregate(pipeline);
    }

    /**
     * insert
     *
     * @param document document to insert
     * @return iterable
     */
    public boolean insert(Document document) {
        try {
            mCollection.insertOne(document);
            mTable.setObjectId(document.getObjectId("_id"));
        } catch (Exception e) {
            mException = e;
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
        if (mMongoCursor != null) {
            if (mMongoCursor.hasNext()) {
                return mTable.setTableDocument(mMongoCursor);
            }
        }
        return mTable.setTableDocument(null);
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
            mCollection.updateOne(eq("_id", mTable.objectId()), bson);
        } catch (Exception e) {
            mException = e;
            return false;
        }
        return true;
    }
}
