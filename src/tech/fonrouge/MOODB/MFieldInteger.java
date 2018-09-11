package tech.fonrouge.MOODB;

public class MFieldInteger extends MField<Integer> {

    public MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Integer getEmptyValue() {
        return 0;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.INTEGER;
    }

    @Override
    public String valueAsString() {
        return value.toString();
    }
}
