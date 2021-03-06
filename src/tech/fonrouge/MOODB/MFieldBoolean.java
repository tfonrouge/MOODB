package tech.fonrouge.MOODB;

public class MFieldBoolean extends MField<Boolean> {

    protected MFieldBoolean(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.BOOLEAN;
    }

    @Override
    public Boolean getEmptyValue() {
        return false;
    }

    @Override
    public Boolean getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return value() == null;
    }

    public boolean setValueAsString(String value) {
        return super.setValue(value.toUpperCase().contentEquals("TRUE"));
    }

    @Override
    protected Boolean getTypedValue() {
        return table.tableState.getFieldValue(this, Boolean.class);
    }

    @Override
    public String valueAsString() {
        return valueAsString(value());
    }

    @Override
    public String valueAsString(Boolean value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
