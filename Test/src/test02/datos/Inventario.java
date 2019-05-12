package test02.datos;

import com.mongodb.client.model.Filters;
import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.*;

@AutoGenerated()
public class Inventario extends Base {

    @AutoGenerated()
    public final MFieldString field_nombre = new MFieldString(this, "nombre") {
        @Override
        protected void initialize() {
            required = true;
            description = "Nombre";
        }
    };

    @AutoGenerated()
    public final MFieldString field_udem = new MFieldString(this, "udem") {
        @Override
        protected void initialize() {
            required = true;
        }
    };

    @AutoGenerated()
    public final MFieldString field_tipo = new MFieldString(this, "tipo") {
        @Override
        protected void initialize() {
            required = true;

            valueItems = new ValueItems<>();
            valueItems.put("S", "Servicio");
            valueItems.put("A", "Articulo");
            valueItems.put("G", "Grupo");
        }
    };

    @AutoGenerated()
    public final MFieldInteger field_level = new MFieldInteger(this, "level") {
        @Override
        protected void initialize() {

            valueItems = new ValueItems<>();
            valueItems.put(1, "one");
            valueItems.put(2, "two");
            valueItems.put(3, "three");
        }
    };

    @AutoGenerated()
    public final MFieldDouble field_existencia = new MFieldDouble(this, "existencia") {
        @Override
        protected void initialize() {
            setCallableNewValue(() -> 0.0);
        }
    };

    @AutoGenerated()
    public final MFieldString field_status = new MFieldString(this, "status") {
        @Override
        protected void initialize() {
            required = true;
            setCallableNewValue(() -> "1");
        }
    };

    @AutoGenerated()
    public final MIndex index_tipo_nombre = new MIndex(this, "tipo_nombre", "", "tipo:1,nombre:1", true, false) {
        @Override
        protected void initialize() {
            partialFilter = Filters.and(Filters.eq("tipo", "A"), Filters.gte("nombre", "CILINDRO"));
        }
    };

    @AutoGenerated()
    public final MIndex index_nombre = new MIndex(this, "nombre", "", "nombre", false, false) {
        @Override
        protected void initialize() {
        }
    };

    @Override
    @AutoGenerated()
    public final String getTableName() {
        return "inventoryItem";
    }

    @Override
    @AutoGenerated()
    public InventarioData getData() {
        return new InventarioData<>(this);
    }
}
