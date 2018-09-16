package tech.fonrouge.MOODB;

import org.bson.types.Binary;

public class MFieldBinary extends MField<Binary> {

    protected MFieldBinary(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.BINARY;
    }

    @Override
    public Binary getEmptyValue() {
        return new Binary(new byte[0]);
    }

    @Override
    public Binary getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        if (fieldState.value == null) {
            return "";
        }
        return fieldState.value.toString();
    }
}
