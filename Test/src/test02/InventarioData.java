package test02;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class InventarioData extends BaseData {

    private SimpleStringProperty nombre;
    private SimpleStringProperty udem;
    private SimpleDoubleProperty existencia;

    public InventarioData(Inventario inventario) {
        super(inventario);
        nombre = new SimpleStringProperty(inventario.field_nombre.value());
        udem = new SimpleStringProperty(inventario.field_udem.value());
        existencia = new SimpleDoubleProperty(inventario.field_existencia.value());
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getUdem() {
        return udem.get();
    }

    public void setUdem(String udem) {
        this.udem.set(udem);
    }

    public double getExistencia() {
        return existencia.get();
    }

    public void setExistencia(double existencia) {
        this.existencia.set(existencia);
    }
}
