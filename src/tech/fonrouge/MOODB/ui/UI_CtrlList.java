package tech.fonrouge.MOODB.ui;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import tech.fonrouge.MOODB.MBaseData;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MFieldTableField;
import tech.fonrouge.MOODB.MTable;

import java.io.IOException;

public abstract class UI_CtrlList<T extends MTable> {

    private final ObservableList<MBaseData> observableList = FXCollections.observableArrayList();
    private T table = getTable();
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_cerrar;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_agregar;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_modificar;
    @SuppressWarnings("unused")
    @FXML
    private MenuItem menuItem_eliminar;
    @SuppressWarnings("unused")
    @FXML
    private TableView<MBaseData> tableView;

    protected abstract String[] getColumns();

    void buildUI(@NotNull Parent parent) {

        menuItem_cerrar.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
        menuItem_agregar.setAccelerator(new KeyCodeCombination(KeyCode.INSERT));
        menuItem_modificar.setAccelerator(new KeyCodeCombination(KeyCode.F3));
        menuItem_eliminar.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));

        table = getTable();

        Stage stage = new Stage();

        tableView.getColumns().clear();

        tableView.setMaxHeight(Control.USE_COMPUTED_SIZE);

        buildColumns();

        populateList();

        tableView.setItems(observableList);

        Scene scene = new Scene(parent);

        tableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.hide();
            }
        });

        menuItem_cerrar.setOnAction(event -> stage.hide());

        stage.setScene(scene);
        stage.setTitle(table.getGenres());

        stage.show();
    }

    private void buildColumns() {

        for (String fieldExpression : getColumns()) {

            TableColumn<MBaseData, ?> column = getColumn(fieldExpression);

            tableView.getColumns().add(column);

        }
    }

    private TableColumn<MBaseData, ?> getColumn(String fieldExpression) {

        if (fieldExpression.indexOf('.') > 0) {
            String[] fieldList = fieldExpression.split("\\.");
            if (fieldList.length > 1) {
                TableColumn<MBaseData, String> column;
                column = new TableColumn<>();
                column.setCellValueFactory(param -> {

                    String label;
                    String value;
                    Document document = param.getValue().getTableState().getLookupDocument().get("@" + fieldList[0], Document.class);

                    MField mField;
                    MFieldTableField mFieldTableField = param.getValue().getTable().fieldTableFieldByName(fieldList[0]);

                    if (document != null && mFieldTableField != null) {
                        label = mFieldTableField.getLabel();
                        int i = 1;
                        do {
                            mField = mFieldTableField.syncLinkedTable().fieldByName(fieldList[i]);
                            if (mField != null) {
                                if (mField instanceof MFieldTableField) {
                                    mFieldTableField = (MFieldTableField) mField;
                                } else {
                                    mFieldTableField = null;
                                }
                                label += " " + mField.getLabel();
                                value = document.get(mField.getName()).toString();
                            } else {
                                label = "!" + fieldExpression + "!";
                                value = "!error!";
                                break;
                            }
                            document = document.get("@" + mField.getName(), Document.class);
                        } while (mFieldTableField != null && document != null && ++i < fieldList.length);
                    } else {
                        label = "!" + fieldExpression + "!";
                        value = "!error!";
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

    void populateList() {

        observableList.clear();

        for (int i = 0; i < getColumns().length; i++) {
            if (getColumns()[i].indexOf(".") > 0) {
                table.addLookupField(getColumns()[i]);
            }
        }

        table.find();

        while (!table.getEof()) {
            MBaseData e = table.getData();
            observableList.add(e);
            table.next();
        }
    }

    abstract protected T buildTable();

    public T getTable() {
        if (table == null) {
            table = buildTable();
        }
        return table;
    }

    abstract protected String getResourceRecordName();

    private void doInsertEdit() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getResourceRecordName()));

        Parent parent = null;

        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
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

    @SuppressWarnings("unused")
    public void onActionMenuAgregar() {
        if (table.insert()) {
            doInsertEdit();
        }
    }

    @SuppressWarnings("unused")
    public void onActionMenuModificar() {

        MBaseData item = tableView.getSelectionModel().getSelectedItem();

        if (item != null) {
            Object id = item.get_id();
            if (table.field__id.find(id) && table.edit()) {
                doInsertEdit();
            }
        }
    }

    @SuppressWarnings("unused")
    public void onActionMenuEliminar() {

        MBaseData item = tableView.getSelectionModel().getSelectedItem();

        if (item != null) {
            Object id = item.get_id();
            if (table.field__id.find(id) && UI_Message.ConfirmYesNo("Confirme:", "Desea eliminar registro de " + table.getGenre() + " seleccionado ?") == UI_Message.MESSAGE_VALUE.OK) {
                table.delete();
                populateList();
            }
        }
    }
}
