package test02.datos;

import com.mongodb.client.model.Filters;
import tech.fonrouge.MOODB.MFieldDouble;
import tech.fonrouge.MOODB.MFieldInteger;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;

import java.util.HashMap;

public class Inventario extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_nombre = new MFieldString(this, "nombre") {
        @Override
        protected void initialize() {
            required = true;
            description = "Nombre";
        }
    };
    public final MFieldString field_udem = new MFieldString(this, "udem") {
        @Override
        protected void initialize() {
            required = true;
        }
    };
    public final MFieldString field_tipo = new MFieldString(this, "tipo") {
        @Override
        protected void initialize() {
            required = true;

            valueItems = new HashMap<>();
            valueItems.put("S", "Servicio");
            valueItems.put("A", "Articulo");
            valueItems.put("G", "Grupo");
        }
    };
    public final MFieldInteger field_level = new MFieldInteger(this, "level") {
        @Override
        protected void initialize() {

            valueItems = new HashMap<>();
            valueItems.put(1, "one");
            valueItems.put(2, "two");
            valueItems.put(3, "three");
        }
    };
    public final MFieldDouble field_existencia = new MFieldDouble(this, "existencia") {
        @Override
        protected void initialize() {
            setCallableNewValue(() -> 0.0);
        }
    };
    public final MFieldString field_status = new MFieldString(this, "status") {
        @Override
        protected void initialize() {
            required = true;
            setCallableNewValue(() -> "1");
        }
    };

    public final MIndex index_tipo_nombre = new MIndex(this, "tipo_nombre", "", "tipo:1,nombre:1", true, false) {
        @Override
        protected void initialize() {
            partialFilter = Filters.and(Filters.eq("tipo", "A"), Filters.gte("nombre", "CILINDRO"));
        }
    };

    public final MIndex index_nombre = new MIndex(this, "nombre", "", "nombre", false, false) {
        @Override
        protected void initialize() {
        }
    };


    @Override
    public final String getTableName() {
        return "inventory";
    }

    @Override
    public InventarioData getData() {
        return new InventarioData<>(this);
    }
    /* @@ end field descriptor @@ */

}
