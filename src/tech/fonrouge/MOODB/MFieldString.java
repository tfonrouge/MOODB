package tech.fonrouge.MOODB;

import org.bson.Document;

import java.util.ArrayList;

public class MFieldString extends MField<String> {

    public MFieldString(MTable owner, String name) {
        super(owner, name);
    }

    public boolean findPartial(String key, boolean ignoreCase) {
        boolean result = false;

        if (key.isEmpty()) {
            table.goTo(null);
        } else {
            /* TODO: add masterSource filter */
            ArrayList<Document> documents = new ArrayList<>();
            documents.add(
                    new Document("$match",
                            new Document(name,
                                    new Document("$regex", "^" + key)
                                            .append("$options", ignoreCase ? "i" : ""))));
            /* TODO: use specific field data info to make a search with regexp */
            result = table.aggregate(documents);
        }
        return result;
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.STRING;
    }

    @Override
    public boolean setValueAsString(String value) {
        return super.setValue(value);
    }

    @Override
    protected String getTypedValue() {
        return table.tableState.getFieldValue(this, String.class);
    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    public String getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        return valueAsString(value());
    }

    @Override
    public String valueAsString(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
