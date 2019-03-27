package test01.data;

import tech.fonrouge.MOODB.*;

import java.util.Date;
import java.util.HashMap;

public class Inventory extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_itemId = new MFieldString(this, "itemId") {
        @Override
        protected void initialize() {
            required = true;
            description = "Inventory Item Id";
        }
    };
    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            required = true;
            description = "name";
        }
    };
    public final MFieldString field_uom = new MFieldString(this, "uom") {
        @Override
        protected void initialize() {
            required = true;
            description = "Unit of measure";
        }
    };
    public final MFieldDouble field_stock = new MFieldDouble(this, "stock") {
        @Override
        protected void initialize() {
            description = "Stock";
        }
    };
    public final MFieldDouble field_unitPrice = new MFieldDouble(this, "unitPrice") {
        @Override
        protected void initialize() {
            required = true;
            description = "Unit price";
        }
    };
    public final MFieldDate field_date = new MFieldDate(this, "date") {
        @Override
        protected void initialize() {
            calculated = true;
            calcValue = () -> calcField_date();
        }
    };
    public final MFieldBinary field_image = new MFieldBinary(this, "image") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_type = new MFieldString(this, "type") {
        @Override
        protected void initialize() {

            valueItems = new HashMap<>();
            valueItems.put("ANA", "ANA");
        }
    };

    public final MIndex index_itemId = new MIndex(this, "itemId", "", "itemId", true, false) {
        @Override
        protected void initialize() {
        }
    };

    public final MIndex index_name = new MIndex(this, "name", "", "name", false, false) {
        @Override
        protected void initialize() {
        }
    };


    @Override
    public final String getTableName() {
        return "inventory";
    }

    @Override
    public InventoryData getData() {
        return new InventoryData<>(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_date @@ */
    private Date calcField_date() {
        return null;
    }
    /* @@ end calcField_date @@ */
}
