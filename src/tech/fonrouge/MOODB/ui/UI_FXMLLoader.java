package tech.fonrouge.MOODB.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import tech.fonrouge.MOODB.Annotations.BindCtrlList;

import java.io.IOException;
import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class UI_FXMLLoader {

    @SuppressWarnings("unused")
    public static void fxmlLoadBindCtrlList(FXMLLoader fxmlLoader) {
        try {
            Parent controller = fxmlLoader.load();
            if (UI_CtrlList.currentCtrlList != null) {
                setControllerMember(controller, UI_CtrlList.currentCtrlList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void fxmlLoadBindCtrlList(FXMLLoader fxmlLoader, Parent parent) {
        try {
            if (UI_CtrlList.currentCtrlList != null) {
                setControllerMember(parent, UI_CtrlList.currentCtrlList);
            }
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = parent.getId();
        if (id != null && !id.isEmpty()) {
            injectFieldValue(UI_CtrlList.currentCtrlList, parent, id);
        }
    }

    private static void injectFieldValue(UI_CtrlList ui_ctrlList, Parent parent, String id) {
        Class<?> clazz = ui_ctrlList.getClass();
        while (clazz != UI_CtrlList.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().contentEquals(id) && field.getType().isAssignableFrom(parent.getClass())) {
                    setFieldValue(field, ui_ctrlList, parent);
                    return;
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static void setControllerMember(Parent parent, UI_CtrlList ui_ctrlList) {
        Field field = null;
        for (Field declaredField : parent.getClass().getDeclaredFields()) {
            BindCtrlList annotation = declaredField.getAnnotation(BindCtrlList.class);
            if (annotation != null && UI_CtrlList.class.isAssignableFrom(declaredField.getType())) {
                field = declaredField;
                break;
            }
        }
        if (field != null) {
            setFieldValue(field, parent, ui_ctrlList);
        }
    }

    private static void setFieldValue(Field field, Object obj, Object value) {
        boolean accesible = field.isAccessible();
        if (!accesible) {
            field.setAccessible(true);
        }
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(accesible);
        }
    }
}
