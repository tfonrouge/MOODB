package tech.fonrouge.MOODB;

import org.bson.types.Binary;

public class MFieldBinary extends MField<Binary> {

    protected MFieldBinary(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Binary getEmptyValue() {
        return new Binary(new byte[0]);
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.BINARY;
    }

    @Override
    public Binary getAsValue(Object anyValue) {
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
