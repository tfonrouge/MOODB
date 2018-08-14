package tech.fonrouge.MOODB;

import org.bson.Document;
import org.bson.types.Binary;

public class MFieldBinary extends MField<Binary> {

    protected MFieldBinary(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Binary emptyValue() {
        Document d = new Document();
        Binary binary;
        return null;
    }
}
