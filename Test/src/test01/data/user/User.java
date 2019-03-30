package test01.data.user;

import tech.fonrouge.MOODB.MFieldDate;
import tech.fonrouge.MOODB.MFieldInteger;
import tech.fonrouge.MOODB.MFieldString;
import tech.fonrouge.MOODB.MIndex;
import test01.data.person.Person;

import java.util.HashMap;

public class User extends Person {

    /* @@ begin field descriptor @@ */

    public final MFieldString field_userId = new MFieldString(this, "userId") {
        @Override
        protected void initialize() {
            required = true;
            description = "User Id";
        }
    };
    public final MFieldString field_userLevel = new MFieldString(this, "userLevel") {
        @Override
        protected void initialize() {
            description = "User level";

            valueItems = new HashMap<>();
            valueItems.put("1", "Admin User");
            valueItems.put("0", "General User");
        }
    };
    public final MFieldString field_password = new MFieldString(this, "password") {
        @Override
        protected void initialize() {
            required = true;
            description = "User password";
        }
    };
    public final MFieldDate field_lastLogin = new MFieldDate(this, "lastLogin") {
        @Override
        protected void initialize() {
            description = "Last date login";
        }
    };
    public final MFieldInteger field_logCounter = new MFieldInteger(this, "logCounter") {
        @Override
        protected void initialize() {
            description = "Login counter";
        }
    };

    public final MIndex index_userId = new MIndex(this, "userId", "", "userId", true, false) {
        @Override
        protected void initialize() {
        }
    };


    @Override
    public final String getTableName() {
        return "users";
    }

    @Override
    public UserData getData() {
        return new UserData<>(this);
    }
    /* @@ end field descriptor @@ */
}