package logic.controllers;

import logic.beans.BEvent;
import logic.dao.*;
import logic.exceptions.EventAlreadyAdded;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.PersistenceClass;
import logic.utils.enums.UserTypes;
import java.util.ArrayList;
import java.util.List;

public class CManageEvent {
    private IEventDAO eventDAO;

    public CManageEvent() {
        eventDAO = switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> new EventDAOInMemory();
            case JDBC, FILE_SYSTEM -> new EventDAO();
        };
    }

    public boolean addEvent(BEvent eventBean) throws EventAlreadyAdded {
        MEvent eventModel = new MEvent(eventBean);
        if (eventDAO.createEvent(eventModel)) {
            eventBean.setEventID(eventModel.getEventID());
            return true;
        } else {
            return false;
        }
    }

    /*
    Il retrieve degli eventi va distinto in 3 casi:
    1: organizer si trova su schermata YourEventsOrg e gli vengono mostrati gli eventi che ha pubblicato (sia passati che futuri) - query con campo organizer_id
    2: user si trova su schermata HomeUser e gli vengono mostrati gli eventi disponibili nella sua citta' - query con campo citta'
    3: user si trova su schermata YourEventsUser e gli vengono mostrati gli eventi passati e futuri a cui ha messo la partecipazione - query con relazione user_id ed event_id
    */

    public List<BEvent> retrieveMyEvents(UserTypes usertype, String className) {
        List<MEvent> myEvents;
        if (usertype == UserTypes.ORGANIZER && className.equals("GCYourEventsOrg")) { //Caso 1:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 0);

        } else if (usertype == UserTypes.USER && className.equals("GCHomeUser")) {    //Caso 2:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 1);
        } else {                                                                        //Caso 3:
            myEvents = eventDAO.retrieveMyEvents(LoggedUser.getUserID(), 2);
        }
        return getEventBeansListFromModelsList(myEvents);
    }

    public boolean editEvent(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        return eventDAO.editEvent(eventModel);
    }

    public boolean deleteEvent(int eventID) {
        return eventDAO.deleteEvent(eventID);
    }

    private List<BEvent> getEventBeansListFromModelsList(List<MEvent> eventModelList) {
        List<BEvent> myEventsBeans = new ArrayList<>();
        BEvent tempEventBean;
        for (MEvent mEvent : eventModelList) {
            tempEventBean = mEvent.getEventInfo();
            myEventsBeans.add(tempEventBean);
        }
        return myEventsBeans;
    }

    public String getEventNameByEventID(int eventID) {
        return eventDAO.getEventNameByEventID(eventID);
    }

}
