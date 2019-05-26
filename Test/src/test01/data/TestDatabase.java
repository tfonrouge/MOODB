package test01.data;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MTable;

public class TestDatabase extends MDatabase {

    public TestDatabase(MTable oTable) {
        super(oTable);
    }

    @Override
    public String getAuthSource() {
        return "";
    }

    @Override
    public String getDatabaseName() {
        return "test01";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public int getPortNumber() {
        return 27017;
    }

    @Override
    public String getServerName() {
        return "localhost";
    }

    @Override
    public String getUserName() {
        return "";
    }

}
