package tech.fonrouge.MOODB;

public class MFieldObject extends MField<Object> {

    MFieldObject(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Object emptyValue() {
        return null;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.OBJECT;
    }
}
