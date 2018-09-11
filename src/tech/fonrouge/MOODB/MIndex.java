package tech.fonrouge.MOODB;

import com.mongodb.Block;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Scanner;

public class MIndex {
    private boolean descending;
    private String keyField;
    private String masterKeyField;
    private String name;
    private boolean unique;
    private MTable table;

    public MIndex(MTable table, String name, String masterKeyField, String keyField, boolean descending, boolean unique) {
        this.table = table;
        this.name = name;
        this.masterKeyField = masterKeyField;
        this.keyField = keyField;
        this.descending = descending;
        this.unique = unique;
        this.table.indices.add(this);
        buildIndex();
    }

    /* *************** */
    /* private methods */
    /* *************** */

    private ArrayList<String> getFields(String s) {
        ArrayList<String> strings = new ArrayList<>();
        Scanner scanner = new Scanner(s).useDelimiter(",");
        while (scanner.hasNext()) {
            strings.add(scanner.next());
        }
        return strings;
    }

    /* ***************** */
    /* protected methods */
    /* ***************** */

    void buildIndex() {
        IndexOptions indexOptions;
        Bson bson;
        String s;

        for (MIndex mIndex : table.indices) {
            s = mIndex.masterKeyField;
            if (!mIndex.masterKeyField.isEmpty()) {
                if (!mIndex.keyField.isEmpty()) {
                    s += "," + mIndex.keyField;
                }
            } else {
                s = mIndex.keyField;
            }
            if (mIndex.descending) {
                bson = Indexes.descending(getFields(s));

            } else {
                bson = Indexes.ascending(getFields(s));
            }
            indexOptions = new IndexOptions();
            indexOptions.name(mIndex.name);
            if (mIndex.unique) {
                indexOptions.unique(true);
            }

            ListIndexesIterable<Document> indices = table.engine.collection.listIndexes();
            final boolean[] buildIndex = {true};
            indices.forEach((Block<? super Document>) document -> {
                if (document.containsKey("name") && document.getString("name").contentEquals(mIndex.name)) {
                    buildIndex[0] = false;
                }
            });
            if (buildIndex[0]) {
                table.engine.collection.createIndex(bson, indexOptions);
            }
        }
    }

    /* ************** */
    /* public methods */
    /* ************** */

    /**
     * descending
     *
     * @return boolean for descending index value
     */
    public boolean descending() {
        return descending;
    }

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
