package test02;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import test02.datos.Inventario;

import java.util.Date;
import java.util.Random;

public class MainCtrl {

    public TableView<InventarioData> tableView = new TableView<>();

    public TableColumn<InventarioData, String> columnNombre = new TableColumn<>();
    public TableColumn<InventarioData, String> columnUdem = new TableColumn<>();
    public TableColumn<InventarioData, Double> columnExistencia = new TableColumn<>();
    public TableColumn<InventarioData, Date> columnFecha = new TableColumn<>();

    private ObservableList<InventarioData> observableList;

    public <a> MainCtrl(Stage primaryStage) {

        initialize();

        VBox node = new VBox();

        node.getChildren().add(tableView);

        Inventario inventario = new Inventario();

        for (String field : getColumns()) {
            TableColumn<InventarioData, a> column = new TableColumn<>();
        }

        tableView.getColumns().add(columnNombre);
        tableView.getColumns().add(columnUdem);
        tableView.getColumns().add(columnExistencia);
        tableView.getColumns().add(columnFecha);

        Scene scene = new Scene(node);

        primaryStage.setTitle("Test02");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    void initialize() {

        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnUdem.setCellValueFactory(new PropertyValueFactory<>("udem"));
        columnExistencia.setCellValueFactory(new PropertyValueFactory<>("existencia"));
        columnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        observableList = FXCollections.observableArrayList();

        buildTable();

        populateList();

        tableView.setItems(observableList);

    }

    String[] getColumns() {
        return new String[]{"nombre", "udem", "existencia", "fecha"};
    }

    private void buildTable() {
        Inventario inventario = new Inventario();

        int tableSize = 1000;

        for (int i = 0; i < tableSize; i++) {
            if (inventario.insert()) {
                inventario.field_nombre.setValue("name " + i);
                inventario.field_udem.setValue("pz");
                inventario.field_existencia.setValue((double) new Random().nextInt(100));
                if (!inventario.post()) {
                    inventario.cancel();
                    break;
                }
            }
        }
    }

    private void populateList() {
        Inventario inventario = new Inventario();

        inventario.find();
        inventario.index_tipo_nombre.find(1);

        while (!inventario.getEof()) {
            observableList.add(new InventarioData(inventario));
            inventario.next();
        }
    }
}
