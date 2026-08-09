package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;
import logic.beans.BGroup;
import logic.utils.enums.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static logic.graphiccontrollers.GCYourEventsGeneral.getBeanFromListView;

public class GCHomeUser extends EssentialGUI implements DoubleClickListener {
    @FXML
    private ListView<String> eventsListView;

    @FXML
    private ListView<String> musicListView;

    @FXML
    private ListView<String> groupsListView;

    private List<BEvent> eventsList = new ArrayList<>();
    private List<BGroup> groupsList = new ArrayList<>();
    private List<BEvent> upcomingEventsList = new ArrayList<>();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void initialize() {
        //Retrieve eventi
        eventsList = cfacade.retrieveEvents(LoggedUser.getUserType(), this.getClass().getSimpleName());
        populateEventsListView();
        setupEventClickListener();

        //Retrieve gruppi
        groupsList = cfacade.retrieveGroups(upcomingEventsList);
        populateGroupsListView();
    }

    private void populateEventsListView() {
        for (BEvent bEvent : eventsList) {
            String eventDateString = bEvent.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, dateTimeFormatter);
            if (LocalDate.now(ZoneId.systemDefault()).minusDays(1).isBefore(date)) {
                this.eventsListView.getItems().add(bEvent.getEventName());
                this.musicListView.getItems().add(bEvent.getEventMusicGenre());
                this.upcomingEventsList.add(bEvent);
            }
        }
    }

    private void populateGroupsListView() {
        for (BGroup bGroup : groupsList) {
            if (bGroup.getGroupID() != null) {
                this.groupsListView.getItems().add(bGroup.getGroupName());
            } else {
                this.groupsListView.getItems().add("No group");
            }
        }
    }

    public void setupEventClickListener() {
        eventsListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                BEvent selectedEventBean = getBeanFromListView(eventsListView, upcomingEventsList);
                try {
                    if (selectedEventBean != null) {
                        onItemDoubleClick(event, selectedEventBean, "EventPageUser.fxml");
                    }
                } catch (RuntimeException e) {
                    alert.displayAlertPopup(Alerts.ERROR, "Fatal: " + e.getMessage());
                }
            }
        });
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
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());

            GCEventPageUser eventPageUserGC = loader.getController();
            eventPageUserGC.initEventFromBean(selectedEventBean, GCHomeUser.class.getSimpleName());
            eventPageUserGC.initEventPageButton();
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        } catch (RuntimeException e) {
            logger.severe("Error during next event page preload");
        }
        gui.nextGuiOnClick(event);
    }

}


