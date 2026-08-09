package logic.model;

public class NotificationProperties {
    private Integer notifierID;
    private Integer notificationID;

    public NotificationProperties(Integer notifierID, Integer notificationID){
        this.notifierID = notifierID;
        this.notificationID = notificationID;
    }

    public Integer getNotificationID() {
        return this.notificationID;
    }

    public Integer getNotifierID() {
        return this.notifierID;
    }
}
