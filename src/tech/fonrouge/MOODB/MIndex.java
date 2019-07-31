package tech.fonrouge.MOODB;

import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.CollationStrength;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public abstract class MIndex {
    protected Bson partialFilter;
    private String name;
    private boolean unique;
    private boolean sparse;
    private MTable table;
    private Document masterKeyDocument;
    private Document keyDocument;
    private Collation collation;
    private Integer keySortValue;

    public MIndex(MTable table, String name, String masterKeyField, String keyField, boolean unique, boolean sparse, String locale, Integer strength) {
        this.table = table;
        this.name = name;
        this.unique = unique;
        this.sparse = sparse;

        if (locale != null || strength != null) {
            Collation.Builder builder = Collation.builder();
            if (locale == null) {
                locale = Locale.getDefault().getLanguage();
            }
            builder.locale(locale);
            if (strength == null) {
                strength = 3; /* default level */
            }
            builder.collationStrength(CollationStrength.fromInt(strength));
            collation = builder.build();
        }

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

    public MIndex keySort(int value) {
        keySortValue = value;
        return this;
    }

    @SuppressWarnings("unused")
    public Collation getCollation() {
        return collation;
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

        if (collation != null) {
            indexOptions.collation(collation);
        }

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

        if (keyDocument != null) {
            keyDocument.forEach(doc::append);
            if (keyDocument.size() == 1) {
                MField<?> mField = null;
                for (Map.Entry<String, Object> entry : keyDocument.entrySet()) {
                    String key = entry.getKey();
                    mField = table.fieldByName(key);
                }
                if (mField != null) {
                    mField.setFieldIndex(this);
                }
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean find(Object... objects) {
        Document document = getMasterKeyFindExpression();
        getKeyFieldFindExpression(document, objects);
        ArrayList<Document> pipeline = new ArrayList<>();
        if (document.size() > 0) {
            pipeline.add(new Document().append("$match", document));
        }
        if (keyDocument != null && keyDocument.size() > 0) {
            if (keySortValue == null) {
                pipeline.add(new Document().append("$sort", keyDocument));
            } else {
                Document keyDoc = new Document();
                keyDocument.forEach((key, value) -> keyDoc.append(key, keySortValue));
                pipeline.add(new Document().append("$sort", keyDoc));
            }
        }

        keySortValue = null;

        MongoCursor<Document> cursor;
        if (collation != null) {
            cursor = table.engine.find(pipeline, collation);
        } else {
            cursor = table.engine.find(pipeline);
        }
        return table.setMongoCursor(cursor);
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
                    document.append(field.trim(), 1);
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
        final boolean[] usingMasterKeyField = {false};

        if (masterKeyDocument != null) {
            masterKeyDocument.forEach((key, value) -> {
                MField<?> mField = table.fieldByName(key);
                Object o = null;
                if (mField != null) {
                    if (table.getMasterSourceField() != null && table.getMasterSourceField().equals(mField)) {
                        o = table.getMasterSource()._id();
                        usingMasterKeyField[0] = true;
                    } else {
                        o = mField.getDefaultValue();
                    }
                }
                document.append(key, o);
            });
        }

        if (table.getMasterSourceField() != null && !usingMasterKeyField[0]) {
            document.append(table.getMasterSourceField().name, table.getMasterSource()._id());
            System.out.println("! MIndex: empty masterKeyDocument on table with masterSource");
            System.out.println("  " + table);
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
