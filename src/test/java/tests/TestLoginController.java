package tests;

import logic.beans.BUserData;
import logic.controllers.CFacade;
import logic.exceptions.InvalidValueException;
import logic.exceptions.MinimumAgeException;
import logic.exceptions.TextTooLongException;
import logic.exceptions.UsernameAlreadyTaken;
import logic.utils.LoggedUser;
import logic.utils.enums.UserTypes;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*Emanuele Moscetta*/
class TestLoginController {
    CFacade facade;

    private static final String NEWPROVINCE = "Roma";
    private static final String NEWCITY = "Frascati";

    public TestLoginController(){
        facade = new CFacade();
    }

    @Test
    void changeCityToRegisteredUser(){
        LoggedUser.setUserID(1); //loggato come Matteo

        int userID = LoggedUser.getUserID();
        if(facade.changeUserCity(LoggedUser.getUserID(), NEWPROVINCE, NEWCITY) == 1){
            assertEquals(NEWCITY, facade.getCityByUserID(userID));
        }
    }

    @Test
    void registerNewUser(){
        BUserData newUser = new BUserData();
        //sto creando un bean user
        try {
            newUser.setUsername("TestUser");
            newUser.setPassword("TestPassword");
            newUser.setFirstName("TestUsername");
            newUser.setLastName("TestLastname");
            newUser.setGender("Other");
            newUser.setBirthDate(LocalDate.parse("1980-01-01"));
            newUser.setProvince("Frosinone");
            newUser.setCity("Anagni");
            newUser.setType(UserTypes.ORGANIZER);
            facade.registerUser(newUser);
        } catch (InvalidValueException | TextTooLongException | UsernameAlreadyTaken | MinimumAgeException e) {
            Logger.getLogger("NightAgent").log(Level.SEVERE, e.getMessage());
        }
        assertEquals(newUser.getUsername(), facade.getUsernameByID(newUser.getUserID()));
    }
}
