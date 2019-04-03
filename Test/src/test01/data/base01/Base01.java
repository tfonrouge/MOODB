package test01.data.base01;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MTable;
import test01.data.TestDatabase;

public abstract class Base01 extends MTable {

    /* @@ begin field descriptor @@ */

    @Override
    protected MDatabase newDatabase() {
        return new TestDatabase(this);
    }
    /* @@ end field descriptor @@ */
}
