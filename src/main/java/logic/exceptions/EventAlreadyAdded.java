package logic.exceptions;

/*Emanuele Moscetta*/

import java.io.Serial;

public class EventAlreadyAdded extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String eventName;


    public EventAlreadyAdded(String message, String eventName){
        super(message);
        this.eventName = eventName;
    }

    public String getEventName(){
        return this.eventName;
    }
}
