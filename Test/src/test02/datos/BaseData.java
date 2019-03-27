package test02.datos;

import tech.fonrouge.MOODB.MBaseData;

import java.util.Date;

public abstract class BaseData<T extends Base> extends MBaseData<T> {

    /* @@ begin field descriptor @@ */
    public BaseData(T base) {
        super(base);
    }

    public Date getFechaRegistro() {
        return tableState.getFieldValue(table.field_fechaRegistro, Date.class);
    }

    public Date getFecha() {
        return tableState.getFieldValue(table.field_fecha, Date.class);
    }
    /* @@ end field descriptor @@ */
}
