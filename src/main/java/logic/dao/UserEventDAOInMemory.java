package logic.dao;

import logic.exceptions.DuplicateEventParticipation;
import logic.exceptions.EventAlreadyDeleted;
import logic.model.MEvent;
import logic.utils.InMemoryDataStore;
import logic.utils.LoggedUser;

/**
 * Implementazione di IUserEventDAO usata in modalita' Demo: nessuna query SQL,
 * i dati vivono solo in InMemoryDataStore e vengono persi alla chiusura dell'app.
 */
public class UserEventDAOInMemory implements IUserEventDAO {

    @Override
    public boolean joinUserToEvent(MEvent eventModel) throws EventAlreadyDeleted {
        if (!InMemoryDataStore.events.containsKey(eventModel.getEventID())) {
            throw new EventAlreadyDeleted("Event already deleted or modified: " + eventModel.getEventID());
        }
        InMemoryDataStore.participations.add(new InMemoryDataStore.Participation(LoggedUser.getUserID(), eventModel.getEventID()));
        return true;
    }

    @Override
    public boolean removeUserToEvent(MEvent eventModel) {
        InMemoryDataStore.participations.removeIf(p -> p.eventID() == eventModel.getEventID() && p.userID() == LoggedUser.getUserID());
        return true;
    }

    @Override
    public void checkPreviousParticipation(int eventID) throws DuplicateEventParticipation {
        for (InMemoryDataStore.Participation p : InMemoryDataStore.participations) {
            if (p.userID() == LoggedUser.getUserID() && p.eventID() == eventID) {
                throw new DuplicateEventParticipation("Event already joined by user. Choose another event from list if available!");
            }
        }
    }

    @Override
    public int getParticipationsToEvent(int id) {
        int count = 0;
        for (InMemoryDataStore.Participation p : InMemoryDataStore.participations) {
            if (p.eventID() == id) {
                count++;
            }
        }
        return count;
    }
}
