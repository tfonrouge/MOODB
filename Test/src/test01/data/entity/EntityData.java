package test01.data.entity;

public abstract class EntityData<T extends Entity> extends tech.fonrouge.MOODB.MBaseData<T> {

    /* @@ begin field descriptor @@ */
    public EntityData(T entity) {
        super(entity);
    }

    public String getName() {
        return tableState.getFieldValue(table.field_name, String.class);
    }

    public String getTaxId() {
        return tableState.getFieldValue(table.field_taxId, String.class);
    }

    public String getCountry() {
        return tableState.getFieldValue(table.field_country, String.class);
    }

    public String getAddress() {
        return tableState.getFieldValue(table.field_address, String.class);
    }

    public String getPhone1() {
        return tableState.getFieldValue(table.field_phone1, String.class);
    }

    public String getPhone2() {
        return tableState.getFieldValue(table.field_phone2, String.class);
    }

    public String getPhone3() {
        return tableState.getFieldValue(table.field_phone3, String.class);
    }

    public String getWebPage() {
        return tableState.getFieldValue(table.field_webPage, String.class);
    }

    public String getEmail1() {
        return tableState.getFieldValue(table.field_email1, String.class);
    }

    public String getEmail2() {
        return tableState.getFieldValue(table.field_email2, String.class);
    }

    public String getEmail3() {
        return tableState.getFieldValue(table.field_email3, String.class);
    }
    /* @@ end field descriptor @@ */
}
