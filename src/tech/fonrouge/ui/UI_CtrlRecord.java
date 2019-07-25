package tech.fonrouge.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import tech.fonrouge.MOODB.MField;
import tech.fonrouge.MOODB.MTable;
import tech.fonrouge.ui.Annotations.NoAutoBinding;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class UI_CtrlRecord<T extends MTable> extends UI_CtrlBase<T> {

    @SuppressWarnings("unused")
    public static UI_CtrlRecord ctrlRecord(MTable table) {
        return ctrlRecord(table, null);
    }

    @SuppressWarnings("WeakerAccess")
    public static UI_CtrlRecord ctrlRecord(MTable table, String ctrlRecordFXMLPath) {

        UI_CtrlRecord ui_ctrlRecord = (UI_CtrlRecord) UI_CtrlRecord.getUIController(table, ctrlRecordFXMLPath, "CtrlRecord");

        if (ui_ctrlRecord != null) {

            List<UI_CtrlList> localCtrlListStack = new ArrayList<>();

            ui_ctrlRecord.fxmlLoader.setControllerFactory(param -> {
                try {
                    if (UI_CtrlList.class.isAssignableFrom(param)) {

                        Type genericSuperclass = param.getGenericSuperclass();

                        Class<?> tableClass;

                        if (genericSuperclass instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                            tableClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        } else {
                            throw new Error("Cannot infer Table class for Controller " + param.getSimpleName());
                        }

                        UI_CtrlList ui_ctrlList;

                        Constructor<?>[] constructors = tableClass.getConstructors();
                        MTable childTable;
                        if (constructors.length == 0 || constructors[0].getParameterTypes().length == 0) {
                            childTable = (MTable) tableClass.newInstance();
                        } else {
                            Constructor<?> ctor = tableClass.getConstructor(table.getClass());
                            childTable = (MTable) ctor.newInstance(table);
                        }
                        Constructor<?> constructor = param.getConstructor();
                        ui_ctrlList = (UI_CtrlList) constructor.newInstance();
                        ui_ctrlList.table = childTable;

                        UI_CtrlList.currentCtrlList = ui_ctrlList;
                        localCtrlListStack.add(ui_ctrlList);

                        return ui_ctrlList;
                    }
                    return param.newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    if (table.getState() != MTable.STATE.NORMAL) {
                        table.cancel();
                    }
                    e.printStackTrace();
                    UI_Message.error("UI_CtrlList Error", "Warning", e.toString());
                }
                return null;
            });

            if (!ui_ctrlRecord.fxmlHasController) {
                ui_ctrlRecord.fxmlLoader.setController(ui_ctrlRecord);
            }

            try {
                ui_ctrlRecord.parent = ui_ctrlRecord.fxmlLoader.load();
                if (ui_ctrlRecord.fxmlHasController) {
                    ui_ctrlRecord = (UI_CtrlRecord) ui_ctrlRecord.getFXMLLoaderController();
                }
            } catch (Throwable e) {
                if (table.getState() != MTable.STATE.NORMAL) {
                    table.cancel();
                }
                e.printStackTrace();
                UI_Message.error("UI_CtrlList Error", "Warning", e.toString());
            }

            UI_CtrlList.currentCtrlList = null;

            if (ui_ctrlRecord.parent != null) {

                for (UI_CtrlList ui_ctrlList : localCtrlListStack) {
                    ui_ctrlList.populateList();
                }

                NoAutoBinding noAutoBinding = ui_ctrlRecord.getClass().getAnnotation(NoAutoBinding.class);
                if (noAutoBinding == null) {
                    ui_ctrlRecord.bindControls();
                }

                ui_ctrlRecord.initStage();

                ui_ctrlRecord.stage.initModality(Modality.APPLICATION_MODAL);

                MTable.STATE state = table.getState();

                Callable<Boolean> onValidateFields = table.getOnValidateFields();

                if (state != MTable.STATE.NORMAL) {
                    table.setOnValidateFields(ui_ctrlRecord::testValidFields);
                    table.setOnRefreshFieldNodes(ui_ctrlRecord::refreshFieldNodeStates);
                }

                String title;
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

                ui_ctrlRecord.stage.setTitle(title + ": " + table.getGenre());

                ui_ctrlRecord.stage.setOnHidden(event -> {
                    if (state != MTable.STATE.NORMAL) {
                        table.setOnValidateFields(onValidateFields);
                        //populateList();
                    }
                    for (UI_CtrlList ui_ctrlList : localCtrlListStack) {
                        ui_ctrlList.refreshTimerStop();
                    }
                    if (table.getState() != MTable.STATE.NORMAL) {
                        table.cancel();
                    }
                });

                UI_CtrlRecord finalUi_ctrlRecord1 = ui_ctrlRecord;
                ui_ctrlRecord.scene.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        finalUi_ctrlRecord1.stage.close();
                    }
                });
            }
        } else {
            if (table.getState() != MTable.STATE.NORMAL) {
                table.cancel();
            }
            UI_Message.error("UI_CtrlList Error", "Warning", "Define FXML file");
        }
        return ui_ctrlRecord;
    }

    @SuppressWarnings("unused")
    @FXML
    protected void onActionButtonAccept(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        MTable.STATE state = table.getState();
        if (state != MTable.STATE.NORMAL) {
            if (!table.onValidate()) {
                Exception e = table.getException();
                String msgWarning;
                if (table.getMessageWarning() == null) {
                    msgWarning = "Not valid state in onValidate(): ";
                    msgWarning += e != null ? e.toString() : "unknown error.";
                } else {
                    msgWarning = table.getMessageWarning();
                }
                Toast.showWarning(msgWarning);
                return;
            }
            if (!testValidFields()) {
                return;
            }
            if (!table.post()) {
                table.cancel();
                Exception e = table.getException();
                if (e != null) {
                    String errMsg = e.toString();
                    UI_Message.warning("Error", "Post Error", errMsg);
                }
            }
        }
        if (source != null) {
            source.getScene().getWindow().hide();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void showAndWait() {
        if (stage != null) {
            stage.showAndWait();
        }
    }

    private void refreshFieldNodeStates() {
        uiChangeListener0s.forEach(ui_changeListener0 -> {
            ui_changeListener0.refreshNode(true);
        });
    }

    @SuppressWarnings("WeakerAccess")
    protected boolean testValidFields() {
        if (table.getState() != MTable.STATE.NORMAL) {
            HashMap<String, String> list = table.getInvalidFieldList();
            if (list.size() > 0) {
                Map.Entry<String, String> i = list.entrySet().iterator().next();
                final MField[] mField = new MField[1];
                table.getFieldListStream().forEach(mField1 -> {
                    if (mField1.getName().contentEquals(i.getKey())) {
                        mField[0] = mField1;
                    }
                });
                if (mField[0].getMessageWarning() == null) {
                    Toast.showWarning(i.getValue() + ": '" + mField[0].getLabel() + "'");
                } else {
                    Toast.showWarning(mField[0].getMessageWarning());
                }
                Node node = nodeHashMap.get(i.getKey());
                if (node != null) {
                    node.requestFocus();
                }
                return false;
            }
            return true;
        }
        return false;
    }
}
