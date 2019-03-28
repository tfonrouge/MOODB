package tech.fonrouge.MOODB.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

@SuppressWarnings("unused")
public abstract class UI_BaseController {

    protected abstract String getBaseResourcePath();

    public void showList(UI_CtrlList baseCtrlList) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getBaseResourcePath()));
        loader.setController(baseCtrlList);
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (parent != null) {
            baseCtrlList.buildUI(parent);
        }
    }
}
