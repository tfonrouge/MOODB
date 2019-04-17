package test01.data.person;

import test01.data.tableBase.TableBaseData;

import java.util.Date;

public abstract class PersonData<T extends Person> extends TableBaseData<T> {

    /* @@ begin field descriptor @@ */
    public PersonData(T person) {
        super(person);
    }

    public String getName() {
        return tableState.getFieldValue(table.field_name, String.class);
    }

    public String getFirstName() {
        return tableState.getFieldValue(table.field_firstName, String.class);
    }

    public String getLastName() {
        return tableState.getFieldValue(table.field_lastName, String.class);
    }

    public Date getBday() {
        return tableState.getFieldValue(table.field_bday, Date.class);
    }

    public String getGender() {
        return tableState.getFieldValue(table.field_gender, String.class);
    }

    public Boolean getMarried() {
        return tableState.getFieldValue(table.field_married, Boolean.class);
    }

    public String getPersonId() {
        return tableState.getFieldValue(table.field_personId, String.class);
    }
    /* @@ end field descriptor @@ */
}

