package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MFieldInteger;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;

import java.util.HashMap;

public class User extends Person {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_userId = new MFieldString(this, "userId") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "User Id";
        }
    };
    public final MFieldString field_userLevel = new MFieldString(this, "userLevel") {
        @Override
        protected void initialize() {
            mDescription = "User level";

            mKeyValueItems = new HashMap<>();
            mKeyValueItems.put("0", "General user");
            mKeyValueItems.put("1", "Admin user");
        }
    };
    public final MFieldString field_password = new MFieldString(this, "password") {
        @Override
        protected void initialize() {
            mRequired = true;
            mDescription = "User password";
        }
    };
    public final MFieldDate field_lastLogin = new MFieldDate(this, "lastLogin") {
        @Override
        protected void initialize() {
            mDescription = "Last date login";
        }
    };
    public final MFieldInteger field_logCounter = new MFieldInteger(this, "logCounter") {
        @Override
        protected void initialize() {
            mDescription = "Login counter";
        }
    };

    public final MIndex index_userId = new MIndex(this, "userId", "", "userId", false, true);

    private UserModel m;

    @Override
    public final String getTableName() {
        return "users";
    }

    @Override
    protected void initializeModel() {
        m = new UserModel();
        m.setTable(this);
    }
    /* @@ end field descriptor @@ */
}
