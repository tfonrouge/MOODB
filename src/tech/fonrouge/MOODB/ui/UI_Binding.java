package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import tech.fonrouge.MOODB.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class UI_Binding<T extends MTable> {

    @SuppressWarnings("WeakerAccess")
    protected final HashMap<String, Node> nodeHashMap = new HashMap<>();
    protected T table;

    private Node findControl(Field field) {
        if (Node.class.isAssignableFrom(field.getType())) {
            try {
                return (Node) field.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private MField getTableField(Field field) {
        String full = field.getName();
        if (full.contains("_")) {
            String name = full.substring(full.indexOf("_") + 1);
            if (name.contains("_")) {
                int beginIndex = 0;
                while (name.contains("_")) {
                    int endIndex = name.indexOf("_");
                    name = full.substring(full.indexOf("_") + 1).substring(beginIndex, endIndex);
                    MField mField = table.fieldByName(name);
                    if (mField != null) {
                        return mField;
                    }
                    beginIndex = endIndex;
                }
            } else {
                return table.fieldByName(name);
            }
        }
        return null;
    }

    private Method getBindControlMethod(Class<?> clazz, Class<?>... clazzArg) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().contentEquals("bindControl") && method.getParameterCount() == clazzArg.length) {
                int i = 0;
                boolean match = true;
                for (Class<?> parameterType : method.getParameterTypes()) {
                    if (!parameterType.isAssignableFrom(clazzArg[i++])) {
                        match = false;
                    }
                }
                if (match) {
                    return method;
                }
            }
        }
        if (!clazz.equals(UI_Binding.class)) {
            return getBindControlMethod(clazz.getSuperclass(), clazzArg);
        }
        return null;
    }

    private void invokeMethod(Method method, Object... objects) {
        if (method != null) {
            try {
                method.invoke(this, objects);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            UI_Message.Warning("No Binding Method", "Params: " + Arrays.toString(objects));
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void bindControls() {
        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {

            Node node = findControl(declaredField);
            MField mField = getTableField(declaredField);

            if (mField != null) {
                if (node != null) {

                    Method method;

                    if (mField.fieldType == MTable.FIELD_TYPE.TABLE_FIELD) {
                        String name = declaredField.getName();
                        String subFieldName = name.substring(name.indexOf(mField.getName()) + mField.getName().length());
                        if (!subFieldName.isEmpty()) {
                            method = getBindControlMethod(getClass(), node.getClass(), mField.getClass().getSuperclass(), String.class);
                            invokeMethod(method, node, mField, subFieldName.substring(1));
                        }
                    } else {
                        method = getBindControlMethod(getClass(), node.getClass(), mField.getClass().getSuperclass());
                        invokeMethod(method, node, mField);
                    }
                } else {
                    UI_Message.Warning("Control not valid", "Control: " + declaredField.toString());
                }
            } else {
                UI_Message.Warning("Table field not found", "Control: " + declaredField.toString());
            }
        }
    }

    /**
     * @param textField
     * @param fieldTableField
     * @param fieldByName
     * @param <U>
     */
    @SuppressWarnings("unused")
    protected <U extends MTable> void bindControl(TextField textField, MFieldTableField<U> fieldTableField, String fieldByName) {
        nodeHashMap.put(fieldTableField.getName(), textField);
        final boolean[] ignore = {false};
        MField mField = fieldTableField.linkedTable().fieldByName(fieldByName);
        textField.setEditable(!mField.isReadOnly());
        String value = fieldTableField.syncedTable().fieldByName(fieldByName).valueAsString();
        textField.textProperty().setValue(value);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignore[0]) {
                if (!mField.find(newValue)) {
                    ignore[0] = true;
                    Platform.runLater(() -> {
                        textField.textProperty().setValue(oldValue);
                        ignore[0] = false;
                    });
                }
            }
        });
        textField.setPromptText(fieldTableField.getDescription());
    }

    /**
     * @param comboBox
     * @param fieldTableField
     * @param detailField
     * @param <U>
     */
    @SuppressWarnings("unused")
    final protected <U extends MTable> void bindControl(ComboBox<Object> comboBox, MFieldTableField<U> fieldTableField, String detailField) {
        nodeHashMap.put(fieldTableField.getName(), comboBox);
        final boolean[] ignore = {false};
        MField mField = fieldTableField.linkedTable().fieldByName(detailField);
        Object a = fieldTableField.syncedTable().fieldByName(detailField).value();
        comboBox.valueProperty().setValue(a);
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignore[0]) {
                if (!mField.find(newValue)) {
                    ignore[0] = true;
                    Platform.runLater(() -> {
                        comboBox.valueProperty().setValue(oldValue);
                        ignore[0] = false;
                    });
                }
            }
        });
        fillComboBoxList(comboBox, fieldTableField, detailField);
        comboBox.setPromptText(fieldTableField.getDescription());
    }

    /**
     * @param comboBox
     * @param fieldTableField
     * @param detailField
     * @param <U>
     */
    private <U extends MTable> void fillComboBoxList(ComboBox<Object> comboBox, MFieldTableField<U> fieldTableField, String detailField) {
        ObservableList<Object> o = FXCollections.observableArrayList();
        U mTable = fieldTableField.linkedTable();
        mTable.tableStatePush();
        MField mField = mTable.fieldByName(detailField);
        if (mField != null && mTable.find()) {
            while (!mTable.getEof()) {
                Object value = mField.value();
                o.add(value);
                mTable.next();
            }
        }
        mTable.tableStatePull();
        comboBox.setItems(o);
    }

    /**
     * @param comboBox
     * @param fieldString
     */
    @SuppressWarnings("unused")
    final protected void bindControl(ComboBox<String> comboBox, MFieldString fieldString) {
        nodeHashMap.put(fieldString.getName(), comboBox);
        final boolean[] ignore = {false};
        comboBox.valueProperty().setValue(fieldString.valueAsString());
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignore[0]) {
                if (!fieldString.setValue(newValue)) {
                    ignore[0] = true;
                    Platform.runLater(() -> {
                        comboBox.valueProperty().setValue(oldValue);
                        ignore[0] = false;
                    });
                }
            }
        });
        comboBox.setItems(FXCollections.observableArrayList());
        HashMap<String, String> keyValueItems = fieldString.getValueItems();
        if (keyValueItems != null) {
            keyValueItems.forEach((key, value) -> comboBox.getItems().add(key));
        }
        comboBox.setPromptText(fieldString.getDescription());
    }

    /**
     * @param textField
     * @param fieldInteger
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldInteger fieldInteger) {
        nodeHashMap.put(fieldInteger.getName(), textField);
        textField.setEditable(!fieldInteger.isReadOnly());

        textField.textProperty().set(String.valueOf(fieldInteger.value()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldInteger.setValue(Integer.valueOf(newValue)));
        textField.setPromptText(fieldInteger.getDescription());
    }

    /**
     * @param textField
     * @param fieldDouble
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDouble fieldDouble) {
        nodeHashMap.put(fieldDouble.getName(), textField);

        textField.textProperty().set(String.valueOf(fieldDouble.value()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldDouble.setValue(Double.valueOf(newValue)));
        textField.setPromptText(fieldDouble.getDescription());
    }

    /**
     * @param textField
     * @param fieldString
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextInputControl textField, MFieldString fieldString) {
        nodeHashMap.put(fieldString.getName(), textField);

        textField.textProperty().set(fieldString.value());
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldString.setValue(newValue));
        textField.setPromptText(fieldString.getDescription());
    }

    /**
     * @param integerSpinner
     * @param fieldInteger
     */
    @SuppressWarnings("unused")
    final protected void bindControl(Spinner<Integer> integerSpinner, MFieldInteger fieldInteger) {
        nodeHashMap.put(fieldInteger.getName(), integerSpinner);

        integerSpinner.getValueFactory().setValue(fieldInteger.value());
        integerSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> fieldInteger.setValue(newValue));
    }

    /**
     * @param checkBox
     * @param mFieldBoolean
     */
    @SuppressWarnings("unused")
    final protected void bindControl(CheckBox checkBox, MFieldBoolean mFieldBoolean) {
        nodeHashMap.put(mFieldBoolean.getName(), checkBox);

        checkBox.setSelected(mFieldBoolean.value());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> mFieldBoolean.setValue(newValue));
    }

    /**
     * @param textField
     * @param mFieldDate
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDate mFieldDate) {
        nodeHashMap.put(mFieldDate.getName(), textField);

        textField.textProperty().set(mFieldDate.valueAsString());
        textField.textProperty().addListener((observable, oldValue, newValue) -> mFieldDate.setValueAsString(newValue));
        textField.setPromptText(mFieldDate.getDescription());
    }
}
