package test01.data.user;

import tech.fonrouge.ui.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.*;
import test01.data.person.Person;

@AutoGenerated()
public class User extends Person {

    /**
     * field_userId
     */
    @AutoGenerated()
    public final MFieldString field_userId = new MFieldString(this, "userId") {
        @Override
        protected void initialize() {
            description = "User Id";
        }
    };

    /**
     * field_userLevel
     */
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

    /**
     * field_password
     */
    @AutoGenerated()
    public final MFieldString field_password = new MFieldString(this, "password") {
        @Override
        protected void initialize() {
            description = "User password";
        }
    };

    /**
     * field_lastLogin
     */
    @AutoGenerated()
    public final MFieldDate field_lastLogin = new MFieldDate(this, "lastLogin") {
        @Override
        protected void initialize() {
            description = "Last date login";
        }
    };

    /**
     * field_logCounter
     */
    @AutoGenerated()
    public final MFieldInteger field_logCounter = new MFieldInteger(this, "logCounter") {
        @Override
        protected void initialize() {
            description = "Login counter";
        }
    };

    /**
     * index_userId
     */
    @AutoGenerated()
    public final MIndex index_userId = new MIndex(this, "userId", "", "userId", true, false, null, null) {
        @Override
        protected void initialize() {
        }
    };

    /**
     * getTableName
     */
    @Override
    @AutoGenerated()
    public final String getTableName() {
        return "users";
    }

    /**
     * getData
     */
    @Override
    @AutoGenerated()
    public UserData getData() {
        return new UserData<>(this);
    }
}
