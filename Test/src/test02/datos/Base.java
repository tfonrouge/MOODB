package test02.datos;

import org.bson.types.ObjectId;
import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MTable;

import java.util.Date;

public abstract class Base extends MTable {

    /* @@ begin field descriptor @@ */

    public final MFieldDate field_fechaRegistro = new MFieldDate(this, "fechaRegistro") {
        @Override
        protected void initialize() {
            calculated = true;
            calcValue = () -> calcField_fechaRegistro();
        }
    };

    public final MFieldDate field_fecha = new MFieldDate(this, "fecha") {
        @Override
        protected void initialize() {
            required = true;
        }
    };


    @Override
    protected Class getMDatabaseClass() {
        return Test2Database.class;
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_fechaRegistro @@ */
    private Date calcField_fechaRegistro() {
        Object o = field__id.value();
        if (o instanceof ObjectId) {
            return ((ObjectId) o).getDate();
        }
        return null;
    }
    /* @@ end calcField_fechaRegistro @@ */
}
