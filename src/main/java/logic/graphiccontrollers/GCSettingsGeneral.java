package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import logic.utils.enums.Alerts;
import logic.view.EssentialGUI;

public class GCSettingsGeneral extends EssentialGUI {

    @FXML
    public void initialize() {
        //empty
    }

    @FXML
    void goToProfile(MouseEvent event) {
        changeGUI(event, "Profile.fxml");
    }

    @FXML
    void goToContactUs(MouseEvent event) {
        changeGUI(event, "ContactUs.fxml");
    }

    @FXML
    void goToHelpFaqs(MouseEvent event) {
        changeGUI(event, "HelpFAQs.fxml");
    }

    @FXML
    void leaveApp(MouseEvent event) {
        cfacade.signOut();
        changeGUI(event, "Login.fxml");
        alert.displayAlertPopup(Alerts.INFORMATION,"Logged out successfully. You have been returned to the login screen!");
    }
}
