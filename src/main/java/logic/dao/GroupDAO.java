package logic.dao;

import logic.exceptions.GroupAlreadyCreated;
import logic.model.MGroup;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class GroupDAO implements IGroupDAO {
    public MGroup retrieveGroupByEventID(int eventID) {
        MGroup group = new MGroup();
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_id, group_name, owner_id FROM EventGroups WHERE (event_id = ?)")) {
            statement.setInt(1, eventID); //id_evento preso come parametro
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    group.setGroupID(rs.getInt(1));
                    group.setGroupName(rs.getString(2));
                    group.setEventID(eventID);
                    group.setOwnerID(rs.getInt(3));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while retrieving group by event ID");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return group;
    }

    public boolean userInGroup(int userID, Integer groupID) {
        if (groupID==null){
            return false;
        }
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT UserGroup.user_id, UserGroup.group_id FROM UserGroup WHERE (user_id=? AND group_id=?)")) {
            statement.setInt(1, userID);
            statement.setInt(2, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while checking user in group");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public String getGroupName(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT group_name FROM eventgroups WHERE (group_id=?)")) {
            statement.setInt(1, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting group name");

        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return null;
    }

    public int createGroup(String groupName, int eventID) throws GroupAlreadyCreated { //returna l'id dell'evento creato
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO eventgroups(group_id, group_name, event_id, owner_id) VALUES (NULL, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)){
            statement.setString(1,groupName);
            statement.setInt(2,eventID);
            statement.setInt(3,LoggedUser.getUserID());

            int insertedRows = statement.executeUpdate();
            //recupero id evento dopo l'aggiunta
            if (insertedRows>0){
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        catch (SQLException e) {
            if(e.getErrorCode() == 1062){
                throw new GroupAlreadyCreated("Group already created for this event ", eventID);
            }
            logger.log(Level.SEVERE, "SQLException occurred while creating group");
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return -1;
    }

    public boolean groupOperations (Integer groupID, boolean isJoining){
        String query;
        if(isJoining) {
            query = "INSERT INTO usergroup(id, user_id,group_id) VALUES (NULL, ?, ?)";
        } else {
            query = "DELETE FROM usergroup WHERE (user_id = ? AND group_id = ?)";
        }

        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement(query)){
            statement.setInt(1,LoggedUser.getUserID());
            statement.setInt(2,groupID);
            statement.execute();
            return true;
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, () -> "SQLException occurred while " + (isJoining ? "joining" : "leaving") + " group");
            return false;
        }
        finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public boolean checkUserInGroup(Integer groupID) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT 1 FROM usergroup WHERE (user_id = ? AND group_id = ?)")){
            statement.setInt(1, LoggedUser.getUserID());
            statement.setInt(2, groupID);
            try(ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while checking user in group");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }
}
