package test01.data.person;

import tech.fonrouge.MOODB.MFieldBoolean;
import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;
import test01.data.tableBase.TableBase;

import java.util.HashMap;

public abstract class Person extends TableBase {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            required = true;
            description = "Name";
        }
    };

    public final MFieldString field_firstName = new MFieldString(this, "firstName") {
        @Override
        protected void initialize() {
            required = true;
            description = "First name";
        }
    };

    public final MFieldString field_lastName = new MFieldString(this, "lastName") {
        @Override
        protected void initialize() {
            description = "Last name";
        }
    };

    public final MFieldDate field_bday = new MFieldDate(this, "bday") {
        @Override
        protected void initialize() {
            description = "Birth day";
        }
    };

    public final MFieldString field_gender = new MFieldString(this, "gender") {
        @Override
        protected void initialize() {
            required = true;

            valueItems = new HashMap<>();
            valueItems.put("?", "Undetermined");
            valueItems.put("M", "Male");
            valueItems.put("F", "Female");
        }
    };

    public final MFieldBoolean field_married = new MFieldBoolean(this, "married") {
        @Override
        protected void initialize() {
        }
    };

    public final MFieldString field_personId = new MFieldString(this, "personId") {
        @Override
        protected void initialize() {
            description = "Person Id";
        }
    };

    public final MIndex index_personId = new MIndex(this, "personId", "", "personId", true, false) {
        @Override
        protected void initialize() {
        }
    };
    /* @@ end field descriptor @@ */
}
