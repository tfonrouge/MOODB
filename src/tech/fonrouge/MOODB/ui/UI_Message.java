package tech.fonrouge.MOODB.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class UI_Message {

    public static MESSAGE_VALUE ConfirmYesNo(String confirm, String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(confirm);
        alert.setHeaderText(headerText);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            return MESSAGE_VALUE.OK;
        }
        return MESSAGE_VALUE.CANCEL;
    }

    public static void Warning(String warning, String headerText) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(warning);

        alert.setHeaderText(Wordwrap.wordwrap(headerText, 80));
        alert.showAndWait();
    }

    public enum MESSAGE_VALUE {
        OK,
        NO,
        CANCEL
    }

    public static class Wordwrap {

        public static String wordwrap(final String input, final int length) {
            if (input == null || length < 1) {
                throw new IllegalArgumentException("Invalid input args");
            }

            final String text = input.trim();

            if (text.length() > length && text.contains(" ")) {
                final String line = text.substring(0, length);
                final int lineBreakIndex = line.indexOf("\n");
                final int lineLastSpaceIndex = line.lastIndexOf(" ");
                final int inputFirstSpaceIndex = text.indexOf(" ");

                final int breakIndex = lineBreakIndex > -1 ? lineBreakIndex :
                        (lineLastSpaceIndex > -1 ? lineLastSpaceIndex : inputFirstSpaceIndex);

                return text.substring(0, breakIndex) + "\n" + wordwrap(text.substring(breakIndex + 1), length);
            } else {
                return text;
            }
        }
    }
}
