package logic.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.logging.Level;

import logic.utils.enums.Alerts;

import static logic.view.EssentialGUI.logger;


public class AlertPopup {
    private Alert.AlertType type;
    private String headerText = null;
    private static final String APPNAME = "NightAgent";

    public AlertPopup() {
        //empty
    }

    public void displayAlertPopup(Alerts alertType, String text) {
        try {
            switch (alertType) {
                case ERROR:
                    type = Alert.AlertType.ERROR;
                    headerText = "A severe error has occurred";
                    break;
                case WARNING:
                    type = Alert.AlertType.WARNING;
                    headerText = "Warning";
                    break;
                case CONFIRMATION:
                    type = Alert.AlertType.CONFIRMATION;
                    break;
                case INFORMATION:
                    type = Alert.AlertType.INFORMATION;
                    headerText = "Alert";
                    break;
                default:
                    logger.log(Level.SEVERE, "Alert type not found");
                    break;
            }

            Alert alert = new Alert(type);
            alert.setTitle(APPNAME);
            alert.setContentText(text);
            alert.setHeaderText(headerText);
            alert.show();
        } catch (IllegalStateException e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    public boolean askDeleteConfirmation(){
        type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type);
        alert.setTitle(APPNAME);
        alert.setHeaderText("Proceed with event cancellation?");

        // Aggiungi i pulsanti personalizzati
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Mostra la finestra di dialogo e ottieni la risposta
        return alert.showAndWait().filter(response -> response==yesButton).isPresent();
    }

    public boolean askChangeCityConfirmation(){
        type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type);
        alert.setTitle(APPNAME);
        alert.setHeaderText("Do you want to proceed updating your city?");

        // Aggiungi i pulsanti personalizzati
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Mostra la finestra di dialogo e ottieni la risposta
        return alert.showAndWait().filter(response -> response == yesButton).isPresent();
    }
}