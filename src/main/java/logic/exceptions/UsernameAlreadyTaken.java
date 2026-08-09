package logic.exceptions;

/*Emanuele Moscetta*/

import java.io.Serial;

public class UsernameAlreadyTaken extends Exception {
    @Serial
    private static final long serialVersionUID = 136543L;

    private final String username;

    public UsernameAlreadyTaken(String message, String username){
        super(message);
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }
}
