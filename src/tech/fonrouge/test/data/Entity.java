package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;

public abstract class Entity extends Base {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_name = new MFieldString(this, "name") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "Name";
        }
    };
    public final MFieldString field_taxId = new MFieldString(this, "taxId") {
        @Override
        protected void initialize() {
            mDescription = "Tax payer id";
        }
    };
    public final MFieldString field_country = new MFieldString(this, "country") {
        @Override
        protected void initialize() {
            mDescription = "Country id";
        }
    };
    public final MFieldString field_address = new MFieldString(this, "address") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_phone1 = new MFieldString(this, "phone1") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_phone2 = new MFieldString(this, "phone2") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_phone3 = new MFieldString(this, "phone3") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_webPage = new MFieldString(this, "webPage") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_email1 = new MFieldString(this, "email1") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_email2 = new MFieldString(this, "email2") {
        @Override
        protected void initialize() {
        }
    };
    public final MFieldString field_email3 = new MFieldString(this, "email3") {
        @Override
        protected void initialize() {
        }
    };

    public final MIndex index_taxId = new MIndex(this, "taxId", "", "taxId", false, true);

    /* @@ end field descriptor @@ */
}
