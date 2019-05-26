package test02.datos;

import tech.fonrouge.MOODB.MDatabase;
import tech.fonrouge.MOODB.MTable;

public class Test2Database extends MDatabase {

    public Test2Database(MTable oTable) {
        super(oTable);
    }

    @Override
    public String getAuthSource() {
        return "";
    }

    @Override
    public String getDatabaseName() {
        return "test02";
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
