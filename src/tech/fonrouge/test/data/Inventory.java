package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.*;

import java.util.Date;

public class Inventory extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_itemId = new MFieldString(this, "itemId") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Inventory Item Id";
        }
    };
    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "name";
        }
    };
    public final MFieldString field_uom = new MFieldString(this, "uom") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Unit of measure";
        }
    };
    public final MFieldDouble field_stock = new MFieldDouble(this, "stock") {
        @Override
        protected void initialize() {
            mDescription = "Stock";
        }
    };
    public final MFieldDouble field_unitPrice = new MFieldDouble(this, "unitPrice") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Unit price";
        }
    };
    public final MFieldDate field_date = new MFieldDate(this, "date") {
        @Override
        protected void initialize() {
            mCalculated = true;
            calcValue = () -> calcField_date();
        }
    };
    public final MFieldBinary field_image = new MFieldBinary(this, "image") {
        @Override
        protected void initialize() {
        }
    };

    public final MIndex index_itemId = new MIndex(this, "itemId", "", "itemId", false, true);
    public final MIndex index_name = new MIndex(this, "name", "", "name", false, false);

    private InventoryModel m;

    @Override
    public final String getTableName() {
        return "inventory";
    }

    @Override
    protected void initializeModel() {
        m = new InventoryModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_date @@ */
    private Date calcField_date() {
        return null;
    }
    /* @@ end calcField_date @@ */
}
