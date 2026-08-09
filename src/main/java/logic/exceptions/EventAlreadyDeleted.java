package logic.exceptions;

import java.io.Serial;

public class EventAlreadyDeleted extends Exception {
    @Serial
    private static final long serialVersionUID = 84563L;

    public EventAlreadyDeleted(String message){
        super(message);
    }
}
