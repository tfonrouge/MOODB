package tech.fonrouge.ui;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.bson.Document;
import tech.fonrouge.MOODB.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class UI_CtrlList<T extends MTable, U extends MBaseData<T>> extends UI_CtrlBase<T> {

    @SuppressWarnings("WeakerAccess")
    public static UI_CtrlList currentCtrlList = null;
    private static Method autosizeColumnMethod = null;
    private static int numTimers = 0;
    @SuppressWarnings("unused")
    @FXML
    protected TableView<U> tableView;
    /**
     * refresh lapse for tableView in seconds
     */
    private int refreshLapse = 3;
    private boolean populatingList = false;
    private Timeline refreshTimer;
    private Runnable findMethod = () -> table.find();
    private boolean columnsAutoSized = false;

    @SuppressWarnings("unused")
    static public UI_CtrlList ctrlList(MTable table) {
        return ctrlList(table, null, null);
    }

    @SuppressWarnings("unused")
    static public UI_CtrlList ctrlList(MTable table, UI_CtrlList ui_ctrlList) {
        return ctrlList(table, ui_ctrlList, null);
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    static public UI_CtrlList ctrlList(MTable table, UI_CtrlList ui_ctrlList, String ctrlListFXMLPath) {

        ui_ctrlList = (UI_CtrlList) UI_CtrlList.getUIController(table, ui_ctrlList, ctrlListFXMLPath, "CtrlList");

        if (ui_ctrlList != null) {

            Class<?> uiCtrlListClass = ui_ctrlList.getClass();

            UI_CtrlList finalUi_ctrlList = ui_ctrlList;
            ui_ctrlList.fxmlLoader.setControllerFactory(param -> {
                try {
                    if (param.isAssignableFrom(uiCtrlListClass)) {
                        return finalUi_ctrlList;
                    }
                    return param.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    UI_Message.error("UI_CtrlList Error", "Warning", e.toString());
                }
                return null;
            });

            if (!ui_ctrlList.fxmlHasController) {
                ui_ctrlList.fxmlLoader.setController(ui_ctrlList);
            }

            try {
                currentCtrlList = ui_ctrlList;
                ui_ctrlList.parent = ui_ctrlList.fxmlLoader.load();
                if (ui_ctrlList.fxmlHasController) {
                    ui_ctrlList = (UI_CtrlList) ui_ctrlList.getFXMLLoaderController();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                UI_Message.error("UI_CtrlList Error", "Warning", e.toString());
            } finally {
                currentCtrlList = null;
            }

            if (ui_ctrlList.parent != null) {
                ui_ctrlList.initStage();
                return ui_ctrlList;
            }
        }

        return null;
    }

    private TableColumn<U, ?> buildColumn(String fieldExpression) {

        if (fieldExpression.indexOf('.') > 0) {
            String[] fieldList = fieldExpression.split("\\.");
            if (fieldList.length > 1) {
                TableColumn<U, String> column;
                column = new TableColumn<>();
                column.setCellValueFactory(param -> {

                    String label = "";
                    String value = "";

                    MField mField = param.getValue()._getTable().fieldByName(fieldList[0]);

                    if (mField != null) {

                        MTable table = mField.getTable();

                        TableState tableState = table.getTableState();
                        table.setTableState(param.getValue()._getTableState());

                        MFieldTableField mFieldTableField = table.fieldTableFieldByName(fieldList[0]);

                        if (mFieldTableField != null) {
                            Document document = param.getValue()._getTableState().getFieldStateDocument(mFieldTableField.index);
                            if (document != null) {
                                label = mFieldTableField.getLabel();
                                int i = 1;
                                do {
                                    mField = mFieldTableField.syncedTable().fieldByName(fieldList[i]);
                                    if (mField != null) {
                                        if (mField instanceof MFieldTableField) {
                                            mFieldTableField = (MFieldTableField) mField;
                                        } else {
                                            mFieldTableField = null;
                                        }
                                        label += " " + mField.getLabel();
                                        if (mField.isCalculated()) {
                                            value = mField.valueAsString();
                                        } else {
                                            value = document.get(mField.getName()).toString();
                                        }
                                    } else {
                                        //label = "!" + fieldExpression + "!";
                                        //value = "!error!";
                                        break;
                                    }
                                    document = document.get("@" + mField.getName(), Document.class);
                                } while (mFieldTableField != null && document != null && ++i < fieldList.length);
                            }
                        } else {
                            if (mField.getValueItems() != null) {
                                if (fieldList[1].equalsIgnoreCase("value")) {
                                    label = mField.getLabel();
                                    value = mField.getLabelOfValue();
                                }
                            }
                        }
                        table.setTableState(tableState);
                    }

                    column.setText(label);
                    return new ReadOnlyObjectWrapper<>(value);
                });
                return column;
            }
        } else {
            MField mField = table.fieldByName(fieldExpression);
            if (mField != null) {
                TableColumn<U, ?> column;
                column = new TableColumn<>(mField.getLabel());
                column.setCellValueFactory(new PropertyValueFactory<>(mField.getName()));
                return column;
            }
        }

        return new TableColumn<>(fieldExpression);
    }

    @SuppressWarnings("WeakerAccess")
    protected void buildColumns() {

        for (String fieldExpression : getFieldColumnList()) {

            TableColumn<U, ?> column = buildColumn(fieldExpression);
            column.setId(fieldExpression);

            tableView.getColumns().add(column);

        }
    }

    @SuppressWarnings("WeakerAccess")
    protected T buildTable() {
        return null;
    }

    private void buildTableView() {

        if (tableView != null) {

            tableView.getColumns().clear();

            tableView.setMaxHeight(Control.USE_COMPUTED_SIZE);

            buildColumns();

            refreshTimerStart();

            tableView.sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (oldScene == null && newScene != null) {
                    Scene scene = tableView.getScene();
                    scene.windowProperty().addListener((observable1, oldWindow, newWindow) -> {
                        if (oldWindow == null && newWindow != null) {
                            newWindow.addEventHandler(WindowEvent.WINDOW_SHOWN, windowEvent -> populateList());
                            newWindow.addEventHandler(WindowEvent.WINDOW_HIDDEN, windowEvent -> refreshTimerStop());
                        }
                    });
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public void close() {
        if (stage != null) {
            stage.hide();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean findSelectedDocument() {
        U item = tableView.getSelectionModel().getSelectedItem();
        if (item != null) {
            return table.field__id.find(item.get_id());
        }
        return false;
    }

    @SuppressWarnings("unused")
    public <V extends Parent> V fxmlLoadBindCtrlList(String url) {
        V parent = null;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));
        fxmlLoader.setController(this);
        try {
            parent = fxmlLoader.load();
            injectFieldValue(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parent;
    }

    protected String[] getFieldColumnList() {
        List<String> strings = new ArrayList<>();

        table.getFieldListStream().forEach(mField -> strings.add(mField.getName()));

        return strings.toArray(new String[0]);
    }

    @SuppressWarnings("unused")
    protected ObservableList<? extends U> getItems() {
        return tableView.getItems();
    }

    void injectFieldValue(Parent parent) {
        if (parent != null) {
            String id = parent.getId();
            if (id != null && !id.isEmpty()) {
                Class<?> clazz = getClass();
                while (clazz != UI_CtrlList.class) {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.getName().contentEquals(id) && field.getType().isAssignableFrom(parent.getClass())) {
                            setFieldValue(field, parent);
                            return;
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }
        }
    }

    protected void initController() {

        super.initController();

        if (tableView != null) {
            tableView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.hide();
                }
            });
        }
    }

    @FXML
    protected void initialize() {

        if (table == null) {
            table = buildTable();
        }
        if (table == null) {
            UI_Message.error("UI_CtrlList Error", "Table not defined.", "build table with buildTable()");
        } else {
            buildTableView();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionDeleteDocument() {

        if (findSelectedDocument() && UI_Message.ConfirmYesNo("Confirme:", "Desea eliminar registro de " + table.getGenre() + " seleccionado ?") == UI_Message.MESSAGE_VALUE.OK) {
            table.delete();
            populateList();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionEditDocument() {

        if (findSelectedDocument()) {
            if (table.edit()) {
                UI_CtrlRecord.ctrlRecord(table, null).showAndWait();
                populateList();
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionInsertDocument() {
        if (table.insert()) {
            UI_CtrlRecord.ctrlRecord(table, null).showAndWait();
            populateList();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionViewDocument() {
        if (findSelectedDocument()) {
            UI_CtrlRecord.ctrlRecord(table, null).show();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void onAfterPopulateList() {

    }

    @SuppressWarnings("WeakerAccess")
    protected void populateList() {

        if (!populatingList) {

            populatingList = true;

            if (refreshTimer != null) {
                refreshTimer.pause();
            }

            int focusedIndex = tableView.getSelectionModel().getFocusedIndex();
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {

                    ObservableList<U> observableList = FXCollections.observableArrayList();

                    try {
                        table.tableStatePush();

                        findMethod.run();

                        while (!table.getEof()) {
                            U e = table.getData();
                            observableList.add(e);
                            table.next();
                        }

                        table.tableStatePull();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> {
                        tableView.setItems(observableList);
                        tableView.getFocusModel().focus(focusedIndex);
                        tableView.getSelectionModel().select(selectedIndex);
                        if (refreshTimer != null) {
                            refreshTimer.play();
                        }
                        if (tableView.getItems().size() > 0 && !columnsAutoSized) {
                            columnsAutoSized = true;
                            if (autosizeColumnMethod == null) {
                                try {
                                    autosizeColumnMethod = TableViewSkin.class.getDeclaredMethod("resizeColumnToFitContent", TableColumn.class, int.class);
                                    autosizeColumnMethod.setAccessible(true);
                                } catch (NoSuchMethodException e) {
                                    e.printStackTrace();
                                }
                            }
                            autosizeColumns();
                        }
                        populatingList = false;
                        onAfterPopulateList();
                    });
                    return null;
                }
            };
            task.run();
        }
    }

    private void refreshTimerStart() {
        if (refreshTimer == null) {
            ++numTimers;
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(refreshLapse * numTimers), actionEvent -> populateList());
            refreshTimer = new Timeline(keyFrame);
            refreshTimer.setCycleCount(Timeline.INDEFINITE);
            refreshTimer.play();
        }
    }

    void refreshTimerStop() {
        if (refreshTimer != null) {
            --numTimers;
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    @SuppressWarnings("unused")
    public int getRefreshLapse() {
        return refreshLapse;
    }

    @SuppressWarnings("unused")
    public void setRefreshLapse(int refreshLapse) {
        this.refreshLapse = refreshLapse;
    }

    private void setFieldValue(Field field, Object value) {
        boolean accesible = field.isAccessible();
        if (!accesible) {
            field.setAccessible(true);
        }
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(accesible);
        }
    }

    public void show() {
        if (stage != null) {
            stage.requestFocus();
            stage.show();
        }
    }

    @SuppressWarnings("unused")
    public void showModal() {
        if (stage != null) {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.requestFocus();
            stage.showAndWait();
        }
    }

    @SuppressWarnings("unused")
    public Runnable getFindMethod() {
        return findMethod;
    }

    @SuppressWarnings("unused")
    public void setFindMethod(Runnable findMethod) {
        this.findMethod = findMethod;
    }

    @SuppressWarnings("WeakerAccess")
    public void autosizeColumns() {

        if (autosizeColumnMethod != null) {
            //System.err.println("::: " + table + " : " + tableView);
            for (TableColumn<U, ?> column : tableView.getColumns()) {
                try {
                    autosizeColumnMethod.invoke(tableView.getSkin(), column, -1);
                } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
