package test01.data;

import org.bson.types.Binary;

import java.util.Date;

public class UserData<T extends User> extends PersonData<T> {

    /* @@ begin field descriptor @@ */
    public UserData(T user) {
        super(user);
    }

    public String getUserId() {
        return tableState.getFieldValue(table.field_userId, String.class);
    }

    public String getUserLevel() {
        return tableState.getFieldValue(table.field_userLevel, String.class);
    }

    public String getPassword() {
        return tableState.getFieldValue(table.field_password, String.class);
    }

    public Date getLastLogin() {
        return tableState.getFieldValue(table.field_lastLogin, Date.class);
    }

    public Integer getLogCounter() {
        return tableState.getFieldValue(table.field_logCounter, Integer.class);
    }
    /* @@ end field descriptor @@ */
}

