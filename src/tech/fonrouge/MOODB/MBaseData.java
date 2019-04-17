package tech.fonrouge.MOODB;

public abstract class MBaseData<T extends MTable> {

    protected final T table;
    protected TableState tableState;

    public MBaseData(T base) {
        table = base;
        tableState = new TableState(table.tableState);
    }

    public MTable _getTable() {
        return table;
    }

    public TableState _getTableState() {
        return tableState;
    }

    public Object get_id() {
        return tableState.getFieldValue(table.field__id, Object.class);
    }
}
