package tech.fonrouge.MOODB;

public class MFieldDouble extends MField<Double> {

    protected MFieldDouble(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Double emptyValue() {
        return 0.0;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.DOUBLE;
    }
}
