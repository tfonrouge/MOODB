package tech.fonrouge.MOODB;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class MField<T> {
    protected boolean mRequired;
    protected String mDescription;
    protected HashMap<String, String> mKeyValueItems;
    protected boolean mCalculated;
    String mName;
    MTable mTable;

    T mValue;
    T mOrigValue;

    private Method method = null;

    protected Callable<T> calcValue = null;

    MField(MTable owner, String name) {
        mTable = owner;
        mName = name;
        mCalculated = false;
        mRequired = false;
        mTable.mFieldList.add(this);
        initialize();
    }

    /**
     * description
     *
     * @return
     */
    public String description() {
        return mDescription;
    }

    /**
     * emptyValue
     *
     * @return empty value for the field type
     */
    abstract public T emptyValue();

    public boolean find() {
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().
                        append("$sort", new Document().
                                append(mName, 1))
        );
        return mTable.mEngine.executeAggregate(pipeline);
    }

    /**
     * find
     *
     * @param keyValue
     * @return
     */
    public boolean find(T keyValue) {
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().
                        append("$match", new Document().
                                append(mName, keyValue)),
                new Document().
                        append("$limit", 1)
        );
        return mTable.mEngine.executeAggregate(pipeline);
    }

    /**
     * getCalculatedValue
     *
     * @return
     */
    private T getCalculatedValue() {
        T value = null;
        if (mCalculated) {
            try {
                value = calcValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * getNewValue
     *
     * @return
     */
    protected T getNewValue() {
        return null;
    }

    /**
     * initialize
     */
    protected void initialize() {

    }

    /**
     * isEmpty
     *
     * @return
     */
    public boolean isEmpty() {
        T value = value();
        return value == null || value.equals(emptyValue());
    }

    /**
     * name
     *
     * @return
     */
    public String name() {
        return mName;
    }

    /**
     * setValue
     *
     * @param value
     * @return
     */
    public boolean setValue(T value) {

        if (mTable.mState == MTable.STATE.NORMAL) {
            throw new RuntimeException("Attempt to setValue() to Table in Normal State.");
        }

        if (mKeyValueItems != null) {
            if (!mKeyValueItems.containsKey(value.toString())) {
                throw new RuntimeException("Attempt to assign Invalid value to field.");
            }
        }

        mValue = value;

        return true;
    }

    /**
     * value
     *
     * @return value of field
     */
    public final T value() {
        if (mCalculated) {
            return getCalculatedValue();
        }
        return mValue;
    }

    public final T value(T defaultValue) {
        T value = value();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * valueChanged
     *
     * @return boolean if value has changed
     */
    boolean valueChanged() {
        return !(mValue == mOrigValue);
    }
}
