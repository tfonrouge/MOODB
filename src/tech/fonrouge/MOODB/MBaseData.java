package tech.fonrouge.MOODB;

import org.bson.Document;

public abstract class MBaseData {

    protected final MTable table;
    protected Document document;

    public MBaseData(MTable base) {
        table = base;
        populateFieldValues();
    }

    public MTable getTable() {
        return table;
    }

    public Document getDocument() {
        return document;
    }

    private void populateFieldValues() {
        document = table.getDocument();
    }

    public Object get_id() {
        return document.get("_id");
    }
}
