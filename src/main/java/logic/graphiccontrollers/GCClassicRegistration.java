package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import logic.beans.BUserData;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.enums.Alerts;

public class GCClassicRegistration extends GCRegistration{
    @Override
    @FXML
    public void initialize() {
        initRegistration();
        this.dataBean = new BUserData();
    }

    @Override
    @FXML
    public void registerControl(MouseEvent event) {
        try {
            setupDataBean(false);
            register(event);
        } catch (InvalidValueException | TextTooLongException e) {
            this.alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }
}
