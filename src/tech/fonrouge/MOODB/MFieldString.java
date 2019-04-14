package tech.fonrouge.MOODB;

public class MFieldString extends MField<String> {

    public MFieldString(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.STRING;
    }

    @Override
    public boolean setValueAsString(String value) {
        return super.setValue(value);
    }

    @Override
    protected String getTypedValue() {
        return table.tableState.getFieldValue(this, String.class);
    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    public String getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        return valueAsString(value());
    }

    @Override
    public String valueAsString(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }
}
