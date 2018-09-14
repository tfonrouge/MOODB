package tech.fonrouge.MOODB;

public class MFieldLong extends MField<Long> {

    protected MFieldLong(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.LONG;
    }

    @Override
    public Long getEmptyValue() {
        return 0L;
    }

    @Override
    public Long getAsValue(Object anyValue) {
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
