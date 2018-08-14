package tech.fonrouge.MOODB;

import org.bson.types.ObjectId;

public abstract class MFieldObjectId extends MField<ObjectId> {

    MFieldObjectId(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public ObjectId emptyValue() {
        return null;
    }
}
