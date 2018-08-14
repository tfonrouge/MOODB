package tech.fonrouge.test.data;

public abstract class EntityModel extends BaseModel {

    protected Entity entity;

    public void setTable(Entity entity) {
        this.entity = entity;
    }

    public String getName() {
        return entity.field_name.value();
    }

    public String getTaxId() {
        return entity.field_taxId.value();
    }

    public String getCountry() {
        return entity.field_country.value();
    }

    public String getAddress() {
        return entity.field_address.value();
    }

    public String getPhone1() {
        return entity.field_phone1.value();
    }

    public String getPhone2() {
        return entity.field_phone2.value();
    }

    public String getPhone3() {
        return entity.field_phone3.value();
    }

    public String getWebPage() {
        return entity.field_webPage.value();
    }

    public String getEmail1() {
        return entity.field_email1.value();
    }

    public String getEmail2() {
        return entity.field_email2.value();
    }

    public String getEmail3() {
        return entity.field_email3.value();
    }
}
