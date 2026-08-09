package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class GCSettingsUser extends GCSettingsGeneral {
    @FXML
    void goToChangeCity(MouseEvent event) {
        changeGUI(event, "EditCity.fxml");
    }

}
