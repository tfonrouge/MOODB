package tech.fonrouge.MOODB;

public class MFieldDouble extends MField<Double> {

    protected MFieldDouble(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.DOUBLE;
    }

    @Override
    public boolean setValueAsString(String value) {
        Double aDouble;
        try {
            aDouble = Double.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return super.setValue(aDouble);
    }

    @Override
    protected Double getTypedValue() {
        return table.tableState.getFieldValue(this, Double.class);
    }

    @Override
    public Double getEmptyValue() {
        return 0.0;
    }

    @Override
    public Double getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        return valueAsString(value());
    }

    @Override
    public String valueAsString(Double value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
