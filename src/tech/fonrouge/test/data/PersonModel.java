package tech.fonrouge.test.data;

import java.util.Date;

public abstract class PersonModel extends BaseModel {

    protected Person person;

    public void setTable(Person person) {
        this.person = person;
    }

    public String getName() {
        return person.field_name.value();
    }

    public String getFirstName() {
        return person.field_firstName.value();
    }

    public String getLastName() {
        return person.field_lastName.value();
    }

    public Date getBday() {
        return person.field_bday.value();
    }

    public String getGender() {
        return person.field_gender.value();
    }

    public Boolean getMarried() {
        return person.field_married.value();
    }

    public String getPersonId() {
        return person.field_personId.value();
    }
}
