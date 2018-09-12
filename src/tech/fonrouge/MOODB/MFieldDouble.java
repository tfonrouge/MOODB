package tech.fonrouge.MOODB;

public class MFieldDouble extends MField<Double> {

    protected MFieldDouble(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Double getEmptyValue() {
        return 0.0;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.DOUBLE;
    }

    @Override
    public Double getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
