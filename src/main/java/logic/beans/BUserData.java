package logic.beans;

import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.enums.UserTypes;

import java.time.LocalDate;

public class BUserData {
    private int userID;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String province;
    private String city;
    private UserTypes userType;
    private String username;
    private String password;


    public BUserData() {
        //empty
    }

    public BUserData(String username) {
        this.username = username;
    }

    public BUserData(String username, String password) {
        this(username);
        this.password = password;
    }

    public void setFirstName(String firstName) throws InvalidValueException, TextTooLongException {
        if(firstName == null || firstName.equalsIgnoreCase("")) {
            throw new InvalidValueException("Please insert a valid name");
        }
        else if(firstName.length() > 20) {
            throw new TextTooLongException("Too many characters for name field");
        }
        this.firstName = firstName;
    }

    public void setLastName(String lastName) throws InvalidValueException, TextTooLongException {
        if(lastName == null || lastName.equalsIgnoreCase("")){
            throw new InvalidValueException("Please insert a valid lastname");
        }
        else if(lastName.length() > 20) {
            throw new TextTooLongException("Too many characters for lastname field");
        }
        this.lastName = lastName;
    }

    public void setBirthDate(LocalDate birthDate) throws InvalidValueException {
        if(birthDate == null){
            throw new InvalidValueException("Please insert a valid date of birth");
        }
        this.birthDate = LocalDate.parse(birthDate.toString());
    }

    public void setProvince(String province) throws InvalidValueException {
        if(province == null){
            throw new InvalidValueException("Please insert a valid province");
        }
        this.province = province;
    }

    public void setCity(String city) throws InvalidValueException {
        if(city == null){
            throw new InvalidValueException("Please insert a valid city");
        }
        this.city = city;
    }


    public void setGender(String gender) throws InvalidValueException {
        if(gender == null){
            throw new InvalidValueException("Please insert a valid gender type");
        }
        this.gender = gender;
    }

    public void setType(UserTypes typeOfUsr) {
        this.userType = typeOfUsr;
    }

    public void setUsername(String usrName) throws InvalidValueException, TextTooLongException {
        if(usrName == null || usrName.equalsIgnoreCase("")) {
            throw new InvalidValueException("Please insert a valid username");
        }
        else if(usrName.length() > 30) {
            throw new TextTooLongException("Too many characters for username field");
        }
        this.username = usrName;
    }

    public void setPassword(String passwd) throws InvalidValueException, TextTooLongException {
        if(passwd == null || passwd.equalsIgnoreCase("")) {
            throw new InvalidValueException("Please insert a valid password");
        }
        else if(passwd.length() > 25) {
            throw new TextTooLongException("Too many character for password field");
        }
        this.password = passwd;
    }

    public void setUserID(int id){
        this.userID = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public UserTypes getUserType() {
        return this.userType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getProvince(){
        return this.province;
    }

    public String getCity() {
        return this.city;
    }

    public int getUserID(){return this.userID;}
}
