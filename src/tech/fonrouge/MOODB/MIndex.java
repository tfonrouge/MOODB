package tech.fonrouge.MOODB;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class MIndex {
    protected Bson partialFilter;
    private String keyField;
    private String masterKeyField;
    private String name;
    private boolean unique;
    private boolean sparse;
    private MTable table;

    public MIndex(MTable table, String name, String masterKeyField, String keyField, boolean unique, boolean sparse) {
        this.table = table;
        this.name = name;
        this.masterKeyField = masterKeyField;
        this.keyField = keyField;
        this.unique = unique;
        this.sparse = sparse;
        this.table.indices.add(this);
        initialize();
        buildIndex();
    }

    /* *************** */
    /* private methods */
    /* *************** */

    private Document getFields(String s) {
        Document document = new Document();

        Scanner scanner = new Scanner(s).useDelimiter(",");
        while (scanner.hasNext()) {
            String field = scanner.next();
            if (field.contains(":")) {
                Scanner scanner1 = new Scanner(field).useDelimiter(":");
                while (scanner1.hasNext()) {
                    String indexField = scanner1.next();
                    String indexSortOrder = scanner1.next();
                    int index = Integer.parseInt(indexSortOrder);
                    document.append(indexField, index);
                }
            } else {
                document.append(field, 1);
            }
        }
        return document;
    }

    /* ***************** */
    /* protected methods */
    /* ***************** */

    void buildIndex() {
        IndexOptions indexOptions;
        Bson bson;
        String s;

        s = masterKeyField;
        if (!masterKeyField.isEmpty()) {
            if (!keyField.isEmpty()) {
                s += "," + keyField;
            }
        } else {
            s = keyField;
        }

        bson = getFields(s);

        indexOptions = new IndexOptions();
        indexOptions.name(name);

        indexOptions.unique(unique);

        indexOptions.sparse(sparse);

        if (partialFilter != null) {
            indexOptions.partialFilterExpression(partialFilter);
        }

        ListIndexesIterable<Document> indexes = table.engine.collection.listIndexes();

        final boolean[] buildIndex = {true};
        indexes.iterator().forEachRemaining(document -> {
            if (document.containsKey("name") && document.getString("name").contentEquals(name)) {
                buildIndex[0] = false;
            }
        });
        if (buildIndex[0]) {
            table.engine.collection.createIndex(bson, indexOptions);
        }
    }

    /* ************** */
    /* public methods */
    /* ************** */

    public boolean aggregateFind(Object... objects) {
        Document document = getMasterKeyFindExpression(masterKeyField);
        getKeyFieldFindExpression(document, objects);
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document().append("$match", document));
        return table.setTableDocument(table.engine.aggregateFind(pipeline));
    }

    private void getKeyFieldFindExpression(Document document, Object... objects) {
        Scanner scanner = new Scanner(keyField).useDelimiter(",");
        int i = 0;
        while (scanner.hasNext()) {
            String fieldName = scanner.next();
            if (i < objects.length) {
                document.append(fieldName, objects[i++]);
            }
        }
    }

    private Document getMasterKeyFindExpression(String masterKeyField) {
        Document document = new Document();
        Scanner scanner = new Scanner(masterKeyField).useDelimiter(",");
        while (scanner.hasNext()) {
            String fieldName = scanner.next();
            MField mField = table.fieldByName(fieldName);
            if (mField != null) {
                document.append(fieldName, mField.getDefaultValue());
            }
        }
        return document;
    }

    protected abstract void initialize();

    /**
     * keyField
     *
     * @return String of key field
     */
    public String keyField() {
        return keyField;
    }

    /**
     * masterKeyField
     *
     * @return String of master key field
     */
    @SuppressWarnings("unused")
    public String masterKeyField() {
        return masterKeyField;
    }

    /**
     * name
     *
     * @return String name of index
     */
    public String name() {
        return name;
    }

    /**
     * unique
     *
     * @return boolean for unique index value
     */
    public boolean unique() {
        return unique;
    }

}
