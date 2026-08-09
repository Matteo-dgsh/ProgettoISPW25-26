package logic.dao;

import logic.exceptions.UsernameAlreadyTaken;
import logic.model.MUser;
import logic.utils.InMemoryDataStore;
import logic.utils.LoggedUser;
import logic.utils.enums.UserTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di IUserDAO usata in modalita' Demo: nessuna query SQL,
 * i dati vivono solo in InMemoryDataStore e vengono persi alla chiusura dell'app.
 */
public class UserDAOInMemory implements IUserDAO {

    @Override
    public int checkLoginInfo(MUser usrMod, boolean isGoogleAccount) {
        for (MUser stored : InMemoryDataStore.users.values()) {
            boolean usernameMatches = stored.getUserName().equals(usrMod.getUserName());
            boolean passwordMatches = isGoogleAccount || stored.getPassword().equals(usrMod.getPassword());
            if (usernameMatches && passwordMatches) {
                usrMod.setId(stored.getUserID());
                usrMod.setFirstName(stored.getFirstName());
                usrMod.setLastName(stored.getLastName());
                usrMod.setBirthDate(stored.getBirthDate());
                usrMod.setGender(stored.getGender());
                usrMod.setProvince(stored.getProvince());
                usrMod.setCity(stored.getCity());
                usrMod.setUserType(stored.getUserType());
                return 1;
            }
        }
        return 0;
    }

    @Override
    public String getUserCityByID(int usrId) {
        MUser stored = InMemoryDataStore.users.get(usrId);
        return stored != null ? stored.getCity() : null;
    }

    @Override
    public void registerUser(MUser usrModel) throws UsernameAlreadyTaken {
        for (MUser stored : InMemoryDataStore.users.values()) {
            if (stored.getUserName().equals(usrModel.getUserName())) {
                throw new UsernameAlreadyTaken("Username already taken", usrModel.getUserName());
            }
        }
        int newID = InMemoryDataStore.nextUserID();
        usrModel.setId(newID);
        InMemoryDataStore.users.put(newID, usrModel);
        InMemoryDataStore.userStatus.put(newID, "Offline");
    }

    @Override
    public int getUserIDByUsername(String username) {
        for (MUser stored : InMemoryDataStore.users.values()) {
            if (stored.getUserName().equals(username)) {
                return stored.getUserID();
            }
        }
        return 0;
    }

    @Override
    public void setStatus(int userID) {
        InMemoryDataStore.userStatus.put(userID, LoggedUser.getStatus());
    }

    @Override
    public int changeCity(int userID, String province, String city) {
        MUser stored = InMemoryDataStore.users.get(userID);
        if (stored == null) {
            return 0;
        }
        stored.setProvince(province);
        stored.setCity(city);
        LoggedUser.setProvince(province);
        LoggedUser.setCity(city);
        return 1;
    }

    @Override
    public String getUsernameByID(int userID) {
        MUser stored = InMemoryDataStore.users.get(userID);
        return stored != null ? stored.getUserName() : null;
    }

    @Override
    public List<Integer> getUsersInCity(String city) {
        List<Integer> usersIDs = new ArrayList<>();
        for (MUser stored : InMemoryDataStore.users.values()) {
            if (city.equals(stored.getCity()) && stored.getUserType() == UserTypes.USER) {
                usersIDs.add(stored.getUserID());
            }
        }
        return usersIDs;
    }
}
