package tech.fonrouge.MOODB;

public abstract class MBaseData<T extends MTable> {

    protected final T table;
    protected TableState tableState;

    public MBaseData(T base) {
        table = base;
        tableState = new TableState(table.tableState);
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
