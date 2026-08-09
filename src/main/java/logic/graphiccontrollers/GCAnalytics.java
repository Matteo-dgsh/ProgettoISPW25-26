package logic.graphiccontrollers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;

import logic.beans.BAnalytics;
import logic.beans.BEvent;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.enums.Alerts;
import logic.view.EssentialGUI;

public class GCAnalytics extends EssentialGUI {
    @FXML
    private Label addressL;

    @FXML
    private Label cityL;

    @FXML
    private Label dateL;

    @FXML
    private Label eventNameL;

    @FXML
    private Label participantsL;

    @FXML
    private Label plannedL;

    @FXML
    private Label timesClickedL;

    @FXML
    private ImageView eventImageView;

    private BEvent eventBean;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static Integer timesClicked;
    private static Integer nParticipants; //partecipanti effettivi (Random tra 0 e participations)
    private static Integer participations;

    @FXML
    public void initialize() {
        //empty
    }

    public void initAnalyticsByBean(BEvent eventBean){
        eventNameL.setText(eventBean.getEventName());
        cityL.setText(eventBean.getEventCity());
        addressL.setText(eventBean.getEventAddress());
        dateL.setText(eventBean.getEventDate());
        try {
            Image eventImage = new Image(new ByteArrayInputStream(eventBean.getEventPicData()));
            this.eventImageView.setImage(eventImage);
        } catch (NullPointerException e) {
            logger.severe("Error during imageView setup");
        }

        //inizializzo l'event bean
        this.eventBean = eventBean;

        this.timesClickedL.setText("" + timesClicked);
        this.plannedL.setText("" + participations);
        this.participantsL.setText("" + nParticipants);
    }

    public static void initParticipantsInfo(BEvent eventBean){
        int eventID = eventBean.getEventID();
        //retrieve del numero delle participations
        //partecipazioni segnate (Query dal DB UserEvent)
        participations = cfacade.retrieveParticipationsToEvent(eventID);
        if (timesClicked == null){
            timesClicked = RANDOM.nextInt(5000 - participations +1) + participations;
            nParticipants = RANDOM.nextInt(participations + 1);
        }
    }

    @FXML
    void exportAnalyticsFile() {
        //popolo il bean con i dati dell'analisi presi dal bean Event che è superclass di bean Analysis
        BAnalytics analytics = null;
        try {
            analytics = new BAnalytics(this.eventBean);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe("Error during analytics bean setup");
        }
        if (analytics!=null){
            analytics.setTimesClicked(Integer.parseInt(this.timesClickedL.getText()));
            analytics.setParticipants(Integer.parseInt(this.participantsL.getText()));
            analytics.setPlannedParticipations(Integer.parseInt(this.plannedL.getText()));
        }


        if(cfacade.exportAnalyticsFile(analytics)){
            alert.displayAlertPopup(Alerts.INFORMATION, "Analytics file exported as a .txt successfully!\nYou can find it into exportedAnalytics folder.");
        } else {
            alert.displayAlertPopup(Alerts.ERROR, "Analytics file export failed!\n Retry...");
        }
    }
}
