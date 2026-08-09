package logic.model;

public class MGroup {

    private Integer groupID;
    private String groupName;
    private Integer eventID;
    private Integer ownerID;

    //SETTERS
    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    //GETTERS

    public Integer getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getEventID() {
        return eventID;
    }

    public Integer getOwnerID() {
        return ownerID;
    }
}
