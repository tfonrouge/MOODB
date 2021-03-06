package test01.data.tableBase;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import tech.fonrouge.ui.UI_CtrlList;

public abstract class TableBaseCtrlList<T extends TableBase, U extends TableBaseData<T>> extends UI_CtrlList<T, U> {

    @SuppressWarnings("unused")
    @FXML
    public MenuItem menuItem_view;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_close;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_insert;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_edit;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_delete;

    @Override
    protected String getCtrlFXMLPath() {
        return "/test01/ui/baseList.fxml";
    }

    @Override
    protected void initController() {

        super.initController();

        menuItem_close.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        menuItem_insert.setAccelerator(new KeyCodeCombination(KeyCode.INSERT));
        menuItem_edit.setAccelerator(new KeyCodeCombination(KeyCode.F3));
        menuItem_delete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        menuItem_view.setAccelerator(new KeyCodeCombination(KeyCode.F5));

        menuItem_close.setOnAction(event -> stage.hide());
    }
}
