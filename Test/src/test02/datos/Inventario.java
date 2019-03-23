package test02.datos;

import com.mongodb.client.model.Filters;
import tech.fonrouge.MOODB.*;

import java.util.ArrayList;
import java.util.Collections;
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
            setNewValue(0.0);
        }
    };
    public final MFieldString field_status = new MFieldString(this, "status") {
        @Override
        protected void initialize() {
            required = true;
            setNewValue("1");
            setDefaultValue("2");
        }
    };

    public final MIndex index_tipo_nombre = new MIndex(this, "tipo_nombre", "", "tipo:1,nombre:1", true, false) {
        @Override
        protected void initialize() {
            partialFilter = Filters.and(Filters.eq("tipo", "A"), Filters.gte("nombre", "CILINDRO"));
        }

        public boolean find(String tipo, String nombre) {
            return super.find(tipo, nombre);
        }
    };
    public final MIndex index_nombre = new MIndex(this, "nombre", "", "nombre", false, false) {
        @Override
        protected void initialize() {
        }

        public boolean find(String nombre) {
            return super.find(new ArrayList<>(Collections.singletonList(nombre)));
        }
    };

    @Override
    public final String getTableName() {
        return "inventory";
    }
    /* @@ end field descriptor @@ */

    @Override
    public MBaseData getData() {
        return null;
    }
}
