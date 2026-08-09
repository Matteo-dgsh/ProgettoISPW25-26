package logic.controllers;

import logic.beans.BEvent;
import logic.beans.BNotification;
import logic.dao.IUserDAO;
import logic.dao.NotificationDAO;
import logic.dao.NotificationDAOCSV;
import logic.dao.NotificationDAOInMemory;
import logic.dao.NotificationDAOJDBC;
import logic.dao.UserDAO;
import logic.dao.UserDAOInMemory;
import logic.model.Notification;
import logic.utils.PersistenceClass;
import logic.utils.enums.NotificationTypes;

import java.util.ArrayList;
import java.util.List;

public class CNotification {
    //questo controller gestisce la persistenza delle notifiche (lette on-demand dalle schermate, niente piu' invio live via socket)
    private NotificationDAO notificationDAO;
    private IUserDAO userDAO;


    public CNotification() {
        switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> {
                this.userDAO = new UserDAOInMemory();
                this.notificationDAO = new NotificationDAOInMemory();
            }
            case FILE_SYSTEM -> {
                this.userDAO = new UserDAO();
                this.notificationDAO = new NotificationDAOCSV();
            }
            case JDBC -> {
                this.userDAO = new UserDAO();
                this.notificationDAO = new NotificationDAOJDBC();
            }
        }
    }

    public List<BNotification> retrieveNotifications(int userID) {
        ArrayList<Notification> notifications = new ArrayList<>(this.notificationDAO.getNotificationsByUserID(userID));
        return makeBeanFromModel(notifications);
    }

    private ArrayList<BNotification> makeBeanFromModel(ArrayList<Notification> notifications) {
        BNotification notiBean;
        ArrayList<BNotification> notiBeanList = new ArrayList<>();
        for (Notification noti : notifications) {
            notiBean = new BNotification();
            notiBean.setMessageType(noti.getNotificationType());
            notiBean.setClientID(noti.getClientID());
            notiBean.setNotifierID(noti.getNotifierID());
            notiBean.setEventID(noti.getEventID());
            notiBean.setNotificationID(noti.getNotificationID());
            notiBeanList.add(notiBean);
        }
        return notiBeanList;
    }

    public boolean deleteNotification(Integer notificationID, List<BNotification> notificationsList, int index) {
        //cancellazione nel DB
        if (this.notificationDAO.deleteNotification(notificationID)) {
            //rimozione dalla lista
            notificationsList.remove(index);
            return true;
        } else {
            return false;
        }
    }

    public void addNotificationToUsers(BEvent eventBean){
        List<Integer> usersIDs = userDAO.getUsersInCity(eventBean.getEventCity());

        //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
        notificationDAO.addNotificationToUsers(usersIDs, NotificationTypes.EVENT_ADDED, eventBean.getEventID());
    }

    public void addNotificationToOrg(BEvent eventBean){
        //notifico l'organizerID della partecipazione all'evento da parte dell'utente
        List<Integer> organizerID = new ArrayList<>();
        organizerID.add(eventBean.getEventOrganizerID());

        notificationDAO.addNotificationToUsers(organizerID, NotificationTypes.USER_EVENT_PARTICIPATION, eventBean.getEventID());
    }

}
