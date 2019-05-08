package tech.fonrouge.MOODB.ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import org.bson.Document;
import tech.fonrouge.MOODB.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class UI_CtrlList<T extends MTable> extends UI_Binding<T> {

    private final ObservableList<MBaseData> observableList = FXCollections.observableArrayList();

    @SuppressWarnings("unused")
    @FXML
    protected TableView<MBaseData> tableView;

    protected Stage stage;
    @SuppressWarnings("WeakerAccess")
    protected Parent parent;
    private ScheduledExecutorService executorServiceRefresh;
    private boolean populatingList = false;

    public UI_CtrlList(T table) {
        this.table = table;
    }

    static private Constructor<?> getCtorClass(Class<?> tableClass) {
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

    static public void showList(MTable table) {
        UI_CtrlList ui_ctrlList = null;

        try {
            Constructor<?> constructor = getCtorClass(table.getClass());
            if (constructor != null) {
                ui_ctrlList = (UI_CtrlList) constructor.newInstance(table);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            UI_Message.Warning("UI Controller", "Warning", e.toString());
        }

        if (ui_ctrlList != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(ui_ctrlList.getClass().getResource(ui_ctrlList.getCtrlListFXMLPath()));
            fxmlLoader.setController(ui_ctrlList);
            Parent parent = null;
            Class<?> uiCtrlListClass = ui_ctrlList.getClass();

            UI_CtrlList finalUi_ctrlList = ui_ctrlList;
            fxmlLoader.setControllerFactory(param -> {
                try {
                    if (param.isAssignableFrom(uiCtrlListClass)) {
                        return finalUi_ctrlList;
                    }
                    return param.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    UI_Message.Warning("UI Controller", "Warning", e.toString());
                }
                return null;
            });

            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
                UI_Message.Warning("UI Controller", "Warning", e.toString());
            }
            if (parent != null) {
                ui_ctrlList.showWindow(parent);
            }
        }
    }

    private void buildColumns() {

        for (String fieldExpression : getColumns()) {

            TableColumn<MBaseData, ?> column = getColumn(fieldExpression);

            tableView.getColumns().add(column);

        }
    }

    private void buildTableView() {

        tableView.getColumns().clear();

        tableView.setMaxHeight(Control.USE_COMPUTED_SIZE);

        buildColumns();

        if (executorServiceRefresh == null) {
            Runnable runnable = () -> Platform.runLater(this::populateList);

            executorServiceRefresh = Executors.newSingleThreadScheduledExecutor();
            executorServiceRefresh.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
        }

        tableView.setItems(observableList);

    }

    @SuppressWarnings("WeakerAccess")
    protected void doInsertEdit() {

        URL resource = getClass().getResource(getCtrlRecordFXMLPath());
        FXMLLoader fxmlLoader = new FXMLLoader(resource);

        ArrayList<UI_CtrlList> ui_ctrlLists = new ArrayList<>();

        fxmlLoader.setControllerFactory(param -> {
            try {
                if (UI_CtrlList.class.isAssignableFrom(param)) {
                    Class<?> tableClass = param.getConstructors()[0].getParameterTypes()[0];
                    UI_CtrlList ui_ctrlList;
                    /* if table is abstract uses last know (if any) controller stored on ui_ctrlLists */
                    if (Modifier.isAbstract(tableClass.getModifiers())) {
                        ui_ctrlList = ui_ctrlLists.get(ui_ctrlLists.size() - 1);
                    } else {
                        Constructor<?>[] constructors = tableClass.getConstructors();
                        MTable childTable;
                        if (constructors.length == 0 || constructors[0].getParameterTypes().length == 0) {
                            childTable = (MTable) tableClass.newInstance();
                        } else {
                            Constructor<?> ctor = tableClass.getConstructor(table.getClass());
                            childTable = (MTable) ctor.newInstance(table);
                        }
                        Constructor<?> constructor = param.getConstructor(tableClass);
                        ui_ctrlList = (UI_CtrlList) constructor.newInstance(childTable);
                    }
                    ui_ctrlLists.add(ui_ctrlList);
                    return ui_ctrlList;
                }
                return param.newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                if (table.getState() != MTable.STATE.NORMAL) {
                    table.cancel();
                }
                e.printStackTrace();
                UI_Message.Warning("UI Controller", "Warning", e.toString());
            }
            return null;
        });

        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            if (table.getState() != MTable.STATE.NORMAL) {
                table.cancel();
            }
            e.printStackTrace();
            UI_Message.Warning("UI Controller", "Warning", e.toString());
        }

        for (UI_CtrlList ui_ctrlList : ui_ctrlLists) {
            ui_ctrlList.buildTableView();
        }

        if (parent != null) {
            fxmlLoader.<UI_CtrlRecord>getController().setCtrlList(this);

            Stage stage = new Stage();
            Scene scene = new Scene(parent);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            String title;

            switch (table.getState()) {
                case NORMAL:
                    title = "Mostrar Detalle";
                    break;
                case EDIT:
                    title = "Modificar";
                    break;
                case INSERT:
                    title = "Agregar";
                    break;
                default:
                    title = "?";
            }

            stage.setTitle(title + ": " + table.getGenre());

            stage.setOnHidden(event -> {
                for (UI_CtrlList ui_ctrlList : ui_ctrlLists) {
                    ui_ctrlList.stopExecutorServiceRefresh();
                }
                if (table.getState() != MTable.STATE.NORMAL) {
                    table.cancel();
                }
            });

            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });

            stage.show();
        }
    }

    private TableColumn<MBaseData, ?> getColumn(String fieldExpression) {

        if (fieldExpression.indexOf('.') > 0) {
            String[] fieldList = fieldExpression.split("\\.");
            if (fieldList.length > 1) {
                TableColumn<MBaseData, String> column;
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
                                    mField = mFieldTableField.linkedTable().fieldByName(fieldList[i]);
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
                TableColumn<MBaseData, ?> column;
                column = new TableColumn<>(mField.getLabel());
                column.setCellValueFactory(new PropertyValueFactory<>(mField.getName()));
                return column;
            }
        }

        return new TableColumn<>(fieldExpression);
    }

    protected abstract String[] getColumns();

    protected String getCtrlListFXMLPath() {
        return "listView.fxml";
    }

    protected String getCtrlRecordFXMLPath() {
        return "recordView.fxml";
    }

    TableView<MBaseData> getTableView() {
        return tableView;
    }

    protected void initController(Parent parent) {

    }

    @SuppressWarnings("WeakerAccess")
    public void onActionDeleteDocument() {

        MBaseData item = tableView.getSelectionModel().getSelectedItem();

        if (item != null) {
            if (table.field__id.aggregateFind(item.get_id()) && UI_Message.ConfirmYesNo("Confirme:", "Desea eliminar registro de " + table.getGenre() + " seleccionado ?") == UI_Message.MESSAGE_VALUE.OK) {
                if (!table.delete()) {
                    if (table.getException() == null) {
                        UI_Message.Warning("UI Controller", "Unknown error", "Delete error");
                    } else {
                        UI_Message.Warning("UI Controller", table.getException().getMessage(), "Delete error");
                    }
                }
                populateList();
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionEditDocument() {

        MBaseData item = tableView.getSelectionModel().getSelectedItem();

        if (item != null) {
            Object id = item.get_id();
            if (table.field__id.aggregateFind(id) && table.edit()) {
                doInsertEdit();
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionInsertDocument() {
        if (table.insert()) {
            doInsertEdit();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionViewDocument() {
        MBaseData item = tableView.getSelectionModel().getSelectedItem();
        if (item != null && table.field__id.aggregateFind(item.get_id())) {
            doInsertEdit();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void populateList() {

        if (!populatingList) {

            populatingList = true;

            int focusedIndex = tableView.getSelectionModel().getFocusedIndex();
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

            table.tableStatePush();

            observableList.clear();

            for (int i = 0; i < getColumns().length; i++) {
                if (getColumns()[i].indexOf(".") > 0) {
                    table.addLookupField(getColumns()[i]);
                }
            }

            tableFind();

            while (!table.getEof()) {
                MBaseData e = table.getData();
                observableList.add(e);
                table.next();
            }

            tableView.getFocusModel().focus(focusedIndex);
            tableView.getSelectionModel().select(selectedIndex);

            table.tableStatePull();

            populatingList = false;
        }
    }

    private void showWindow(Parent parent) {

        initController(parent);

        stage = new Stage();

        buildTableView();

        Scene scene = new Scene(parent);

        tableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.hide();
            }
        });

        stage.setOnHidden(event -> stopExecutorServiceRefresh());

        stage.setScene(scene);
        stage.setTitle(table.getGenres());

        stage.show();
    }

    private void stopExecutorServiceRefresh() {
        if (executorServiceRefresh != null) {
            executorServiceRefresh.shutdown();
            executorServiceRefresh = null;
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void tableFind() {
        table.aggregateFind();
    }
}
