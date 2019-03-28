package test01.data.base;

import tech.fonrouge.MOODB.MBaseData;

public abstract class Base01Data<T extends Base01> extends MBaseData<T> {

    /* @@ begin field descriptor @@ */
    public Base01Data(T base01) {
        super(base01);
    }
    /* @@ end field descriptor @@ */
}

