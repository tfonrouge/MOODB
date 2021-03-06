package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import tech.fonrouge.ui.Toast;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public abstract class MField<T> {

    public final int index;
    public final MTable.FIELD_TYPE fieldType = getFieldType();
    final MTable table;
    protected boolean notNull;
    protected boolean notEmpty;
    protected boolean calculated;
    protected boolean newFinal;
    protected String description;
    protected String label;
    protected ValueItems<T> valueItems = null;
    protected Callable<T> calcValue = null;
    protected boolean autoInc;
    protected boolean readOnly;
    protected Runnable onAfterChangeValue;
    protected OnBeforeChangeValue<T> onBeforeChangeValue;
    protected Callable<T> onNewValue;
    protected Callable<Boolean> onValidate;
    String name;
    private String messageWarning;
    private String invalidCause = null;
    private MIndex fieldIndex = null;

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
        newFinal = false;
        autoInc = false;
        readOnly = false;
        notNull = false;
        notEmpty = false;
        index = table.fieldList.size();
        table.fieldList.add(this);
        if (table.tableState == null) {
            table.tableState = new TableState();
        }
        table.tableState.fieldStateList.add(new FieldState());
        initialize();
    }

    public boolean find() {
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(
                new Document().
                        append("$sort", new Document().
                                append(name, 1))
        );
        return table.setMongoCursor(table.engine.find(pipeline));
    }

    /**
     * find
     *
     * @param keyValue
     * @return
     */
    public boolean find(Object keyValue) {
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(
                new Document().
                        append("$match", new Document().
                                append(name, keyValue)));
        pipeline.add(
                new Document().
                        append("$limit", 1));
        MongoCursor<Document> cursor;
        if (fieldIndex != null) {
            return fieldIndex.find(keyValue);
        }
        return table.setMongoCursor(table.engine.find(pipeline));
    }

    public MTable getTable() {
        return table;
    }

    /**
     * initialize
     */
    protected void initialize() {

    }

    public abstract T getAsValue(Object anyValue);

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

    protected abstract MTable.FIELD_TYPE getFieldType();

    public String getInvalidCause() {
        return invalidCause;
    }

    protected T getNextSequence() {
        return null;
    }

    public String getLabelOfValue() {
        if (valueItems != null) {
            return valueItems.get(value());
        }
        return null;
    }

    /**
     * getValueItems
     *
     * @return valueItems
     */
    public ValueItems<T> getValueItems() {
        return valueItems;
    }

    /**
     * getEmptyValue
     *
     * @return empty value for the field type
     */
    abstract public T getEmptyValue();

    /**
     * getNewValue
     *
     * @return
     */
    final protected T getNewValue() {
        if (onNewValue != null) {
            try {
                return onNewValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return on invalid status, returns string with detail
     */
    public boolean getValidStatus() {
        invalidCause = null;
        if (!calculated) {
            if (notNull && value() == null && !autoInc) {
                invalidCause = "Not Null value required on field";
                return false;
            }
            if (fieldType == MTable.FIELD_TYPE.STRING && notEmpty && (value() == null || ((String) value()).isEmpty())) {
                invalidCause = "Not Empty string value required on field";
                return false;
            }
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
     * isEmpty
     *
     * @return
     */
    public boolean isEmpty() {
        T value = value();
        return value == null || value.equals(getEmptyValue());
    }

    /**
     * value
     *
     * @return value of field
     */
    public final T value() {
        if (calculated) {
            return getCalculatedValue();
        }
        return getTypedValue();
        //return fieldState.value;
    }

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

    protected abstract T getTypedValue();

    public boolean isCalculated() {
        return calculated;
    }

    public boolean isReadOnly() {
        return calculated || autoInc || (table.tableState.state == MTable.STATE.EDIT && newFinal && getFieldState().origValue != null) || readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        table.tableState.set_UI_state(this);
    }

    public void setFilterValue(T value) {
        getFieldState().filterValue = value;
    }

    public FieldState getFieldState() {
        return table.tableState.fieldStateList.get(index);
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

        if (isReadOnly()) {
            return false;
        }

        if (onBeforeChangeValue != null) {
            boolean result = onBeforeChangeValue.testValue(value);
            if (!result) {
                if (messageWarning != null) {
                    Toast.INSTANCE.showWarning(messageWarning);
                }
                getFieldState().updateUI(false);
                return false;
            }
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

        //this.fieldState.value = value;
        if (table.tableState.setFieldValue(index, value)) {
            if (onAfterChangeValue != null) {
                try {
                    onAfterChangeValue.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        getFieldState().updateUI(false);

        return true;
    }

    abstract public boolean setValueAsString(String value);

    public final T value(T defaultValue) {
        T value = value();
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public abstract String valueAsString();

    public abstract String valueAsString(T value);

    /**
     * valueChanged
     *
     * @return boolean if value has changed
     */
    public boolean valueChanged() {
        return !(getTypedValue() == getFieldState().origValue);
    }

    @SuppressWarnings("WeakerAccess")
    final public Object getDefaultValue() {
        return getFieldState().defaultValue;
    }

    @SuppressWarnings("unused")
    final public void setDefaultValue(T defaultValue) {
        getFieldState().defaultValue = defaultValue;
    }

    final public T getOrigValue() {
        return (T) getFieldState().origValue;
    }

    @SuppressWarnings("unused")
    public boolean valueEquals(T value) {
        T thisValue = value();
        if (value == null) {
            return thisValue == null;
        }
        return value.equals(thisValue);
    }

    public String toString() {
        Object value = value();
        String s = table.getClass().getSimpleName() + "@" + getClass().getSuperclass().getSimpleName();
        s += "[name=" + name + "]";
        s += "=" + (value == null ? "null" : value.toString());
        return s;
    }

    public String getMessageWarning() {
        return messageWarning;
    }

    public void setMessageWarning(String messageWarning) {
        this.messageWarning = messageWarning;
    }

    @SuppressWarnings("unused")
    public MIndex getFieldIndex() {
        return fieldIndex;
    }

    @SuppressWarnings("WeakerAccess")
    public void setFieldIndex(MIndex fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    protected interface OnBeforeChangeValue<U> {
        boolean testValue(U value);
    }
}
