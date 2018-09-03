package tech.fonrouge.MOODB;

public class MFieldBoolean extends MField<Boolean> {

    protected MFieldBoolean(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Boolean emptyValue() {
        return false;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.BOOLEAN;
    }
}
