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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class UI_CtrlList<T extends MTable> extends UI_Binding<T> {

    @SuppressWarnings("WeakerAccess")
    public static UI_CtrlList currentCtrlList = null;
    private final ObservableList<MBaseData> observableList = FXCollections.observableArrayList();
    @SuppressWarnings("unused")
    @FXML
    protected TableView<MBaseData> tableView;
    protected Stage stage;
    @SuppressWarnings("WeakerAccess")
    protected Parent parent;
    /**
     * refresh lapse for tableView en seconds
     */
    private int refreshLapse = 3;
    private boolean populatingList = false;
    private ScheduledExecutorService executorServiceRefresh;

    private static boolean fxmlHasFXController(URL fxmlPath) {
        InputStream inputStream = null;
        try {
            inputStream = fxmlPath.openStream();
        } catch (IOException e) {
            e.printStackTrace();
            UI_Message.Error("UI_CtrlList Error", "FXML URL not valid.", e.toString());
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
                UI_Message.Error("UI_CtrlList Error", "FXML not valid.", e.toString());
            }
        }
        return false;
    }

    static private Constructor<?> getCtorClass(Class<?> tableClass) {
        Constructor<?> constructor;
        String className;

        if (tableClass.equals(MTable.class)) {
            return null;
        }

        className = tableClass.getName() + "CtrlList";

        try {
            constructor = Class.forName(className).getConstructor();
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
                ui_ctrlList = (UI_CtrlList) constructor.newInstance();
                ui_ctrlList.table = table;
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            UI_Message.Error("UI_CtrlList Error", "Warning", e.toString());
        }

        if (ui_ctrlList != null) {

            String ctrlListFXMLPath = ui_ctrlList.getCtrlListFXMLPath();
            URL resource = ui_ctrlList.getClass().getResource(ctrlListFXMLPath);

            if (resource != null) {
                boolean hasController = fxmlHasFXController(resource);
                FXMLLoader fxmlLoader = new FXMLLoader(resource);
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
                        UI_Message.Error("UI_CtrlList Error", "Warning", e.toString());
                    }
                    return null;
                });

                if (!hasController) {
                    fxmlLoader.setController(ui_ctrlList);
                }

                Parent parent = null;
                try {
                    currentCtrlList = ui_ctrlList;
                    parent = fxmlLoader.load();
                } catch (Throwable e) {
                    e.printStackTrace();
                    UI_Message.Error("UI_CtrlList Error", "Warning", e.toString());
                } finally {
                    currentCtrlList = null;
                }
                if (parent != null) {
                    ui_ctrlList.showWindow(parent);
                }
            } else {
                UI_Message.Error("UI_CtrlList Error", "No FXML resource found.", "define FXML resource.");
            }
        } else {
            UI_Message.Error("UI_CtrlList Error", "No controller found.", "define controller for table.");
        }
    }

    private TableColumn<MBaseData, ?> buildColumn(String fieldExpression) {

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
                TableColumn<MBaseData, ?> column;
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

            TableColumn<MBaseData, ?> column = buildColumn(fieldExpression);
            column.setId(fieldExpression);

            tableView.getColumns().add(column);

        }
    }

    protected T buildTable() {
        return null;
    }

    private void buildTableView() {

        if (tableView != null) {

            tableView.getColumns().clear();

            tableView.setMaxHeight(Control.USE_COMPUTED_SIZE);

            buildColumns();

            Runnable runnable = () -> Platform.runLater(this::populateList);
            executorServiceRefresh = Executors.newSingleThreadScheduledExecutor();
            executorServiceRefresh.scheduleAtFixedRate(runnable, 0, refreshLapse, TimeUnit.SECONDS);

            tableView.setItems(observableList);

            tableView.sceneProperty().addListener((observable, oldValue, newValue) -> {
                if (oldValue == null && newValue != null) {
                    Scene scene = tableView.getScene();
                    scene.windowProperty().addListener((observable1, oldValue1, newValue1) -> {
                        if (oldValue1 == null && newValue1 != null) {
                            tableView.getScene().getWindow().setOnHidden(event -> {
                                if (executorServiceRefresh != null) {
                                    executorServiceRefresh.shutdown();
                                    executorServiceRefresh = null;
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected void doInsertEdit() {

        URL resource = getCtrlRecordResourceURL();

        if (resource != null) {

            FXMLLoader fxmlLoader = new FXMLLoader(resource);

            List<UI_CtrlList> localCtrlListStack = new ArrayList<>();

            fxmlLoader.setControllerFactory(param -> {
                try {
                    if (UI_CtrlList.class.isAssignableFrom(param)) {

                        Class<?> tableClass = param.getConstructors()[0].getParameterTypes()[0];
                        UI_CtrlList ui_ctrlList;

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

                        currentCtrlList = ui_ctrlList;
                        localCtrlListStack.add(ui_ctrlList);

                        return ui_ctrlList;
                    }
                    return param.newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    if (table.getState() != MTable.STATE.NORMAL) {
                        table.cancel();
                    }
                    e.printStackTrace();
                    UI_Message.Error("UI_CtrlList Error", "Warning", e.toString());
                }
                return null;
            });

            try {
                parent = fxmlLoader.load();
            } catch (Throwable e) {
                if (table.getState() != MTable.STATE.NORMAL) {
                    table.cancel();
                }
                e.printStackTrace();
                UI_Message.Error("UI_CtrlList Error", "Warning", e.toString());
            }

            currentCtrlList = null;

            if (parent != null) {

                for (UI_CtrlList ui_ctrlList : localCtrlListStack) {
                    //ui_ctrlList.buildTableView();
                }

                fxmlLoader.<UI_CtrlRecord>getController().setCtrlList(this);

                Stage stage = new Stage();
                Scene scene = new Scene(parent);

                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);

                String title;

                MTable.STATE state = table.getState();

                Callable<Boolean> onValidateFields = table.getOnValidateFields();

                if (state != MTable.STATE.NORMAL) {
                    table.setOnValidateFields(() -> fxmlLoader.<UI_CtrlRecord>getController().testValidFields());
                }

                switch (state) {
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
                    if (state != MTable.STATE.NORMAL) {
                        table.setOnValidateFields(onValidateFields);
                    }
                    for (UI_CtrlList ui_ctrlList : localCtrlListStack) {
                        //ui_ctrlList.stopExecutorServiceRefresh();
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
        } else {
            if (table.getState() != MTable.STATE.NORMAL) {
                table.cancel();
            }
            UI_Message.Error("UI_CtrlList Error", "Warning", "Define FXML file");
        }
    }

    protected String getCtrlListFXMLPath() {
        return "listView.fxml";
    }

    protected String getCtrlRecordFXMLPath() {
        return "recordView.fxml";
    }

    private URL getCtrlRecordResourceURL() {

        Class<?> tableClass = table.getClass();

        while (!tableClass.equals(MTable.class)) {

            String uiCtrlRecordClassName = tableClass.getName() + "CtrlRecord";

            Class<?> uiCtrlRecordClass = null;

            try {
                uiCtrlRecordClass = Class.forName(uiCtrlRecordClassName);
            } catch (ClassNotFoundException ignored) {

            }

            if (uiCtrlRecordClass != null) {
                URL resource = tableClass.getResource(getCtrlRecordFXMLPath());
                if (resource != null) {
                    return resource;
                }
            }
            tableClass = tableClass.getSuperclass();
        }

        return null;
    }

    protected String[] getFieldColumnList() {
        List<String> strings = new ArrayList<>();

        table.getFieldListStream().forEach(mField -> strings.add(mField.getName()));

        return strings.toArray(new String[0]);
    }

    TableView<MBaseData> getTableView() {
        return tableView;
    }

    protected void initController(Parent parent) {

    }

    @FXML
    protected void initialize() {
        if (table == null) {
            table = buildTable();
        }
        if (table == null) {
            System.out.println("ERROR ERROR ERROR");
            UI_Message.Error("UI_CtrlList Error", "Table not defined.", "build table with buildTable()");
        } else {
            buildTableView();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onActionDeleteDocument() {

        MBaseData item = tableView.getSelectionModel().getSelectedItem();

        if (item != null) {
            if (table.field__id.aggregateFind(item.get_id()) && UI_Message.ConfirmYesNo("Confirme:", "Desea eliminar registro de " + table.getGenre() + " seleccionado ?") == UI_Message.MESSAGE_VALUE.OK) {
                if (!table.delete()) {
                    if (table.getException() == null) {
                        UI_Message.Error("UI_CtrlList Error", "Unknown error", "Delete error");
                    } else {
                        UI_Message.Error("UI_CtrlList Error", table.getException().getMessage(), "Delete error");
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

            tableFind();

            observableList.clear();

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

        Scene scene = new Scene(parent);

        if (tableView != null) {
            tableView.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    stage.hide();
                }
            });
        }

        stage.setScene(scene);
        stage.setTitle(table.getGenres());

        stage.show();
    }

    @SuppressWarnings("unused")
    protected void tableFind() {
        table.aggregateFind();
    }

    public int getRefreshLapse() {
        return refreshLapse;
    }

    public void setRefreshLapse(int refreshLapse) {
        this.refreshLapse = refreshLapse;
    }
}
