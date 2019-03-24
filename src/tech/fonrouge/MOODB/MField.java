package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class MField<T> {

    final MTable.FIELD_TYPE fieldType = getFieldType();
    final MTable table;
    protected boolean notNullable;
    protected boolean required;
    protected boolean calculated;
    protected String description;
    protected String label;
    protected HashMap<T, String> valueItems;
    protected Callable<T> calcValue = null;
    protected Callable<Boolean> onValidate = null;
    protected boolean autoInc;
    FieldState fieldState = new FieldState();
    String name;
    private String invalidCause = null;
    private int fieldStateIndex = 0;
    private ArrayList<FieldState> fieldStateList = new ArrayList<>();

    MField(MTable owner, String name) {

        table = owner;

        if (table.fieldList == null) {
            table.fieldList = new ArrayList<>();
        }

        if (name.contentEquals("_id") && table.fieldList.stream().anyMatch(mField -> mField.getName().contentEquals("_id"))) {
            throw new RuntimeException("attempt to re-assign_id field");
        }

        this.name = name;
        calculated = false;
        required = false;
        notNullable = false;
        table.fieldList.add(this);
        initialize();
    }

    void fieldStatePull() {
        if (fieldStateIndex == 0) {
            throw new RuntimeException("fieldStateIndex out of bounds.");
        }
        fieldState = fieldStateList.get(--fieldStateIndex);
    }

    void fieldStatePush() {
        if (fieldStateIndex == 0) {
            fieldStateList.add(fieldState);
        }
        ++fieldStateIndex;
        fieldState = new FieldState();
        fieldStateList.add(fieldState);
    }

    public boolean find() {
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().
                        append("$sort", new Document().
                                append(name, 1))
        );
        return table.setTableDocument(table.engine.executeAggregate(pipeline));
    }

    /**
     * find
     *
     * @param keyValue
     * @return
     */
    public <V extends T> boolean find(V keyValue) {
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().
                        append("$match", new Document().
                                append(name, keyValue)),
                new Document().
                        append("$limit", 1)
        );
        return table.setTableDocument(table.engine.executeAggregate(pipeline));
    }

    public abstract T getAsValue(Object anyValue);

    /**
     * getCalculatedValue
     *
     * @return
     */
    private T getCalculatedValue() {
        T value = null;
        if (calculated) {
            try {
                value = calcValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * getDescription
     *
     * @return String description for field
     */
    public String getDescription() {
        if (description == null) {
            return getLabel();
        }
        int nPos = description.indexOf("%%");
        if (nPos > 0) {
            return description.replaceFirst("%%", table.getGenre());
        }
        return description;
    }

    /**
     * getEmptyValue
     *
     * @return empty value for the field type
     */
    abstract public T getEmptyValue();

    protected abstract MTable.FIELD_TYPE getFieldType();

    public String getInvalidCause() {
        return invalidCause;
    }

    protected T getNextSequence() {
        return null;
    }

    /**
     * getValueItems
     *
     * @return valueItems
     */
    public HashMap<T, String> getValueItems() {
        return valueItems;
    }

    /**
     * getLabel
     *
     * @return String label for field
     */
    public String getLabel() {
        if (label == null) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }
        return label;
    }

    public T getMaxValue() {
        T value;
        List<? extends Bson> pipeline = Arrays.asList(
                new Document().
                        append("$sort", new Document().
                                append(name, -1)),
                new Document().
                        append("$limit", 1),
                new Document().
                        append("$project", new Document().
                                append("folio", 1))
        );
        MongoCursor<Document> mongoCursor = table.engine.executeAggregate(pipeline);
        if (mongoCursor.hasNext()) {
            Document d = mongoCursor.next();
            value = d.get(name, getEmptyValue());
        } else {
            value = getEmptyValue();
        }
        return value;
    }

    /**
     * getNewValue
     *
     * @return
     */
    final protected T getNewValue() {
        if (fieldState.callableNewValue != null) {
            try {
                return fieldState.callableNewValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    final public void setCallableNewValue(Callable<T> callable) {
        fieldState.callableNewValue = callable;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return on invalid status, returns string with detail
     */
    public boolean getValidStatus() {
        invalidCause = null;
        if (!calculated && required && isEmpty()) {
            invalidCause = "Empty value on required field";
            return false;
        }
        if (onValidate == null) {
            return true;
        }
        boolean status = false;
        try {
            status = onValidate.call();
            if (!status) {
                invalidCause = "invalid status";
            }
        } catch (Exception e) {
            e.printStackTrace();
            invalidCause = e.getLocalizedMessage();
        }
        return status;
    }

    /**
     * initialize
     */
    protected void initialize() {

    }

    public boolean isCalculated() {
        return calculated;
    }

    /**
     * isEmpty
     *
     * @return
     */
    public boolean isEmpty() {
        T value = value();
        return value == null || value.equals(getEmptyValue());
    }

    /**
     * setValue
     *
     * @param value T
     * @return true on success
     */
    public boolean setValue(T value) {

        if (table.tableState.state == MTable.STATE.NORMAL) {
            return false; //throw new RuntimeException("Attempt to setValue() to Table in Normal State.");
        }

        if (value != null && valueItems != null) {
            if (!valueItems.containsKey(value)) {
                if (value.equals(getEmptyValue())) {
                    value = null;
                } else {
                    return false; //throw new RuntimeException("Attempt to assign Invalid value to field.");
                }
            }
        }

        /* TODO: validate against notNullable value */
        this.fieldState.value = value;

        return true;
    }

    abstract public boolean setValueAsString(String value);

    /**
     * value
     *
     * @return value of field
     */
    public final T value() {
        if (calculated) {
            return getCalculatedValue();
        }
        return fieldState.value;
    }

    public final T value(T defaultValue) {
        T value = value();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public abstract String valueAsString();

    /**
     * valueChanged
     *
     * @return boolean if value has changed
     */
    boolean valueChanged() {
        return !(fieldState.value == fieldState.
                origValue);
    }

    final public Object getDefaultValue() {
        if (fieldState.defaultValue == null) {
            return fieldState.callableNewValue;
        }
        return fieldState.defaultValue;
    }

    final public void setDefaultValue(T defaultValue) {
        fieldState.defaultValue = defaultValue;
    }

    class FieldState {
        T value;
        Callable<T> callableNewValue;
        T defaultValue;
        T origValue;
    }
}
