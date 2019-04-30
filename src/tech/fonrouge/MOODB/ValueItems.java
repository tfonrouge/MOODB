package tech.fonrouge.MOODB;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class ValueItems<T> extends HashMap<T, String> {

    public ObservableList<String> getObservableValueList() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        forEach((key, value) -> observableList.add(value));
        return observableList;
    }

    public T getKey(String value) {
        AtomicReference<T> key = new AtomicReference<>();
        forEach((key1, value1) -> {
            if (key.get() == null && value.contentEquals(value1)) {
                key.set(key1);
            }
        });
        return key.get();
    }
}
