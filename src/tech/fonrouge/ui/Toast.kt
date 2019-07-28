package tech.fonrouge.ui

import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration

object Toast {

    private val taskItems: ObservableList<TaskItem> = FXCollections.observableArrayList()

    init {
        taskItems.addListener(ListChangeListener<TaskItem> { change ->
            while (change.next()) {
                if (change.wasAdded() && taskItems.size == 1 || change.wasRemoved() && taskItems.size > 0) {
                    val taskItem = taskItems[0]
                    if (taskItem.stopped) {
                        taskItem.stopped = false
                        taskItem.runnable.run()
                    }
                }
            }
        } as ListChangeListener<in TaskItem>)
    }

    private fun showToast(msgType: MsgType, ownerStage: Stage, toastMsg: String) {

        val toastStage = Stage()
        toastStage.initOwner(ownerStage)
        toastStage.isResizable = false
        toastStage.initStyle(StageStyle.TRANSPARENT)

        val text = Text(toastMsg)
        text.font = Font.font("Verdana", 24.0)

        val root = StackPane(text)
        when (msgType) {
            Toast.MsgType.MESSAGE -> {
                root.style = "-fx-background-radius: 20; -fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 32px;"
                root.opacity = 0.0
                text.fill = Color.WHITE
            }
            Toast.MsgType.WARNING -> {
                root.style = "-fx-background-radius: 20; -fx-background-color: rgba(255, 153, 51, 0.5); -fx-padding: 32px;"
                root.opacity = 0.0
                text.fill = Color.BLACK
            }
            Toast.MsgType.ERROR -> {
                root.style = "-fx-background-radius: 20; -fx-background-color: rgba(255, 0, 0, 0.5); -fx-padding: 32px;"
                root.opacity = 0.0
                text.fill = Color.WHITE
            }
        }

        val scene = Scene(root)
        scene.fill = Color.TRANSPARENT
        toastStage.scene = scene

        val fadeInTimeline = Timeline()
        val fadeInKey1 = KeyFrame(Duration.millis(500.0), KeyValue(toastStage.scene.root.opacityProperty(), 1))
        fadeInTimeline.keyFrames.add(fadeInKey1)

        val thread = Thread {
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            val fadeOutTimeline = Timeline()
            val fadeOutKey1 = KeyFrame(Duration.millis(500.0), KeyValue(toastStage.scene.root.opacityProperty(), 0))
            fadeOutTimeline.keyFrames.add(fadeOutKey1)
            fadeOutTimeline.setOnFinished {
                toastStage.close()
                removeFromList()
            }
            fadeOutTimeline.play()
        }

        addToastStage(toastStage, fadeInTimeline, thread)
    }

    private fun removeFromList() {
        taskItems.removeAt(0)
    }

    private fun addToastStage(toastStage: Stage, fadeInTimeline: Timeline, thread: Thread) {

        val runnable = Runnable {
            toastStage.show()
            fadeInTimeline.setOnFinished { thread.start() }
            fadeInTimeline.play()
        }

        taskItems.add(TaskItem(runnable))
    }

    @Suppress("unused")
    fun showMessage(message: String) {
        Platform.runLater { showToast(MsgType.MESSAGE, Stage(), message) }
    }

    fun showWarning(message: String) {
        Platform.runLater { showToast(MsgType.WARNING, Stage(), message) }
    }

    fun showError(errorMsg: String) {
        Platform.runLater { showToast(MsgType.ERROR, Stage(), errorMsg) }
    }

    internal enum class MsgType {
        MESSAGE,
        WARNING,
        ERROR
    }

    internal class TaskItem(var runnable: Runnable) {
        var stopped = true
    }
}
