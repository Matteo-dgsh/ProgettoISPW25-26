package logic.dao;

import logic.controllers.factory.NotificationFactory;
import logic.model.Notification;
import logic.model.NotificationProperties;
import logic.utils.*;
import logic.utils.enums.NotificationTypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class NotificationDAOJDBC implements NotificationDAO{
    private NotificationFactory notiFactory;

    public NotificationDAOJDBC() {
        notiFactory = new NotificationFactory();
    }

    @Override
    public void addNotificationToUsers(List<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID) {
        //questo metodo ha come valore di ritorno l'id della notifica appena inserita nel database
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Notifications VALUES (NULL, ?, ?, ?, ?)")) {
            statement.setInt(3, eventID);
            for (Integer notifiedID : notifiedIDs) {
                statement.setInt(1, notifiedID);
                statement.setString(2, notificationTypes.toString());
                statement.setInt(4, LoggedUser.getUserID());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while adding notification to users");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> notifications = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, type, event_id, notifier_id FROM notifications WHERE (notified_id = ?)")) {
            statement.setInt(1, usrID);
            try(ResultSet rs = statement.executeQuery()) {
                Notification msg;
                while (rs.next()) {
                    if (rs.getString(2).equals(NotificationTypes.EVENT_ADDED.toString())) {
                        msg = notiFactory.createNotification(NotificationTypes.EVENT_ADDED, usrID, new NotificationProperties(rs.getInt(4), rs.getInt(1)), rs.getInt(3), null, null);
                    } else {
                        msg = notiFactory.createNotification(NotificationTypes.USER_EVENT_PARTICIPATION, usrID, new NotificationProperties(rs.getInt(4), rs.getInt(1)), rs.getInt(3), null, null);
                    }
                    notifications.add(msg);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while fetching notifications by userID from db");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return notifications;
    }

    @Override
    public boolean deleteNotification(int notificationID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM Notifications WHERE id=?")) {
            statement.setInt(1, notificationID);
            statement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while removing notification from db");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
