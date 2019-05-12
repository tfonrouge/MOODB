package tech.fonrouge.MOODB.ui;

import javafx.scene.Node;
import javafx.scene.control.*;
import tech.fonrouge.MOODB.Annotations.AssignWith;
import tech.fonrouge.MOODB.Annotations.NoBindNode;
import tech.fonrouge.MOODB.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class UI_Binding<T extends MTable> {

    @SuppressWarnings("WeakerAccess")
    protected final HashMap<String, Node> nodeHashMap = new HashMap<>();

    T table;

    private void assignFieldWith(Field declaredField, AssignWith assignWith) {
        Field parentField = null;
        try {
            parentField = getClass().getDeclaredField(assignWith.parentNode());

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        Node parentNode = null;
        if (parentField != null) {
            try {
                if (!parentField.isAccessible()) {
                    parentField.setAccessible(true);
                    parentNode = (Node) parentField.get(this);
                    parentField.setAccessible(false);
                } else {
                    parentNode = (Node) parentField.get(this);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String selectorId = assignWith.selectorId();
        Node node = null;
        if (parentNode != null) {
            node = parentNode.lookup("#" + selectorId);
        }
        if (node != null) {
            try {
                if (!declaredField.isAccessible()) {
                    declaredField.setAccessible(true);
                    declaredField.set(this, node);
                    declaredField.setAccessible(false);
                } else {
                    declaredField.set(this, node);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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

    @SuppressWarnings("WeakerAccess")
    protected void bindControls() {
        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {

            AssignWith assignWith = declaredField.getAnnotation(AssignWith.class);

            if (assignWith != null) {
                assignFieldWith(declaredField, assignWith);
            }

            NoBindNode annotation = declaredField.getAnnotation(NoBindNode.class);

            Node node = findNodeByField(declaredField);

            if (node != null && annotation == null) {
                MField mField = getTableField(declaredField);

                if (mField != null) {

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
                    UI_Message.Warning("UI Controller", "Table field not found", "Control: " + declaredField.toString());
                }
            }
        }
    }

    private Node findNodeByField(Field field) {
        if (Node.class.isAssignableFrom(field.getType())) {
            try {
                Node node;
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                    node = (Node) field.get(this);
                    field.setAccessible(false);
                } else {
                    node = (Node) field.get(this);
                }
                return node;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
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

    public T getTable() {
        return table;
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
                UI_Message.Warning("UI Controller", "Bind error:", s + " : " + e.toString());
            }
        } else {
            UI_Message.Warning("UI Controller", "No Binding Method", "Params: " + Arrays.toString(objects));
        }
        return false;
    }

    private void registerControl(Node node, MField mField) {
        nodeHashMap.put(mField.getName(), node);
    }
}
