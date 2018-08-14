package tech.fonrouge.test.data;

import java.util.Date;

public class UserModel extends PersonModel {

    protected User user;

    public void setTable(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user.field_userId.value();
    }

    public String getUserLevel() {
        return user.field_userLevel.value();
    }

    public String getPassword() {
        return user.field_password.value();
    }

    public Date getLastLogin() {
        return user.field_lastLogin.value();
    }

    public Integer getLogCounter() {
        return user.field_logCounter.value();
    }
}
