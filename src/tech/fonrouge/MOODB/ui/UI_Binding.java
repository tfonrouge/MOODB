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

    private boolean invokeMethod(Method method, Object... objects) {
        if (method != null) {
            try {
                method.invoke(this, objects);
                return true;
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
        return false;
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
                    Boolean invoquedOk = null;

                    if (mField.fieldType == MTable.FIELD_TYPE.TABLE_FIELD) {
                        String name = declaredField.getName();
                        String subFieldName = name.substring(name.indexOf(mField.getName()) + mField.getName().length());
                        if (!subFieldName.isEmpty()) {
                            method = getBindControlMethod(getClass(), node.getClass(), mField.getClass().getSuperclass(), String.class);
                            invoquedOk = invokeMethod(method, node, mField, subFieldName.substring(1));
                        }
                    } else {
                        method = getBindControlMethod(getClass(), node.getClass(), mField.getClass().getSuperclass());
                        invoquedOk = invokeMethod(method, node, mField);
                    }
                    if (invoquedOk != null && invoquedOk) {
                        registerControl(node, mField);
                    }
                } else {
                    UI_Message.Warning("Control not valid", "Control: " + declaredField.toString());
                }
            } else {
                UI_Message.Warning("Table field not found", "Control: " + declaredField.toString());
            }
        }
    }

    private void registerControl(Node node, MField mField) {
        nodeHashMap.put(mField.getName(), node);
        node.setDisable(mField.isReadOnly());
    }

    @SuppressWarnings("unused")
    protected <U extends MTable> void bindControl(TextField textField, MFieldTableField<U> fieldTableField, String detailField) {
        new UI_ChangeListenerTextInputControl<>(textField, fieldTableField.syncedTable().fieldByName(detailField));
    }

    @SuppressWarnings("unused")
    final protected <U extends MTable> void bindControl(ComboBox<Object> comboBox, MFieldTableField<U> fieldTableField, String detailField) {
        new UI_ChangeListenerComboBox<>(comboBox, fieldTableField.syncedTable().fieldByName(detailField));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(ComboBox<String> comboBox, MFieldString fieldString) {
        new UI_ChangeListenerComboBox<>(comboBox, fieldString);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextInputControl textInputControl, MFieldString mFieldString) {
        new UI_ChangeListenerTextInputControl<>(textInputControl, mFieldString);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldInteger fieldInteger) {
        new UI_ChangeListenerTextInputControl<>(textField, fieldInteger);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDouble fieldDouble) {
        new UI_ChangeListenerTextInputControl<>(textField, fieldDouble);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDate mFieldDate) {
        new UI_ChangeListenerTextInputControl<>(textField, mFieldDate);
    }

    @SuppressWarnings("unused")
    public void bindControl(Spinner<Integer> integerSpinner, MFieldInteger fieldInteger) {
        new UI_ChangeListenerSpinnerInteger(integerSpinner, fieldInteger);
    }

    @SuppressWarnings("unused")
    final protected void bindControl(CheckBox checkBox, MFieldBoolean mFieldBoolean) {
        new UI_ChangeListenerCheckBox(checkBox, mFieldBoolean);
    }
}
