package logic.dao;

import logic.controllers.factory.NotificationFactory;
import logic.model.Notification;
import logic.model.NotificationProperties;
import logic.utils.InMemoryDataStore;
import logic.utils.LoggedUser;
import logic.utils.enums.NotificationTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di NotificationDAO usata in modalita' Demo: nessuna query SQL
 * e nessun file .csv, le notifiche vivono solo in InMemoryDataStore e vengono
 * perse alla chiusura dell'app.
 */
public class NotificationDAOInMemory implements NotificationDAO {
    private final NotificationFactory notiFactory;

    public NotificationDAOInMemory() {
        notiFactory = new NotificationFactory();
    }

    @Override
    public void addNotificationToUsers(List<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID) {
        for (Integer notifiedID : notifiedIDs) {
            int newID = InMemoryDataStore.nextNotificationID();
            NotificationProperties notiProps = new NotificationProperties(LoggedUser.getUserID(), newID);
            Notification noti = notiFactory.createNotification(notificationTypes, notifiedID, notiProps, eventID, null, null);
            InMemoryDataStore.notifications.add(noti);
        }
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> result = new ArrayList<>();
        for (Notification noti : InMemoryDataStore.notifications) {
            if (noti.getClientID() != null && noti.getClientID() == usrID) {
                result.add(noti);
            }
        }
        return result;
    }

    @Override
    public boolean deleteNotification(int notificationID) {
        return InMemoryDataStore.notifications.removeIf(noti -> noti.getNotificationID() != null && noti.getNotificationID() == notificationID);
    }
}
