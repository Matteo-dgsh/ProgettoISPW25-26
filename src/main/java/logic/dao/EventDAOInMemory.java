package logic.dao;

import logic.exceptions.EventAlreadyAdded;
import logic.model.MEvent;
import logic.model.MUser;
import logic.utils.InMemoryDataStore;
import logic.utils.LoggedUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di IEventDAO usata in modalita' Demo: nessuna query SQL,
 * i dati vivono solo in InMemoryDataStore e vengono persi alla chiusura dell'app.
 */
public class EventDAOInMemory implements IEventDAO {

    @Override
    public boolean createEvent(MEvent eventModel) throws EventAlreadyAdded {
        for (MEvent stored : InMemoryDataStore.events.values()) {
            if (stored.getEventName().equals(eventModel.getEventName())) {
                throw new EventAlreadyAdded("Event name already used", eventModel.getEventName());
            }
        }
        int newID = InMemoryDataStore.nextEventID();
        eventModel.setEventID(newID);
        InMemoryDataStore.events.put(newID, eventModel);
        return true;
    }

    @Override
    public boolean editEvent(MEvent eventModel) {
        InMemoryDataStore.events.put(eventModel.getEventID(), eventModel);
        return true;
    }

    @Override
    public boolean deleteEvent(int eventID) {
        InMemoryDataStore.events.remove(eventID);
        return true;
    }

    @Override
    public List<MEvent> retrieveMyEvents(int userID, int queryType) {
        switch (queryType) {
            case 0: //ORGANIZER && YourEventsOrg
                return retrieveEventsByOrganizer(userID);
            case 1: //USER && HomeUser
                return retrieveEventsByCity(userID);
            default: //USER && YourEventsUser
                return retrieveEventsByParticipation();
        }
    }

    private List<MEvent> retrieveEventsByOrganizer(int userID) {
        List<MEvent> result = new ArrayList<>();
        for (MEvent event : InMemoryDataStore.events.values()) {
            if (event.getEventOrganizerID() == userID) {
                result.add(event);
            }
        }
        return result;
    }

    private List<MEvent> retrieveEventsByCity(int userID) {
        List<MEvent> result = new ArrayList<>();
        MUser user = InMemoryDataStore.users.get(userID);
        String city = user != null ? user.getCity() : null;
        for (MEvent event : InMemoryDataStore.events.values()) {
            if (event.getEventCity() != null && event.getEventCity().equals(city)) {
                result.add(event);
            }
        }
        return result;
    }

    private List<MEvent> retrieveEventsByParticipation() {
        List<MEvent> result = new ArrayList<>();
        for (InMemoryDataStore.Participation p : InMemoryDataStore.participations) {
            if (p.userID() == LoggedUser.getUserID()) {
                MEvent event = InMemoryDataStore.events.get(p.eventID());
                if (event != null) {
                    result.add(event);
                }
            }
        }
        return result;
    }

    @Override
    public String getEventNameByEventID(int eventID) {
        MEvent event = InMemoryDataStore.events.get(eventID);
        return event != null ? event.getEventName() : null;
    }
}
