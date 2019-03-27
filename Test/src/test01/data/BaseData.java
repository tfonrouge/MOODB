package test01.data;

import tech.fonrouge.MOODB.MBaseData;
import tech.fonrouge.MOODB.MTable;

public abstract class BaseData<T extends Base> extends MBaseData<T> {

    /* @@ begin field descriptor @@ */
    public BaseData(T base) {
        super(base);
    }
    /* @@ end field descriptor @@ */
}

