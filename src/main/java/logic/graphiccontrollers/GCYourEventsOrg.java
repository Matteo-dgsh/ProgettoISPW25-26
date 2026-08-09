package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import logic.beans.BEvent;
import logic.utils.enums.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;


public class GCYourEventsOrg extends GCYourEventsGeneral implements DoubleClickListener {

    @FXML
    private ListView<String> analyticsLV;

    @FXML
    private ListView<String> musicLV;

    @FXML
    private ListView<String> pastEventsLV;

    @FXML
    private ListView<String> timeAndDateLV;

    @FXML
    private ListView<String> upcEventsLV;
    
    private List<BEvent> eventsBeanList = new ArrayList<>();


    private ArrayList <BEvent> upComingEventsBeans = new ArrayList<>();
    private ArrayList <BEvent> pastEventsBeans = new ArrayList<>();

    @FXML
    public void initialize() {
        this.eventsBeanList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        if (eventsBeanList != null) {
            populateLVs();
            setupEventClickListener();
        }
    }

    private void populateLVs() {

        for (BEvent bEvent : eventsBeanList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, dateTimeFormatter);

            if (LocalDate.now(ZoneId.systemDefault()).minusDays(1).isBefore(date)) {
                upComingEventsBeans.add(bEvent);
                this.upcEventsLV.getItems().add(bEvent.getEventName());
                this.timeAndDateLV.getItems().add(formatTimeAndDate(bEvent.getEventDate(), bEvent.getEventTime()));
                this.musicLV.getItems().add(bEvent.getEventMusicGenre());

            } else {
                pastEventsBeans.add(bEvent);
                this.pastEventsLV.getItems().add(bEvent.getEventName());
                this.analyticsLV.getItems().add(bEvent.getEventName() + " - Analytics");
            }

        }
    }

    @Override
    public void setupEventClickListener() {
        analyticsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BEvent selectedEventBean = getBeanFromListView(analyticsLV, pastEventsBeans);
                showAnalytics(event, selectedEventBean);
            }
        });
        upcEventsLV.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Verifica se è stato effettuato un doppio clic
                BEvent selectedEventBean = getBeanFromListView(upcEventsLV, upComingEventsBeans);
                showEventPage(event, selectedEventBean);
            }
        });
    }

    private void showEventPage(MouseEvent event, BEvent selectedEventBean) {
        try {
            if (selectedEventBean != null) {
                onItemDoubleClick(event, selectedEventBean, "EventPageOrg.fxml");
            }
        } catch (RuntimeException e) {
            alert.displayAlertPopup(Alerts.ERROR, "Fatal: " + e.getMessage());
        }
    }

    private void showAnalytics(MouseEvent event, BEvent selectedEventBean) {
        try {
            if (selectedEventBean != null) {
                onItemDoubleClick(event, selectedEventBean, "Analytics.fxml");
            }
        } catch (RuntimeException e) {
            alert.displayAlertPopup(Alerts.ERROR, "Fatal: " + e.getMessage());
        }
    }

    @Override
    public void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean, String fxmlpage) {
        changePage(event, selectedEventBean, fxmlpage);
    }

    private static void changePage(MouseEvent event, BEvent selectedEventBean, String fxmlpage){
        try {
            URL loc = EssentialGUI.class.getResource(fxmlpage);
            FXMLLoader loader = new FXMLLoader(loc);
            Parent root = null;
            if (loc != null) {
                root = loader.load();
            }
            if (fxmlpage.equals("Analytics.fxml")) {
                GCAnalytics analyticsGC = loader.getController();
                GCAnalytics.initParticipantsInfo(selectedEventBean);
                analyticsGC.initAnalyticsByBean(selectedEventBean);
            } else {
                GCEventPageOrg eventPageOrgGC = loader.getController();
                eventPageOrgGC.initEventFromBean(selectedEventBean, GCYourEventsOrg.class.getSimpleName());
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
        }
        gui.nextGuiOnClick(event);
    }
}
