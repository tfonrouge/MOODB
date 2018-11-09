package test01.data;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MTable;

public abstract class Base extends MTable {
    public Base() {
    }

    public Base(MTable masterSource) {
        super(masterSource);
    }

    /* @@ begin field descriptor @@ */


    @Override
    protected MDatabase newDatabase() {
        return new TestDatabase(this);
    }
    /* @@ end field descriptor @@ */
}
