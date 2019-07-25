package tech.fonrouge.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tech.fonrouge.MOODB.*;
import tech.fonrouge.ui.Annotations.AssignWith;
import tech.fonrouge.ui.Annotations.NoBindNode;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static tech.fonrouge.ui.Annotations.BindField;

public abstract class UI_CtrlBase<T extends MTable> {

    protected final List<UI_ChangeListener0> uiChangeListener0s = new ArrayList<>();
    protected final HashMap<String, Node> nodeHashMap = new HashMap<>();
    public T table = null;
    protected Stage stage;
    protected Scene scene;
    Parent parent;
    @SuppressWarnings("WeakerAccess")
    URL fxmlResourcePath;
    FXMLLoader fxmlLoader;
    boolean fxmlHasController = false;

    private static Constructor<?> getCtorClass(Class<?> tableClass, String suffix) {
        Constructor<?> constructor;
        String className;

        if (tableClass.equals(MTable.class)) {
            return null;
        }

        className = tableClass.getName() + suffix;

        try {
            constructor = Class.forName(className).getConstructor();
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return getCtorClass(tableClass.getSuperclass(), suffix);
        }
        return constructor;
    }

    static UI_CtrlBase getUIController(MTable table, String ctrlFXMLPath, String suffix) {

        UI_CtrlBase ui_ctrl = null;
        Constructor<?> constructor;

        if (ctrlFXMLPath != null) {
            String classPrefix = table.getClass().getName().substring(0, table.getClass().getName().lastIndexOf("."));
            String ctrlName = classPrefix + "." + ctrlFXMLPath.substring(0, 1).toUpperCase() + ctrlFXMLPath.substring(1, ctrlFXMLPath.lastIndexOf("."));
            try {
                constructor = Class.forName(ctrlName).getConstructor();
                ui_ctrl = (UI_CtrlBase) constructor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ignored) {

            }
        } else {
            try {
                constructor = getCtorClass(table.getClass(), suffix);
                if (constructor != null) {
                    ui_ctrl = (UI_CtrlBase) constructor.newInstance();
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                UI_Message.error("UI Controller Error", "Warning", e.toString());
            }
        }

        if (ui_ctrl != null) {
            if (ctrlFXMLPath == null) {
                ctrlFXMLPath = ui_ctrl.getCtrlFXMLPath();
            }
            URL resource = ui_ctrl.getClass().getResource(ctrlFXMLPath);
            if (resource != null) {
                ui_ctrl.table = table;
                ui_ctrl.fxmlResourcePath = resource;
                ui_ctrl.fxmlHasController = fxmlHasFXController(resource);
                ui_ctrl.fxmlLoader = new FXMLLoader(resource);
                return ui_ctrl;
            } else {
                UI_Message.error("UI Controller Error", "No FXML resource found.", "define FXML resource.");
            }
        } else {
            UI_Message.error("UI Controller Error", "No controller found.", "define controller for table.");
        }

        return null;
    }

    private static boolean fxmlHasFXController(URL fxmlPath) {
        InputStream inputStream = null;
        try {
            inputStream = fxmlPath.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            UI_Message.error("UI_CtrlList Error", "FXML URL not valid.", e.toString());
        }
        if (inputStream != null) {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            try {
                XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
                while (xmlStreamReader.hasNext()) {
                    int next = xmlStreamReader.next();
                    if (next == XMLStreamConstants.START_ELEMENT) {
                        return xmlStreamReader.getAttributeValue(xmlStreamReader.getNamespaceURI("fx"), "controller") != null;
                    }
                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
                UI_Message.error("UI_CtrlList Error", "FXML not valid.", e.toString());
            }
        }
        return false;
    }

    @SuppressWarnings("unused")
    public Stage getStage() {
        return stage;
    }

    UI_CtrlBase getFXMLLoaderController() {
        UI_CtrlBase ui_ctrlBase = fxmlLoader.getController();
        ui_ctrlBase.parent = parent;
        ui_ctrlBase.table = table;
        return ui_ctrlBase;
    }

    protected abstract String getCtrlFXMLPath();

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
        uiChangeListener0s.add(new UI_ChangeListenerTextInputControl<>(textField, fieldTableField.syncedTable().fieldByName(detailField)));
    }

    @SuppressWarnings("unused")
    final protected <U extends MTable> void bindControl(ComboBox<Object> comboBox, MFieldTableField<U> fieldTableField, String detailField) {
        uiChangeListener0s.add(new UI_ChangeListenerComboBox<>(comboBox, fieldTableField.syncedTable().fieldByName(detailField)));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(ComboBox<String> comboBox, MFieldString fieldString) {
        uiChangeListener0s.add(new UI_ChangeListenerComboBox<>(comboBox, fieldString));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextInputControl textInputControl, MFieldString mFieldString) {
        uiChangeListener0s.add(new UI_ChangeListenerTextInputControl<>(textInputControl, mFieldString));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldInteger fieldInteger) {
        uiChangeListener0s.add(new UI_ChangeListenerTextInputControl<>(textField, fieldInteger));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDouble fieldDouble) {
        uiChangeListener0s.add(new UI_ChangeListenerTextInputControl<>(textField, fieldDouble));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(TextField textField, MFieldDate mFieldDate) {
        uiChangeListener0s.add(new UI_ChangeListenerTextInputControl<>(textField, mFieldDate));
    }

    @SuppressWarnings("unused")
    public void bindControl(Spinner<Integer> integerSpinner, MFieldInteger fieldInteger) {
        uiChangeListener0s.add(new UI_ChangeListenerSpinnerInteger(integerSpinner, fieldInteger));
    }

    @SuppressWarnings("unused")
    final protected void bindControl(CheckBox checkBox, MFieldBoolean mFieldBoolean) {
        uiChangeListener0s.add(new UI_ChangeListenerCheckBox(checkBox, mFieldBoolean));
    }

    @SuppressWarnings("WeakerAccess")
    protected void bindControls() {
        Field[] declaredFields = getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {

            AssignWith assignWith = declaredField.getAnnotation(AssignWith.class);

            if (assignWith != null) {
                assignFieldWith(declaredField, assignWith);
            }

            NoBindNode noBindNode = declaredField.getAnnotation(NoBindNode.class);

            Node node = findNodeByField(declaredField);

            if (node != null && noBindNode == null) {

                Field fieldMField = null;
                try {
                    fieldMField = node.getClass().getDeclaredField("mField");
                } catch (NoSuchFieldException ignored) {

                }
                BindField bindField = declaredField.getAnnotation(BindField.class);
                if (fieldMField == null || !fieldMField.getType().isAssignableFrom(MField.class)) {

                    MField mField;

                    if (bindField == null) {
                        mField = getTableField(declaredField);
                    } else {
                        if (bindField.fieldName().contains(".")) {
                            mField = table.fieldByName(bindField.fieldName().substring(0, bindField.fieldName().indexOf(".")));
                        } else {
                            mField = table.fieldByName(bindField.fieldName());
                        }
                    }

                    if (mField != null) {

                        Method method;
                        Boolean invoquedOk = null;

                        if (mField.fieldType == MTable.FIELD_TYPE.TABLE_FIELD) {
                            String subFieldName;
                            if (bindField == null) {
                                String name = declaredField.getName();
                                subFieldName = name.substring(name.indexOf(mField.getName()) + mField.getName().length());
                            } else {
                                subFieldName = bindField.fieldName().substring(bindField.fieldName().indexOf("."));
                            }
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
                        UI_Message.warning("UI Controller", "Table field not found", "Control: " + declaredField.toString());
                    }
                } else {
                    if (bindField != null) {
                        MField<Integer> mField = table.fieldByName(bindField.fieldName());
                        if (mField != null) {
                            UITextFieldInteger uiTextFieldInteger = (UITextFieldInteger) node;
                            uiTextFieldInteger.setMField(mField);
                        }
                    }
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
        if (!clazz.equals(UI_CtrlBase.class)) {
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

    protected void initController() {

    }

    @SuppressWarnings("WeakerAccess")
    protected void initStage() {
        stage = new Stage();
        scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle(table.getGenres());
        initController();
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
                UI_Message.warning("UI Controller", "Bind error:", s + " : " + e.toString());
            }
        } else {
            UI_Message.warning("UI Controller", "No Binding Method", "Params: " + Arrays.toString(objects));
        }
        return false;
    }

    private void registerControl(Node node, MField mField) {
        nodeHashMap.put(mField.getName(), node);
    }
}
