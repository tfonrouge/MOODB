package tech.fonrouge.MOODB;

public class MFieldObject extends MField<Object> {

    public MFieldObject(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.OBJECT;
    }

    @Override
    public boolean setValueAsString(String value) {
        return super.setValue(value);
    }

    @Override
    protected Object getTypedValue() {
        return table.tableState.getFieldValue(this);
    }

    @Override
    public Object getEmptyValue() {
        return null;
    }

    @Override
    public Object getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        return valueAsString(value());
    }

    @Override
    public String valueAsString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
