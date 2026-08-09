package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;

import logic.exceptions.EventAlreadyDeleted;
import logic.utils.enums.Alerts;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class GCEventPageUser extends GCEventPageGeneral implements InfoPages {


    @FXML
    private Button eventBtn;

    @FXML
    private Label eventBtnLabel;

    @FXML
    private ImageView iconEventBtn;

    boolean isEventJoined = false;

    public void initEventPageButton(){
        //verifico una partecipazione precedente all'evento e poi setto il pulsante
        isEventJoined = cfacade.checkPreviousEventParticipation(this.eventBean);

        String loc;
        Image image = null;
        if(isEventJoined){
            try {
                loc = getClass().getResource("/icons/remove_event_part.png").toExternalForm();
            } catch (NullPointerException e){
                throw new NullPointerException();
            }
            if (loc != null) {
                image = new Image(loc);
            }
            eventBtn.setOnMouseClicked(this::removeParticipationToEvent);
            eventBtnLabel.setText("Remove participation");
        } else {
            try {
                loc = getClass().getResource("/icons/plan_event_part.png").toExternalForm();
            } catch (NullPointerException e){
                throw new NullPointerException();
            }
            if (loc != null) {
                image = new Image(loc);
            }
            eventBtn.setOnMouseClicked(this::participateToEvent);
            eventBtnLabel.setText("Participate to event");
        }
        iconEventBtn.setImage(image);
    }

    @FXML
    void goBack(MouseEvent event) {
        //torno nella pagina precedente
        if(className.equals("GCHomeUser")){
            goToHome(event);
        } else if (className.equals("GCYourEventsUser")) {
            goToYourEvents(event);
        }
    }

    @FXML
    public void participateToEvent(MouseEvent event) {
        try {
            if (cfacade.participateToEvent(this.eventBean)) {
                alert.displayAlertPopup(Alerts.INFORMATION, "Event participation added successfully!\nYou can now view it on Your Events page");
            } else {
                alert.displayAlertPopup(Alerts.ERROR, "Event participation failed :(");
            }
        } catch (EventAlreadyDeleted e) {
            alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
        goBack(event);
    }

    private void removeParticipationToEvent(MouseEvent event){
        if(cfacade.removeEventParticipation(this.eventBean)){
            alert.displayAlertPopup(Alerts.INFORMATION, "Event participation removed successfully!");
        } else {
            alert.displayAlertPopup(Alerts.ERROR, "Event participation removal failed :(");
        }
        goBack(event);
    }


    @Override
    public void openLink(ActionEvent event) throws URISyntaxException, IOException {
        String link = generateMapLink(eventBean.getEventAddress());
        Desktop.getDesktop().browse(new URI(link));
    }

    private static String generateMapLink(String address){
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        return "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
    }
}
