package logic.dao;

import logic.exceptions.DuplicateEventParticipation;
import logic.exceptions.EventAlreadyDeleted;
import logic.model.MEvent;
import logic.utils.LoggedUser;
import logic.utils.SingletonDBSession;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static logic.view.EssentialGUI.logger;

public class UserEventDAO implements IUserEventDAO {

    public boolean joinUserToEvent(MEvent eventModel) throws EventAlreadyDeleted {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("INSERT INTO userevent (id, user_id, event_id) VALUES (NULL, ?, ?)")) {
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventModel.getEventID());
            statement.execute();
            return true;
        } catch (SQLException e) {
            if(e.getErrorCode() == 1452){
                throw new EventAlreadyDeleted("Event already deleted or modified: " + eventModel.getEventID());
            }
            logger.log(Level.SEVERE, "SQLException occurred while adding user participation to event");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public boolean removeUserToEvent(MEvent eventModel) {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("DELETE FROM userevent WHERE (event_id = ? && user_id = ?)")) {
            statement.setInt(1, eventModel.getEventID()); //id_user preso dalla sessione di Login
            statement.setInt(2, LoggedUser.getUserID());
            statement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while removing user participation to event");
            return false;
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public void checkPreviousParticipation(int eventID) throws DuplicateEventParticipation {
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT UserEvent.user_id, UserEvent.event_id FROM userevent WHERE (user_id = ? AND event_id = ?)")) {
            statement.setInt(1, LoggedUser.getUserID()); //id_user preso dalla sessione di Login
            statement.setInt(2, eventID);
            try(ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    throw new DuplicateEventParticipation("Event already joined by user. Choose another event from list if available!");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while checking previous event participation");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
    }

    public int getParticipationsToEvent(int id) {
        int res = 0;
        try (PreparedStatement statement = SingletonDBSession.getInstance().getConnection().prepareStatement("SELECT COUNT(user_id) FROM userevent WHERE (event_id = ?)")) {
            statement.setInt(1, id); //id_evento preso come parametro
            try(ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    res = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting participations to event");
        } finally {
            SingletonDBSession.getInstance().closeConn();
        }
        return res;
    }
}
