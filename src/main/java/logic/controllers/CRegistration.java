package logic.controllers;

import logic.beans.BUserData;
import logic.dao.IUserDAO;
import logic.dao.UserDAO;
import logic.dao.UserDAOInMemory;
import logic.exceptions.MinimumAgeException;
import logic.exceptions.UsernameAlreadyTaken;
import logic.model.MUser;
import logic.dao.ILocationDAO;
import logic.dao.LocationDAO;
import logic.dao.LocationDAOInMemory;
import logic.utils.PersistenceClass;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


public class CRegistration {
    private ILocationDAO locationDao;
    private IUserDAO userDao;
    private MUser userModel;

    public CRegistration() {
        switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> {
                this.userDao = new UserDAOInMemory();
                this.locationDao = new LocationDAOInMemory();
            }
            case JDBC, FILE_SYSTEM -> {
                this.userDao = new UserDAO();
                this.locationDao = new LocationDAO();
            }
        }
        this.userModel = new MUser();
    }

    public boolean registerUserControl(BUserData usrBean) throws UsernameAlreadyTaken, MinimumAgeException {
        if (checkBirthDate(usrBean.getBirthDate()) == -1) {
            throw new MinimumAgeException("Minimum age requirement not reached");
        } else {
            this.userModel.setCredentialsByBean(usrBean);
            this.userDao.registerUser(this.userModel);
            int registeredUserID = userDao.getUserIDByUsername(this.userModel.getUserName());
            this.userModel.setId(registeredUserID);
            usrBean.setUserID(registeredUserID);
        }
        return true;
    }

    public List<String> getProvincesList() {
        List<String> provincesList;
        provincesList = this.locationDao.getProvincesList();
        return provincesList;
    }

    public List<String> getCitiesList(String selectedProvince) {
        List<String> citiesList;
        citiesList = this.locationDao.getCitiesList(selectedProvince);
        return citiesList;
    }

    private int checkBirthDate(LocalDate birthDate) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        // the user is for sure adult
        if (((today.getYear() - birthDate.getYear()) > 18) || ((today.getYear() - birthDate.getYear()) == 18 && birthDate.getDayOfMonth() <= today.getDayOfMonth() && birthDate.getMonthValue() <= today.getMonthValue())) {
            return 0;
        }
        return -1;
    }
}
