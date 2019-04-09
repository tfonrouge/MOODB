package tech.fonrouge.MOODB;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class MFieldDate extends MField<Date> {

    protected MFieldDate(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    protected MTable.FIELD_TYPE getFieldType() {
        return MTable.FIELD_TYPE.DATE;
    }

    @Override
    public boolean setValueAsString(String value) {
        Date date = Date.from(LocalDateTime.parse(value).atZone(ZoneId.systemDefault()).toInstant());
        return super.setValue(date);
    }

    @Override
    protected Date getTypedValue() {
        return table.tableState.getFieldValue(this, Date.class);
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
        Date value = value();
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
