package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MTable;

import java.util.Date;

public abstract class Base extends MTable {

    /* @@ begin field descriptor @@ */

    public final MFieldDate field_dateId = new MFieldDate(this, "dateId") {
        @Override
        protected void initialize() {
            mCalculated = true;
            calcValue = () -> calcField_dateId();
        }
    };


    @Override
    protected MDatabase newDatabase() {
        return new TestDatabase(this);
    }
    /* @@ end field descriptor @@ */

    /* @@ begin calcField_dateId @@ */
    private Date calcField_dateId() {
        return field__id.value().getDate();
    }
    /* @@ end calcField_dateId @@ */
}
