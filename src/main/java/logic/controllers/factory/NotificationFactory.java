package logic.controllers.factory;

import logic.model.*;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.UserTypes;

public class NotificationFactory {
    //questa factory crea le notifiche persistite (letta on-demand dalle schermate)

    public Notification createNotification(NotificationTypes notiType, Integer clientOrNotifiedID, NotificationProperties notiProps, Integer eventID, CityData cityData, UserTypes usrType) {
        Notification noti = new LocalNotification();
        noti.setNotificationType(notiType);
        noti.setClientID(clientOrNotifiedID);
        if(notiProps != null) {
            noti.setNotifierID(notiProps.getNotifierID());
            noti.setNotificationID(notiProps.getNotificationID());
        }
        noti.setEventID(eventID);
        if(cityData != null) {
            noti.setCity(cityData.getCity());
            noti.setNewCity(cityData.getNewCity());
        }
        noti.setUserType(usrType);
        return noti;
    }

}
