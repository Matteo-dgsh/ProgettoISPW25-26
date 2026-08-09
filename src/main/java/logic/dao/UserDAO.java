package logic.dao;

import logic.exceptions.UsernameAlreadyTaken;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;
import logic.model.MUser;
import logic.utils.enums.UserTypes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class UserDAO implements IUserDAO {

    public int checkLoginInfo(MUser usrMod, boolean isGoogleAccount) {
        int ret = 0;
        if (!isGoogleAccount) {
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, firstName, lastName, dateOfBirth, gender, province, city, userType FROM users WHERE (username=? and password=?)")) {
                statement.setString(1, usrMod.getUserName());
                statement.setString(2, usrMod.getPassword());
                ret = getLoggedUser(statement, usrMod);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException occurred while fetching user credentials");
            } finally {
                SingletonDBSession.getInstance().closeConn();
            }
        } else {
            //Google Login, verifico la presenza nel db della email Google
            try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id, firstName, lastName, dateOfBirth, gender, province, city, userType FROM users WHERE (username=?)")) {
                statement.setString(1, usrMod.getUserName());
                ret = getLoggedUser(statement, usrMod);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQLException occurred while fetching user credentials");
            } finally {
                SingletonDBSession.getInstance().closeConn();
            }
        }
        return ret;
    }

    private int getLoggedUser(PreparedStatement statement, MUser usrMod) {
        //verifica il tipo di utente che siamo
        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                usrMod.setId(rs.getInt(1));
                usrMod.setFirstName(rs.getString(2));
                usrMod.setLastName(rs.getString(3));
                usrMod.setBirthDate(rs.getString(4));
                usrMod.setGender(rs.getString(5));
                usrMod.setProvince(rs.getString(6));
                usrMod.setCity(rs.getString(7));

                //conversione da stringa a UserType
                String usrtypeString = rs.getString(8);
                if (usrtypeString.equals(UserTypes.USER.toString())) {
                    usrMod.setUserType(UserTypes.USER);
                } else {
                    usrMod.setUserType(UserTypes.ORGANIZER);
                }
                return 1;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting logged user");
        }
        return 0;
    }

    public String getUserCityByID(int usrId) {
        String userCity = null;

        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT city FROM users WHERE (id=?)")) {
            statement.setInt(1, usrId);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                userCity = rs.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting user city by userID");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return userCity;
    }

    public void registerUser(MUser usrModel) throws UsernameAlreadyTaken {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO Users (id, username, password, firstName, lastName, dateOfBirth, gender, province, city, userType,  userStatus) VALUES(NULL,?,?,?,?,?,?,?,?,?,?)")) {
            statement.setString(1, usrModel.getUserName());
            statement.setString(2, usrModel.getPassword());
            statement.setString(3, usrModel.getFirstName());
            statement.setString(4, usrModel.getLastName());
            statement.setString(5, usrModel.getBirthDate());
            statement.setString(6, usrModel.getGender());
            statement.setString(7, usrModel.getProvince());
            statement.setString(8, usrModel.getCity());
            statement.setString(9, usrModel.getUserType().toString());
            statement.setString(10, "Offline");
            statement.execute();
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062) {
                throw new UsernameAlreadyTaken(e.getMessage(), usrModel.getUserName());
            }
            logger.log(Level.SEVERE, "SQLException occurred while adding new user in db");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public int getUserIDByUsername(String username) {
        int userID = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id FROM users WHERE (username = ?)")) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    userID = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting userID by username");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return userID;
    }

    public void setStatus(int userID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("UPDATE users SET userStatus = ? WHERE id = ?")) {
            statement.setString(1, LoggedUser.getStatus());
            statement.setInt(2, userID);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while setting user status");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public int changeCity(int userID, String province, String city) {
        int res = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("UPDATE users SET province = ?, city = ? WHERE id = ?")) {
            statement.setString(1, province);
            statement.setString(2, city);
            statement.setInt(3, userID);
            res = statement.executeUpdate();
            if (res == 1) {
                LoggedUser.setProvince(province);
                LoggedUser.setCity(city);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while changing user city");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return res;
    }

    public String getUsernameByID(int userID) {
        String username = null;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT username FROM users WHERE (id = ?)")) {
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting username by userID");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return username;
    }

    public List<Integer> getUsersInCity(String city) {
        List<Integer> usersIDs = new ArrayList<>();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT id FROM users WHERE (city = ? AND userType = ?)")) {
            statement.setString(1, city);
            statement.setString(2, "USER");
            try(ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    usersIDs.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting userIDs by city name");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return usersIDs;
    }

}
