package tech.fonrouge.MOODB;

import org.bson.types.Binary;

public class MFieldBinary extends MField<Binary> {

    protected MFieldBinary(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Binary emptyValue() {
        return new Binary(new byte[0]);
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.BINARY;
    }
}
