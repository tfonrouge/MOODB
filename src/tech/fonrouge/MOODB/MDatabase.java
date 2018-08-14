package tech.fonrouge.MOODB;

public abstract class MDatabase {

    private MTable table;

    public MDatabase(MTable oTable) {
        this.table = oTable;
        defineRelations();
    }

    public MTable getTable() {
        return table;
    }

    public final String getDatabaseURI() {
        String uri = "mongodb://";

        if (getUserName().isEmpty()) {
            uri += getServerName();
        } else {
            uri += getUserName() + ":" + getPassword() + "@" + getServerName();
        }
        if (getPortNumber() != 0) {
            uri += ":" + getPortNumber();
        }
        if (!getAuthSource().isEmpty()) {
            uri += "/?authSource=" + getAuthSource();
        }
        return uri;
    }

    protected abstract void defineRelations();

    public abstract String getAuthSource();

    public abstract String getDatabaseName();

    public abstract String getUserName();

    public abstract String getPassword();

    public abstract String getServerName();

    public abstract int getPortNumber();

}
