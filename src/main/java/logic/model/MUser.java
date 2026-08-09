package logic.model;

import logic.beans.BUserData;
import logic.utils.enums.UserTypes;

public class MUser {
    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;
    private String province;
    private String city;
    private String username;
    private String password;
    private UserTypes userType;
    private int userID;


    public void setCredentialsByBean(BUserData dataBean) {
        this.firstName = dataBean.getFirstName();
        this.lastName = dataBean.getLastName();
        this.birthDate = dataBean.getBirthDate().toString();
        this.gender = dataBean.getGender();
        this.province = dataBean.getProvince();
        this.city = dataBean.getCity();
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
        this.userType = dataBean.getUserType();
    }

    public void setUsrAndPswByBean(BUserData dataBean) {
        this.username = dataBean.getUsername();
        this.password = dataBean.getPassword();
    }

    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    public void setLastName(String lastname) {
        this.lastName = lastname;
    }

    public void setBirthDate(String birthdate) {
        this.birthDate = birthdate;
    }

    public void setGender(String gen) {
        this.gender = gen;
    }

    public void setProvince(String province){
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setId(int id) {
        this.userID = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getBirthDate() {
        return this.birthDate;
    }

    public String getGender() {
        return this.gender;
    }

    public String getProvince(){
        return this.province;
    }

    public String getCity() {
        return this.city;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public UserTypes getUserType() {
        return this.userType;
    }

    public int getUserID() {
        return this.userID;
    }
}
