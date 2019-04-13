package tech.fonrouge.MOODB;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import tech.fonrouge.MOODB.ui.UI_ChangeListener0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

public abstract class MField<T> {

    public final int index;
    public final MTable.FIELD_TYPE fieldType = getFieldType();
    final MTable table;
    protected boolean required;
    protected boolean calculated;
    protected boolean newFinal;
    protected String description;
    protected String label;
    protected HashMap<T, String> valueItems;
    protected Callable<T> calcValue = null;
    protected Callable<Boolean> onValidate = null;
    protected boolean autoInc;
    protected boolean readOnly;
    String name;
    Callable<T> callableNewValue;
    Runnable runnableOnAfterChangeValue;
    private String invalidCause = null;

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
        required = false;
        index = table.fieldList.size();
        table.fieldList.add(this);
        if (table.tableState == null) {
            table.tableState = new TableState();
        }
        table.tableState.fieldStateList.add(new FieldState());
        initialize();
    }

    /**
     * initialize
     */
    protected void initialize() {

    }

    public boolean find() {
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(
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
    public boolean find(Object keyValue) {
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(
                new Document().
                        append("$match", new Document().
                                append(name, keyValue)));
        pipeline.add(
                new Document().
                        append("$limit", 1));
        return table.setTableDocument(table.engine.executeAggregate(pipeline));
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

    /**
     * getValueItems
     *
     * @return valueItems
     */
    public HashMap<T, String> getValueItems() {
        return valueItems;
    }

    public T getMaxValue() {
        T value;
        ArrayList<Document> pipeline = new ArrayList<>();
        pipeline.add(
                new Document().
                        append("$sort", new Document().
                                append(name, -1)));
        pipeline.add(
                new Document().
                        append("$limit", 1));
        pipeline.add(
                new Document().
                        append("$project", new Document().
                                append("folio", 1)));
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
        if (callableNewValue != null) {
            try {
                return callableNewValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    final public void setCallableNewValue(Callable<T> callable) {
        callableNewValue = callable;
    }

    final public void setRunnableOnAfterChangeValue(Runnable callable) {
        runnableOnAfterChangeValue = callable;
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
        if (!calculated && required && isEmpty() && !autoInc) {
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
        return calculated || autoInc || (newFinal && table.tableState.state == MTable.STATE.EDIT) || readOnly;
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

        if (autoInc) {
            return false;
        }

        if (newFinal && table.tableState.state == MTable.STATE.EDIT) {
            return false;
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
            if (runnableOnAfterChangeValue != null) {
                try {
                    runnableOnAfterChangeValue.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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

    /**
     * valueChanged
     *
     * @return boolean if value has changed
     */
    boolean valueChanged() {
        return !(getTypedValue() == getFieldState().origValue);
    }

    @SuppressWarnings("WeakerAccess")
    final public Object getDefaultValue() {
        if (getFieldState().defaultValue == null) {
            try {
                return callableNewValue.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getFieldState().defaultValue;
    }

    @SuppressWarnings("unused")
    final public void setDefaultValue(T defaultValue) {
        getFieldState().defaultValue = defaultValue;
    }

    public String toString() {
        Object value = value();
        String s = table.getClass().getSimpleName() + "@" + getClass().getSuperclass().getSimpleName();
        s += "[name=" + name + "]";
        s += "'" + (value == null ? "null" : value.toString()) + "'";
        return s;
    }

    public class FieldState {
        public UI_ChangeListener0 ui_changeListener;
        T filterValue;
        T value;
        T defaultValue;
        T origValue;
        Document document;

        FieldState cloneThis() {
            FieldState fieldState = new FieldState();
            fieldState.filterValue = this.filterValue;
            fieldState.value = this.value;
            fieldState.defaultValue = this.defaultValue;
            fieldState.origValue = this.origValue;
            fieldState.document = this.document;
            return fieldState;
        }
    }
}
