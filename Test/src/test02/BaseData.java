package test02;

import javafx.beans.property.SimpleStringProperty;
import test02.datos.Base;

public abstract class BaseData {

    private SimpleStringProperty fecha;

    public <S> BaseData(Base base) {
        fecha = new SimpleStringProperty(base.field_fecha.value().toString());
    }

    public String getFecha() {
        return fecha.get();
    }

    public void setFecha(String fecha) {
        this.fecha.set(fecha);
    }
}
