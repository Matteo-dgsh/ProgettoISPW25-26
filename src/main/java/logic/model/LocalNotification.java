package logic.model;

import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class LocalNotification implements Notification{
    private NotificationTypes messageType;
    private Integer notifiedID;
    private Integer eventID;
    private Integer notifierID;
    private Integer notificationID;


    @Override
    public void setNotificationType(NotificationTypes msgType) {
        this.messageType = msgType;
    }

    @Override
    public void setCity(String city) {
        //not used
    }

    @Override
    public void setNewCity(String newCity){
        //not used
    }

    @Override
    public void setClientID(Integer notifiedID) {
        this.notifiedID = notifiedID;
    }

    @Override
    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    @Override
    public void setUserType(UserTypes userType) {
       //not used
    }

    @Override
    public void setNotifierID(Integer notifierID) {
        this.notifierID = notifierID;
    }

    @Override
    public void setNotificationID(Integer notificationID){
        this.notificationID = notificationID;
    }

    @Override
    public NotificationTypes getNotificationType() {
        return this.messageType;
    }

    @Override
    public String getCity() {
        return null;
    }

    @Override
    public String getNewCity(){
        return null;
    }

    @Override
    public Integer getClientID() {
        return this.notifiedID;
    }

    @Override
    public Integer getEventID() {
        return this.eventID;
    }


    @Override
    public UserTypes getUserType() {
        return null;
    }


    @Override
    public Integer getNotifierID() {
        return this.notifierID;
    }

    @Override
    public Integer getNotificationID(){
        return this.notificationID;
    }
}
