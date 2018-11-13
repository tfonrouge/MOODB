package tech.fonrouge.MOODB;

import java.util.HashMap;

public abstract class MBaseData {

    protected final MTable table;
    private HashMap<String, DataValueItem> fieldValues;

    public MBaseData(MTable base) {

        table = base;
        fieldValues = new HashMap<>();

        populateFieldValues();

    }

    private void populateFieldValues() {
        table.getFieldListStream().forEach(mField -> {
            fieldValues.put(mField.getName(), new DataValueItem(mField));
        });
    }

    public DataValueItem getDataValueItem(String fieldName) {
        return fieldValues.get(fieldName);
    }

    public Object fieldValue(String fieldName) {
        return fieldValues.get(fieldName).getValue();
    }

    public Object get_id() {
        return fieldValue("_id");
    }

    public class DataValueItem {
        private Object value;
        private boolean isTableField;
        private MBaseData MBaseData;
        private String label;
        private boolean isCalculated;

        DataValueItem(MField mField) {
            isCalculated = mField.isCalculated();
            value = mField.value();
            isTableField = mField instanceof MFieldTableField;
            label = mField.getLabel();
            if (isTableField) {
                MFieldTableField mFieldTableField = (MFieldTableField) mField;
                MTable base = mFieldTableField.getLinkedTable();
                MBaseData = base.getData();
            }
        }

        public boolean isCalculated() {
            return isCalculated;
        }

        public String getLabel() {
            return label;
        }

        public Object getValue() {
            return value;
        }

        public boolean isTableField() {
            return isTableField;
        }

        public MBaseData getMBaseData() {
            if (value == null || !value.equals(MBaseData.get_id())) {
                MBaseData.table.field__id.find(value);
                MBaseData.populateFieldValues();
            }
            return MBaseData;
        }
    }
}
