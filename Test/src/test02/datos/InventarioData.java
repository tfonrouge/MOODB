package test02.datos;

public class InventarioData<T extends Inventario> extends BaseData<T> {

    /* @@ begin field descriptor @@ */
    public InventarioData(T inventario) {
        super(inventario);
    }

    public String getNombre() {
        return tableState.getFieldValue(table.field_nombre, String.class);
    }

    public String getUdem() {
        return tableState.getFieldValue(table.field_udem, String.class);
    }

    public String getTipo() {
        return tableState.getFieldValue(table.field_tipo, String.class);
    }

    public Integer getLevel() {
        return tableState.getFieldValue(table.field_level, Integer.class);
    }

    public Double getExistencia() {
        return tableState.getFieldValue(table.field_existencia, Double.class);
    }

    public String getStatus() {
        return tableState.getFieldValue(table.field_status, String.class);
    }
    /* @@ end field descriptor @@ */
}

