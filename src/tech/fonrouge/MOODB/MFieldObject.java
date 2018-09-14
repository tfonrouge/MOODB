package tech.fonrouge.MOODB;

public class MFieldObject extends MField<Object> {

    MFieldObject(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.OBJECT;
    }

    @Override
    public Object getEmptyValue() {
        return null;
    }

    @Override
    public Object getAsValue(Object anyValue) {
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
