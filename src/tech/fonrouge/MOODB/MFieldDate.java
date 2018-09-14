package tech.fonrouge.MOODB;

import java.util.Date;

public class MFieldDate extends MField<Date> {

    protected boolean mNewDate;

    protected MFieldDate(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.DATE;
    }

    @Override
    public Date getEmptyValue() {
        return null;
    }

    @Override
    public Date getAsValue(Object anyValue) {
        return null;
    }

    @Override
    public String valueAsString() {
        if (getFieldState().value == null) {
            return "";
        }
        return getFieldState().value.toString();
    }
}
