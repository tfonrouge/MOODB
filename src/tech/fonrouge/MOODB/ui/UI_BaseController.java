package tech.fonrouge.MOODB.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import tech.fonrouge.MOODB.MTable;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public abstract class UI_BaseController {

    protected abstract String getBaseResourcePath();

    private Constructor<?> getCtorClass(Class<?> tableClass) {
        Constructor<?> constructor;
        String className;

        if (tableClass.equals(MTable.class)) {
            return null;
        }

        className = tableClass.getName() + "CtrlList";

        try {
            constructor = Class.forName(className).getConstructor(tableClass);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return getCtorClass(tableClass.getSuperclass());
        }
        return constructor;
    }

    @SuppressWarnings("WeakerAccess")
    public void showList(MTable table) {
        UI_CtrlList ui_ctrlList = null;

        try {
            Constructor<?> constructor = getCtorClass(table.getClass());
            if (constructor != null) {
                ui_ctrlList = (UI_CtrlList) constructor.newInstance(table);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            UI_Message.Warning("Warning", e.toString());
        }

        if (ui_ctrlList != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getBaseResourcePath()));
            loader.setController(ui_ctrlList);
            Parent parent = null;
            try {
                parent = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                UI_Message.Warning("Warning", e.toString());
            }
            if (parent != null) {
                ui_ctrlList.showWindow(parent);
            }
        }
    }
}
