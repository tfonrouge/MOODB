package tech.fonrouge.test.data;

import org.bson.types.Binary;

import java.util.Date;

public class InventoryModel extends BaseModel {

    protected Inventory inventory;

    public void setTable(Inventory inventory) {
        this.inventory = inventory;
    }

    public String getItemId() {
        return inventory.field_itemId.value();
    }

    public String getName() {
        return inventory.field_name.value();
    }

    public String getUom() {
        return inventory.field_uom.value();
    }

    public Double getStock() {
        return inventory.field_stock.value();
    }

    public Double getUnitPrice() {
        return inventory.field_unitPrice.value();
    }

    public Date getDate() {
        return inventory.field_date.value();
    }

    public Binary getImage() {
        return inventory.field_image.value();
    }
}
