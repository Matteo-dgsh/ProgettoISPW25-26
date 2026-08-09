package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.enums.Alerts;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Level;

public class GCEditEvent extends GCManageEvent {

    private BEvent oldBean;

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now(ZoneId.systemDefault()));
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setOpacity(1);

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now(ZoneId.systemDefault())));
            }
        });
    }
    public void initPageFromBean(BEvent eventB) {
        this.oldBean = eventB;
        this.eventNameTF.setText(eventB.getEventName());

        //province e citta' choicebox
        if (provincesList.isEmpty()) {
            provincesList = cfacade.getProvincesList();
            provincesObsList = FXCollections.observableArrayList(provincesList);
        }
        this.provinceChoiceBox.setItems(provincesObsList);
        setupProvinceBoxListener();
        this.provinceChoiceBox.setValue(eventB.getEventProvince());
        this.cityChoiceBox.setValue(eventB.getEventCity());

        this.eventAddressTF.setText(eventB.getEventAddress());
        setDate(eventB.getEventDate());
        setTime(eventB.getEventTime());
        this.musicGenreBox.setItems(musicGenresList);
        this.musicGenreBox.setValue(eventB.getEventMusicGenre());
        this.pickedFileLabel.setText(eventB.getEventPicPath());

        //salvo localmente il bean evento
        this.eventBean = eventB;
    }

    @FXML
    void editEventControl(MouseEvent event) {
        try {
            setEventBean(this.eventBean);
            if (cfacade.editEvent(this.eventBean)) {
                //alerts
                alert.displayAlertPopup(Alerts.INFORMATION, "Event edited successfully");
                goToPreviousEventPage(event, eventBean);
            } else {
                alert.displayAlertPopup(Alerts.WARNING, "Event adding procedure failed. Please retry...");
            }
        } catch (InvalidValueException | TextTooLongException e) {
            alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }

    @FXML
    void cancelButtonControl(MouseEvent event) {
        //devo tornare all'eventpage corretta, quindi devo ri-inizializzarla di nuovo col bean "vecchio"
        goToPreviousEventPage(event, oldBean);
    }

    private void setTime(String time) {
        String[] timeArr = time.split(":");
        this.eventHourTF.setText(timeArr[0]);
        this.eventMinutesTF.setText(timeArr[1]);
    }

    private void setDate(String dateFromBean) {
        String date = dateFromBean;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.datePicker.setValue(LocalDate.parse(date, formatter));
    }

    private static void goToPreviousEventPage(MouseEvent event, BEvent bean) {
        try {
            URL loc = EssentialGUI.class.getResource("EventPageOrg.fxml");
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            GCEventPageOrg eventPageOrgGC = loader.getController();
            eventPageOrgGC.initEventFromBean(bean, GCEditEvent.class.getSimpleName());
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException |
                 NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            logger.severe("Error caused runtime exception - Interrupting current thread");
            Thread.currentThread().interrupt();
        }

        gui.nextGuiOnClick(event);
    }

}
