package logic.controllers;

import logic.dao.IUserDAO;
import logic.dao.UserDAO;
import logic.dao.UserDAOInMemory;
import logic.model.MUser;
import logic.beans.BUserData;
import logic.utils.LoggedUser;
import logic.utils.PersistenceClass;

public class CLogin {
    private IUserDAO userDao;
    private MUser userModel;

    public CLogin() {
        this.userDao = switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> new UserDAOInMemory();
            case JDBC, FILE_SYSTEM -> new UserDAO();
        };
        this.userModel = new MUser();
    }

    //Il caso Google non fa piu' nulla di specifico qui: l'autenticazione OAuth (apertura browser,
    //scambio del codice con le Credential, recupero della mail) avviene a monte, nel chiamante
    //(GCLogin/CLI), tramite GoogleLogin.performGoogleLogin(); qui arriva un bean gia' con lo
    //username (la mail Google) impostato, esattamente come nel login classico.
    public int checkLoginControl(BUserData logBean, boolean isGoogleAuth) {
        this.userModel.setUsrAndPswByBean(logBean); //qui ancora non avviene il controllo della correttezza dei dati,
        int ret = this.userDao.checkLoginInfo(this.userModel, isGoogleAuth); //qui effettivamente è il DAO che va a controllare la correttezza delle credenziali
        if (ret == 1) {
            createLoggedSession();
        }

        return ret;
    }

    public int changeCity(int userID, String province, String city) {
        return this.userDao.changeCity(userID, province, city);
    }

    public String getUsernameByID(int userID) {
        return this.userDao.getUsernameByID(userID);
    }

    private void createLoggedSession() {
        LoggedUser.setUserID(this.userModel.getUserID());
        LoggedUser.setUserName(this.userModel.getUserName());
        LoggedUser.setUserType(this.userModel.getUserType());
        LoggedUser.setFirstName(this.userModel.getFirstName());
        LoggedUser.setLastName(this.userModel.getLastName());
        LoggedUser.setGender(this.userModel.getGender());
        LoggedUser.setProvince(this.userModel.getProvince());
        LoggedUser.setCity(this.userModel.getCity());
        LoggedUser.setBirthDate(this.userModel.getBirthDate());
        LoggedUser.setStatus("Online");
        //set status gestita dal UserDAO
        this.userDao.setStatus(this.userModel.getUserID());
    }

    public void closeLoginSession() {
        LoggedUser.setUserID(0);
        LoggedUser.setUserName(null);
        LoggedUser.setUserType(null);
        LoggedUser.setFirstName(null);
        LoggedUser.setLastName(null);
        LoggedUser.setGender(null);
        LoggedUser.setProvince(null);
        LoggedUser.setCity(null);
        LoggedUser.setBirthDate(null);
        LoggedUser.setStatus("Offline");
        //set status gestita dal UserDAO
        this.userDao.setStatus(LoggedUser.getUserID());
    }

    public String getCityByUserID(int userID) {
        return userDao.getUserCityByID(userID);
    }
}
