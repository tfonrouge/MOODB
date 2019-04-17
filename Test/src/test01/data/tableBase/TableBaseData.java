package test01.data.tableBase;

import tech.fonrouge.MOODB.MBaseData;

public abstract class TableBaseData<T extends TableBase> extends MBaseData<T> {

    /* @@ begin field descriptor @@ */
    public TableBaseData(T tablebase) {
        super(tablebase);
    }
    /* @@ end field descriptor @@ */
}

