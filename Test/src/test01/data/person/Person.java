package test01.data.person;

import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.*;
import test01.data.tableBase.TableBase;

@AutoGenerated()
public abstract class Person extends TableBase {

    /**
     * field_name
     */
    @AutoGenerated()
    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            description = "Name";
        }
    };

    /**
     * field_firstName
     */
    @AutoGenerated()
    public final MFieldString field_firstName = new MFieldString(this, "firstName") {
        @Override
        protected void initialize() {
            description = "First name";
        }
    };

    /**
     * field_lastName
     */
    @AutoGenerated()
    public final MFieldString field_lastName = new MFieldString(this, "lastName") {
        @Override
        protected void initialize() {
            description = "Last name";
        }
    };

    /**
     * field_bday
     */
    @AutoGenerated()
    public final MFieldDate field_bday = new MFieldDate(this, "bday") {
        @Override
        protected void initialize() {
            description = "Birth day";
        }
    };

    /**
     * field_gender
     */
    @AutoGenerated()
    public final MFieldString field_gender = new MFieldString(this, "gender") {
        @Override
        protected void initialize() {

            valueItems = new ValueItems<>();
            valueItems.put("?", "Undetermined");
            valueItems.put("M", "Male");
            valueItems.put("F", "Female");
        }
    };

    /**
     * field_married
     */
    @AutoGenerated()
    public final MFieldBoolean field_married = new MFieldBoolean(this, "married") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_personId
     */
    @AutoGenerated()
    public final MFieldString field_personId = new MFieldString(this, "personId") {
        @Override
        protected void initialize() {
            description = "Person Id";
        }
    };

    /**
     * index_personId
     */
    @AutoGenerated()
    public final MIndex index_personId = new MIndex(this, "personId", "", "personId", true, false, null, null) {
        @Override
        protected void initialize() {
        }
    };
}
