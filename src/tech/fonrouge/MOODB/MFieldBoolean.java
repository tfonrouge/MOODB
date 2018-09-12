package tech.fonrouge.MOODB;

public class MFieldBoolean extends MField<Boolean> {

    protected MFieldBoolean(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Boolean getEmptyValue() {
        return false;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.BOOLEAN;
    }

    @Override
    public Boolean getAsValue(Object anyValue) {
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
