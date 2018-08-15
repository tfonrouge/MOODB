package tech.fonrouge.MOODB;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Scanner;

public class MIndex {
    private boolean mDescending;
    private String mKeyField;
    private String mMasterKeyField;
    private String mName;
    private boolean mUnique;
    private MTable mTable;

    public MIndex(MTable table, String name, String masterKeyField, String keyField, boolean descending, boolean unique) {
        mTable = table;
        mName = name;
        mMasterKeyField = masterKeyField;
        mKeyField = keyField;
        mDescending = descending;
        mUnique = unique;
        mTable.mIndices.add(this);
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

        for (MIndex mIndex : mTable.mIndices) {
            s = mIndex.mMasterKeyField;
            if (!mIndex.mMasterKeyField.isEmpty()) {
                if (!mIndex.mKeyField.isEmpty()) {
                    s += "," + mIndex.mKeyField;
                }
            } else {
                s = mIndex.mKeyField;
            }
            if (mIndex.mDescending) {
                bson = Indexes.descending(getFields(s));

            } else {
                bson = Indexes.ascending(getFields(s));
            }
            indexOptions = new IndexOptions();
            indexOptions.name(mIndex.mName);
            if (mIndex.mUnique) {
                indexOptions.unique(true);
            }
            mTable.mEngine.mCollection.createIndex(bson, indexOptions);
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
        return mDescending;
    }

    /**
     * keyField
     *
     * @return String of key field
     */
    public String keyField() {
        return mKeyField;
    }

    /**
     * masterKeyField
     *
     * @return String of master key field
     */
    public String masterKeyField() {
        return mMasterKeyField;
    }

    /**
     * name
     *
     * @return String name of index
     */
    public String name() {
        return mName;
    }

    /**
     * unique
     *
     * @return boolean for unique index value
     */
    public boolean unique() {
        return mUnique;
    }
}
