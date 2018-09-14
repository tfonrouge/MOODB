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
    public Double getEmptyValue() {
        return 0.0;
    }

    @Override
    public Double getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        if (getFieldState().value == null) {
            return "";
        }
        return getFieldState().value.toString();
    }
}
