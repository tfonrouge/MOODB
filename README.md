# MOODB

MOODB is a ORM framework that allows to simplify Data Modeling when writting Java applications that use the capabilities of the NoSQL MongoDB engine by automatically generating code to describe container classes, field descriptors, indices, and constraints defined on a .XML file for each collection.

For example, the following meta-descriptor XML:

```xml
  <oons:Table extends="Base" tableName="inventoryItem">

    <oons:Fields>

        <oons:FieldString name="itemId" description="Inventory Item Id" required="true"/>
        <oons:FieldString name="name" description="name" required="true"/>
        <oons:FieldString name="uom" description="Unit of measure" required="true"/>
        <oons:FieldDouble name="stock" description="Stock"/>
        <oons:FieldDouble name="unitPrice" description="Unit price" required="true"/>
        <oons:FieldDate name="date" calculated="true"/>
        <oons:FieldBinary name="image"/>

    </oons:Fields>

    <oons:Index name="itemId" keyField="itemId" unique="true"/>
    <oons:Index name="name" keyField="name"/>

  </oons:Table>

```

Creates the following Java class:

```java
package tech.fonrouge.test01.data;

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
        return inventoryItem;
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
```

And then you can use this Java class in the form:

```java

    Inventory inventoryItem = new Inventory();
    
    if (inventoryItem.insert()) {
        inventoryItem.field_itemId.setValue("9198798734");
        inventoryItem.field_name.setValue("Mouse");
        inventoryItem.field_stock.setValue(50.0);
        inventoryItem.field_uom.setValue("pz");
        inventoryItem.field_unitPrice.setValue(2.74);
        if (!inventoryItem.post()) {
            inventoryItem.cancel();
        }
    }
    
    if (inventoryItem.field_name.find("Mouse")) {
        ...
    }

```
