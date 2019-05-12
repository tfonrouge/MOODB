package test01.data.tableBase;

import tech.fonrouge.MOODB.Annotations.AutoGenerated;
import tech.fonrouge.MOODB.MTable;
import test01.data.TestDatabase;

@AutoGenerated()
public abstract class TableBase extends MTable {

    @AutoGenerated()
    @Override()
    public Class getMDatabaseClass() {
        return TestDatabase.class;
    }
}
