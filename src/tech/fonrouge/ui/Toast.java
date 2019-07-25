package tech.fonrouge.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

abstract public class Toast {

    private static ObservableList<TaskItem> taskItems;

    private static void showToast(MSGTYPE msgtype, Stage ownerStage, String toastMsg) {

        Stage toastStage = new Stage();
        toastStage.initOwner(ownerStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 24));

        StackPane root = new StackPane(text);
        switch (msgtype) {
            case MESSAGE:
                root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 32px;");
                root.setOpacity(0);
                text.setFill(Color.WHITE);
                break;
            case WARNING:
                root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(255, 153, 51, 0.5); -fx-padding: 32px;");
                root.setOpacity(0);
                text.setFill(Color.BLACK);
                break;
            case ERROR:
                root.setStyle("-fx-background-radius: 20; -fx-background-color: rgba(255, 0, 0, 0.5); -fx-padding: 32px;");
                root.setOpacity(0);
                text.setFill(Color.WHITE);
                break;
        }

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(500), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Timeline fadeOutTimeline = new Timeline();
            KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(500), new KeyValue(toastStage.getScene().getRoot().opacityProperty(), 0));
            fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
            fadeOutTimeline.setOnFinished((aeb) -> {
                toastStage.close();
                removeFromList();
            });
            fadeOutTimeline.play();
        });

        addToastStage(toastStage, fadeInTimeline, thread);
    }

    private static void removeFromList() {
        taskItems.remove(0);
    }

    private static void addToastStage(Stage toastStage, Timeline fadeInTimeline, Thread thread) {

        if (taskItems == null) {
            taskItems = FXCollections.observableArrayList();
            taskItems.addListener((ListChangeListener<? super TaskItem>) c -> {
                while (c.next()) {
                    if ((c.wasAdded() && taskItems.size() == 1) || (c.wasRemoved() && taskItems.size() > 0)) {
                        TaskItem taskItem = taskItems.get(0);
                        if (taskItem.stopped) {
                            taskItem.stopped = false;
                            taskItem.runnable.run();
                        }
                    }
                }
            });
        }

        Runnable runnable = () -> {
            toastStage.show();
            fadeInTimeline.setOnFinished(event -> thread.start());
            fadeInTimeline.play();
        };

        taskItems.add(new TaskItem(runnable));
    }

    @SuppressWarnings("unused")
    public static void showMessage(String message) {
        Platform.runLater(() -> Toast.showToast(MSGTYPE.MESSAGE, new Stage(), message));
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static void showWarning(String message) {
        Platform.runLater(() -> Toast.showToast(MSGTYPE.WARNING, new Stage(), message));
    }

    @SuppressWarnings("unused")
    public static void showError(String errorMsg) {
        Platform.runLater(() -> Toast.showToast(MSGTYPE.ERROR, new Stage(), errorMsg));
    }

    enum MSGTYPE {
        MESSAGE,
        WARNING,
        ERROR
    }

    static class TaskItem {
        boolean stopped = true;
        Runnable runnable;

        TaskItem(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
