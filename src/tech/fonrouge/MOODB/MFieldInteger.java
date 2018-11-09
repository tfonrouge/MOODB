package tech.fonrouge.MOODB;

public class MFieldInteger extends MField<Integer> {

    public MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.INTEGER;
    }

    @Override
    public boolean setValueAsString(String value) {
        Integer integer;
        try {
            integer = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return super.setValue(integer);
    }

    @Override
    public Integer getEmptyValue() {
        return 0;
    }

    @Override
    public Integer getAsValue(Object anyValue) {
        switch (anyValue.getClass().getName()) {
            case "java.lang.Integer":
                return (Integer) anyValue;
            case "java.lang.String":
                return Integer.valueOf((String) anyValue);
        }
        return null;
    }

    @Override
    public String valueAsString() {
        Integer value = value();
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
