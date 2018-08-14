package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.*;

import java.util.HashMap;

public abstract class Person extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Name";
        }
    };
    public final MFieldString field_firstName = new MFieldString(this, "firstName") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "First name";
        }
    };
    public final MFieldString field_lastName = new MFieldString(this, "lastName") {
        @Override
        protected void initialize() {
            mDescription = "Last name";
        }
    };
    public final MFieldDate field_bday = new MFieldDate(this, "bday") {
        @Override
        protected void initialize() {
            mDescription = "Birth day";
        }
    };
    public final MFieldString field_gender = new MFieldString(this, "gender") {
        @Override
        protected void initialize() {
            mRequired = true;

            mKeyValueItems = new HashMap<>();
            mKeyValueItems.put("F", "Female");
            mKeyValueItems.put("M", "Male");
            mKeyValueItems.put("?", "Undetermined");
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
            mDescription = "Person Id";
        }
    };

    public final MIndex index_personId = new MIndex(this, "personId", "", "personId", false, true);

    /* @@ end field descriptor @@ */
}
