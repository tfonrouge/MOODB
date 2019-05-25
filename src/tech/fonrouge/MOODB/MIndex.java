package tech.fonrouge.MOODB;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Scanner;

public abstract class MIndex {
    protected Bson partialFilter;
    private String name;
    private boolean unique;
    private boolean sparse;
    private MTable table;
    private Document masterKeyDocument;
    private Document keyDocument;

    public MIndex(MTable table, String name, String masterKeyField, String keyField, boolean unique, boolean sparse) {
        this.table = table;
        this.name = name;
        this.unique = unique;
        this.sparse = sparse;

        masterKeyDocument = getDocumentField(masterKeyField);
        keyDocument = getDocumentField(keyField);

        if (table.indexList == null) {
            table.indexList = new ArrayList<>();
        }
        table.indexList.add(this);
        if (table.getIndex() == null) {
            table.setIndex(this);
        }

        initialize();
        buildIndex();
    }

    public boolean aggregateFind(Object... objects) {
        Document document = getMasterKeyFindExpression();
        getKeyFieldFindExpression(document, objects);
        ArrayList<Document> pipeline = new ArrayList<>();
        if (document.size() > 0) {
            pipeline.add(new Document().append("$match", document));
        }
        if (keyDocument != null && keyDocument.size() > 0) {
            pipeline.add(new Document().append("$sort", keyDocument));
        }
        return table.setMongoCursor(table.engine.aggregateFind(pipeline));
    }

    void buildIndex() {
        IndexOptions indexOptions;

        Document doc = new Document();

        if (masterKeyDocument != null) {
            masterKeyDocument.forEach(doc::append);
        }

        if (keyDocument != null) {
            keyDocument.forEach(doc::append);
        }

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
            table.engine.collection.createIndex(doc, indexOptions);
        }
    }

    private Document getDocumentField(String s) {
        Document document = null;

        Scanner scanner = new Scanner(s).useDelimiter(",");

        if (scanner.hasNext()) {
            document = new Document();
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
        }
        return document;
    }

    private void getKeyFieldFindExpression(Document document, Object... objects) {
        final int[] i = {0};
        keyDocument.forEach((key, value) -> {
            if (i[0] < objects.length) {
                document.append(key, objects[i[0]++]);
            }
        });
    }

    private Document getMasterKeyFindExpression() {

        Document document = new Document();

        if (masterKeyDocument != null) {
            masterKeyDocument.forEach((key, value) -> {
                MField<?> mField = table.fieldByName(key);
                Object o = null;
                if (mField != null) {
                    o = mField.getDefaultValue();
                }
                document.append(key, o);
            });
        }
        return document;
    }

    public String getName() {
        return name;
    }

    public boolean getUnique() {
        return unique;
    }

    protected abstract void initialize();
}
