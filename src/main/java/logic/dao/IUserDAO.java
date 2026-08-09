package logic.dao;

import logic.exceptions.UsernameAlreadyTaken;
import logic.model.MUser;

import java.util.List;

public interface IUserDAO {
    int checkLoginInfo(MUser usrMod, boolean isGoogleAccount);

    String getUserCityByID(int usrId);

    void registerUser(MUser usrModel) throws UsernameAlreadyTaken;

    int getUserIDByUsername(String username);

    void setStatus(int userID);

    int changeCity(int userID, String province, String city);

    String getUsernameByID(int userID);

    List<Integer> getUsersInCity(String city);
}
