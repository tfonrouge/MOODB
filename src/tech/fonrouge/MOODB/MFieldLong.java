package tech.fonrouge.MOODB;

public class MFieldLong extends MField<Long> {

    protected MFieldLong(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Long emptyValue() {
        return 0L;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.LONG;
    }
}
