package test02.datos;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MTable;

public abstract class Base extends MTable {

    /* @@ begin field descriptor @@ */

    public final MFieldDate field_fecha = new MFieldDate(this, "fecha") {
        @Override
        protected void initialize() {
            required = true;
            mNewDate = true;
        }
    };


    @Override
    protected MDatabase newDatabase() {
        return new Test2Database(this);
    }
    /* @@ end field descriptor @@ */
}
