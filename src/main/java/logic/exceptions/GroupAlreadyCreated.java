package logic.exceptions;

import java.io.Serial;

public class GroupAlreadyCreated extends Exception{
    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer eventID;


    public GroupAlreadyCreated(String message, Integer eventID){
        super(message);
        this.eventID = eventID;
    }

    public Integer getEventID(){
        return this.eventID;
    }


}
