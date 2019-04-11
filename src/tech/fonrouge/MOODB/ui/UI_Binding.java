package tech.fonrouge.MOODB.ui;

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
                String s;
                if (objects.length > 1) {
                    s = objects[1].toString();
                } else {
                    s = "no field info";
                }
                UI_Message.Warning("Bind error:", s + " : " + e.toString());
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

    private void registerControl(MField mField, Node node) {
        nodeHashMap.put(mField.getName(), node);
        node.setDisable(mField.isReadOnly());
    }

    /**
     * @param textField
     * @param fieldTableField
     * @param detailField
     * @param <U>
     */
    @SuppressWarnings("unused")
    protected <U extends MTable> void bindControl(TextField textField, MFieldTableField<U> fieldTableField, String detailField) {
        registerControl(fieldTableField, textField);
        textField.textProperty().addListener(new UI_ChangeListenerTextField(textField, fieldTableField, detailField));
    }

    /**
     * @param comboBox
     * @param fieldTableField
     * @param detailField
     * @param <U>
     */
    @SuppressWarnings("unused")
    final protected <U extends MTable> void bindControl(ComboBox<Object> comboBox, MFieldTableField<U> fieldTableField, String detailField) {
        registerControl(fieldTableField, comboBox);
        comboBox.valueProperty().addListener(new UI_ChangeListenerComboBox<>(comboBox, fieldTableField, detailField));
    }

    /**
     * @param comboBox
     * @param fieldString
     */
    @SuppressWarnings("unused")
    final protected void bindControl(ComboBox<String> comboBox, MFieldString fieldString) {
        registerControl(fieldString, comboBox);
        comboBox.valueProperty().addListener(new UI_ChangeListenerComboBox<>(comboBox, fieldString));
    }

    /**
     * @param textInputControl
     * @param mFieldString
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextInputControl textInputControl, MFieldString mFieldString) {
        registerControl(mFieldString, textInputControl);
        textInputControl.textProperty().addListener(new UI_ChangeListenerTextField(textInputControl, mFieldString));
    }

    /**
     * @param textField
     * @param fieldInteger
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldInteger fieldInteger) {
        registerControl(fieldInteger, textField);
        //textField.textProperty().addListener(new UI_ChangeListenerTextField(textField, fieldInteger));

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
        registerControl(fieldDouble, textField);

        textField.textProperty().set(String.valueOf(fieldDouble.value()));
        textField.textProperty().addListener((observable, oldValue, newValue) -> fieldDouble.setValue(Double.valueOf(newValue)));
        textField.setPromptText(fieldDouble.getDescription());
    }

    /**
     * @param textField
     * @param mFieldDate
     */
    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDate mFieldDate) {
        registerControl(mFieldDate, textField);

        textField.textProperty().set(mFieldDate.valueAsString());
        textField.textProperty().addListener((observable, oldValue, newValue) -> mFieldDate.setValueAsString(newValue));
        textField.setPromptText(mFieldDate.getDescription());
    }

    /**
     * @param integerSpinner
     * @param fieldInteger
     */
    @SuppressWarnings("unused")
    final protected void bindControl(Spinner<Integer> integerSpinner, MFieldInteger fieldInteger) {
        registerControl(fieldInteger, integerSpinner);

        integerSpinner.getValueFactory().setValue(fieldInteger.value());
        integerSpinner.getValueFactory().valueProperty().addListener((observable, oldValue, newValue) -> fieldInteger.setValue(newValue));
    }

    /**
     * @param checkBox
     * @param mFieldBoolean
     */
    @SuppressWarnings("unused")
    final protected void bindControl(CheckBox checkBox, MFieldBoolean mFieldBoolean) {
        registerControl(mFieldBoolean, checkBox);

        checkBox.setSelected(mFieldBoolean.value());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> mFieldBoolean.setValue(newValue));
    }
}
