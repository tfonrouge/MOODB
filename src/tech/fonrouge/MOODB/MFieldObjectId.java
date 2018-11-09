package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public class MFieldObjectId extends MField<ObjectId> {

    MFieldObjectId(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public ObjectId getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public ObjectId getEmptyValue() {
        return new ObjectId();
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.OBJECT_ID;
    }

    @Override
    public boolean setValueAsString(String value) {
        return super.setValue(new ObjectId(value));
    }

    @Override
    public String valueAsString() {
        ObjectId value = value();
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
