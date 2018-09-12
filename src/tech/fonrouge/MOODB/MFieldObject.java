package tech.fonrouge.MOODB;

public class MFieldObject extends MField<Object> {

    MFieldObject(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Object getEmptyValue() {
        return null;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.OBJECT;
    }

    @Override
    public Object getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        if (value==null) {
            return "";
        }
        return value.toString();
    }
}
