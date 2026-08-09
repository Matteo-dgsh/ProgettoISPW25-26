package logic.dao;

import logic.exceptions.DuplicateEventParticipation;
import logic.exceptions.EventAlreadyDeleted;
import logic.model.MEvent;

public interface IUserEventDAO {
    boolean joinUserToEvent(MEvent eventModel) throws EventAlreadyDeleted;

    boolean removeUserToEvent(MEvent eventModel);

    void checkPreviousParticipation(int eventID) throws DuplicateEventParticipation;

    int getParticipationsToEvent(int id);
}
