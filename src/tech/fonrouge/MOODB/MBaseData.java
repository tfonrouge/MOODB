package tech.fonrouge.MOODB;

public abstract class MBaseData<T extends MTable> {

    protected final T table;
    protected TableState tableState;

    public MBaseData(T base) {
        table = base;
        try {
            tableState = table.tableState.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public MTable getTable() {
        return table;
    }

    public TableState getTableState() {
        return tableState;
    }

    public Object get_id() {
        return tableState.getFieldValue(table.field__id, Object.class);
    }
}
