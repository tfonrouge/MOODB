package tech.fonrouge.MOODB;

public class MFieldString extends MField<String> {

    public MFieldString(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public String getEmptyValue() {
        return "";
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.STRING;
    }

    @Override
    public String valueAsString() {
        return value;
    }
}
