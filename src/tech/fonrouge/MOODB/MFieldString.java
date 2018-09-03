package tech.fonrouge.MOODB;

public class MFieldString extends MField<String> {

    public MFieldString(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public String emptyValue() {
        return "";
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.STRING;
    }
}
