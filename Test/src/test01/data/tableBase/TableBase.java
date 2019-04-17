package test01.data.tableBase;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MTable;
import test01.data.TestDatabase;

public abstract class TableBase extends MTable {

    /* @@ begin field descriptor @@ */

    @Override
    protected MDatabase newDatabase() {
        return new TestDatabase(this);
    }
    /* @@ end field descriptor @@ */
}
