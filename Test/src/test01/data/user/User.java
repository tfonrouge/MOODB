package test01.data.user;

import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.*;
import test01.data.person.Person;

@AutoGenerated()
public class User extends Person {

    /* @@ begin field descriptor @@ */
    @AutoGenerated()
    public final MFieldString field_userId = new MFieldString(this, "userId") {
        @Override
        protected void initialize() {
            notNull = true;
            description = "User Id";
        }
    };

    @AutoGenerated()
    public final MFieldString field_userLevel = new MFieldString(this, "userLevel") {
        @Override
        protected void initialize() {
            description = "User level";

            valueItems = new ValueItems<>();
            valueItems.put("1", "Admin User");
            valueItems.put("0", "General User");
        }
    };

    @AutoGenerated()
    public final MFieldString field_password = new MFieldString(this, "password") {
        @Override
        protected void initialize() {
            notNull = true;
            description = "User password";
        }
    };

    @AutoGenerated()
    public final MFieldDate field_lastLogin = new MFieldDate(this, "lastLogin") {
        @Override
        protected void initialize() {
            description = "Last date login";
        }
    };

    @AutoGenerated()
    public final MFieldInteger field_logCounter = new MFieldInteger(this, "logCounter") {
        @Override
        protected void initialize() {
            description = "Login counter";
        }
    };

    @AutoGenerated()
    public final MIndex index_userId = new MIndex(this, "userId", "", "userId", true, false) {
        @Override
        protected void initialize() {
        }
    };

    @Override
    @AutoGenerated()
    public final String getTableName() {
        return "users";
    }

    @Override
    @AutoGenerated()
    public UserData getData() {
        return new UserData<>(this);
    }
}
