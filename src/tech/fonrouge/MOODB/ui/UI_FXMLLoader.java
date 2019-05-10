package tech.fonrouge.MOODB.ui;

import javafx.fxml.FXMLLoader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;

public class UI_FXMLLoader {

    public static void fxmlLoadBindCtrlList(FXMLLoader fxmlLoader) {
        try {
            Object controller = fxmlLoader.load();
            UI_CtrlList last_ui_ctrlList;
            if (UI_CtrlList.currentCtrlList != null) {
                last_ui_ctrlList = UI_CtrlList.currentCtrlList;
                setControllerMember(controller, last_ui_ctrlList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Boolean fxmlHasFXController(URL resource) {
        try {
            InputStream inputStream = new FileInputStream(resource.getFile());
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            while (xmlStreamReader.hasNext()) {
                int next = xmlStreamReader.next();
                if (next == XMLStreamConstants.START_ELEMENT) {
                    return xmlStreamReader.getAttributeValue(xmlStreamReader.getNamespaceURI("fx"), "controller") != null;
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
            UI_Message.Error("UI_CtrlList Error", "FXML not found or not valid.", e.toString());
        }
        return null;
    }

    private static void setControllerMember(Object controller, UI_CtrlList last_ui_ctrlList) {
        Field field = null;
        for (Field declaredField : controller.getClass().getDeclaredFields()) {
            if (UI_CtrlList.class.isAssignableFrom(declaredField.getType())) {
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
