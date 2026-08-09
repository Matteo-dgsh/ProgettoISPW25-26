package logic.dao;

import logic.exceptions.EventAlreadyAdded;
import logic.model.MEvent;

import java.util.List;

public interface IEventDAO {
    boolean createEvent(MEvent eventModel) throws EventAlreadyAdded;

    boolean editEvent(MEvent eventModel);

    boolean deleteEvent(int eventID);

    List<MEvent> retrieveMyEvents(int userID, int queryType);

    String getEventNameByEventID(int eventID);
}
