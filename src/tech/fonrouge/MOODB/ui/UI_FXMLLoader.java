package tech.fonrouge.MOODB.ui;

import javafx.fxml.FXMLLoader;
import tech.fonrouge.MOODB.Annotations.BindCtrlList;

import java.io.IOException;
import java.lang.reflect.Field;

public class UI_FXMLLoader {

    @SuppressWarnings("unused")
    public static void fxmlLoadBindCtrlList(FXMLLoader fxmlLoader) {
        try {
            Object controller = fxmlLoader.load();
            if (UI_CtrlList.currentCtrlList != null) {
                setControllerMember(controller, UI_CtrlList.currentCtrlList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public static void fxmlLoadBindCtrlList(FXMLLoader fxmlLoader, Object controller) {
        try {
            if (UI_CtrlList.currentCtrlList != null) {
                setControllerMember(controller, UI_CtrlList.currentCtrlList);
            }
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setControllerMember(Object controller, UI_CtrlList last_ui_ctrlList) {
        Field field = null;
        for (Field declaredField : controller.getClass().getDeclaredFields()) {
            BindCtrlList annotation = declaredField.getAnnotation(BindCtrlList.class);
            if (annotation != null && UI_CtrlList.class.isAssignableFrom(declaredField.getType())) {
                field = declaredField;
                break;
            }
        }
        if (field != null) {
            try {
                boolean accesible = field.isAccessible();
                if (!accesible) {
                    field.setAccessible(true);
                }
                field.set(controller, last_ui_ctrlList);
                if (!accesible) {
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
