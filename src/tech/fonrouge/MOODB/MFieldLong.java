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
    public boolean setValueAsString(String value) {
        Long aLong;
        try {
            aLong = Long.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return super.setValue(aLong);
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
        Long value = value();
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
