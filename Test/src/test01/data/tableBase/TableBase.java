package test01.data.tableBase;

import tech.fonrouge.MOODB.MTable;
import test01.data.TestDatabase;

public abstract class TableBase extends MTable {

    /* @@ begin field descriptor @@ */

    @Override
    protected Class getMDatabaseClass() {
        return TestDatabase.class;
    }
    /* @@ end field descriptor @@ */
}
