package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;

public class MFieldInteger extends MField<Integer> {

    public MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.INTEGER;
    }

    @Override
    public boolean setValueAsString(String value) {
        Integer integer;
        try {
            integer = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return super.setValue(integer);
    }

    @Override
    protected Integer getTypedValue() {
        return table.tableState.getFieldValue(this, Integer.class);
    }

    @Override
    public Integer getEmptyValue() {
        return 0;
    }

    @Override
    public Integer getAsValue(Object anyValue) {
        switch (anyValue.getClass().getName()) {
            case "java.lang.Integer":
                return (Integer) anyValue;
            case "java.lang.String":
                return Integer.valueOf((String) anyValue);
        }
        return null;
    }

    @Override
    public Integer getNextSequence() {
        MongoCollection<Document> countersCollection = table.engine.getMongoDatabase().getCollection("__counters");

        Document query = new Document("_id", table.getTableName());
        Document update = new Document("$inc", new Document(name, 1));

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        options.returnDocument(ReturnDocument.AFTER);

        Document result = countersCollection.findOneAndUpdate(query, update, options);

        if (result == null) {
            return 0;
        }

        return result.getInteger(name);
    }

    @Override
    public String valueAsString() {
        Integer value = value();
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
