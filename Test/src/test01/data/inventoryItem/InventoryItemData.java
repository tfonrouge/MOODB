package test01.data.inventoryItem;

import org.bson.types.Binary;
import test01.data.tableBase.TableBaseData;

import java.util.Date;

public class InventoryItemData<T extends InventoryItem> extends TableBaseData<T> {

    /* @@ begin field descriptor @@ */
    public InventoryItemData(T inventoryitem) {
        super(inventoryitem);
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

    public Boolean getTaxable() {
        return tableState.getFieldValue(table.field_taxable, Boolean.class);
    }
    /* @@ end field descriptor @@ */
}

