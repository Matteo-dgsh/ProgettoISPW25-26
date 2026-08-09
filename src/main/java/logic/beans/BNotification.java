package logic.beans;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class BNotification {
    private NotificationTypes messageType;
    private Integer clientID; //notifiedID
    private Integer notifierID;
    private Integer eventID;
    private Integer notificationID;
    private String city;
    private UserTypes userType;

    //SETTERS
    public void setMessageType(NotificationTypes messageType) {
        this.messageType = messageType;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public void setNotifierID(Integer notifierID) {
        this.notifierID = notifierID;
    }

    public void setNotificationID(Integer notificationID) {
        this.notificationID = notificationID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }


    //GETTERS

    public NotificationTypes getMessageType() {
        return this.messageType;
    }

    public Integer getClientID() {
        return this.clientID;
    }

    public Integer getNotifierID() {
        return this.notifierID;
    }

    public Integer getEventID() {
        return this.eventID;
    }

    public Integer getNotificationID() {
        return this.notificationID;
    }

    public String getCity() {
        return this.city;
    }

    public UserTypes getUserType() {
        return this.userType;
    }

}
