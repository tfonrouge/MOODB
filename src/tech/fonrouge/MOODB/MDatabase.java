package tech.fonrouge.MOODB;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

public abstract class MDatabase {

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    private MongoCollection<Document> referentialIntegrityTable;
    private MTable table;

    public MDatabase(MTable oTable) {
        this.table = oTable;
        String clientURI = getDatabaseURI();
        MongoClientURI mongoClientURI = new MongoClientURI(clientURI);
        mongoClient = new MongoClient(mongoClientURI);
        mongoDatabase = mongoClient.getDatabase(getDatabaseName());
    }

    public MongoCollection<Document> getReferentialIntegrityTable() {
        if (referentialIntegrityTable == null) {
            referentialIntegrityTable = mongoDatabase.getCollection("__referentialIntegrity");
            Document indexDoc = new Document("master", 1).append("detail", 1).append("detailField", 1);
            IndexOptions options = new IndexOptions().unique(true);
            referentialIntegrityTable.createIndex(indexDoc, options);
        }
        return referentialIntegrityTable;
    }

    public MTable getTable() {
        return table;
    }

    final String getDatabaseURI() {
        String uri = "mongodb://";

        if (getUserName().isEmpty()) {
            uri += getServerName();
        } else {
            uri += getUserName() + ":" + getPassword() + "@" + getServerName();
        }
        if (getPortNumber() != 0) {
            uri += ":" + getPortNumber();
        }
        if (!getAuthSource().isEmpty()) {
            uri += "/?authSource=" + getAuthSource();
        }
        return uri;
    }

    public abstract String getAuthSource();

    public abstract String getDatabaseName();

    public abstract String getUserName();

    public abstract String getPassword();

    public abstract String getServerName();

    public abstract int getPortNumber();

    MongoCollection<Document> getCollection(String tableName) {
        return mongoDatabase.getCollection(tableName);
    }
}
