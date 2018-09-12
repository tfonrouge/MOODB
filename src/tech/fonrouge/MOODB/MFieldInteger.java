package tech.fonrouge.MOODB;

public class MFieldInteger extends MField<Integer> {

    public MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Integer getEmptyValue() {
        return 0;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.INTEGER;
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
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
