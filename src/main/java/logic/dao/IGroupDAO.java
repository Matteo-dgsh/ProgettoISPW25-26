package logic.dao;

import logic.exceptions.GroupAlreadyCreated;
import logic.model.MGroup;

public interface IGroupDAO {
    MGroup retrieveGroupByEventID(int eventID);

    boolean userInGroup(int userID, Integer groupID);

    String getGroupName(Integer groupID);

    int createGroup(String groupName, int eventID) throws GroupAlreadyCreated;

    boolean groupOperations(Integer groupID, boolean isJoining);

    boolean checkUserInGroup(Integer groupID);
}
