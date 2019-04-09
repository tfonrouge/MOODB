package tech.fonrouge.MOODB.ui;

import javafx.scene.control.ComboBox;

abstract class UI_ChangeListenerComboBox<T, U> extends UI_ChangeListener<T, U> {

    abstract void fillComboBoxList(ComboBox<T> comboBox);

}
