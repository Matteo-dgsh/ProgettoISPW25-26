package logic.controllers;

import logic.beans.*;
import logic.exceptions.*;
import logic.model.Message;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;
import logic.view.ChatView;
import logic.view.NotificationView;

import java.util.List;

public class CFacade {
    private final CLogin loginController = new CLogin();
    private final CRegistration regController = new CRegistration();
    private final CManageEvent manageEventController = new CManageEvent();
    private final CEventParticipation eventParticipationController = new CEventParticipation();
    private final CNotification notificationController = new CNotification();
    private final CAnalytics analyticsController = new CAnalytics();
    private final CGroup groupController = new CGroup();
    private final CGroupChat chatController = new CGroupChat();
    private ChatView chatView;
    private static NotificationView notiView;

    public CFacade() {
        //empty
    }

    //Metodi che notificano gli utenti (persistenza su database, niente piu' invio live via socket)
    public boolean addEvent(BEvent bean) throws EventAlreadyAdded {
        boolean res = manageEventController.addEvent(bean); //chiamata al controller effettivo
        if (res) {
            notificationController.addNotificationToUsers(bean);
        }
        return res;
    }

    public boolean deleteEvent(int eventID) {
        return manageEventController.deleteEvent(eventID);
    }

    public boolean participateToEvent(BEvent eventBean) throws EventAlreadyDeleted {
        boolean res = eventParticipationController.participateToEvent(eventBean);
        if (res) {
            notificationController.addNotificationToOrg(eventBean);
        }
        return res;
    }

    public boolean removeEventParticipation(BEvent eventBean) {
        boolean result = false;

        boolean res = eventParticipationController.removeEventParticipation(eventBean);
        if (res) {
            Integer groupID = getGroupByEventID(eventBean.getEventID()).getGroupID();
            result = leaveGroupAfterRemoveEventPart(groupID);
        }
        return result;
    }

    private boolean leaveGroupAfterRemoveEventPart(Integer groupID){
        //se il gruppo non esiste salto il leaveGroup
        if (groupID == null || !checkUserInGroup(groupID)) {
            //gruppo non esistente o utente non nel gruppo
            return true;
        }

        //eseguo questo se gruppo esiste e l'utente ne fa parte
        return groupController.leaveGroup(groupID);
    }

    private boolean checkUserInGroup(Integer groupID) {
        return groupController.checkUserInGroup(groupID);
    }

    public boolean checkPreviousEventParticipation(BEvent eventBean) {
        return eventParticipationController.checkPreviousEventParticipation(eventBean);
    }


    public boolean registerUser(BUserData bean) throws UsernameAlreadyTaken, MinimumAgeException {
        return regController.registerUserControl(bean);
    }

    public int loginUser(BUserData bean, boolean isGoogleAuth) {
        return loginController.checkLoginControl(bean, isGoogleAuth);
    }

    public void signOut() {
        loginController.closeLoginSession();
    }

    public boolean createGroup(String groupName, int eventID) throws GroupAlreadyCreated, InvalidGroupName {
        boolean res = false;

        int newGroupID = groupController.createGroup(groupName, eventID); //res == new groupID
        if (newGroupID > 0) {
            res = this.joinGroup(newGroupID);
        }
        return res;
    }

    public boolean joinGroup(Integer groupID) {
        return groupController.joinGroup(groupID);
    }

    public boolean leaveGroup(Integer groupID) {
        return groupController.leaveGroup(groupID);
    }

    public boolean sendMessageToGroup(BMessage message) {
        return chatController.writeMessage(message);
    }


    //Metodi che non interagiscono col server
    public List<BEvent> retrieveEvents(UserTypes userType, String className) {
        return manageEventController.retrieveMyEvents(userType, className);
    }

    public boolean editEvent(BEvent eventBean) {
        return manageEventController.editEvent(eventBean);
    }


    public int retrieveParticipationsToEvent(int id) {
        return analyticsController.getParticipationsToEvent(id);
    }


    public List<String> getProvincesList() {
        return regController.getProvincesList();
    }

    public List<String> getCitiesList(String province) {
        return regController.getCitiesList(province);
    }


    public List<BNotification> retrieveNotifications(int userID) {
        return notificationController.retrieveNotifications(userID);
    }

    public String getEventNameByEventID(int eventID) {
        return manageEventController.getEventNameByEventID(eventID);
    }

    public int changeUserCity(int userID, String newProvince, String newCity) {
        return loginController.changeCity(userID, newProvince, newCity);
    }

    public String getUsernameByID(int userID) {
        return loginController.getUsernameByID(userID);
    }

    public String getCityByUserID(int userID) {
        return loginController.getCityByUserID(userID);
    }

    public boolean deleteNotification(Integer notificationID, List<BNotification> notificationsList, int index) {
        return notificationController.deleteNotification(notificationID, notificationsList, index);
    }

    public List<BGroup> retrieveGroups(List<BEvent> upcomingEventsList) {
        return groupController.retrieveGroups(upcomingEventsList);
    }

    public BGroup getGroupByEventID(int eventID) {
        return groupController.getGroupByEventID(eventID);
    }

    public boolean userInGroup(int userID, Integer groupID) {
        return groupController.userInGroup(userID, groupID);
    }

    public String getGroupNameByGroupID(Integer groupID) {
        return groupController.getGroupNameByGroupID(groupID);
    }

    public List<BMessage> retrieveGroupChat(Integer groupID) {
        return chatController.retrieveGroupChat(groupID);
    }

    public static void setNotiGraphic(NotificationView notificationView) {
        notiView = notificationView;
    }

    public void setChatGraphic(ChatView chatView) {
        this.chatView = chatView;
    }

    public void showNotification(NotificationTypes notiType) {
        notiView.showNotification(notiType);
    }

    public void addMessageToChat(Message msg) {
        //anche qui l'unico caso e' quello della group chat quindi non faccio controlli aggiuntivi
        BMessage beanMsg = new BMessage(msg);
        this.chatView.addMessageToChat(beanMsg);
    }


    public boolean exportAnalyticsFile(BAnalytics analysis) {
        return analyticsController.exportAnalyticsFile(analysis);
    }


}
