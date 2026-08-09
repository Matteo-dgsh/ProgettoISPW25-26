package logic.dao;

import logic.exceptions.GroupAlreadyCreated;
import logic.model.MGroup;
import logic.utils.InMemoryDataStore;
import logic.utils.LoggedUser;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementazione di IGroupDAO usata in modalita' Demo: nessuna query SQL,
 * i dati vivono solo in InMemoryDataStore e vengono persi alla chiusura dell'app.
 */
public class GroupDAOInMemory implements IGroupDAO {

    @Override
    public MGroup retrieveGroupByEventID(int eventID) {
        for (MGroup group : InMemoryDataStore.groupsById.values()) {
            if (group.getEventID() != null && group.getEventID() == eventID) {
                return group;
            }
        }
        return new MGroup();
    }

    @Override
    public boolean userInGroup(int userID, Integer groupID) {
        if (groupID == null) {
            return false;
        }
        Set<Integer> members = InMemoryDataStore.groupMembers.get(groupID);
        return members != null && members.contains(userID);
    }

    @Override
    public String getGroupName(Integer groupID) {
        MGroup group = InMemoryDataStore.groupsById.get(groupID);
        return group != null ? group.getGroupName() : null;
    }

    @Override
    public int createGroup(String groupName, int eventID) throws GroupAlreadyCreated {
        for (MGroup group : InMemoryDataStore.groupsById.values()) {
            if (group.getEventID() != null && group.getEventID() == eventID) {
                throw new GroupAlreadyCreated("Group already created for this event ", eventID);
            }
        }
        int newID = InMemoryDataStore.nextGroupID();
        MGroup group = new MGroup();
        group.setGroupID(newID);
        group.setGroupName(groupName);
        group.setEventID(eventID);
        group.setOwnerID(LoggedUser.getUserID());
        InMemoryDataStore.groupsById.put(newID, group);
        InMemoryDataStore.groupMembers.put(newID, new HashSet<>());
        return newID;
    }

    @Override
    public boolean groupOperations(Integer groupID, boolean isJoining) {
        Set<Integer> members = InMemoryDataStore.groupMembers.computeIfAbsent(groupID, id -> new HashSet<>());
        if (isJoining) {
            members.add(LoggedUser.getUserID());
        } else {
            members.remove(LoggedUser.getUserID());
        }
        return true;
    }

    @Override
    public boolean checkUserInGroup(Integer groupID) {
        Set<Integer> members = InMemoryDataStore.groupMembers.get(groupID);
        return members != null && members.contains(LoggedUser.getUserID());
    }
}
