package com.group4.client.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Realizes methods for displaying dialog windows
 */
public class DialogWindow {
    private static Alert lastInstance;

    /**
     * Shows the dialog and waits for the user response (in other words, brings
     * up a blocking dialog, with the returned value the users input)
     *
     * @param title  title for dialog window
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     * @param type   alert type of the dialog window
     * @return value the users input
     */
    private static void showDialogWindow(String title, String header, String text, Alert.AlertType type) {
        lastInstance = new Alert(type);
        lastInstance.setTitle(title);
        lastInstance.setHeaderText(header);
        lastInstance.setContentText(text);
    }

    /**
     * Shows the warning dialog
     *
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     */
    public static void showWarningWindow(String header, String text) {
        showDialogWindow("Warning!", header, text, Alert.AlertType.WARNING);
        lastInstance.showAndWait();
    }

    /**
     * Shows the information dialog
     *
     * @param text text to show in the dialog content area
     */
    public static void showInfoWindow(String text) {
        showDialogWindow("Information Dialog", null, text, Alert.AlertType.INFORMATION);
        lastInstance.showAndWait();
    }

    /**
     * Shows the error dialog
     *
     * @param text text to show in the dialog content area
     */
    public static void showErrorWindow(String text) {
        showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR);
        lastInstance.showAndWait();
    }

    /**
     * Shows the error dialog that can't be closed by the user
     *
     * @param text text to show in the dialog content area
     */
    public static void showErrorWindowWithoutButtons(String text) {
        showDialogWindow("Error Dialog", "Error!", text, Alert.AlertType.ERROR);
        lastInstance.getButtonTypes().clear();
        lastInstance.setResult(ButtonType.OK);
        lastInstance.show();
        System.out.println("Error Dialog is shown");
    }

    /**
     * Shows the confirmation dialog and returns {@code true} if user pressed "OK" button, otherwise {@code false}
     *
     * @param header text to show in the dialog header area
     * @param text   text to show in the dialog content area
     * @return {@code true}  if user pressed "OK" button
     */
    public static boolean showConfirmationWindow(String header, String text) {
        showDialogWindow("Confirmation Dialog", header, text, Alert.AlertType.CONFIRMATION);
        Optional<ButtonType> result = lastInstance.showAndWait();
        return result.get() == ButtonType.OK;
    }

    public static Alert getLastInstance() {
        return lastInstance;
    }
}
