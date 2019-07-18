package test01.data.entity;

import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;
import test01.data.tableBase.TableBase;
import tech.fonrouge.MOODB.*;

@AutoGenerated()
public abstract class Entity extends TableBase {

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
     * field_taxId
     */
    @AutoGenerated()
    public final MFieldString field_taxId = new MFieldString(this, "taxId") {
        @Override
        protected void initialize() {
            description = "Tax payer id";
        }
    };

    /**
     * field_country
     */
    @AutoGenerated()
    public final MFieldString field_country = new MFieldString(this, "country") {
        @Override
        protected void initialize() {
            description = "Country id";
        }
    };

    /**
     * field_address
     */
    @AutoGenerated()
    public final MFieldString field_address = new MFieldString(this, "address") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_phone1
     */
    @AutoGenerated()
    public final MFieldString field_phone1 = new MFieldString(this, "phone1") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_phone2
     */
    @AutoGenerated()
    public final MFieldString field_phone2 = new MFieldString(this, "phone2") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_phone3
     */
    @AutoGenerated()
    public final MFieldString field_phone3 = new MFieldString(this, "phone3") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_webPage
     */
    @AutoGenerated()
    public final MFieldString field_webPage = new MFieldString(this, "webPage") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_email1
     */
    @AutoGenerated()
    public final MFieldString field_email1 = new MFieldString(this, "email1") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_email2
     */
    @AutoGenerated()
    public final MFieldString field_email2 = new MFieldString(this, "email2") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * field_email3
     */
    @AutoGenerated()
    public final MFieldString field_email3 = new MFieldString(this, "email3") {
        @Override
        protected void initialize() {
        }
    };

    /**
     * index_taxId
     */
    @AutoGenerated()
    public final MIndex index_taxId = new MIndex(this, "taxId", "", "taxId", true, false) {
        @Override
        protected void initialize() {
        }
    };

    /* @@ end field descriptor @@ */
    protected final Integer counter = 1;
}
