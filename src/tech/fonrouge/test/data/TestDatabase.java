package tech.fonrouge.test.data;

import tech.fonrouge.MOODB.MTable;
import tech.fonrouge.MOODB.MDatabase;

public class TestDatabase extends MDatabase {

    public TestDatabase(MTable oTable) {
        super(oTable);
    }

    @Override
    protected void defineRelations() {

    }

    @Override
    public String getAuthSource() {
        return "";
    }

    @Override
    public String getDatabaseName() {
        return "test";
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
