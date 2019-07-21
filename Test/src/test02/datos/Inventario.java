package test02.datos;

import com.mongodb.client.model.Filters;
import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.*;

@AutoGenerated()
public class Inventario extends Base {

    /**
     * field_nombre
     */
    @AutoGenerated()
    public final MFieldString field_nombre = new MFieldString(this, "nombre") {
        @Override
        protected void initialize() {
            description = "Nombre";
        }
    };

    /**
     * field_udem
     */
    @AutoGenerated()
    public final MFieldString field_udem = new MFieldString(this, "udem") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_tipo
     */
    @AutoGenerated()
    public final MFieldString field_tipo = new MFieldString(this, "tipo") {
        @Override
        protected void initialize() {

            valueItems = new ValueItems<>();
            valueItems.put("S", "Servicio");
            valueItems.put("A", "Articulo");
            valueItems.put("G", "Grupo");
        }
    };

    /**
     * field_level
     */
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

    /**
     * field_existencia
     */
    @AutoGenerated()
    public final MFieldDouble field_existencia = new MFieldDouble(this, "existencia") {
        @Override
        protected void initialize() {
            setCallableNewValue(() -> 0.0);
        }
    };

    /**
     * field_status
     */
    @AutoGenerated()
    public final MFieldString field_status = new MFieldString(this, "status") {
        @Override
        protected void initialize() {
            setCallableNewValue(() -> "1");
        }
    };

    /**
     * index_tipo_nombre
     */
    @AutoGenerated()
    public final MIndex index_tipo_nombre = new MIndex(this, "tipo_nombre", "", "tipo:1,nombre:1", true, false, null, null) {
        @Override
        protected void initialize() {
            partialFilter = Filters.and(Filters.eq("tipo", "A"), Filters.gte("nombre", "CILINDRO"));        }
    };

    /**
     * index_nombre
     */
    @AutoGenerated()
    public final MIndex index_nombre = new MIndex(this, "nombre", "", "nombre", false, false, null, null) {
        @Override
        protected void initialize() {
        }
    };

    /**
     * getTableName
     */
    @Override
    @AutoGenerated()
    public final String getTableName() {
        return "inventoryItem";
    }

    /**
     * getData
     */
    @Override
    @AutoGenerated()
    public InventarioData getData() {
        return new InventarioData<>(this);
    }
}
