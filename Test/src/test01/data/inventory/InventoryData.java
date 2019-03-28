package test01.data.inventory;

import org.bson.types.Binary;
import test01.data.base01.Base01Data;

import java.util.Date;

public class InventoryData<T extends Inventory> extends Base01Data<T> {

    /* @@ begin field descriptor @@ */
    public InventoryData(T inventory) {
        super(inventory);
    }

    public String getItemId() {
        return tableState.getFieldValue(table.field_itemId, String.class);
    }

    public String getName() {
        return tableState.getFieldValue(table.field_name, String.class);
    }

    public String getUom() {
        return tableState.getFieldValue(table.field_uom, String.class);
    }

    public Double getStock() {
        return tableState.getFieldValue(table.field_stock, Double.class);
    }

    public Double getUnitPrice() {
        return tableState.getFieldValue(table.field_unitPrice, Double.class);
    }

    public Date getDate() {
        return tableState.getFieldValue(table.field_date, Date.class);
    }

    public Binary getImage() {
        return tableState.getFieldValue(table.field_image, Binary.class);
    }

    public String getType() {
        return tableState.getFieldValue(table.field_type, String.class);
    }
    /* @@ end field descriptor @@ */
}

