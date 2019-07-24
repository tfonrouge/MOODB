package tech.fonrouge.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import tech.fonrouge.ui.Annotations.BindCtrlList;

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
            if (UI_CtrlList.currentCtrlList != null) {
                UI_CtrlList.currentCtrlList.injectFieldValue(parent);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    private static void setFieldValue(Field field, Parent parent, UI_CtrlList ui_ctrlList) {
        boolean accesible = field.isAccessible();
        if (!accesible) {
            field.setAccessible(true);
        }
        try {
            field.set(parent, ui_ctrlList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(accesible);
        }
    }
}
