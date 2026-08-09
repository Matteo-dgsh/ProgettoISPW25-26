package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import logic.beans.BNotification;
import logic.utils.enums.Alerts;
import logic.utils.LoggedUser;
import logic.utils.enums.NotificationTypes;
import logic.view.EssentialGUI;

import java.util.List;
import java.util.logging.Level;

public class GCNotifications extends EssentialGUI {
    @FXML
    private ListView<String> notificationsLV;
    private List<BNotification> notificationsList;

    private class DeleteButtonCell extends ListCell<String> {
        private HBox hbox;
        private Label label;
        private Region spacer;
        private Button deleteButton;

        public DeleteButtonCell() {
            hbox = new HBox();
            label = new Label();
            HBox.setHgrow(label, Priority.ALWAYS);
            spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            deleteButton = new Button("Delete");
            deleteButton.setOnMouseClicked(event -> {
                getListView().getItems().remove(getItem());
                if (cfacade.deleteNotification(notificationsList.get(getIndex()).getNotificationID(), notificationsList, getIndex())) {
                    //ID notifica, lista (per rimuovere notifica) e indice di item della listview
                    alert.displayAlertPopup(Alerts.INFORMATION, "Removed notification successfully");
                } else {
                    alert.displayAlertPopup(Alerts.ERROR, "Failed to remove notification!");
                }

            });

            hbox.getChildren().addAll(label, spacer, deleteButton);
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(5);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setGraphic(null);
            } else {
                label.setText(item);
                setGraphic(hbox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }

    @FXML
    public void initialize() {

        //Imposta la cell factory personalizzata
        notificationsLV.setCellFactory(param -> new DeleteButtonCell());
        notificationsList = cfacade.retrieveNotifications(LoggedUser.getUserID());
        populateNotificationsLV(notificationsList);

    }

    private void populateNotificationsLV(List<BNotification> notificationsList) {
        try {
            for (int i = 0; i < notificationsList.size(); i++) {
                if (notificationsList.get(i).getMessageType() == NotificationTypes.EVENT_ADDED) {
                    notificationsLV.getItems().add("New event called " + cfacade.getEventNameByEventID(notificationsList.get(i).getEventID()) + " in your city!");
                } else if (notificationsList.get(i).getMessageType() == NotificationTypes.USER_EVENT_PARTICIPATION) {
                    notificationsLV.getItems().add("New user " + cfacade.getUsernameByID(notificationsList.get(i).getNotifierID()) + " participating to your event " + cfacade.getEventNameByEventID(notificationsList.get(i).getEventID()));
                }
            }

        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Invalid notification string value");
        }
    }

}
