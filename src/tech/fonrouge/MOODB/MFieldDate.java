package tech.fonrouge.MOODB;

import java.util.Date;

public class MFieldDate extends MField<Date> {

    protected boolean mNewDate;

    protected MFieldDate(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Date getEmptyValue() {
        return null;
    }

    @Override
    public MTable.FIELD_TYPE fieldType() {
        return MTable.FIELD_TYPE.DATE;
    }

    @Override
    public Date getAsValue(Object anyValue) {
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
