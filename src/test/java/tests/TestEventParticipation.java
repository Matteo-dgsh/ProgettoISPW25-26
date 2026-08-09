package tests;

import logic.beans.BEvent;
import logic.controllers.CFacade;
import logic.exceptions.EventAlreadyDeleted;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.Test;

import logic.utils.LoggedUser;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*Emanuele Moscetta*/

class TestEventParticipation {
    CFacade facade;

    public TestEventParticipation() {
        facade = new CFacade();
    }

    @Test
    void participateToEventAlreadyCreated() {
        LoggedUser.setUserID(1); //come se mi fossi loggato con Matteo
        List<BEvent> eventsInUserCity = facade.retrieveEvents(UserTypes.USER, "GCHomeUser");

        BEvent chosenEvent = eventsInUserCity.getFirst();
        boolean participationResult;
        try {
            participationResult = facade.participateToEvent(chosenEvent);

            // Verifico che il risultato di partecipazione sia true
            assertTrue(participationResult, "Participation result should be true");
        } catch (EventAlreadyDeleted e) {
            Logger.getLogger("NightAgent").log(Level.SEVERE, "Event already deleted");
        }
    }

    @Test
    void removeEventParticipation() {
        LoggedUser.setUserID(1); //come se mi fossi loggato con Matteo
        List<BEvent> eventsInUserCity = facade.retrieveEvents(UserTypes.USER, "GCHomeUser");

        BEvent chosenEvent = null;
        for (BEvent bEvent : eventsInUserCity) {
            if (bEvent.getEventID() == 1) {
                chosenEvent = bEvent;
            }
        }

        boolean resultRemoveParticipation = facade.removeEventParticipation(chosenEvent);

        assertTrue(resultRemoveParticipation);
    }

    @Test
    void checkPreviousParticipationToAlreadyPartEvent() {
        LoggedUser.setUserID(1);
        List<BEvent> eventsInUserCity = facade.retrieveEvents(UserTypes.USER, "GCHomeUser");

        BEvent chosenEvent = null;
        for (BEvent bEvent : eventsInUserCity) {
            if (bEvent.getEventID() == 1) {
                chosenEvent = bEvent;
            }
        }

        try {
            facade.participateToEvent(chosenEvent);
        } catch (EventAlreadyDeleted e) {
            Logger.getLogger("NightAgent").log(Level.SEVERE, "Event already deleted");
        }

        boolean resultCheckPreviousParticipation = facade.checkPreviousEventParticipation(chosenEvent);
        assertTrue(resultCheckPreviousParticipation, "Event already participated");
    }
}
