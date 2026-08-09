package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.enums.Alerts;
import logic.utils.LoggedUser;

public class GCGoogleRegistration extends GCRegistration {
    @FXML
    @Override
    public void initialize() {
        initRegistration();
        this.dataBean = new BUserData(LoggedUser.getUserName()); //inizializzo il data bean con username preso da GoogleAuth
    }

    @FXML
    @Override
    public void registerControl(MouseEvent event) {
        try {
            setupDataBean(true);
            register(event);
        } catch (InvalidValueException | TextTooLongException e) {
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }
}
