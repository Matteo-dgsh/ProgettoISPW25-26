package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import logic.view.EssentialGUI;

public class GCHomeOrg extends EssentialGUI {
    @FXML
    void goToAddEvent(MouseEvent event) {
        changeGUI(event, "AddEvent.fxml");
    }
}
