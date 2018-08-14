package tech.fonrouge.test.data;

import java.util.Date;

public abstract class BaseModel {

    protected Base base;

    public void setTable(Base base) {
        this.base = base;
    }

    public Date getDateId() {
        return base.field_dateId.value();
    }
}
