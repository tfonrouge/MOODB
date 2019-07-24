package tech.fonrouge.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class UI_Message {

    @SuppressWarnings("WeakerAccess")
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

    public static void alert(Alert.AlertType alertType, String title, String header, String context) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(Wordwrap.wordwrap(header, 80));
        alert.setContentText(context);
        alert.showAndWait();
    }

    @SuppressWarnings("unused")
    public static void information(String title, String headerText, String context) {
        alert(Alert.AlertType.INFORMATION, title, headerText, context);
    }

    @SuppressWarnings("unused")
    public static void error(String title, String headerText, String error) {
        alert(Alert.AlertType.ERROR, title, headerText, error);
    }

    @SuppressWarnings("unused")
    public static void warning(String title, String headerText, String warning) {
        alert(Alert.AlertType.WARNING, title, headerText, warning);
    }

    @SuppressWarnings("unused")
    public enum MESSAGE_VALUE {
        OK,
        NO,
        CANCEL
    }

    public static class Wordwrap {

        @SuppressWarnings("WeakerAccess")
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
