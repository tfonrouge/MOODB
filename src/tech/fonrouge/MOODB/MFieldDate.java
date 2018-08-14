package tech.fonrouge.MOODB;

import java.util.Date;

public class MFieldDate extends MField<Date> {

    protected MFieldDate(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Date emptyValue() {
        return null;
    }
}
