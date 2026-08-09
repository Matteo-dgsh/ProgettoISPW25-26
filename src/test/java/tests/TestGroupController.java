package tests;

import logic.beans.BGroup;
import logic.controllers.CFacade;
import logic.exceptions.GroupAlreadyCreated;
import logic.exceptions.InvalidGroupName;
import logic.utils.LoggedUser;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*Emanuele Moscetta*/
class TestGroupController {
    private static final String GROUPNAME = "TestGroup";
    CFacade facade;
    public TestGroupController(){
        facade = new CFacade();
    }

    @Test
    void createGroup(){
        //prendo un evento già creato ma ancora senza un gruppo
        int eventID = 3;

        //faccio come se l'avesse creato l'utente 1
        LoggedUser.setUserID(1);

        try {
            facade.createGroup(GROUPNAME, eventID);
        } catch (GroupAlreadyCreated e) {
            Logger.getLogger("NightAgent").log(Level.WARNING, "Group already created for this event");
        } catch (InvalidGroupName e){
            Logger.getLogger("NightAgent").log(Level.WARNING, "Group name cannot be null");
        }
        BGroup newGroupBean = facade.getGroupByEventID(eventID);
        String newGroupName = facade.getGroupNameByGroupID(newGroupBean.getGroupID());
        assertEquals(GROUPNAME, newGroupName);
    }

    @Test
    void joinGroup(){
        //faccio come se fossi l'utente 1
        LoggedUser.setUserID(1);
        //prendo un evento già creato che ora ha un gruppo
        int eventID = 5;

        try {
            //creo un nuovo gruppo per l'evento 4 ed esco immediatamente dal gruppo
            facade.createGroup(GROUPNAME, eventID);
            facade.leaveGroup(facade.getGroupByEventID(eventID).getGroupID());
        } catch (GroupAlreadyCreated e) {
            Logger.getLogger("NightAgent").log(Level.SEVERE, "Group creation failed!");
        } catch (InvalidGroupName e){
            Logger.getLogger("NightAgent").log(Level.WARNING, "Group name cannot be null");
        }

        BGroup groupBean = facade.getGroupByEventID(eventID);
        int groupID = groupBean.getGroupID();

        //faccio il join del gruppo
        if(!facade.joinGroup(groupID)){
            Logger.getLogger("NightAgent").log(Level.SEVERE, "Group join failed!");
        }
        //verifico il risultato finale
        boolean result = facade.userInGroup(LoggedUser.getUserID(), groupID);
        assertTrue(result, "User should be part of the group");
    }
}
