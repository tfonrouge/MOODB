package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldTableField<T extends MTable> extends MFieldObject {

    public final T tableField = initializeTableField();
    private MFieldTableField<? extends MTable> linkedField;
    private String expression;

    protected MFieldTableField(MTable owner, String name) {
        super(owner, name);
    }

    private T initializeTableField() {
        T t = buildTableField();
        t.setLinkedField(this);
        return t;
    }

    protected abstract T buildTableField();

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.TABLE_FIELD;
    }

    @Override
    public ObjectId getEmptyValue() {
        return null;
    }

    @SuppressWarnings("unused")
    public String getExpression() {
        return expression;
    }

    @SuppressWarnings("unused")
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @SuppressWarnings("unused")
    public String[] getExpressionList() {
        if (expression != null) {
            return expression.split("\\.");
        }
        return null;
    }

    @SuppressWarnings("unused")
    public Object getValueFromExpression() {
        return getValueFromExpression(expression);
    }

    public Object getValueFromExpression(String expression) {
        Object value = tableField()._id();
        if (expression != null) {
            int nPos = expression.indexOf('.');
            if (nPos > 0) {
                String fieldName = expression.substring(nPos + 1);
                MField mField = tableField.getFieldList().get(fieldName);
                if (mField != null) {
                    value = mField.value();
                } else {
                    value = "Error on: '" + fieldName + "'";
                }
            }
        }
        return value;
    }

    @SuppressWarnings("unused")
    public boolean setValue(T table) {
        return super.setValue(table._id());
    }

    @SuppressWarnings("unused")
    public boolean setValueWithExpression(String value) {
        return setValueWithExpression(expression, value);
    }

    public boolean setValueWithExpression(String expression, String value) {
        if (expression != null) {
            int nPos = expression.indexOf('.');
            if (nPos > 0) {
                String fieldName = expression.substring(nPos + 1);
                MFieldString mField = (MFieldString) tableField.getFieldList().get(fieldName);
                if (mField != null) {
                    if (value == null || value.isEmpty()) {
                        return super.setValue(null);
                    } else {
                        if (mField.find(value)) {
                            return setValue(tableField);
                        }
                    }
                }
            }
        }
        return false;
    }

    public final T tableField() {

        Object objectId = tableField._id();

        if (objectId == null || !objectId.equals(value)) {
            tableField.goTo(value);
        }

        return tableField;
    }
}
