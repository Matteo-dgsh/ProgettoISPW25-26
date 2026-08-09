package logic.controllers;

import logic.beans.BEvent;
import logic.dao.*;
import logic.exceptions.DuplicateEventParticipation;
import logic.exceptions.EventAlreadyDeleted;
import logic.model.MEvent;
import logic.utils.PersistenceClass;

public class CEventParticipation {

    private IUserEventDAO userEventDAO;

    public CEventParticipation() {
        userEventDAO = switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> new UserEventDAOInMemory();
            case JDBC, FILE_SYSTEM -> new UserEventDAO();
        };
    }

    public boolean participateToEvent(BEvent eventBean) throws EventAlreadyDeleted {
        MEvent eventModel = new MEvent(eventBean);
        //in ogni caso scrivi sul database delle notifiche le notifiche per quell'utente
        return userEventDAO.joinUserToEvent(eventModel);
    }

    public boolean removeEventParticipation(BEvent eventBean) {
        MEvent eventModel = new MEvent(eventBean);
        return userEventDAO.removeUserToEvent(eventModel);
    }

    public boolean checkPreviousEventParticipation(BEvent eventBean) {
        try {
            userEventDAO.checkPreviousParticipation(eventBean.getEventID());
            return false;
        } catch (DuplicateEventParticipation e) {
            return true;
        }
    }
}
